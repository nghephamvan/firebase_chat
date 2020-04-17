package com.example.tranquoctrungcntt.uchat.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tranquoctrungcntt.uchat.OtherClasses.AudioPlayer;
import com.example.tranquoctrungcntt.uchat.R;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.calling.CallState;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSecondsToHours;

public class BaseVoiceCallActivity extends BaseCallActivity {

    protected final Handler mTimerHandler = new Handler();

    protected CircleImageView civ_avatar;

    protected TextView tv_status;
    protected TextView tv_name;

    protected ImageView btn_hangup;
    protected ImageView btn_mute;
    protected ImageView btn_speaker;

    protected boolean isMuted;
    protected boolean isSpeaker;

    protected String mVoiceCallId;

    protected AudioController mBaseAudioController;

    protected AudioPlayer mBaseAudioPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isMuted = false;

        isSpeaker = false;

        mVoiceCallId = null;

        mBaseAudioPlayer = new AudioPlayer(BaseVoiceCallActivity.this);

    }

    protected void initControlCallClickEvents() {

        btn_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMuted) mBaseAudioController.unmute();
                else mBaseAudioController.mute();

                isMuted = !isMuted;

                btn_mute.setImageResource(isMuted ? R.drawable.ic_mute_active : R.drawable.ic_mute_nonactive);

            }
        });

        btn_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSpeaker) mBaseAudioController.disableSpeaker();
                else mBaseAudioController.enableSpeaker();

                isSpeaker = !isSpeaker;

                btn_speaker.setImageResource(isSpeaker ? R.drawable.ic_speaker_active : R.drawable.ic_speaker_nonactive);

            }
        });

        btn_mute.setImageResource(R.drawable.ic_mute_nonactive);
        btn_speaker.setImageResource(R.drawable.ic_speaker_nonactive);
    }

    protected void onVoiceCallProgressing() {
//for sub class
    }

    protected void onVoiceCallEstablished() {
//for sub class
    }

    protected void onVoiceCallEnded(Call call) {
//for sub class
        sendVoiceCallMessageIfNeeded(call);

        releaseCallSection();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAndRemoveTask();
            }
        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseCallSection();

        if (mVoiceCallId != null) {
            final Call call = getSinchServiceInterface().getCurrentCall(mVoiceCallId);
            if (call != null && call.getState() != CallState.ENDED) call.hangup();
        }

    }

    protected void sendVoiceCallMessageIfNeeded(Call call) {
        //for subclass
    }

    protected void releaseCallSection() {

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        tv_status.setText("Kết thúc cuộc gọi");

        mBaseAudioPlayer.stopProgressTone();
        mBaseAudioPlayer.stopRingtone();

        if (mBaseAudioController != null) {
            mBaseAudioController.disableSpeaker();
            mBaseAudioController.unmute();
            mBaseAudioController = null;
        }

        mTimerHandler.removeCallbacksAndMessages(null);

        disableAllButton();
    }

    protected void disableAllButton() {
        //for sub class
        btn_mute.setClickable(false);
        btn_speaker.setClickable(false);
        btn_hangup.setClickable(false);

        btn_hangup.setImageResource(R.drawable.ic_hangup_nonactive);
        btn_mute.setImageResource(R.drawable.ic_mute_unclickable);
        btn_speaker.setImageResource(R.drawable.ic_speaker_unclickable);

        disableAnswerButtonIfNeeded();
    }

    protected void disableAnswerButtonIfNeeded() {
        //for incoming call
    }

    protected void startTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Call call = getSinchServiceInterface().getCurrentCall(mVoiceCallId);
                if (call != null) tv_status.setText(formatSecondsToHours(call.getDetails().getDuration()));
                mTimerHandler.postDelayed(this, 250);
            }
        });
    }

    protected void showVoiceCallRequestPermissionDialog(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Bạn cần cấp quyền để có thể gọi điện. Vui lòng cấp quyền trong " +
                        "lần yêu cầu tiếp theo hoặc vào cài đặt và cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAndRemoveTask();
                    }
                }).create().show();

    }


    protected class MyVoiceCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {

            onVoiceCallProgressing();

        }

        @Override
        public void onCallEstablished(Call call) {


            onVoiceCallEstablished();

        }

        @Override
        public void onCallEnded(Call call) {

            onVoiceCallEnded(call);

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }


}
