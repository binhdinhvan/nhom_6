package com.example.myfile.core.model;

import androidx.annotation.Nullable;

/**
 * State thong nhat giua ViewModel va View.
 * Dung: LiveData<UiState<List<FileItem>>>
 */
public final class UiState<T> {

    public enum Status { LOADING, SUCCESS, EMPTY, ERROR }

    private final Status status;
    private final T data;
    private final String message;

    private UiState(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> UiState<T> loading()             { return new UiState<>(Status.LOADING, null, null); }
    public static <T> UiState<T> success(T data)       { return new UiState<>(Status.SUCCESS, data, null); }
    public static <T> UiState<T> empty()               { return new UiState<>(Status.EMPTY, null, null); }
    public static <T> UiState<T> error(String message) { return new UiState<>(Status.ERROR, null, message); }

    public Status getStatus() { return status; }
    @Nullable public T getData()       { return data; }
    @Nullable public String getMessage() { return message; }

    public boolean isLoading() { return status == Status.LOADING; }
    public boolean isSuccess() { return status == Status.SUCCESS; }
}
