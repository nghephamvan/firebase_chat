package com.example.tranquoctrungcntt.uchat.PagerAdapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tranquoctrungcntt.uchat.EmojiFragments.AnimalEmojiFragment;
import com.example.tranquoctrungcntt.uchat.EmojiFragments.FaceEmojiFragment;
import com.example.tranquoctrungcntt.uchat.EmojiFragments.FlagEmojiFragment;
import com.example.tranquoctrungcntt.uchat.EmojiFragments.FoodEmojiFragment;
import com.example.tranquoctrungcntt.uchat.EmojiFragments.ObjectEmojiFragment;
import com.example.tranquoctrungcntt.uchat.EmojiFragments.PlaceEmojiFragment;
import com.example.tranquoctrungcntt.uchat.EmojiFragments.SymbolEmojiFragment;

public class EmojiPagerAdapter extends FragmentPagerAdapter {


    private final FaceEmojiFragment fragment_face_emoji = new FaceEmojiFragment();
    private final AnimalEmojiFragment fragment_animal_emoji = new AnimalEmojiFragment();
    private final FoodEmojiFragment fragment_food_emoji = new FoodEmojiFragment();
    private final ObjectEmojiFragment fragment_object_emoji = new ObjectEmojiFragment();
    private final PlaceEmojiFragment fragment_place_emoji = new PlaceEmojiFragment();
    private final SymbolEmojiFragment fragment_symbol_emoji = new SymbolEmojiFragment();
    private final FlagEmojiFragment fragment_flag_emoji = new FlagEmojiFragment();


    public EmojiPagerAdapter(FragmentManager fm) {

        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return fragment_face_emoji;
            case 1:
                return fragment_animal_emoji;
            case 2:
                return fragment_food_emoji;
            case 3:
                return fragment_object_emoji;
            case 4:
                return fragment_place_emoji;
            case 5:
                return fragment_symbol_emoji;
            case 6:
                return fragment_flag_emoji;
        }

        return fragment_face_emoji;

    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}