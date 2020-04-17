package com.example.tranquoctrungcntt.uchat.Notification;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import androidx.core.app.NotificationCompat;

import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.R;
import com.example.tranquoctrungcntt.uchat.Services.SendNotificationActionData;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_ACCEPT_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_DENY_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_LIKE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_MUTE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_GROUP_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_NEW_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_UPDATE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VIDEO_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VIDEO_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VOICE_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_VOICE_CALL_SUCCESS;


public class NormalNotification {

    private Uri mSound;

    private Context mNotificationContext;

    public NormalNotification(Context context) {

        this.mNotificationContext = context;

        if (mSound == null) {

            mSound = Uri.parse("android.resource://" + mNotificationContext.getPackageName() + "/" + R.raw.notification_sound);
        }
    }

    public NotificationCompat.Builder getBaseBuilder(String title,
                                                     String body,
                                                     Bitmap avatar,
                                                     PendingIntent mainContentIntent) {
        long[] pattern = {0, 300};

        return new NotificationCompat.Builder(mNotificationContext)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(avatar)
                .setPriority(PRIORITY_MAX)
                .setContentIntent(mainContentIntent)
                .setSmallIcon(R.mipmap.app_logo)
                .setShowWhen(true)
                .setSound(mSound)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setWhen(System.currentTimeMillis());


    }

    public NotificationCompat.Builder TypeAcceptFriendRequest(String title,
                                                              String content,
                                                              Bitmap avatar,
                                                              PendingIntent mainContentIntent) {

        NotificationCompat.Action actionMain = new NotificationCompat.Action.Builder(0, "Nhắn tin ngay", mainContentIntent).build();

        return getBaseBuilder(title, content, avatar, mainContentIntent).addAction(actionMain);
    }

    public NotificationCompat.Builder TypeNewFriendRequest(String title,
                                                           String content,
                                                           Bitmap avatar,

                                                           PendingIntent acceptPendingIntent,
                                                           PendingIntent denyPendingIntent,
                                                           PendingIntent mainContentIntent) {

        NotificationCompat.Action actionAcceptFriendRequest = new NotificationCompat.Action.Builder(0, "Đồng ý", acceptPendingIntent).build();

        NotificationCompat.Action actionDenyFriendRequest = new NotificationCompat.Action.Builder(0, "Từ chối", denyPendingIntent).build();

        NotificationCompat.Action actionMain = new NotificationCompat.Action.Builder(0, "Xem tất cả", mainContentIntent).build();

        return getBaseBuilder(title, content, avatar, mainContentIntent)
                .addAction(actionAcceptFriendRequest)
                .addAction(actionDenyFriendRequest)
                .addAction(actionMain);
    }

    public NotificationCompat.Builder TypeCall(String title,
                                               String content,
                                               Bitmap avatar,
                                               PendingIntent likeIntent,
                                               PendingIntent mainContentIntent,
                                               PendingIntent muteIntent,
                                               int notificationType) {

        NotificationCompat.Action actionLike = new NotificationCompat.Action.Builder(0, "Thích", likeIntent).build();

        NotificationCompat.Action actionMain = new NotificationCompat.Action.Builder(0, "Xem tin nhắn", mainContentIntent).build();

        NotificationCompat.Action actionMute = new NotificationCompat.Action.Builder(0, "Tắt thông báo", muteIntent).build();


        NotificationCompat.Builder mBuilder
                = getBaseBuilder(title, content, avatar, mainContentIntent)
                .addAction(actionLike)
                .addAction(actionMain)
                .addAction(actionMute);

        if (notificationType == NOTIFICATION_TYPE_VOICE_CALL_NOT_ANSWERED)
            return mBuilder.setColorized(true).setSmallIcon(R.drawable.ic_missed_voice_call)
                    .setColor(mNotificationContext.getResources().getColor(R.color.red));

        if (notificationType == NOTIFICATION_TYPE_VIDEO_CALL_NOT_ANSWERED)
            return mBuilder.setColorized(true).setSmallIcon(R.drawable.ic_missed_video_call)
                    .setColor(mNotificationContext.getResources().getColor(R.color.red));

        return mBuilder;
    }

