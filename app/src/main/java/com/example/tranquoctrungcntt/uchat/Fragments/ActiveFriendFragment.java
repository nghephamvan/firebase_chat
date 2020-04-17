package com.example.tranquoctrungcntt.uchat.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.FragmentAdapters.ActiveFriendAdapter;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFriendFragment extends Fragment {

    private RecyclerView rv_friends;
    private ArrayList<User> mFriendsList;
    private ActiveFriendAdapter mFriendsAdapter;

    private View rootView;

    public ActiveFriendFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_active_friends, container, false);

        mFriendsList = new ArrayList<>();

        mFriendsAdapter = new ActiveFriendAdapter(getActivity(), mFriendsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                goToChat(getActivity(), mFriendsList.get(position));
            }

            @Override
            public void OnItemLongClick(View v, int position) {
                goToChat(getActivity(), mFriendsList.get(position));
            }
        });

        rv_friends = (RecyclerView) rootView.findViewById(R.id.rv_online_friends);
        rv_friends.setHasFixedSize(true);
        rv_friends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_friends.setAdapter(mFriendsAdapter);
        rv_friends.setItemAnimator(null);

        return rootView;
    }

    public void addActiveFriend(User friendProfile) {

        mFriendsList.add(friendProfile);
        mFriendsAdapter.notifyItemInserted(mFriendsList.size() - 1);

    }

    public int countActiveFriend() {
        return mFriendsList.size();
    }

    public void removeActiveFriend(String friendId) {

        final int i = searchUser(mFriendsList, friendId);

        if (hasItemInList(i)) {
            mFriendsList.remove(i);
            mFriendsAdapter.notifyItemRemoved(i);
            mFriendsAdapter.notifyItemRangeChanged(i, mFriendsAdapter.getItemCount());
        }

    }

    public void updateFriendProfile(User updatedFriendProfile) {

        final int i = searchUser(mFriendsList, updatedFriendProfile.getUserId());

        if (hasItemInList(i)) {
            mFriendsList.set(i, updatedFriendProfile);
            mFriendsAdapter.notifyItemChanged(i);
        }
    }

}
