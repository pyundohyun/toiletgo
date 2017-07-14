package com.pdh.lenovo.gubhang;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class StartStickService extends Service {

    private MediaPlayer mPlayer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("StartStickService", "onCreate()");
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.d("StartStickService", "onBind()");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("StartStickService", "onStartCommand()");
        handleStart(intent, startId);
        return START_STICKY;

    }

    private void handleStart(Intent intent, int startId) {
        mPlayer = MediaPlayer.create(StartStickService.this, R.raw.cheerup);
        mPlayer.start();
        Toast.makeText(this, "음악 재생중..(힘을내욧)", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Log.d("StartStickService", "onDestroy()");
        mPlayer.stop();
        super.onDestroy();
    }


}
