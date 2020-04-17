package com.example.tranquoctrungcntt.uchat.AppUtils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;

import com.example.tranquoctrungcntt.uchat.Activities.GroupDetailPage;
import com.example.tranquoctrungcntt.uchat.Activities.GroupMemberPage;
import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.GroupMember;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_JOIN_GROUP_REQUESTS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.GroupRole.ROLE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.GroupRole.ROLE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_CHANGE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_LEAVE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kAdminId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupMember;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kRole;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetailWithTransaction;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class GroupActionUtils {

    public static void deleteMember(Context context, String deletedMemberId, String groupIdToDelete) {

        if (isConnectedToFirebaseService(context)) {

            final AlertDialog alertDialog = getLoadingBuilder(context);

            alertDialog.show();

            getGroupDetailWithTransaction(groupIdToDelete, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                @Override
                public void OnCallBack(GroupDetail callbackGroupDetail) {

                    if (callbackGroupDetail != null) {

                        if (callbackGroupDetail.getMember() != null && callbackGroupDetail.getMember().get(deletedMemberId) != null) {

                            final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                            final int notificationId = getNotificationsId();

                            final Map<String, Boolean> relatedUserIdMap = new HashMap<>();

                            relatedUserIdMap.put(deletedMemberId, true);


                            final Map<String, Object> deletedMemberMap = new HashMap<>();

                            for (String memberId : callbackGroupDetail.getMember().keySet()) {

                                final Message message = buildInstantMessage(messageId, memberId, groupIdToDelete,
                                        "Đã xóa " + relatedUserIdMap.size() + " thành viên khỏi nhóm",
                                        MESSAGE_TYPE_UPDATE_MEMBER, notificationId, relatedUserIdMap);

                                deletedMemberMap.put("/" + CHILD_MESSAGES + "/" + memberId + "/" + groupIdToDelete + "/" + messageId, message);

                                if (memberId.equals(deletedMemberId))
                                    deletedMemberMap.put("/" + CHILD_GROUP_DETAIL + "/" + memberId + "/" + groupIdToDelete + "/" + kGroupMember, null);
                                else
                                    deletedMemberMap.put("/" + CHILD_GROUP_DETAIL + "/" + memberId + "/" + groupIdToDelete + "/" + kGroupMember + "/" + deletedMemberId, null);

                            }

                            ROOT_REF.updateChildren(deletedMemberMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendNotificationToUser(
                                            deletedMemberId,
                                            getMyFirebaseUserId(),
                                            groupIdToDelete,
                                            messageId,
                                            "Đã xóa bạn ra khỏi nhóm trò chuyện.",
                                            callbackGroupDetail.getGroupName(),
                                            callbackGroupDetail.getGroupThumbAvatar(),
                                            NOTIFICATION_TYPE_UPDATE_MEMBER,
                                            notificationId);

                                }
                            });

                        } else showMessageDialog(context, "Người dùng không phải thành viên nhóm !");

                    } else showMessageDialog(context, "Lỗi xử lý, vui lòng thử lại !");

                    alertDialog.dismiss();
                }
            });

        } else showNoConnectionDialog(context);

    }

    public static void changeGroupAdmin(Context context, String newAdminId, String groupToChangeId) {

        if (isConnectedToFirebaseService(context)) {

            final AlertDialog alertDialog = getLoadingBuilder(context);

            alertDialog.show();

            getGroupDetailWithTransaction(groupToChangeId, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                @Override
                public void OnCallBack(GroupDetail callbackGroupDetail) {

                    if (callbackGroupDetail != null) {

                        if (callbackGroupDetail.getMember() != null && callbackGroupDetail.getMember().get(newAdminId) != null) {

                            final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                            final int notificationId = getNotificationsId();

                            final Map<String, Boolean> relatedUserIdMap = new HashMap<>();

                            relatedUserIdMap.put(newAdminId, true);


                            final Map<String, Object> changeAdminMap = new HashMap<>();

                            for (String key : callbackGroupDetail.getMember().keySet()) {

                                final Message message = buildInstantMessage(messageId, key, groupToChangeId,
                                        "Nhóm đã được đổi quản trị viên",
                                        MESSAGE_TYPE_CHANGE_ADMIN, notificationId, relatedUserIdMap);

                                changeAdminMap.put("/" + CHILD_GROUP_DETAIL + "/" + key + "/" + groupToChangeId + "/" + kGroupMember + "/" + newAdminId + "/" + kRole, ROLE_ADMIN);
                                changeAdminMap.put("/" + CHILD_GROUP_DETAIL + "/" + key + "/" + groupToChangeId + "/" + kGroupMember + "/" + getMyFirebaseUserId() + "/" + kRole, ROLE_MEMBER);
                                changeAdminMap.put("/" + CHILD_GROUP_DETAIL + "/" + key + "/" + groupToChangeId + "/" + kAdminId, newAdminId);
                                changeAdminMap.put("/" + CHILD_MESSAGES + "/" + key + "/" + groupToChangeId + "/" + messageId, message);
                            }

                            ROOT_REF.updateChildren(changeAdminMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendNotificationToUser(
                                            newAdminId,
                                            getMyFirebaseUserId(),
                                            groupToChangeId,
                                            messageId,
                                            "Đã chọn bạn làm quản trị viên.",
                                            callbackGroupDetail.getGroupName(),
                                            callbackGroupDetail.getGroupThumbAvatar(),
                                            NOTIFICATION_TYPE_UPDATE_MEMBER,
                                            notificationId);

                                }
                            });

                        } else showMessageDialog(context, "Người dùng không phải thành viên nhóm !");

                    } else showMessageDialog(context, "Lỗi xử lý, vui lòng thử lại !");

                    alertDialog.dismiss();
                }
            });
        } else showNoConnectionDialog(context);

    }

    public static void startLeavingGroup(Context context, String groupIdToLeave) { // true = ngăn mời,

        if (isConnectedToFirebaseService(context)) {

            final AlertDialog alertDialog = getLoadingBuilder(context);

            alertDialog.show();

            getGroupDetailWithTransaction(groupIdToLeave, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                @Override
                public void OnCallBack(GroupDetail myGroupDetail) {

                    if (myGroupDetail != null) {

                        final String currentAdminId = myGroupDetail.getAdminId();

                        if (currentAdminId.equals(getMyFirebaseUserId())) {

                            if (myGroupDetail.getMember().size() == 1) {

                                destroyGroup(context, groupIdToLeave);

                            } else showStillBeAdminDialog(context, groupIdToLeave);

                        } else {

                            if (myGroupDetail.getMember() != null && myGroupDetail.getMember().get(getMyFirebaseUserId()) != null) {

                                leaveGroup(context, groupIdToLeave, myGroupDetail.getMember());

                            } else leaveGroup(context, groupIdToLeave, null);

                        }

                    } else showMessageDialog(context, "Lỗi xử lý, vui lòng thử lại !");

                    alertDialog.dismiss();

                }
            });

        } else showNoConnectionDialog(context);

    }

    public static void destroyGroup(Context context, String groupIdToLeave) {

        final Map<String, Object> leaveGroupMap = new HashMap<>();

        leaveGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);
        leaveGroupMap.put("/" + CHILD_MEDIA + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);
        leaveGroupMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);
        leaveGroupMap.put("/" + CHILD_SETTINGS + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);
        leaveGroupMap.put("/" + CHILD_JOIN_GROUP_REQUESTS + "/" + groupIdToLeave, null);

        ROOT_REF.updateChildren(leaveGroupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                final Activity currentActivity = (Activity) context;

                if (currentActivity instanceof GroupDetailPage) {
                    if (!currentActivity.isFinishing()) {
                        Intent it = new Intent(currentActivity, MainActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        currentActivity.startActivity(it);
                    }
                }
            }
        });

    }

    public static void leaveGroup(Context context, String groupIdToLeave, Map<String, GroupMember> currentMemberMap) {

        final Map<String, Object> leaveGroupMap = new HashMap<>();

        leaveGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);
        leaveGroupMap.put("/" + CHILD_MEDIA + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);
        leaveGroupMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);
        leaveGroupMap.put("/" + CHILD_SETTINGS + "/" + getMyFirebaseUserId() + "/" + groupIdToLeave, null);

        if (currentMemberMap != null && currentMemberMap.get(getMyFirebaseUserId()) != null) {

            final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

            final int notificationId = getNotificationsId();

            Map<String, Boolean> relatedUserIdMap = new HashMap<>();

            relatedUserIdMap.put(getMyFirebaseUserId(), true);

            for (String key : currentMemberMap.keySet()) {
                if (!key.equals(getMyFirebaseUserId())) {

                    final Message message = buildInstantMessage(messageId, key, groupIdToLeave,
                            "Đã rời khỏi nhóm trò chuyện",
                            MESSAGE_TYPE_LEAVE_GROUP, notificationId, relatedUserIdMap);

                    leaveGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + key + "/" + groupIdToLeave + "/" + kGroupMember + "/" + getMyFirebaseUserId(), null);
                    leaveGroupMap.put("/" + CHILD_MESSAGES + "/" + key + "/" + groupIdToLeave + "/" + messageId, message);
                }
            }
        }

        ROOT_REF.updateChildren(leaveGroupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                final Activity currentActivity = (Activity) context;

                if (currentActivity instanceof GroupDetailPage) {
                    if (!currentActivity.isFinishing()) {
                        Intent it = new Intent(currentActivity, MainActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        currentActivity.startActivity(it);
                    }
                }
            }
        });
    }

    public static void showChangeAdminConfirmDialog(Context context, String newAdminId, String groupIdToChange) {

        getSingleUserProfile(newAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                if (callbackUserProfile != null) {

                    new AlertDialog.Builder(context)
                            .setTitle("Thay đổi quản trị viên")
                            .setMessage(Html.fromHtml("Bạn có chắc chắn muốn chọn <b>" + callbackUserProfile.getName() + "</b> làm quản trị viên không ?"))
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    changeGroupAdmin(context, newAdminId, groupIdToChange);

                                }
                            }).setNegativeButton("Không", null).create().show();

                }

            }
        });

    }

    public static void showLeaveGroupConfirmDialog(Context context, String groupIdToLeave) {

        new AlertDialog.Builder(context)
                .setTitle("Rời nhóm")
                .setMessage("Bạn sẽ không thể nhận tin nhắn hay cập nhật về nhóm trò chuyện này nữa," +
                        " hành động này cũng sẽ xóa cuộc trò chuyện của bạn và nhóm." +
                        " Bạn có chắc chắn muốn rời khỏi nhóm không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startLeavingGroup(context, groupIdToLeave);

                    }
                }).setNegativeButton("Không", null).create().show();
    }

    public static void showStillBeAdminDialog(Context context, String groupId) {

        if (!((Activity) context).isFinishing()) {

            new AlertDialog.Builder(context)
                    .setTitle("Rời nhóm")
                    .setMessage("Bạn vẫn đang là quản trị viên của nhóm này, " +
                            "vui lòng chỉ định thành viên khác làm quản trị viên " +
                            "hoặc thử lại nếu nhóm chỉ còn một thành viên là bạn.")
                    .setPositiveButton("Chỉ định", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent it = new Intent(context, GroupMemberPage.class);
                            it.putExtra(INTENT_KEY_GROUP_ID, groupId);
                            context.startActivity(it);

                        }
                    }).setNegativeButton("Hủy", null).create().show();

        }
    }

    public static void showNotMemberDialog(Context context) {

        if (!((Activity) context).isFinishing()) {

            new AlertDialog.Builder(context)
                    .setMessage("Bạn không phải thành viên của nhóm trò chuyện này !")
                    .setPositiveButton("Đã hiểu", null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ((Activity) context).finish();
                        }
                    }).create().show();

        }
    }

}
