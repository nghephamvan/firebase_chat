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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.GroupRole.ROLE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.GroupRole.ROLE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_UPDATE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.SEARCH_DELAY_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_INVALID_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_VALID_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMatchingUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isValidSingleName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentDate;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentMonth;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentYear;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSmallNumber;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToGroupChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class CreateGroup extends BaseActivity {

    private final Handler mSearchHandler = new Handler();

    private Toolbar mToolbar;

    private LinearLayout nonactive_searchbar;
    private AppBarLayout active_searchbar;

    private AppBarLayout appBarLayout;

    private TextView tv_title;

    private EditText edt_search;
    private EditText edt_group_name;

    private LinearLayout linear_group_name;

    private RecyclerView rv_friends;
    private RecyclerView rv_picked;

    private ArrayList<User> mFriendList;
    private ArrayList<User> mPickedList;
    private ArrayList<User> mSearchList;

    private UserMultiplePickAdapter mSearchAdapter;
    private UserMultiplePickAdapter mFriendAdapter;
    private PickedUserAdapter mPickedAdapter;

    private Map<String, Boolean> mCheckBoxMap;
    private ProgressBar pb_loading;

    private FrameLayout btn_clear_keyword;
    private FrameLayout btn_back;

    private boolean isSearching;

    private AlertDialog mLoadingDialog;

    private ChildEventListener mFriendChildEvent;
    private Query mFriendQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initViews();

        initClickEvents();

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);

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
                if (hasItemInList(k)) removePickedUser(k);


                checkToShowGroupNameLayout();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mFriendQuery.addChildEventListener(mFriendChildEvent);

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

        checkToShowGroupNameLayout();

        //set search adapter
        mSearchList.clear();

        mSearchList.addAll(mFriendList);

        mSearchAdapter.notifyDataSetChanged();

        rv_friends.setAdapter(mSearchAdapter);

        edt_search.requestFocus();

        showKeyboard(CreateGroup.this);

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
//hide keyboard
        hideKeyboard(CreateGroup.this);

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

        checkToShowGroupNameLayout();

        pb_loading.setVisibility(View.GONE);

        mSearchHandler.removeCallbacksAndMessages(null);
    }

    private void checkToShowGroupNameLayout() {

        if (isSearching)

            linear_group_name.setVisibility(View.GONE);

        else linear_group_name.setVisibility(View.VISIBLE);

    }


    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tin nhắn mới");

        mCheckBoxMap = new HashMap<>();

        mFriendList = new ArrayList<>();
        mPickedList = new ArrayList<>();
        mSearchList = new ArrayList<>();

        mFriendAdapter = new UserMultiplePickAdapter(CreateGroup.this, mFriendList, mCheckBoxMap, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
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

                checkToShowGroupNameLayout();


            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });
        mPickedAdapter = new PickedUserAdapter(CreateGroup.this, mPickedList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final String clickedUserId = mPickedList.get(position).getUserId();

                mCheckBoxMap.remove(clickedUserId);

                final int i = searchUser(mFriendList, clickedUserId);
                if (hasItemInList(i)) mFriendAdapter.notifyItemChanged(i);

                final int j = searchUser(mSearchList, clickedUserId);
                if (hasItemInList(j)) mSearchAdapter.notifyItemChanged(j);

                removePickedUser(position);

                checkToShowGroupNameLayout();

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });
        mSearchAdapter = new UserMultiplePickAdapter(CreateGroup.this, mSearchList, mCheckBoxMap, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
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

                checkToShowGroupNameLayout();


            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });

        rv_friends = (RecyclerView) findViewById(R.id.rv_create_conversation);
        rv_friends.setHasFixedSize(true);
        rv_friends.setLayoutManager(new LinearLayoutManager(CreateGroup.this, LinearLayoutManager.VERTICAL, false));
        rv_friends.setAdapter(mFriendAdapter);
        rv_friends.setItemAnimator(null);

        rv_picked = (RecyclerView) findViewById(R.id.rv_picked_users);
        rv_picked.setLayoutManager(new LinearLayoutManager(CreateGroup.this, LinearLayoutManager.HORIZONTAL, false));
        rv_picked.setAdapter(mPickedAdapter);
        rv_picked.setItemAnimator(null);

        mLoadingDialog = getLoadingBuilder(CreateGroup.this);

        initSearchViews();

    }

    private void initSearchViews() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        nonactive_searchbar = (LinearLayout) findViewById(R.id.nonactive_searchbar);

        active_searchbar = (AppBarLayout) findViewById(R.id.active_searchbar);

        edt_search = (EditText) findViewById(R.id.edt_search);

        edt_group_name = (EditText) findViewById(R.id.edt_group_name);

        tv_title = (TextView) findViewById(R.id.tv_title);

        linear_group_name = (LinearLayout) findViewById(R.id.linear_group_name);

        btn_clear_keyword = (FrameLayout) findViewById(R.id.frame_clear_keyword);

        btn_back = (FrameLayout) findViewById(R.id.frame_back);

        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

        isSearching = false;
    }

    private void showInvalidUsersDialog(ArrayList<User> invalidMembers, GroupDetail groupDetail) {

        String invalidName = getListFullName(invalidMembers);

        new AlertDialog.Builder(CreateGroup.this)
                .setMessage("Không thể tạo nhóm với : " + invalidName + ".")
                .setPositiveButton("Đã hiểu", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        if (groupDetail != null) goToGroupChat(CreateGroup.this, groupDetail);

                        finish();
                    }
                }).create().show();

    }

    private void showCancelConfirmDialog() {

        new AlertDialog.Builder(CreateGroup.this)
                .setMessage("Bạn có chắc chắn muốn huỷ tạo nhóm không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("Không", null)
                .create().show();

    }

    private void createGroup(String groupName, ArrayList<User> validMembers, ArrayList<User> invalidMembers) {

        final String groupId = ROOT_REF.child(CHILD_GROUP_DETAIL).push().getKey();

        final String createdDate = formatSmallNumber(getCurrentDate()) + "/" + formatSmallNumber(getCurrentMonth()) + "/" + formatSmallNumber(getCurrentYear());

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();


        final Map<String, GroupMember> allMemberMap = new HashMap<>();
        final Map<String, Object> initGroupMap = new HashMap<>();

        GroupMember myMemberShip = new GroupMember(getMyFirebaseUserId(), getMyFirebaseUserId(), ROLE_ADMIN);

        allMemberMap.put(myMemberShip.getMemberId(), myMemberShip);

        for (User user : validMembers) {

            GroupMember memberShip = new GroupMember(user.getUserId(), getMyFirebaseUserId(), ROLE_MEMBER);

            allMemberMap.put(memberShip.getMemberId(), memberShip);

        }

        final GroupDetail groupDetail = new GroupDetail(
                groupId, getMyFirebaseUserId(), groupName,
                null, null, null,
                createdDate, allMemberMap, false);

        final Message myMessage = buildInstantMessage(messageId, getMyFirebaseUserId(), groupId,
                "Đã tạo nhóm trò chuyện",
                MESSAGE_TYPE_UPDATE_GROUP, notificationId, null);

        initGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + getMyFirebaseUserId() + "/" + groupId, groupDetail);
        initGroupMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + groupId + "/" + messageId, myMessage);

        for (User user : validMembers) {

            final Message memberMessage = buildInstantMessage(messageId, user.getUserId(), groupId,
                    "Đã tạo nhóm trò chuyện",
                    MESSAGE_TYPE_UPDATE_GROUP, notificationId, null);

            initGroupMap.put("/" + CHILD_GROUP_DETAIL + "/" + user.getUserId() + "/" + groupId, groupDetail);
            initGroupMap.put("/" + CHILD_MESSAGES + "/" + user.getUserId() + "/" + groupId + "/" + messageId, memberMessage);
        }

        ROOT_REF.updateChildren(initGroupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mLoadingDialog.dismiss();

                for (User user : validMembers) {

                    sendNotificationToUser(
                            user.getUserId(),
                            getMyFirebaseUserId(),
                            groupId,
                            messageId,
                            "Đã tạo nhóm trò chuyện.",
                            groupDetail.getGroupName(),
                            groupDetail.getGroupThumbAvatar(),
                            NOTIFICATION_TYPE_UPDATE_GROUP,
                            notificationId);

                }

                if (invalidMembers.isEmpty()) {

                    goToGroupChat(CreateGroup.this, groupDetail);

                    finish();

                } else showInvalidUsersDialog(invalidMembers, groupDetail);

            }
        });

    }

    private void loadValidMembers(AppConstants.AppInterfaces.FirebaseUserListCallBack userListCallBack) {

        ArrayList<User> validList = new ArrayList<>();
        ArrayList<User> invalidList = new ArrayList<>();

        ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId())
                .orderByChild(CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (User member : mPickedList) {

                            if (dataSnapshot.hasChild(member.getUserId())) {

                                invalidList.add(member);

                            } else validList.add(member);

                        }

                        Map<String, ArrayList<User>> map = new HashMap<>();

                        map.put(KEY_VALID_USER, validList);
                        map.put(KEY_INVALID_USER, invalidList);

                        userListCallBack.OnCallBack(map);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSearchHandler != null) mSearchHandler.removeCallbacksAndMessages(null);

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.one_button_menu, menu);
        menu.findItem(R.id.item_one_button).setTitle("OK");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                if (mPickedList.size() > 0)

                    showCancelConfirmDialog();

                else finish();

                break;

            case R.id.item_one_button:

                if (mPickedList.isEmpty()) {

                    finish();

                } else {

                    if (isConnectedToFirebaseService(CreateGroup.this)) {

                        final String groupName = edt_group_name.getText().toString().trim();

                        if (groupName.isEmpty()) {

                            showMessageDialog(CreateGroup.this, "Tên nhóm không được trống");

                        } else if (!isValidSingleName(groupName)) {

                            showMessageDialog(CreateGroup.this, "Tên nhóm không hợp lệ");

                        } else {

                            mLoadingDialog.show();

                            loadValidMembers(new AppConstants.AppInterfaces.FirebaseUserListCallBack() {
                                @Override
                                public void OnCallBack(Map<String, ArrayList<User>> callbackUserList) {

                                    if (callbackUserList.get(KEY_VALID_USER).isEmpty()) {

                                        mLoadingDialog.dismiss();

                                        showInvalidUsersDialog(callbackUserList.get(KEY_INVALID_USER), null);

                                    } else {
                                        createGroup(groupName, callbackUserList.get(KEY_VALID_USER), callbackUserList.get(KEY_INVALID_USER));
                                    }

                                }
                            });

                        }

                    } else showNoConnectionDialog(CreateGroup.this);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (isSearching) disableSearchMode();

        else if (mPickedList.size() > 0)

            showCancelConfirmDialog();

        else super.onBackPressed();

    }

    private String getListFullName(List<User> userList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int position = 0; position < userList.size(); position++) {

            String fullName = userList.get(position).getName();

            if (position == userList.size() - 1)
                stringBuilder.append(fullName);
            else stringBuilder.append(fullName).append(", ");

        }
        return stringBuilder.toString();
    }

}
