package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.AllUserFriendAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.MutualFriend;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;

public class AllUserFriend extends BaseActivity {

    private Toolbar mToolbar;

    private RecyclerView rv_user_friends;
    private ArrayList<MutualFriend> mUserFriendsList;
    private AllUserFriendAdapter mUserFriendsAdapter;

    private String mUserId;

    private ValueEventListener mValueEventRelationship;

    private DatabaseReference mRelationshipRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user_friend);

        mUserId = (String) getDataFromIntent(AllUserFriend.this, INTENT_KEY_USER_ID);

        if (mUserId != null) {

            initViews();

            ROOT_REF.child(CHILD_RELATIONSHIPS)
                    .child(mUserId).orderByChild(CHILD_FRIEND).equalTo(true)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapFriends) {

                            ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapRelationships) {

                                    for (DataSnapshot snapshot : snapFriends.getChildren()) {

                                        if (!snapRelationships.child(snapshot.getKey()).hasChild(CHILD_I_BLOCKED_USER)
                                                && !snapRelationships.child(snapshot.getKey()).hasChild(CHILD_USER_BLOCKED_ME)) {

                                            getSingleUserProfile(snapshot.getKey(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                @Override
                                                public void OnCallBack(User callbackUserProfile) {

                                                    if (callbackUserProfile != null) {

                                                        if (callbackUserProfile.getUserId().equals(getMyFirebaseUserId()))
                                                            callbackUserProfile.setName(callbackUserProfile.getName() + " (bạn)");

                                                        final MutualFriend mutualFriend = new MutualFriend(callbackUserProfile, snapRelationships.child(snapshot.getKey()).hasChild(CHILD_FRIEND));

                                                        mUserFriendsList.add(mutualFriend);
                                                        mUserFriendsAdapter.notifyItemInserted(mUserFriendsList.size() - 1);

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

        } else finish();


    }

    private void showUserLockedMeDialog() {

        if (!AllUserFriend.this.isFinishing()) {

            new AlertDialog.Builder(this)
                    .setMessage("Người dùng không tồn tại !")
                    .setPositiveButton("Đồng ý", null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    }).create().show();

        }

    }

    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tất cả bạn bè");

        mUserFriendsList = new ArrayList<>();
        mUserFriendsAdapter = new AllUserFriendAdapter(AllUserFriend.this, mUserFriendsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                if (!mUserFriendsList.get(position).getFriendProfile().getUserId().equals(getMyFirebaseUserId()))
                    goToChat(AllUserFriend.this, mUserFriendsList.get(position).getFriendProfile());
            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_user_friends = (RecyclerView) findViewById(R.id.rv_user_friends);
        rv_user_friends.setLayoutManager(new LinearLayoutManager(AllUserFriend.this, LinearLayoutManager.VERTICAL, false));
        rv_user_friends.setAdapter(mUserFriendsAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mValueEventRelationship != null && mRelationshipRef != null) mRelationshipRef.removeEventListener(mValueEventRelationship);

        if (mUserId != null) {

            mRelationshipRef = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).child(mUserId);

            mValueEventRelationship = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapRelationship) {

                    if (snapRelationship.hasChild(CHILD_USER_BLOCKED_ME)) showUserLockedMeDialog();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mRelationshipRef.addValueEventListener(mValueEventRelationship);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mValueEventRelationship != null && mRelationshipRef != null) mRelationshipRef.removeEventListener(mValueEventRelationship);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }

}
