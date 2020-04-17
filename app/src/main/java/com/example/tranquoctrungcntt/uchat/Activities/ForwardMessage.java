package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.PickedUserAdapter;
import com.example.tranquoctrungcntt.uchat.ActivityAdapters.UserMultiplePickAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_MEDIA_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_MESSAGE_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_REMOVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_SINGLE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.SEARCH_DELAY_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMatchingUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setMediaUrlToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showKeyboard;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class ForwardMessage extends BaseActivity {

    private final Handler mSearchHandler = new Handler();

    private Toolbar mToolbar;

    private LinearLayout nonactive_searchbar;

    private AppBarLayout active_searchbar;
    private AppBarLayout appBarLayout;

    private TextView tv_title;
    private EditText edt_search;

    private RecyclerView rv_friends;
    private RecyclerView rv_picked;

    private ArrayList<User> mFriendList;
    private ArrayList<User> mPickedList;
    private ArrayList<User> mSearchList;

    private UserMultiplePickAdapter mSearchAdapter;
    private UserMultiplePickAdapter mFriendAdapter;
    private PickedUserAdapter mPickedAdapter;

    private Map<String, Boolean> mCheckBoxMap;

    private FrameLayout btn_clear_keyword;
    private FrameLayout btn_back;

    private boolean isSearching;

    private ProgressBar pb_loading;

    private FrameLayout frame_preview;
    private RoundedImageView riv_preview;
    private ImageView btn_play_video;

    private String mMessageId;
    private String mUserIdOrGroupId;
    private String mMediaId;

    private Message mMessage;
    private Media mMedia;

    private DatabaseReference mMessageRef;
    private DatabaseReference mMediaRef;
    private Query mFriendQuery;

    private ValueEventListener mMessageValueEvent;
    private ValueEventListener mMediaValueEvent;
    private ChildEventListener mFriendChildEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_message);

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);

        if (mMediaValueEvent != null && mMediaRef != null) mMediaRef.removeEventListener(mMediaValueEvent);

        if (mMessageValueEvent != null && mMessageRef != null) mMessageRef.removeEventListener(mMessageValueEvent);

        initViews();

        initClickEvents();

        mFriendQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_FRIEND).equalTo(true);

        mFriendChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot snapshotFriendId, @Nullable String s) {


                getSingleUserProfile(snapshotFriendId.getKey(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User callbackUserProfile) {

                        if (callbackUserProfile != null) {

                            mFriendList.add(callbackUserProfile);
                            mFriendAdapter.notifyItemInserted(mFriendList.size() - 1);

                        }


                    }
                });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                mCheckBoxMap.remove(dataSnapshot.getKey());

                final int i = searchUser(mSearchList, dataSnapshot.getKey());
                if (hasItemInList(i)) {
                    mSearchList.remove(i);
                    mSearchAdapter.notifyItemRemoved(i);
                    mSearchAdapter.notifyItemRangeChanged(i, mSearchAdapter.getItemCount());
                }

                final int j = searchUser(mFriendList, dataSnapshot.getKey());
                if (hasItemInList(j)) {
                    mFriendList.remove(j);
                    mFriendAdapter.notifyItemRemoved(j);
                    mFriendAdapter.notifyItemRangeChanged(j, mFriendAdapter.getItemCount());
                }

                final int k = searchUser(mPickedList, dataSnapshot.getKey());
                if (hasItemInList(k)) {
                    mPickedList.remove(k);
                    mPickedAdapter.notifyItemRemoved(k);
                    mPickedAdapter.notifyItemRangeChanged(k, mPickedAdapter.getItemCount() - k);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mFriendQuery.addChildEventListener(mFriendChildEvent);


        if (getDataFromIntent(ForwardMessage.this, INTENT_KEY_USER_ID) != null)

            mUserIdOrGroupId = (String) getDataFromIntent(ForwardMessage.this, INTENT_KEY_USER_ID);

        else if (getDataFromIntent(ForwardMessage.this, INTENT_KEY_GROUP_ID) != null)

            mUserIdOrGroupId = (String) getDataFromIntent(ForwardMessage.this, INTENT_KEY_GROUP_ID);

        else mUserIdOrGroupId = null;


        if (mUserIdOrGroupId != null) {

            mMediaId = (String) getDataFromIntent(ForwardMessage.this, INTENT_KEY_MEDIA_ID);

            mMessageId = (String) getDataFromIntent(ForwardMessage.this, INTENT_KEY_MESSAGE_ID);

            if (mMessageId != null) {

                mMessageRef = ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mUserIdOrGroupId).child(mMessageId);

                messageMode();

            } else if (mMediaId != null) {

                mMediaRef = ROOT_REF.child(CHILD_MEDIA).child(getMyFirebaseUserId()).child(mUserIdOrGroupId).child(mMediaId);

                mediaMode();

            } else finish();

        } else finish();
    }

    private void initClickEvents() {


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disableSearchMode();
            }
        });

        nonactive_searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enableSearchMode();
            }
        });

        btn_clear_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edt_search.setText(null);

            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {


                mSearchHandler.removeCallbacksAndMessages(null);

                String keyword = edt_search.getText().toString();

                if (keyword.length() > 0) {
                    //show progressbar
                    pb_loading.setVisibility(View.VISIBLE);

                    rv_friends.setVisibility(View.GONE);
                    //show clear button
                    btn_clear_keyword.setVisibility(View.VISIBLE);

                    mSearchHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //load data
                            mSearchList.clear();

                            for (User user : mFriendList)
                                if (isMatchingUser(keyword, user)) mSearchList.add(user);

                            mSearchAdapter.notifyDataSetChanged();
                            //hide progressbar
                            pb_loading.setVisibility(View.GONE);

                            rv_friends.setVisibility(View.VISIBLE);

                        }
                    }, SEARCH_DELAY_TIME);


                } else {

                    btn_clear_keyword.setVisibility(View.INVISIBLE);
                    // recovery friend list
                    mSearchList.clear();

                    mSearchList.addAll(mFriendList);

                    mSearchAdapter.notifyDataSetChanged();
                    //hide progressbar
                    pb_loading.setVisibility(View.GONE);

                    rv_friends.setVisibility(View.VISIBLE);

                }


            }
        });


    }

    private void enableSearchMode() {

        isSearching = true;
        //show search bar
        active_searchbar.setVisibility(View.VISIBLE);

        appBarLayout.setVisibility(View.GONE);

        nonactive_searchbar.setVisibility(View.GONE);

        rv_picked.setVisibility(View.GONE);

        tv_title.setVisibility(View.GONE);

        if (mMedia != null && mMedia.getType() != MEDIA_TYPE_AUDIO) frame_preview.setVisibility(View.GONE);

        //set search adapter
        mSearchList.clear();

        mSearchList.addAll(mFriendList);

        mSearchAdapter.notifyDataSetChanged();

        rv_friends.setAdapter(mSearchAdapter);

        edt_search.requestFocus();

        showKeyboard(ForwardMessage.this);

        //remove handler
        mSearchHandler.removeCallbacksAndMessages(null);
    }

    private void addPickedUser(User clickedUser) {
        mPickedList.add(clickedUser);
        mPickedAdapter.notifyItemInserted(mPickedList.size() - 1);
        rv_picked.getLayoutManager().scrollToPosition(mPickedList.size() - 1);
    }

    private void removePickedUser(int index) {
        mPickedList.remove(index);
        mPickedAdapter.notifyItemRemoved(index);
        mPickedAdapter.notifyItemRangeChanged(index, mPickedAdapter.getItemCount() - index);
    }

    private void disableSearchMode() {

        isSearching = false;
//hide keyboard
        hideKeyboard(ForwardMessage.this);

        edt_search.setText(null);

        edt_search.clearFocus();
        //view nonactive search bar
        active_searchbar.setVisibility(View.GONE);

        appBarLayout.setVisibility(View.VISIBLE);

        nonactive_searchbar.setVisibility(View.VISIBLE);

        // set friend adapter

        tv_title.setVisibility(View.VISIBLE);

        mSearchList.clear();

        mSearchAdapter.notifyDataSetChanged();

        rv_friends.setAdapter(mFriendAdapter);

        rv_friends.setVisibility(View.VISIBLE);

        rv_picked.setVisibility(View.VISIBLE);

        if (mMedia != null && mMedia.getType() != MEDIA_TYPE_AUDIO) frame_preview.setVisibility(View.VISIBLE);

        pb_loading.setVisibility(View.GONE);

        mSearchHandler.removeCallbacksAndMessages(null);
    }


    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chuyển tiếp");

        mCheckBoxMap = new HashMap<>();

        mFriendList = new ArrayList<>();
        mPickedList = new ArrayList<>();
        mSearchList = new ArrayList<>();

        mFriendAdapter = new UserMultiplePickAdapter(ForwardMessage.this, mFriendList, mCheckBoxMap, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final User clickedUser = mFriendList.get(position);

                if (mCheckBoxMap.get(clickedUser.getUserId()) == null) {

                    mCheckBoxMap.put(clickedUser.getUserId(), true);

                    addPickedUser(clickedUser);

                } else {

                    mCheckBoxMap.remove(clickedUser.getUserId());

                    final int i = searchUser(mPickedList, clickedUser.getUserId());
                    if (hasItemInList(i)) removePickedUser(i);

                }

                mFriendAdapter.notifyItemChanged(position);

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });
        mPickedAdapter = new PickedUserAdapter(ForwardMessage.this, mPickedList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                String clickedUserId = mPickedList.get(position).getUserId();

                mCheckBoxMap.remove(clickedUserId);

                final int i = searchUser(mFriendList, clickedUserId);
                if (hasItemInList(i)) mFriendAdapter.notifyItemChanged(i);

                final int j = searchUser(mSearchList, clickedUserId);
                if (hasItemInList(j)) mSearchAdapter.notifyItemChanged(j);

                removePickedUser(position);


            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });
        mSearchAdapter = new UserMultiplePickAdapter(ForwardMessage.this, mSearchList, mCheckBoxMap, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                User clickedUser = mSearchList.get(position);

                if (mCheckBoxMap.get(clickedUser.getUserId()) == null) {

                    mCheckBoxMap.put(clickedUser.getUserId(), true);

                    addPickedUser(clickedUser);

                    final int i = searchUser(mFriendList, clickedUser.getUserId());
                    if (hasItemInList(i)) mFriendAdapter.notifyItemChanged(i);

                } else {

                    mCheckBoxMap.remove(clickedUser.getUserId());

                    final int i = searchUser(mPickedList, clickedUser.getUserId());
                    if (hasItemInList(i)) removePickedUser(i);

                    final int j = searchUser(mFriendList, clickedUser.getUserId());
                    if (hasItemInList(j)) mFriendAdapter.notifyItemChanged(j);

                }

                mSearchAdapter.notifyItemChanged(position);

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });

        rv_friends = (RecyclerView) findViewById(R.id.rv_forward_message);
        rv_friends.setHasFixedSize(true);
        rv_friends.setLayoutManager(new LinearLayoutManager(ForwardMessage.this, RecyclerView.VERTICAL, false));
        rv_friends.setAdapter(mFriendAdapter);
        rv_friends.setItemAnimator(null);

        rv_picked = (RecyclerView) findViewById(R.id.rv_picked_users);
        rv_picked.setLayoutManager(new LinearLayoutManager(ForwardMessage.this, RecyclerView.HORIZONTAL, false));
        rv_picked.setAdapter(mPickedAdapter);
        rv_picked.setItemAnimator(null);


        riv_preview = (RoundedImageView) findViewById(R.id.riv_preview);
        frame_preview = (FrameLayout) findViewById(R.id.frame_preview);
        btn_play_video = (ImageView) findViewById(R.id.img_play_video);

        tv_title = (TextView) findViewById(R.id.tv_title);

        initSearchViews();

    }

    private void initSearchViews() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        nonactive_searchbar = (LinearLayout) findViewById(R.id.nonactive_searchbar);

        active_searchbar = (AppBarLayout) findViewById(R.id.active_searchbar);

        edt_search = (EditText) findViewById(R.id.edt_search);

        btn_clear_keyword = (FrameLayout) findViewById(R.id.frame_clear_keyword);

        btn_back = (FrameLayout) findViewById(R.id.frame_back);

        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

        isSearching = false;
    }

    private void messageMode() {

        mMessageValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    mMessage = dataSnapshot.getValue(Message.class);

                    if (mMessage.getType() == MESSAGE_TYPE_REMOVED) showMessageIsRemovedDialog();

                } else showMessageIsRemovedDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMessageRef.addValueEventListener(mMessageValueEvent);
    }

    private void mediaMode() {

        mMediaValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    mMedia = dataSnapshot.getValue(Media.class);

                    switch (mMedia.getType()) {

                        case MEDIA_TYPE_VIDEO:

                            btn_play_video.setVisibility(View.VISIBLE);
                            frame_preview.setVisibility(View.VISIBLE);

                            setMediaUrlToView(ForwardMessage.this, mMedia.getThumbContentUrl(), riv_preview);

                            break;

                        case MEDIA_TYPE_PICTURE:

                            btn_play_video.setVisibility(View.GONE);
                            frame_preview.setVisibility(View.VISIBLE);

                            setMediaUrlToView(ForwardMessage.this, mMedia.getThumbContentUrl(), riv_preview);

                            break;

                        case MEDIA_TYPE_AUDIO:

                            frame_preview.setVisibility(View.GONE);

                            break;

                    }

                } else showMessageIsRemovedDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMediaRef.addValueEventListener(mMediaValueEvent);

    }

    private void forwardTextMessage(Message messageToForward) {


        final String forwardMessageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Map<String, Object> myMessageMap = new HashMap<>();

        final Map<String, Object> myMessageStatusMap = new HashMap<>();

        final Map<String, Object> userMessageMap = new HashMap<>();


        for (User pickedUser : mPickedList) {

            final Message myMessage = new Message(
                    forwardMessageId, getMyFirebaseUserId(), pickedUser.getUserId(), null,
                    messageToForward.getContent(), MESSAGE_STATUS_SENDING,
                    sendTime, 0, messageToForward.getType(),
                    notificationId, 0, null, true, null, null, null);

            final Message userMessage = new Message(
                    forwardMessageId, getMyFirebaseUserId(), pickedUser.getUserId(), null,
                    messageToForward.getContent(), MESSAGE_STATUS_SENT,
                    sendTime, 0, messageToForward.getType(),
                    notificationId, 0, null, true, null, null, null);

            myMessageMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + pickedUser.getUserId() + "/" + forwardMessageId, myMessage);

            userMessageMap.put("/" + CHILD_MESSAGES + "/" + pickedUser.getUserId() + "/" + getMyFirebaseUserId() + "/" + forwardMessageId, userMessage);

            myMessageStatusMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + pickedUser.getUserId() + "/" + forwardMessageId + "/" + kMessageStatus, MESSAGE_STATUS_SENT);

        }

        ROOT_REF.updateChildren(myMessageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                ROOT_REF.updateChildren(myMessageStatusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.updateChildren(userMessageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                    @Override
                                    public void OnCallBack(User callbackUserProfile) {

                                        for (User pickedUser : mPickedList) {

                                            sendNotificationToUser(
                                                    pickedUser.getUserId(),
                                                    getMyFirebaseUserId(),
                                                    null,
                                                    forwardMessageId,
                                                    messageToForward.getContent(),
                                                    callbackUserProfile.getName(),
                                                    callbackUserProfile.getThumbAvatarUrl(),
                                                    NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                    notificationId);

                                        }

                                    }
                                });

                            }
                        });

                    }


                });
            }
        });


    }

    private void forwardMedia(Media mediaToForward) {

        final String forwardMessageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final String forwardMediaId = ROOT_REF.child(CHILD_MEDIA).push().getKey();

        final long sendTime = getCurrentTimeInMilies();

        final int notificationId = getNotificationsId();

        final int messageType = convertToMessageType(mediaToForward.getType());

        final String messageContent = getForwardContent(mediaToForward.getType());

        final Map<String, Object> myMapForward = new HashMap<>();

        final Map<String, Object> userMapForward = new HashMap<>();

        final Map<String, Object> mapMyMessageStatus = new HashMap<>();

        for (User pickedUser : mPickedList) {

            final Media media = new Media(
                    forwardMediaId, forwardMessageId, getMyFirebaseUserId(), pickedUser.getUserId(), null,
                    mediaToForward.getMediaName(), mediaToForward.getContentUrl(), mediaToForward.getThumbContentUrl()
                    , mediaToForward.getType(), mediaToForward.getDuration(), sendTime);

            final Message myMessage = new Message(
                    forwardMessageId, getMyFirebaseUserId(), pickedUser.getUserId(), null,
                    messageContent, MESSAGE_STATUS_SENDING,
                    sendTime, 0, messageType,
                    notificationId, 0, null, true, null, null, null);

            final Message userMess = new Message(
                    forwardMessageId, getMyFirebaseUserId(), pickedUser.getUserId(), null,
                    messageContent, MESSAGE_STATUS_SENT,
                    sendTime, 0, messageType,
                    notificationId, 0, null, true, null, null, null);

            myMapForward.put("/" + CHILD_MEDIA + "/" + getMyFirebaseUserId() + "/" + pickedUser.getUserId() + "/" + forwardMediaId, media);
            myMapForward.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + pickedUser.getUserId() + "/" + forwardMessageId, myMessage);

            userMapForward.put("/" + CHILD_MEDIA + "/" + pickedUser.getUserId() + "/" + getMyFirebaseUserId() + "/" + forwardMediaId, media);
            userMapForward.put("/" + CHILD_MESSAGES + "/" + pickedUser.getUserId() + "/" + getMyFirebaseUserId() + "/" + forwardMessageId, userMess);

            mapMyMessageStatus.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + pickedUser.getUserId() + "/" + forwardMessageId + "/" + kMessageStatus, MESSAGE_STATUS_SENT);

        }


        ROOT_REF.updateChildren(myMapForward)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.updateChildren(mapMyMessageStatus)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        ROOT_REF.updateChildren(userMapForward)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                            @Override
                                                            public void OnCallBack(User callbackUserProfile) {

                                                                for (User pickedUser : mPickedList) {

                                                                    sendNotificationToUser(
                                                                            pickedUser.getUserId(),
                                                                            getMyFirebaseUserId(),
                                                                            null,
                                                                            forwardMessageId,
                                                                            messageContent,
                                                                            callbackUserProfile.getName(),
                                                                            callbackUserProfile.getThumbAvatarUrl(),
                                                                            NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                                            notificationId
                                                                    );

                                                                }

                                                            }
                                                        });

                                                    }
                                                });

                                    }

                                });
                    }

                });


    }

    private String getForwardContent(int type) {

        if (type == MEDIA_TYPE_VIDEO)
            return "Đã chuyển tiếp 1 video.";
        else if (type == MEDIA_TYPE_PICTURE)
            return "Đã chuyển tiếp 1 ảnh.";
        else if (type == MEDIA_TYPE_AUDIO)
            return "Đã chuyển tiếp 1 đoạn ghi âm.";

        return null;
    }

    private int convertToMessageType(int type) {
        switch (type) {

            case MEDIA_TYPE_AUDIO:
                return MESSAGE_TYPE_AUDIO;

            case MEDIA_TYPE_PICTURE:
                return MESSAGE_TYPE_PICTURE;

            case MEDIA_TYPE_VIDEO:
                return MESSAGE_TYPE_VIDEO;
        }

        return -1;
    }

    private void showMessageIsRemovedDialog() {

        new AlertDialog.Builder(ForwardMessage.this)
                .setMessage("Tin nhắn đã được thu hồi !")
                .setPositiveButton("Đã hiểu", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                }).create().show();

    }

    private void showCancelCorfirmDialog() {

        new AlertDialog.Builder(ForwardMessage.this)
                .setMessage("Bạn có chắc chắn muốn huỷ chuyển tiếp không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("Không", null)
                .create().show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSearchHandler != null) mSearchHandler.removeCallbacksAndMessages(null);

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);

        if (mMediaValueEvent != null && mMediaRef != null) mMediaRef.removeEventListener(mMediaValueEvent);

        if (mMessageValueEvent != null && mMessageRef != null) mMessageRef.removeEventListener(mMessageValueEvent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.one_button_menu, menu);
        menu.findItem(R.id.item_one_button).setTitle("CHUYỂN");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                if (!mPickedList.isEmpty()) showCancelCorfirmDialog();

                else finish();

                break;

            case R.id.item_one_button:

                if (isConnectedToFirebaseService(ForwardMessage.this)) {

                    if (!mPickedList.isEmpty()) {

                        if (mMessage != null) forwardTextMessage(mMessage);
                        else if (mMedia != null) forwardMedia(mMedia);

                    }

                    finish();

                } else showNoConnectionDialog(ForwardMessage.this);


                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (isSearching) disableSearchMode();

        else if (mPickedList.isEmpty()) super.onBackPressed();

        else showCancelCorfirmDialog();

    }


}
