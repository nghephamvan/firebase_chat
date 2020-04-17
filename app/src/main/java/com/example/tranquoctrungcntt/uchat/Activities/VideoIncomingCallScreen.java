package com.example.tranquoctrungcntt.uchat.Activities;

import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoScalingType;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALLER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALL_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_VIDEO_CALL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.VIDEO_CALL_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isVideoCallPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class VideoIncomingCallScreen extends BaseVideoCallActivity {

    private ImageView btn_answer;

    private String mCallerId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_incomming_callscreen);

        mCallerId = (String) getDataFromIntent(VideoIncomingCallScreen.this, INTENT_KEY_CALLER_ID);

        if (mCallerId != null) {

            initViews();

            loadCallerProfile();

            btn_answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mVideoCallId != null && getSinchServiceInterface() != null) {

                        btn_answer.setImageResource(R.drawable.ic_answer_video_call_nonactive);

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (isVideoCallPermissionsGranted(VideoIncomingCallScreen.this))
                                answerCall();

                            else
                                ActivityCompat.requestPermissions(VideoIncomingCallScreen.this, PERMISSIONS_VIDEO_CALL, VIDEO_CALL_CODE);

                        } else answerCall();

                    } else finishAndRemoveTask();

                }
            });

            btn_hangup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btn_hangup.setImageResource(R.drawable.ic_hangup_nonactive);

                    if (mVideoCallId != null && getSinchServiceInterface() != null) {

                        Call call = getSinchServiceInterface().getCurrentCall(mVideoCallId);
                        if (call != null) call.hangup();
                        else finishAndRemoveTask();

                    } else finishAndRemoveTask();

                }
            });


        } else finishAndRemoveTask();

    }

    private void initViews() {

        civ_pause_avatar = (CircleImageView) findViewById(R.id.civ_pause);
        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);

        linear_call_infor = (LinearLayout) findViewById(R.id.linear_call_infor);
        linear_btns = (LinearLayout) findViewById(R.id.linear_btns);
        linear_pause = (LinearLayout) findViewById(R.id.linear_pause);

        tv_status = (TextView) findViewById(R.id.tv_call_status);
        tv_name = (TextView) findViewById(R.id.tv_name);

        btn_pause = (ImageView) findViewById(R.id.img_pause_stream);
        btn_mute = (ImageView) findViewById(R.id.img_mute_microphone);
        btn_switch = (ImageView) findViewById(R.id.img_switch_camera);
        btn_hangup = (ImageView) findViewById(R.id.img_hangup_call);
        btn_answer = (ImageView) findViewById(R.id.img_answer_call);

        frame_userPreview = (FrameLayout) findViewById(R.id.frame_user_preview);
        frame_myPreview = (FrameLayout) findViewById(R.id.frame_my_preview);

    }

    private void loadCallerProfile() {

        getSingleUserProfile(mCallerId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                if (callbackUserProfile != null) {

                    setAvatarToView(VideoIncomingCallScreen.this, callbackUserProfile.getThumbAvatarUrl(), callbackUserProfile.getName(), civ_avatar);

                    setAvatarToView(VideoIncomingCallScreen.this, callbackUserProfile.getThumbAvatarUrl(), callbackUserProfile.getName(), civ_pause_avatar);

                    tv_name.setText(callbackUserProfile.getName());

                    tv_status.setText("Đang gọi video cho bạn...");

                }

            }
        });

    }

    private void answerCall() {

        if (mVideoCallId != null) {

            final Call call = getSinchServiceInterface().getCurrentCall(mVideoCallId);
            if (call != null) call.answer();
            else finishAndRemoveTask();

        } else finishAndRemoveTask();

    }

    @Override
    protected void onConnectToSinchService() {

        mVideoCallId = (String) getDataFromIntent(VideoIncomingCallScreen.this, INTENT_KEY_CALL_ID);

        if (mVideoCallId != null) {

            mBaseAudioController = getSinchServiceInterface().getAudioController();

            mBaseAudioController.disableSpeaker();
            mBaseAudioController.unmute();

            mBaseAudioPlayer.playRingtone();

            mBaseVideoController = getSinchServiceInterface().getVideoController();
            mBaseVideoController.setResizeBehaviour(VideoScalingType.ASPECT_FILL);

            final Call call = getSinchServiceInterface().getCurrentCall(mVideoCallId);

            if (call != null) call.addCallListener(new MyVideoCallListener());
            else finishAndRemoveTask();

        } else finishAndRemoveTask();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VIDEO_CALL_CODE) {
            if (isAllPermissionsGrantedInResult(grantResults)) answerCall();
            else showVideoCallRequestPermissionDialog(VideoIncomingCallScreen.this);
        }

    }

    @Override
    protected void showInternetConnectionView(boolean isConnectedToFirebase) {
        TextView textView = (TextView) findViewById(R.id.tv_no_internet);
        if (textView != null) textView.setVisibility(isConnectedToFirebase ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onVideoCallEstablished() {

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        mBaseAudioPlayer.stopRingtone();

        mBaseAudioController.enableSpeaker();

        linear_btns.setWeightSum(4);
        linear_call_infor.setVisibility(View.GONE);

        btn_answer.setVisibility(View.GONE);
        btn_mute.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.VISIBLE);
        btn_switch.setVisibility(View.VISIBLE);
        btn_hangup.setVisibility(View.VISIBLE);

        shouldPauseStreamWhenStop = true;

    }

    @Override
    protected void disableAnswerButtonIfNeeded() {
        btn_answer.setClickable(false);
        btn_answer.setImageResource(R.drawable.ic_answer_video_call_nonactive);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (shouldPauseStreamWhenStop) {

            final Call call = getSinchServiceInterface() != null ? getSinchServiceInterface().getCurrentCall(mVideoCallId) : null;

            if (call != null && !call.getState().equals(CallState.ENDED)) {
                if (!isPause) {
                    call.resumeVideo();
                    resumeMyPreview();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (shouldPauseStreamWhenStop) {

            final Call call = getSinchServiceInterface() != null ? getSinchServiceInterface().getCurrentCall(mVideoCallId) : null;

            if (call != null && !call.getState().equals(CallState.ENDED)) {
                if (!isPause) {
                    call.pauseVideo();
                    pauseMyPreview();
                }
            }
        }
    }
}
