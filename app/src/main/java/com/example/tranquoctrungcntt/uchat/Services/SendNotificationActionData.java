package com.example.tranquoctrungcntt.uchat.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_BLACKLIST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_RECEIVER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_SENDER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_ACCEPT_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_DENY_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_LIKE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_DATA_MUTE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_NOTIFICATION_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.LIKE_ICON;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_LIKE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_GROUP_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_SINGLE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetailWithTransaction;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkHasChildBlock;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.preventNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.markMessageIsSent;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;

public class SendNotificationActionData extends Service {


    private void showToast(String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();

    }

    private void denyFriendRequest(String userToDeny) {

        if (isConnectedToFirebaseService(getApplicationContext())) {

            ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId())
                    .child(userToDeny).child(CHILD_REQUEST_RECEIVER)
                    .runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                            return Transaction.success(mutableData);

                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null && dataSnapshot.getValue(Boolean.class)) {

                                Map<String, Object> mapDeny = new HashMap<>();

                                mapDeny.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToDeny + "/" + CHILD_REQUEST_RECEIVER, null);
                                mapDeny.put("/" + CHILD_RELATIONSHIPS + "/" + userToDeny + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_SENDER, null);
                                mapDeny.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToDeny + "/" + CHILD_BLACKLIST, true);

                                ROOT_REF.updateChildren(mapDeny).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        showToast("Từ chối yêu cầu thành công !");
                                    }
                                });
                            }

                        }
                    });


        } else showToast("Không thể kết nối đến máy chủ !");

    }

    private void acceptFriendRequest(String userToAccept) {

        if (isConnectedToFirebaseService(getApplicationContext())) {

            ROOT_REF.child(CHILD_RELATIONSHIPS)
                    .child(getMyFirebaseUserId()).child(userToAccept)
                    .child(CHILD_REQUEST_RECEIVER)
                    .runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null && dataSnapshot.getValue(Boolean.class)) {

                                Map<String, Object> mapFriend = new HashMap<>();

                                mapFriend.put("/" + CHILD_RELATIONSHIPS + "/" + userToAccept + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_SENDER, null);
                                mapFriend.put("/" + CHILD_RELATIONSHIPS + "/" + userToAccept + "/" + getMyFirebaseUserId() + "/" + CHILD_BLACKLIST, null);
                                mapFriend.put("/" + CHILD_RELATIONSHIPS + "/" + userToAccept + "/" + getMyFirebaseUserId() + "/" + CHILD_FRIEND, true);

                                mapFriend.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToAccept + "/" + CHILD_REQUEST_RECEIVER, null);
                                mapFriend.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToAccept + "/" + CHILD_BLACKLIST, null);
                                mapFriend.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToAccept + "/" + CHILD_FRIEND, true);

                                ROOT_REF.updateChildren(mapFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                            @Override
                                            public void OnCallBack(User callbackUserProfile) {

                                                sendNotificationToUser(
                                                        userToAccept,
                                                        getMyFirebaseUserId(),
                                                        null,
                                                        null,
                                                        "Lời mời kết bạn đã được chấp nhận.",
                                                        callbackUserProfile.getName(),
                                                        callbackUserProfile.getThumbAvatarUrl(),
                                                        NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST,
                                                        getNotificationsId());

                                            }
                                        });


                                        showToast("Đồng ý kết bạn thành công !");

                                    }
                                });

                            }
                        }
                    });

        } else showToast("Không thể kết nối đến máy chủ !");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getExtras().getString(INTENT_KEY_NOTIFICATION_DATA)) {

            case INTENT_KEY_NOTIFICATION_DATA_LIKE:

                String userId = intent.getExtras().getString(INTENT_KEY_USER_ID);
                String groupId = intent.getExtras().getString(INTENT_KEY_GROUP_ID);

                //send like icon

                if (userId != null) {

                    sendLikeMessage(userId);

                } else if (groupId != null) {

                    sendGroupLikeMessage(groupId);
                }

                break;

            case INTENT_KEY_NOTIFICATION_DATA_ACCEPT_REQUEST:

                acceptFriendRequest(intent.getExtras().getString(INTENT_KEY_USER_ID));

                break;

            case INTENT_KEY_NOTIFICATION_DATA_DENY_REQUEST:

                denyFriendRequest(intent.getExtras().getString(INTENT_KEY_USER_ID));

                break;

            case INTENT_KEY_NOTIFICATION_DATA_MUTE:

                String mutedUserId = intent.getExtras().getString(INTENT_KEY_USER_ID);
                String mutedGroupId = intent.getExtras().getString(INTENT_KEY_GROUP_ID);

                preventNotifyMe(this, mutedGroupId != null ? mutedGroupId : mutedUserId);

                break;
        }

        int notificationId = Integer.parseInt(intent.getExtras().get(INTENT_KEY_NOTIFICATION_ID) + "");

        getNotificationManager(getApplicationContext()).cancel(notificationId);

        stopSelf();

        return START_NOT_STICKY;
    }

    private void sendLikeMessage(String receiverId) {

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), receiverId, null,
                LIKE_ICON, MESSAGE_STATUS_SENDING,
                sendTime, 0, MESSAGE_TYPE_LIKE,
                notificationId, 0, null, false, null, null, null);

        final Message userMess = new Message(
                messageId, getMyFirebaseUserId(), receiverId, null,
                LIKE_ICON, MESSAGE_STATUS_SENT,
                sendTime, 0, MESSAGE_TYPE_LIKE,
                notificationId, 0, null, false, null, null, null);

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(receiverId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        checkHasChildBlock(receiverId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                            @Override
                            public void OnCallBack(boolean hasChildBlock) {

                                if (hasChildBlock) {

                                    showToast("Lỗi trong khi gửi tin nhắn");

                                } else {

                                    markMessageIsSent(messageId, receiverId, null);

                                    ROOT_REF.child(CHILD_MESSAGES)
                                            .child(receiverId).child(getMyFirebaseUserId())
                                            .child(messageId).setValue(userMess)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                        @Override
                                                        public void OnCallBack(User callbackUserProfile) {

                                                            sendNotificationToUser(
                                                                    receiverId,
                                                                    getMyFirebaseUserId(),
                                                                    null,
                                                                    messageId,
                                                                    LIKE_ICON,
                                                                    callbackUserProfile.getName(),
                                                                    callbackUserProfile.getThumbAvatarUrl(),
                                                                    NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                                    notificationId);
                                                        }
                                                    });

                                                    showToast("Đã gửi !");
                                                }
                                            });


                                }
                            }
                        });
                    }

                });


    }

    private void sendGroupLikeMessage(String groupId) {

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), getMyFirebaseUserId(), groupId,
                LIKE_ICON, MESSAGE_STATUS_SENDING,
                sendTime, 0, MESSAGE_TYPE_LIKE,
                notificationId, 0, null, false, null, null, null);

        getGroupDetailWithTransaction(groupId, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
            @Override
            public void OnCallBack(GroupDetail callbackGroupDetail) {

                if (callbackGroupDetail != null && callbackGroupDetail.getMember() != null && callbackGroupDetail.getMember().get(getMyFirebaseUserId()) != null) {

                    ROOT_REF.child(CHILD_MESSAGES)
                            .child(getMyFirebaseUserId()).child(groupId)
                            .child(messageId).setValue(myMess)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    markMessageIsSent(messageId, null, groupId);

                                    final Map<String, Object> mapMessageToSend = new HashMap<>();

                                    for (String memberId : callbackGroupDetail.getMember().keySet()) {

                                        if (!memberId.equals(getMyFirebaseUserId())) {

                                            final Message memberMess = new Message(
                                                    messageId, getMyFirebaseUserId(), memberId, groupId,
                                                    LIKE_ICON, MESSAGE_STATUS_SENT,
                                                    sendTime, 0, MESSAGE_TYPE_LIKE,
                                                    notificationId, 0, null, false, null, null, null);

                                            mapMessageToSend.put("/" + CHILD_MESSAGES + "/" + memberId + "/" + groupId + "/" + messageId, memberMess);
                                        }

                                    }

                                    ROOT_REF.updateChildren(mapMessageToSend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            for (String memberId : callbackGroupDetail.getMember().keySet()) {

                                                if (!memberId.equals(getMyFirebaseUserId())) {

                                                    sendNotificationToUser(
                                                            memberId,
                                                            getMyFirebaseUserId(),
                                                            groupId,
                                                            messageId,
                                                            LIKE_ICON,
                                                            callbackGroupDetail.getGroupName(),
                                                            callbackGroupDetail.getGroupThumbAvatar(),
                                                            NOTIFICATION_TYPE_GROUP_MESSAGE,
                                                            notificationId);

                                                }

                                            }

                                            showToast("Đã gửi !");
                                        }
                                    });

                                }
                            });


                } else showToast("Lỗi gửi tin nhắn");
            }
        });


    }
}
