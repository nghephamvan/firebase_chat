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
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.SearchResult;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEARCH_HISTORY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewGroupDetail;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends Fragment {

    private RecyclerView rv_search_groups;


    private ArrayList<SearchResult> mSearchGroupsList;
    private SearchAdapter mSearchGroupsAdapter;

    private ProgressBar pb_loading;

    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search_groups, container, false);

        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);

        mSearchGroupsList = new ArrayList<>();
        mSearchGroupsAdapter = new SearchAdapter(getActivity(), mSearchGroupsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final SearchResult searchResult = mSearchGroupsList.get(position);

                searchResult.setSearchTime(getCurrentTimeInMilies());

                ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).child(searchResult.getSearchId()).setValue(searchResult);

                viewGroupDetail(getActivity(), searchResult.getSearchId());

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });


        rv_search_groups = (RecyclerView) view.findViewById(R.id.rv_search_groups);
        rv_search_groups.setHasFixedSize(true);
        rv_search_groups.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        rv_search_groups.setAdapter(mSearchGroupsAdapter);

        return view;
    }

    public void showProgressBar() {
        if (pb_loading != null && rv_search_groups != null) {
            pb_loading.setVisibility(View.VISIBLE);
            rv_search_groups.setVisibility(View.GONE);
        }
    }

    private void hideProgessBar() {
        if (pb_loading != null && rv_search_groups != null) {
            pb_loading.setVisibility(View.GONE);
            rv_search_groups.setVisibility(View.VISIBLE);
        }
    }

    public void stopSearching() {
        mSearchGroupsList.clear();
        mSearchGroupsAdapter.notifyDataSetChanged();
    }

    public void searchGroups(String keyword) {

        ROOT_REF.child(CHILD_GROUP_DETAIL)
                .child(getMyFirebaseUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotGroups) {

                        mSearchGroupsList.clear();

                        for (DataSnapshot snapshot : dataSnapshotGroups.getChildren()) {

                            final GroupDetail groupDetail = snapshot.getValue(GroupDetail.class);

                            if (groupDetail != null) {

                                if (isMatchingGroup(keyword, groupDetail)) {

                                    SearchResult searchResult = new SearchResult(groupDetail.getGroupId(), groupDetail.getGroupName(), groupDetail.getGroupThumbAvatar(), 0, true);

                                    mSearchGroupsList.add(searchResult);

                                }
                            }
                        }

                        mSearchGroupsAdapter.notifyDataSetChanged();

                        hideProgessBar();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private boolean isMatchingGroup(String keyword, GroupDetail groupDetail) {

        return groupDetail.getGroupName().toLowerCase()
                .contains(keyword.toLowerCase());

    }
}
