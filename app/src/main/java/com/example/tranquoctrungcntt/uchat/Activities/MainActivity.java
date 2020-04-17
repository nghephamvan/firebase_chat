package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Fragments.AccountFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.ContactFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.ConversationFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.MakingFriendFragment;
import com.example.tranquoctrungcntt.uchat.Models.StickerToShow;
import com.example.tranquoctrungcntt.uchat.Notification.Token;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.OtherClasses.AppLocalDatabase;
import com.example.tranquoctrungcntt.uchat.PagerAdapters.MainPagerAdapter;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.CONNECT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_EGG_STICKER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_STICKERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_TOKENS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_RECEIVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_SINGLE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_VIDEO_CALL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.VIDEO_CALL_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kLastSeen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setConnectedToFirebase;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.ableToUseApp;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isGroupInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isGroupMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getAndroidID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isVideoCallPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.isFirstOpenMain;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.shouldShowRequestPermissionWhenLogin;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.updateFirstOpenMain;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.updateShouldShowRequestPermissionWhenLogin;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.keepSyncAll;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.markMessageIsSent;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class MainActivity extends BaseActivity {

    private final MakingFriendFragment mMakeFriendFragment = new MakingFriendFragment();
    private final ContactFragment mContactFragment = new ContactFragment();
    private final ConversationFragment mConversationFragment = new ConversationFragment();
    private final AccountFragment mAccountFragment = new AccountFragment();
    private final ValueEventListener mDatabaseConnectionValueEvent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            final boolean isConnected = dataSnapshot.getValue(Boolean.class);

            if (isAccountValid()) {

                if (isConnected) {

                    markOnline();

                } else markOffline();

            }

            setConnectedToFirebase(isConnected);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private FrameLayout btn_search;
    private FrameLayout btn_new_conv;
    private FrameLayout btn_scan_qr;
    private TextView tv_title;
    private ViewPager mMainViewPager;
    private MainPagerAdapter mMainPagerAdapter;
    private BottomNavigationView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ableToUseApp(MainActivity.this)) {

            if (!isFirstOpenMain(MainActivity.this)) FirebaseDatabase.getInstance().goOffline();

            initViews();

            initClickEvents();

            keepSyncAll(true);

            requestPermissionForTheFirstTime();

            if (!isFirstOpenMain(MainActivity.this)) FirebaseDatabase.getInstance().goOnline();

            if (isFirstOpenMain(MainActivity.this)) updateFirstOpenMain(MainActivity.this);

            CONNECT_REF.removeEventListener(mDatabaseConnectionValueEvent);

            CONNECT_REF.addValueEventListener(mDatabaseConnectionValueEvent);

            updateToken();

            repairStickers();

            markAllMessagesAreReceived();

            sendAllMessageFromLocalDatabase();

            performNotificationAction(getIntent());

        }

    }

    private void updateToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (task.isSuccessful()) {

                    ROOT_REF.child(CHILD_TOKENS)
                            .child(getMyFirebaseUserId()).child(getAndroidID(getApplicationContext()))
                            .setValue(new Token(task.getResult().getToken()));

                }

            }
        });
    }

    private void markOnline() {

        final DatabaseReference ACTIVE_REF = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId());

        ACTIVE_REF.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                final User user = dataSnapshot.getValue(User.class);

                final User tempOnline = new User(user);

                tempOnline.setLastSeen(-1);

                ACTIVE_REF.setValue(tempOnline);

            }
        });


    }

    private void markOffline() {

        final DatabaseReference ACTIVE_REF = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId()).child(kLastSeen);

        final long lastSeen = getCurrentTimeInMilies();

        ACTIVE_REF.setValue(lastSeen);

        ACTIVE_REF.onDisconnect().setValue(lastSeen);

    }

    private void repairStickers() {

        final AppLocalDatabase appLocalDatabase = new AppLocalDatabase(MainActivity.this);

        appLocalDatabase.deleteAllSticker();

        ROOT_REF.child(CHILD_STICKERS).child(CHILD_EGG_STICKER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            StickerToShow stickerToShow = snapshot.getValue(StickerToShow.class);

                            appLocalDatabase.addSticker(stickerToShow);

                            Glide.with(getApplicationContext())
                                    .load(stickerToShow.getStickerUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                    .submit();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void initViews() {

        tv_title = (TextView) findViewById(R.id.tv_main_title);

        btn_search = (FrameLayout) findViewById(R.id.frame_main_search);
        btn_new_conv = (FrameLayout) findViewById(R.id.frame_new_conv);
        btn_scan_qr = (FrameLayout) findViewById(R.id.frame_scan_qr);

        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),
                mConversationFragment,
                mContactFragment,
                mMakeFriendFragment,
                mAccountFragment);

        mMainViewPager = (ViewPager) findViewById(R.id.vp_main);
        mBottomBar = (BottomNavigationView) findViewById(R.id.main_bottombar);

        mMainViewPager.setAdapter(mMainPagerAdapter);
        mMainViewPager.setOffscreenPageLimit(4);
        mMainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomBar.setSelectedItemId(R.id.item_conversation);
                        break;
                    case 1:
                        mBottomBar.setSelectedItemId(R.id.item_contact);
                        break;
                    case 2:
                        mBottomBar.setSelectedItemId(R.id.item_make_friend);
                        break;
                    case 3:
                        mBottomBar.setSelectedItemId(R.id.item_account);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.item_conversation:
                        tv_title.setText("Trò chuyện");
                        btn_new_conv.setVisibility(View.VISIBLE);
                        btn_scan_qr.setVisibility(View.GONE);
                        mMainViewPager.setCurrentItem(0);
                        return true;
                    case R.id.item_contact:
                        tv_title.setText("Liên hệ");
                        btn_new_conv.setVisibility(View.GONE);
                        btn_scan_qr.setVisibility(View.VISIBLE);
                        mMainViewPager.setCurrentItem(1);
                        return true;
                    case R.id.item_make_friend:
                        tv_title.setText("Kết bạn");
                        btn_new_conv.setVisibility(View.GONE);
                        btn_scan_qr.setVisibility(View.VISIBLE);
                        mMainViewPager.setCurrentItem(2);
                        return true;
                    case R.id.item_account:
                        tv_title.setText("Tài khoản");
                        btn_new_conv.setVisibility(View.GONE);
                        btn_scan_qr.setVisibility(View.VISIBLE);
                        mMainViewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });
        mBottomBar.setSelectedItemId(R.id.item_conversation);

    }

    private void initClickEvents() {

        btn_new_conv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, CreateConversation.class));

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, Search.class));

            }
        });

        btn_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanQRCode.class));
            }
        });


    }

    private void performNotificationAction(Intent intent) {

        if (intent != null && intent.getExtras() != null) {

            getNotificationManager(MainActivity.this).cancelAll();

            Intent it = new Intent(MainActivity.this, MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(it);

        }

    }

    private void sendAllMessageFromLocalDatabase() {

        final AppLocalDatabase appLocalDatabase = new AppLocalDatabase(MainActivity.this);

        final ArrayList<Message> messages = appLocalDatabase.getAllMessage();

        ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot relationshipSnapshot) {

                for (Message myMessage : messages) {

                    if (!relationshipSnapshot.child(myMessage.getReceiverId()).hasChild(CHILD_I_BLOCKED_USER)
                            && !relationshipSnapshot.child(myMessage.getReceiverId()).hasChild(CHILD_USER_BLOCKED_ME)) {

                        final Message userMessage = new Message(
                                myMessage.getMessageId(), getMyFirebaseUserId(),
                                myMessage.getReceiverId(), myMessage.getGroupId(),
                                myMessage.getContent(), MESSAGE_STATUS_SENT,
                                myMessage.getSendTime(), 0, myMessage.getType(),
                                myMessage.getNotificationId(), myMessage.getCallDuration(),
                                myMessage.getSticker(), myMessage.isForwardedMessage(),
                                myMessage.getRelatedUserId(), myMessage.getMessageViewer(), myMessage.getEditHistory());

                        ROOT_REF.child(CHILD_MESSAGES)
                                .child(getMyFirebaseUserId()).child(myMessage.getReceiverId())
                                .child(myMessage.getMessageId()).setValue(myMessage)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        appLocalDatabase.deleteMessage(myMessage.getMessageId());

                                        markMessageIsSent(myMessage.getMessageId(), myMessage.getReceiverId(), myMessage.getGroupId());

                                        ROOT_REF.child(CHILD_MESSAGES)
                                                .child(myMessage.getReceiverId()).child(getMyFirebaseUserId())
                                                .child(myMessage.getMessageId()).setValue(userMessage)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                            @Override
                                                            public void OnCallBack(User callbackUserProfile) {

                                                                sendNotificationToUser(
                                                                        myMessage.getReceiverId(),
                                                                        myMessage.getSenderId(),
                                                                        myMessage.getGroupId(),
                                                                        myMessage.getMessageId(),
                                                                        myMessage.getContent(),
                                                                        callbackUserProfile.getName(),
                                                                        callbackUserProfile.getThumbAvatarUrl(),
                                                                        NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                                        myMessage.getNotificationId());

                                                            }
                                                        });

                                                    }
                                                });

                                    }

                                });

                    } else appLocalDatabase.deleteMessage(myMessage.getMessageId());


                }
            }
        });
    }

    private void markAllMessagesAreReceived() {

        ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                for (DataSnapshot snapConversationId : dataSnapshot.getChildren()) {

                    ROOT_REF.child(CHILD_MESSAGES)
                            .child(getMyFirebaseUserId()).child(snapConversationId.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshotMessages) {

                                    for (DataSnapshot snapMessage : dataSnapshotMessages.getChildren()) {

                                        Message messageToCheck = snapMessage.getValue(Message.class);

                                        markReceived(messageToCheck);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

    }

    private void markReceived(Message messageToCheck) {

        boolean isReceiver = !messageToCheck.getSenderId().equals(getMyFirebaseUserId());

        if (isReceiver) {

            if (isGroupMessage(messageToCheck)) {

                if (!isGroupInstantMessage(messageToCheck)) {

                    markReceivedForMe(messageToCheck);

                    markReceivedForSender(messageToCheck);
                }

            } else {

                markReceivedForMe(messageToCheck);

                markReceivedForSender(messageToCheck);
            }
        }

    }

    private void markReceivedForSender(Message messageToCheck) {

        final String chatId = isGroupMessage(messageToCheck) ? messageToCheck.getGroupId() : messageToCheck.getReceiverId();

        final DatabaseReference senderMessageStatusRef = ROOT_REF.child(CHILD_MESSAGES)
                .child(messageToCheck.getSenderId()).child(chatId)
                .child(messageToCheck.getMessageId()).child(kMessageStatus);

        senderMessageStatusRef.runTransaction(new Transaction.Handler() {
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
                        senderMessageStatusRef.setValue(MESSAGE_STATUS_RECEIVED);
                    }

                }

            }
        });
    }

    private void markReceivedForMe(Message messageToCheck) {

        final String chatId = isGroupMessage(messageToCheck) ? messageToCheck.getGroupId() : messageToCheck.getSenderId();

        final DatabaseReference myMessageStatusRef = ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(chatId)
                .child(messageToCheck.getMessageId()).child(kMessageStatus);

        myMessageStatusRef.runTransaction(new Transaction.Handler() {
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
                        myMessageStatusRef.setValue(MESSAGE_STATUS_RECEIVED);
                    }
                }

            }
        });
    }

    public void logoutFromMainActivity() {
        logoutFromBaseActivity();
    }

    private void requestPermissionForTheFirstTime() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (shouldShowRequestPermissionWhenLogin(MainActivity.this)) {

                if (isVideoCallPermissionsGranted(MainActivity.this)) {

                    //all permissions granted

                } else showRequestPermissionDialog();

                updateShouldShowRequestPermissionWhenLogin(MainActivity.this, false);
            }
        }
    }

    private void showRequestPermissionDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Để có thể nhận các cuộc gọi từ bạn bè. Vui lòng cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Cấp quyền", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_VIDEO_CALL, VIDEO_CALL_CODE);

                    }
                }).setNegativeButton("Để sau", null).create().show();

    }

    public void updateReceivedRequestBadges(int num) {

        BadgeDrawable badgeDrawable = mBottomBar.getOrCreateBadge(R.id.item_make_friend);
        badgeDrawable.setMaxCharacterCount(3);
        badgeDrawable.setBackgroundColor(getResources().getColor(R.color.badge_color));
        if (num > 0) badgeDrawable.setNumber(num);
        else mBottomBar.removeBadge(R.id.item_make_friend);


    }

    public void updateConversationBadges(int num) {

        BadgeDrawable badgeDrawable = mBottomBar.getOrCreateBadge(R.id.item_conversation);
        badgeDrawable.setMaxCharacterCount(3);
        badgeDrawable.setBackgroundColor(getResources().getColor(R.color.badge_color));
        if (num > 0) badgeDrawable.setNumber(num);
        else mBottomBar.removeBadge(R.id.item_conversation);

    }

    public void updateContactBadges(int num) {

        BadgeDrawable badgeDrawable = mBottomBar.getOrCreateBadge(R.id.item_contact);
        badgeDrawable.setMaxCharacterCount(3);
        badgeDrawable.setBackgroundColor(getResources().getColor(R.color.badge_color));
        if (num > 0) badgeDrawable.setNumber(num);
        else mBottomBar.removeBadge(R.id.item_conversation);

    }

    public void updateConversationGroupDetail(GroupDetail updatedGroup) {
        mConversationFragment.updateConversationGroupProfile(updatedGroup);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        performNotificationAction(intent);
    }

    @Override
    public void onBackPressed() {

        if (mBottomBar.getSelectedItemId() != R.id.item_conversation)
            mBottomBar.setSelectedItemId(R.id.item_conversation);
        else moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CONNECT_REF.removeEventListener(mDatabaseConnectionValueEvent);
    }
}
