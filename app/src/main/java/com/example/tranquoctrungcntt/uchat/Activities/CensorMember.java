package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.CensorMemberAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.GroupMemberModel;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.GroupMember;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_JOIN_GROUP_REQUESTS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_INVALID_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_VALID_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupMember;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetailWithTransaction;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupMemberSnapshot;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;

public class CensorMember extends BaseActivity {

    private Toolbar mToolbar;

    private RecyclerView rv_join_request;
    private ArrayList<GroupMemberModel> mJoinRequestList;
    private CensorMemberAdapter mJoinRequestAdapter;

    private Map<String, GroupMemberModel> mAcceptedMap;
    private Map<String, Boolean> mDeniedMap;

    private ChildEventListener mJoinRequestChildEvent;
    private DatabaseReference mJoinRequestRef;

    private AlertDialog mLoadingDialog;

    private String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_censor_member);

        if (mJoinRequestChildEvent != null && mJoinRequestRef != null) mJoinRequestRef.removeEventListener(mJoinRequestChildEvent);

        mGroupId = (String) getDataFromIntent(CensorMember.this, INTENT_KEY_GROUP_ID);

        if (mGroupId != null) {

            initViews();

            mJoinRequestRef = ROOT_REF.child(CHILD_JOIN_GROUP_REQUESTS).child(mGroupId);

            mJoinRequestChildEvent = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    final GroupMember groupMember = dataSnapshot.getValue(GroupMember.class);

                    final String memberId = groupMember.getMemberId();
                    final String adderId = groupMember.getAdderId();
                    final String role = groupMember.getRole();

                    getSingleUserProfile(memberId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                        @Override
                        public void OnCallBack(User memberProfile) {

                            if (memberProfile != null) {

                                getSingleUserProfile(adderId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                    @Override
                                    public void OnCallBack(User adderProfile) {

                                        if (adderProfile != null) {

                                            GroupMemberModel member = new GroupMemberModel(memberProfile, adderProfile, role);

                                            mJoinRequestList.add(member);
                                            mJoinRequestAdapter.notifyItemInserted(mJoinRequestList.size() - 1);

                                        }

                                    }
                                });

                            }

                        }
                    });
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

            mJoinRequestRef.addChildEventListener(mJoinRequestChildEvent);

        } else finish();

    }


    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Duyệt thành viên");

        mJoinRequestList = new ArrayList<>();

        mAcceptedMap = new HashMap<>();
        mDeniedMap = new HashMap<>();

        mJoinRequestAdapter = new CensorMemberAdapter(CensorMember.this, mJoinRequestList, mAcceptedMap, mDeniedMap, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                viewUserProfile(CensorMember.this, mJoinRequestList.get(position).getMember().getUserId());
            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_join_request = (RecyclerView) findViewById(R.id.rv_join_request);
        rv_join_request.setLayoutManager(new LinearLayoutManager(CensorMember.this, LinearLayoutManager.VERTICAL, false));
        rv_join_request.setHasFixedSize(true);
        rv_join_request.setAdapter(mJoinRequestAdapter);

        disableChangeAnimation(rv_join_request);

        mLoadingDialog = getLoadingBuilder(CensorMember.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_button_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (mAcceptedMap.size() > 0 || mDeniedMap.size() > 0)

                    showCancelConfirmDialog();

                else finish();

                break;

            case R.id.item_one_button:

                if (isConnectedToFirebaseService(CensorMember.this)) {

                    if (mDeniedMap.size() > 0) {

                        final Map<String, Object> map = new HashMap<>();

                        for (String deniedUserId : mDeniedMap.keySet()) map.put(deniedUserId, null);

                        ROOT_REF.child(CHILD_JOIN_GROUP_REQUESTS).child(mGroupId).updateChildren(map);

                    }

                    if (mAcceptedMap.isEmpty()) {

                        finish();

                    } else {

                        mLoadingDialog.show();

                        checkBeforeAdd(mGroupId, new FirebaseGroupMemberShipCallBack() {
                            @Override
                            public void OnCallBack(Map<String, ArrayList<GroupMemberModel>> groupMemberModelMap) {

                                if (groupMemberModelMap.get(KEY_VALID_USER).isEmpty()) {

                                    mLoadingDialog.dismiss();

                                    showInvalidUsersDialog(groupMemberModelMap.get(KEY_INVALID_USER));

                                } else {

                                    getGroupDetailWithTransaction(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                                        @Override
                                        public void OnCallBack(GroupDetail callbackGroupDetail) {

                                            addToGroup(callbackGroupDetail, groupMemberModelMap.get(KEY_VALID_USER), groupMemberModelMap.get(KEY_INVALID_USER));

                                        }
                                    });
                                }
                            }
                        });

                    }


                } else showNoConnectionDialog(CensorMember.this);


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkBeforeAdd(String groupId, FirebaseGroupMemberShipCallBack firebaseGroupMemberShipCallBack) {

        getGroupMemberSnapshot(groupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
            @Override
            public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                final ArrayList<GroupMemberModel> invalidList = new ArrayList<>();
                final ArrayList<GroupMemberModel> validList = new ArrayList<>();

                for (GroupMemberModel membership : mAcceptedMap.values()) {

                    if (groupMemberSnapshot.hasChild(membership.getMember().getUserId())) {
                        invalidList.add(membership);
                    } else validList.add(membership);

                }

                Map<String, ArrayList<GroupMemberModel>> map = new HashMap<>();

                map.put(KEY_VALID_USER, validList);
                map.put(KEY_INVALID_USER, invalidList);

                firebaseGroupMemberShipCallBack.OnCallBack(map);
            }
        });

    }

    private void addToGroup(GroupDetail groupDetail, ArrayList<GroupMemberModel> validMembers, ArrayList<GroupMemberModel> invalidMembers) {

        final Map<String, Boolean> relatedUserIdMap = new HashMap<>();

        for (GroupMemberModel user : validMembers) relatedUserIdMap.put(user.getMember().getUserId(), true);

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();


        final Map<String, Object> mapForPreviousMember = new HashMap<>();

        for (GroupMemberModel addedUser : validMembers) {

            for (GroupMember previousMember : groupDetail.getMember().values()) {

                final GroupMember memberShip = new GroupMember(addedUser.getMember().getUserId(), addedUser.getAdder().getUserId(), addedUser.getRole());

                final Message message = buildInstantMessage(messageId, previousMember.getMemberId(), mGroupId,
                        "Đã cho phép " + relatedUserIdMap.size() + " thành viên mới tham gia nhóm",
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

                                for (GroupMemberModel user : validMembers) {

                                    final Message message = buildInstantMessage(messageId, user.getMember().getUserId(), mGroupId,
                                            "Đã cho phép " + relatedUserIdMap.size() + " thành viên mới tham gia nhóm",
                                            MESSAGE_TYPE_UPDATE_MEMBER, notificationId, relatedUserIdMap);

                                    mapForAddedMember.put("/" + CHILD_JOIN_GROUP_REQUESTS + "/" + mGroupId + "/" + user.getMember().getUserId(), null);
                                    mapForAddedMember.put("/" + CHILD_GROUP_DETAIL + "/" + user.getMember().getUserId() + "/" + mGroupId, updatedGroup);
                                    mapForAddedMember.put("/" + CHILD_MESSAGES + "/" + user.getMember().getUserId() + "/" + mGroupId + "/" + messageId, message);

                                }

                                ROOT_REF.updateChildren(mapForAddedMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mLoadingDialog.dismiss();

                                        for (GroupMemberModel user : validMembers) {

                                            sendNotificationToUser(
                                                    user.getMember().getUserId(),
                                                    getMyFirebaseUserId(),
                                                    mGroupId,
                                                    messageId,
                                                    "Đã cho phép bạn tham gia nhóm trò chuyện.",
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

    private void showCancelConfirmDialog() {

        new AlertDialog.Builder(CensorMember.this)
                .setMessage("Bạn có chắc chắn muốn huỷ phiên kiểm duyệt yêu cầu tham gia này không ? ")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Không", null)
                .create().show();

    }

    @Override
    public void onBackPressed() {
        if (mAcceptedMap.size() > 0 || mDeniedMap.size() > 0)

            showCancelConfirmDialog();

        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mJoinRequestChildEvent != null && mJoinRequestRef != null) mJoinRequestRef.removeEventListener(mJoinRequestChildEvent);

    }

    private String getListFullName(List<GroupMemberModel> userList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int position = 0; position < userList.size(); position++) {
            String fullname = userList.get(position).getMember().getName();
            if (position == userList.size() - 1)
                stringBuilder.append(fullname).append(".");
            else stringBuilder.append(fullname).append(", ");

        }
        return stringBuilder.toString();
    }

    private void showInvalidUsersDialog(ArrayList<GroupMemberModel> invalidMembers) {

        new AlertDialog.Builder(CensorMember.this)
                .setMessage("Không thể thêm: " + getListFullName(invalidMembers) + ".")
                .setPositiveButton("Đã hiểu", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!CensorMember.this.isFinishing()) finish();
                    }
                }).create().show();

    }

    private interface FirebaseGroupMemberShipCallBack {
        void OnCallBack(Map<String, ArrayList<GroupMemberModel>> groupMemberModelMap);
    }

}
