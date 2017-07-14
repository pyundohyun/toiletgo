package com.pdh.lenovo.gubhang;

import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by lenovo on 2016-10-27.
 */

public class ApplicationClass extends android.app.Application {

    String TAG = "Application";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "onCreate()");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("TAG", "onTerminate()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("TAG", "onLowMemory()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("TAG", "onConfigurationChanged()");
    }

}
