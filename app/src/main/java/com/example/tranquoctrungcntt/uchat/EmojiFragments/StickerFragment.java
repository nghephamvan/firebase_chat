package com.example.tranquoctrungcntt.uchat.EmojiFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.Chat;
import com.example.tranquoctrungcntt.uchat.Activities.GroupChat;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.FragmentAdapters.StickerAdapter;
import com.example.tranquoctrungcntt.uchat.Models.StickerToShow;
import com.example.tranquoctrungcntt.uchat.OtherClasses.AppLocalDatabase;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StickerFragment extends Fragment {


    public StickerFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_stickers, container, false);

        final AppLocalDatabase appLocalDatabase = new AppLocalDatabase(getActivity());

        ArrayList<StickerToShow> stickers = appLocalDatabase.getAllSticker();

        StickerAdapter adapter = new StickerAdapter(getActivity(), stickers, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                if (getActivity() instanceof Chat) {

                    Chat mParentActivity = (Chat) getActivity();
                    mParentActivity.sendSticker(stickers.get(position).getStickerUrl());

                } else if (getActivity() instanceof GroupChat) {

                    GroupChat mParentActivity = (GroupChat) getActivity();
                    mParentActivity.sendSticker(stickers.get(position).getStickerUrl());

                }


            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });


        RecyclerView rv_places = (RecyclerView) view.findViewById(R.id.rv_stickers);
        rv_places.setHasFixedSize(true);
        rv_places.setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        rv_places.setItemAnimator(null);
        rv_places.setAdapter(adapter);

        return view;
    }


}
