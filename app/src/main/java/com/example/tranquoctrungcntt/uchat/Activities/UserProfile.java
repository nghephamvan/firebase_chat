package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.UserFriendAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.MutualFriend;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_BLACKLIST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_CANCEL_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_PREVENTED_THIS_USER_FROM_MAKING_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MUTE_NOTIFICATIONS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_RECEIVER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_SENDER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEND_FRIEND_REQUEST_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_NEW_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_MILIES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isUserOnline;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.acceptFriendRequest;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkBeforeBLockUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.denyFriendRequest;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.showCancelRequestConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.showUnfriendConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.makeCall;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.allowMakeGroup;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.allowNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.preventMakeGroup;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.SettingAction.preventNotifyMe;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewAvatar;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class UserProfile extends BaseActivity {

    private CircleImageView user_avatar;
    private CircleImageView active_dot;
    private EmojiAppCompatTextView tv_status;

    private Button btn_view_friends;

    private ImageView img_muted;
    private ImageView img_no_group;
    private ImageView img_gender;

    private TextView tv_dateofbirth;
    private TextView tv_gender;
    private TextView tv_datejoined;
    private TextView tv_name;
    private TextView tv_same_friends;

    private LinearLayout linear_addfriend;
    private LinearLayout linear_unfriend;
    private LinearLayout linear_voice;
    private LinearLayout linear_video;
    private LinearLayout linear_chat;
    private LinearLayout linear_more;
    private LinearLayout linear_cancel;

    private LinearLayout linear_call;
    private LinearLayout card_user_friends;
    private LinearLayout card_user_request;

    private Button btn_accept;
    private Button btn_deny;

    private RecyclerView rv_user_friends;
    private ArrayList<MutualFriend> mUserFriendsList;
    private UserFriendAdapter mUserFriendsAdapter;

    private FrameLayout btn_back;

    private AlertDialog mOptionsDialog;

    private ValueEventListener mRelationshipValueEvent;
    private ValueEventListener mSettingValueEvent;
    private ValueEventListener mUserProfileValueEvent;

    private DatabaseReference mRelationshipRef;
    private DatabaseReference mSettingsRef;
    private DatabaseReference mUserProfileRef;

    private boolean isNoMakingGroup;
    private boolean isMute;
    private int mFriendCounter;
    private String mUserId;
    private User mUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);

        mUserId = (String) getDataFromIntent(UserProfile.this, INTENT_KEY_USER_ID);

        if (mUserId != null) {

            initViews();

            initClickEvents();

            loadUserFriends();

        } else finish();

    }

    private void loadUserFriends() {

        ROOT_REF.child(CHILD_RELATIONSHIPS)
                .child(mUserId).orderByChild(CHILD_FRIEND).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapFriends) {

                        mFriendCounter = 0;

                        mUserFriendsList.clear();
                        mUserFriendsAdapter.notifyDataSetChanged();

                        ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapRelationships) {

                                        for (DataSnapshot snapshot : snapFriends.getChildren()) {

                                            if (!snapRelationships.child(snapshot.getKey()).hasChild(CHILD_I_BLOCKED_USER)
                                                    && !snapRelationships.child(snapshot.getKey()).hasChild(CHILD_USER_BLOCKED_ME)) {


                                                getSingleUserProfile(snapshot.getKey(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                    @Override
                                                    public void OnCallBack(User callbackUserProfile) {

                                                        if (callbackUserProfile != null) {

                                                            if (snapRelationships.child(snapshot.getKey()).hasChild(CHILD_FRIEND)) {
                                                                mFriendCounter++;
                                                                tv_same_friends.setText(mFriendCounter + " bạn chung");
                                                                tv_same_friends.setVisibility(View.VISIBLE);
                                                            }

                                                            if (callbackUserProfile.getUserId().equals(getMyFirebaseUserId()))
                                                                callbackUserProfile.setName(callbackUserProfile.getName() + " (bạn)");

                                                            MutualFriend mutualFriend = new MutualFriend(callbackUserProfile, snapRelationships.child(snapshot.getKey()).hasChild(CHILD_FRIEND));

                                                            mUserFriendsList.add(mutualFriend);
                                                            mUserFriendsAdapter.notifyItemInserted(mUserFriendsList.size() - 1);

                                                            if (card_user_friends.getVisibility() == View.GONE)
                                                                card_user_friends.setVisibility(View.VISIBLE);

                                                            if (mUserFriendsList.size() <= 6)
                                                                btn_view_friends.setVisibility(View.GONE);
                                                            else btn_view_friends.setVisibility(View.VISIBLE);

                                                        }

                                                    }
                                                });


                                            }

                                        }

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

    private void initViews() {

        user_avatar = (CircleImageView) findViewById(R.id.civ_avatar);

        btn_view_friends = (Button) findViewById(R.id.btn_see_all);
        btn_back = (FrameLayout) findViewById(R.id.frame_back);

        img_no_group = (ImageView) findViewById(R.id.img_no_group);
        img_muted = (ImageView) findViewById(R.id.img_muted);
        img_gender = (ImageView) findViewById(R.id.img_gender);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_status = (EmojiAppCompatTextView) findViewById(R.id.tv_status);
        tv_dateofbirth = (TextView) findViewById(R.id.tv_dateofbirth);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_datejoined = (TextView) findViewById(R.id.tv_datejoined);
        tv_same_friends = (TextView) findViewById(R.id.tv_same_friends);
        active_dot = (CircleImageView) findViewById(R.id.civ_active_dot);

        linear_call = (LinearLayout) findViewById(R.id.linear_call);

        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_deny = (Button) findViewById(R.id.btn_deny);

        linear_cancel = (LinearLayout) findViewById(R.id.linear_cancel);
        linear_addfriend = (LinearLayout) findViewById(R.id.linear_addfriend);
        linear_unfriend = (LinearLayout) findViewById(R.id.linear_unfriend);
        linear_chat = (LinearLayout) findViewById(R.id.linear_chat);
        linear_voice = (LinearLayout) findViewById(R.id.linear_voice_call);
        linear_video = (LinearLayout) findViewById(R.id.linear_video_call);
        linear_more = (LinearLayout) findViewById(R.id.linear_more);

        card_user_friends = (LinearLayout) findViewById(R.id.card_user_friends);
        card_user_request = (LinearLayout) findViewById(R.id.card_user_friend_request);

        mUserFriendsList = new ArrayList<>();
        mUserFriendsAdapter = new UserFriendAdapter(UserProfile.this, mUserFriendsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                if (!mUserFriendsList.get(position).getFriendProfile().getUserId().equals(getMyFirebaseUserId()))
                    viewUserProfile(UserProfile.this, mUserFriendsList.get(position).getFriendProfile().getUserId());
            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_user_friends = (RecyclerView) findViewById(R.id.rv_user_friends);
        rv_user_friends.setLayoutManager(new GridLayoutManager(UserProfile.this, 3));
        rv_user_friends.setAdapter(mUserFriendsAdapter);

    }

    private void initClickEvents() {

        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewAvatar(UserProfile.this, mUserProfile.getAvatarUrl());

            }
        });

        linear_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addFriend();

            }
        });

        linear_unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUnfriendConfirmDialog(UserProfile.this, mUserId);

            }
        });


        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                acceptFriendRequest(UserProfile.this, mUserId);

            }
        });

        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                denyFriendRequest(UserProfile.this, mUserId);
            }
        });

        linear_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCancelRequestConfirmDialog(UserProfile.this, mUserId);

            }
        });

        linear_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(UserProfile.this, mUserId, false);
            }
        });

        linear_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(UserProfile.this, mUserId, true);
            }
        });

        linear_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goToChat(UserProfile.this, mUserProfile);

            }
        });

        linear_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions();
            }
        });

        btn_view_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(UserProfile.this, AllUserFriend.class);
                it.putExtra(INTENT_KEY_USER_ID, mUserId);
                startActivity(it);
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showOptions() {

        ArrayList<String> options = new ArrayList<>();

        final String option0 = "Cho phép thông báo";
        final String option1 = "Tắt thông báo";
        final String option2 = "Cho phép tạo nhóm";
        final String option3 = "Ngăn tạo nhóm với tôi";
        final String option4 = "Chặn người này";
        final String option5 = "Hủy";

        if (isMute) options.add(option0);
        else options.add(option1);

        if (isNoMakingGroup) options.add(option2);
        else options.add(option3);

        options.add(option4);
        options.add(option5);

        ArrayAdapter<String> optionAdapter = new ArrayAdapter(UserProfile.this, android.R.layout.simple_list_item_1, options);

        mOptionsDialog = new AlertDialog.Builder(UserProfile.this)
                .setTitle("Tuỳ chọn")
                .setAdapter(optionAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (options.get(which)) {

                            case option0:
                                allowNotifyMe(UserProfile.this, mUserId);
                                break;

                            case option1:
                                preventNotifyMe(UserProfile.this, mUserId);
                                break;

                            case option2:
                                allowMakeGroup(UserProfile.this, mUserId);
                                break;

                            case option3:
                                preventMakeGroup(UserProfile.this, mUserId);
                                break;

                            case option4:
                                checkBeforeBLockUser(UserProfile.this, mUserId);
                                break;

                            default:

                        }

                    }
                }).create();

        mOptionsDialog.show();

    }

    private void showIBlockedUserDialog() {


        new AlertDialog.Builder(UserProfile.this)
                .setMessage("Bạn đã chặn người dùng này, vui lòng bỏ chặn để có thể xem trang cá nhân của họ.")
                .setPositiveButton("Đã hiểu", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                }).create().show();
    }

    private void showUserBlockedMeDialog() {

        if (mOptionsDialog != null && mOptionsDialog.isShowing()) mOptionsDialog.dismiss();

        new AlertDialog.Builder(UserProfile.this)
                .setMessage("Người dùng không tồn tại !")
                .setPositiveButton("Đã hiểu", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                }).create().show();

    }

    private void addFriend() {

        if (isConnectedToFirebaseService(UserProfile.this)) {

            ROOT_REF.child(CHILD_SEND_FRIEND_REQUEST_TIMER)
                    .child(getMyFirebaseUserId()).child(mUserId)
                    .child(CHILD_CANCEL_TIME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            long timeToWait = -1;

                            if (dataSnapshot.exists()) {

                                long timePassed = getCurrentTimeInMilies() - dataSnapshot.getValue(Long.class);

                                if (timePassed <= TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_MILIES) {
                                    timeToWait = TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_MILIES - timePassed;
                                }

                            }

                            if (timeToWait == -1) {

                                sendFriendRequest();

                            } else
                                showMessageDialog(UserProfile.this, "Bạn phải chờ " + TimeUnit.MILLISECONDS.toMinutes(timeToWait) + " phút nữa để có thể gửi lại lời mời !");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        } else showNoConnectionDialog(UserProfile.this);
    }

    private void sendFriendRequest() {

        ROOT_REF.child(CHILD_RELATIONSHIPS)
                .child(getMyFirebaseUserId()).child(mUserId)
                .child(CHILD_REQUEST_SENDER)
                .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                ROOT_REF.child(CHILD_RELATIONSHIPS)
                        .child(getMyFirebaseUserId()).child(mUserId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild(CHILD_FRIEND)
                                        || dataSnapshot.hasChild(CHILD_I_BLOCKED_USER)
                                        || dataSnapshot.hasChild(CHILD_USER_BLOCKED_ME)
                                        || dataSnapshot.hasChild(CHILD_REQUEST_RECEIVER)) {

                                    ROOT_REF.child(CHILD_RELATIONSHIPS)
                                            .child(getMyFirebaseUserId()).child(mUserId)
                                            .child(CHILD_REQUEST_SENDER)
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            showMessageDialog(UserProfile.this, "Lỗi xử lý, vui lòng thử lại !");

                                        }
                                    });

                                } else {

                                    final Map<String, Object> map = new HashMap<>();

                                    map.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + mUserId + "/" + CHILD_BLACKLIST, null);
                                    map.put("/" + CHILD_SEND_FRIEND_REQUEST_TIMER + "/" + getMyFirebaseUserId() + "/" + mUserId + "/" + CHILD_CANCEL_TIME, null);
                                    map.put("/" + CHILD_RELATIONSHIPS + "/" + mUserId + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_RECEIVER, true);

                                    ROOT_REF.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                @Override
                                                public void OnCallBack(User callbackUserProfile) {

                                                    sendNotificationToUser(
                                                            mUserId,
                                                            getMyFirebaseUserId(),
                                                            null,
                                                            null,
                                                            "Đã gửi cho bạn lời mời kết bạn.",
                                                            callbackUserProfile.getName(),
                                                            callbackUserProfile.getThumbAvatarUrl(),
                                                            NOTIFICATION_TYPE_NEW_FRIEND_REQUEST,
                                                            getNotificationsId());

                                                }
                                            });

                                        }
                                    });


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSettingValueEvent != null && mSettingsRef != null) mSettingsRef.removeEventListener(mSettingValueEvent);

        if (mUserProfileValueEvent != null && mUserProfileRef != null) mUserProfileRef.removeEventListener(mUserProfileValueEvent);

        if (mRelationshipValueEvent != null && mRelationshipRef != null) mRelationshipRef.removeEventListener(mRelationshipValueEvent);

        if (mUserId != null) {

            mSettingsRef = ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId()).child(mUserId);

            mUserProfileRef = ROOT_REF.child(CHILD_USERS).child(mUserId);

            mRelationshipRef = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).child(mUserId);

            mSettingValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(CHILD_MUTE_NOTIFICATIONS)) {
                        img_muted.setVisibility(View.VISIBLE);
                        isMute = true;
                    }


                    if (!dataSnapshot.hasChild(CHILD_MUTE_NOTIFICATIONS)) {
                        img_muted.setVisibility(View.GONE);
                        isMute = false;
                    }


                    if (dataSnapshot.hasChild(CHILD_I_PREVENTED_THIS_USER_FROM_MAKING_GROUP)) {
                        img_no_group.setVisibility(View.VISIBLE);
                        isNoMakingGroup = true;

                    }


                    if (!dataSnapshot.hasChild(CHILD_I_PREVENTED_THIS_USER_FROM_MAKING_GROUP)) {
                        img_no_group.setVisibility(View.GONE);
                        isNoMakingGroup = false;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mUserProfileValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final User user = dataSnapshot.getValue(User.class);

                    setAvatarToView(UserProfile.this, user.getThumbAvatarUrl(), user.getName(), user_avatar);

                    tv_name.setText(user.getName());

                    tv_status.setText(user.getStatus() != null ? "❝ " + user.getStatus() + " ❞" : null);

                    tv_datejoined.setText(user.getJoinedDate());

                    tv_dateofbirth.setText(user.getDateofbirth());

                    tv_gender.setText(user.getGender());

                    active_dot.setVisibility(isUserOnline(user.getLastSeen()) ? View.VISIBLE : View.GONE);

                    img_gender.setImageResource(user.getGender().equals("Nam") ? R.drawable.ic_boy_tritone : R.drawable.ic_girl_tritone);

                    mUserProfile = user;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mRelationshipValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot_root) {

                    if (dataSnapshot_root.hasChild(CHILD_USER_BLOCKED_ME)) {

                        showUserBlockedMeDialog();

                    } else if (dataSnapshot_root.hasChild(CHILD_I_BLOCKED_USER)) {

                        showIBlockedUserDialog();

                    } else {

                        mSettingsRef.addValueEventListener(mSettingValueEvent);

                        mUserProfileRef.addValueEventListener(mUserProfileValueEvent);

                        if (dataSnapshot_root.hasChild(CHILD_REQUEST_SENDER)) {

                            card_user_request.setVisibility(View.GONE);
                            linear_addfriend.setVisibility(View.GONE);
                            linear_unfriend.setVisibility(View.GONE);
                            linear_call.setVisibility(View.GONE);

                            linear_chat.setVisibility(View.VISIBLE);
                            linear_more.setVisibility(View.VISIBLE);
                            linear_cancel.setVisibility(View.VISIBLE);

                        } else if (dataSnapshot_root.hasChild(CHILD_REQUEST_RECEIVER)) {

                            //mình là người nhận yêu cầu

                            linear_addfriend.setVisibility(View.GONE);
                            linear_unfriend.setVisibility(View.GONE);
                            linear_cancel.setVisibility(View.GONE);
                            linear_call.setVisibility(View.GONE);

                            card_user_request.setVisibility(View.VISIBLE);
                            linear_chat.setVisibility(View.VISIBLE);
                            linear_more.setVisibility(View.VISIBLE);

                        } else if (dataSnapshot_root.hasChild(CHILD_FRIEND)) {

                            //bạn bè
                            card_user_request.setVisibility(View.GONE);
                            linear_addfriend.setVisibility(View.GONE);
                            linear_cancel.setVisibility(View.GONE);

                            linear_unfriend.setVisibility(View.VISIBLE);
                            linear_chat.setVisibility(View.VISIBLE);
                            linear_more.setVisibility(View.VISIBLE);
                            linear_call.setVisibility(View.VISIBLE);

                        } else { // not friends

                            card_user_request.setVisibility(View.GONE);
                            linear_unfriend.setVisibility(View.GONE);
                            linear_cancel.setVisibility(View.GONE);
                            linear_call.setVisibility(View.GONE);

                            linear_addfriend.setVisibility(View.VISIBLE);
                            linear_chat.setVisibility(View.VISIBLE);
                            linear_more.setVisibility(View.VISIBLE);

                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mRelationshipRef.addValueEventListener(mRelationshipValueEvent);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mSettingValueEvent != null && mSettingsRef != null) mSettingsRef.removeEventListener(mSettingValueEvent);

        if (mUserProfileValueEvent != null && mUserProfileRef != null) mUserProfileRef.removeEventListener(mUserProfileValueEvent);

        if (mRelationshipValueEvent != null && mRelationshipRef != null) mRelationshipRef.removeEventListener(mRelationshipValueEvent);

    }


}
