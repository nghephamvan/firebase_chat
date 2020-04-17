package com.example.tranquoctrungcntt.uchat.PagerAdapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tranquoctrungcntt.uchat.Fragments.AccountFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.ContactFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.ConversationFragment;
import com.example.tranquoctrungcntt.uchat.Fragments.MakingFriendFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final ContactFragment frag_friends;
    private final MakingFriendFragment frag_make_friend;
    private final ConversationFragment frag_conversations;
    private final AccountFragment frag_me;

    public MainPagerAdapter(FragmentManager fm,
                            ConversationFragment fconv,
                            ContactFragment ffriend,
                            MakingFriendFragment fmakefriend,
                            AccountFragment fme) {

        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.frag_make_friend = fmakefriend;
        this.frag_conversations = fconv;
        this.frag_friends = ffriend;
        this.frag_me = fme;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return frag_conversations;
            case 1:
                return frag_friends;
            case 2:
                return frag_make_friend;
            case 3:
                return frag_me;
        }

        return frag_conversations;
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}
