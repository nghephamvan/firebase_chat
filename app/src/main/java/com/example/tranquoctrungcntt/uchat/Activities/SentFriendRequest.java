package com.example.tranquoctrungcntt.uchat.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.SentRequestAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_SENDER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;


public class SentFriendRequest extends BaseActivity {

    private Toolbar mToolbar;

    private RecyclerView rv_request;
    private ArrayList<User> mRequestList;
    private SentRequestAdapter mRequestsAdapter;

    private ChildEventListener mSentRequestChildEvent;
    private Query mSentRequestQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_friend_request);

        initView();

        if (mSentRequestChildEvent != null && mSentRequestQuery != null) mSentRequestQuery.removeEventListener(mSentRequestChildEvent);

        mSentRequestQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_REQUEST_SENDER).equalTo(true);

        mSentRequestChildEvent = new ChildEventListener() {
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

        mSentRequestQuery.addChildEventListener(mSentRequestChildEvent);

    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Yêu cầu đã gửi");

        mRequestList = new ArrayList<>();

        mRequestsAdapter = new SentRequestAdapter(SentFriendRequest.this, mRequestList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                viewUserProfile(SentFriendRequest.this, mRequestList.get(position).getUserId());
            }

            @Override
            public void OnItemLongClick(View v, int position) { }
        });

        rv_request = (RecyclerView) findViewById(R.id.rv_sent_requests);
        rv_request.setHasFixedSize(true);
        rv_request.setLayoutManager(
                new LinearLayoutManager(SentFriendRequest.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_request.setAdapter(mRequestsAdapter);

        disableChangeAnimation(rv_request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSentRequestChildEvent != null && mSentRequestQuery != null) mSentRequestQuery.removeEventListener(mSentRequestChildEvent);

    }


}
