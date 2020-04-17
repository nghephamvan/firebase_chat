package com.example.tranquoctrungcntt.uchat.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.PagerAdapters.ContactPagerAdapter;
import com.example.tranquoctrungcntt.uchat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private static final String[] mTabTitle = {"BẠN BÈ", "ĐANG HOẠT ĐỘNG", "NHÓM"};

    private TextView tv_tab_friends;
    private TextView tv_tab_active_friends;
    private TextView tv_tab_groups;

    private AllFriendFragment fragment_allFriends = new AllFriendFragment();
    private ActiveFriendFragment fragment_activeFriends = new ActiveFriendFragment();
    private GroupFragment fragment_groups = new GroupFragment();

    private ContactPagerAdapter mContactPagerAdapter;
    private ViewPager vp_contacts;

    private View rootView;

    public ContactFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        initViews();

        initClickEvents();

        return rootView;
    }

    private void initClickEvents() {
        tv_tab_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_contacts.setCurrentItem(0);
            }
        });

        tv_tab_active_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_contacts.setCurrentItem(1);
            }
        });

        tv_tab_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_contacts.setCurrentItem(2);
            }
        });
    }

    private void initViews() {


        tv_tab_friends = (TextView) rootView.findViewById(R.id.tv_tab_friends);
        tv_tab_active_friends = (TextView) rootView.findViewById(R.id.tv_tab_active_friends);
        tv_tab_groups = (TextView) rootView.findViewById(R.id.tv_tab_groups);

        mContactPagerAdapter = new ContactPagerAdapter(getChildFragmentManager(),
                fragment_allFriends,
                fragment_activeFriends,
                fragment_groups);

        vp_contacts = (ViewPager) rootView.findViewById(R.id.vp_contacts);
        vp_contacts.setAdapter(mContactPagerAdapter);
        vp_contacts.setOffscreenPageLimit(3);
        vp_contacts.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pos) {
                switch (pos) {
                    case 0:
                        selectTabFriends();
                        break;
                    case 1:

                        selectTabActiveFriends();
                        break;
                    case 2:
                        selectTabGroups();

                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        selectTabFriends();
    }

    private void selectTabGroups() {

        tv_tab_groups.setBackgroundResource(R.drawable.filled_light_grey_bg);
        tv_tab_groups.setTextColor(getResources().getColor(R.color.black));

        tv_tab_friends.setBackgroundResource(0);
        tv_tab_friends.setTextColor(getResources().getColor(R.color.grey));
        tv_tab_active_friends.setBackgroundResource(0);
        tv_tab_active_friends.setTextColor(getResources().getColor(R.color.grey));
    }

    private void selectTabActiveFriends() {

        tv_tab_active_friends.setBackgroundResource(R.drawable.filled_light_grey_bg);
        tv_tab_active_friends.setTextColor(getResources().getColor(R.color.black));

        tv_tab_friends.setBackgroundResource(0);
        tv_tab_friends.setTextColor(getResources().getColor(R.color.grey));
        tv_tab_groups.setBackgroundResource(0);
        tv_tab_groups.setTextColor(getResources().getColor(R.color.grey));
    }

    private void selectTabFriends() {

        tv_tab_friends.setBackgroundResource(R.drawable.filled_light_grey_bg);
        tv_tab_friends.setTextColor(getResources().getColor(R.color.black));
        tv_tab_active_friends.setBackgroundResource(0);
        tv_tab_active_friends.setTextColor(getResources().getColor(R.color.grey));
        tv_tab_groups.setBackgroundResource(0);
        tv_tab_groups.setTextColor(getResources().getColor(R.color.grey));

    }

    public void addActiveFriendIfNeeded(User friendProfile) {
        fragment_activeFriends.addActiveFriend(friendProfile);
        updateActiveFriendsTitle(fragment_activeFriends.countActiveFriend());
    }

    public void removeActiveFriend(String friendId) {
        fragment_activeFriends.removeActiveFriend(friendId);
        updateActiveFriendsTitle(fragment_activeFriends.countActiveFriend());
    }

    public void updateActiveFriendProfile(User updatedFriendProfile) {
        fragment_activeFriends.updateFriendProfile(updatedFriendProfile);
    }

    public void updateAllFriendsTitle(int num) {
        if (num == 0) {
            tv_tab_friends.setText(mTabTitle[0]);
        } else if (num <= 99)
            tv_tab_friends.setText(mTabTitle[0] + " (" + num + ")");
        else tv_tab_friends.setText(mTabTitle[0] + " (99+)");
    }

    public void updateActiveFriendsTitle(int num) {
        if (num == 0) tv_tab_active_friends.setText(mTabTitle[1]);
        else if (num <= 99)
            tv_tab_active_friends.setText(mTabTitle[1] + " (" + num + ")");
        else tv_tab_active_friends.setText(mTabTitle[1] + " (99+)");

        ((MainActivity) getActivity()).updateContactBadges(num);
    }

    public void updateGroupsTitle(int num) {
        if (num == 0) {
            tv_tab_groups.setText(mTabTitle[2]);
        } else if (num <= 99)
            tv_tab_groups.setText(mTabTitle[2] + " (" + num + ")");
        else tv_tab_groups.setText(mTabTitle[2] + " (99+)");
    }

}
