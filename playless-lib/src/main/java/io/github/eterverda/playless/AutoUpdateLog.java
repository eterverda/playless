package io.github.eterverda.playless;

import android.util.Log;

/* package */ final class AutoUpdateLog {
    private static final String TAG = "Playless";

    private AutoUpdateLog() {
        // private constructor to prevent instantiation
    }

    // ASSERT

    public static void a(String msg) {
        Log.wtf(TAG, msg);
    }

    public static void a(String fmt, Object... args) {
        Log.wtf(TAG, String.format(fmt, args));
    }

    // ASSERT WITH THROWABLE

    public static void a(Throwable tr) {
        Log.w(TAG, tr);
    }

    public static void a(Throwable tr, String msg) {
        Log.wtf(TAG, msg, tr);
    }

    public static void a(Throwable tr, String fmt, Object... args) {
        Log.wtf(TAG, String.format(fmt, args), tr);
    }

    // ERROR

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String fmt, Object... args) {
        Log.e(TAG, String.format(fmt, args));
    }

    // ERROR WITH THROWABLE

    public static void e(Throwable tr) {
        Log.e(TAG, tr.getMessage(), tr);
    }

    public static void e(Throwable tr, String msg) {
        Log.e(TAG, msg, tr);
    }

    public static void e(Throwable tr, String fmt, Object... args) {
        Log.e(TAG, String.format(fmt, args), tr);
    }

    // WARNING

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String fmt, Object... args) {
        Log.w(TAG, String.format(fmt, args));
    }

    // WARNING WITH THROWABLE

    public static void w(Throwable tr) {
        Log.w(TAG, tr);
    }

    public static void w(Throwable tr, String msg) {
        Log.w(TAG, msg, tr);
    }

    public static void w(Throwable tr, String fmt, Object... args) {
        Log.w(TAG, String.format(fmt, args), tr);
    }

    // DEBUG

    public static void d(String msg) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(TAG, msg);
    }

    public static void d(String fmt, Object arg1) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(TAG, String.format(fmt, arg1));
    }

    public static void d(String fmt, Object arg1, Object arg2) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(TAG, String.format(fmt, arg1, arg2));
    }

    public static void d(String fmt, Object arg1, Object arg2, Object arg3) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(TAG, String.format(fmt, arg1, arg2, arg3));
    }

    public static void d(String fmt, Object... args) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(TAG, String.format(fmt, args));
    }

    // VERBOSE

    public static void v(String msg) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.v(TAG, msg);
    }

    public static void v(String fmt, Object arg1) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.v(TAG, String.format(fmt, arg1));
    }

    public static void v(String fmt, Object arg1, Object arg2) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.v(TAG, String.format(fmt, arg1, arg2));
    }

    public static void v(String fmt, Object arg1, Object arg2, Object arg3) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.v(TAG, String.format(fmt, arg1, arg2, arg3));
    }

    public static void v(String fmt, Object... args) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.v(TAG, String.format(fmt, args));
    }
}
