package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.emoji.widget.EmojiAppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.MessageAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
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
import com.google.firebase.database.Query;
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

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MUTE_NOTIFICATIONS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_TYPING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_TYPING_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_FULL_SIZE_IMAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_SINGLE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_THUMB_IMAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_PROFILE;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_SINGLE_MESSAGE;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kLastSeen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageSeenTime;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkHasChildBlock;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.showUnlockUserConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeAgoDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.makeCall;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.allowNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.preventNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
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


public class Chat extends BaseChatActivity {

    private final Handler mTypingHandler = new Handler();

    private boolean isTyping = false;

    private RecyclerView rv_messages;
    private ArrayList<Message> mMessageList;
    private MessageAdapter mMessagesAdapter;

    private Button btn_unlock;
    private ImageView btn_lock_message;
    private TextView tv_lock_content;
    private LinearLayout linear_block;
    private FrameLayout btn_voice_call;
    private FrameLayout btn_video_call;
    private TextView tv_typing;

    private ValueEventListener mUserActiveValueEvent;
    private ValueEventListener mRelationshipValueEvent;
    private ValueEventListener mSettingValueEvent;
    private ValueEventListener mTypingVaLueEvent;
    private ChildEventListener mAllMessageChildEvent;
    private ChildEventListener mLimitedMessageChildEvent;

    private DatabaseReference mUserActiveRef;
    private DatabaseReference mRelationshipRef;
    private DatabaseReference mSettingRef;
    private DatabaseReference mTypingRef;
    private Query mMessageQuery;

    private User mUserProfile; // user id

    private String mChattingUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (mAllMessageChildEvent != null && mMessageQuery != null) mMessageQuery.removeEventListener(mAllMessageChildEvent);

        if (mLimitedMessageChildEvent != null && mMessageQuery != null)
            mMessageQuery.limitToLast(NUMBER_MESSAGE_PER_PAGE).removeEventListener(mLimitedMessageChildEvent);

        mUserProfile = (User) getDataFromIntent(Chat.this, INTENT_KEY_USER_PROFILE);

        mChattingUserId = mUserProfile != null ? mUserProfile.getUserId() : null;

        if (mChattingUserId != null) {

            mMessageQuery = ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mChattingUserId);

            initViews();

            initMutualViews();

            initAudioRecorderView();

            initEmoji();

            initEditTextContent();

            initClickEvents();

            addRvScrollListener();

            loadAllMessages();

            loadLimitedMessages();

            countUnSeenMessage();

            tv_name.setText(mUserProfile.getName());

