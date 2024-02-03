package com.nathaniel.motus.umlclasseditor;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.nathaniel.motus.umlclasseditor.view.CrashActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

public class App extends Application implements LifecycleObserver {
    private static final String TAG = "App";
    public void onCreate() {
        super.onCreate();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, e) -> {


                    Intent intent = new Intent(getApplicationContext(), CrashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String stackTrace = sw.toString(); // stack trace as a string
                    intent.putExtra("ex", stackTrace);
                    startActivity(intent);
                    Log.e(TAG, "uncaughtException: ", e);
                    System.exit(1);
                });

    }
}