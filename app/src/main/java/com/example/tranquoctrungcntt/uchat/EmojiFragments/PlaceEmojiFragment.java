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
import com.example.tranquoctrungcntt.uchat.Activities.UpdateStatus;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.FragmentAdapters.EmojiAdapter;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getEmojiPlacesAndVehicles;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceEmojiFragment extends Fragment {


    public PlaceEmojiFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_places_emoji, container, false);

        ArrayList<String> places = new ArrayList<>();

        getEmojiPlacesAndVehicles(places);

        EmojiAdapter adapter = new EmojiAdapter(getActivity(), places, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                if (getActivity() instanceof UpdateStatus) {

                    UpdateStatus mParentActivity = (UpdateStatus) getActivity();
                    mParentActivity.appendEmoji(places.get(position));

                } else if (getActivity() instanceof Chat) {
                    Chat mParentActivity = (Chat) getActivity();
                    mParentActivity.appendEmoji(places.get(position));
                } else if (getActivity() instanceof GroupChat) {
                    GroupChat mParentActivity = (GroupChat) getActivity();
                    mParentActivity.appendEmoji(places.get(position));
                }
            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });


        RecyclerView rv_places = (RecyclerView) view.findViewById(R.id.rv_places_emoji);
        rv_places.setHasFixedSize(true);
        rv_places.setLayoutManager(new GridLayoutManager(getActivity(), 7, GridLayoutManager.VERTICAL, false));
        rv_places.setItemAnimator(null);
        rv_places.setAdapter(adapter);

        return view;
    }

}
