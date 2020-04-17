package com.example.tranquoctrungcntt.uchat.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.FragmentAdapters.AllFriendAdapter;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isUserOnline;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkBeforeBLockUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.showUnfriendConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.makeCall;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;


public class AllFriendFragment extends Fragment {

    private final Handler mRefreshHandler = new Handler();

    private RecyclerView rv_friends;
    private ArrayList<User> mAllFriendsList;
    private AllFriendAdapter mAllFriendsAdapter;

    private LinearLayout linear_no_friend;

    private AlertDialog mOptionsDialog;

    private String mUsingFriendId;

    private ContactFragment fragment_parent;

    private Map<String, ValueEventListener> mFriendProfileListenerMap;

    private ChildEventListener mFriendChildEvent;
    private Query mFriendQuery;

    private View rootView;

    public AllFriendFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_all_friends, container, false);

        fragment_parent = (ContactFragment) getParentFragment();

        initViews();

        refreshList(10 * 60 * 1000);

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);

        mFriendQuery = ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).orderByChild(CHILD_FRIEND).equalTo(true);

        mFriendChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (linear_no_friend.getVisibility() == View.VISIBLE && rv_friends.getVisibility() == View.GONE) {
                    linear_no_friend.setVisibility(View.GONE);
                    rv_friends.setVisibility(View.VISIBLE);
                }

                addFriendToList(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                final String removedUserId = dataSnapshot.getKey();

                closeOptionsDialogWithThisId(removedUserId);

                removeProfileListener(removedUserId);

                final int i = searchUser(mAllFriendsList, removedUserId);

                if (hasItemInList(i)) {

                    fragment_parent.removeActiveFriend(removedUserId);

                    mAllFriendsList.remove(i);
                    mAllFriendsAdapter.notifyItemRemoved(i);
                    mAllFriendsAdapter.notifyItemRangeChanged(i, mAllFriendsAdapter.getItemCount());

                    fragment_parent.updateAllFriendsTitle(mAllFriendsList.size());

                }

                if (mAllFriendsList.isEmpty()) {
                    linear_no_friend.setVisibility(View.VISIBLE);
                    rv_friends.setVisibility(View.GONE);
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

        mFriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotFriendId) {

                if (dataSnapshotFriendId.exists()) {
                    linear_no_friend.setVisibility(View.GONE);
                    rv_friends.setVisibility(View.VISIBLE);
                } else {
                    linear_no_friend.setVisibility(View.VISIBLE);
                    rv_friends.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void refreshList(long every) {
        mRefreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mAllFriendsAdapter.notifyItemRangeChanged(0, mAllFriendsAdapter.getItemCount());

                mRefreshHandler.postDelayed(this, every);

            }
        }, every);
    }

    private void initViews() {

        mFriendProfileListenerMap = new HashMap<>();

        mAllFriendsList = new ArrayList<>();
        mAllFriendsAdapter = new AllFriendAdapter(getActivity(), mAllFriendsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                goToChat(getActivity(), mAllFriendsList.get(position));
            }

            @Override
            public void OnItemLongClick(View v, int position) {
                showOptions(mAllFriendsList.get(position));
            }
        });

        rv_friends = (RecyclerView) rootView.findViewById(R.id.rv_friends);
        rv_friends.setHasFixedSize(true);
        rv_friends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_friends.setAdapter(mAllFriendsAdapter);
        rv_friends.setItemAnimator(null);

        linear_no_friend = (LinearLayout) rootView.findViewById(R.id.linear_no_friend);

        mUsingFriendId = null;


    }

    private void addFriendToList(String friendId) {

        getSingleUserProfile(friendId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                if (mFriendProfileListenerMap.get(friendId) == null) {

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            final User updatedUser = dataSnapshot.getValue(User.class);//todo: wrong

                            if (updatedUser != null) updateFriendProfile(updatedUser);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    ROOT_REF.child(CHILD_USERS).child(friendId).addValueEventListener(valueEventListener);

                    mFriendProfileListenerMap.put(friendId, valueEventListener);

                }

                mAllFriendsList.add(callbackUserProfile);
                mAllFriendsAdapter.notifyItemInserted(mAllFriendsList.size() - 1);

                if (isUserOnline(callbackUserProfile.getLastSeen())) fragment_parent.addActiveFriendIfNeeded(callbackUserProfile);

                fragment_parent.updateAllFriendsTitle(mAllFriendsList.size());

                sortList();
            }
        });

    }

    private void sortList() {

        Collections.sort(mAllFriendsList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Character.compare(o1.getName().charAt(0), o2.getName().charAt(0));
            }
        });

        mAllFriendsAdapter.notifyItemRangeChanged(0, mAllFriendsAdapter.getItemCount());

    }

    private void updateFriendProfile(User updatedFriendProfile) {

        for (int index = 0; index < mAllFriendsList.size(); index++) {

            final User user = mAllFriendsList.get(index);

            if (user.getUserId().equals(updatedFriendProfile.getUserId())) {

                if (isUserOnline(user.getLastSeen()) && !isUserOnline(updatedFriendProfile.getLastSeen())) {
                    fragment_parent.removeActiveFriend(updatedFriendProfile.getUserId());
                } else if (!isUserOnline(user.getLastSeen()) && isUserOnline(updatedFriendProfile.getLastSeen())) {
                    fragment_parent.addActiveFriendIfNeeded(updatedFriendProfile);
                } else if (isUserOnline(user.getLastSeen()) && isUserOnline(updatedFriendProfile.getLastSeen())) {
                    fragment_parent.updateActiveFriendProfile(updatedFriendProfile);
                }

                mAllFriendsList.set(index, updatedFriendProfile);
                mAllFriendsAdapter.notifyItemChanged(index);

                if (!updatedFriendProfile.getName().equalsIgnoreCase(user.getName())) {
                    sortList();
                }

                break;

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRefreshHandler != null) mRefreshHandler.removeCallbacksAndMessages(null);

        if (mFriendChildEvent != null && mFriendQuery != null) mFriendQuery.removeEventListener(mFriendChildEvent);

        for (User friend : mAllFriendsList) removeProfileListener(friend.getUserId());


    }

    private void removeProfileListener(String userId) {
        if (mFriendProfileListenerMap.get(userId) != null) {

            ROOT_REF.child(CHILD_USERS).child(userId).removeEventListener(mFriendProfileListenerMap.get(userId));

            mFriendProfileListenerMap.put(userId, null);
        }
    }

    private void showOptions(User friendProfile) {

        mUsingFriendId = friendProfile.getUserId();

        final ArrayList<String> options = new ArrayList<>();

        final String option0 = "Xem trang cá nhân";
        final String option1 = "Nhắn tin";
        final String option2 = "Gọi thoại";
        final String option3 = "Gọi video";
        final String option4 = "Hủy kết bạn";
        final String option5 = "Chặn người này";
        final String option6 = "Hủy";

        options.add(option0);
        options.add(option1);
        options.add(option2);
        options.add(option3);
        options.add(option4);
        options.add(option5);
        options.add(option6);

        ArrayAdapter<String> optionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, options);

        mOptionsDialog = new AlertDialog.Builder(getActivity())
                .setTitle(friendProfile.getName())
                .setAdapter(optionAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                viewUserProfile(getActivity(), friendProfile.getUserId());
                                break;

                            case 1:
                                goToChat(getActivity(), friendProfile);
                                break;

                            case 2:
                                makeCall(getActivity(), friendProfile.getUserId(), false);
                                break;

                            case 3:
                                makeCall(getActivity(), friendProfile.getUserId(), true);
                                break;

                            case 4:
                                showUnfriendConfirmDialog(getActivity(), friendProfile.getUserId());
                                break;

                            case 5:
                                checkBeforeBLockUser(getActivity(), friendProfile.getUserId());
                                break;

                            default:
                        }

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mUsingFriendId = null;
                    }
                }).create();

        mOptionsDialog.show();

    }

    private void closeOptionsDialogWithThisId(String userId) {
        if (mUsingFriendId != null && mUsingFriendId.equals(userId))
            if (mOptionsDialog != null && mOptionsDialog.isShowing()) mOptionsDialog.dismiss();
    }


}
