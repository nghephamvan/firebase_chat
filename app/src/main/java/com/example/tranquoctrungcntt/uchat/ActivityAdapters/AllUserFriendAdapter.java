package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class AllUserFriendAdapter extends RecyclerView.Adapter<AllUserFriendAdapter.AllUserFriendViewHolder> {

    private final Context mAdapterContext;
    private final List<MutualFriend> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public AllUserFriendAdapter(Context mAdapterContext, List<MutualFriend> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public AllUserFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_all_user_friend, parent, false);
        return new AllUserFriendViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AllUserFriendViewHolder mViewHolder, int mIndex) {

        User user = mList.get(mIndex).getFriendProfile();

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(user.getName());

        if (mList.get(mIndex).isMutualFriend())
            mViewHolder.tv_mutual.setVisibility(View.VISIBLE);
        else mViewHolder.tv_mutual.setVisibility(View.GONE);

        mViewHolder.tv_mutual.setText(mList.get(mIndex).isMutualFriend() ? "Bạn chung" : null);

        mViewHolder.btn_infor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!user.getUserId().equals(getMyFirebaseUserId()))
                    viewUserProfile(mAdapterContext, user.getUserId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class AllUserFriendViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name, tv_mutual;
        final FrameLayout btn_infor;

        public AllUserFriendViewHolder(View itemView) {
            super(itemView);

            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_mutual = (TextView) itemView.findViewById(R.id.tv_mutual);
            btn_infor = (FrameLayout) itemView.findViewById(R.id.frame_more_infor);

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
