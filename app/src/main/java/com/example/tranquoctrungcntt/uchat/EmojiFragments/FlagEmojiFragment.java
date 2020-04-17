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

import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getEmojiFacesAndPeople;


/**
 * A simple {@link Fragment} subclass.
 */
public class FlagEmojiFragment extends Fragment {


    public FlagEmojiFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_flags_emoji, container, false);


        ArrayList<String> flags = new ArrayList<>();

        getEmojiFacesAndPeople(flags);

        EmojiAdapter adapter = new EmojiAdapter(getActivity(), flags, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                if (getActivity() instanceof UpdateStatus) {

                    UpdateStatus mParentActivity = (UpdateStatus) getActivity();
                    mParentActivity.appendEmoji(flags.get(position));

                } else if (getActivity() instanceof Chat) {

                    Chat mParentActivity = (Chat) getActivity();
                    mParentActivity.appendEmoji(flags.get(position));

                } else if (getActivity() instanceof GroupChat) {

                    GroupChat mParentActivity = (GroupChat) getActivity();
                    mParentActivity.appendEmoji(flags.get(position));

                }
            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });

        RecyclerView rv_flags = (RecyclerView) view.findViewById(R.id.rv_flags_emoji);
        rv_flags.setHasFixedSize(true);
        rv_flags.setLayoutManager(new GridLayoutManager(getActivity(), 7, GridLayoutManager.VERTICAL, false));
        rv_flags.setItemAnimator(null);
        rv_flags.setAdapter(adapter);

        return view;
    }

}
