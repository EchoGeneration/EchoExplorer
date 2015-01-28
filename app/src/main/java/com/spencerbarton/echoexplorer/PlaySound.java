package com.spencerbarton.echoexplorer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlaySound extends Service {
    private final static String TAG = "PlaySoundService";
    private final IBinder mBinder = new PlayAudioBinder();
    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        stopAudio();
    }

    public void playAudio(int audioFile) {
        try {
            initAudio(audioFile);
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
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

    private void initAudio(int audioFile) {
        mMediaPlayer = MediaPlayer.create(this, audioFile);

        // Register termination
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
            }
        });
    }

}