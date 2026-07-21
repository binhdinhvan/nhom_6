package com.example.myfile.core.event;

/**
 * Phat khi noi dung mot thu muc thay doi (them/xoa/doi ten/paste/giai nen).
 * FileListFragment (A) subscribe de reload danh sach.
 */
public class FileChangedEvent {

    public enum ChangeType { CREATED, DELETED, RENAMED, MOVED, COPIED }

    private final String affectedDirectory;
    private final ChangeType changeType;

    public FileChangedEvent(String affectedDirectory, ChangeType changeType) {
        this.affectedDirectory = affectedDirectory;
        this.changeType = changeType;
    }

    public String getAffectedDirectory() { return affectedDirectory; }
    public ChangeType getChangeType()    { return changeType; }
}
