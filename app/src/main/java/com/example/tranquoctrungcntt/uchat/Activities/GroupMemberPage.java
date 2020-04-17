package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.GroupMemberAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.GroupMemberModel;
import com.example.tranquoctrungcntt.uchat.Objects.GroupMember;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.GroupRole.ROLE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.SEARCH_DELAY_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupMember;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kRole;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMatchingUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.deleteMember;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.showChangeAdminConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.showNotMemberDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkBeforeBLockUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.makeCall;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showKeyboard;

public class GroupMemberPage extends BaseActivity {

    private final Handler mSearchHandler = new Handler();

    private Toolbar mToolbar;

    private RecyclerView rv_members;

    private ArrayList<GroupMemberModel> mSearchList;
    private ArrayList<GroupMemberModel> mMemberList;

    private GroupMemberAdapter mMemberAdapter;
    private GroupMemberAdapter mSearchAdapter;

    private LinearLayout nonactive_searchbar;

    private AppBarLayout active_searchbar;
    private AppBarLayout appBarLayout;

    private EditText edt_search;

    private FrameLayout btn_clear_keyword;
    private FrameLayout btn_back;

    private ProgressBar pb_loading;
    private boolean isSearching;

    private AlertDialog mOptionsDialog;

    private ChildEventListener mGroupMemberChildEvent;
    private DatabaseReference mCurrentMemberRef;

