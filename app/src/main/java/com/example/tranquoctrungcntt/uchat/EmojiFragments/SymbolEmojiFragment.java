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
public class SymbolEmojiFragment extends Fragment {


    public SymbolEmojiFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_symbols_emoji, container, false);

        ArrayList<String> symbols = new ArrayList<>();

        getEmojiFacesAndPeople(symbols);

        EmojiAdapter adapter = new EmojiAdapter(getActivity(), symbols, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                if (getActivity() instanceof UpdateStatus) {

                    UpdateStatus mParentActivity = (UpdateStatus) getActivity();
                    mParentActivity.appendEmoji(symbols.get(position));

                } else if (getActivity() instanceof Chat) {
                    Chat mParentActivity = (Chat) getActivity();
                    mParentActivity.appendEmoji(symbols.get(position));
                } else if (getActivity() instanceof GroupChat) {
                    GroupChat mParentActivity = (GroupChat) getActivity();
                    mParentActivity.appendEmoji(symbols.get(position));
                }
            }

            @Override
            public void OnItemLongClick(View v, int position) {

            }
        });


        RecyclerView rv_symbols = (RecyclerView) view.findViewById(R.id.rv_symbols_emoji);
        rv_symbols.setHasFixedSize(true);
        rv_symbols.setLayoutManager(new GridLayoutManager(getActivity(), 7, GridLayoutManager.VERTICAL, false));
        rv_symbols.setItemAnimator(null);
        rv_symbols.setAdapter(adapter);

        return view;
    }

}
