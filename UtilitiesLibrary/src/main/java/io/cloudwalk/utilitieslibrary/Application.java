package io.cloudwalk.utilitieslibrary;

import android.content.Context;

import androidx.lifecycle.ProcessLifecycleOwner;

import io.cloudwalk.loglibrary.Log;

public class Application extends android.app.Application {
    private static final String
            TAG = Application.class.getSimpleName();

    private static Context
            sPackageContext = null;

    public static Context getPackageContext() {
        Log.d(TAG, "getPackageContext");

        return Application.sPackageContext;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        super.onCreate();

        LifecycleObserver lifecycleObserver = new LifecycleObserver();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);

        Application.sPackageContext = getApplicationContext();
    }
}