            setAvatarToView(Chat.this, mUserProfile.getThumbAvatarUrl(), mUserProfile.getName(), civ_avatar);

        } else finish();


    }

    private void initViews() {

        isTyping = false;

        tv_typing = (TextView) findViewById(R.id.tv_typing);

        btn_voice_call = (FrameLayout) findViewById(R.id.frame_voice_call);
        btn_video_call = (FrameLayout) findViewById(R.id.frame_video_call);
        btn_more = (FrameLayout) findViewById(R.id.frame_more);

        btn_lock_message = (ImageView) findViewById(R.id.img_question_mark);
        btn_unlock = (Button) findViewById(R.id.btn_unlock);
        tv_lock_content = (TextView) findViewById(R.id.tv_lock_content);
        linear_block = (LinearLayout) findViewById(R.id.linear_block);

        mMessageList = new ArrayList<>();
        mMessagesAdapter = new MessageAdapter(Chat.this, mMessageList, mChattingUserId, mUserProfile);
        mLinearLayoutManager = new LinearLayoutManager(Chat.this, LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager.setStackFromEnd(true);

        rv_messages = (RecyclerView) findViewById(R.id.rv_messages);
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

                    if (isConnectedToFirebaseService(Chat.this)) {
                        if (!isTyping) {

                            isTyping = true;

                            ROOT_REF.child(CHILD_TYPING_MESSAGE)
                                    .child(mChattingUserId).child(getMyFirebaseUserId())
                                    .child(CHILD_TYPING).setValue(true);
                        }
                    }

                    btn_send_message.setVisibility(View.VISIBLE);
                    btn_right_arrow.setVisibility(View.VISIBLE);

                    btn_like.setVisibility(View.GONE);
                    btn_mms.setVisibility(View.GONE);
                    btn_record_audio.setVisibility(View.GONE);

                } else {

                    if (isTyping) {

                        isTyping = false;

                        ROOT_REF.child(CHILD_TYPING_MESSAGE).child(mChattingUserId).child(getMyFirebaseUserId()).child(CHILD_TYPING).removeValue();
                        ROOT_REF.child(CHILD_TYPING_MESSAGE).child(mChattingUserId).child(getMyFirebaseUserId()).child(CHILD_TYPING).onDisconnect().removeValue();

                        mTypingHandler.removeCallbacksAndMessages(null);

                    }
                    btn_send_message.setVisibility(View.GONE);
                    btn_right_arrow.setVisibility(View.GONE);

                    btn_like.setVisibility(View.VISIBLE);
                    btn_mms.setVisibility(View.VISIBLE);
                    btn_record_audio.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

                mTypingHandler.removeCallbacksAndMessages(null);
                mTypingHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (isTyping) {

                            isTyping = false;

                            ROOT_REF.child(CHILD_TYPING_MESSAGE).child(mChattingUserId).child(getMyFirebaseUserId()).child(CHILD_TYPING).removeValue();
                            ROOT_REF.child(CHILD_TYPING_MESSAGE).child(mChattingUserId).child(getMyFirebaseUserId()).child(CHILD_TYPING).onDisconnect().removeValue();

                            mTypingHandler.removeCallbacksAndMessages(null);
                        }
                    }
                }, 2000);

            }
        });
        edt_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_content.requestFocus();

                showKeyboard(Chat.this);

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

                    hideKeyboard(Chat.this);

                    if (Build.VERSION.SDK_INT >= 23) {

                        if (isAudioRecordPermissionsGranted(Chat.this))

                            showRecordAudioDialog();

                        else ActivityCompat.requestPermissions(Chat.this, PERMISSIONS_RECORD_AUDIO, RECORD_AUDIO_CODE);

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

                final String messContent = edt_content.getText().toString().trim();

                if (!messContent.isEmpty()) {

                    sendTextMessage(messContent, MESSAGE_TYPE_TEXT);

                    edt_content.setText("");

                    rv_messages.scrollToPosition(mMessageList.size() - 1);

                }

            }
        });

        btn_mms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideEmoji();

                hideKeyboard(Chat.this);

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

        View.OnClickListener showLockInforClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(Chat.this)
                        .setMessage("Bạn đã chặn người dùng này. Hai bạn sẽ:"
                                + "\n⦁ Không thể tìm thấy hoặc liên hệ với nhau."
                                + "\n⦁ Chỉ thấy được nhau khi tham gia cùng nhóm trò chuyện, trừ khi bạn rời khỏi nhóm đó."
                                + "\n⦁ Có thể xem các tin nhắn của nhau trong các nhóm trò chuyện chung."
                                + "\n⦁ Không thể xem trang cá nhân của nhau."
                                + "\n⦁ Hủy bạn bè nếu hai bạn đang là bạn của nhau.")
                        .setPositiveButton("Đã hiểu", null)
                        .create().show();
            }
        };

        btn_lock_message.setOnClickListener(showLockInforClick);

        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnlockUserConfirmDialog(Chat.this, mChattingUserId);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View.OnClickListener viewProfileClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideEmoji();

                hideKeyboard(Chat.this);

                viewUserProfile(Chat.this, mChattingUserId);
            }
        };

        civ_avatar.setOnClickListener(viewProfileClick);
        tv_name.setOnClickListener(viewProfileClick);
        tv_sub_infor.setOnClickListener(viewProfileClick);

        linear_new_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rv_messages.scrollToPosition(mMessageList.size() - 1);

            }
        });

        btn_voice_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideEmoji();

                hideKeyboard(Chat.this);

                makeCall(Chat.this, mChattingUserId, false);
            }
        });

        btn_video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideEmoji();

                hideKeyboard(Chat.this);

                makeCall(Chat.this, mChattingUserId, true);
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideEmoji();

                hideKeyboard(Chat.this);

                showOptions();
            }
        });

    }

    private void addRvScrollListener() {
        rv_messages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!mMessageList.isEmpty()) {

                    if (mLinearLayoutManager != null) {
                        int last = mLinearLayoutManager.findLastVisibleItemPosition();
                        int first = mLinearLayoutManager.findFirstVisibleItemPosition();

                        if (last >= 0 && first >= 0 && first < mMessageList.size() && last < mMessageList.size()) {
                            for (int index = first; index <= last; index++) {
                                markSeen(mMessageList.get(index));
                            }
                        }
                    }


                }


                if (mLinearLayoutManager.findLastVisibleItemPosition() <= mMessageList.size() - 5)

                    linear_new_messages.setVisibility(View.VISIBLE);

                else linear_new_messages.setVisibility(View.GONE);


                if (!rv_messages.canScrollVertically(-1)) {

                    if (!isLoadingMore) if (ableToLoadMore) {
                        final int first = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

                        if (first >= 0) {
                            if (!mMessageList.isEmpty()) {
                                if (mMessageList.get(0).getMessageId().equals(mMessageList.get(first).getMessageId())) {
                                    checkToLoadMore();
                                }
                            }
                        }

                    }

                }


            }
        });

    }

    private void showOptions() {

        ArrayList<String> options = new ArrayList<>();

        final String option0 = "Xem trang cá nhân";
        final String option1 = "Tìm kiếm tin nhắn";
        final String option2 = "Xem ảnh chung";
        final String option3 = "Cho phép thông báo";
        final String option4 = "Tắt thông báo";

        options.add(option0);
        options.add(option1);
        options.add(option2);

        if (isMute) options.add(option3);
        else options.add(option4);

        options.add("Hủy");

        ArrayAdapter<String> optionAdapter = new ArrayAdapter(Chat.this, android.R.layout.simple_list_item_1, options);

        new AlertDialog.Builder(Chat.this)
                .setTitle("Tuỳ chọn")
                .setAdapter(optionAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (options.get(which)) {

                            case option0:
                                viewUserProfile(Chat.this, mChattingUserId);
                                break;

                            case option1:
                                Intent it1 = new Intent(Chat.this, SearchMessage.class);
                                it1.putExtra(INTENT_KEY_USER_ID, mChattingUserId);
                                startActivity(it1);
                                break;

                            case option2:
                                Intent it2 = new Intent(Chat.this, SharedMedia.class);
                                it2.putExtra(INTENT_KEY_USER_ID, mChattingUserId);
                                startActivity(it2);
                                break;

                            case option3:
                                allowNotifyMe(Chat.this, mChattingUserId);
                                break;

                            case option4:
                                preventNotifyMe(Chat.this, mChattingUserId);
                                break;

                            default:

                        }
                    }
                }).create().show();

    }

    private void loadLimitedMessages() {

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

        mMessageQuery.limitToLast(NUMBER_MESSAGE_PER_PAGE).addChildEventListener(mLimitedMessageChildEvent);

    }

    private void loadAllMessages() {

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
                            mMessagesAdapter.stopActionWithRemovedMessage(updatedMessage);

                        mMessageList.set(index, updatedMessage);
                        mMessagesAdapter.notifyItemChanged(index);

                        break;

                    }


                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                final Message removedMessage = dataSnapshot.getValue(Message.class);

                mMessagesAdapter.stopActionWithRemovedMessage(removedMessage);

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

        mMessageQuery.addChildEventListener(mAllMessageChildEvent);

    }

    private void markSeen(Message messageToSeen) {

        if (messageToSeen.getMessageId() != null) {

            if (!messageToSeen.getSenderId().equals(getMyFirebaseUserId()) && !isSeen(messageToSeen.getStatus())
                    && getChattingUserId() != null && getChattingUserId().equals(mChattingUserId)) {

                final long seenTime = getCurrentTimeInMilies();

                final Map<String, Object> mapSeen = new HashMap<>();

                mapSeen.put(kMessageStatus, MESSAGE_STATUS_SEEN);
                mapSeen.put(kMessageSeenTime, seenTime);

                ROOT_REF.child(CHILD_MESSAGES)
                        .child(getMyFirebaseUserId()).child(mChattingUserId)
                        .child(messageToSeen.getMessageId())
                        .updateChildren(mapSeen).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        DatabaseReference userMessageRef = ROOT_REF.child(CHILD_MESSAGES)
                                .child(mChattingUserId).child(getMyFirebaseUserId())
                                .child(messageToSeen.getMessageId());

                        userMessageRef.child(kMessageStatus).runTransaction(new Transaction.Handler() {
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
                                        userMessageRef.child(kMessageStatus).setValue(MESSAGE_STATUS_RECEIVED)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        userMessageRef.updateChildren(mapSeen);
                                                    }
                                                });
                                    } else if (currentStatus.equals(MESSAGE_STATUS_RECEIVED)) {
                                        userMessageRef.updateChildren(mapSeen);
                                    }


                                }

                            }

                        });

                    }
                });


            }

        }

    }

    private void countUnSeenMessage() {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        int unseenCounter = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            final Message message = snapshot.getValue(Message.class);

                            if (!message.getSenderId().equals(getMyFirebaseUserId())
                                    && !message.getStatus().equals(MESSAGE_STATUS_SEEN)) {

                                unseenCounter++;

                            }

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

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), mChattingUserId, null,
                messageContent, MESSAGE_STATUS_SENDING,
                sendTime, 0, messageType,
                notificationId, 0, null, false, null, null, null);

        final Message userMess = new Message(
                messageId, getMyFirebaseUserId(), mChattingUserId, null,
                messageContent, MESSAGE_STATUS_SENT,
                sendTime, 0, messageType,
                notificationId, 0, null, false, null, null, null);

        if (isConnectedToFirebaseService(Chat.this)) {
            sendTextMessageOnline(messageId, messageContent, myMess, userMess, notificationId);
        } else sendTextMessageOffline(messageId, messageContent, myMess, userMess, notificationId);


    }

    private void sendTextMessageOffline(String messageId, String messageContent, Message myMess, Message userMess, int notificationId) {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        checkHasChildBlock(mChattingUserId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                            @Override
                            public void OnCallBack(boolean hasChildBlock) {

                                if (hasChildBlock) {

                                    showMessageDialog(Chat.this, "Lỗi trong khi gửi tin nhắn");

                                } else {

                                    markMessageIsSent(messageId, mChattingUserId, null);

                                    ROOT_REF.child(CHILD_MESSAGES)
                                            .child(mChattingUserId).child(getMyFirebaseUserId())
                                            .child(messageId).setValue(userMess)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                        @Override
                                                        public void OnCallBack(User callbackUserProfile) {

                                                            sendNotificationToUser(
                                                                    mChattingUserId,
                                                                    getMyFirebaseUserId(), null,
                                                                    messageId,
                                                                    messageContent,
                                                                    callbackUserProfile.getName(),
                                                                    callbackUserProfile.getThumbAvatarUrl(),
                                                                    NOTIFICATION_TYPE_SINGLE_MESSAGE,
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

    private void sendTextMessageOnline(String messageId, String messageContent, Message myMess, Message userMess, int notificationId) {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.child(CHILD_MESSAGES)
                                .child(getMyFirebaseUserId()).child(mChattingUserId)
                                .child(messageId).child(kMessageStatus)
                                .setValue(MESSAGE_STATUS_SENT).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                ROOT_REF.child(CHILD_MESSAGES)
                                        .child(mChattingUserId)
                                        .child(getMyFirebaseUserId()).child(messageId)
                                        .setValue(userMess).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                            @Override
                                            public void OnCallBack(User callbackUserProfile) {

                                                sendNotificationToUser(
                                                        mChattingUserId,
                                                        getMyFirebaseUserId(), null,
                                                        messageId,
                                                        messageContent,
                                                        callbackUserProfile.getName(),
                                                        callbackUserProfile.getThumbAvatarUrl(),
                                                        NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                        notificationId);

                                            }
                                        });

                                    }
                                });

                            }
                        });
                    }
                });
    }

    public void sendSticker(String sticker) {

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), mChattingUserId, null,
                "Đã gửi một nhãn dán", MESSAGE_STATUS_SENDING,
                sendTime, 0, MESSAGE_TYPE_STICKER,
                notificationId, 0, sticker, false, null, null, null);

        final Message userMess = new Message(
                messageId, getMyFirebaseUserId(), mChattingUserId, null,
                "Đã gửi một nhãn dán", MESSAGE_STATUS_SENT,
                sendTime, 0, MESSAGE_TYPE_STICKER,
                notificationId, 0, sticker, false, null, null, null);


        if (isConnectedToFirebaseService(Chat.this))

            sendStickerOnline(messageId, myMess, userMess, notificationId);

        else sendStickerOffline(messageId, myMess, userMess, notificationId);


    }

    private void sendStickerOffline(String messageId, Message myMess, Message userMess, int notificationId) {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        checkHasChildBlock(mChattingUserId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                            @Override
                            public void OnCallBack(boolean hasChildBlock) {

                                if (hasChildBlock) {

                                    showMessageDialog(Chat.this, "Lỗi trong khi gửi tin nhắn");

                                } else {

                                    markMessageIsSent(messageId, mChattingUserId, null);

                                    ROOT_REF.child(CHILD_MESSAGES)
                                            .child(mChattingUserId).child(getMyFirebaseUserId())
                                            .child(messageId).setValue(userMess)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                        @Override
                                                        public void OnCallBack(User callbackUserProfile) {

                                                            sendNotificationToUser(
                                                                    mChattingUserId,
                                                                    getMyFirebaseUserId(),
                                                                    null,
                                                                    messageId,
                                                                    "Đã gửi một nhãn dán",
                                                                    callbackUserProfile.getName(),
                                                                    callbackUserProfile.getThumbAvatarUrl(),
                                                                    NOTIFICATION_TYPE_SINGLE_MESSAGE,
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

    private void sendStickerOnline(String messageId, Message myMess, Message userMess, int notificationId) {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.child(CHILD_MESSAGES)
                                .child(getMyFirebaseUserId()).child(mChattingUserId)
                                .child(messageId).child(kMessageStatus).setValue(MESSAGE_STATUS_SENT)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        ROOT_REF.child(CHILD_MESSAGES)
                                                .child(mChattingUserId).child(getMyFirebaseUserId())
                                                .child(messageId).setValue(userMess)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                            @Override
                                                            public void OnCallBack(User callbackUserProfile) {

                                                                sendNotificationToUser(
                                                                        mChattingUserId,
                                                                        getMyFirebaseUserId(), null,
                                                                        messageId,
                                                                        "Đã gửi một nhãn dán",
                                                                        callbackUserProfile.getName(),
                                                                        callbackUserProfile.getThumbAvatarUrl(),
                                                                        NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                                        notificationId);
                                                            }
                                                        });


                                                    }
                                                });

                                    }
                                });
                    }
                });
    }

    private void uploadAndSendPictures(List<Uri> uriInputList, boolean fromGallery) {

        if (fromGallery) {

            ArrayList<Uri> validList = new ArrayList<>();
            ArrayList<Uri> invalidList = new ArrayList<>();

            for (Uri uri : uriInputList) {
                if (isValidImage(Chat.this, uri)) {
                    validList.add(uri);
                } else invalidList.add(uri);
            }

            if (invalidList.size() > 0) {
                showMessageDialog(Chat.this,
                        "Không thể gửi " + invalidList.size() + " ảnh có kích thước không hợp lệ !");
            }

            if (validList.size() > 0) {

                showSendingMediaView();

                Map<String, Object> mapToSend = new HashMap<>();
                Map<String, Object> mapFailed = new HashMap<>();

                final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                final long sendTime = getCurrentTimeInMilies();

                for (Uri uriToUpload : validList) {

                    final String imageName = generateMediaName(MEDIA_TYPE_PICTURE);

                    uploadImages(imageName, uriToUpload, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                final Media picture = new Media(
                                        mediaId, messageId, getMyFirebaseUserId(), mChattingUserId, null,
                                        imageName,
                                        callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "",
                                        callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "",
                                        MEDIA_TYPE_PICTURE, 0, sendTime);

                                mapToSend.put(mediaId, picture);

                            } else mapFailed.put(mediaId, "");

                            if (mapToSend.size() + mapFailed.size() == validList.size()) {

                                hideSendingMediaView();

                                if (mapFailed.size() > 0) {
                                    showLongToast(Chat.this, "Tải " + mapFailed.size() + " ảnh lên không thành công !");
                                }

                                if (mapToSend.size() > 0) {
                                    sendMedia(messageId, mapToSend, MEDIA_TYPE_PICTURE);
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

                    final String imageName = generateMediaName(MEDIA_TYPE_PICTURE);

                    uploadImages(imageName, uriToUpload, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            hideSendingMediaView();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                Map<String, Object> mapToSend = new HashMap<>();

                                final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                                final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                                final long sendTime = getCurrentTimeInMilies();

                                final Media picture = new Media(
                                        mediaId, messageId, getMyFirebaseUserId(), mChattingUserId, null,
                                        imageName,
                                        callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "",
                                        callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "",
                                        MEDIA_TYPE_PICTURE, 0, sendTime);

                                mapToSend.put(mediaId, picture);

                                sendMedia(messageId, mapToSend, MEDIA_TYPE_PICTURE);

                            } else showLongToast(Chat.this, "Tải ảnh lên không thành công !");


                        }
                    });

                }
            }

        }


    }

    private void uploadAndSendVideos(List<Uri> uriInputList, boolean fromGallery) {

        if (fromGallery) {

            ArrayList<Uri> validList = new ArrayList<>();
            ArrayList<Uri> invalidList = new ArrayList<>();

            for (Uri uri : uriInputList) {
                if (isValidVideo(Chat.this, uri)) {
                    validList.add(uri);
                } else invalidList.add(uri);
            }

            if (invalidList.size() > 0) {
                showMessageDialog(Chat.this,
                        "Không thể gửi " + invalidList.size() + " video có kích thước lớn hơn " + VIDEO_MAX_SIZE_IN_MB + "MB hoặc bằng 0!");

            }

            if (validList.size() > 0) {

                showSendingMediaView();

                Map<String, Object> mapToSend = new HashMap<>();
                Map<String, Object> mapFailed = new HashMap<>();

                final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                final long sendTime = getCurrentTimeInMilies();

                for (Uri uriToSend : validList) {

                    String videoName = generateMediaName(MEDIA_TYPE_VIDEO);

                    uploadVideos(videoName, uriToSend, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                Media video = new Media(
                                        mediaId, messageId, getMyFirebaseUserId(), mChattingUserId, null,
                                        videoName,
                                        callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "",
                                        callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "",
                                        MEDIA_TYPE_VIDEO, 0, sendTime);

                                mapToSend.put(mediaId, video);

                            } else mapFailed.put(mediaId, "");

                            if (mapToSend.size() + mapFailed.size() == validList.size()) {

                                hideSendingMediaView();

                                if (mapFailed.size() > 0) {
                                    showMessageDialog(Chat.this, "Tải " + mapFailed.size() + " video lên không thành công");
                                }

                                if (mapToSend.size() > 0) {
                                    sendMedia(messageId, mapToSend, MEDIA_TYPE_VIDEO);
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

                    String videoName = generateMediaName(MEDIA_TYPE_VIDEO);

                    uploadVideos(videoName, uriToSend, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                        @Override
                        public void OnCallBack(Map<String, Uri> callbackUrl) {

                            hideSendingMediaView();

                            if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                                Map<String, Object> mapToSend = new HashMap<>();

                                final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                                final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                                final long sendTime = getCurrentTimeInMilies();

                                Media video = new Media(
                                        mediaId, messageId, getMyFirebaseUserId(), mChattingUserId, null,
                                        videoName,
                                        callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "",
                                        callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "",
                                        MEDIA_TYPE_VIDEO, 0, sendTime);

                                mapToSend.put(mediaId, video);

                                sendMedia(messageId, mapToSend, MEDIA_TYPE_VIDEO);

                            } else showMessageDialog(Chat.this, "Tải video lên không thành công");

                        }
                    });
                }
            }
        }

    }

    @Override
    protected void uploadAndSendAudio(String audioFilePath, int audioDuration) {

        showSendingAudioView();

        String audioName = generateMediaName(MEDIA_TYPE_AUDIO);

        Uri audioUri = Uri.fromFile(new File(audioFilePath));

        uploadAudio(audioName, audioUri, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
            @Override
            public void OnCallBack(Map<String, Uri> callbackUrl) {

                hideSendingAudioView();

                if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                    final String mediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

                    final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

                    final long sendTime = getCurrentTimeInMilies();

                    Media audio = new Media(mediaId, messageId, getMyFirebaseUserId(), mChattingUserId, null,
                            audioName,
                            callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "",
                            callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "",
                            MEDIA_TYPE_AUDIO, audioDuration, sendTime);

                    final Map<String, Object> mapToSend = new HashMap<>();

                    mapToSend.put(mediaId, audio);

                    sendMedia(messageId, mapToSend, MEDIA_TYPE_AUDIO);

                } else showMessageDialog(Chat.this, "Tải lên đoạn ghi âm không thành công !");

            }
        });
    }

    private void sendMedia(String messageId, Map<String, Object> mediaInputMap, int elementType) {

        final int notificationId = getNotificationsId();
        final long sendTime = getCurrentTimeInMilies();

        String messageContent;

        if (elementType == MEDIA_TYPE_PICTURE) messageContent = "Đã gửi " + mediaInputMap.size() + " ảnh";
        else if (elementType == MEDIA_TYPE_VIDEO) messageContent = "Đã gửi " + mediaInputMap.size() + " video";
        else messageContent = "Đã gửi " + mediaInputMap.size() + " tin nhắn thoại";

        int messageType = -1;

        if (elementType == MEDIA_TYPE_AUDIO) {
            messageType = MESSAGE_TYPE_AUDIO;
        } else if (mediaInputMap.size() > 1) {
            messageType = MESSAGE_TYPE_MULTIPLE_MEDIA;
        } else if (elementType == MEDIA_TYPE_PICTURE) {
            messageType = MESSAGE_TYPE_PICTURE;
        } else if (elementType == MEDIA_TYPE_VIDEO) {
            messageType = MESSAGE_TYPE_VIDEO;
        }

        final Message myMessage = new Message(
                messageId, getMyFirebaseUserId(), mChattingUserId, null,
                messageContent, MESSAGE_STATUS_SENDING,
                sendTime, 0, messageType,
                notificationId, 0, null, false, null, null, null);

        final Message userMessage = new Message(
                messageId, getMyFirebaseUserId(), mChattingUserId, null,
                messageContent, MESSAGE_STATUS_SENT,
                sendTime, 0, messageType,
                notificationId, 0, null, false, null, null, null);

        final Map<String, Object> myMapToSend = new HashMap<>();
        final Map<String, Object> userMapToSend = new HashMap<>();

        for (String k : mediaInputMap.keySet()) {
            myMapToSend.put("/" + CHILD_MEDIA + "/" + getMyFirebaseUserId() + "/" + mChattingUserId + "/" + k, mediaInputMap.get(k));
            userMapToSend.put("/" + CHILD_MEDIA + "/" + mChattingUserId + "/" + getMyFirebaseUserId() + "/" + k, mediaInputMap.get(k));
        }

        myMapToSend.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + mChattingUserId + "/" + messageId, myMessage);
        userMapToSend.put("/" + CHILD_MESSAGES + "/" + mChattingUserId + "/" + getMyFirebaseUserId() + "/" + messageId, userMessage);

        ROOT_REF.updateChildren(myMapToSend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                checkHasChildBlock(mChattingUserId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                    @Override
                    public void OnCallBack(boolean hasChildBlock) {

                        if (hasChildBlock) {

                            showMessageDialog(Chat.this, "Lỗi trong khi gửi tin nhắn !");

                        } else {

                            markMessageIsSent(messageId, mChattingUserId, null);

                            ROOT_REF.updateChildren(userMapToSend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                        @Override
                                        public void OnCallBack(User callbackUserProfile) {

                                            sendNotificationToUser(
                                                    mChattingUserId,
                                                    getMyFirebaseUserId(),
                                                    null,
                                                    messageId,
                                                    messageContent,
                                                    callbackUserProfile.getName(),
                                                    callbackUserProfile.getThumbAvatarUrl(),
                                                    NOTIFICATION_TYPE_SINGLE_MESSAGE,
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

    private void uploadVideos(String videoName, Uri videoUri, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBack) {

        final StorageReference videoRef = ROOT_STORAGE
                .child(STORAGE_SINGLE_MEDIA)
                .child(STORAGE_VIDEO)
                .child(getMyFirebaseUserId())
                .child(mChattingUserId)
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
                .child(STORAGE_SINGLE_MEDIA)
                .child(STORAGE_FULL_SIZE_IMAGES)
                .child(getMyFirebaseUserId())
                .child(mChattingUserId)
                .child(imageName);

        final StorageReference thumbRef = ROOT_STORAGE
                .child(STORAGE_SINGLE_MEDIA)
                .child(STORAGE_THUMB_IMAGES)
                .child(getMyFirebaseUserId())
                .child(mChattingUserId)
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

                UploadTask thumbUploadTask = thumbRef.putBytes(getThumbnail(Chat.this, imageUri, 40));

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
                .child(STORAGE_SINGLE_MEDIA)
                .child(STORAGE_AUDIO)
                .child(getMyFirebaseUserId())
                .child(mChattingUserId)
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

                    startDownloadingMedia(Chat.this, mediaToDownload);

                else showDownloadMediaRequestPermissionDialog(Chat.this);

                break;

            case RECORD_AUDIO_CODE:

                if (isAllPermissionsGrantedInResult(grantResults))

                    showRecordAudioDialog();

                else showRecordAudioRequestPermissionDialog(Chat.this);

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

        if (isConnectedToFirebaseService(Chat.this)) {

            checkHasChildBlock(mChattingUserId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                @Override
                public void OnCallBack(boolean hasChildBlock) {

                    if (hasChildBlock) {

                        showLongToast(Chat.this, "Lỗi trong khi gửi tin nhắn");

                    } else {

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

                    }
                }
            });

        } else showNoConnectionDialog(Chat.this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        removeAllFirebaseListener();

        if (mChattingUserId != null) {

            setChattingUserId(mChattingUserId);

            mRelationshipRef = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).child(mChattingUserId);

            mSettingRef = ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId()).child(mChattingUserId).child(CHILD_MUTE_NOTIFICATIONS);

            mUserActiveRef = ROOT_REF.child(CHILD_USERS).child(mChattingUserId).child(kLastSeen);

            mTypingRef = ROOT_REF.child(CHILD_TYPING_MESSAGE).child(getMyFirebaseUserId()).child(mChattingUserId).child(CHILD_TYPING);


            mTypingVaLueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    tv_typing.setVisibility(dataSnapshot.exists() ? View.VISIBLE : View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mUserActiveValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final long offlineTime = dataSnapshot.getValue(Long.class);

                    if (offlineTime == -1) {

                        tv_sub_infor.setVisibility(View.VISIBLE);
                        tv_sub_infor.setText("Đang hoạt động");

                    } else {

                        final String value = formatTimeAgoDetail(offlineTime);

                        tv_sub_infor.setText(value);
                        tv_sub_infor.setVisibility(value != null ? View.VISIBLE : View.GONE);

                    }
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

            mRelationshipValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(CHILD_USER_BLOCKED_ME) || dataSnapshot.hasChild(CHILD_I_BLOCKED_USER)) {

                        hideAudioRecorder();

                        hideEmoji();

                        hideKeyboard(Chat.this);

                        if (dataSnapshot.hasChild(CHILD_USER_BLOCKED_ME)) {

                            tv_lock_content.setText("Bạn tạm thời không thể trả lời cuộc trò chuyện này.");
                            btn_unlock.setVisibility(View.GONE);
                            btn_lock_message.setVisibility(View.GONE);

                        } else if (dataSnapshot.hasChild(CHILD_I_BLOCKED_USER)) {

                            tv_lock_content.setText("Bạn đã chặn người dùng này.");
                            btn_unlock.setVisibility(View.VISIBLE);
                            btn_lock_message.setVisibility(View.VISIBLE);

                        }

                        linear_block.setVisibility(View.VISIBLE);
                        linear_send_messages.setVisibility(View.GONE);
                        tv_typing.setVisibility(View.GONE);

                        tv_sub_infor.setText(null);
                        tv_sub_infor.setVisibility(View.GONE);

                        if (mSendMediaOptionsDialog != null && mSendMediaOptionsDialog.isShowing()) {
                            mSendMediaOptionsDialog.dismiss();
                        }

                    } else {

                        linear_send_messages.setVisibility(View.VISIBLE);
                        linear_block.setVisibility(View.GONE);
                        btn_unlock.setVisibility(View.GONE);
                        btn_lock_message.setVisibility(View.GONE);

                        mUserActiveRef.addValueEventListener(mUserActiveValueEvent);
                        mTypingRef.addValueEventListener(mTypingVaLueEvent);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };


            mSettingRef.addValueEventListener(mSettingValueEvent);

            mRelationshipRef.addValueEventListener(mRelationshipValueEvent);

            if (mMessageList.size() > 0) {

                if (mLinearLayoutManager != null) {

                    final int posFirst = mLinearLayoutManager.findFirstVisibleItemPosition();
                    final int posLast = mLinearLayoutManager.findLastVisibleItemPosition();

                    if (posFirst >= 0 && posLast >= 0 && posFirst < mMessageList.size() && posLast < mMessageList.size()) {
                        for (int index = posFirst; index <= posLast; index++) {
                            markSeen(mMessageList.get(index));
                        }
                    }

                }

            }
        }

    }

    private void removeAllFirebaseListener() {

        if (mRelationshipValueEvent != null && mRelationshipRef != null) mRelationshipRef.removeEventListener(mRelationshipValueEvent);

        if (mSettingValueEvent != null && mSettingRef != null) mSettingRef.removeEventListener(mSettingValueEvent);

        if (mUserActiveValueEvent != null && mUserActiveRef != null) mUserActiveRef.removeEventListener(mUserActiveValueEvent);

        if (mTypingVaLueEvent != null && mTypingRef != null) mTypingRef.removeEventListener(mTypingVaLueEvent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAllMessageChildEvent != null && mMessageQuery != null) mMessageQuery.removeEventListener(mAllMessageChildEvent);

        if (mLimitedMessageChildEvent != null && mMessageQuery != null)
            mMessageQuery.limitToLast(NUMBER_MESSAGE_PER_PAGE).removeEventListener(mLimitedMessageChildEvent);


    }

    @Override
    protected void onStop() {
        super.onStop();

        hideAudioRecorder();

        if (mMessagesAdapter != null) mMessagesAdapter.releaseMediaPlayer();

        removeAllFirebaseListener();

    }

    private void checkToLoadMore() {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
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

    @Override
    protected void handleLoadMore(android.os.Message msg) {

        linear_loadmore.setVisibility(View.GONE);

        mMessagesAdapter.loadMoreMessages((ArrayList<Message>) msg.obj);

        isLoadingMore = false;
    }

    @Override
    protected void getLoadMoreItem(BaseChatActivity.FirebaseCallBackMessageList firebaseCallBackMessageList) {
        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
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

    public void downloadMediaFromActivity(Media media) {

        mediaToDownload = media;

        if (Build.VERSION.SDK_INT >= 23) {

            if (isDownloadMediaPermissionsGranted(Chat.this)) {

                startDownloadingMedia(Chat.this, mediaToDownload);

            } else ActivityCompat.requestPermissions(Chat.this, PERMISSIONS_RECORD_AUDIO, RECORD_AUDIO_CODE);

        } else startDownloadingMedia(Chat.this, mediaToDownload);

    }

}
