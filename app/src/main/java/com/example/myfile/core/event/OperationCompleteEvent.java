package com.example.myfile.core.event;

/** Phat khi thao tac ket thuc (thanh cong, loi hoac bi huy). */
public class OperationCompleteEvent {

    private final String operationId;
    private final String displayName;
    private final boolean success;
    private final boolean cancelled;
    private final String message;

    public OperationCompleteEvent(String operationId, String displayName,
                                  boolean success, boolean cancelled, String message) {
        this.operationId = operationId;
        this.displayName = displayName;
        this.success = success;
        this.cancelled = cancelled;
        this.message = message;
    }

    public String getOperationId() { return operationId; }
    public String getDisplayName() { return displayName; }
    public boolean isSuccess()     { return success; }
    public boolean isCancelled()   { return cancelled; }
    public String getMessage()     { return message; }
}
