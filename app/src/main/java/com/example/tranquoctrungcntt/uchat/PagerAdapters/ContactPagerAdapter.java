package com.example.tranquoctrungcntt.uchat.PagerAdapters;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tranquoctrungcntt.uchat.Fragments.ActiveFriendFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.AllFriendFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.GroupFragment;

public class ContactPagerAdapter extends FragmentPagerAdapter {


    private final AllFriendFragment fragment_allFriends;
    private final ActiveFriendFragment fragment_activeFriends;
    private final GroupFragment fragment_group;


    public ContactPagerAdapter(FragmentManager fm,
                               AllFriendFragment fragment_allFriends,
                               ActiveFriendFragment fragment_activeFriends,
                               GroupFragment fragment_group) {

        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.fragment_allFriends = fragment_allFriends;
        this.fragment_activeFriends = fragment_activeFriends;
        this.fragment_group = fragment_group;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return fragment_allFriends;
            case 1:

                return fragment_activeFriends;
            case 2:
                return fragment_group;
        }

        return fragment_allFriends;

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }
}