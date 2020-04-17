package com.example.tranquoctrungcntt.uchat.PagerAdapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tranquoctrungcntt.uchat.SearchFraments.SearchFriendFragment;
import com.example.tranquoctrungcntt.uchat.SearchFraments.SearchGroupFragment;
import com.example.tranquoctrungcntt.uchat.SearchFraments.SearchPeopleFragment;

public class SearchPagerAdapter extends FragmentPagerAdapter {


    private final SearchPeopleFragment fragment_search_people;
    private final SearchFriendFragment fragment_search_friend;
    private final SearchGroupFragment fragment_search_group;

    public SearchPagerAdapter(FragmentManager fm,
                              SearchPeopleFragment fragment_search_people,
                              SearchFriendFragment fragment_search_friend,
                              SearchGroupFragment fragment_search_group) {

        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.fragment_search_people = fragment_search_people;
        this.fragment_search_friend = fragment_search_friend;
        this.fragment_search_group = fragment_search_group;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return fragment_search_people;
            case 1:
                return fragment_search_friend;
            case 2:
                return fragment_search_group;
        }

        return fragment_search_people;

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}