    private String mUsingMemberId;
    private String mGroupId;
    private String mCurrentAdminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);

        if (mGroupMemberChildEvent != null && mCurrentMemberRef != null) mCurrentMemberRef.removeEventListener(mGroupMemberChildEvent);

        mGroupId = (String) getDataFromIntent(GroupMemberPage.this, INTENT_KEY_GROUP_ID);

        if (mGroupId != null) {

            initViews();

            initClickEvents();

            mCurrentMemberRef = ROOT_REF.child(CHILD_GROUP_DETAIL).child(getMyFirebaseUserId()).child(mGroupId).child(kGroupMember);

            mCurrentMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(getMyFirebaseUserId())) {

                        loadGroupMembers();

                    } else showNotMemberDialog(GroupMemberPage.this);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else finish();

    }

    private void initViews() {

        mUsingMemberId = null;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thành viên");

        mMemberList = new ArrayList<>();
        mSearchList = new ArrayList<>();

        mMemberAdapter = new GroupMemberAdapter(GroupMemberPage.this, mMemberList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                User member = mMemberList.get(position).getMember();

                if (!member.getUserId().equals(getMyFirebaseUserId()))
                    showOptions(member, mCurrentAdminId != null && mCurrentAdminId.equals(getMyFirebaseUserId()));

            }

            @Override
            public void OnItemLongClick(View v, int position) {

                User member = mMemberList.get(position).getMember();

                if (!member.getUserId().equals(getMyFirebaseUserId())) {

                    showOptions(member, mCurrentAdminId.equals(getMyFirebaseUserId()));

                }

            }
        });
        mSearchAdapter = new GroupMemberAdapter(GroupMemberPage.this, mSearchList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final User member = mSearchList.get(position).getMember();

                if (!member.getUserId().equals(getMyFirebaseUserId())) {

                    showOptions(member, mCurrentAdminId.equals(getMyFirebaseUserId()));

                }

            }

            @Override
            public void OnItemLongClick(View v, int position) {

                final User member = mSearchList.get(position).getMember();

                if (!member.getUserId().equals(getMyFirebaseUserId())) {

                    showOptions(member, mCurrentAdminId.equals(getMyFirebaseUserId()));

                }
            }
        });

        rv_members = (RecyclerView) findViewById(R.id.rv_group_members);
        rv_members.setHasFixedSize(true);
        rv_members.setLayoutManager(new LinearLayoutManager(GroupMemberPage.this, LinearLayoutManager.VERTICAL, false));
        rv_members.setAdapter(mMemberAdapter);

        disableChangeAnimation(rv_members);

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

    private void loadGroupMembers() {


        mGroupMemberChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final GroupMember groupMember = dataSnapshot.getValue(com.example.tranquoctrungcntt.uchat.Objects.GroupMember.class);

                final String memberId = groupMember.getMemberId();
                final String adderId = groupMember.getAdderId();
                final String role = groupMember.getRole();

                if (role.equals(ROLE_ADMIN)) mCurrentAdminId = memberId;

                getSingleUserProfile(memberId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User memberProfile) {

                        if (memberProfile != null) {

                            getSingleUserProfile(adderId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                @Override
                                public void OnCallBack(User adderProfile) {

                                    if (adderProfile != null) {

                                        final GroupMemberModel groupMemberModel = new GroupMemberModel(memberProfile, adderProfile, role);

                                        if (groupMemberModel.getMember().getUserId().equals(getMyFirebaseUserId()))
                                            groupMemberModel.getMember().setName(memberProfile.getName() + " (bạn)");

                                        if (role.equals(ROLE_ADMIN)) {

                                            mMemberList.add(0, groupMemberModel);
                                            mMemberAdapter.notifyItemInserted(0);

                                            rv_members.scrollToPosition(0);

                                        } else {

                                            mMemberList.add(groupMemberModel);
                                            mMemberAdapter.notifyItemInserted(mMemberList.size() - 1);

                                        }

                                        getSupportActionBar().setSubtitle(mMemberList.size() + " thành viên");

                                    }


                                }
                            });

                        }

                    }
                });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final String newRole = dataSnapshot.child(kRole).getValue(String.class);

                if (newRole.equals(ROLE_ADMIN)) mCurrentAdminId = dataSnapshot.getKey();

                for (int index = 0; index < mMemberList.size(); index++) {

                    final GroupMemberModel member = mMemberList.get(index);

                    if (member.getMember().getUserId().equals(dataSnapshot.getKey())) {

                        member.setRole(newRole);

                        mMemberAdapter.notifyItemChanged(index);

                        break;

                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getKey().equals(getMyFirebaseUserId())) {

                    showNotMemberDialog(GroupMemberPage.this);

                } else {

                    final String removedMemberId = dataSnapshot.getKey();

                    for (int index = 0; index < mSearchList.size(); index++) {

                        String memberId = mSearchList.get(index).getMember().getUserId();

                        if (removedMemberId.equals(memberId)) {

                            mSearchList.remove(index);
                            mSearchAdapter.notifyItemRemoved(index);
                            mSearchAdapter.notifyItemRangeChanged(index, mSearchAdapter.getItemCount());

                            break;

                        }
                    }

                    for (int index = 0; index < mMemberList.size(); index++) {

                        String member_id = mMemberList.get(index).getMember().getUserId();

                        if (removedMemberId.equals(member_id)) {

                            mMemberList.remove(index);
                            mMemberAdapter.notifyItemRemoved(index);
                            mMemberAdapter.notifyItemRangeChanged(index, mMemberAdapter.getItemCount());

                            break;

                        }
                    }

                    closeDialogWithThisId(removedMemberId);

                    getSupportActionBar().setSubtitle(mMemberList.size() + " thành viên");

                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mCurrentMemberRef.addChildEventListener(mGroupMemberChildEvent);
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

                    rv_members.setVisibility(View.GONE);

                    //show clear button
                    btn_clear_keyword.setVisibility(View.VISIBLE);

                    mSearchHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            //load data
                            mSearchList.clear();

                            for (int index = 0; index < mMemberList.size(); index++)
                                if (isMatchingUser(keyword, mMemberList.get(index).getMember()))
                                    mSearchList.add(mMemberList.get(index));

                            mSearchAdapter.notifyDataSetChanged();


                            //hide progressbar
                            pb_loading.setVisibility(View.GONE);

                            rv_members.setVisibility(View.VISIBLE);

                        }
                    }, SEARCH_DELAY_TIME);

                } else {

                    btn_clear_keyword.setVisibility(View.INVISIBLE);

                    // recovery friend list
                    mSearchList.clear();

                    mSearchList.addAll(mMemberList);

                    mSearchAdapter.notifyDataSetChanged();

                    //hide progressbar
                    pb_loading.setVisibility(View.GONE);

                    rv_members.setVisibility(View.VISIBLE);

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

        //set search adapter
        mSearchList.clear();

        mSearchList.addAll(mMemberList);

        mSearchAdapter.notifyDataSetChanged();

        rv_members.setAdapter(mSearchAdapter);

        edt_search.requestFocus();

        showKeyboard(GroupMemberPage.this);

        //remove handler
        mSearchHandler.removeCallbacksAndMessages(null);


    }

    private void disableSearchMode() {

        isSearching = false;

        //hide keyboard

        hideKeyboard(GroupMemberPage.this);

        edt_search.setText(null);

        edt_search.clearFocus();

        //view nonactive search bar
        active_searchbar.setVisibility(View.GONE);

        appBarLayout.setVisibility(View.VISIBLE);

        nonactive_searchbar.setVisibility(View.VISIBLE);

        pb_loading.setVisibility(View.GONE);

        // set friend adapter

        mSearchList.clear();

        mSearchAdapter.notifyDataSetChanged();

        rv_members.setAdapter(mMemberAdapter);

        rv_members.setVisibility(View.VISIBLE);


        //remove handlder
        mSearchHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_button_menu, menu);

        MenuItem item = menu.findItem(R.id.item_one_button);
        item.setIcon(R.drawable.ic_add_member_filled);
        item.setTitle("Thêm thành viên");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            case R.id.item_one_button:

                Intent it_add = new Intent(GroupMemberPage.this, AddGroupMember.class);
                it_add.putExtra(INTENT_KEY_GROUP_ID, mGroupId);
                startActivity(it_add);

                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSearchHandler != null) mSearchHandler.removeCallbacksAndMessages(null);

        if (mGroupMemberChildEvent != null && mCurrentMemberRef != null) mCurrentMemberRef.removeEventListener(mGroupMemberChildEvent);


    }

    @Override
    public void onBackPressed() {

        if (isSearching)

            disableSearchMode();

        else super.onBackPressed();
    }

    private void closeDialogWithThisId(String userId) {

        if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
            if (mUsingMemberId != null && mUsingMemberId.equals(userId)) {
                mOptionsDialog.dismiss();
            }
        }

    }

    private void showOptions(User memberProfile, boolean isAdmin) {

        ArrayList<String> options = new ArrayList<>();

        final String option0 = "Xem trang cá nhân";
        final String option1 = "Chọn làm quản trị viên";
        final String option2 = "Nhắn tin";
        final String option3 = "Gọi thoại";
        final String option4 = "Gọi video";
        final String option5 = "Xóa khỏi nhóm";
        final String option6 = "Chặn";
        final String option7 = "Hủy";

        options.add(option0);
        if (isAdmin) options.add(option1);
        options.add(option2);
        options.add(option3);
        options.add(option4);
        if (isAdmin) options.add(option5);
        options.add(option6);
        options.add(option7);

        ArrayAdapter<String> optionsAdapter = new ArrayAdapter(GroupMemberPage.this, android.R.layout.simple_list_item_1, options);

        mOptionsDialog = new AlertDialog.Builder(GroupMemberPage.this)
                .setTitle(memberProfile.getName())
                .setAdapter(optionsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (options.get(which)) {

                            case option0:
                                viewUserProfile(GroupMemberPage.this, memberProfile.getUserId());
                                break;

                            case option1:
                                showChangeAdminConfirmDialog(GroupMemberPage.this, memberProfile.getUserId(), mGroupId);
                                break;

                            case option2:
                                goToChat(GroupMemberPage.this, memberProfile);
                                break;

                            case option3:
                                makeCall(GroupMemberPage.this, memberProfile.getUserId(), false);
                                break;

                            case option4:
                                makeCall(GroupMemberPage.this, memberProfile.getUserId(), true);
                                break;

                            case option5:
                                deleteMember(GroupMemberPage.this, memberProfile.getUserId(), mGroupId);
                                break;

                            case option6:
                                checkBeforeBLockUser(GroupMemberPage.this, memberProfile.getUserId());
                                break;

                            default:

                        }

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mUsingMemberId = null;
                    }
                }).create();


        mUsingMemberId = memberProfile.getUserId();

        mOptionsDialog.show();

    }


}
