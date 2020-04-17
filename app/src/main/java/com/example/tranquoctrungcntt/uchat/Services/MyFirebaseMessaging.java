package com.example.tranquoctrungcntt.uchat.Services;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.example.tranquoctrungcntt.uchat.Notification.NormalNotification;
import com.example.tranquoctrungcntt.uchat.Notification.OreoNotification;
import com.example.tranquoctrungcntt.uchat.Notification.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;

import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MUTE_NOTIFICATIONS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_TOKENS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALLEE_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MY_AVATAR_COLOR_GENERATOR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_RECEIVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_GROUP_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_NEW_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_REMOVE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VIDEO_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VOICE_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.getChattingUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.ableToUseApp;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getAndroidID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isVideoCallPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isVoiceCallPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.getNotificationsRequestcode;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.updateNotificationsRequestcode;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.keepSyncAll;


public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (ableToUseApp(getApplicationContext())) {

            if (SinchHelpers.isSinchPushPayload(remoteMessage.getData())) {

                connectToSinch(remoteMessage);

            } else {

                final String receiverId = remoteMessage.getData().get("receiverId");

                if (receiverId.equals(getMyFirebaseUserId())) {

                    keepSyncAll(true);

                    final String messageSenderId = remoteMessage.getData().get("notificationSenderId");
                    final String groupId = remoteMessage.getData().get("groupId");
                    final String messageId = remoteMessage.getData().get("messageId");
                    final int notificationType = Integer.parseInt(remoteMessage.getData().get("notificationType"));
                    final int notificationId = Integer.parseInt(remoteMessage.getData().get("notificationId"));

                    switch (notificationType) {

                        case NOTIFICATION_TYPE_REMOVE_MESSAGE:

                            getNotificationManager(getApplicationContext()).cancel(notificationId);

                            break;

                        case NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST:
                        case NOTIFICATION_TYPE_NEW_FRIEND_REQUEST:

                            pushNotification(remoteMessage);

                            break;

                        default:

                            if (groupId != null) {

                                if (notificationType == NOTIFICATION_TYPE_GROUP_MESSAGE) {

                                    markMessageIsReceived(messageId, messageSenderId, groupId);

                                }

                            } else markMessageIsReceived(messageId, messageSenderId, null);

                            final String chattingId = groupId != null ? groupId : messageSenderId;

                            final boolean ableToPush = getChattingUserId() == null || !getChattingUserId().equals(chattingId);

                            final boolean isNotCallMessage = notificationType != NOTIFICATION_TYPE_VOICE_CALL_SUCCESS
                                    && notificationType != NOTIFICATION_TYPE_VIDEO_CALL_SUCCESS;

                            if (ableToPush) {

                                if (isNotCallMessage) {

                                    ROOT_REF.child(CHILD_SETTINGS)
                                            .child(getMyFirebaseUserId()).child(chattingId)
                                            .child(CHILD_MUTE_NOTIFICATIONS)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.exists()) {
                                                        //mute notification
                                                    } else pushNotification(remoteMessage);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }

                            }
                    }

                }

            }
        }

    }


    private void connectToSinch(RemoteMessage remoteMessage) {

        new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                final SinchService.SinchServiceInterface sinchServiceInterface = (SinchService.SinchServiceInterface) service;

                if (sinchServiceInterface != null) {

                    final NotificationResult result = SinchHelpers.queryPushNotificationPayload(getApplicationContext(), remoteMessage.getData());

                    final CallNotificationResult callResult = result.getCallResult();

                    final String calleeId = callResult.getHeaders().get(INTENT_KEY_CALLEE_ID);

                    if (!callResult.isCallCanceled()) {

                        final boolean isMyCall = calleeId.equals(getMyFirebaseUserId()) && calleeId.equals(sinchServiceInterface.getSinchClient().getLocalUserId());

                        if (isMyCall) {

                            if (Build.VERSION.SDK_INT >= 23) {

                                if (callResult.isVideoOffered()) {

                                    if (isVideoCallPermissionsGranted(getApplicationContext())) {
                                        sinchServiceInterface.relayRemotePushNotificationPayload(remoteMessage.getData());
                                    }

                                } else if (isVoiceCallPermissionsGranted(getApplicationContext())) {
                                    sinchServiceInterface.relayRemotePushNotificationPayload(remoteMessage.getData());
                                }

                            } else {
                                sinchServiceInterface.relayRemotePushNotificationPayload(remoteMessage.getData());
                            }

                        }
                    }

                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) { }

            public void relayMessageData() {
                getApplicationContext().bindService(new Intent(getApplicationContext(), SinchService.class), this, BIND_AUTO_CREATE);
            }

        }.relayMessageData();

    }

    private void pushNotification(RemoteMessage remoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationOreo(remoteMessage);
        else sendNormalNotification(remoteMessage);
    }

    private void markMessageIsReceived(String messageId, String messageSenderId, String groupId) {

        markReceivedForMe(messageId, messageSenderId, groupId);

        markReceivedForSender(messageId, messageSenderId, groupId);

    }

    private void markReceivedForSender(String messageId, String messageSenderId, String groupId) {

        final String chatId = groupId != null ? groupId : getMyFirebaseUserId();

        final DatabaseReference senderMessageStatusRef = ROOT_REF.child(CHILD_MESSAGES)
                .child(messageSenderId).child(chatId)
                .child(messageId).child(kMessageStatus);

        senderMessageStatusRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                final String currentStatus = dataSnapshot.getValue(String.class);

                if (currentStatus != null)
                    if (currentStatus.equals(MESSAGE_STATUS_SENT))
                        senderMessageStatusRef.setValue(MESSAGE_STATUS_RECEIVED);


            }
        });
    }

    private void markReceivedForMe(String messageId, String messageSenderId, String groupId) {

        final String chatId = groupId != null ? groupId : messageSenderId;

        final DatabaseReference myMessageStatusRef = ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(chatId)
                .child(messageId).child(kMessageStatus);

        myMessageStatusRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                final String currentStatus = dataSnapshot.getValue(String.class);

                if (currentStatus != null)
                    if (currentStatus.equals(MESSAGE_STATUS_SENT))
                        myMessageStatusRef.setValue(MESSAGE_STATUS_RECEIVED);


            }
        });
    }


    private Bitmap getAvatarBitmap(String source, String title) {

        TextDrawable avatarDrawable = TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .endConfig().buildRound((title.charAt(0) + "").toUpperCase(), MY_AVATAR_COLOR_GENERATOR.getColor(title.replaceAll(" ", "")));

        Bitmap bitmap;

        if (source != null) {

            try {
                bitmap = Glide.with(getApplicationContext())
                        .asBitmap().load(source).fitCenter().circleCrop().submit().get();

            } catch (Exception e) {
                bitmap = drawableToBitmap(avatarDrawable);
            }

        } else {

            bitmap = drawableToBitmap(avatarDrawable);

        }

        return bitmap;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

//        int width = drawable.getIntrinsicWidth();
//        width = width > 0 ? width : 96; // Replaced the 1 by a 96
//        int height = drawable.getIntrinsicHeight();
//        height = height > 0 ? height : 96; // Replaced the 1 by a 96

        Bitmap bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void sendNotificationOreo(final RemoteMessage remoteMessage) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Map<String, String> notificationData = remoteMessage.getData();

                final String messageSenderId = notificationData.get("notificationSenderId");
                final String groupId = notificationData.get("groupId");

                final String title = notificationData.get("title");
                final String content = notificationData.get("content");
                final String avatar = notificationData.get("avatar");

                final int notificationType = Integer.parseInt(notificationData.get("notificationType"));
                final int notificationId = Integer.parseInt(notificationData.get("notificationId"));

                final int requestCode = getNotificationsRequestcode(getApplicationContext());

                final OreoNotification oreoNotification = new OreoNotification(getApplicationContext());

                final Bitmap avatarBitmap = getAvatarBitmap(avatar, title);

                final Notification.Builder builder = oreoNotification.generateNotificationOreo(
                        messageSenderId, groupId,
                        title, content, avatarBitmap,
                        notificationType, requestCode, notificationId);

                getNotificationManager(getApplicationContext()).notify(notificationId, builder.build());

                updateNotificationsRequestcode(getApplicationContext());

            }
        }).start();


    }

    private void sendNormalNotification(final RemoteMessage remoteMessage) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Map<String, String> notificationData = remoteMessage.getData();

                final String messageSenderId = notificationData.get("notificationSenderId");
                final String groupId = notificationData.get("groupId");

                final String avatar = notificationData.get("avatar");
                final String title = notificationData.get("title");
                final String content = notificationData.get("content");

                final int notificationType = Integer.parseInt(notificationData.get("notificationType"));
                final int notificationId = Integer.parseInt(notificationData.get("notificationId"));

                final int requestCode = getNotificationsRequestcode(getApplicationContext());

                final NormalNotification normalNotification = new NormalNotification(getApplicationContext());

                final Bitmap avatarBitmap = getAvatarBitmap(avatar, title);

                final NotificationCompat.Builder builder = normalNotification.generateNormalNotification(
                        messageSenderId, groupId,
                        title, content, avatarBitmap,
                        notificationType, requestCode, notificationId);

                getNotificationManager(getApplicationContext()).notify(notificationId, builder.build());

                updateNotificationsRequestcode(getApplicationContext());

            }
        }).start();
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        if (isAccountValid()) {

            ROOT_REF.child(CHILD_TOKENS)
                    .child(getMyFirebaseUserId())
                    .child(getAndroidID(getApplicationContext()))
                    .setValue(new Token(s));

        }

    }
}
