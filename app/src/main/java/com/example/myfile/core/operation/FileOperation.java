package com.example.myfile.core.operation;

import com.example.myfile.core.model.OperationResult;

/**
 * HOP DONG cua moi thao tac file. Nho interface nay, Service va ViewModel
 * xu ly Copy / Delete / Zip / Unzip theo cung mot cach -> de mo rong.
 *
 * @param <T> kieu du lieu tra ve khi thanh cong
 */
public interface FileOperation<T> {

    /** Chay dong bo. Nguoi goi chiu trach nhiem dua vao background thread. */
    OperationResult<T> execute();

    /** Yeu cau dung. Cai dat phai kiem tra co cancel o vong lap. */
    void cancel();

    boolean isCancelled();

    /** Ten hien thi tren notification / progress dialog. VD: "Dang nen 12 file". */
    String getDisplayName();

    /** Id duy nhat de dinh danh tren notification va EventBus. */
    String getOperationId();

    void setProgressListener(ProgressListener listener);
}
