package com.example.tranquoctrungcntt.uchat.OtherClasses;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.tranquoctrungcntt.uchat.R;

import java.io.IOException;

public class AudioPlayer {


    private final Context mAudioPlayerContext;

    private MediaPlayer mPlayer;

    public AudioPlayer(Context context) {
        this.mAudioPlayerContext = context;
    }

    public void playRingtone() {

        Vibrator mVibrator = (Vibrator) mAudioPlayerContext.getSystemService(Context.VIBRATOR_SERVICE);
        AudioManager mAudioManager = (AudioManager) mAudioPlayerContext.getSystemService(Context.AUDIO_SERVICE);

        long[] pattern = {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        int[] mAmplitudes = {0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0};

        stopRingtone();

        // Honour silent mode
        switch (mAudioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                try {
                    mPlayer.setDataSource(mAudioPlayerContext, Uri.parse("android.resource://" + mAudioPlayerContext.getPackageName() + "/" + R.raw.incomming_sound));
                    mPlayer.prepare();
                } catch (IOException e) {
                    mPlayer = null;
                    return;
                }
                mPlayer.setLooping(true);
                mPlayer.start();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    mVibrator.vibrate(VibrationEffect.createWaveform(pattern, mAmplitudes, 0));
                else mVibrator.vibrate(pattern, 0);

                break;
            case AudioManager.RINGER_MODE_VIBRATE:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    mVibrator.vibrate(VibrationEffect.createWaveform(pattern, mAmplitudes, 0));
                else mVibrator.vibrate(pattern, 0);

                break;
        }
    }

    public void stopRingtone() {

        Vibrator mVibrator = (Vibrator) mAudioPlayerContext.getSystemService(Context.VIBRATOR_SERVICE);

        if (mVibrator != null) mVibrator.cancel();

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void playProgressTone() {

        stopProgressTone();

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        try {
            mPlayer.setDataSource(mAudioPlayerContext, Uri.parse("android.resource://" + mAudioPlayerContext.getPackageName() + "/" + R.raw.progress_sound));
            mPlayer.prepare();
        } catch (IOException e) {
            mPlayer = null;
            return;
        }
        mPlayer.setLooping(true);
        mPlayer.start();

    }

    public void stopProgressTone() {

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

}
