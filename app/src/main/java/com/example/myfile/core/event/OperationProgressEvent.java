package com.example.myfile.core.event;

/**
 * Phat lien tuc trong khi copy / move / zip / unzip.
 * ProgressDialogFragment (B) subscribe de cap nhat thanh tien do.
 */
public class OperationProgressEvent {

    private final String operationId;
    private final String displayName;
    private final long current;
    private final long total;
    private final String currentItem;

    public OperationProgressEvent(String operationId, String displayName,
                                  long current, long total, String currentItem) {
        this.operationId = operationId;
        this.displayName = displayName;
        this.current = current;
        this.total = total;
        this.currentItem = currentItem;
    }

    public String getOperationId() { return operationId; }
    public String getDisplayName() { return displayName; }
    public long getCurrent()       { return current; }
    public long getTotal()         { return total; }
    public String getCurrentItem() { return currentItem; }

    /** 0..100, tra ve -1 neu chua xac dinh duoc tong. */
    public int getPercent() {
        if (total <= 0) return -1;
        return (int) (current * 100 / total);
    }
}
