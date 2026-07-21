package com.example.myfile.core.model;

import androidx.annotation.Nullable;

/**
 * Ket qua tra ve cua moi thao tac file. Thay cho viec nem exception xuyen tang.
 */
public final class OperationResult<T> {

    private final boolean success;
    private final T data;
    private final Throwable error;
    private final boolean cancelled;

    private OperationResult(boolean success, T data, Throwable error, boolean cancelled) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.cancelled = cancelled;
    }

    public static <T> OperationResult<T> success(@Nullable T data) {
        return new OperationResult<>(true, data, null, false);
    }

    public static <T> OperationResult<T> error(Throwable t) {
        return new OperationResult<>(false, null, t, false);
    }

    public static <T> OperationResult<T> cancelled() {
        return new OperationResult<>(false, null, null, true);
    }

    public boolean isSuccess()   { return success; }
    public boolean isCancelled() { return cancelled; }
    @Nullable public T getData()          { return data; }
    @Nullable public Throwable getError() { return error; }

    public String getErrorMessage() {
        if (cancelled) return "Da huy";
        return error != null && error.getMessage() != null
                ? error.getMessage() : "Loi khong xac dinh";
    }
}
