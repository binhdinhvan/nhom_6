package com.example.myfile.core.operation;

import androidx.annotation.Nullable;

import com.example.myfile.core.model.OperationResult;

import java.util.UUID;

/**
 * TEMPLATE METHOD PATTERN.
 *
 * execute() la final: co dinh khung xu ly (start -> doExecute -> finish,
 * bat exception, kiem tra cancel). Lop con chi viet doExecute().
 *
 * Ke thua boi: CopyOperation, MoveOperation, DeleteOperation, RenameOperation (B)
 *              ZipOperation, UnzipOperation (C)
 */
public abstract class AbstractFileOperation<T> implements FileOperation<T> {

    private final String operationId = UUID.randomUUID().toString();

    private volatile boolean cancelled = false;
    private ProgressListener listener = ProgressListener.NO_OP;

    @Override
    public final OperationResult<T> execute() {
        try {
            onStart();
            if (cancelled) return OperationResult.cancelled();

            T data = doExecute();

            if (cancelled) return OperationResult.cancelled();
            return OperationResult.success(data);

        } catch (OperationException e) {
            return OperationResult.error(e);
        } catch (Exception e) {
            return OperationResult.error(new OperationException(
                    "Loi khi " + getDisplayName() + ": " + e.getMessage(), e));
        } finally {
            onFinish();
        }
    }

    /** Phan viec that su. Lop con cai dat. */
    protected abstract T doExecute() throws Exception;

    /** Hook tuy chon: kiem tra dieu kien dau vao, ghi log... */
    protected void onStart() { }

    /** Hook tuy chon: dong stream, don file tam... Luon duoc goi. */
    protected void onFinish() { }

    // ---- tien ich cho lop con ----

    protected final void publishProgress(long current, long total, String currentItem) {
        listener.onProgress(current, total, currentItem);
    }

    /** Goi trong vong lap. Nem exception de thoat nhanh khoi de quy sau. */
    protected final void throwIfCancelled() throws OperationException {
        if (cancelled) throw new OperationException("Da huy thao tac");
    }

    // ---- FileOperation ----

    @Override public void cancel()            { this.cancelled = true; }
    @Override public boolean isCancelled()    { return cancelled; }
    @Override public String getOperationId()  { return operationId; }

    @Override
    public void setProgressListener(@Nullable ProgressListener listener) {
        this.listener = (listener == null) ? ProgressListener.NO_OP : listener;
    }
}
