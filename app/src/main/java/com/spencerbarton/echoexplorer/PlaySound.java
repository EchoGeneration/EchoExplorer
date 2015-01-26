package com.spencerbarton.echoexplorer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class PlaySound extends Service {
    private final static String TAG = "PlaySoundService";
    private final IBinder mBinder = new PlayAudioBinder();
    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"Binding service");
        initAudio(intent);
        return mBinder;
    }

    @Override
    public void onDestroy() {
        stopAudio();
    }

    public void playAudio() {
        try {
            Log.d(TAG, "Starting Audio");
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Audio bug:" + e.getMessage());
        }
    }

    public void stopAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public class PlayAudioBinder extends Binder {

        PlaySound getService() { return PlaySound.this; }

    }

    private void initAudio(Intent intent) {
        int audioFile = intent.getIntExtra(MainActivity.EXTRA_AUDIO, 0);
        mMediaPlayer = MediaPlayer.create(this, audioFile);
        Log.d(TAG, "Created Media player, got audio file");
        Log.d(TAG, "audioFile:"+String.valueOf(audioFile));

        // Register termination
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
            }
        });
    }

}