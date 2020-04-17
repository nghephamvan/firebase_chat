package com.example.tranquoctrungcntt.uchat.FragmentAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {

    private final Context mAdapterContext;
    private final List<String> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;

    public EmojiAdapter(Context mAdapterContext, List<String> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new EmojiViewHolder(LayoutInflater.from(mAdapterContext).inflate(R.layout.row_emoji, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder emojiViewHolder, int mIndex) {

        emojiViewHolder.textView.setText(mList.get(mIndex));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class EmojiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final EmojiAppCompatTextView textView;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (EmojiAppCompatTextView) itemView.findViewById(R.id.tv_emoji);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.OnItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mItemClickListener.OnItemLongClick(v, getAdapterPosition());
            return true;
        }
    }
}