    public NotificationCompat.Builder TypeGroupMessage(String title,
                                                       String content,
                                                       Bitmap avatar,
                                                       PendingIntent likeIntent,
                                                       PendingIntent mainContentIntent,
                                                       PendingIntent muteIntent) {

        NotificationCompat.Action actionLike = new NotificationCompat.Action.Builder(0, "Thích", likeIntent).build();

        NotificationCompat.Action actionMain = new NotificationCompat.Action.Builder(0, "Xem tin nhắn", mainContentIntent).build();

        NotificationCompat.Action actionMute = new NotificationCompat.Action.Builder(0, "Tắt thông báo", muteIntent).build();

        Spannable sbName = new SpannableString(content);
        sbName.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, content.indexOf(":") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        long[] pattern = {0, 300};

        return new NotificationCompat.Builder(mNotificationContext)
                .setContentTitle(title)
                .setContentText(sbName)
                .setLargeIcon(avatar)
                .setContentIntent(mainContentIntent)
                .setSmallIcon(R.mipmap.app_logo)
                .setShowWhen(true)
                .setSound(mSound)
                .setPriority(PRIORITY_MAX)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setWhen(System.currentTimeMillis())
                .addAction(actionLike)
                .addAction(actionMain)
                .addAction(actionMute);

    }

    public NotificationCompat.Builder TypeMessage(String title,
                                                  String content,
                                                  Bitmap avatar,
                                                  PendingIntent likePendingIntent,
                                                  PendingIntent mainContentIntent,
                                                  PendingIntent muteIntent) {


        NotificationCompat.Action actionLike = new NotificationCompat.Action.Builder(0, "Thích", likePendingIntent).build();

        NotificationCompat.Action actionMain = new NotificationCompat.Action.Builder(0, "Xem tin nhắn", mainContentIntent).build();

        NotificationCompat.Action actionMute = new NotificationCompat.Action.Builder(0, "Tắt thông báo", muteIntent).build();

        return getBaseBuilder(title, content, avatar, mainContentIntent)
                .addAction(actionLike)
                .addAction(actionMain)
                .addAction(actionMute);

    }

    public NotificationCompat.Builder generateNormalNotification(String notificationSenderId,
                                                                 String groupId,
                                                                 String title,
                                                                 String content,
                                                                 Bitmap avatar,
                                                                 int notificationType,
                                                                 int requestCode,
                                                                 int notificationId) {

        final PendingIntent mainContentIntent = getMainContentIntent(requestCode);

        final PendingIntent likePendingIntent = getPendingIntentLike(notificationSenderId, groupId, requestCode, notificationId);

        final PendingIntent mutePendingIntent = getPendingIntentMute(notificationSenderId, groupId, requestCode, notificationId);

        switch (notificationType) {

            case NOTIFICATION_TYPE_NEW_FRIEND_REQUEST:

                PendingIntent acceptIntent = getPendingIntentAcceptFriendRequest(notificationSenderId, requestCode, notificationId);

                PendingIntent denyIntent = getPendingIntentDenyFriendRequest(notificationSenderId, requestCode, notificationId);

                return TypeNewFriendRequest(title, content, avatar, acceptIntent, denyIntent, mainContentIntent);

            case NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST:

                return TypeAcceptFriendRequest(title, content, avatar, mainContentIntent);

            case NOTIFICATION_TYPE_VIDEO_CALL_SUCCESS:
            case NOTIFICATION_TYPE_VOICE_CALL_SUCCESS:
            case NOTIFICATION_TYPE_VOICE_CALL_NOT_ANSWERED:
            case NOTIFICATION_TYPE_VIDEO_CALL_NOT_ANSWERED:

                return TypeCall(title, content, avatar, likePendingIntent, mainContentIntent, mutePendingIntent, notificationType);

            case NOTIFICATION_TYPE_GROUP_MESSAGE:
            case NOTIFICATION_TYPE_UPDATE_GROUP:
            case NOTIFICATION_TYPE_UPDATE_MEMBER:

                return TypeGroupMessage(title, content, avatar, likePendingIntent, mainContentIntent, mutePendingIntent);

        }

        return TypeMessage(title, content, avatar, likePendingIntent, mainContentIntent, mutePendingIntent);

    }


