package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.emoji.widget.EmojiAppCompatEditText;
import androidx.emoji.widget.EmojiAppCompatTextView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.BuildConfig;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MUTE_NOTIFICATIONS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_FULL_SIZE_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_GROUP_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_THUMB_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_UPDATE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.MAX_PROCESSING_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.CAPTURE_IMAGE_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_ACCESS_GALLERY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_CAPTURE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PICK_IMAGES_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_STORAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_FULL_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_THUMB_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kCensorMode;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupAvatar;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupDescription;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupThumbAvatar;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isGroupInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isValidSingleName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildUriMapAfterUpload;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.generateMediaName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupMemberSnapshot;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getThumbnail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.showLeaveGroupConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isCaptureMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isPickMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showCaptureImageRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showPickImagesRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.allowNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.preventNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewAvatar;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setMediaUrlToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;

public class GroupDetailPage extends BaseActivity {

    private Toolbar mToolbar;

    private CircleImageView civ_avatar;
    private CircleImageView civ_new_avatar;

    private TextView tv_group_name;
    private TextView tv_created_date;
    private TextView tv_censor_mode;

    private EmojiAppCompatTextView tv_group_description;

    private LinearLayout btn_view_members;
    private LinearLayout btn_shared_media;
    private LinearLayout btn_allow_notifications;
    private LinearLayout btn_censor_mode;
    private LinearLayout btn_leave_group;
    private LinearLayout btn_update_group;
    private LinearLayout btn_delete_instant_messages;
    private LinearLayout btn_censor_member;
    private LinearLayout btn_search_message;

    private SwitchCompat switch_notifications;
    private SwitchCompat switch_censor_mode;

    private UploadTask mPictureUploadTask;
    private UploadTask mThumbPictureUploadTask;

    private ValueEventListener mSettingValueEvent;
    private ValueEventListener mDetailValueEvent;

    private DatabaseReference mGroupDetailRef;
    private DatabaseReference mSettingRef;

    private boolean isMute;

    private String mPicturePath;
    private String mGroupId;

    private GroupDetail mGroupDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        mGroupId = (String) getDataFromIntent(GroupDetailPage.this, INTENT_KEY_GROUP_ID);

        mGroupDetail = null;

