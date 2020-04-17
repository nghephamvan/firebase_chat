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
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.OtherClasses.AppLocalDatabase;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoScalingType;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALLEE_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VIDEO_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VIDEO_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_VIDEO_CALL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.VIDEO_CALL_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isVideoCallPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkHasChildBlock;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.markMessageIsSent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;

public class VideoCallScreen extends BaseVideoCallActivity {

    private String mCalleeId;

    private boolean shouldSendMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_callscreen);

        mCalleeId = (String) getDataFromIntent(VideoCallScreen.this, INTENT_KEY_CALLEE_ID);

        shouldSendMessage = false;

        if (mCalleeId != null) {

            initViews();

            loadCalleeProfile();

            btn_hangup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btn_hangup.setImageResource(R.drawable.ic_hangup_nonactive);

                    if (mCalleeId != null && getSinchServiceInterface() != null) {

                        final Call call = getSinchServiceInterface().getCurrentCall(mVideoCallId);
                        if (call != null) call.hangup();
                        else finishAndRemoveTask();

                    } else finishAndRemoveTask();

                }
            });

        } else finishAndRemoveTask();
    }

    private void initViews() {

        linear_pause = (LinearLayout) findViewById(R.id.linear_pause);
        linear_call_infor = (LinearLayout) findViewById(R.id.linear_call_infor);
        linear_btns = (LinearLayout) findViewById(R.id.linear_btns);

        tv_status = (TextView) findViewById(R.id.tv_call_status);
        tv_name = (TextView) findViewById(R.id.tv_name);

        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);
        civ_pause_avatar = (CircleImageView) findViewById(R.id.civ_pause);

        btn_pause = (ImageView) findViewById(R.id.img_pause_stream);
        btn_mute = (ImageView) findViewById(R.id.img_mute_microphone);
        btn_switch = (ImageView) findViewById(R.id.img_switch_camera);
        btn_hangup = (ImageView) findViewById(R.id.img_hangup_call);

        frame_userPreview = (FrameLayout) findViewById(R.id.frame_user_preview);
        frame_myPreview = (FrameLayout) findViewById(R.id.frame_my_preview);

    }

    private void loadCalleeProfile() {

        getSingleUserProfile(mCalleeId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                if (callbackUserProfile != null) {

                    setAvatarToView(VideoCallScreen.this, callbackUserProfile.getThumbAvatarUrl(), callbackUserProfile.getName(), civ_avatar);

                    setAvatarToView(VideoCallScreen.this, callbackUserProfile.getThumbAvatarUrl(), callbackUserProfile.getName(), civ_pause_avatar);

                    tv_name.setText(callbackUserProfile.getName());

                    tv_status.setText("Đang kết nối...");

                }

            }
        });

    }

    private void startCalling() {

        mBaseAudioController = getSinchServiceInterface().getAudioController();
        mBaseAudioController.unmute();
        mBaseAudioController.enableSpeaker();

        mBaseVideoController = getSinchServiceInterface().getVideoController();
        mBaseVideoController.setResizeBehaviour(VideoScalingType.ASPECT_FILL);

        if (frame_userPreview.getChildCount() != 0) frame_userPreview.removeAllViews();
        frame_userPreview.addView(mBaseVideoController.getLocalView());
        frame_userPreview.setVisibility(View.VISIBLE);

        final Map<String, String> headers = new HashMap<>();
        headers.put(INTENT_KEY_CALLEE_ID, mCalleeId);

        Call call = getSinchServiceInterface().callUserVideo(mCalleeId, headers);

        call.addCallListener(new MyVideoCallListener());

        mVideoCallId = call.getCallId();

    }

    private void sendCallMessages(int callDuration, int messageType) {

        final AppLocalDatabase appLocalDatabase = new AppLocalDatabase(VideoCallScreen.this);

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        String myContentToSend;

        String userContentToSend;

        if (messageType == MESSAGE_TYPE_VIDEO_CALL_SUCCESS) {

            myContentToSend = "Cuộc gọi video đi thành công";
            userContentToSend = "Cuộc gọi video đến thành công";

        } else {

            myContentToSend = "Cuộc gọi video đi bị nhỡ";
            userContentToSend = "Cuộc gọi video đến bị nhỡ";

        }

        int notificationType;

        if (messageType == MESSAGE_TYPE_VIDEO_CALL_SUCCESS) {
            notificationType = NOTIFICATION_TYPE_VIDEO_CALL_SUCCESS;
        } else notificationType = NOTIFICATION_TYPE_VIDEO_CALL_NOT_ANSWERED;

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), mCalleeId, null,
                myContentToSend, MESSAGE_STATUS_SENDING,
                sendTime, 0, messageType,
                notificationId, callDuration, null, false, null, null, null);

        final Message userMess = new Message(
                messageId, getMyFirebaseUserId(), mCalleeId, null,
                userContentToSend, MESSAGE_STATUS_SENT,
                sendTime, 0, messageType,
                notificationId, callDuration, null, false, null, null, null);

        appLocalDatabase.addMessage(myMess);

        if (isConnectedToFirebaseService(VideoCallScreen.this)) {

            ROOT_REF.child(CHILD_MESSAGES)
                    .child(getMyFirebaseUserId()).child(mCalleeId)
                    .child(messageId).setValue(myMess)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            appLocalDatabase.deleteMessage(messageId);

                            ROOT_REF.child(CHILD_MESSAGES)
                                    .child(getMyFirebaseUserId()).child(mCalleeId)
                                    .child(messageId).child(kMessageStatus).setValue(MESSAGE_STATUS_SENT)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            ROOT_REF.child(CHILD_MESSAGES)
                                                    .child(mCalleeId).child(getMyFirebaseUserId())
                                                    .child(messageId).setValue(userMess)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                                @Override
                                                                public void OnCallBack(User callbackUserProfile) {

                                                                    sendNotificationToUser(
                                                                            mCalleeId,
                                                                            getMyFirebaseUserId(),
                                                                            null,
                                                                            messageId,
                                                                            userContentToSend,
                                                                            callbackUserProfile.getName(),
                                                                            callbackUserProfile.getThumbAvatarUrl(),
                                                                            notificationType,
                                                                            notificationId);

                                                                }
                                                            });

                                                        }
                                                    });

                                        }
                                    });


                        }

                    });

        } else {

            ROOT_REF.child(CHILD_MESSAGES)
                    .child(getMyFirebaseUserId()).child(mCalleeId)
                    .child(messageId).setValue(myMess)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            appLocalDatabase.deleteMessage(messageId);

                            checkHasChildBlock(mCalleeId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                                @Override
                                public void OnCallBack(boolean hasChildBlock) {

                                    if (hasChildBlock) {

                                        showMessageDialog(VideoCallScreen.this, "Lỗi trong khi gửi tin nhắn !");

                                    } else {

                                        markMessageIsSent(messageId, mCalleeId, null);

                                        ROOT_REF.child(CHILD_MESSAGES)
                                                .child(mCalleeId).child(getMyFirebaseUserId())
                                                .child(messageId).setValue(userMess)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                            @Override
                                                            public void OnCallBack(User callbackUserProfile) {

                                                                sendNotificationToUser(
                                                                        mCalleeId,
                                                                        getMyFirebaseUserId(),
                                                                        null,
                                                                        messageId,
                                                                        userContentToSend,
                                                                        callbackUserProfile.getName(),
                                                                        callbackUserProfile.getThumbAvatarUrl(),
                                                                        notificationType,
                                                                        notificationId);

                                                            }
                                                        });

                                                    }
                                                });

                                    }
                                }
                            });


                        }

                    });

        }


    }

    @Override
    protected void onConnectToSinchService() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (isVideoCallPermissionsGranted(this)) startCalling();

            else ActivityCompat.requestPermissions(this, PERMISSIONS_VIDEO_CALL, VIDEO_CALL_CODE);

        } else startCalling();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VIDEO_CALL_CODE) {

            if (isAllPermissionsGrantedInResult(grantResults)) startCalling();
            else showVideoCallRequestPermissionDialog(VideoCallScreen.this);

        }


    }

    @Override
    protected void showInternetConnectionView(boolean isConnectedToFirebase) {
        TextView textView = (TextView) findViewById(R.id.tv_no_internet);
        if (textView != null) textView.setVisibility(isConnectedToFirebase ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onVideoCallProgressing() {

        mBaseAudioPlayer.playProgressTone();

        shouldSendMessage = true;

    }

    @Override
    protected void onVideoCallEstablished() {

        mBaseAudioPlayer.stopProgressTone();

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        linear_call_infor.setVisibility(View.GONE);

        shouldPauseStreamWhenStop = true;
    }


    @Override
    protected void sendVideoCallMessageIfNeeded(Call call) {
        //need to send message

        if (shouldSendMessage) {

            CallEndCause callEndCause = call.getDetails().getEndCause();

            sendCallMessages(call.getDetails().getDuration(), callEndCause.equals(CallEndCause.HUNG_UP) ? MESSAGE_TYPE_VIDEO_CALL_SUCCESS : MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED);

        }

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
