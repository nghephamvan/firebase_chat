package com.example.tranquoctrungcntt.uchat.Activities;

import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALLEE_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VOICE_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VOICE_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_VOICE_CALL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.VOICE_CALL_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isVoiceCallPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkHasChildBlock;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.markMessageIsSent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;

public class VoiceCallScreen extends BaseVoiceCallActivity {

    private String mCalleeId;

    private boolean shouldSendMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_callscreen);

        mCalleeId = (String) getDataFromIntent(VoiceCallScreen.this, INTENT_KEY_CALLEE_ID);

        shouldSendMessage = false;

        if (mCalleeId != null) {

            initViews();

            loadCalleeProfile();

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

        } else finishAndRemoveTask();
    }

    private void initViews() {

        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_status = (TextView) findViewById(R.id.tv_call_status);
        btn_hangup = (ImageView) findViewById(R.id.img_hangup_call);
        btn_mute = (ImageView) findViewById(R.id.img_mute_microphone);
        btn_speaker = (ImageView) findViewById(R.id.img_speaker);

    }

    private void loadCalleeProfile() {

        getSingleUserProfile(mCalleeId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                if (callbackUserProfile != null) {

                    setAvatarToView(VoiceCallScreen.this, callbackUserProfile.getThumbAvatarUrl(), callbackUserProfile.getName(), civ_avatar);

                    tv_name.setText(callbackUserProfile.getName());
                    tv_status.setText("Đang kết nối...");

                }

            }
        });

    }

    private void startCalling() {


        mBaseAudioController = getSinchServiceInterface().getAudioController();
        mBaseAudioController.disableSpeaker();
        mBaseAudioController.unmute();

        final Map<String, String> headers = new HashMap<>();
        headers.put(INTENT_KEY_CALLEE_ID, mCalleeId);

        Call call = getSinchServiceInterface().callUser(mCalleeId, headers);
        call.addCallListener(new MyVoiceCallListener());

        mVoiceCallId = call.getCallId();

        initControlCallClickEvents();

    }

    @Override
    protected void onConnectToSinchService() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (isVoiceCallPermissionsGranted(this)) startCalling();

            else ActivityCompat.requestPermissions(this, PERMISSIONS_VOICE_CALL, VOICE_CALL_CODE);

        } else startCalling();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VOICE_CALL_CODE) {
            if (isAllPermissionsGrantedInResult(grantResults)) startCalling();
            else showVoiceCallRequestPermissionDialog(VoiceCallScreen.this);
        }

    }

    @Override
    protected void showInternetConnectionView(boolean isConnectedToFirebase) {
        TextView textView = (TextView) findViewById(R.id.tv_no_internet);
        if (textView != null) textView.setVisibility(isConnectedToFirebase ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onVoiceCallProgressing() {

        mBaseAudioPlayer.playProgressTone();

        shouldSendMessage = true;

    }

    @Override
    protected void onVoiceCallEstablished() {

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        mBaseAudioPlayer.stopProgressTone();

        startTimer();
    }

    @Override
    protected void sendVoiceCallMessageIfNeeded(Call call) {

        if (shouldSendMessage) {

            CallEndCause callEndCause = call.getDetails().getEndCause();

            sendCallMessages(call.getDetails().getDuration(), callEndCause.equals(CallEndCause.HUNG_UP) ? MESSAGE_TYPE_VOICE_CALL_SUCCESS : MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED);

        }
    }

    private void sendCallMessages(int callDuration, int messageType) {

        final AppLocalDatabase appLocalDatabase = new AppLocalDatabase(VoiceCallScreen.this);

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        String myContentToSend;
        String userContentToSend;

        if (messageType == MESSAGE_TYPE_VOICE_CALL_SUCCESS) {

            myContentToSend = "Cuộc gọi thoại đi thành công";
            userContentToSend = "Cuộc gọi thoại đến thành công";

        } else {

            myContentToSend = "Cuộc gọi thoại đi bị nhỡ";
            userContentToSend = "Cuộc gọi thoại đến bị nhỡ";

        }

        int notificationType;

        if (messageType == MESSAGE_TYPE_VOICE_CALL_SUCCESS) {
            notificationType = NOTIFICATION_TYPE_VOICE_CALL_SUCCESS;
        } else notificationType = NOTIFICATION_TYPE_VOICE_CALL_NOT_ANSWERED;

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

        if (isConnectedToFirebaseService(VoiceCallScreen.this)) {

            ROOT_REF.child(CHILD_MESSAGES)
                    .child(getMyFirebaseUserId()).child(mCalleeId)
                    .child(messageId).setValue(myMess)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //todo : delete from local
                            appLocalDatabase.deleteMessage(messageId);

                            ROOT_REF.child(CHILD_MESSAGES)
                                    .child(getMyFirebaseUserId()).child(mCalleeId)
                                    .child(messageId).child(kMessageStatus)
                                    .setValue(MESSAGE_STATUS_SENT)
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

                                        showMessageDialog(VoiceCallScreen.this, "Lỗi trong khi gửi tin nhắn !");

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

}
