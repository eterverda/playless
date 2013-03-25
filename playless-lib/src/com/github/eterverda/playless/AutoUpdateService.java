package com.github.eterverda.playless;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AutoUpdateService extends Service {
    private AutoUpdateBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new AutoUpdateBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mBinder = null;

        super.onDestroy();
    }

    public class AutoUpdateBinder extends Binder {

    }
}