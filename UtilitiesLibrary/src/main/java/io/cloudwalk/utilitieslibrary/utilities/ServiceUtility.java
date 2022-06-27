package io.cloudwalk.utilitieslibrary.utilities;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import io.cloudwalk.loglibrary.Log;
import io.cloudwalk.utilitieslibrary.Application;
import io.cloudwalk.utilitieslibrary.models.ServiceModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ServiceUtility {
    private static final String
            TAG = ServiceUtility.class.getSimpleName();

    private static final List<ServiceModel>
            mServiceList = new ArrayList<>(0);

    private static final Semaphore
            sSemaphore = new Semaphore(1, true);

    /**
     * Connection callback.
     */
    public static interface Callback {
        /**
         * Self-describing.
         */
        public void onSuccess();

        /**
         * Indicates a service was disconnected, isn't found or its bind failed due to missing
         * permissions.<br>
         * A reconnection strategy is recommended in here.
         */
        public void onFailure();
    }

    /**
     * Constructor.
     */
    private ServiceUtility() {
        Log.d(TAG, "ServiceUtility");

        /* Nothing to do */
    }

    /**
     * See {@link ServiceUtility#retrieve(String, String)}.
     */
    private static IBinder _getService(@NotNull String pkg, @NotNull String cls) {
        // Log.d(TAG, "_getService");

        IBinder service   = null;

        long    timeout   = 1000;
        long    timestamp = SystemClock.elapsedRealtime();

        do {
            sSemaphore.acquireUninterruptibly();

            int index = _searchService(cls);

            if (index >= 0) {
                service = mServiceList.get(index).getService();
            }

            sSemaphore.release();

            if (index < 0 || service != null) { // TODO: remove loop?!
                break;
            } else {
                SystemClock.sleep(timeout / 3);
            }
        } while ((timestamp + timeout) >= SystemClock.elapsedRealtime());

        return service;
    }

    /**
     * Searches for a previously bounded service.
     *
     * @param cls service full class name
     * @return index of the service in the list
     */
    private static int _searchService(String cls) {
        // Log.d(TAG, "_searchService");

        int index = -1;

        for (int i = 0; i < mServiceList.size(); i++) {
            if (mServiceList.get(i).getComponentName().getClassName().equals(cls)) {
                index = i;
            }

            // Log.d(TAG, "_searchService::mServiceList.get(i).getComponentName().getClassName() [" + mServiceList.get(i).getComponentName().getClassName() + "]");

            if (index != -1) {
                break;
            }
        }

        // Log.d(TAG, "_searchService::index [" + index + "]");

        return index;
    }

    /**
     * Unbinds a service according to given {@code pkg} and {@code cls}, triggering previously
     * registered connection {@code callback} in the process.
     *
     * @param pkg service package name
     * @param cls service class name
     * @param callback {@link ServiceUtility.Callback}
     */
    private static void _onFailure(String pkg, String cls, Callback callback) {
        // Log.d(TAG, "_onFailure");

        _unregister(pkg, cls);

        new Thread() {
            @Override
            public void run() { super.run(); callback.onFailure(); }
        }.start();
    }

    /**
     * See {@link ServiceUtility#register(String, String, Bundle, Callback)}.
     */
    private static void _register(String pkg, String cls, Bundle extras, Callback callback) {
        // Log.d(TAG, "_register");

        _unregister(pkg, cls);

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                // Log.d(TAG, "onServiceConnected::name [" + name.getClassName() + "]");

                _setService(name, binder, this);

                new Thread() {
                    @Override
                    public void run() { super.run(); callback.onSuccess(); }
                }.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisconnected::name [" + name.getClassName() + "]");

                _onFailure(pkg, cls, callback);
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Log.e(TAG, "onBindingDied::name [" + name.getClassName() + "]");

                _onFailure(pkg, cls, callback);
            }
        };

        int count = 0;

        mServiceList.add(new ServiceModel(new ComponentName(pkg, cls), null));

        do {
            Intent intent = new Intent();

            if (intent.getExtras() == null && extras != null) {
                intent.putExtras(extras);
            }

            if (count == 0) {
                intent.setClassName(pkg, cls);
            }

            if (count == 1 || count == 3) {
                break;
            }

            if (count == 2) {
                intent.setAction(cls);

                intent.setPackage(pkg);
            }

            if (count >= 4) {
                mServiceList.remove(_searchService(cls));

                Log.e(TAG, "Failed to bind to " + intent.getAction() + " (either not found or missing permission).");

                _onFailure(pkg, cls, callback);

                break;
            }

            count += (Application.getContext().bindService(intent, serviceConnection, BIND_AUTO_CREATE)) ? 1 : 2;
        } while (true);
    }

    /**
     * @param service {@link IBinder}
     */
    private static void _setService(ComponentName name, IBinder service, ServiceConnection serviceConnection) {
        // Log.d(TAG, "_setService");

        try {
            sSemaphore.acquireUninterruptibly();

            int index = _searchService(name.getClassName());

            if (index >= 0) {
                mServiceList.get(index).setComponentName(name);

                mServiceList.get(index).setService(service);

                mServiceList.get(index).setServiceConnection(serviceConnection);
            }
        } finally {
            sSemaphore.release();
        }
    }

    /**
     * See {@link ServiceUtility#unregister(String, String)}.
     */
    private static void _unregister(String pkg, String cls) {
        // Log.d(TAG, "_unregister");

        int index = _searchService(cls);

        if (index >= 0) {
            ServiceConnection serviceConnection = mServiceList.get(index).getServiceConnection();

            if (serviceConnection != null) {
                Application.getContext().unbindService(serviceConnection);
            }

            // Log.d(TAG, "_unregister::remove [" + index + "]");

            mServiceList.remove(index);
        }
    }

    /**
     * Retrieves an instance of {@link IBinder} according to given {@code pkg} and {@code cls}.
     *
     * @param pkg service package name
     * @param cls service class name
     * @return {@link IBinder}
     */
    public static IBinder retrieve(@NotNull String pkg, @NotNull String cls) {
        // Log.d(TAG, "retrieve");

        return _getService(pkg, cls);
    }

    /**
     * Starts a new thread and calls {@link Runnable#run()} from given {@link Runnable}.<br>
     * Intended as a helper for UI thread calls.<br>
     * <code>
     *     <pre>
     * ServiceUtility.execute(new Runnable() {
     *    {@literal @}Override
     *     public void execute() {
     *         // code you shouldn't run on the main thread goes here
     *     }
     * });
     *     </pre>
     * </code>
     *
     * @param runnable {@link Runnable}
     */
    public static void execute(@NotNull Runnable runnable) {
        // Log.d(TAG, "execute");

        new Thread() {
            @Override
            public void run() { super.run(); runnable.run(); }
        }.start();
    }

    /**
     * See {@link ServiceUtility#register(String, String, Bundle, Callback)}.
     */
    public static void register(@NotNull String pkg, @NotNull String cls, @NotNull Callback callback) {
        // Log.d(TAG, "register");

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sSemaphore.acquireUninterruptibly();

                    _register(pkg, cls, null, callback);
                } finally {
                    sSemaphore.release();
                }
            }
        }.start();
    }

    /**
     * Binds a service according to given {@code pkg} and {@code cls}.<br>
     * Ensures the binding will be undone in the event of a service disconnection.
     *
     * @param pkg service package name
     * @param cls service class name
     * @param extras optional bind data
     * @param callback {@link ServiceUtility.Callback}
     */
    public static void register(@NotNull String pkg, @NotNull String cls, Bundle extras, @NotNull Callback callback) {
        // Log.d(TAG, "register");

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sSemaphore.acquireUninterruptibly();

                    _register(pkg, cls, extras, callback);
                } finally {
                    sSemaphore.release();
                }
            }
        }.start();
    }

    /**
     * Unbinds a service according to given {@code pkg} and {@code cls}.
     *
     * @param pkg service package name
     * @param cls service class name
     */
    public static void unregister(@NotNull String pkg, @NotNull String cls) {
        // Log.d(TAG, "unregister");

        try {
            sSemaphore.acquireUninterruptibly();

            _unregister(pkg, cls);
        } finally {
            sSemaphore.release();
        }
    }
}