        if (mGroupId != null) {

            initViews();

            initClickEvents();

        } else finish();


    }


    private void switchNotifications(boolean value) {

        switch_notifications.setChecked(value);
        switch_notifications.getThumbDrawable().setColorFilter(value ? Color.parseColor("#ffffff") : Color.parseColor("#f2f2f2"), PorterDuff.Mode.MULTIPLY);
        switch_notifications.getTrackDrawable().setColorFilter(value ? Color.parseColor("#64C915") : Color.parseColor("#b7b7b7"), PorterDuff.Mode.MULTIPLY);

    }

    private void switchCensorMode(boolean value) {

        switch_censor_mode.setChecked(value);
        switch_censor_mode.getThumbDrawable().setColorFilter(value ? Color.parseColor("#ffffff") : Color.parseColor("#f2f2f2"), PorterDuff.Mode.MULTIPLY);
        switch_censor_mode.getTrackDrawable().setColorFilter(value ? Color.parseColor("#64C915") : Color.parseColor("#b7b7b7"), PorterDuff.Mode.MULTIPLY);

    }

    private void initViews() {

        mPicturePath = null;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        civ_avatar = (CircleImageView) findViewById(R.id.civ_group_avatar);
        tv_group_name = (TextView) findViewById(R.id.tv_group_name);
        tv_group_description = (EmojiAppCompatTextView) findViewById(R.id.tv_group_description);
        tv_created_date = (TextView) findViewById(R.id.tv_created_date);
        tv_censor_mode = (TextView) findViewById(R.id.tv_censor_mode);

        switch_notifications = (SwitchCompat) findViewById(R.id.switch_notifications);
        switch_censor_mode = (SwitchCompat) findViewById(R.id.switch_censor_mode);

        btn_allow_notifications = (LinearLayout) findViewById(R.id.linear_unmute_notifications);
        btn_censor_mode = (LinearLayout) findViewById(R.id.linear_censor_mode);
        btn_view_members = (LinearLayout) findViewById(R.id.linear_member_list);
        btn_shared_media = (LinearLayout) findViewById(R.id.linear_shared_media);
        btn_leave_group = (LinearLayout) findViewById(R.id.linear_leave_group);
        btn_update_group = (LinearLayout) findViewById(R.id.linear_update_group);
        btn_delete_instant_messages = (LinearLayout) findViewById(R.id.linear_delete_instant_messages);
        btn_censor_member = (LinearLayout) findViewById(R.id.linear_censor_member);
        btn_search_message = (LinearLayout) findViewById(R.id.linear_search_message);


    }

    private void initClickEvents() {

        btn_update_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGroupDetail != null && mGroupDetail.getAdminId().equals(getMyFirebaseUserId())) {

                    showUpdateGroupDialog();

                } else showMessageDialog(GroupDetailPage.this, "Bạn không phải quản trị viên.");

            }
        });

        btn_leave_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLeaveGroupConfirmDialog(GroupDetailPage.this, mGroupId);

            }
        });

        civ_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGroupDetail != null) viewAvatar(GroupDetailPage.this, mGroupDetail.getGroupAvatar());

            }
        });

        btn_view_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGroupDetail != null && mGroupDetail.getMember() != null && mGroupDetail.getMember().get(getMyFirebaseUserId()) != null) {

                    Intent it = new Intent(GroupDetailPage.this, GroupMemberPage.class);
                    it.putExtra(INTENT_KEY_GROUP_ID, mGroupId);
                    startActivity(it);

                } else showMessageDialog(GroupDetailPage.this, "Bạn không phải thành viên của nhóm !");

            }
        });

        btn_shared_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(GroupDetailPage.this, SharedMedia.class);
                it.putExtra(INTENT_KEY_GROUP_ID, mGroupId);
                startActivity(it);

            }
        });

        btn_allow_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_allow_notifications.setClickable(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_allow_notifications.setClickable(true);
                    }
                }, 2000);

                if (isMute) allowNotifyMe(GroupDetailPage.this, mGroupId);

                else preventNotifyMe(GroupDetailPage.this, mGroupId);
            }
        });

        btn_censor_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGroupDetail != null && mGroupDetail.getAdminId().equals(getMyFirebaseUserId())) {

                    if (isConnectedToFirebaseService(GroupDetailPage.this)) {

                        btn_censor_mode.setClickable(false);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btn_censor_mode.setClickable(true);
                            }
                        }, 5000);

                        final boolean valueToSet = !mGroupDetail.isCensorMode();

                        final Map<String, Object> mapCensorMode = new HashMap<>();

                        for (String key : mGroupDetail.getMember().keySet()) {
                            mapCensorMode.put("/" + CHILD_GROUP_DETAIL + "/" + key + "/" + mGroupId + "/" + kCensorMode, valueToSet);
                        }

                        ROOT_REF.updateChildren(mapCensorMode);

                    } else showNoConnectionDialog(GroupDetailPage.this);

                } else showMessageDialog(GroupDetailPage.this, "Bạn không phải quản trị viên.");


            }
        });

        btn_delete_instant_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteIntanstMessagesConfirmDialog();
            }
        });

        btn_censor_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGroupDetail != null && mGroupDetail.getAdminId().equals(getMyFirebaseUserId())) {

                    Intent it_censor = new Intent(GroupDetailPage.this, CensorMember.class);
                    it_censor.putExtra(INTENT_KEY_GROUP_ID, mGroupId);
                    startActivity(it_censor);

                } else showMessageDialog(GroupDetailPage.this, "Bạn không phải quản trị viên.");

            }
        });

        btn_search_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GroupDetailPage.this, SearchMessage.class);
                it.putExtra(INTENT_KEY_GROUP_ID, mGroupId);
                startActivity(it);
            }
        });
    }

    private void showDeleteIntanstMessagesConfirmDialog() {

        new AlertDialog.Builder(GroupDetailPage.this)
                .setTitle("Xóa tin nhắn")
                .setMessage("Thao tác này cũng sẽ xóa cuộc trò chuyện nếu như cuộc trò chuyện chỉ còn lại các tin nhắn cập nhật nhóm." +
                        " Bạn có chắc chắn muốn xóa tất cả các tin nhắn cập nhật nhóm hiện đang có trong cuộc trò chuyện này không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        if (isConnectedToFirebaseService(GroupDetailPage.this)) {

                            deleteInstantMessage();

                        } else showNoConnectionDialog(GroupDetailPage.this);

                    }
                }).setNegativeButton("Không", null).create().show();

    }

    private void deleteInstantMessage() {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final Map<String, Object> map = new HashMap<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Message message = snapshot.getValue(Message.class);

                            if (isGroupInstantMessage(message)) {
                                map.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + mGroupId + "/" + message.getMessageId(), null);
                            }
                        }

                        if (map.size() > 0) {
                            ROOT_REF.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showLongToast(GroupDetailPage.this, "Đã xóa các tin nhắn cập nhật nhóm !");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showUpdateGroupDialog() {

        final ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Đổi tên nhóm");
        arrayList.add("Cập nhật mô tả");
        arrayList.add("Đổi ảnh đại diện");
        arrayList.add("Huỷ");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter(GroupDetailPage.this, android.R.layout.simple_list_item_1, arrayList);

        new AlertDialog.Builder(GroupDetailPage.this)
                .setTitle("Tuỳ chọn")
                .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                showRenamedDialog();
                                break;
                            case 1:
                                showUpdateDescriptionDialog();
                                break;
                            case 2:
                                showUpdateAvatarDialog();
                                break;
                            default:
                        }

                    }
                }).create().show();

    }

    private void showRenamedDialog() {

        View view = LayoutInflater.from(GroupDetailPage.this).inflate(R.layout.dialog_renamed_group, null);

        EditText edt_updated_name = (EditText) view.findViewById(R.id.edt_updated_name);

        edt_updated_name.setText(mGroupDetail.getGroupName());
        edt_updated_name.requestFocus();

        new AlertDialog.Builder(GroupDetailPage.this)
                .setTitle("Đổi tên nhóm")
                .setView(view)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isConnectedToFirebaseService(GroupDetailPage.this)) {

                            final String updatedName = edt_updated_name.getText().toString().trim();

                            if (isValidSingleName(updatedName)) {

                                if (!updatedName.isEmpty() && !updatedName.equals(mGroupDetail.getGroupName())) {

                                    final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                                    final int notificationId = getNotificationsId();


                                    final Map<String, Object> updateGroupMap = new HashMap<>();

                                    for (String key : mGroupDetail.getMember().keySet()) {

                                        final Message message = buildInstantMessage(messageId, key, mGroupId,
                                                "Đã đổi tên nhóm thành: " + updatedName,
                                                MESSAGE_TYPE_UPDATE_GROUP, notificationId, null);

                                        updateGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + key + "/" + mGroupId + "/" + kGroupName, updatedName);
                                        updateGroupMap.put("/" + CHILD_MESSAGES + "/" + key + "/" + mGroupId + "/" + messageId, message);
                                    }

                                    ROOT_REF.updateChildren(updateGroupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            for (String key : mGroupDetail.getMember().keySet()) {

                                                if (!key.equals(getMyFirebaseUserId())) {

                                                    sendNotificationToUser(
                                                            key,
                                                            getMyFirebaseUserId(),
                                                            mGroupId,
                                                            messageId,
                                                            "Đã đổi tên nhóm thành: " + updatedName + ".",
                                                            mGroupDetail.getGroupName(),
                                                            mGroupDetail.getGroupThumbAvatar(),
                                                            NOTIFICATION_TYPE_UPDATE_GROUP,
                                                            notificationId);

                                                }
                                            }
                                        }
                                    });

                                }

                            } else showMessageDialog(GroupDetailPage.this, "Tên nhóm không hợp lệ");

                        } else showNoConnectionDialog(GroupDetailPage.this);
                    }
                }).setNegativeButton("Huỷ", null).create().show();

    }

    private void showUpdateDescriptionDialog() {

        View view = LayoutInflater.from(GroupDetailPage.this).inflate(R.layout.dialog_update_description, null);

        EmojiAppCompatEditText edt_description = (EmojiAppCompatEditText) view.findViewById(R.id.edt_description);
        edt_description.requestFocus();
        edt_description.setText(mGroupDetail.getGroupDescription());

        new AlertDialog.Builder(GroupDetailPage.this)
                .setTitle("Cập nhật mô tả")
                .setView(view)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isConnectedToFirebaseService(GroupDetailPage.this)) {

                            final String updatedDescription = edt_description.getText().toString().trim();

                            if (!updatedDescription.isEmpty() && !updatedDescription.equals(mGroupDetail.getGroupDescription())) {

                                final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                                final int notificationId = getNotificationsId();

                                final Map<String, Object> updateGroupMap = new HashMap<>();

                                for (String key : mGroupDetail.getMember().keySet()) {

                                    final Message message = buildInstantMessage(messageId, key, mGroupId,
                                            "Đã cập nhật mô tả nhóm",
                                            MESSAGE_TYPE_UPDATE_GROUP, notificationId, null);

                                    updateGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + key + "/" + mGroupId + "/" + kGroupDescription, updatedDescription);
                                    updateGroupMap.put("/" + CHILD_MESSAGES + "/" + key + "/" + mGroupId + "/" + messageId, message);
                                }

                                ROOT_REF.updateChildren(updateGroupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        for (String key : mGroupDetail.getMember().keySet()) {

                                            if (!key.equals(getMyFirebaseUserId())) {

                                                sendNotificationToUser(
                                                        key,
                                                        getMyFirebaseUserId(),
                                                        mGroupId,
                                                        messageId,
                                                        "Đã cập nhật mô tả cho nhóm trò chuyện.",
                                                        mGroupDetail.getGroupName(),
                                                        mGroupDetail.getGroupThumbAvatar(),
                                                        NOTIFICATION_TYPE_UPDATE_GROUP,
                                                        notificationId);

                                            }

                                        }
                                    }
                                });
                            }

                        } else showNoConnectionDialog(GroupDetailPage.this);
                    }
                }).setNegativeButton("Huỷ", null).create().show();


    }

    private void showUpdateAvatarDialog() {

        final View view = LayoutInflater.from(GroupDetailPage.this).inflate(R.layout.dialog_update_avatar, null);

        civ_new_avatar = (CircleImageView) view.findViewById(R.id.civ_group_new_avatar);
        civ_new_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarOptions();
            }
        });

        setAvatarToView(GroupDetailPage.this, mGroupDetail.getGroupThumbAvatar(), mGroupDetail.getGroupName(), civ_new_avatar);

        final AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetailPage.this)
                .setTitle("Đổi ảnh đại diện").setView(view)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (mPicturePath != null) {

                            if (isConnectedToFirebaseService(GroupDetailPage.this)) {

                                updateGroupAvatar();

                            } else showNoConnectionDialog(GroupDetailPage.this);

                        }
                    }
                })
                .setNegativeButton("Huỷ", null);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                mPicturePath = null;

            }
        });

        alertDialog.show();
    }

    private void uploadPicture(String imageName, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBack) {


        final StorageReference imageRef = ROOT_STORAGE.child(STORAGE_GROUP_MEDIA)
                .child(STORAGE_FULL_SIZE_AVATAR)
                .child(mGroupId).child(imageName);

        final StorageReference thumbRef = ROOT_STORAGE.child(STORAGE_GROUP_MEDIA)
                .child(STORAGE_THUMB_AVATAR)
                .child(mGroupId).child(imageName);

        mPictureUploadTask = imageRef.putFile(Uri.parse(mPicturePath));

        mPictureUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (task.isSuccessful()) return imageRef.getDownloadUrl();

                urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

                throw task.getException();


            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> imageTask) {


                mThumbPictureUploadTask = thumbRef.putBytes(getThumbnail(GroupDetailPage.this, Uri.parse(mPicturePath), 60));

                mThumbPictureUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (task.isSuccessful()) return thumbRef.getDownloadUrl();

                        urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

                        throw task.getException();


                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> thumbTask) {

                        final Uri imageResult = imageTask.isSuccessful() ? imageTask.getResult() : null;
                        final Uri thumbResult = thumbTask.isSuccessful() ? thumbTask.getResult() : null;

                        urlCallBack.OnCallBack(buildUriMapAfterUpload(imageResult, thumbResult));

                    }
                });

            }
        });


    }

    private void showAvatarOptions() {

        PopupMenu popupMenu = new PopupMenu(GroupDetailPage.this, civ_new_avatar);

        MenuInflater inflater = new MenuInflater(GroupDetailPage.this);

        inflater.inflate(R.menu.avatar_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.item_pick_avatar:

                        if (Build.VERSION.SDK_INT >= 23) {
                            if (isPickMediaPermissionsGranted(GroupDetailPage.this))

                                pickImageIntent();

                            else ActivityCompat.requestPermissions(
                                    GroupDetailPage.this, PERMISSIONS_ACCESS_GALLERY, PICK_IMAGES_CODE);

                        } else pickImageIntent();

                        break;

                    case R.id.item_capture_avatar:

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (isCaptureMediaPermissionsGranted(GroupDetailPage.this)) {

                                if (Build.VERSION.SDK_INT >= 24) {
                                    try {
                                        captureImageIntentSdk24();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else captureImageIntent();

                            } else ActivityCompat.requestPermissions(GroupDetailPage.this,
                                    PERMISSIONS_CAPTURE_MEDIA, CAPTURE_IMAGE_CODE);

                        } else captureImageIntent();

                        break;

                }
                return true;
            }
        });

        popupMenu.show();

    }

    private void updateGroupAvatar() {

        Handler loadingHandler = new Handler();

        AlertDialog loadingDialog = getLoadingBuilder(GroupDetailPage.this);

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mPictureUploadTask != null && !mPictureUploadTask.isComplete()) {
                    mPictureUploadTask.cancel();
                    mPictureUploadTask = null;
                }

                if (mThumbPictureUploadTask != null && !mThumbPictureUploadTask.isComplete()) {
                    mThumbPictureUploadTask.cancel();
                    mThumbPictureUploadTask = null;
                }

                loadingHandler.removeCallbacksAndMessages(null);
            }
        });

        loadingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                loadingHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (loadingDialog.isShowing()) {

                            loadingDialog.dismiss();

                            showMessageDialog(GroupDetailPage.this, "Đã vượt quá thời gian xử lý !");

                        }

                    }
                }, MAX_PROCESSING_TIME);

            }
        });

        loadingDialog.show();

        final String imgName = generateMediaName(MEDIA_TYPE_PICTURE);

        uploadPicture(imgName, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
            @Override
            public void OnCallBack(Map<String, Uri> callbackUrl) {

                loadingDialog.dismiss();

                if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null)

                    setupAvatarUrl(callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "", callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                else showMessageDialog(GroupDetailPage.this, "Tải ảnh lên không thành công !");

            }
        });


    }

    private void setupAvatarUrl(String imageUrl, String thumbUrl) {

        getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
            @Override
            public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                final int notificationId = getNotificationsId();

                final Map<String, Object> updateGroupMap = new HashMap<>();

                for (DataSnapshot snapshot : groupMemberSnapshot.getChildren()) {

                    final Message message = buildInstantMessage(messageId, snapshot.getKey(), mGroupId,
                            "Đã cập nhật ảnh đại diện nhóm",
                            MESSAGE_TYPE_UPDATE_GROUP, notificationId, null);

                    updateGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + snapshot.getKey() + "/" + mGroupId + "/" + kGroupAvatar, imageUrl);
                    updateGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + snapshot.getKey() + "/" + mGroupId + "/" + kGroupThumbAvatar, thumbUrl);
                    updateGroupMap.put("/" + CHILD_MESSAGES + "/" + snapshot.getKey() + "/" + mGroupId + "/" + messageId, message);

                }

                ROOT_REF.updateChildren(updateGroupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        for (DataSnapshot snapshot : groupMemberSnapshot.getChildren()) {

                            if (!snapshot.getKey().equals(getMyFirebaseUserId())) {

                                sendNotificationToUser(
                                        snapshot.getKey(),
                                        getMyFirebaseUserId(),
                                        mGroupId,
                                        messageId,
                                        "Đã cập nhật ảnh đại diện cho nhóm trò chuyện.",
                                        mGroupDetail.getGroupName(),
                                        mGroupDetail.getGroupThumbAvatar(),
                                        NOTIFICATION_TYPE_UPDATE_GROUP,
                                        notificationId);

                            }

                        }
                    }
                });

            }
        });


    }

    private void captureImageIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_CODE);
    }

    private void pickImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGES_CODE);
    }

    private File createProfilePctureFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mPicturePath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void captureImageIntentSdk24() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(GroupDetailPage.this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            try {
                photoFile = createProfilePctureFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(GroupDetailPage.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createProfilePctureFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_CODE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case CAPTURE_IMAGE_CODE:

                if (isAllPermissionsGrantedInResult(grantResults)) {

                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            captureImageIntentSdk24();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else captureImageIntent();

                } else showCaptureImageRequestPermissionDialog(this);


                break;

            case PICK_IMAGES_CODE:

                if (isAllPermissionsGrantedInResult(grantResults)) {
                    pickImageIntent();
                } else showPickImagesRequestPermissionDialog(this);

                break;


        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mDetailValueEvent != null && mGroupDetailRef != null) mGroupDetailRef.removeEventListener(mDetailValueEvent);

        if (mSettingValueEvent != null && mSettingRef != null) mSettingRef.removeEventListener(mSettingValueEvent);

        if (mGroupId != null) {

            mGroupDetailRef = ROOT_REF.child(CHILD_GROUP_DETAIL).child(getMyFirebaseUserId()).child(mGroupId);

            mSettingRef = ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId()).child(mGroupId).child(CHILD_MUTE_NOTIFICATIONS);

            mDetailValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        mGroupDetail = dataSnapshot.getValue(GroupDetail.class);

                        setAvatarToView(GroupDetailPage.this, mGroupDetail.getGroupAvatar(), mGroupDetail.getGroupName(), civ_avatar);

                        tv_group_name.setText(mGroupDetail.getGroupName());

                        tv_created_date.setText("Tạo ngày " + mGroupDetail.getCreatedDate());

                        tv_group_description.setText(mGroupDetail.getGroupDescription());

                        switchCensorMode(mGroupDetail.isCensorMode());

                        if (mGroupDetail.getAdminId().equals(getMyFirebaseUserId())) {

                            btn_censor_mode.setClickable(true);
                            tv_censor_mode.setTextColor(getResources().getColor(R.color.black));
                            btn_censor_member.setVisibility(View.VISIBLE);
                            btn_update_group.setVisibility(View.VISIBLE);

                        } else {

                            btn_censor_mode.setClickable(false);
                            tv_censor_mode.setTextColor(getResources().getColor(R.color.grey));
                            btn_censor_member.setVisibility(View.GONE);
                            btn_update_group.setVisibility(View.GONE);

                        }

                        if (mGroupDetail.getMember() != null && mGroupDetail.getMember().get(getMyFirebaseUserId()) != null) {

                            btn_view_members.setVisibility(View.VISIBLE);
                            btn_censor_mode.setVisibility(View.VISIBLE);

                        } else {

                            btn_view_members.setVisibility(View.GONE);
                            btn_censor_mode.setVisibility(View.GONE);

                        }

                    } else finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mSettingValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    isMute = dataSnapshot.exists();

                    switchNotifications(!isMute);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mGroupDetailRef.addValueEventListener(mDetailValueEvent);

            mSettingRef.addValueEventListener(mSettingValueEvent);

        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mDetailValueEvent != null && mGroupDetailRef != null) mGroupDetailRef.removeEventListener(mDetailValueEvent);

        if (mSettingValueEvent != null && mSettingRef != null) mSettingRef.removeEventListener(mSettingValueEvent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case CAPTURE_IMAGE_CODE:

                    if (Build.VERSION.SDK_INT < 24) mPicturePath = data != null ? data.getDataString() : null;

                    if (mPicturePath != null) setMediaUrlToView(GroupDetailPage.this, mPicturePath, civ_new_avatar);

                    break;

                case PICK_IMAGES_CODE:

                    mPicturePath = data != null ? data.getDataString() : null;

                    if (mPicturePath != null) setMediaUrlToView(GroupDetailPage.this, mPicturePath, civ_new_avatar);

                    break;
            }

        }

    }


}
