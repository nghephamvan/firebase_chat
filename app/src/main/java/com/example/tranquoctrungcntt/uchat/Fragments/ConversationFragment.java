package com.example.tranquoctrungcntt.uchat.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.FragmentAdapters.ConversationAdapter;
import com.example.tranquoctrungcntt.uchat.Models.Conversation;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MUTE_NOTIFICATIONS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_CHANGE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageSendTime;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isGroupMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isSeen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupMemberSnapshot;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLastAndMiddleName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.showLeaveGroupConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkBeforeBLockUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkHasChildBlock;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.showUnlockUserConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToGroupChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.allowNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.preventNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;


/**
 * A simple {@link Fragment} subclass.
 */

public class ConversationFragment extends Fragment {


    private final Handler mRefreshHandler = new Handler();

    private RecyclerView rv_conversations;
    private ArrayList<Conversation> mConversationsList;
    private ConversationAdapter mConversationsAdapter;

    private LinearLayout linear_no_conversation;
    private MainActivity mMainActivity;

    private ChildEventListener mSettingChildEvent;
    private ChildEventListener mMessageChildEvent;
    private ChildEventListener mBlockRelationshipChildEvent;
    private ChildEventListener mBlockedRelationshipChildEvent;

    private Query mSettingQuery;
    private Query mBlockQuery;
    private Query mBlockedQuery;
    private DatabaseReference mMessageRef;

    private LinearLayoutManager mLinearLayoutManager;

    private Map<String, ValueEventListener> mConversationProfileListenerMap;
    private Map<String, Boolean> mAcceptBlockedUserMap;
    private Map<String, Boolean> mSettingMap;
    private Map<String, Boolean> mBlockMap;
    private Map<String, Boolean> mBlockedMap;

    private String mShowingOptionConversationId;
    private BottomSheetDialog optionsDialog;

    private boolean isFragmentVisible;

    private View rootView;

