package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.SearchAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.SearchResult;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.PagerAdapters.SearchPagerAdapter;
import com.example.tranquoctrungcntt.uchat.R;
import com.example.tranquoctrungcntt.uchat.SearchFraments.SearchFriendFragment;
import com.example.tranquoctrungcntt.uchat.SearchFraments.SearchGroupFragment;
import com.example.tranquoctrungcntt.uchat.SearchFraments.SearchPeopleFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEARCH_HISTORY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.SEARCH_DELAY_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewGroupDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;


public class Search extends BaseActivity {


    private static final String keySearchTime = "searchTime";
    private static final String keySearchName = "searchName";
    private static final String keySearchAvatar = "searchAvatar";

    private final Handler mSearchHandler = new Handler();

    private final SearchPeopleFragment fragment_search_people = new SearchPeopleFragment();
    private final SearchFriendFragment fragment_search_friend = new SearchFriendFragment();
    private final SearchGroupFragment fragment_search_group = new SearchGroupFragment();

    private FrameLayout btn_back;
    private FrameLayout btn_clear;
    private FrameLayout btn_history;
    private EditText edt_search;
    private TextView tv_tab_search_people;
    private TextView tv_tab_search_friends;
    private TextView tv_tab_search_groups;
    private LinearLayout linear_search_history;
    private LinearLayout linear_tabs_search;
    private ViewPager vp_search;
    private RecyclerView rv_history;
    private ArrayList<SearchResult> mHistoryList;
    private SearchAdapter mHistoryAdapter;

    private ChildEventListener mSearchHistoryChildEvent;
    private Query mSearchHistoryQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        initViews();

        initClickEvents();

        setupSearchBar();

        if (mSearchHistoryChildEvent != null && mSearchHistoryQuery != null) mSearchHistoryQuery.removeEventListener(mSearchHistoryChildEvent);

