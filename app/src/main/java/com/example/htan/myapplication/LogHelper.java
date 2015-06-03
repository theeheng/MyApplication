package com.example.htan.myapplication;

import android.util.Log;

/**
 * Created by Jim on 9/20/13.
 */
public class LogHelper {
    public static void logThreadId(String message) {
        long processId = android.os.Process.myPid();
        long threadId = Thread.currentThread().getId();
        Log.d("BackgroundWork", String.format("[ Process: %d | Thread: %d] %s", processId, threadId, message));
    }
}