package com.example.tranquoctrungcntt.uchat.Activities;

import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALLER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALL_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_VOICE_CALL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.VOICE_CALL_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isVoiceCallPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class VoiceIncomingCallScreen extends BaseVoiceCallActivity {

    private ImageView btn_answer;

    private LinearLayout linear_btns;

    private String mCallerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incomming_callscreen);

        mCallerId = (String) getDataFromIntent(VoiceIncomingCallScreen.this, INTENT_KEY_CALLER_ID);

        if (mCallerId != null) {

            initViews();

            loadCallerProfile();

            btn_hangup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btn_hangup.setImageResource(R.drawable.ic_hangup_nonactive);

                    if (mVoiceCallId != null && getSinchServiceInterface() != null) {

                        final Call call = getSinchServiceInterface().getCurrentCall(mVoiceCallId);
                        if (call != null) call.hangup();
                        else finishAndRemoveTask();

                    } else finishAndRemoveTask();


                }
            });

            btn_answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mVoiceCallId != null && getSinchServiceInterface() != null) {

                        btn_answer.setImageResource(R.drawable.ic_answer_voice_call_nonactive);

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (isVoiceCallPermissionsGranted(VoiceIncomingCallScreen.this))
                                answerCall();

                            else
                                ActivityCompat.requestPermissions(VoiceIncomingCallScreen.this, PERMISSIONS_VOICE_CALL, VOICE_CALL_CODE);

                        } else answerCall();

                    }

                }
            });

        } else finishAndRemoveTask();


    }

    private void initViews() {

        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_status = (TextView) findViewById(R.id.tv_call_status);
        btn_hangup = (ImageView) findViewById(R.id.img_hangup_call);
        btn_mute = (ImageView) findViewById(R.id.img_mute_microphone);
        btn_speaker = (ImageView) findViewById(R.id.img_speaker);
        btn_answer = (ImageView) findViewById(R.id.img_answer_call);
        linear_btns = (LinearLayout) findViewById(R.id.linear_btns);

    }

    @Override
    protected void onConnectToSinchService() {

        mVoiceCallId = (String) getDataFromIntent(VoiceIncomingCallScreen.this, INTENT_KEY_CALL_ID);

        if (mVoiceCallId != null) {

            mBaseAudioController = getSinchServiceInterface().getAudioController();

            mBaseAudioController.disableSpeaker();
            mBaseAudioController.unmute();

            mBaseAudioPlayer.playRingtone();

            final Call call = getSinchServiceInterface().getCurrentCall(mVoiceCallId);

            if (call != null) call.addCallListener(new MyVoiceCallListener());
            else finishAndRemoveTask();

        } else finishAndRemoveTask();


    }

    private void answerCall() {

        if (mVoiceCallId != null) {

            final Call call = getSinchServiceInterface().getCurrentCall(mVoiceCallId);
            if (call != null) call.answer();
            else finishAndRemoveTask();

        } else finishAndRemoveTask();


    }

    private void loadCallerProfile() {

        getSingleUserProfile(mCallerId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                if (callbackUserProfile != null) {

                    setAvatarToView(VoiceIncomingCallScreen.this, callbackUserProfile.getThumbAvatarUrl(), callbackUserProfile.getName(), civ_avatar);

                    tv_name.setText(callbackUserProfile.getName());

                    tv_status.setText("Đang gọi thoại cho bạn...");

                }

            }
        });

    }

    @Override
    protected void disableAnswerButtonIfNeeded() {
        btn_answer.setClickable(false);
        btn_answer.setImageResource(R.drawable.ic_answer_voice_call_nonactive);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VOICE_CALL_CODE) {
            if (isAllPermissionsGrantedInResult(grantResults)) answerCall();
            else showVoiceCallRequestPermissionDialog(VoiceIncomingCallScreen.this);
        }


    }

    @Override
    protected void showInternetConnectionView(boolean isConnectedToFirebase) {
        TextView textView = (TextView) findViewById(R.id.tv_no_internet);
        if (textView != null) textView.setVisibility(isConnectedToFirebase ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onVoiceCallEstablished() {
        super.onVoiceCallEstablished();

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        initControlCallClickEvents();

        mBaseAudioPlayer.stopRingtone();

        mBaseAudioController.disableSpeaker();

        linear_btns.setWeightSum(3);

        btn_answer.setVisibility(View.GONE);
        btn_mute.setVisibility(View.VISIBLE);
        btn_speaker.setVisibility(View.VISIBLE);

        startTimer();
    }

}

