package com.flixster.android.captioning;

import android.util.Log;

/**
 * A wrapper around android.util.Log with support for sensitive logging and logging toggle
 */
public class CaptionLogger {

    public static final String TAG = "CC";

    private static boolean debugMode = false;

    /** Set the logger to display or hide certain logs */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    /** @return If the app is in debug mode which supports logging of all default levels */
    public static boolean inDebugMode() {
        return debugMode;
    }

    /** A sensitive verbose msg is only logged in debug mode */
    public static void sv(String msg) {
        if (debugMode) {
            Log.v(TAG, msg);
        }
    }

    /** A sensitive debug msg is only logged in debug mode */
    public static void sd(String msg) {
        if (debugMode) {
            Log.d(TAG, msg);
        }
    }

    /** A sensitive info msg is only logged in debug mode */
    public static void si(String msg) {
        if (debugMode) {
            Log.i(TAG, msg);
        }
    }

    /** A sensitive warning msg is only logged in debug mode */
    public static void sw(String msg) {
        if (debugMode) {
            Log.w(TAG, msg);
        }
    }

    /** Forced logging */
    public static void fd(String msg) {
        Log.d(TAG, msg);
    }

    /** A verbose msg is only logged in debug mode */
    public static void v(String msg) {
        if (debugMode) {
            Log.v(TAG, msg);
        }
    }

    /** A verbose msg is only logged in debug mode */
    public static void v(String msg, Throwable tr) {
        if (debugMode) {
            Log.v(TAG, msg, tr);
        }
    }

    /** A debug msg is only logged in debug mode */
    public static void d(String msg) {
        if (debugMode) {
            Log.d(TAG, msg);
        }
    }

    /** A debug msg is only logged in debug mode */
    public static void d(String msg, Throwable tr) {
        if (debugMode) {
            Log.d(TAG, msg, tr);
        }
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void i(String msg, Throwable tr) {
        Log.i(TAG, msg, tr);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String msg, Throwable tr) {
        Log.w(TAG, msg, tr);
    }

    public static void w(Throwable tr) {
        Log.w(TAG, tr);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable tr) {
        Log.e(TAG, msg, tr);
    }
}
