package io.cloudwalk.utilitieslibrary;

import android.content.Context;

import io.cloudwalk.loglibrary.Log;

public class Application extends io.cloudwalk.loglibrary.Application {
    private static final String
            TAG = Application.class.getSimpleName();

    public static Context getPackageContext() {
        // Log.d(TAG, "getPackageContext");

        return io.cloudwalk.loglibrary.Application.getPackageContext();
    }
}
