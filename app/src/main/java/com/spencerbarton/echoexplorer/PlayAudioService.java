package com.spencerbarton.echoexplorer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

public class PlayAudioService extends Service {
    private final static String TAG = "PlaySoundService";
    private final static int ECHO_DELAY = 500; // ms
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
            mMediaPlayer = MediaPlayer.create(this, audioFile);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopAudio();
                }
            });
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private int mNumAudioPlayed = 0;
    public void playAudioFiles(final int[] audioFiles) {

        // Done playing audio files
        if (mNumAudioPlayed >= audioFiles.length) {
            mNumAudioPlayed = 0;
            return;
        }

        // Set-up new media player and on completion play next file
        try {
            mMediaPlayer = MediaPlayer.create(this, audioFiles[mNumAudioPlayed]);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopAudio();
                    mNumAudioPlayed++;

                    // Delay between echos
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playAudioFiles(audioFiles);
                        }
                    }, ECHO_DELAY);

                }
            });
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

        PlayAudioService getService() { return PlayAudioService.this; }

    }

}