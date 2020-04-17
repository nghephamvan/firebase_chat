package com.example.tranquoctrungcntt.uchat.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.R;
import com.example.tranquoctrungcntt.uchat.Services.SendNotificationActionData;

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
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;


public class OreoNotification {

    private static final String CHANNEL_ID_ACTIVE = "com.example.tranquoctrungcntt.uchat.active";
    private static final String CHANNEL_NAME_ACTIVE = "UchatActive";

    private NotificationChannel channelActive;

    private Context mNotificationContext;

    public OreoNotification(Context context) {

        this.mNotificationContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        long[] pattern = {0, 300};


        if (channelActive == null) {

            channelActive = new NotificationChannel(CHANNEL_ID_ACTIVE, CHANNEL_NAME_ACTIVE, NotificationManager.IMPORTANCE_HIGH);
            channelActive.enableLights(false);
            channelActive.enableVibration(true);
            channelActive.setVibrationPattern(pattern);
            channelActive.setShowBadge(true);

            channelActive.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mNotificationContext.getPackageName() + "/" + R.raw.notification_sound);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            channelActive.setSound(sound, attributes); // This is IMPORTANT

            getNotificationManager(mNotificationContext).createNotificationChannel(channelActive);

        }


    }


    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getBaseBuilder(String title,
                                               String body,
                                               Bitmap avatar,
                                               PendingIntent mainContentIntent) {

        return new Notification.Builder(mNotificationContext, CHANNEL_ID_ACTIVE)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(avatar)
                .setContentIntent(mainContentIntent)
                .setSmallIcon(R.mipmap.app_logo)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());


    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder TypeAcceptFriendRequest(String title,
                                                        String content,
                                                        Bitmap avatar,
                                                        PendingIntent mainContentIntent) {

        Notification.Action actionMain = new Notification.Action.Builder(0, "Nhắn tin ngay", mainContentIntent).build();

        return getBaseBuilder(title, content, avatar, mainContentIntent).addAction(actionMain);
    }


    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder TypeNewFriendRequest(String title,
                                                     String content,
                                                     Bitmap avatar,
                                                     PendingIntent acceptPendingIntent,
                                                     PendingIntent denyPendingIntent,
                                                     PendingIntent mainContentIntent) {

        Notification.Action actionAcceptFriendRequest = new Notification.Action.Builder(0, "Đồng ý", acceptPendingIntent).build();

        Notification.Action actionDenyFriendRequest = new Notification.Action.Builder(0, "Từ chối", denyPendingIntent).build();

        Notification.Action actionMain = new Notification.Action.Builder(0, "Xem tất cả", mainContentIntent).build();

        return getBaseBuilder(title, content, avatar, mainContentIntent)
                .addAction(actionAcceptFriendRequest)
                .addAction(actionDenyFriendRequest)
                .addAction(actionMain);


    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder TypeCall(String title,
                                         String content,
                                         Bitmap avatar,
                                         PendingIntent likeIntent,
                                         PendingIntent mainContentIntent,
                                         PendingIntent muteIntent,
                                         int notificationType) {

        Notification.Action actionLike = new Notification.Action.Builder(0, "Thích", likeIntent).build();

        Notification.Action actionMain = new Notification.Action.Builder(0, "Xem tin nhắn", mainContentIntent).build();

        Notification.Action actionMute = new Notification.Action.Builder(0, "Tắt thông báo", muteIntent).build();


        Notification.Builder mBuilder = getBaseBuilder(title, content, avatar, mainContentIntent)
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

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder TypeGroupMessage(String title,
                                                 String content,
                                                 Bitmap avatar,
                                                 PendingIntent likeIntent,
                                                 PendingIntent mainContentIntent,
                                                 PendingIntent muteIntent) {

        Notification.Action actionLike = new Notification.Action.Builder(0, "Thích", likeIntent).build();

        Notification.Action actionMain = new Notification.Action.Builder(0, "Xem tin nhắn", mainContentIntent).build();

        Notification.Action actionMute = new Notification.Action.Builder(0, "Tắt thông báo", muteIntent).build();

        Spannable sbName = new SpannableString(content);

        sbName.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, content.indexOf(":") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return new Notification.Builder(mNotificationContext, CHANNEL_ID_ACTIVE)
                .setContentTitle(title)
                .setContentText(sbName)
                .setLargeIcon(avatar)
                .setContentIntent(mainContentIntent)
                .setSmallIcon(R.mipmap.app_logo)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .addAction(actionLike)
                .addAction(actionMain)
                .addAction(actionMute);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder TypeMessage(String title,
                                            String content,
                                            Bitmap avatar,
                                            PendingIntent likeIntent,
                                            PendingIntent mainContentIntent,
                                            PendingIntent muteIntent) {

        Notification.Action actionLike = new Notification.Action.Builder(0, "Thích", likeIntent).build();

        Notification.Action actionMain = new Notification.Action.Builder(0, "Xem tin nhắn", mainContentIntent).build();

        Notification.Action actionMute = new Notification.Action.Builder(0, "Tắt thông báo", muteIntent).build();

        return getBaseBuilder(title, content, avatar, mainContentIntent)
                .addAction(actionLike)
                .addAction(actionMain)
                .addAction(actionMute);

    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder generateNotificationOreo(String notificationSenderId,
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

        if (groupId != null) intentLike.putExtra(INTENT_KEY_GROUP_ID, groupId);
        else intentLike.putExtra(INTENT_KEY_USER_ID, userId);

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
        if (groupId != null) intentMute.putExtra(INTENT_KEY_GROUP_ID, groupId);
        else intentMute.putExtra(INTENT_KEY_USER_ID, senderId);

        intentMute.putExtra(INTENT_KEY_NOTIFICATION_ID, notificationId);

        intentMute.addCategory("type mute notification");

        return PendingIntent.getService(mNotificationContext, requestCode, intentMute, PendingIntent.FLAG_ONE_SHOT);
    }

}
