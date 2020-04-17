package com.example.tranquoctrungcntt.uchat.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tranquoctrungcntt.uchat.OtherClasses.AudioPlayer;
import com.example.tranquoctrungcntt.uchat.R;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseVideoCallActivity extends BaseCallActivity {

    protected LinearLayout linear_call_infor;
    protected LinearLayout linear_pause;
    protected LinearLayout linear_btns;

    protected FrameLayout frame_userPreview;
    protected FrameLayout frame_myPreview;

    protected TextView tv_status;
    protected TextView tv_name;

    protected CircleImageView civ_avatar;
    protected CircleImageView civ_pause_avatar;

    protected ImageView btn_pause;
    protected ImageView btn_mute;
    protected ImageView btn_switch;
    protected ImageView btn_hangup;

    protected boolean isPause;
    protected boolean isMuted;
    protected boolean isFacingFront;
    protected boolean shouldPauseStreamWhenStop;

    protected VideoController mBaseVideoController;
    protected AudioController mBaseAudioController;

    protected AudioPlayer mBaseAudioPlayer;

    protected String mVideoCallId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isPause = false;

        isMuted = false;

        isFacingFront = true;

        shouldPauseStreamWhenStop = false;

        mVideoCallId = null;

        mBaseAudioPlayer = new AudioPlayer(BaseVideoCallActivity.this);

    }

    protected void addMyPreview() {
        if (frame_myPreview.getChildCount() != 0) frame_myPreview.removeAllViews();
        frame_myPreview.addView(mBaseVideoController.getLocalView());
        frame_myPreview.setVisibility(View.VISIBLE);
    }


    protected void addUserPreview() {
        if (frame_userPreview.getChildCount() != 0) frame_userPreview.removeAllViews();
        frame_userPreview.addView(mBaseVideoController.getRemoteView());
        frame_userPreview.setVisibility(View.VISIBLE);
    }


    protected void onUserPreviewPaused() {
//for sub class
        if (frame_userPreview.getChildCount() != 0) frame_userPreview.removeAllViews();
        frame_userPreview.setVisibility(View.GONE);

        linear_pause.setVisibility(View.VISIBLE);
    }

    protected void onUserPreviewResumed() {
//for sub class

        if (frame_userPreview.getChildCount() != 0) frame_userPreview.removeAllViews();
        frame_userPreview.addView(mBaseVideoController.getRemoteView());
        frame_userPreview.setVisibility(View.VISIBLE);

        linear_pause.setVisibility(View.GONE);

    }

    protected void onVideoCallProgressing() {
//for sub class
    }

    protected void onVideoCallEstablished() {
//for sub class
    }

    protected void onVideoCallEnded(Call call) {
//for sub class
        sendVideoCallMessageIfNeeded(call);

        releaseCallSection();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAndRemoveTask();
            }
        }, 1000);

    }

    protected void initControlCallClickEvents() {

        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBaseVideoController.toggleCaptureDevicePosition();
                isFacingFront = !isFacingFront;

            }
        });

        btn_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMuted) mBaseAudioController.unmute();
                else mBaseAudioController.mute();

                isMuted = !isMuted;

                btn_mute.setImageResource(isMuted ? R.drawable.ic_mute_active : R.drawable.ic_mute_nonactive);

            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Call call = getSinchServiceInterface().getCurrentCall(mVideoCallId);

                if (isPause) {
                    call.resumeVideo();
                    resumeMyPreview();
                } else {
                    call.pauseVideo();
                    pauseMyPreview();
                }

                isPause = !isPause;

                btn_pause.setImageResource(isPause ? R.drawable.ic_pause_video_active : R.drawable.ic_pause_video_nonactive);

            }
        });


    }

    protected void initTouchScreenEvents() {

        frame_userPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTouchScreen();
            }
        });

        linear_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTouchScreen();
            }
        });

    }

    protected void onTouchScreen() {

        if (linear_btns.getVisibility() == View.VISIBLE)
            linear_btns.setVisibility(View.GONE);
        else linear_btns.setVisibility(View.VISIBLE);

    }

    protected void resumeMyPreview() {

        if (frame_myPreview.getChildCount() != 0) frame_myPreview.removeAllViews();
        frame_myPreview.addView(mBaseVideoController.getLocalView());
        frame_myPreview.setVisibility(View.VISIBLE);

        btn_switch.setClickable(true);
        btn_switch.setImageResource(R.drawable.ic_switch_camera_active);

    }

    protected void pauseMyPreview() {

        if (frame_myPreview.getChildCount() != 0) frame_myPreview.removeAllViews();
        frame_myPreview.setVisibility(View.GONE);

        btn_switch.setClickable(false);
        btn_switch.setImageResource(R.drawable.ic_switch_camera_nonactive);

    }

    protected void disableAllButton() {
        //for sub class

        btn_switch.setClickable(false);
        btn_pause.setClickable(false);
        btn_mute.setClickable(false);
        btn_hangup.setClickable(false);
        btn_switch.setImageResource(R.drawable.ic_switch_camera_nonactive);
        btn_pause.setImageResource(R.drawable.ic_pause_video_unclickable);
        btn_mute.setImageResource(R.drawable.ic_mute_unclickable);
        btn_hangup.setImageResource(R.drawable.ic_hangup_nonactive);

        disableAnswerButtonIfNeeded();

    }

    protected void disableAnswerButtonIfNeeded() {
        //for incomming call
    }

    protected void releaseCallSection() {

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        tv_status.setText("Kết thúc cuộc gọi");

        mBaseAudioPlayer.stopProgressTone();
        mBaseAudioPlayer.stopRingtone();

        if (mBaseAudioController != null) {
            mBaseAudioController.unmute();
            mBaseAudioController.disableSpeaker();
            mBaseAudioController = null;
        }

        if (mBaseVideoController != null) {
            if (!isFacingFront) mBaseVideoController.toggleCaptureDevicePosition();
            mBaseVideoController = null;
        }

        disableAllButton();

        if (frame_userPreview.getChildCount() != 0) frame_userPreview.removeAllViews();
        if (frame_myPreview.getChildCount() != 0) frame_myPreview.removeAllViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseCallSection();

        if (mVideoCallId != null) {
            final Call call = getSinchServiceInterface().getCurrentCall(mVideoCallId);
            if (call != null && call.getState() != CallState.ENDED) call.hangup();
        }

    }

    protected void sendVideoCallMessageIfNeeded(Call call) {
        //for subclass
    }

    protected void showVideoCallRequestPermissionDialog(Activity activity) {

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

    protected class MyVideoCallListener implements VideoCallListener {

        @Override
        public void onVideoTrackAdded(Call call) {

            btn_switch.setImageResource(R.drawable.ic_switch_camera_active);
            btn_pause.setImageResource(R.drawable.ic_pause_video_nonactive);
            btn_mute.setImageResource(R.drawable.ic_mute_nonactive);

            if (frame_userPreview.getChildCount() != 0) frame_userPreview.removeAllViews();
            if (frame_myPreview.getChildCount() != 0) frame_myPreview.removeAllViews();

            addMyPreview();

            addUserPreview();

            initControlCallClickEvents();
        }

        @Override
        public void onVideoTrackPaused(Call call) {

            onUserPreviewPaused();

        }

        @Override
        public void onVideoTrackResumed(Call call) {
            onUserPreviewResumed();
        }

        @Override
        public void onCallProgressing(Call call) {
            onVideoCallProgressing();
        }

        @Override
        public void onCallEstablished(Call call) {

            onVideoCallEstablished();

            initTouchScreenEvents();
        }

        @Override
        public void onCallEnded(Call call) {

            onVideoCallEnded(call);

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }


}
