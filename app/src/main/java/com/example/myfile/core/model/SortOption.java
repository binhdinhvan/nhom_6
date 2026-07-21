package com.example.myfile.core.model;

/** Tieu chi sap xep. SortStrategy (A) doc enum nay de chon Comparator. */
public enum SortOption {
    NAME_ASC,
    NAME_DESC,
    SIZE_ASC,
    SIZE_DESC,
    DATE_ASC,
    DATE_DESC,
    TYPE_ASC;

    public boolean isDescending() {
        return this == NAME_DESC || this == SIZE_DESC || this == DATE_DESC;
    }
}
