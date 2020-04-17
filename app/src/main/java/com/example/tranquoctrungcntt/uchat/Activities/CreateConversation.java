package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.UserSinglePickAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.SEARCH_DELAY_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMatchingUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showKeyboard;


public class CreateConversation extends BaseActivity {

    private final Handler mSearchHandler = new Handler();

    private Toolbar mToolbar;

    private LinearLayout nonactive_searchbar;

    private AppBarLayout active_searchbar;
    private AppBarLayout appBarLayout;

    private EditText edt_search;
    private RecyclerView rv_friends;

    private ArrayList<User> mFriendList;
    private ArrayList<User> mSearchList;

    private UserSinglePickAdapter mSearchAdapter;
    private UserSinglePickAdapter mFriendAdapter;

    private FrameLayout btn_clear_keyword;
    private FrameLayout btn_back;

    private boolean isSearching;
    private ProgressBar pb_loading;

    private ChildEventListener mFriendChildEvent;
    private Query mFriendQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conversation);

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

                //remove handler

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

        //set search adapter
        mSearchList.clear();

        mSearchList.addAll(mFriendList);

        mSearchAdapter.notifyDataSetChanged();

        rv_friends.setAdapter(mSearchAdapter);

        edt_search.requestFocus();

        showKeyboard(CreateConversation.this);

        //remove handler
        mSearchHandler.removeCallbacksAndMessages(null);
    }

    private void disableSearchMode() {

        isSearching = false;
//hide keyboard
        hideKeyboard(CreateConversation.this);

        edt_search.setText(null);

        edt_search.clearFocus();
        //view nonactive search bar
        active_searchbar.setVisibility(View.GONE);

        appBarLayout.setVisibility(View.VISIBLE);

        nonactive_searchbar.setVisibility(View.VISIBLE);

        mSearchList.clear();

        mSearchAdapter.notifyDataSetChanged();

        rv_friends.setAdapter(mFriendAdapter);

        rv_friends.setVisibility(View.VISIBLE);

        pb_loading.setVisibility(View.GONE);

        //remove handler
        mSearchHandler.removeCallbacksAndMessages(null);
    }


    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nhóm mới");

        mFriendList = new ArrayList<>();
        mSearchList = new ArrayList<>();

        mFriendAdapter = new UserSinglePickAdapter(CreateConversation.this, mFriendList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final User clickedUser = mFriendList.get(position);

                goToChat(CreateConversation.this, clickedUser);

                finish();

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });

        mSearchAdapter = new UserSinglePickAdapter(CreateConversation.this, mSearchList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final User clickedUser = mSearchList.get(position);

                goToChat(CreateConversation.this, clickedUser);

                finish();


            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }

        });

        rv_friends = (RecyclerView) findViewById(R.id.rv_create_conversation);
        rv_friends.setHasFixedSize(true);
        rv_friends.setLayoutManager(new LinearLayoutManager(CreateConversation.this, LinearLayoutManager.VERTICAL, false));
        rv_friends.setAdapter(mFriendAdapter);
        rv_friends.setItemAnimator(null);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSearchHandler != null) mSearchHandler.removeCallbacksAndMessages(null);

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.one_button_menu, menu);
        menu.findItem(R.id.item_one_button).setTitle("TẠO NHÓM");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            case R.id.item_one_button:

                Intent it = new Intent(this, CreateGroup.class);
                startActivity(it);
                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (isSearching) disableSearchMode();

        else super.onBackPressed();

    }


}
