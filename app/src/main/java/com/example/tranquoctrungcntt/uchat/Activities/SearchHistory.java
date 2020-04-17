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

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.SearchHistoryAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.SearchResult;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewGroupDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;


public class SearchHistory extends BaseActivity {

    private static final String keySearchTime = "searchTime";
    private static final String keySearchName = "searchName";
    private static final String keySearchAvatar = "searchAvatar";

    private Toolbar mToolbar;

    private RecyclerView rv_history;
    private ArrayList<SearchResult> mHistoryList;
    private SearchHistoryAdapter mHistoryAdapter;

    private ChildEventListener mHistoryChildEvent;
    private Query mSearchHistoryQuery;

    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        initViews();

        if (mHistoryChildEvent != null && mSearchHistoryQuery != null) mSearchHistoryQuery.removeEventListener(mHistoryChildEvent);

        mSearchHistoryQuery = ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).orderByChild(keySearchTime);

        mHistoryChildEvent = new ChildEventListener() {
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

        mSearchHistoryQuery.addChildEventListener(mHistoryChildEvent);

    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lịch sử tìm kiếm");

        mHistoryList = new ArrayList<>();

        mHistoryAdapter = new SearchHistoryAdapter(SearchHistory.this, mHistoryList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final SearchResult searchResult = mHistoryList.get(position);

                if (searchResult.isSearchGroup()) {

                    viewGroupDetail(SearchHistory.this, searchResult.getSearchId());

                } else viewUserProfile(SearchHistory.this, searchResult.getSearchId());

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_history = (RecyclerView) findViewById(R.id.rv_search_history);
        rv_history.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(SearchHistory.this, LinearLayoutManager.VERTICAL, false);
        rv_history.setLayoutManager(mLinearLayoutManager);
        rv_history.setAdapter(mHistoryAdapter);

        disableChangeAnimation(rv_history);


    }


    private void showClearHistoryConfirmDialog() {

        new AlertDialog.Builder(SearchHistory.this)
                .setTitle("Xóa toàn bộ lịch sử")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả lịch sử tìm kiếm không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        ROOT_REF.child(CHILD_SEARCH_HISTORY)
                                .child(getMyFirebaseUserId()).removeValue();

                    }
                }).setNegativeButton("Không", null).create().show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHistoryChildEvent != null && mSearchHistoryQuery != null) mSearchHistoryQuery.removeEventListener(mHistoryChildEvent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_button_menu, menu);
        menu.findItem(R.id.item_one_button).setTitle("XÓA LỊCH SỬ");
        menu.findItem(R.id.item_one_button).setIcon(R.drawable.ic_trash_filled_blue);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_one_button:

                if (mHistoryList.isEmpty()) {

                    showLongToast(SearchHistory.this, "Lịch sử trống !");

                } else showClearHistoryConfirmDialog();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
