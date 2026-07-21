package com.example.myfile.core.operation;

/**
 * Callback tien do cho moi thao tac dai (copy, move, zip, unzip).
 * Tang domain KHONG biet EventBus; Service se noi listener nay -> EventBus.
 */
public interface ProgressListener {

    /**
     * @param current   don vi da xu ly (byte hoac so entry)
     * @param total     tong don vi; -1 neu chua biet
     * @param currentItem ten file dang xu ly, hien tren dialog
     */
    void onProgress(long current, long total, String currentItem);

    ProgressListener NO_OP = new ProgressListener() {
        @Override public void onProgress(long current, long total, String currentItem) { }
    };
}
