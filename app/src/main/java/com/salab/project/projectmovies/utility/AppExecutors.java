package com.salab.project.projectmovies.utility;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Holds executors for background threading works and switching back to main threading
 */
public class AppExecutors {

    private static Executor mIOExecutor;
    private static Executor mainThread;

    public static Executor getIOExecutor(){
        if (mIOExecutor == null){
            mIOExecutor = Executors.newFixedThreadPool(3);
        }
        return mIOExecutor;
    };

    public static Executor getMainThread(){
        if (mainThread == null){
            mainThread = new MainThreadExecutor();
        }
        return mainThread;
    }


    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }


}
