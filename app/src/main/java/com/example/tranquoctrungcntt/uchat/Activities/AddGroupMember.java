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
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.GroupMember;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_JOIN_GROUP_REQUESTS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.GroupRole.ROLE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.SEARCH_DELAY_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_INVALID_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_VALID_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupMember;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMatchingUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetailWithTransaction;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupMemberSnapshot;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.showNotMemberDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showKeyboard;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class AddGroupMember extends BaseActivity {

    private final Handler mSearchHandler = new Handler();

    private Toolbar mToolbar;

    private LinearLayout nonactive_searchbar;

    private AppBarLayout active_searchbar;
    private AppBarLayout appBarLayout;

    private EditText edt_search;
    private TextView tv_title;

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

    private AlertDialog mMoreInforDialog;
    private AlertDialog mLoadingDialog;

    private ProgressBar pb_loading;

    private ChildEventListener mFriendChildEvent;
    private ValueEventListener mMemberCheckerValueEvent;

    private DatabaseReference mMemberCheckerRef;

    private Query mFriendQuery;

    private String mGroupId;

    private boolean isSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        if (mMemberCheckerValueEvent != null && mMemberCheckerRef != null) mMemberCheckerRef.removeEventListener(mMemberCheckerValueEvent);

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);

        mGroupId = (String) getDataFromIntent(AddGroupMember.this, INTENT_KEY_GROUP_ID);

        if (mGroupId != null) {

            initViews();

            initClickEvents();

            mMemberCheckerRef = ROOT_REF.child(CHILD_GROUP_DETAIL).child(getMyFirebaseUserId()).child(mGroupId).child(kGroupMember).child(getMyFirebaseUserId());

            mMemberCheckerValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        showNotMemberDialog(AddGroupMember.this);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mMemberCheckerRef.addValueEventListener(mMemberCheckerValueEvent);


            mFriendQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_FRIEND).equalTo(true);

            mFriendChildEvent = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull final DataSnapshot friendSnapshot, @Nullable String s) {

                    ROOT_REF.child(CHILD_GROUP_DETAIL)
                            .child(getMyFirebaseUserId()).child(mGroupId)
                            .child(kGroupMember).child(friendSnapshot.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.exists()) {

                                        getSingleUserProfile(friendSnapshot.getKey(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                            @Override
                                            public void OnCallBack(User callbackUserProfile) {

                                                if (callbackUserProfile != null) {

                                                    mFriendList.add(callbackUserProfile);
                                                    mFriendAdapter.notifyItemInserted(mFriendList.size() - 1);

                                                }

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    int i = searchUser(mPickedList, dataSnapshot.getKey());

                    if (hasItemInList(i)) {
                        mPickedList.remove(i);
                        mPickedAdapter.notifyItemRemoved(i);
                        mPickedAdapter.notifyItemRangeChanged(i, mPickedAdapter.getItemCount());
                    }

                    int j = searchUser(mSearchList, dataSnapshot.getKey());

                    if (hasItemInList(j)) {
                        mSearchList.remove(j);
                        mSearchAdapter.notifyItemRemoved(j);
                        mSearchAdapter.notifyItemRangeChanged(j, mSearchAdapter.getItemCount());
                    }

                    int k = searchUser(mFriendList, dataSnapshot.getKey());

                    if (hasItemInList(k)) {
                        mFriendList.remove(k);
                        mFriendAdapter.notifyItemRemoved(k);
                        mFriendAdapter.notifyItemRangeChanged(k, mFriendAdapter.getItemCount());
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

                //remove hanlder

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

                    mSearchList.clear();

                    mSearchList.addAll(mFriendList);

                    mSearchAdapter.notifyDataSetChanged();

                    pb_loading.setVisibility(View.GONE);

                    rv_friends.setVisibility(View.VISIBLE);

                }


            }
        });


    }

    private void enableSearchMode() {

        isSearching = true;

        active_searchbar.setVisibility(View.VISIBLE);

        appBarLayout.setVisibility(View.GONE);

        nonactive_searchbar.setVisibility(View.GONE);

        rv_picked.setVisibility(View.GONE);

        tv_title.setVisibility(View.GONE);

        mSearchList.clear();

        mSearchList.addAll(mFriendList);

        mSearchAdapter.notifyDataSetChanged();

        rv_friends.setAdapter(mSearchAdapter);

        edt_search.requestFocus();

        showKeyboard(AddGroupMember.this);

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
        mPickedAdapter.notifyItemRangeChanged(index, mPickedAdapter.getItemCount());
    }

    private void disableSearchMode() {

        isSearching = false;

        hideKeyboard(AddGroupMember.this);

        edt_search.setText(null);

        edt_search.clearFocus();

        active_searchbar.setVisibility(View.GONE);

        appBarLayout.setVisibility(View.VISIBLE);

        nonactive_searchbar.setVisibility(View.VISIBLE);

        tv_title.setVisibility(View.VISIBLE);

        mSearchList.clear();

        mSearchAdapter.notifyDataSetChanged();

        rv_friends.setAdapter(mFriendAdapter);

        rv_friends.setVisibility(View.VISIBLE);

        rv_picked.setVisibility(View.VISIBLE);

        pb_loading.setVisibility(View.GONE);

        mSearchHandler.removeCallbacksAndMessages(null);
    }

    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thêm thành viên");

        mCheckBoxMap = new HashMap<>();

        mFriendList = new ArrayList<>();
        mPickedList = new ArrayList<>();
        mSearchList = new ArrayList<>();

        mFriendAdapter = new UserMultiplePickAdapter(AddGroupMember.this, mFriendList, mCheckBoxMap, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final User clickedUser = mFriendList.get(position);

                if (mCheckBoxMap.get(clickedUser.getUserId()) == null) {

                    addPickedUser(clickedUser);

                    mCheckBoxMap.put(clickedUser.getUserId(), true);

                } else {

                    int i = searchUser(mPickedList, clickedUser.getUserId());
                    if (hasItemInList(i)) removePickedUser(i);

                    mCheckBoxMap.remove(clickedUser.getUserId());

                }

                mFriendAdapter.notifyItemChanged(position);

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });

        mPickedAdapter = new PickedUserAdapter(AddGroupMember.this, mPickedList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
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

        mSearchAdapter = new UserMultiplePickAdapter(AddGroupMember.this, mSearchList, mCheckBoxMap, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final User clickedUser = mSearchList.get(position);

                if (mCheckBoxMap.get(clickedUser.getUserId()) == null) {

                    addPickedUser(clickedUser);

                    mCheckBoxMap.put(clickedUser.getUserId(), true);

                    int i = searchUser(mFriendList, clickedUser.getUserId());
                    if (hasItemInList(i)) mFriendAdapter.notifyItemChanged(i);

                } else {

                    int i = searchUser(mPickedList, clickedUser.getUserId());
                    if (hasItemInList(i)) removePickedUser(i);

                    mCheckBoxMap.remove(clickedUser.getUserId());

                    int j = searchUser(mFriendList, clickedUser.getUserId());
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
        rv_friends.setLayoutManager(new LinearLayoutManager(AddGroupMember.this, LinearLayoutManager.VERTICAL, false));
        rv_friends.setAdapter(mFriendAdapter);
        rv_friends.setItemAnimator(null);

        rv_picked = (RecyclerView) findViewById(R.id.rv_picked_users);
        rv_picked.setLayoutManager(new LinearLayoutManager(AddGroupMember.this, LinearLayoutManager.HORIZONTAL, false));
        rv_picked.setAdapter(mPickedAdapter);
        rv_picked.setItemAnimator(null);

        tv_title = (TextView) findViewById(R.id.tv_title);

        mLoadingDialog = getLoadingBuilder(AddGroupMember.this);

        initSearchViews();

        buildMoreInforDialog();
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

    private void buildMoreInforDialog() {

        mMoreInforDialog = new AlertDialog.Builder(AddGroupMember.this).setMessage("Danh sách " +
                "người dùng có thể thêm là bạn bè của bạn, không bao gồm những bạn bè đang tham " +
                "gia nhóm. Bạn không thể thêm bạn bè khi: " + "\n" + "\t\t ⦁ Bạn bè đang là thành" +
                " viên của nhóm." + "\n" + "\t\t ⦁ Bạn bè đang đợi được duyệt dể vào nhóm." + "\n"
                + "\t\t ⦁ Bạn bè đã ngăn bạn tạo nhóm với họ." + "\n").setPositiveButton("Đã " +
                "hiểu", null).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        }).create();

    }

    private void showCancelCorfirmDialog() {

        new AlertDialog.Builder(AddGroupMember.this).setMessage("Bạn có chắc chắn muốn huỷ thêm " +
                "những bạn bè này vào nhóm không ?").setPositiveButton("Có",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("Không", null).create().show();

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mGroupId != null) ROOT_REF.child(CHILD_JOIN_GROUP_REQUESTS).child(mGroupId).keepSynced(true);


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGroupId != null) ROOT_REF.child(CHILD_JOIN_GROUP_REQUESTS).child(mGroupId).keepSynced(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);

        if (mMemberCheckerValueEvent != null && mMemberCheckerRef != null) mMemberCheckerRef.removeEventListener(mMemberCheckerValueEvent);

        if (mSearchHandler != null) mSearchHandler.removeCallbacksAndMessages(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.one_button_menu, menu);
        menu.findItem(R.id.item_one_button).setTitle("THÊM");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                if (mPickedList.size() > 0) showCancelCorfirmDialog();

                else finish();

                break;

            case R.id.item_one_button:

                if (isConnectedToFirebaseService(AddGroupMember.this)) {

                    if (mPickedList.isEmpty()) {

                        finish();

                    } else {

                        mLoadingDialog.show();

                        checkBeforeAdd(new AppConstants.AppInterfaces.FirebaseUserListCallBack() {
                            @Override
                            public void OnCallBack(Map<String, ArrayList<User>> callbackUserList) {

                                if (callbackUserList.get(KEY_VALID_USER).isEmpty()) {

                                    mLoadingDialog.dismiss();

                                    showInvalidUsersDialog(callbackUserList.get(KEY_INVALID_USER));

                                } else {

                                    getGroupDetailWithTransaction(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                                        @Override
                                        public void OnCallBack(GroupDetail callbackGroupDetail) {

                                            if (callbackGroupDetail.getMember() != null && callbackGroupDetail.getMember().get(getMyFirebaseUserId()) != null) {

                                                if (callbackGroupDetail.getAdminId().equals(getMyFirebaseUserId()) || !callbackGroupDetail.isCensorMode()) {

                                                    addToGroup(callbackGroupDetail, callbackUserList.get(KEY_VALID_USER), callbackUserList.get(KEY_INVALID_USER));

                                                } else {

                                                    addToQueue(callbackGroupDetail, callbackUserList.get(KEY_VALID_USER), callbackUserList.get(KEY_INVALID_USER));

                                                }

                                            } else {

                                                mLoadingDialog.dismiss();

                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                } else showNoConnectionDialog(AddGroupMember.this);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkBeforeAdd(AppConstants.AppInterfaces.FirebaseUserListCallBack userListCallBack) {

        ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId())
                .orderByChild(CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP)
                .equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotNoMakingGroup) {

                ROOT_REF.child(CHILD_JOIN_GROUP_REQUESTS).child(mGroupId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotJoinGroupRequest) {

                                getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                    @Override
                                    public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                        final ArrayList<User> validList = new ArrayList<>();
                                        final ArrayList<User> invalidList = new ArrayList<>();

                                        for (User user : mPickedList) {

                                            final boolean isNoMakingGroup = snapshotNoMakingGroup.hasChild(user.getUserId());
                                            final boolean isCurrentMember = groupMemberSnapshot.hasChild(user.getUserId());
                                            final boolean isInQueue = snapshotJoinGroupRequest.hasChild(user.getUserId());

                                            if (isNoMakingGroup || isCurrentMember || isInQueue) {
                                                invalidList.add(user);
                                            } else validList.add(user);

                                        }

                                        Map<String, ArrayList<User>> map = new HashMap<>();

                                        map.put(KEY_VALID_USER, validList);
                                        map.put(KEY_INVALID_USER, invalidList);

                                        userListCallBack.OnCallBack(map);
                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addToGroup(GroupDetail groupDetail, ArrayList<User> validMembers, ArrayList<User> invalidMembers) {

        final Map<String, Boolean> relatedUserIdMap = new HashMap<>();

        for (User user : validMembers) relatedUserIdMap.put(user.getUserId(), true);

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();


        final Map<String, Object> mapForPreviousMember = new HashMap<>();

        for (User user : validMembers) {

            final GroupMember memberShip = new GroupMember(user.getUserId(), getMyFirebaseUserId(), ROLE_MEMBER);

            for (GroupMember previousMember : groupDetail.getMember().values()) {

                final Message message = buildInstantMessage(messageId, previousMember.getMemberId(), mGroupId,
                        "Đã thêm " + relatedUserIdMap.size() + " thành viên mới vào nhóm",
                        MESSAGE_TYPE_UPDATE_MEMBER, notificationId, relatedUserIdMap);

                mapForPreviousMember.put("/" + CHILD_GROUP_DETAIL + "/" + previousMember.getMemberId() + "/" + mGroupId + "/" + kGroupMember + "/" + memberShip.getMemberId(), memberShip);
                mapForPreviousMember.put("/" + CHILD_MESSAGES + "/" + previousMember.getMemberId() + "/" + mGroupId + "/" + messageId, message);
            }
        }

        ROOT_REF.updateChildren(mapForPreviousMember).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                ROOT_REF.child(CHILD_GROUP_DETAIL)
                        .child(getMyFirebaseUserId()).child(mGroupId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                final GroupDetail updatedGroup = dataSnapshot.getValue(GroupDetail.class);

                                final Map<String, Object> mapForAddedMember = new HashMap<>();

                                for (User user : validMembers) {

                                    final Message message = buildInstantMessage(messageId, user.getUserId(), mGroupId,
                                            "Đã thêm " + relatedUserIdMap.size() + " thành viên mới vào nhóm",
                                            MESSAGE_TYPE_UPDATE_MEMBER, notificationId, relatedUserIdMap);

                                    mapForAddedMember.put("/" + CHILD_JOIN_GROUP_REQUESTS + "/" + mGroupId + "/" + user.getUserId(), null);
                                    mapForAddedMember.put("/" + CHILD_GROUP_DETAIL + "/" + user.getUserId() + "/" + mGroupId, updatedGroup);
                                    mapForAddedMember.put("/" + CHILD_MESSAGES + "/" + user.getUserId() + "/" + mGroupId + "/" + messageId, message);

                                }

                                ROOT_REF.updateChildren(mapForAddedMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mLoadingDialog.dismiss();

                                        for (User user : validMembers) {

                                            sendNotificationToUser(
                                                    user.getUserId(),
                                                    getMyFirebaseUserId(),
                                                    mGroupId,
                                                    messageId,
                                                    "Đã thêm bạn vào nhóm trò chuyện.",
                                                    updatedGroup.getGroupName(),
                                                    updatedGroup.getGroupThumbAvatar(),
                                                    NOTIFICATION_TYPE_UPDATE_MEMBER,
                                                    notificationId);

                                        }

                                        if (invalidMembers.isEmpty()) finish();

                                        else showInvalidUsersDialog(invalidMembers);

                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        });


    }

    private void addToQueue(GroupDetail groupDetail, ArrayList<User> validMembers, ArrayList<User> invalidMembers) {

        final Map<String, Boolean> relatedUserIdMap = new HashMap<>();

        for (User user : validMembers) relatedUserIdMap.put(user.getUserId(), true);

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final Map<String, Object> mapToPush = new HashMap<>();

        for (User user : validMembers) {

            GroupMember memberShip = new GroupMember(user.getUserId(), getMyFirebaseUserId(), ROLE_MEMBER);

            mapToPush.put("/" + CHILD_JOIN_GROUP_REQUESTS + "/" + mGroupId + "/" + memberShip.getMemberId(), memberShip);

        }

        for (String key : groupDetail.getMember().keySet()) {

            final Message message = buildInstantMessage(messageId, key, mGroupId,
                    "Đã thêm " + relatedUserIdMap.size() + " thành viên mới vào danh sách chờ duyệt",
                    MESSAGE_TYPE_UPDATE_MEMBER, notificationId, relatedUserIdMap);

            mapToPush.put("/" + CHILD_MESSAGES + "/" + key + "/" + mGroupId + "/" + messageId, message);

        }


        ROOT_REF.updateChildren(mapToPush).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mLoadingDialog.dismiss();

                sendNotificationToUser(
                        groupDetail.getAdminId(),
                        getMyFirebaseUserId(),
                        mGroupId,
                        messageId, "Đã thêm " + relatedUserIdMap.size() + " thành viên mới vào danh sách chờ duyệt.",
                        groupDetail.getGroupName(),
                        groupDetail.getGroupThumbAvatar(),
                        NOTIFICATION_TYPE_UPDATE_MEMBER,
                        notificationId);

                if (invalidMembers.isEmpty()) finish();

                else showInvalidUsersDialog(invalidMembers);

            }
        });


    }

    private void showInvalidUsersDialog(ArrayList<User> invalidMembers) {

        new AlertDialog.Builder(AddGroupMember.this).setMessage("Không thể thêm: " + getListFullName(invalidMembers)).setPositiveButton("Đã hiểu", null).setNegativeButton("Tìm hiểu thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMoreInforDialog.show();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!mMoreInforDialog.isShowing()) finish();
            }
        }).create().show();

    }

    private String getListFullName(List<User> userList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int position = 0; position < userList.size(); position++) {
            String fullname = userList.get(position).getName();
            if (position == userList.size() - 1)
                stringBuilder.append(fullname).append(".");
            else stringBuilder.append(fullname).append(", ");
        }
        return stringBuilder.toString();
    }

    @Override
    public void onBackPressed() {

        if (isSearching) disableSearchMode();

        else if (mPickedList.size() > 0) showCancelCorfirmDialog();

        else super.onBackPressed();
    }


}
