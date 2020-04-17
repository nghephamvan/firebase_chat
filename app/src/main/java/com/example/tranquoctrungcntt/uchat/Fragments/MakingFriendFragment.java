package com.example.tranquoctrungcntt.uchat.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.FriendRequest;
import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.Activities.SentFriendRequest;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.FragmentAdapters.SuggestionAdapter;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_RECEIVER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_SENDER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.disableChangeAnimation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MakingFriendFragment extends Fragment {

    private RecyclerView rv_suggestions;
    private ArrayList<User> mSuggestionsList;
    private SuggestionAdapter mSuggestionsAdapter;

    private LinearLayout linear_sent_requests;
    private LinearLayout linear_received_requests;

    private TextView tv_badges_sent_requests;
    private TextView tv_badges_received_requests;

    private MainActivity mActivity;

    private String mLoadMoreKey;

    private NestedScrollView nestedScrollView;
    private LinearLayoutManager linearLayoutManager;

    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisibleItems;

    private boolean isLoadingMore;
    private boolean ableToLoadMore;

    private DatabaseReference mRelationshipRef;
    private ValueEventListener mRelationshipsValueEvent;

    private View rootView;

    public MakingFriendFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_people, container, false);

        mActivity = (MainActivity) getActivity();

        initViews();

        initClickEvents();

        if (mRelationshipsValueEvent != null && mRelationshipRef != null) mRelationshipRef.removeEventListener(mRelationshipsValueEvent);

        mRelationshipRef = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId());

        mRelationshipsValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (int index = 0; index < mSuggestionsList.size(); index++) {

                    if (dataSnapshot.hasChild(mSuggestionsList.get(index).getUserId())) {

                        mSuggestionsList.remove(index);
                        mSuggestionsAdapter.notifyItemRemoved(index);
                        mSuggestionsAdapter.notifyItemRangeChanged(index, mSuggestionsAdapter.getItemCount());

                        break;
                    }

                }

                int friendRequestCounter = 0;
                int sentRequestCounter = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.child(snapshot.getKey()).hasChild(CHILD_REQUEST_RECEIVER))
                        friendRequestCounter++;
                    if (dataSnapshot.child(snapshot.getKey()).hasChild(CHILD_REQUEST_SENDER))
                        sentRequestCounter++;
                }

                updateHeaderBadges(sentRequestCounter, tv_badges_sent_requests);

                updateHeaderBadges(friendRequestCounter, tv_badges_received_requests);

                mActivity.updateReceivedRequestBadges(friendRequestCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRelationshipRef.addValueEventListener(mRelationshipsValueEvent);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {

                        visibleItemCount = linearLayoutManager.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                        if (!isLoadingMore && ableToLoadMore) {

                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {

                                isLoadingMore = true;

                                mSuggestionsAdapter.addProgressbar();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        loadMoreUsers(new FirebaseCallBackList() {
                                            @Override
                                            public void OnCallBack(ArrayList<User> users) {

                                                mSuggestionsAdapter.removeProgressbar();

                                                if (users.isEmpty()) ableToLoadMore = false;
                                                else {

                                                    // lấy số slot còn trống để gợi ý
                                                    int suggestedNum = Math.min(users.size(), 50 - mSuggestionsList.size());

                                                    // nếu đã gợi ý đủ 50 người thì tắt loadmore gợi ý
                                                    if (suggestedNum == 50 - mSuggestionsList.size())
                                                        ableToLoadMore = false;

                                                    //chọn số người dùng vừa với slot còn lại
                                                    ArrayList<User> shouldSuggestedList = new ArrayList<>();

                                                    for (int index = 0; index < suggestedNum; index++)
                                                        shouldSuggestedList.add(users.get(index));

                                                    mSuggestionsAdapter.loadMoreItems(shouldSuggestedList);
                                                }

                                                isLoadingMore = false;

                                            }
                                        });

                                    }
                                }, 2000);

                            }
                        }
                    }
                }
            }
        });

        ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapRelationShip) {

                        ROOT_REF.child(CHILD_USERS).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    User user = snapshot.getValue(User.class);

                                    if (!user.getUserId().equals(getMyFirebaseUserId())) {

                                        if (!snapRelationShip.hasChild(user.getUserId())) {

                                            if (user.isVerified()) {

                                                mSuggestionsList.add(user);
                                                mSuggestionsAdapter.notifyItemInserted(mSuggestionsList.size() - 1);

                                                if (mSuggestionsList.size() == 10) break;
                                            }
                                        }
                                    }
                                }

                                if (!mSuggestionsList.isEmpty()) mLoadMoreKey = mSuggestionsList.get(mSuggestionsList.size() - 1).getUserId();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return rootView;
    }


    private void initViews() {

        mSuggestionsList = new ArrayList<>();

        mSuggestionsAdapter = new SuggestionAdapter(getActivity(), mSuggestionsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                viewUserProfile(getActivity(), mSuggestionsList.get(position).getUserId());

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        rv_suggestions = (RecyclerView) rootView.findViewById(R.id.rv_suggestions);
        rv_suggestions.setLayoutManager(linearLayoutManager);
        rv_suggestions.setAdapter(mSuggestionsAdapter);

        disableChangeAnimation(rv_suggestions);

        linear_sent_requests = (LinearLayout) rootView.findViewById(R.id.linear_sent_requests);
        linear_received_requests = (LinearLayout) rootView.findViewById(R.id.linear_received_requests);
        tv_badges_sent_requests = (TextView) rootView.findViewById(R.id.tv_badges_sent_requests);
        tv_badges_received_requests = (TextView) rootView.findViewById(R.id.tv_badges_received_requests);

        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nestedscrollview);

        mLoadMoreKey = "";

        isLoadingMore = false;

        ableToLoadMore = true;

    }

    private void initClickEvents() {

        linear_received_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), FriendRequest.class));
            }
        });

        linear_sent_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), SentFriendRequest.class));
            }
        });

    }

    private void updateHeaderBadges(int num, TextView textView) {

        if (num == 0) textView.setVisibility(View.INVISIBLE);

        else {

            textView.setVisibility(View.VISIBLE);

            if (num <= 99) textView.setText("" + num);

            else textView.setText("99+");


        }
    }

    private void loadMoreUsers(FirebaseCallBackList firebaseCallBackList) {

        ROOT_REF.child(CHILD_RELATIONSHIPS)
                .child(getMyFirebaseUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapRelationShip) {

                        ROOT_REF.child(CHILD_USERS).orderByKey().startAt(mLoadMoreKey)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapUsers) {

                                        ArrayList<User> newlist = new ArrayList<>();

                                        for (DataSnapshot snapshot : snapUsers.getChildren()) {

                                            User user = snapshot.getValue(User.class);

                                            if (!user.getUserId().equals(getMyFirebaseUserId()) && user.isVerified()) {

                                                if (!snapRelationShip.hasChild(user.getUserId())) {

                                                    if (!mLoadMoreKey.equals(user.getUserId())) {

                                                        newlist.add(user);

                                                        if (newlist.size() == 10) break;
                                                    }
                                                }
                                            }
                                        }

                                        if (!newlist.isEmpty()) mLoadMoreKey = newlist.get(newlist.size() - 1).getUserId();

                                        firebaseCallBackList.OnCallBack(newlist);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRelationshipsValueEvent != null && mRelationshipRef != null) mRelationshipRef.removeEventListener(mRelationshipsValueEvent);

    }


    private interface FirebaseCallBackList {
        void OnCallBack(ArrayList<User> users);
    }
}
