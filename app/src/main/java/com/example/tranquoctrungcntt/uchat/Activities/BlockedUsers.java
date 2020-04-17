package com.example.tranquoctrungcntt.uchat.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.BlockedUserAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;


public class BlockedUsers extends BaseActivity {

    private Toolbar mToolbar;

    private RecyclerView rv_blocked;
    private ArrayList<User> mBlockedList;
    private BlockedUserAdapter mBlockedAdapter;

    private ChildEventListener mBlockedUserChildEvent;
    private Query mBlockedUserQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);

        initViews();

        if (mBlockedUserChildEvent != null && mBlockedUserQuery != null) mBlockedUserQuery.removeEventListener(mBlockedUserChildEvent);

        mBlockedUserQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_I_BLOCKED_USER).equalTo(true);

        mBlockedUserChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                getSingleUserProfile(dataSnapshot.getKey(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User callbackUserProfile) {

                        if (callbackUserProfile != null) {

                            mBlockedList.add(callbackUserProfile);
                            mBlockedAdapter.notifyItemInserted(mBlockedList.size() - 1);

                        }

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                final int i = searchUser(mBlockedList, dataSnapshot.getKey());
                if (hasItemInList(i)) {
                    mBlockedList.remove(i);
                    mBlockedAdapter.notifyItemRemoved(i);
                    mBlockedAdapter.notifyItemRangeChanged(i, mBlockedAdapter.getItemCount());
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mBlockedUserQuery.addChildEventListener(mBlockedUserChildEvent);

    }

    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Người dùng đã chặn");

        mBlockedList = new ArrayList<>();
        mBlockedAdapter = new BlockedUserAdapter(this, mBlockedList);

        rv_blocked = (RecyclerView) findViewById(R.id.rv_blocked_users);
        rv_blocked.setHasFixedSize(true);
        rv_blocked.setLayoutManager(new LinearLayoutManager(BlockedUsers.this, LinearLayoutManager.VERTICAL, false));
        rv_blocked.setAdapter(mBlockedAdapter);

        disableChangeAnimation(rv_blocked);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBlockedUserChildEvent != null && mBlockedUserQuery != null) mBlockedUserQuery.removeEventListener(mBlockedUserChildEvent);

    }


}
