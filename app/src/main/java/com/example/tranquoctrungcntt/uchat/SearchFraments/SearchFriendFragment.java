package com.example.tranquoctrungcntt.uchat.SearchFraments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.SearchAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.SearchResult;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEARCH_HISTORY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMatchingUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFriendFragment extends Fragment {


    private RecyclerView rv_search_friends;
    private ArrayList<SearchResult> mSearchFriendsList;
    private SearchAdapter mSearchFriendsAdapter;
    private ProgressBar pb_loading;
    private Map<String, User> mFriendProfileMap;

    public SearchFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search_friends, container, false);


        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);

        mSearchFriendsList = new ArrayList<>();

        mSearchFriendsAdapter = new SearchAdapter(getActivity(), mSearchFriendsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final SearchResult searchResult = mSearchFriendsList.get(position);

                searchResult.setSearchTime(getCurrentTimeInMilies());

                ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).child(searchResult.getSearchId()).setValue(searchResult);

                viewUserProfile(getActivity(), searchResult.getSearchId());

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_search_friends = (RecyclerView) view.findViewById(R.id.rv_search_friends);
        rv_search_friends.setHasFixedSize(true);
        rv_search_friends.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rv_search_friends.setAdapter(mSearchFriendsAdapter);

        mFriendProfileMap = new HashMap<>();

        return view;
    }


    public void showProgessBar() {
        if (pb_loading != null && rv_search_friends != null) {
            pb_loading.setVisibility(View.VISIBLE);
            rv_search_friends.setVisibility(View.GONE);
        }
    }

    private void hideProgressBar() {
        if (pb_loading != null && rv_search_friends != null) {
            pb_loading.setVisibility(View.GONE);
            rv_search_friends.setVisibility(View.VISIBLE);
        }
    }

    public void stopSearching() {
        mSearchFriendsList.clear();
        mSearchFriendsAdapter.notifyDataSetChanged();
    }

    public void searchFriends(String keyword) {

        ROOT_REF.child(CHILD_RELATIONSHIPS)
                .child(getMyFirebaseUserId()).orderByChild(CHILD_FRIEND).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotFriends) {

                        mSearchFriendsList.clear();
                        mSearchFriendsAdapter.notifyDataSetChanged();

                        for (DataSnapshot snapshot : dataSnapshotFriends.getChildren()) {

                            if (mFriendProfileMap.get(snapshot.getKey()) == null) {

                                getSingleUserProfile(snapshot.getKey(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                    @Override
                                    public void OnCallBack(User callbackUserProfile) {

                                        if (callbackUserProfile != null) {

                                            mFriendProfileMap.put(snapshot.getKey(), callbackUserProfile);

                                            if (isMatchingUser(keyword, callbackUserProfile)) {

                                                SearchResult search = new SearchResult(callbackUserProfile.getUserId(), callbackUserProfile.getName(), callbackUserProfile.getThumbAvatarUrl(), 0, false);

                                                mSearchFriendsList.add(search);
                                                mSearchFriendsAdapter.notifyItemInserted(mSearchFriendsList.size() - 1);

                                            }
                                        }
                                    }
                                });

                            } else {
                                if (isMatchingUser(keyword, mFriendProfileMap.get(snapshot.getKey()))) {

                                    SearchResult search = new SearchResult(mFriendProfileMap.get(snapshot.getKey()).getUserId(), mFriendProfileMap.get(snapshot.getKey()).getName(), mFriendProfileMap.get(snapshot.getKey()).getThumbAvatarUrl(), 0, false);

                                    mSearchFriendsList.add(search);
                                    mSearchFriendsAdapter.notifyItemInserted(mSearchFriendsList.size() - 1);

                                }
                            }
                        }

                        hideProgressBar();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


}
