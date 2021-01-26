package com.hitt.mvptoframenwork;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class MVPApplication extends Application {
    private static Context application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static Context getContext() {
        if (!(application instanceof Activity)) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return application;
    }
}