    public ConversationFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_conversations, container, false);

        mMainActivity = (MainActivity) getActivity();

        removeAllFirebaseListener();

        mSettingQuery = ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId()).orderByChild(CHILD_MUTE_NOTIFICATIONS).equalTo(true);

        mMessageRef = ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId());

        mBlockQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_I_BLOCKED_USER).equalTo(true);

        mBlockedQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_USER_BLOCKED_ME).equalTo(true);

        initViews();

        initOptionDialog();

        attachMessageListener();

        attachSettingsListener();

        attachBlockRelationshipListener();

        attachBlockedRelationshipListener();

        refreshList(10 * 60 * 1000);

        mMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotAllConversations) {

                if (snapshotAllConversations.exists()) {

                    linear_no_conversation.setVisibility(View.GONE);
                    rv_conversations.setVisibility(View.VISIBLE);

                } else {

                    linear_no_conversation.setVisibility(View.VISIBLE);
                    rv_conversations.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void removeAllFirebaseListener() {

        if (mSettingChildEvent != null && mSettingQuery != null) mSettingQuery.removeEventListener(mSettingChildEvent);

        if (mMessageChildEvent != null && mMessageRef != null) mMessageRef.removeEventListener(mMessageChildEvent);

        if (mBlockRelationshipChildEvent != null && mBlockQuery != null) mBlockQuery.removeEventListener(mBlockRelationshipChildEvent);

        if (mBlockedRelationshipChildEvent != null && mBlockedQuery != null) mBlockedQuery.removeEventListener(mBlockedRelationshipChildEvent);

    }

    private void refreshList(long every) {
        mRefreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!getActivity().isFinishing()) {
                    for (int index = 0; index < mConversationsList.size(); index++) {
                        if (mConversationsList.get(index).isSingleConversation()) {
                            mConversationsAdapter.notifyItemChanged(index);
                        }
                    }

                    mRefreshHandler.postDelayed(this, every);
                }

            }
        }, every);
    }

    private void initViews() {

        mShowingOptionConversationId = null;

        mConversationProfileListenerMap = new HashMap<>();
        mAcceptBlockedUserMap = new HashMap<>();
        mSettingMap = new HashMap<>();
        mBlockMap = new HashMap<>();
        mBlockedMap = new HashMap<>();

        mConversationsList = new ArrayList<>();
        mConversationsAdapter = new ConversationAdapter(getActivity(), mConversationsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                if (mConversationsList.get(position).isGroupConversation()) {

                    if (mAcceptBlockedUserMap.get(mConversationsList.get(position).getConversationId()) != null) {

                        goToGroupChat(getActivity(), mConversationsList.get(position).getGroupDetail());

                    } else {

                        ROOT_REF.child(CHILD_RELATIONSHIPS)
                                .child(getMyFirebaseUserId()).orderByChild(CHILD_I_BLOCKED_USER).equalTo(true)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot relationshipSnapshot) {

                                        getGroupMemberSnapshot(mConversationsList.get(position).getConversationId(), new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                            @Override
                                            public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                                for (DataSnapshot snapshot : groupMemberSnapshot.getChildren()) {

                                                    if (relationshipSnapshot.hasChild(snapshot.getKey())) {

                                                        showWarningDialog(mConversationsList.get(position).getGroupDetail());

                                                        return;

                                                    }
                                                }

                                                goToGroupChat(getActivity(), mConversationsList.get(position).getGroupDetail());

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                } else goToChat(getActivity(), mConversationsList.get(position).getUserProfile());

            }

            @Override
            public void OnItemLongClick(View v, int position) {

                showOptions(mConversationsList.get(position));

            }
        });

        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_conversations = (RecyclerView) rootView.findViewById(R.id.rv_conversations);
        rv_conversations.setHasFixedSize(true);
        rv_conversations.setLayoutManager(mLinearLayoutManager);
        rv_conversations.setAdapter(mConversationsAdapter);

        disableChangeAnimation(rv_conversations);

        linear_no_conversation = (LinearLayout) rootView.findViewById(R.id.linear_no_conversation);


    }

    private void attachBlockRelationshipListener() {

        mBlockRelationshipChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot blockRelationshipSnapshot, @Nullable String s) {

                mBlockMap.put(blockRelationshipSnapshot.getKey(), true);

                updateProfileWhenHasBlockRelationship(blockRelationshipSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot blockRelationshipSnapshot) {

                mBlockMap.remove(blockRelationshipSnapshot.getKey());

                updateProfileWhenRemoveBlockRelationship(blockRelationshipSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mBlockQuery.addChildEventListener(mBlockRelationshipChildEvent);

    }

    private void attachBlockedRelationshipListener() {

        mBlockedRelationshipChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot blockedRelationshipSnapshot, @Nullable String s) {

                mBlockedMap.put(blockedRelationshipSnapshot.getKey(), true);

                updateProfileWhenHasBlockRelationship(blockedRelationshipSnapshot.getKey());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot blockedRelationshipSnapshot) {

                mBlockedMap.remove(blockedRelationshipSnapshot.getKey());

                updateProfileWhenRemoveBlockRelationship(blockedRelationshipSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mBlockedQuery.addChildEventListener(mBlockedRelationshipChildEvent);

    }

    private void attachSettingsListener() {

        mSettingChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshotSetting, @Nullable String s) {

                mSettingMap.put(snapshotSetting.getKey(), true);

                for (int index = 0; index < mConversationsList.size(); index++) {

                    if (snapshotSetting.getKey().equals(mConversationsList.get(index).getConversationId())) {

                        mConversationsList.get(index).setMuteNotifications(true);
                        mConversationsAdapter.notifyItemChanged(index);

                        break;
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshotSetting) {

                mSettingMap.remove(snapshotSetting.getKey());

                for (int index = 0; index < mConversationsList.size(); index++) {

                    if (snapshotSetting.getKey().equals(mConversationsList.get(index).getConversationId())) {

                        mConversationsList.get(index).setMuteNotifications(false);
                        mConversationsAdapter.notifyItemChanged(index);

                        break;
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mSettingQuery.addChildEventListener(mSettingChildEvent);

    }

    private void attachMessageListener() {

        mMessageChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshotConversation, @Nullable String s) {

                if (linear_no_conversation.getVisibility() == View.VISIBLE
                        && rv_conversations.getVisibility() == View.GONE) {
                    linear_no_conversation.setVisibility(View.GONE);
                    rv_conversations.setVisibility(View.VISIBLE);
                }

                ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId())
                        .child(snapshotConversation.getKey()).orderByChild(kMessageSendTime)
                        .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotLastMessage) {

                        for (DataSnapshot snapshot : snapshotLastMessage.getChildren()) {

                            final Message message = snapshot.getValue(Message.class);

                            if (isGroupMessage(message)) {
                                buildGroupConversation(snapshotConversation.getKey(), message);  //conversationId = groupId
                            } else buildSingleConversation(snapshotConversation.getKey(), message);//conversationId = userId

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshotConversation, @Nullable String s) {

                ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId())
                        .child(snapshotConversation.getKey()).orderByChild(kMessageSendTime)
                        .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            final Message message = snapshot.getValue(Message.class);

                            for (int index = 0; index < mConversationsList.size(); index++) {

                                if (snapshotConversation.getKey().equals(mConversationsList.get(index).getConversationId())) {

                                    if (isGroupMessage(message)) {
                                        updateGroupConversationMessage(index, message);
                                    } else updateSingleConversationMessage(index, message);

                                    break;

                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapConversation) {

                if (mShowingOptionConversationId != null && mShowingOptionConversationId.equals(snapConversation.getKey())) {
                    if (optionsDialog != null && optionsDialog.isShowing()) {
                        optionsDialog.dismiss();
                    }
                }

                if (mAcceptBlockedUserMap.get(snapConversation.getKey()) != null) {
                    mAcceptBlockedUserMap.remove(snapConversation.getKey());
                }

                removeProfileListener(snapConversation.getKey());

                removeConversation(snapConversation.getKey());

                Map<String, Object> mapDeleteConversation = new HashMap<>();

                mapDeleteConversation.put("/" + CHILD_MEDIA + "/" + getMyFirebaseUserId() + "/" + snapConversation.getKey(), null);
                mapDeleteConversation.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + snapConversation.getKey(), null);

                ROOT_REF.updateChildren(mapDeleteConversation);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMessageRef.addChildEventListener(mMessageChildEvent);

    }

    private void buildSingleConversation(String userId, Message message) {
        ROOT_REF.child(CHILD_SETTINGS)
                .child(getMyFirebaseUserId()).child(userId)
                .child(CHILD_MUTE_NOTIFICATIONS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot settingSnapshot) {
                        checkHasChildBlock(userId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                            @Override
                            public void OnCallBack(boolean hasChildBlock) {
                                getSingleUserProfile(userId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                    @Override
                                    public void OnCallBack(User callbackUserProfile) {

                                        addSingleConversation(new Conversation(
                                                userId, callbackUserProfile,
                                                hasChildBlock,
                                                settingSnapshot.exists(),
                                                message)
                                        );

                                        addProfileListener(userId);

                                    }
                                });


                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void buildGroupConversation(String groupId, Message message) {

        ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId()).child(groupId)
                .child(CHILD_MUTE_NOTIFICATIONS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot settingSnapshot) {

                getGroupDetail(groupId, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                    @Override
                    public void OnCallBack(GroupDetail callbackGroupDetail) {

                        if (callbackGroupDetail != null) {

                            final Conversation conversation = new Conversation(
                                    groupId,
                                    callbackGroupDetail,
                                    false,
                                    settingSnapshot.exists(),
                                    message);

                            addGroupConversation(conversation);

                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addSingleConversation(Conversation conversation) {

        //process single message content

        switch (conversation.getMessage().getType()) {

            case MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VIDEO_CALL_SUCCESS:
            case MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VOICE_CALL_SUCCESS:

                conversation.getMessage().setContent(conversation.getMessage().getContent());

                break;

            default:

                if (conversation.getMessage().getSenderId().equals(getMyFirebaseUserId()))
                    conversation.getMessage().setContent("Bạn:" + " " + conversation.getMessage().getContent());


        }

        int firstPos = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int offsetTop = 0;
        if (firstPos >= 0) {
            View firstView = mLinearLayoutManager.findViewByPosition(firstPos);
            offsetTop = mLinearLayoutManager.getDecoratedTop(firstView) - mLinearLayoutManager.getTopDecorationHeight(firstView);
        }

        mConversationsList.add(conversation);
        mConversationsAdapter.notifyItemInserted(mConversationsList.size() - 1);

        mMainActivity.updateConversationBadges(countUnSeenConversation());

        if (firstPos >= 0) mLinearLayoutManager.scrollToPositionWithOffset(firstPos, offsetTop);

        sortList();

    }

    private void addGroupConversation(Conversation conversation) {

        if (conversation.getMessage().getSenderId().equals(getMyFirebaseUserId())) {

            processMyGroupMessagesContent(conversation.getMessage(), new FirebaseCallBackMessageContent() {
                @Override
                public void OnCallBack(String content) {

                    conversation.getMessage().setContent(content);

                    int firstPos = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int offsetTop = 0;
                    if (firstPos >= 0) {
                        View firstView = mLinearLayoutManager.findViewByPosition(firstPos);
                        offsetTop = mLinearLayoutManager.getDecoratedTop(firstView) - mLinearLayoutManager.getTopDecorationHeight(firstView);
                    }

                    mConversationsList.add(conversation);
                    mConversationsAdapter.notifyItemInserted(mConversationsList.size() - 1);

                    mMainActivity.updateConversationBadges(countUnSeenConversation());

                    if (firstPos >= 0) mLinearLayoutManager.scrollToPositionWithOffset(firstPos, offsetTop);

                    sortList();

                }
            });

        } else {
            processUserGroupMessagesContent(conversation.getMessage(), new FirebaseCallBackMessageContent() {
                @Override
                public void OnCallBack(String content) {

                    conversation.getMessage().setContent(content);

                    int firstPos = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int offsetTop = 0;
                    if (firstPos >= 0) {
                        View firstView = mLinearLayoutManager.findViewByPosition(firstPos);
                        offsetTop = mLinearLayoutManager.getDecoratedTop(firstView) - mLinearLayoutManager.getTopDecorationHeight(firstView);
                    }

                    mConversationsList.add(conversation);
                    mConversationsAdapter.notifyItemInserted(mConversationsList.size() - 1);

                    mMainActivity.updateConversationBadges(countUnSeenConversation());

                    if (firstPos >= 0) mLinearLayoutManager.scrollToPositionWithOffset(firstPos, offsetTop);

                    sortList();

                }
            });

        }

    }

    private void updateSingleConversationMessage(int index, Message message) {


        //process single message content

        switch (message.getType()) {

            case MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VIDEO_CALL_SUCCESS:
            case MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VOICE_CALL_SUCCESS:

                message.setContent(message.getContent());

                break;

            default:

                if (message.getSenderId().equals(getMyFirebaseUserId()))
                    message.setContent("Bạn:" + " " + message.getContent());


        }

        setUpdatedMessage(index, message);

    }

    private void updateGroupConversationMessage(int index, Message message) {

        if (message.getSenderId().equals(getMyFirebaseUserId())) {
            processMyGroupMessagesContent(message, new FirebaseCallBackMessageContent() {
                @Override
                public void OnCallBack(String content) {

                    message.setContent(content);

                    setUpdatedMessage(index, message);

                }
            });
        } else {
            processUserGroupMessagesContent(message, new FirebaseCallBackMessageContent() {
                @Override
                public void OnCallBack(String content) {

                    message.setContent(content);

                    setUpdatedMessage(index, message);

                }
            });
        }


    }

    private void removeConversation(String conversationId) {

        for (int index = 0; index < mConversationsList.size(); index++) {

            if (mConversationsList.get(index).getConversationId().equals(conversationId)) {

                mConversationsList.remove(index);
                mConversationsAdapter.notifyItemRemoved(index);
                mConversationsAdapter.notifyItemRangeChanged(index, mConversationsAdapter.getItemCount());

                mMainActivity.updateConversationBadges(countUnSeenConversation());

                if (mConversationsList.isEmpty()) {
                    linear_no_conversation.setVisibility(View.VISIBLE);
                    rv_conversations.setVisibility(View.GONE);
                }

                break;

            }

        }
    }

    private void addProfileListener(String conversationId) {

        if (mConversationProfileListenerMap.get(conversationId) == null) {

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final User updatedUser = dataSnapshot.getValue(User.class);

                    for (int index = 0; index < mConversationsList.size(); index++) {

                        if (mConversationsList.get(index).isSingleConversation()) {

                            final String conversationId = mConversationsList.get(index).getConversationId();

                            if (updatedUser.getUserId().equals(conversationId)) {

                                mConversationsList.get(index).setConversationProfile(updatedUser);
                                mConversationsAdapter.notifyItemChanged(index);

                                break;

                            }
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mConversationProfileListenerMap.put(conversationId, valueEventListener);

            ROOT_REF.child(CHILD_USERS).child(conversationId).addValueEventListener(valueEventListener);

        }

    }

    private void removeProfileListener(String conversationId) {

        if (mConversationProfileListenerMap.get(conversationId) != null) {

            ROOT_REF.child(CHILD_USERS).child(conversationId).removeEventListener(mConversationProfileListenerMap.get(conversationId));

            mConversationProfileListenerMap.put(conversationId, null);
        }

    }

    private void updateProfileWhenHasBlockRelationship(String userId) {

        for (int index = 0; index < mConversationsList.size(); index++) {

            if (mConversationsList.get(index).isSingleConversation()) {

                final String conversationId = mConversationsList.get(index).getConversationId();

                if (userId.equals(conversationId)) {

                    mConversationsList.get(index).setBlockedConversation(true);
                    mConversationsAdapter.notifyItemChanged(index);

                    break;

                }
            }
        }

    }

    private void updateProfileWhenRemoveBlockRelationship(String userId) {

        for (int index = 0; index < mConversationsList.size(); index++) {

            if (mConversationsList.get(index).isSingleConversation()) {

                final String conversationId = mConversationsList.get(index).getConversationId();

                if (userId.equals(conversationId)) {

                    addProfileListener(userId);

                    mConversationsList.get(index).setBlockedConversation(false);
                    mConversationsAdapter.notifyItemChanged(index);

                    break;

                }
            }
        }

    }

    public void updateConversationGroupProfile(GroupDetail updatedGroup) {

        for (int index = 0; index < mConversationsList.size(); index++) {

            if (mConversationsList.get(index).isGroupConversation()) {

                final String conversationId = mConversationsList.get(index).getConversationId();

                if (updatedGroup.getGroupId().equals(conversationId)) {

                    mConversationsList.get(index).setConversationProfile(updatedGroup);
                    mConversationsAdapter.notifyItemChanged(index);

                    break;

                }

            }

        }

    }

    private int countUnSeenConversation() {

        int counter = 0;

        for (Conversation conversation : mConversationsList) {
            if (!conversation.getMessage().getSenderId().equals(getMyFirebaseUserId())
                    && !isSeen(conversation.getMessage().getStatus())) {
                counter++;
            }
        }

        return counter;
    }

    private void setUpdatedMessage(int index, Message updatedMessage) {

        mConversationsList.get(index).setMessage(updatedMessage);
        mConversationsAdapter.notifyItemChanged(index);
        mMainActivity.updateConversationBadges(countUnSeenConversation());

        if (isFragmentVisible) {
            swapItems(index);
        } else sortList();

    }

    private void sortList() {


        Collections.sort(mConversationsList, Collections.reverseOrder(new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                return Long.compare(o1.getMessage().getSendTime(), o2.getMessage().getSendTime());
            }
        }));

        mConversationsAdapter.notifyItemRangeChanged(0, mConversationsAdapter.getItemCount());

    }

    private void swapItems(int toIndex) {
// figure out the position of the first visible item
        for (int i = 0; i <= toIndex; i++) {

            for (int j = i; j <= toIndex; j++) {

                long time_i = mConversationsList.get(i).getMessage().getSendTime();
                long time_j = mConversationsList.get(j).getMessage().getSendTime();

                if (time_j >= time_i) {
                    int firstPos = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int offsetTop = 0;
                    if (firstPos >= 0) {
                        View firstView = mLinearLayoutManager.findViewByPosition(firstPos);
                        offsetTop = mLinearLayoutManager.getDecoratedTop(firstView) - mLinearLayoutManager.getTopDecorationHeight(firstView);
                    }

                    Collections.swap(mConversationsList, i, j);
                    mConversationsAdapter.notifyItemMoved(i, j);

                    mConversationsAdapter.notifyItemChanged(i);
                    mConversationsAdapter.notifyItemChanged(j);

                    if (firstPos >= 0) {
                        mLinearLayoutManager.scrollToPositionWithOffset(firstPos, offsetTop);
                    }
                }
            }

        }
    }

    private void processUserGroupMessagesContent(Message message, FirebaseCallBackMessageContent firebaseCallBackMessageContent) {

        getSingleUserProfile(message.getSenderId(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User senderProfile) {

                if (message.getType() == MESSAGE_TYPE_CHANGE_ADMIN) {

                    String newAdminId = "";

                    for (String relatedUserId : message.getRelatedUserId().keySet()) newAdminId = relatedUserId;

                    if (newAdminId.equals(getMyFirebaseUserId())) {

                        firebaseCallBackMessageContent.OnCallBack(getLastAndMiddleName(senderProfile.getName()) + ": Đã chọn bạn làm quản trị viên");

                    } else getSingleUserProfile(newAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                        @Override
                        public void OnCallBack(User newAdminProfile) {

                            firebaseCallBackMessageContent.OnCallBack(getLastAndMiddleName(senderProfile.getName()) + ": Đã chọn " + newAdminProfile.getName() + " làm quản trị viên");

                        }
                    });

                } else {

                    firebaseCallBackMessageContent.OnCallBack(getLastAndMiddleName(senderProfile.getName()) + ":" + " " + message.getContent());

                }


            }
        });

    }

    private void processMyGroupMessagesContent(Message message,
                                               FirebaseCallBackMessageContent firebaseCallBackMessageContent) {

        if (message.getType() == MESSAGE_TYPE_CHANGE_ADMIN) {

            String newAdminId = "";

            for (String relatedUserId : message.getRelatedUserId().keySet()) newAdminId = relatedUserId;

            getSingleUserProfile(newAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                @Override
                public void OnCallBack(User newAdminProfile) {

                    firebaseCallBackMessageContent.OnCallBack("Bạn: Đã chọn " + getLastAndMiddleName(newAdminProfile.getName()) + " làm quản trị viên");

                }
            });

        } else {

            firebaseCallBackMessageContent.OnCallBack("Bạn:" + " " + message.getContent());

        }


    }

    private void showWarningDialog(GroupDetail groupDetail) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Lưu ý")
                .setMessage("Thành viên trong nhóm này có người dùng mà bạn đã chặn. Người này vẫn sẽ xem được các tin nhắn mà bạn gửi cho nhóm. Bạn có muốn tiếp tục trò chuyện trong nhóm này không ?")
                .setNegativeButton("Rời nhóm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLeaveGroupConfirmDialog(getActivity(), groupDetail.getGroupId());
                    }
                }).setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mAcceptBlockedUserMap.put(groupDetail.getGroupId(), true);

                goToGroupChat(getActivity(), groupDetail);

            }
        }).setNeutralButton("Huỷ", null).create().show();

    }

    private void initOptionDialog() {
        optionsDialog = new BottomSheetDialog(getActivity());
        optionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        optionsDialog.setContentView(R.layout.dialog_conversation_options);
        optionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mShowingOptionConversationId = null;
            }
        });
    }

    private void showOptions(Conversation conversation) {

        mShowingOptionConversationId = conversation.getConversationId();

        LinearLayout btn_leave_group = (LinearLayout) optionsDialog.findViewById(R.id.linear_leave_group);
        LinearLayout btn_lock = (LinearLayout) optionsDialog.findViewById(R.id.linear_block);
        LinearLayout btn_unlock = (LinearLayout) optionsDialog.findViewById(R.id.linear_unlock_user);
        LinearLayout btn_mute = (LinearLayout) optionsDialog.findViewById(R.id.linear_mute_notification);
        LinearLayout btn_unmute = (LinearLayout) optionsDialog.findViewById(R.id.linear_unmute_notification);
        LinearLayout btn_delete = (LinearLayout) optionsDialog.findViewById(R.id.linear_delete);

        if (mSettingMap.get(conversation.getConversationId()) != null) {
            btn_unmute.setVisibility(View.VISIBLE);
            btn_mute.setVisibility(View.GONE);
        } else {
            btn_unmute.setVisibility(View.GONE);
            btn_mute.setVisibility(View.VISIBLE);
        }

        if (conversation.isGroupConversation()) {

            btn_lock.setVisibility(View.GONE);
            btn_unlock.setVisibility(View.GONE);
            btn_leave_group.setVisibility(View.VISIBLE);

        } else {

            btn_leave_group.setVisibility(View.GONE);

            if (mBlockMap.get(conversation.getConversationId()) != null) {
                btn_unlock.setVisibility(View.VISIBLE);
                btn_lock.setVisibility(View.GONE);
            } else if (mBlockedMap.get(conversation.getConversationId()) != null) {
                btn_lock.setVisibility(View.GONE);
                btn_unlock.setVisibility(View.GONE);
            } else {
                btn_unlock.setVisibility(View.GONE);
                btn_lock.setVisibility(View.VISIBLE);
            }

        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.linear_leave_group:
                        showLeaveGroupConfirmDialog(getActivity(), conversation.getConversationId());
                        break;

                    case R.id.linear_block:
                        checkBeforeBLockUser(getContext(), conversation.getConversationId());
                        break;

                    case R.id.linear_unlock_user:
                        showUnlockUserConfirmDialog(getContext(), conversation.getConversationId());
                        break;

                    case R.id.linear_mute_notification:
                        preventNotifyMe(getActivity(), conversation.getConversationId());
                        break;

                    case R.id.linear_unmute_notification:
                        allowNotifyMe(getActivity(), conversation.getConversationId());
                        break;

                    case R.id.linear_delete:
                        showDeleteConversationConfirmDialog(conversation.getConversationId());
                        break;
                }

                optionsDialog.dismiss();
            }
        };

        btn_delete.setOnClickListener(onClickListener);
        btn_leave_group.setOnClickListener(onClickListener);
        btn_lock.setOnClickListener(onClickListener);
        btn_unlock.setOnClickListener(onClickListener);
        btn_mute.setOnClickListener(onClickListener);
        btn_unmute.setOnClickListener(onClickListener);

        optionsDialog.show();

    }

    private void showDeleteConversationConfirmDialog(String conversationId) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Xóa cuộc trò chuyện")
                .setMessage("Xóa cuộc trò chuyện sẽ xóa tất cả tin nhắn và ảnh chung đã gửi trong cuộc trò chuyện này. " +
                        "Bạn có chắc chắn muốn xóa không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        if (isConnectedToFirebaseService(getActivity())) {

                            AlertDialog loadingDialog = getLoadingBuilder(getActivity());

                            loadingDialog.show();

                            Map<String, Object> mapDeleteConv = new HashMap<>();

                            mapDeleteConv.put("/" + CHILD_MEDIA + "/" + getMyFirebaseUserId() + "/" + conversationId, null);
                            mapDeleteConv.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + conversationId, null);

                            ROOT_REF.updateChildren(mapDeleteConv).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingDialog.dismiss();
                                }
                            });

                        } else showNoConnectionDialog(getActivity());
                    }

                }).setNegativeButton("Không", null).create().show();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRefreshHandler != null) mRefreshHandler.removeCallbacksAndMessages(null);

        removeAllFirebaseListener();

        for (Conversation conversation : mConversationsList) {
            if (conversation.isSingleConversation()) {
                removeProfileListener(conversation.getConversationId());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
    }

    private interface FirebaseCallBackMessageContent {
        void OnCallBack(String content);
    }
}
