package com.example.tranquoctrungcntt.uchat.DialogAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Objects.EditHistory;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeForDetail;


public class EditHistoryAdapter extends RecyclerView.Adapter<EditHistoryAdapter.EditHistoryViewHolder> {


    private final Context mAdapterContext;
    private final List<EditHistory> mList;

    public EditHistoryAdapter(Context mAdapterContext, List<EditHistory> mList) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
    }


    @NonNull
    @Override
    public EditHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_edit_history, viewGroup, false);

        return new EditHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditHistoryViewHolder mViewHolder, int mIndex) {

        final EditHistory editHistory = mList.get(mIndex);

        mViewHolder.tv_time.setText(formatTimeForDetail(mList.get(mIndex).getEditTime()));
        mViewHolder.tv_content.setText(editHistory.getPreviousContent());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class EditHistoryViewHolder extends RecyclerView.ViewHolder {

        final EmojiAppCompatTextView tv_content;
        final TextView tv_time;

        public EditHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_content = (EmojiAppCompatTextView) itemView.findViewById(R.id.tv_edited_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_edit_time);

        }


    }
}
