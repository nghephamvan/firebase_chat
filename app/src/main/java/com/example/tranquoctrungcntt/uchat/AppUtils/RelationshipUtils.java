package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_BLACKLIST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_BLOCK_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_CANCEL_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_RECEIVER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_SENDER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEND_FRIEND_REQUEST_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_UNBLOCK_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TIME_TO_WAIT_FOR_NEXT_BLOCK_HOUR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TIME_TO_WAIT_FOR_NEXT_BLOCK_MILIES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_HOUR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class RelationshipUtils {

    public static void checkBeforeBLockUser(Context context, String userId) {

        if (isConnectedToFirebaseService(context)) {

            ROOT_REF.child(CHILD_BLOCK_TIMER)
                    .child(getMyFirebaseUserId()).child(userId)
                    .child(CHILD_UNBLOCK_TIME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            long timeToWait = -1;

                            if (dataSnapshot.exists()) {
                                long timeValue = dataSnapshot.getValue(Long.class);
                                long timePassed = getCurrentTimeInMilies() - timeValue;
                                if (timePassed <= TIME_TO_WAIT_FOR_NEXT_BLOCK_MILIES) {
                                    timeToWait = TIME_TO_WAIT_FOR_NEXT_BLOCK_MILIES - timePassed;
                                }
                            }

                            if (timeToWait == -1) {

                                showLockUserConfirmDialog(context, userId);

                            } else {

                                long timeToWaitHour = TimeUnit.MILLISECONDS.toHours(timeToWait);
                                long timeToWaitMinute = TimeUnit.MILLISECONDS.toMinutes(timeToWait);

                                String content = timeToWaitHour < 1 ?
                                        "Bạn phải chờ " + timeToWaitMinute + " phút nữa để có thể chặn lại người này !"
                                        : "Bạn phải chờ " + timeToWaitHour + " giờ nữa để có thể chặn lại người này !";

                                showMessageDialog(context, content);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        } else showNoConnectionDialog(context);

    }

    public static void showLockUserConfirmDialog(Context context, String userId) {

        new AlertDialog.Builder(context)
                .setTitle("Chặn người dùng")
                .setMessage("Bạn và người dùng này sẽ:"
                        + "\n⦁ Không thể tìm thấy hoặc liên hệ với nhau."
                        + "\n⦁ Chỉ thấy được nhau khi tham gia cùng nhóm trò chuyện, trừ khi bạn rời khỏi nhóm đó."
                        + "\n⦁ Có thể xem các tin nhắn của nhau trong các nhóm trò chuyện chung."
                        + "\n⦁ Không thể xem trang cá nhân của nhau."
                        + "\n⦁ Hủy bạn bè nếu hai bạn đang là bạn của nhau."
                        + "\n\n\nBẠN CÓ CHẮC CHẮN MUỐN CHẶN NGƯỜI DÙNG NÀY KHÔNG ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        lockUser(context, userId);

                    }
                }).setNegativeButton("Không", null).create().show();


    }

    public static void lockUser(Context context, String userId) {

        if (isConnectedToFirebaseService(context)) {

            checkHasChildBlock(userId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                @Override
                public void OnCallBack(boolean hasChildBlock) {

                    if (hasChildBlock) {

                        showMessageDialog(context, "Bạn tạm thời không thể chặn người này !");

                    } else {

                        final Map<String, Object> mapLockUser = new HashMap<>();

                        mapLockUser.put("/" + CHILD_BLOCK_TIMER + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_UNBLOCK_TIME, null);

                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_FRIEND, null);
                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_BLACKLIST, null);
                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_REQUEST_SENDER, null);
                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_REQUEST_RECEIVER, null);
                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_I_BLOCKED_USER, true);
                        // remove all relations ship with this user

                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_FRIEND, null);
                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_SENDER, null);
                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_RECEIVER, null);
                        mapLockUser.put("/" + CHILD_RELATIONSHIPS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_USER_BLOCKED_ME, true);

                        ROOT_REF.updateChildren(mapLockUser);


                    }
                }
            });

        } else showNoConnectionDialog(context);

    }


    // unlock

    public static void showUnlockUserConfirmDialog(Context context, String userId) {

        new AlertDialog.Builder(context)
                .setTitle("Bỏ chặn người dùng")
                .setMessage("Bạn sẽ phải chờ " + TIME_TO_WAIT_FOR_NEXT_BLOCK_HOUR + "h để có thể chặn lại người này. Bạn có chắc chắn muốn bỏ chặn người này không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        unlockUser(context, userId);

                    }
                }).setNegativeButton("Không", null).create().show();

    }

    public static void unlockUser(Context context, String userId) {

        if (isConnectedToFirebaseService(context)) {

            final Map<String, Object> mapUnLock = new HashMap<>();

            mapUnLock.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_I_BLOCKED_USER, null);
            mapUnLock.put("/" + CHILD_RELATIONSHIPS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_USER_BLOCKED_ME, null);
            mapUnLock.put("/" + CHILD_BLOCK_TIMER + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_UNBLOCK_TIME, getCurrentTimeInMilies());

            ROOT_REF.updateChildren(mapUnLock);

        } else showNoConnectionDialog(context);

    }

    // cancel friend request

    public static void showCancelRequestConfirmDialog(Context context, String userId) {

        new AlertDialog.Builder(context)
                .setTitle("Hủy lời mời")
                .setMessage("Bạn sẽ phải chờ " + TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_HOUR + "h để có thể gửi lại lời mời kết bạn." +
                        " Bạn có chắc chắn muốn hủy không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        cancelFriendRequest(context, userId);

                    }
                }).setNegativeButton("Không", null).create().show();

    }

    public static void cancelFriendRequest(Context context, String userToCancel) {

        if (isConnectedToFirebaseService(context)) {

            final Map<String, Object> mapCancel = new HashMap<>();

            mapCancel.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToCancel + "/" + CHILD_REQUEST_SENDER, null);
            mapCancel.put("/" + CHILD_RELATIONSHIPS + "/" + userToCancel + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_RECEIVER, null);
            mapCancel.put("/" + CHILD_SEND_FRIEND_REQUEST_TIMER + "/" + getMyFirebaseUserId() + "/" + userToCancel + "/" + CHILD_CANCEL_TIME, getCurrentTimeInMilies());

            ROOT_REF.updateChildren(mapCancel);

        } else showNoConnectionDialog(context);
    }


    //deny friend request

    public static void denyFriendRequest(Context context, String userToDeny) {

        if (isConnectedToFirebaseService(context)) {

            final Map<String, Object> mapDeny = new HashMap<>();

            mapDeny.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToDeny + "/" + CHILD_REQUEST_RECEIVER, null);
            mapDeny.put("/" + CHILD_RELATIONSHIPS + "/" + userToDeny + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_SENDER, null);
            mapDeny.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userToDeny + "/" + CHILD_BLACKLIST, true);

            ROOT_REF.updateChildren(mapDeny);

        } else showNoConnectionDialog(context);
    }

    public static void acceptFriendRequest(Context context, String userToAccept) {

        if (isConnectedToFirebaseService(context)) {

            final Map<String, Object> mapFriend = new HashMap<>();

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


                }
            });
        } else showNoConnectionDialog(context);

    }


    //unfriend

    public static void showUnfriendConfirmDialog(Context context, String userId) {

        new AlertDialog.Builder(context)
                .setTitle("Hủy kết bạn")
                .setMessage("Bạn có chắc chắn muốn hủy bạn bè với người này không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        unfriend(context, userId);

                    }
                }).setNegativeButton("Không", null).create().show();

    }

    public static void unfriend(Context context, String userId) {

        if (isConnectedToFirebaseService(context)) {

            Map<String, Object> mapUnFriend = new HashMap<>();

            mapUnFriend.put("/" + CHILD_RELATIONSHIPS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_FRIEND, null);
            mapUnFriend.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_FRIEND, null);

            ROOT_REF.updateChildren(mapUnFriend);

        } else showNoConnectionDialog(context);


    }

    //checker


    public static void checkHasChildBlock(String userId, AppConstants.AppInterfaces.FirebaseBooleanCallBack booleanCallBack) {

        ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final boolean hasChildBlock = dataSnapshot.hasChild(CHILD_I_BLOCKED_USER) || dataSnapshot.hasChild(CHILD_USER_BLOCKED_ME);

                        booleanCallBack.OnCallBack(hasChildBlock);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
