package com.example.myfile.core.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfile.core.model.FileItem;
import com.example.myfile.core.model.OperationResult;

import java.util.List;

/**
 * HOP DONG CHINH cua tang du lieu. Moi tang tren (ViewModel, Operation)
 * chi phu thuoc vao interface nay, khong phu thuoc java.io.File.
 *
 * Tat ca method deu CHAY DONG BO -> nguoi goi phai dua vao AppExecutors.
 */
public interface FileRepository {

    /** Liet ke con truc tiep cua mot thu muc. */
    OperationResult<List<FileItem>> listFiles(@NonNull String directoryPath, boolean showHidden);

    /** Lay thong tin 1 file/folder theo duong dan. */
    OperationResult<FileItem> getFile(@NonNull String path);

    /** Thu muc goc bo nho trong (thuong la /storage/emulated/0). */
    String getRootPath();

    boolean exists(@NonNull String path);

    /** Kich thuoc thuc te; voi folder thi tinh de quy. Dung cho PropertiesDialog. */
    long calculateSize(@NonNull FileItem item);

    /** Tao ten khong trung trong cung thu muc: "abc.txt" -> "abc (1).txt". */
    String generateUniqueName(@NonNull String parentPath, @NonNull String desiredName);

    // ---- CRUD nguyen thuy: Operation goi vao, KHONG goi tu UI ----

    OperationResult<FileItem> createFolder(@NonNull String parentPath, @NonNull String name);

    OperationResult<FileItem> rename(@NonNull FileItem item, @NonNull String newName);

    OperationResult<Boolean> delete(@NonNull FileItem item);

    /**
     * Copy 1 item (de quy neu la folder).
     * @param listener nhan tien do theo byte; co the null.
     */
    OperationResult<FileItem> copy(@NonNull FileItem source,
                                   @NonNull String destDirPath,
                                   @Nullable ProgressCallback listener);

    OperationResult<FileItem> move(@NonNull FileItem source, @NonNull String destDirPath);

    /** Callback tien do cap thap; AbstractFileOperation se bao ra ngoai. */
    interface ProgressCallback {
        void onBytesCopied(long copied, long total);
        boolean isCancelled();
    }
}
