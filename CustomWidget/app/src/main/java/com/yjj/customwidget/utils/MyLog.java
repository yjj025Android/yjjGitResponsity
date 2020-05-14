package com.yjj.customwidget.utils;

import android.util.Log;

/**
 * 自定义日志工具类，统一对日志输出做出控制。
 */
public final class MyLog {

    private static final String TAG = "MyLog";
    private static final String TAG_TRACE = "MyLogTrace";

    private static final class StackTraceDebug extends RuntimeException {
        final static private long serialVersionUID = 27058374L;
    }

    private static boolean DEBUG = true;

    private static boolean LOGTRACE = true;

    private MyLog() {
    }

    public static void trace(Object object) {
        if (LOGTRACE) {
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            StackTraceElement trace = traces[3];
            Log.d(TAG_TRACE, addThreadInfo(object.getClass().getSimpleName() + " : " + trace.getMethodName()));
        }
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setDebug(final boolean isDebug) {
        DEBUG = isDebug;
    }

    public static boolean isLogTrace() {
        return LOGTRACE;
    }

    public static void setLogTrace(final boolean isLogTrace) {
        LOGTRACE = isLogTrace;
    }

    private static String addThreadInfo(final String msg) {
        final String threadName = Thread.currentThread().getName();
        final String shortName = threadName.startsWith("OkHttp") ? "OkHttp" : threadName;
        return "[" + shortName + "] " + msg;
    }

    public static void v(final String msg) {
        if (DEBUG) {
            Log.v(TAG, addThreadInfo(msg));
        }
    }

    public static void v(final String msg, final Throwable t) {
        if (DEBUG) {
            Log.v(TAG, addThreadInfo(msg), t);
        }
    }

    public static void d(final String msg) {
        if (DEBUG) {
            Log.d(TAG, addThreadInfo(msg));
        }
    }

    public static void d(final String msg, final Throwable t) {
        if (DEBUG) {
            Log.d(TAG, addThreadInfo(msg), t);
        }
    }

    public static void i(final String msg) {
        if (DEBUG) {
            Log.i(TAG, addThreadInfo(msg));
        }
    }

    public static void i(final String msg, final Throwable t) {
        if (DEBUG) {
            Log.i(TAG, addThreadInfo(msg), t);
        }
    }

    public static void w(final String msg) {
        Log.w(TAG, addThreadInfo(msg));
    }

    public static void w(final String msg, final Throwable t) {
        Log.w(TAG, addThreadInfo(msg), t);
    }

    public static void e(final String msg) {
        Log.e(TAG, addThreadInfo(msg));
    }

    public static void e(final String msg, final Throwable t) {
        Log.e(TAG, addThreadInfo(msg), t);
    }

    public static void logStackTrace(final String msg) {
        try {
            throw new StackTraceDebug();
        } catch (final StackTraceDebug dbg) {
            d(msg, dbg);
        }
    }
}
