package com.example.tranquoctrungcntt.uchat.EmojiFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.tranquoctrungcntt.uchat.Activities.Chat;
import com.example.tranquoctrungcntt.uchat.Activities.GroupChat;
import com.example.tranquoctrungcntt.uchat.Activities.UpdateStatus;
import com.example.tranquoctrungcntt.uchat.PagerAdapters.EmojiPagerAdapter;
import com.example.tranquoctrungcntt.uchat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllEmojiFragment extends Fragment implements View.OnClickListener {

    private ViewPager vp_emoji;

    private FrameLayout tab_faces;
    private FrameLayout tab_animals;
    private FrameLayout tab_foods;
    private FrameLayout tab_objects;
    private FrameLayout tab_places;
    private FrameLayout tab_symbols;
    private FrameLayout tab_flags;
    private FrameLayout tab_delete;

    private FrameLayout tab_faces_indicator;
    private FrameLayout tab_animals_indicator;
    private FrameLayout tab_foods_indicator;
    private FrameLayout tab_objects_indicator;
    private FrameLayout tab_places_indicator;
    private FrameLayout tab_symbols_indicator;
    private FrameLayout tab_flags_indicator;


    public AllEmojiFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_emoji, container, false);

        final EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter(getChildFragmentManager());

        vp_emoji = (ViewPager) view.findViewById(R.id.vp_emoji);
        vp_emoji.setOffscreenPageLimit(7);
        vp_emoji.setAdapter(emojiPagerAdapter);

        tab_faces = (FrameLayout) view.findViewById(R.id.emoji_tab_faces);
        tab_animals = (FrameLayout) view.findViewById(R.id.emoji_tab_animals);
        tab_foods = (FrameLayout) view.findViewById(R.id.emoji_tab_foods);
        tab_objects = (FrameLayout) view.findViewById(R.id.emoji_tab_objects);
        tab_places = (FrameLayout) view.findViewById(R.id.emoji_tab_places);
        tab_symbols = (FrameLayout) view.findViewById(R.id.emoji_tab_symbols);
        tab_flags = (FrameLayout) view.findViewById(R.id.emoji_tab_flags);
        tab_delete = (FrameLayout) view.findViewById(R.id.emoji_tab_delete);

        tab_faces_indicator = (FrameLayout) view.findViewById(R.id.tab_faces_indicator);
        tab_animals_indicator = (FrameLayout) view.findViewById(R.id.tab_animals_indicator);
        tab_foods_indicator = (FrameLayout) view.findViewById(R.id.tab_foods_indicator);
        tab_objects_indicator = (FrameLayout) view.findViewById(R.id.tab_objects_indicator);
        tab_places_indicator = (FrameLayout) view.findViewById(R.id.tab_places_indicator);
        tab_symbols_indicator = (FrameLayout) view.findViewById(R.id.tab_symbols_indicator);
        tab_flags_indicator = (FrameLayout) view.findViewById(R.id.tab_flags_indicator);

        tab_faces.setOnClickListener(this);
        tab_animals.setOnClickListener(this);
        tab_foods.setOnClickListener(this);
        tab_objects.setOnClickListener(this);
        tab_places.setOnClickListener(this);
        tab_symbols.setOnClickListener(this);
        tab_flags.setOnClickListener(this);
        tab_delete.setOnClickListener(this);

        vp_emoji.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:

                        tab_faces_indicator.setVisibility(View.VISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 1:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.VISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 2:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.VISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 3:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.VISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 4:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.VISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 5:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.VISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 6:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.VISIBLE);

                        break;

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vp_emoji.setCurrentItem(0, false);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emoji_tab_faces:
                vp_emoji.setCurrentItem(0, false);
                break;

            case R.id.emoji_tab_animals:
                vp_emoji.setCurrentItem(1, false);
                break;

            case R.id.emoji_tab_foods:
                vp_emoji.setCurrentItem(2, false);

                break;
            case R.id.emoji_tab_objects:
                vp_emoji.setCurrentItem(3, false);
                break;

            case R.id.emoji_tab_places:
                vp_emoji.setCurrentItem(4, false);
                break;

            case R.id.emoji_tab_symbols:
                vp_emoji.setCurrentItem(5, false);
                break;

            case R.id.emoji_tab_flags:
                vp_emoji.setCurrentItem(6, false);
                break;

            case R.id.emoji_tab_delete:

                if (getActivity() instanceof Chat) {
                    Chat mActivity = (Chat) getActivity();
                    mActivity.deleteOneEmoji();
                } else if (getActivity() instanceof GroupChat) {
                    GroupChat mActivity = (GroupChat) getActivity();
                    mActivity.deleteOneEmoji();
                } else if (getActivity() instanceof UpdateStatus) {
                    UpdateStatus mActivity = (UpdateStatus) getActivity();
                    mActivity.deleteOneEmoji();
                }

                break;
        }
    }


}
