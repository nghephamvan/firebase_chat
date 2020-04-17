package com.example.tranquoctrungcntt.uchat.PagerAdapters;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tranquoctrungcntt.uchat.EmojiFragments.AllEmojiFragment;
import com.example.tranquoctrungcntt.uchat.EmojiFragments.StickerFragment;

public class StickerEmojiPagerAdapter extends FragmentPagerAdapter {


    private final StickerFragment fragment_stickers = new StickerFragment();
    private final AllEmojiFragment fragment_all_emoji = new AllEmojiFragment();

    public StickerEmojiPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return fragment_stickers;
            case 1:
                return fragment_all_emoji;
        }

        return fragment_stickers;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
