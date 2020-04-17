package com.example.tranquoctrungcntt.uchat.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.FriendRequestAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_RECEIVER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;


public class FriendRequest extends BaseActivity {

    private Toolbar mToolbar;

    private RecyclerView rv_requests;
    private ArrayList<User> mRequestList;
    private FriendRequestAdapter mRequestsAdapter;

    private ChildEventListener mFriendRequestChildEvent;
    private Query mFriendRequestQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        initViews();

        if (mFriendRequestChildEvent != null && mFriendRequestQuery != null) mFriendRequestQuery.removeEventListener(mFriendRequestChildEvent);

        mFriendRequestQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_REQUEST_RECEIVER).equalTo(true);

        mFriendRequestChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                getSingleUserProfile(dataSnapshot.getKey(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User callbackUserProfile) {

                        if (callbackUserProfile != null) {

                            mRequestList.add(callbackUserProfile);
                            mRequestsAdapter.notifyItemInserted(mRequestList.size() - 1);

                        }


                    }
                });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                final int i = searchUser(mRequestList, dataSnapshot.getKey());
                if (hasItemInList(i)) {
                    mRequestList.remove(i);
                    mRequestsAdapter.notifyItemRemoved(i);
                    mRequestsAdapter.notifyItemRangeChanged(i, mRequestsAdapter.getItemCount());
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mFriendRequestQuery.addChildEventListener(mFriendRequestChildEvent);

    }


    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lời mời kết bạn");

        mRequestList = new ArrayList<>();

        mRequestsAdapter = new FriendRequestAdapter(FriendRequest.this, mRequestList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                viewUserProfile(FriendRequest.this, mRequestList.get(position).getUserId());
            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_requests = (RecyclerView) findViewById(R.id.rv_friend_requests);
        rv_requests.setLayoutManager(new LinearLayoutManager(
                FriendRequest.this, LinearLayoutManager.VERTICAL, false));
        rv_requests.setHasFixedSize(true);
        rv_requests.setAdapter(mRequestsAdapter);

        disableChangeAnimation(rv_requests);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mFriendRequestChildEvent != null && mFriendRequestQuery != null) mFriendRequestQuery.removeEventListener(mFriendRequestChildEvent);

    }


}
