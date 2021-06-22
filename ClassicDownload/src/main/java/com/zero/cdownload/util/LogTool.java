package com.zero.cdownload.util;

import android.util.Log;

/**
 * @Name: ChsLogDemo
 * @Description: 描述信息
 * @Author: Created by heyong on 4/16/21
 */
public class LogTool {
    private static final String TAG = "Download_LogTool";
    private static boolean logEnabled = false;

    public static void setLogEnabled(boolean logEnabled) {
        LogTool.logEnabled = logEnabled;
    }

    public static void e(String tag, String msg) {
        if (logEnabled) {
            Log.e(tag, msg);
        }

    }

    public static void e(String msg) {
        if (logEnabled) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, int msg) {
        if (logEnabled) {
            Log.e(tag, msg + "");
        }
    }

    public static void e(int msg) {
        if (logEnabled) {
            Log.e(TAG, msg + "");
        }
    }

    public static void d(String tag, String msg) {
        if (logEnabled) {
            Log.d(tag, msg + "");
        }
    }

    public static void d(String msg) {
        if (logEnabled) {
            Log.d(TAG, msg);
        }
    }

    public static void e(Object object, String msg) {
        if (logEnabled) {
            Log.e(object.getClass().getSimpleName(), msg);
        }
    }
}
