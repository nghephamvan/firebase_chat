package com.example.tranquoctrungcntt.uchat.DialogAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Models.MessageViewerModel;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeForDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class MessageViewerAdapter extends RecyclerView.Adapter<MessageViewerAdapter.MessageViewerViewHolder> {


    private final Context mAdapterContext;
    private final List<MessageViewerModel> mList;
    private final boolean isDetailView;

    public MessageViewerAdapter(Context mAdapterContext, List<MessageViewerModel> mList, boolean isDetailView) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.isDetailView = isDetailView;

    }


    @NonNull
    @Override
    public MessageViewerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {

        View view;

        if (isDetailView)

            view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_message_viewer_detail, viewGroup, false);

        else
            view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_message_viewer, viewGroup, false);

        return new MessageViewerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewerViewHolder mViewholder, int mIndex) {

        final User user = mList.get(mIndex).getUser();

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewholder.viewer_avatar);

        if (isDetailView) {

            mViewholder.tv_time.setText(formatTimeForDetail(mList.get(mIndex).getSeenTime()));

            mViewholder.tv_name.setText(user.getName() + (user.getUserId().equals(getMyFirebaseUserId()) ? " (bạn)" : ""));

        }

    }

    @Override
    public int getItemCount() {
        return isDetailView ? mList.size() : Math.min(5, mList.size());
    }

    public class MessageViewerViewHolder extends RecyclerView.ViewHolder {

        final CircleImageView viewer_avatar;

        TextView tv_name;
        TextView tv_time;

        public MessageViewerViewHolder(@NonNull View itemView) {
            super(itemView);

            viewer_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);

            if (isDetailView) {
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            }

        }


    }
}
