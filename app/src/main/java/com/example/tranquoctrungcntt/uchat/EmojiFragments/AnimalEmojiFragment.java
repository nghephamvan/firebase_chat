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

import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getEmojiAnimalsAndPlants;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnimalEmojiFragment extends Fragment {


    public AnimalEmojiFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_animals_emoji, container, false);

        ArrayList<String> animals = new ArrayList<>();

        getEmojiAnimalsAndPlants(animals);

        EmojiAdapter adapter = new EmojiAdapter(getActivity(), animals, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                if (getActivity() instanceof UpdateStatus) {

                    UpdateStatus mParentActivity = (UpdateStatus) getActivity();
                    mParentActivity.appendEmoji(animals.get(position));

                } else if (getActivity() instanceof Chat) {

                    Chat mParentActivity = (Chat) getActivity();
                    mParentActivity.appendEmoji(animals.get(position));

                } else if (getActivity() instanceof GroupChat) {

                    GroupChat mParentActivity = (GroupChat) getActivity();
                    mParentActivity.appendEmoji(animals.get(position));

                }

            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        RecyclerView rv_animals = (RecyclerView) view.findViewById(R.id.rv_animals_emoji);
        rv_animals.setHasFixedSize(true);
        rv_animals.setLayoutManager(new GridLayoutManager(getActivity(), 7, GridLayoutManager.VERTICAL, false));
        rv_animals.setItemAnimator(null);
        rv_animals.setAdapter(adapter);

        return view;
    }

}
