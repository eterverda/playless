package io.github.eterverda.playless;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AutoUpdateService extends Service {
    private static final String ACTION_CHECK_FOR_UPDATES = "io.github.eterverda.playless.intent.action.CHECK_FOR_UPDATES";

    private AutoUpdateBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new AutoUpdateBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();

        if (action == null || action.equals(Intent.ACTION_MAIN)) {
            final Intent checkIntent = new Intent(ACTION_CHECK_FOR_UPDATES).setClass(this, AutoUpdateService.class);
            final PendingIntent checkPendingIntent = PendingIntent.getService(this, 0, checkIntent, 0x0);
            final AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(checkPendingIntent);
            manager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, checkPendingIntent);

            stopSelf();

        } else if (action.equals(ACTION_CHECK_FOR_UPDATES)) {
            stopSelf();
        }
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