    private PendingIntent getPendingIntentLike(String userId, String groupId, int requestCode, int notificationId) {

        Intent intentLike = new Intent(mNotificationContext, SendNotificationActionData.class);

        intentLike.putExtra(INTENT_KEY_NOTIFICATION_DATA, INTENT_KEY_NOTIFICATION_DATA_LIKE);

        intentLike.putExtra(INTENT_KEY_NOTIFICATION_ID, notificationId);

        intentLike.addCategory("type like");

        intentLike.putExtra(groupId != null ? INTENT_KEY_GROUP_ID : INTENT_KEY_USER_ID, groupId != null ? groupId : userId);

        return PendingIntent.getService(mNotificationContext, requestCode, intentLike, PendingIntent.FLAG_ONE_SHOT);
    }


    private PendingIntent getMainContentIntent(final int requestCode) {

        Intent intent = new Intent(mNotificationContext, MainActivity.class);

        intent.addCategory("type main");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return PendingIntent.getActivity(mNotificationContext, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);

    }

    private PendingIntent getPendingIntentAcceptFriendRequest(String senderId, int requestCode, int notificationId) {

        Intent intentFriendReq = new Intent(mNotificationContext, SendNotificationActionData.class);

        intentFriendReq.putExtra(INTENT_KEY_NOTIFICATION_DATA, INTENT_KEY_NOTIFICATION_DATA_ACCEPT_REQUEST);
        intentFriendReq.putExtra(INTENT_KEY_USER_ID, senderId);
        intentFriendReq.putExtra(INTENT_KEY_NOTIFICATION_ID, notificationId);

        intentFriendReq.addCategory("type accept friend request");

        return PendingIntent.getService(mNotificationContext, requestCode, intentFriendReq, PendingIntent.FLAG_ONE_SHOT);
    }


    private PendingIntent getPendingIntentDenyFriendRequest(String senderId, int requestCode, int notificationId) {

        Intent intentFriendReq = new Intent(mNotificationContext, SendNotificationActionData.class);

        intentFriendReq.putExtra(INTENT_KEY_NOTIFICATION_DATA, INTENT_KEY_NOTIFICATION_DATA_DENY_REQUEST);
        intentFriendReq.putExtra(INTENT_KEY_USER_ID, senderId);
        intentFriendReq.putExtra(INTENT_KEY_NOTIFICATION_ID, notificationId);

        intentFriendReq.addCategory("type deny friend request");

        return PendingIntent.getService(mNotificationContext, requestCode, intentFriendReq, PendingIntent.FLAG_ONE_SHOT);
    }

    private PendingIntent getPendingIntentMute(String senderId, String groupId, int requestCode, int notificationId) {

        Intent intentMute = new Intent(mNotificationContext, SendNotificationActionData.class);

        intentMute.putExtra(INTENT_KEY_NOTIFICATION_DATA, INTENT_KEY_NOTIFICATION_DATA_MUTE);

        intentMute.putExtra(groupId != null ? INTENT_KEY_GROUP_ID : INTENT_KEY_USER_ID, groupId != null ? groupId : senderId);

        intentMute.putExtra(INTENT_KEY_NOTIFICATION_ID, notificationId);

        intentMute.addCategory("type mute notification");

        return PendingIntent.getService(mNotificationContext, requestCode, intentMute, PendingIntent.FLAG_ONE_SHOT);
    }

}
