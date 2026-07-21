package com.example.myfile.core;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Thay the cho Coroutine/RxJava. Moi tac vu IO chay o disk executor,
 * ket qua post ve main thread.
 *
 * Dung trong ViewModel:
 *   executors.disk().execute(() -> {
 *       OperationResult<...> r = repository.listFiles(path, false);
 *       executors.main().post(() -> state.setValue(...));
 *   });
 */
@Singleton
public class AppExecutors {

    private final ExecutorService diskIO;
    private final ExecutorService background;
    private final Handler mainHandler;

    @Inject
    public AppExecutors() {
        this.diskIO = Executors.newSingleThreadExecutor();   // doc thu muc: tuan tu, tranh giat UI
        this.background = Executors.newFixedThreadPool(3);   // copy/zip: chay song song
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public ExecutorService disk()       { return diskIO; }
    public ExecutorService background() { return background; }
    public Handler main()               { return mainHandler; }

    public void postToMain(Runnable r)  { mainHandler.post(r); }
}
