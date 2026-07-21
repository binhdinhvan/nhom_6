package com.example.myfile.core.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myfile.core.AppExecutors;
import com.example.myfile.core.model.UiState;

/**
 * ABSTRACT CLASS chuan hoa cach ViewModel bao trang thai cho View.
 *
 * Lop con chi can goi setLoading() / setSuccess() / setError().
 *
 * @param <T> kieu du lieu man hinh can hien thi
 */
public abstract class BaseViewModel<T> extends ViewModel {

    protected final AppExecutors executors;

    private final MutableLiveData<UiState<T>> uiState = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    protected BaseViewModel(AppExecutors executors) {
        this.executors = executors;
    }

    public LiveData<UiState<T>> getUiState()   { return uiState; }
    public LiveData<String> getToastMessage()  { return toastMessage; }

    // ---- goi tu BAT KY thread nao ----

    protected void setLoading() {
        uiState.postValue(UiState.<T>loading());
    }

    protected void setSuccess(T data) {
        uiState.postValue(UiState.success(data));
    }

    protected void setEmpty() {
        uiState.postValue(UiState.<T>empty());
    }

    protected void setError(String message) {
        uiState.postValue(UiState.<T>error(message));
    }

    protected void showToast(String message) {
        toastMessage.postValue(message);
    }

    /** Chay tac vu IO tren background, khong tu post ket qua. */
    protected void runOnDisk(Runnable task) {
        executors.disk().execute(task);
    }
}
