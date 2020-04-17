package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.emoji.widget.EmojiAppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.GroupMessageAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.MessageViewer;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MUTE_NOTIFICATIONS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_FULL_SIZE_IMAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_GROUP_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_THUMB_IMAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.LIKE_ICON;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_RECEIVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SEEN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_LIKE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_MULTIPLE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_REMOVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_STICKER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_TEXT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_GROUP_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.NUMBER_MESSAGE_PER_PAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.VIDEO_MAX_SIZE_IN_MB;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.CAPTURE_IMAGE_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.DOWNLOAD_MEDIA_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_RECORD_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PICK_IMAGES_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PICK_VIDEOS_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.RECORD_AUDIO_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.RECORD_VIDEO_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_STORAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_FULL_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_THUMB_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageViewer;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.getChattingUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setChattingUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isSeen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isValidImage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isValidVideo;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildUriMapAfterUpload;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.generateMediaName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupMemberSnapshot;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getThumbnail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAudioRecordPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isDownloadMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showCaptureImageRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showDownloadMediaRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showPickImagesRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showPickVideosRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showRecordAudioRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showRecordVideoRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewGroupDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.markMessageIsSent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.startDownloadingMedia;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class GroupChat extends BaseChatActivity {

    private LinearLayout linear_block;

    private RecyclerView rv_messages;
    private GroupMessageAdapter mMessagesAdapter; // adapter of list messages
    private ArrayList<Message> mMessageList; // list messages

    private Map<String, User> mUserProfileMap;

    private ValueEventListener mGroupDetailValueEvent;
    private ValueEventListener mSettingValueEvent;

    private ChildEventListener mAllMessageChildEvent;
    private ChildEventListener mLimitedMessageChildEvent;

    private DatabaseReference mGroupDetailRef;
    private DatabaseReference mSettingRef;
    private DatabaseReference mMessageRef;

    private GroupDetail mGroupDetail;

    private String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        if (mAllMessageChildEvent != null && mMessageRef != null) mMessageRef.removeEventListener(mAllMessageChildEvent);

        if (mLimitedMessageChildEvent != null && mMessageRef != null)
            mMessageRef.limitToLast(NUMBER_MESSAGE_PER_PAGE).removeEventListener(mLimitedMessageChildEvent);

        mGroupDetail = (GroupDetail) getDataFromIntent(GroupChat.this, INTENT_KEY_GROUP_DETAIL);

        mGroupId = mGroupDetail != null ? mGroupDetail.getGroupId() : null;

        if (mGroupId != null) {

            mMessageRef = ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mGroupId);

            initViews();

            initMutualViews();

            initAudioRecorderView();

            initEmoji();

            initEditTextContent();

            initClickEvents();

            addRvScrollListener();

            loadAllMessage();

            loadLimitedMessage();

            countUnseenMessage();

            setAvatarToView(GroupChat.this, mGroupDetail.getGroupThumbAvatar(), mGroupDetail.getGroupName(), civ_avatar);

            tv_name.setText(mGroupDetail.getGroupName());

            tv_sub_infor.setText(mGroupDetail.getMember() != null ? mGroupDetail.getMember().size() + " thành viên" : null);

            tv_sub_infor.setVisibility(mGroupDetail.getMember() != null ? View.VISIBLE : View.GONE);

            if (mGroupDetail.getMember() != null) {

                linear_block.setVisibility(View.GONE);
                linear_send_messages.setVisibility(View.VISIBLE);

            } else {

                if (mSendMediaOptionsDialog != null && mSendMediaOptionsDialog.isShowing()) mSendMediaOptionsDialog.dismiss();

                hideAudioRecorder();

                hideEmoji();

                hideKeyboard(GroupChat.this);

                linear_block.setVisibility(View.VISIBLE);
                linear_send_messages.setVisibility(View.GONE);

            }

        } else finish();

    }

    private void initViews() {

        btn_more = findViewById(R.id.frame_more);

        linear_block = findViewById(R.id.linear_block);

        mUserProfileMap = new HashMap<>();

        mMessageList = new ArrayList<>();
        mMessagesAdapter = new GroupMessageAdapter(GroupChat.this, mMessageList, mUserProfileMap, mGroupId);

        mLinearLayoutManager = new LinearLayoutManager(GroupChat.this, LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager.setStackFromEnd(true);

        rv_messages = findViewById(R.id.rv_messages);
        rv_messages.setHasFixedSize(true);
        rv_messages.setLayoutManager(mLinearLayoutManager);
        rv_messages.setAdapter(mMessagesAdapter);

        disableChangeAnimation(rv_messages);
    }

    private void initEditTextContent() {
        edt_content = (EmojiAppCompatEditText) findViewById(R.id.edt_content);
        edt_content.clearFocus();
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!edt_content.getText().toString().isEmpty()) {

                    btn_send_message.setVisibility(View.VISIBLE);
                    btn_right_arrow.setVisibility(View.VISIBLE);

                    btn_like.setVisibility(View.GONE);
                    btn_mms.setVisibility(View.GONE);
                    btn_record_audio.setVisibility(View.GONE);


                } else {

                    btn_send_message.setVisibility(View.GONE);
                    btn_right_arrow.setVisibility(View.GONE);

                    btn_like.setVisibility(View.VISIBLE);
                    btn_mms.setVisibility(View.VISIBLE);
                    btn_record_audio.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edt_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_content.requestFocus();

                showKeyboard(GroupChat.this);

                hideEmoji();

                rv_messages.scrollToPosition(mMessageList.size() - 1);


            }
        });
    }

    private void initClickEvents() {
        btn_record_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linear_audio_recorder.getVisibility() == View.VISIBLE) {

                    hideAudioRecorder();

                    linear_send_messages.setVisibility(View.VISIBLE);

                } else {

                    hideEmoji();

                    hideKeyboard(GroupChat.this);

                    if (Build.VERSION.SDK_INT >= 23) {

                        if (isAudioRecordPermissionsGranted(GroupChat.this))

                            showRecordAudioDialog();

                        else ActivityCompat.requestPermissions(GroupChat.this, PERMISSIONS_RECORD_AUDIO, RECORD_AUDIO_CODE);

                    } else showRecordAudioDialog();

                }
            }
        });

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_like.setClickable(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_like.setClickable(true);
                    }
                }, 2000);

                sendTextMessage(LIKE_ICON, MESSAGE_TYPE_LIKE);

                rv_messages.scrollToPosition(mMessageList.size() - 1);

            }
        });

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String content = edt_content.getText().toString().trim();

                if (!content.isEmpty()) {

                    sendTextMessage(content, MESSAGE_TYPE_TEXT);

                    edt_content.setText("");
                    rv_messages.scrollToPosition(mMessageList.size() - 1);

                }


            }
        });

        btn_mms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideEmoji();

                hideKeyboard(GroupChat.this);

                showSendMediaOptionsDialog();
            }
        });

        btn_right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_mms.setVisibility(View.VISIBLE);
                btn_record_audio.setVisibility(View.VISIBLE);
                btn_right_arrow.setVisibility(View.GONE);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linear_new_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_messages.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
            }
        });

        View.OnClickListener viewDetailClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideEmoji();

                hideKeyboard(GroupChat.this);

                viewGroupDetail(GroupChat.this, mGroupId);

            }
        };

        civ_avatar.setOnClickListener(viewDetailClick);
        tv_name.setOnClickListener(viewDetailClick);
        tv_sub_infor.setOnClickListener(viewDetailClick);
        btn_more.setOnClickListener(viewDetailClick);

    }

    private void addRvScrollListener() {
        rv_messages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!mMessageList.isEmpty()) {
                    if (mLinearLayoutManager != null) {
                        final int last = mLinearLayoutManager.findLastVisibleItemPosition();
                        final int first = mLinearLayoutManager.findFirstVisibleItemPosition();

                        if (last >= 0 && first >= 0 && last < mMessageList.size() && first < mMessageList.size()) {
                            for (int index = first; index <= last; index++) {
                                markSeen(mMessageList.get(index));
                            }
                        }
                    }
                }


                if (mLinearLayoutManager.findLastVisibleItemPosition() <= mMessageList.size() - 5) {

                    linear_new_messages.setVisibility(View.VISIBLE);


                } else linear_new_messages.setVisibility(View.GONE);

                if (!rv_messages.canScrollVertically(-1)) {

                    if (!isLoadingMore) if (ableToLoadMore) {
                        final int firstPos = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

                        if (firstPos >= 0) {
                            if (!mMessageList.isEmpty()) {
                                if (mMessageList.get(0).getMessageId().equals(mMessageList.get(firstPos).getMessageId())) {
                                    checkToLoadMore();
                                }
                            }
                        }

                    }

                }

            }
        });

    }


    private void markSeen(Message messageToSeen) {

        if (messageToSeen.getMessageId() != null) {
            if (!messageToSeen.getSenderId().equals(getMyFirebaseUserId())
                    && !isSeen(messageToSeen.getStatus())
                    && getChattingUserId() != null && getChattingUserId().equals(mGroupId)) {


                final long seenTime = getCurrentTimeInMilies();

                final MessageViewer messageViewer = new MessageViewer(getMyFirebaseUserId(), seenTime);

                final Map<String, Object> messageViewerMap = new HashMap<>();

                messageViewerMap.put("/" + kMessageStatus, MESSAGE_STATUS_SEEN);
                messageViewerMap.put("/" + kMessageViewer + "/" + messageViewer.getViewerId(), messageViewer);

                ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mGroupId)
                        .child(messageToSeen.getMessageId()).updateChildren(messageViewerMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                    @Override
                                    public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                        if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                                            markSeenForMember(groupMemberSnapshot);

                                            if (groupMemberSnapshot.hasChild(messageToSeen.getSenderId())) markSeenForSender();
                                        }
                                    }

                                    private void markSeenForSender() {

                                        DatabaseReference senderMessageRef = ROOT_REF.child(CHILD_MESSAGES)
                                                .child(messageToSeen.getSenderId()).child(mGroupId)
                                                .child(messageToSeen.getMessageId());

                                        senderMessageRef.child(kMessageStatus).runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                                final String currentStatus = dataSnapshot.getValue(String.class);

                                                if (currentStatus != null) {

                                                    if (currentStatus.equals(MESSAGE_STATUS_SENT)) {

                                                        senderMessageRef.child(kMessageStatus).setValue(MESSAGE_STATUS_RECEIVED)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        senderMessageRef.updateChildren(messageViewerMap);
                                                                    }
                                                                });

                                                    } else if (currentStatus.equals(MESSAGE_STATUS_RECEIVED)) {

                                                        senderMessageRef.updateChildren(messageViewerMap);

                                                    }


                                                }


                                            }
                                        });
                                    }

                                    private void markSeenForMember(DataSnapshot snapshotMembers) {

                                        for (DataSnapshot snapshot : snapshotMembers.getChildren()) {

                                            final boolean shouldMark = !snapshot.getKey().equals(getMyFirebaseUserId())
                                                    && !snapshot.getKey().equals(messageToSeen.getSenderId());

                                            if (shouldMark) {

                                                DatabaseReference memberMessageRef = ROOT_REF.child(CHILD_MESSAGES)
                                                        .child(snapshot.getKey()).child(mGroupId)
                                                        .child(messageToSeen.getMessageId());

                                                memberMessageRef.child(kMessageStatus).runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                        return Transaction.success(mutableData);
                                                    }

                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                                        final String currentStatus = dataSnapshot.getValue(String.class);

                                                        if (currentStatus != null) {
                                                            memberMessageRef.child(kMessageViewer).child(messageViewer.getViewerId()).setValue(messageViewer);
                                                        }

                                                    }
                                                });
                                            }
                                        }
                                    }
                                });

                            }
                        });

            }
        }


    }

    private void loadLimitedMessage() {

        mLimitedMessageChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Message message = dataSnapshot.getValue(Message.class);

                mMessageList.add(message);
                mMessagesAdapter.notifyItemInserted(mMessageList.size() - 1);

                if (mLinearLayoutManager.findLastVisibleItemPosition() >= mMessageList.size() - 2)
                    rv_messages.scrollToPosition(mMessageList.size() - 1);

                mNewestKey = mCheckerKey = mMessageList.get(0).getMessageId();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMessageRef.limitToLast(NUMBER_MESSAGE_PER_PAGE)
                .addChildEventListener(mLimitedMessageChildEvent);

    }

    private void loadAllMessage() {

        mAllMessageChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Message updatedMessage = dataSnapshot.getValue(Message.class);

                for (int index = 0; index < mMessageList.size(); index++) {

                    final Message message = mMessageList.get(index);

                    if (message.getMessageId().equals(updatedMessage.getMessageId())) {

                        if (message.getType() != MESSAGE_TYPE_REMOVED && updatedMessage.getType() == MESSAGE_TYPE_REMOVED)
                            mMessagesAdapter.stopActionWithThisMessage(updatedMessage);

                        mMessageList.set(index, updatedMessage);
                        mMessagesAdapter.notifyItemChanged(index);

                        break;

                    }

                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                final Message removedMessage = dataSnapshot.getValue(Message.class);

                mMessagesAdapter.stopActionWithThisMessage(removedMessage);

                final int i = searchMessage(mMessageList, removedMessage.getMessageId());
                if (hasItemInList(i)) {
                    mMessageList.remove(i);
                    mMessagesAdapter.notifyItemRemoved(i);
                    mMessagesAdapter.notifyItemRangeChanged(Math.max(0, i - 1), mMessagesAdapter.getItemCount());
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMessageRef.addChildEventListener(mAllMessageChildEvent);

    }

    private void countUnseenMessage() {

        ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mGroupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        int unseenCounter = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            final Message message = snapshot.getValue(Message.class);


                            if (!message.getSenderId().equals(getMyFirebaseUserId())
                                    && !message.getStatus().equals(MESSAGE_STATUS_SEEN))
                                unseenCounter++;

                        }

                        if (unseenCounter == 0) {

                            card_unseen.setVisibility(View.GONE);
                            tv_num_unseen.setText(null);

                        } else {

                            card_unseen.setVisibility(View.VISIBLE);
                            tv_num_unseen.setText((unseenCounter > 99 ? "99+" : unseenCounter) + " tin nhắn chưa xem");
                        }

                        rv_messages.scrollToPosition(mMessageList.size() - 1);

                        ableToLoadMore = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendTextMessage(String messageContent, int messageType) {


        if (isConnectedToFirebaseService(GroupChat.this))

            sendTextMessageOnline(messageContent, messageType);

        else sendTextMessageOffline(messageContent, messageType);


    }

    private void sendTextMessageOffline(String messageContent, int messageType) {

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), getMyFirebaseUserId(), mGroupId,
                messageContent, MESSAGE_STATUS_SENDING,
                sendTime, 0, messageType,
                notificationId, 0, null, false, null, null, null);

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        getGroupDetail(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                            @Override
                            public void OnCallBack(GroupDetail callbackGroupDetail) {

                                if (callbackGroupDetail != null) {

                                    if (callbackGroupDetail.getMember() != null && callbackGroupDetail.getMember().get(getMyFirebaseUserId()) != null) {

                                        markMessageIsSent(messageId, null, mGroupId);

                                        final Map<String, Object> mapMessageToSend = new HashMap<>();

                                        for (String memberId : callbackGroupDetail.getMember().keySet()) {

                                            if (!memberId.equals(getMyFirebaseUserId())) {

                                                final Message memberMess = new Message(
                                                        messageId, getMyFirebaseUserId(), memberId, mGroupId,
                                                        messageContent, MESSAGE_STATUS_SENT,
                                                        sendTime, 0, messageType,
                                                        notificationId, 0, null, false, null, null, null);

                                                mapMessageToSend.put("/" + CHILD_MESSAGES + "/" + memberId + "/" + mGroupId + "/" + messageId, memberMess);
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
                                                                mGroupId,
                                                                messageId,
                                                                messageContent,
                                                                callbackGroupDetail.getGroupName(),
                                                                callbackGroupDetail.getGroupThumbAvatar(),
                                                                NOTIFICATION_TYPE_GROUP_MESSAGE,
                                                                notificationId);
                                                    }

                                                }

                                            }
                                        });


                                    } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn");

                                }
                            }
                        });

                    }
                });

    }

    private void sendTextMessageOnline(String messageContent, int messageType) {

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), getMyFirebaseUserId(), mGroupId,
                messageContent, MESSAGE_STATUS_SENDING,
                sendTime, 0, messageType,
                notificationId, 0, null, false, null, null, null);

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId())
                                .child(mGroupId).child(messageId)
                                .child(kMessageStatus).setValue(MESSAGE_STATUS_SENT)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        final Map<String, Object> mapMessageToSend = new HashMap<>();

                                        for (String memberId : mGroupDetail.getMember().keySet()) {

                                            if (!memberId.equals(getMyFirebaseUserId())) {

                                                final Message memberMess = new Message(
                                                        messageId, getMyFirebaseUserId(), memberId, mGroupId,
                                                        messageContent, MESSAGE_STATUS_SENT,
                                                        sendTime, 0, messageType,
                                                        notificationId, 0, null, false, null, null, null);

                                                mapMessageToSend.put("/" + CHILD_MESSAGES + "/" + memberId + "/" + mGroupId + "/" + messageId, memberMess);
                                            }
                                        }

                                        ROOT_REF.updateChildren(mapMessageToSend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                for (String key : mGroupDetail.getMember().keySet()) {

                                                    if (!key.equals(getMyFirebaseUserId())) {

                                                        sendNotificationToUser(
                                                                key,
                                                                getMyFirebaseUserId(),
                                                                mGroupId,
                                                                messageId,
                                                                messageContent,
                                                                mGroupDetail.getGroupName(),
                                                                mGroupDetail.getGroupThumbAvatar(),
                                                                NOTIFICATION_TYPE_GROUP_MESSAGE,
                                                                notificationId
                                                        );

                                                    }

                                                }
                                            }
                                        });

                                    }
                                });

                    }
                });

    }

    public void sendSticker(String sticker) {

        if (isConnectedToFirebaseService(GroupChat.this))

            sendStickerOnline(sticker);

        else sendStickerOffline(sticker);

    }

    private void sendStickerOnline(String sticker) {

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), getMyFirebaseUserId(), mGroupId,
                "Đã gửi một nhãn dán", MESSAGE_STATUS_SENDING,
                sendTime, 0, MESSAGE_TYPE_STICKER,
                notificationId, 0, sticker, false, null, null, null);


        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.child(CHILD_MESSAGES)
                                .child(getMyFirebaseUserId()).child(mGroupId).child(messageId)
                                .child(kMessageStatus).setValue(MESSAGE_STATUS_SENT)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        final Map<String, Object> mapMessageToSend = new HashMap<>();

                                        for (String memberId : mGroupDetail.getMember().keySet()) {

                                            if (!memberId.equals(getMyFirebaseUserId())) {

                                                final Message memberMess = new Message(
                                                        messageId, getMyFirebaseUserId(), memberId, mGroupId,
                                                        "Đã gửi một nhãn dán", MESSAGE_STATUS_SENT,
                                                        sendTime, 0, MESSAGE_TYPE_STICKER,
                                                        notificationId, 0, sticker, false, null, null, null);

                                                mapMessageToSend.put("/" + CHILD_MESSAGES + "/" + memberId + "/" + mGroupId + "/" + messageId, memberMess);
                                            }

                                        }

                                        ROOT_REF.updateChildren(mapMessageToSend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                for (String memberId : mGroupDetail.getMember().keySet()) {

                                                    if (!memberId.equals(getMyFirebaseUserId())) {

                                                        sendNotificationToUser(
                                                                memberId,
                                                                getMyFirebaseUserId(),
                                                                mGroupId,
                                                                messageId,
                                                                "Đã gửi một nhãn dán.",
                                                                mGroupDetail.getGroupName(),
                                                                mGroupDetail.getGroupThumbAvatar(),
                                                                NOTIFICATION_TYPE_GROUP_MESSAGE,
                                                                notificationId
                                                        );

                                                    }
                                                }
                                            }
                                        });

                                    }
                                });


                    }


                });

    }

    private void sendStickerOffline(String sticker) {

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), getMyFirebaseUserId(), mGroupId,
                "Đã gửi một nhãn dán", MESSAGE_STATUS_SENDING,
                sendTime, 0, MESSAGE_TYPE_STICKER,
                notificationId, 0, sticker, false, null, null, null);

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        getGroupDetail(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                            @Override
                            public void OnCallBack(GroupDetail callbackGroupDetail) {

                                if (callbackGroupDetail != null) {

                                    if (callbackGroupDetail.getMember() != null && callbackGroupDetail.getMember().get(getMyFirebaseUserId()) != null) {

                                        markMessageIsSent(messageId, null, mGroupId);

                                        final Map<String, Object> mapMessageToSend = new HashMap<>();

                                        for (String memberId : callbackGroupDetail.getMember().keySet()) {

                                            if (!memberId.equals(getMyFirebaseUserId())) {

                                                final Message memberMess = new Message(
                                                        messageId, getMyFirebaseUserId(), memberId, mGroupId,
                                                        "Đã gửi một nhãn dán", MESSAGE_STATUS_SENT,
                                                        sendTime, 0, MESSAGE_TYPE_STICKER,
                                                        notificationId, 0, sticker, false, null, null, null);

                                                mapMessageToSend.put("/" + CHILD_MESSAGES + "/" + memberId + "/" + mGroupId + "/" + messageId, memberMess);

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
                                                                mGroupId,
                                                                messageId,
                                                                "Đã gửi một nhãn dán",
                                                                callbackGroupDetail.getGroupName(),
                                                                callbackGroupDetail.getGroupThumbAvatar(),
                                                                NOTIFICATION_TYPE_GROUP_MESSAGE,
                                                                notificationId);

                                                    }

                                                }

                                            }
                                        });


                                    } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn");

                                }
                            }
                        });

                    }
                });

    }

    private void sendMedia(DataSnapshot memberSnapshot,
                           Map<String, String> mediaNameMap, Map<String, String> fullSizeMap,
                           Map<String, String> thumbSizeMap, int elementType, int audioDuration) {


        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long mediaSendTime = getCurrentTimeInMilies();

        final int numberOfMedia = mediaNameMap.size();

        String messageContent;

        if (elementType == MEDIA_TYPE_PICTURE) messageContent = "Đã gửi " + numberOfMedia + " ảnh";
        else if (elementType == MEDIA_TYPE_VIDEO) messageContent = "Đã gửi " + numberOfMedia + " video";
        else messageContent = "Đã gửi " + numberOfMedia + " tin nhắn thoại";

        int messageType = -1;

        if (elementType == MEDIA_TYPE_AUDIO) {
            messageType = MESSAGE_TYPE_AUDIO;
        } else if (numberOfMedia > 1) {
            messageType = MESSAGE_TYPE_MULTIPLE_MEDIA;
        } else if (elementType == MEDIA_TYPE_PICTURE) {
            messageType = MESSAGE_TYPE_PICTURE;
        } else if (elementType == MEDIA_TYPE_VIDEO) {
            messageType = MESSAGE_TYPE_VIDEO;
        }

        final Map<String, Object> myMapToSend = new HashMap<>();
        final Map<String, Object> memberMapToSend = new HashMap<>();

        //add message for member
        for (DataSnapshot snapshot : memberSnapshot.getChildren()) {

            for (String mediaKey : mediaNameMap.keySet()) {

                Media video = new Media(
                        mediaKey, messageId, getMyFirebaseUserId(), snapshot.getKey(), mGroupId,
                        mediaNameMap.get(mediaKey),
                        fullSizeMap.get(mediaKey),
                        thumbSizeMap.get(mediaKey),
                        elementType, audioDuration, mediaSendTime);

                if (snapshot.getKey().equals(getMyFirebaseUserId())) {

                    final Message myMess = new Message(
                            messageId, getMyFirebaseUserId(), snapshot.getKey(), mGroupId,
                            messageContent, MESSAGE_STATUS_SENDING,
                            mediaSendTime, 0, messageType,
                            notificationId, 0, null, false, null, null, null);


                    myMapToSend.put("/" + CHILD_MEDIA + "/" + snapshot.getKey() + "/" + mGroupId + "/" + mediaKey, video);
                    myMapToSend.put("/" + CHILD_MESSAGES + "/" + snapshot.getKey() + "/" + mGroupId + "/" + messageId, myMess);

                } else {

                    final Message memberMess = new Message(
                            messageId, getMyFirebaseUserId(), snapshot.getKey(), mGroupId,
                            messageContent, MESSAGE_STATUS_SENT,
                            mediaSendTime, 0, messageType,
                            notificationId, 0, null, false, null, null, null);

                    memberMapToSend.put("/" + CHILD_MEDIA + "/" + snapshot.getKey() + "/" + mGroupId + "/" + mediaKey, video);
                    memberMapToSend.put("/" + CHILD_MESSAGES + "/" + snapshot.getKey() + "/" + mGroupId + "/" + messageId, memberMess);

                }

            }


        }

        ROOT_REF.updateChildren(myMapToSend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (memberSnapshot.hasChild(getMyFirebaseUserId())) {

                    markMessageIsSent(messageId, null, mGroupId);

                    ROOT_REF.updateChildren(memberMapToSend).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            for (DataSnapshot snapshot : memberSnapshot.getChildren()) {

                                if (!snapshot.getKey().equals(getMyFirebaseUserId())) {

                                    sendNotificationToUser(
                                            snapshot.getKey(),
                                            getMyFirebaseUserId(),
                                            mGroupId,
                                            messageId,
                                            messageContent,
                                            mGroupDetail.getGroupName(),
                                            mGroupDetail.getGroupThumbAvatar(),
                                            NOTIFICATION_TYPE_GROUP_MESSAGE,
                                            notificationId
                                    );

                                }
                            }
                        }
                    });

                } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn !");


            }
        });

    }

    private void uploadAndSendVideos(List<Uri> uriInputList, boolean fromGallery) {

        if (fromGallery) {

            ArrayList<Uri> validList = new ArrayList<>();
            ArrayList<Uri> invalidList = new ArrayList<>();

            for (Uri uri : uriInputList) {

                if (isValidVideo(GroupChat.this, uri)) {
                    validList.add(uri);
                } else invalidList.add(uri);

            }
            if (invalidList.size() > 0) {

                showMessageDialog(GroupChat.this,
                        "Không thể gửi " + invalidList.size() + " video có kích thước lớn hơn " + VIDEO_MAX_SIZE_IN_MB + "MB hoặc bằng 0!");

            }

            if (validList.size() > 0) {

                showSendingMediaView();

                Map<String, String> uploadSuccessMapFullSize = new HashMap<>();
                Map<String, String> uploadSuccessMapThumbSize = new HashMap<>();
                Map<String, String> uploadSuccessMapMediaName = new HashMap<>();

                Map<String, String> uploadFailMap = new HashMap<>();

                for (Uri uriToUpload : validList) {

                    String videoName = generateMediaName(MEDIA_TYPE_VIDEO);

                    uploadVideos(videoName, uriToUpload, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                uploadSuccessMapMediaName.put(mediaId, videoName);

                                uploadSuccessMapFullSize.put(mediaId, callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "");
                                uploadSuccessMapThumbSize.put(mediaId, callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                            } else uploadFailMap.put(mediaId, "");

                            if (uploadSuccessMapMediaName.size() + uploadFailMap.size() == validList.size()) {

                                hideSendingMediaView();

                                if (uploadFailMap.size() > 0) {

                                    showMessageDialog(GroupChat.this, "Tải " + uploadFailMap.size() + " video lên không thành công !");

                                }

                                if (uploadSuccessMapFullSize.size() > 0 && uploadSuccessMapThumbSize.size() > 0 && uploadSuccessMapMediaName.size() > 0) {

                                    getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                        @Override
                                        public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                            if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                                                sendMedia(groupMemberSnapshot,
                                                        uploadSuccessMapMediaName,
                                                        uploadSuccessMapFullSize,
                                                        uploadSuccessMapThumbSize,
                                                        MEDIA_TYPE_VIDEO, 0);

                                            } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn !");

                                        }
                                    });

                                }

                            }
                        }
                    });

                }


            }

        } else {

            if (uriInputList.size() > 0) {

                showSendingMediaView();

                for (Uri uriToUpload : uriInputList) {

                    String videoName = generateMediaName(MEDIA_TYPE_VIDEO);

                    uploadVideos(videoName, uriToUpload, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            hideSendingMediaView();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                Map<String, String> uploadSuccessMapFullSize = new HashMap<>();
                                Map<String, String> uploadSuccessMapThumbSize = new HashMap<>();
                                Map<String, String> uploadSuccessMapMediaName = new HashMap<>();

                                final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                                uploadSuccessMapMediaName.put(mediaId, videoName);

                                uploadSuccessMapFullSize.put(mediaId, callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "");
                                uploadSuccessMapThumbSize.put(mediaId, callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                                getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                    @Override
                                    public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                        if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                                            sendMedia(groupMemberSnapshot,
                                                    uploadSuccessMapMediaName,
                                                    uploadSuccessMapFullSize,
                                                    uploadSuccessMapThumbSize,
                                                    MEDIA_TYPE_VIDEO, 0);

                                        } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn !");

                                    }
                                });


                            } else showMessageDialog(GroupChat.this, "Tải video lên không thành công !");

                        }
                    });

                }

            }
        }


    }

    private void uploadAndSendPictures(List<Uri> uriInputList, boolean fromGallery) {

        if (fromGallery) {

            ArrayList<Uri> validList = new ArrayList<>();
            ArrayList<Uri> invalidList = new ArrayList<>();

            for (Uri uri : uriInputList) {
                if (isValidImage(GroupChat.this, uri))
                    validList.add(uri);
                else invalidList.add(uri);
            }

            if (invalidList.size() > 0) {

                showMessageDialog(GroupChat.this,
                        "Không thể gửi " + invalidList.size() + " ảnh có kích thước không hợp lệ !");
            }

            if (validList.size() > 0) {

                showSendingMediaView();

                Map<String, String> uploadSuccessMapFullSize = new HashMap<>();
                Map<String, String> uploadSuccessMapThumbSize = new HashMap<>();
                Map<String, String> uploadSuccessMapMediaName = new HashMap<>();

                Map<String, String> uploadFailMap = new HashMap<>();

                for (Uri uriToSend : validList) {

                    final String imageName = generateMediaName(MEDIA_TYPE_PICTURE);

                    uploadImages(imageName, uriToSend, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                uploadSuccessMapMediaName.put(mediaId, imageName);

                                uploadSuccessMapFullSize.put(mediaId, callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "");
                                uploadSuccessMapThumbSize.put(mediaId, callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                            } else uploadFailMap.put(mediaId, "");

                            if (uploadSuccessMapMediaName.size() + uploadFailMap.size() == validList.size()) {

                                hideSendingMediaView();

                                if (uploadFailMap.size() > 0) {

                                    showMessageDialog(GroupChat.this,
                                            "Tải " + uploadFailMap.size() + " ảnh lên không thành công !");

                                }

                                if (uploadSuccessMapFullSize.size() > 0 && uploadSuccessMapThumbSize.size() > 0 && uploadSuccessMapMediaName.size() > 0) {

                                    getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                        @Override
                                        public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                            if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                                                sendMedia(groupMemberSnapshot,
                                                        uploadSuccessMapMediaName,
                                                        uploadSuccessMapFullSize,
                                                        uploadSuccessMapThumbSize,
                                                        MEDIA_TYPE_PICTURE, 0);

                                            } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn !");
                                        }
                                    });

                                }

                            }
                        }
                    });

                }
            }

        } else {

            if (uriInputList.size() > 0) {

                showSendingMediaView();

                for (Uri uriToSend : uriInputList) {

                    final String imageName = generateMediaName(MEDIA_TYPE_PICTURE);

                    uploadImages(imageName, uriToSend, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            hideSendingMediaView();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                Map<String, String> uploadSuccessMapFullSize = new HashMap<>();
                                Map<String, String> uploadSuccessMapThumbSize = new HashMap<>();
                                Map<String, String> uploadSuccessMapMediaName = new HashMap<>();

                                final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                                uploadSuccessMapMediaName.put(mediaId, imageName);

                                uploadSuccessMapFullSize.put(mediaId, callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "");
                                uploadSuccessMapThumbSize.put(mediaId, callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                                getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                    @Override
                                    public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                        if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                                            sendMedia(groupMemberSnapshot,
                                                    uploadSuccessMapMediaName,
                                                    uploadSuccessMapFullSize,
                                                    uploadSuccessMapThumbSize,
                                                    MEDIA_TYPE_PICTURE, 0);

                                        } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn !");

                                    }
                                });

                            } else showMessageDialog(GroupChat.this, "Tải ảnh lên không thành công !");
                        }
                    });

                }

            }
        }


    }

    @Override
    protected void uploadAndSendAudio(String audioFilePath, int audioDuration) {

        String audioName = generateMediaName(MEDIA_TYPE_AUDIO);

        showSendingAudioView();

        uploadAudio(audioName, Uri.fromFile(new File(audioFilePath)), new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
            @Override
            public void OnCallBack(Map<String, Uri> callbackUrl) {

                hideSendingAudioView();

                if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                    Map<String, String> uploadSuccessMapFullSize = new HashMap<>();
                    Map<String, String> uploadSuccessMapThumbSize = new HashMap<>();
                    Map<String, String> uploadSuccessMapMediaName = new HashMap<>();

                    final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                    uploadSuccessMapMediaName.put(mediaId, audioName);

                    uploadSuccessMapFullSize.put(mediaId, callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "");
                    uploadSuccessMapThumbSize.put(mediaId, callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                    getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                        @Override
                        public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                            if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                                sendMedia(groupMemberSnapshot,
                                        uploadSuccessMapMediaName,
                                        uploadSuccessMapFullSize,
                                        uploadSuccessMapThumbSize,
                                        MEDIA_TYPE_AUDIO, audioDuration);

                            } else showMessageDialog(GroupChat.this, "Lỗi trong khi gửi tin nhắn !");
                        }
                    });

                } else showMessageDialog(GroupChat.this, "Tải lên đoạn nghi âm không thành công !");

            }
        });

    }

    @Override
    protected void stopMessageAudioPlayer() {
        mMessagesAdapter.releaseMediaPlayer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case DOWNLOAD_MEDIA_CODE:

                if (isAllPermissionsGrantedInResult(grantResults))

                    startDownloadingMedia(GroupChat.this, mediaToDownload);

                else showDownloadMediaRequestPermissionDialog(GroupChat.this);


                break;

            case RECORD_AUDIO_CODE:

                if (isAllPermissionsGrantedInResult(grantResults))

                    showRecordAudioDialog();

                else showRecordAudioRequestPermissionDialog(GroupChat.this);

                break;

            case CAPTURE_IMAGE_CODE:

                if (isAllPermissionsGrantedInResult(grantResults)) {

                    if (Build.VERSION.SDK_INT >= 24) {

                        try {
                            captureImageSdk24();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else captureImageIntent();

                } else showCaptureImageRequestPermissionDialog(this);


                break;

            case RECORD_VIDEO_CODE:

                if (isAllPermissionsGrantedInResult(grantResults))

                    recordVideoIntent();

                else showRecordVideoRequestPermissionDialog(this);

                break;

            case PICK_IMAGES_CODE:

                if (isAllPermissionsGrantedInResult(grantResults))

                    pickImagesFromGallery();

                else showPickImagesRequestPermissionDialog(this);


                break;

            case PICK_VIDEOS_CODE:

                if (isAllPermissionsGrantedInResult(grantResults))

                    pickVideosFromGallery();

                else showPickVideosRequestPermissionDialog(this);

                break;

        }


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isConnectedToFirebaseService(GroupChat.this)) {

            getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                @Override
                public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                    if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                        if (resultCode == RESULT_OK) {

                            rv_messages.scrollToPosition(mMessageList.size() - 1);

                            switch (requestCode) {

                                case RECORD_VIDEO_CODE:

                                    if (data != null) {
                                        ArrayList<Uri> videoList = new ArrayList<>();
                                        videoList.add(data.getData());
                                        uploadAndSendVideos(videoList, false);
                                    }

                                    break;

                                case PICK_VIDEOS_CODE:

                                    if (data != null) {
                                        List<Uri> videos = Matisse.obtainResult(data);
                                        uploadAndSendVideos(videos, true);
                                    }

                                    break;

                                case PICK_IMAGES_CODE:

                                    if (data != null) {
                                        List<Uri> pictures = Matisse.obtainResult(data);
                                        uploadAndSendPictures(pictures, true);
                                    }

                                    break;

                                case CAPTURE_IMAGE_CODE:

                                    if (Build.VERSION.SDK_INT < 24) mPicturePath = data != null ? data.getDataString() : null;

                                    if (mPicturePath != null) {

                                        Uri imageUri = Uri.parse(mPicturePath);

                                        ArrayList<Uri> pictureList = new ArrayList<>();

                                        pictureList.add(imageUri);

                                        uploadAndSendPictures(pictureList, false);

                                    }

                                    break;
                            }

                        }

                    } else showLongToast(GroupChat.this, "Lỗi trong khi gửi tin nhắn");

                }
            });

        } else showNoConnectionDialog(GroupChat.this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGroupDetailValueEvent != null && mGroupDetailRef != null) mGroupDetailRef.removeEventListener(mGroupDetailValueEvent);

        if (mSettingValueEvent != null && mSettingRef != null) mSettingRef.removeEventListener(mSettingValueEvent);

        if (mGroupId != null) {

            setChattingUserId(mGroupId);

            mGroupDetailRef = ROOT_REF.child(CHILD_GROUP_DETAIL).child(getMyFirebaseUserId()).child(mGroupId);

            mSettingRef = ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId()).child(mGroupId).child(CHILD_MUTE_NOTIFICATIONS);

            mGroupDetailValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        final GroupDetail updatedGroupDetail = dataSnapshot.getValue(GroupDetail.class);

                        setAvatarToView(GroupChat.this, updatedGroupDetail.getGroupThumbAvatar(), updatedGroupDetail.getGroupName(), civ_avatar);

                        tv_name.setText(updatedGroupDetail.getGroupName());

                        tv_sub_infor.setText(updatedGroupDetail.getMember() != null ? updatedGroupDetail.getMember().size() + " thành viên" : null);

                        tv_sub_infor.setVisibility(updatedGroupDetail.getMember() != null ? View.VISIBLE : View.GONE);

                        if (mGroupDetail.getMember() != null) {

                            if (updatedGroupDetail.getMember() == null) {

                                hideAudioRecorder();

                                hideEmoji();

                                hideKeyboard(GroupChat.this);

                                linear_block.setVisibility(View.VISIBLE);
                                linear_send_messages.setVisibility(View.GONE);

                                if (mSendMediaOptionsDialog != null && mSendMediaOptionsDialog.isShowing()) mSendMediaOptionsDialog.dismiss();

                            }

                        } else if (updatedGroupDetail.getMember() != null) { //mGroupDetail ==null

                            linear_block.setVisibility(View.GONE);
                            linear_send_messages.setVisibility(View.VISIBLE);

                        }

                        mGroupDetail = updatedGroupDetail;

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

                    civ_muted.setVisibility(isMute ? View.VISIBLE : View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mGroupDetailRef.addValueEventListener(mGroupDetailValueEvent);

            mSettingRef.addValueEventListener(mSettingValueEvent);

            if (mMessageList.size() > 0) {

                if (mLinearLayoutManager != null) {

                    final int posFirst = mLinearLayoutManager.findFirstVisibleItemPosition();
                    final int posLast = mLinearLayoutManager.findLastVisibleItemPosition();

                    if (posFirst >= 0 && posLast >= 0 && posLast < mMessageList.size() && posFirst < mMessageList.size()) {
                        for (int index = posFirst; index <= posLast; index++) {
                            markSeen(mMessageList.get(index));
                        }
                    }
                }
            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        hideAudioRecorder();

        if (mMessagesAdapter != null) mMessagesAdapter.releaseMediaPlayer();

        if (mGroupDetailValueEvent != null && mGroupDetailRef != null) mGroupDetailRef.removeEventListener(mGroupDetailValueEvent);

        if (mSettingValueEvent != null && mSettingRef != null) mSettingRef.removeEventListener(mSettingValueEvent);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAllMessageChildEvent != null && mMessageRef != null) mMessageRef.removeEventListener(mAllMessageChildEvent);

        if (mLimitedMessageChildEvent != null && mMessageRef != null)
            mMessageRef.limitToLast(NUMBER_MESSAGE_PER_PAGE).removeEventListener(mLimitedMessageChildEvent);

    }

    private void checkToLoadMore() {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (mMessageList.size() < dataSnapshot.getChildrenCount()) {

                            isLoadingMore = true;

                            linear_loadmore.setVisibility(View.VISIBLE);

                            new LoadMoreThread().start();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public String getGroupAdminId() {
        return mGroupDetail.getAdminId();
    }

    private void uploadVideos(String videoName, Uri videoUri, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBack) {

        final StorageReference videoRef = ROOT_STORAGE
                .child(STORAGE_GROUP_MEDIA)
                .child(STORAGE_VIDEO)
                .child(mGroupId)
                .child(getMyFirebaseUserId())
                .child(videoName);

        UploadTask videoUploadTask = videoRef.putFile(videoUri);

        videoUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (task.isSuccessful()) return videoRef.getDownloadUrl();

                urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

                throw task.getException();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {

                    urlCallBack.OnCallBack(buildUriMapAfterUpload(task.getResult(), task.getResult()));

                } else urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

            }
        });

    }

    private void uploadImages(String imageName, Uri imageUri, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBack) {

        final StorageReference imageRef = ROOT_STORAGE
                .child(STORAGE_GROUP_MEDIA)
                .child(STORAGE_FULL_SIZE_IMAGES)
                .child(mGroupId)
                .child(getMyFirebaseUserId())
                .child(imageName);

        final StorageReference thumbRef = ROOT_STORAGE
                .child(STORAGE_GROUP_MEDIA)
                .child(STORAGE_THUMB_IMAGES)
                .child(mGroupId)
                .child(getMyFirebaseUserId())
                .child(imageName);

        UploadTask imageUploadTask = imageRef.putFile(imageUri);

        imageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (task.isSuccessful()) return imageRef.getDownloadUrl();

                urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

                throw task.getException();

            }

        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> imageTask) {

                UploadTask thumbUploadTask = thumbRef.putBytes(getThumbnail(GroupChat.this, imageUri, 40));

                thumbUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

    private void uploadAudio(String audioName, Uri audioUri, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBack) {

        final StorageReference audioRef = ROOT_STORAGE
                .child(STORAGE_GROUP_MEDIA)
                .child(STORAGE_AUDIO)
                .child(mGroupId)
                .child(getMyFirebaseUserId())
                .child(audioName);

        UploadTask audioUploadTask = audioRef.putFile(audioUri);

        audioUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (task.isSuccessful()) return audioRef.getDownloadUrl();

                urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

                throw task.getException();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {

                    urlCallBack.OnCallBack(buildUriMapAfterUpload(task.getResult(), task.getResult()));

                } else urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

            }
        });

    }

    public void downloadMediaFromActivity(Media media) {

        mediaToDownload = media;

        if (Build.VERSION.SDK_INT >= 23) {

            if (isDownloadMediaPermissionsGranted(GroupChat.this)) {

                startDownloadingMedia(GroupChat.this, mediaToDownload);

            } else ActivityCompat.requestPermissions(GroupChat.this, PERMISSIONS_RECORD_AUDIO, RECORD_AUDIO_CODE);

        } else startDownloadingMedia(GroupChat.this, mediaToDownload);

    }

    @Override
    protected void handleLoadMore(android.os.Message msg) {

        linear_loadmore.setVisibility(View.GONE);

        mMessagesAdapter.loadMoreMessages((ArrayList<Message>) msg.obj);

        isLoadingMore = false;
    }

    @Override
    protected void getLoadMoreItem(FirebaseCallBackMessageList firebaseCallBackMessageList) {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .orderByKey().endAt(mNewestKey)
                .limitToLast(NUMBER_MESSAGE_PER_PAGE + 1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final ArrayList<Message> newItemList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Message message = snapshot.getValue(Message.class);

                            if (newItemList.isEmpty()) mNewestKey = message.getMessageId();

                            if (!mCheckerKey.equals(message.getMessageId()))

                                newItemList.add(message);

                            else mCheckerKey = mNewestKey;

                        }

                        firebaseCallBackMessageList.OnCallBack(newItemList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


}
