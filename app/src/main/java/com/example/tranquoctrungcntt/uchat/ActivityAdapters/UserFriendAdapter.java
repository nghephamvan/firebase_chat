package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.MutualFriend;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class UserFriendAdapter extends RecyclerView.Adapter<UserFriendAdapter.PickedMemberViewHolder> {

    private final Context mAdapterContext;
    private final List<MutualFriend> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;

    private boolean isDelaying;

    public UserFriendAdapter(Context mAdapterContext, List<MutualFriend> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public PickedMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_user_friend, parent, false);
        return new PickedMemberViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PickedMemberViewHolder mViewHolder, int mIndex) {

        final User user = mList.get(mIndex).getFriendProfile();

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(user.getName());

    }

    @Override
    public int getItemCount() {
        return Math.min(mList.size(), 6);
    }

    public class PickedMemberViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name;

        public PickedMemberViewHolder(View itemView) {
            super(itemView);


            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (!isDelaying) {

                isDelaying = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isDelaying = false;
                    }
                }, CLICK_DELAY);

                mItemClickListener.OnItemClick(v, getAdapterPosition());

            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (!isDelaying) {

                isDelaying = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isDelaying = false;
                    }
                }, CLICK_DELAY);

                mItemClickListener.OnItemLongClick(v, getAdapterPosition());

            }


            return true;
        }
    }


}