        mSearchHistoryQuery = ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).orderByChild(keySearchTime).limitToLast(10);

        mSearchHistoryChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final SearchResult searchResult = dataSnapshot.getValue(SearchResult.class);

                mHistoryList.add(0, searchResult);
                mHistoryAdapter.notifyItemInserted(0);

                if (searchResult.isSearchGroup()) {

                    getGroupDetail(searchResult.getSearchId(), new AppConstants.AppInterfaces.FirebaseGroupDetailCallBack() {
                        @Override
                        public void OnCallBack(GroupDetail callbackGroupDetail) {

                            if (callbackGroupDetail != null) {

                                Map<String, Object> map = new HashMap<>();

                                map.put(keySearchName, callbackGroupDetail.getGroupName());
                                map.put(keySearchAvatar, callbackGroupDetail.getGroupThumbAvatar());

                                ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).child(searchResult.getSearchId()).updateChildren(map);

                            }
                        }
                    });

                } else {

                    getSingleUserProfile(searchResult.getSearchId(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                        @Override
                        public void OnCallBack(User callbackUserProfile) {

                            if (callbackUserProfile != null) {

                                Map<String, Object> map = new HashMap<>();

                                map.put(keySearchName, callbackUserProfile.getName());
                                map.put(keySearchAvatar, callbackUserProfile.getThumbAvatarUrl());

                                ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).child(searchResult.getSearchId()).updateChildren(map);

                            }
                        }
                    });

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final SearchResult updatedSearchResult = dataSnapshot.getValue(SearchResult.class);

                for (int index = 0; index < mHistoryList.size(); index++) {

                    if (mHistoryList.get(index).getSearchId().equals(updatedSearchResult.getSearchId())) {

                        mHistoryList.set(index, updatedSearchResult);
                        mHistoryAdapter.notifyItemChanged(index);

                        Collections.sort(mHistoryList, Collections.reverseOrder(new Comparator<SearchResult>() {
                            @Override
                            public int compare(SearchResult o1, SearchResult o2) {
                                return Long.compare(o1.getSearchTime(), o2.getSearchTime());
                            }
                        }));

                        mHistoryAdapter.notifyItemRangeChanged(0, mHistoryAdapter.getItemCount());

                        break;
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for (int index = 0; index < mHistoryList.size(); index++) {

                    if (mHistoryList.get(index).getSearchId().equals(dataSnapshot.getKey())) {

                        mHistoryList.remove(index);
                        mHistoryAdapter.notifyItemRemoved(index);
                        mHistoryAdapter.notifyItemRangeChanged(index, mHistoryAdapter.getItemCount());

                        break;

                    }

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mSearchHistoryQuery.addChildEventListener(mSearchHistoryChildEvent);

    }

    private void setupSearchBar() {

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int ACTON, KeyEvent keyEvent) {

                if (ACTON == EditorInfo.IME_ACTION_SEARCH) hideKeyboard(Search.this);

                return true;
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

                final String keyword = edt_search.getText().toString();

                if (keyword.length() > 0) {

                    btn_history.setVisibility(View.GONE);
                    linear_search_history.setVisibility(View.GONE);

                    btn_clear.setVisibility(View.VISIBLE);
                    linear_tabs_search.setVisibility(View.VISIBLE);
                    vp_search.setVisibility(View.VISIBLE);

                    fragment_search_people.showProgessBar();
                    fragment_search_friend.showProgessBar();
                    fragment_search_group.showProgressBar();

                    mSearchHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            fragment_search_people.searchPeople(keyword);
                            fragment_search_friend.searchFriends(keyword);
                            fragment_search_group.searchGroups(keyword);

                        }
                    }, SEARCH_DELAY_TIME);


                } else {

                    fragment_search_people.stopSearching();
                    fragment_search_friend.stopSearching();
                    fragment_search_group.stopSearching();

                    btn_history.setVisibility(View.VISIBLE);
                    linear_search_history.setVisibility(View.VISIBLE);

                    btn_clear.setVisibility(View.GONE);
                    linear_tabs_search.setVisibility(View.GONE);
                    vp_search.setVisibility(View.GONE);

                    mSearchHandler.removeCallbacksAndMessages(null);
                }


            }
        });
    }

    private void initViews() {

        edt_search = (EditText) findViewById(R.id.edt_search);

        edt_search.requestFocus();

        tv_tab_search_people = (TextView) findViewById(R.id.tv_tab_search_people);

        tv_tab_search_friends = (TextView) findViewById(R.id.tv_tab_search_friends);

        tv_tab_search_groups = (TextView) findViewById(R.id.tv_tab_search_groups);

        linear_search_history = (LinearLayout) findViewById(R.id.linear_recent_search);

        linear_tabs_search = (LinearLayout) findViewById(R.id.linear_tabs_search);

        SearchPagerAdapter searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager(),
                fragment_search_people,
                fragment_search_friend,
                fragment_search_group
        );

        vp_search = (ViewPager) findViewById(R.id.vp_search_results);
        vp_search.setOffscreenPageLimit(3);
        vp_search.setAdapter(searchPagerAdapter);
        vp_search.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pos) {
                switch (pos) {
                    case 0:
                        tv_tab_search_people.setBackgroundResource(R.drawable.filled_light_grey_bg);
                        tv_tab_search_friends.setBackgroundResource(0);
                        tv_tab_search_groups.setBackgroundResource(0);
                        break;
                    case 1:
                        tv_tab_search_friends.setBackgroundResource(R.drawable.filled_light_grey_bg);
                        tv_tab_search_people.setBackgroundResource(0);
                        tv_tab_search_groups.setBackgroundResource(0);
                        break;
                    case 2:
                        tv_tab_search_groups.setBackgroundResource(R.drawable.filled_light_grey_bg);
                        tv_tab_search_friends.setBackgroundResource(0);
                        tv_tab_search_people.setBackgroundResource(0);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mHistoryList = new ArrayList<>();
        mHistoryAdapter = new SearchAdapter(Search.this, mHistoryList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final SearchResult searchResult = mHistoryList.get(position);

                if (searchResult.isSearchGroup()) {

                    viewGroupDetail(Search.this, searchResult.getSearchId());

                } else viewUserProfile(Search.this, searchResult.getSearchId());


            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_history = (RecyclerView) findViewById(R.id.rv_recent_searches);
        rv_history.setHasFixedSize(true);
        rv_history.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));
        rv_history.setAdapter(mHistoryAdapter);
        rv_history.setItemAnimator(null);

        btn_back = (FrameLayout) findViewById(R.id.frame_back);
        btn_clear = (FrameLayout) findViewById(R.id.frame_clear_keyword);
        btn_history = (FrameLayout) findViewById(R.id.frame_history);

    }

    private void initClickEvents() {


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(Search.this);

                finish();

            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_search.setText("");
            }
        });


        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search.this, com.example.tranquoctrungcntt.uchat.Activities.SearchHistory.class));
            }
        });

        tv_tab_search_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vp_search.setCurrentItem(0, false);
            }
        });

        tv_tab_search_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_search.setCurrentItem(1, false);
            }
        });

        tv_tab_search_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_search.setCurrentItem(2, false);
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSearchHandler != null) mSearchHandler.removeCallbacksAndMessages(null);

        if (mSearchHistoryChildEvent != null && mSearchHistoryQuery != null) mSearchHistoryQuery.removeEventListener(mSearchHistoryChildEvent);

    }


}
