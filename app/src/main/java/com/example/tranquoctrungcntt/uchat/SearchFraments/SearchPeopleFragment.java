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

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEARCH_HISTORY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kVerified;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMatchingUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPeopleFragment extends Fragment {

    private RecyclerView rv_search_people;
    private ArrayList<SearchResult> mSearchPeopleList;
    private SearchAdapter mSearchPeopleAdapter;
    private ProgressBar pb_loading;

    public SearchPeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search_people, container, false);

        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);

        mSearchPeopleList = new ArrayList<>();
        mSearchPeopleAdapter = new SearchAdapter(getActivity(), mSearchPeopleList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                final SearchResult searchResult = mSearchPeopleList.get(position);

                searchResult.setSearchTime(getCurrentTimeInMilies());

                ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).child(searchResult.getSearchId()).setValue(searchResult);

                viewUserProfile(getActivity(), searchResult.getSearchId());

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        rv_search_people = (RecyclerView) view.findViewById(R.id.rv_search_people);
        rv_search_people.setHasFixedSize(true);
        rv_search_people.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        rv_search_people.setAdapter(mSearchPeopleAdapter);


        return view;
    }


    public void showProgessBar() {
        if (pb_loading != null && rv_search_people != null) {
            pb_loading.setVisibility(View.VISIBLE);
            rv_search_people.setVisibility(View.GONE);
        }

    }

    private void hideProgressBar() {

        if (pb_loading != null && rv_search_people != null) {
            pb_loading.setVisibility(View.GONE);
            rv_search_people.setVisibility(View.VISIBLE);
        }
    }

    public void stopSearching() {
        mSearchPeopleList.clear();
        mSearchPeopleAdapter.notifyDataSetChanged();
    }


    public void searchPeople(String keyword) {

        ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotRelationships) {

                ROOT_REF.child(CHILD_USERS).orderByChild(kVerified).equalTo(true)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                mSearchPeopleList.clear();

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    final User user = snapshot.getValue(User.class);

                                    if (!user.getUserId().equals(getMyFirebaseUserId()) && user.isVerified()) {

                                        if (!snapshotRelationships.child(user.getUserId()).hasChild(CHILD_USER_BLOCKED_ME)
                                                && !snapshotRelationships.child(user.getUserId()).hasChild(CHILD_I_BLOCKED_USER)) {

                                            if (isMatchingUser(keyword, user)) {

                                                SearchResult search = new SearchResult(user.getUserId(), user.getName(), user.getThumbAvatarUrl(), 0, false);

                                                mSearchPeopleList.add(search);

                                            }
                                        }

                                    }
                                }

                                mSearchPeopleAdapter.notifyDataSetChanged();

                                hideProgressBar();
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
    }


}
