package io.cloudwalk.utilitieslibrary;

import android.content.Context;

import androidx.lifecycle.ProcessLifecycleOwner;

import io.cloudwalk.loglibrary.Log;

public class Application extends io.cloudwalk.loglibrary.Application {
    private static final String
            TAG = Application.class.getSimpleName();

    public static Context getPackageContext() {
        // Log.d(TAG, "getPackageContext");

        return io.cloudwalk.loglibrary.Application.getPackageContext();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        super.onCreate();

        LifecycleObserver lifecycleObserver = new LifecycleObserver();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
    }
}
