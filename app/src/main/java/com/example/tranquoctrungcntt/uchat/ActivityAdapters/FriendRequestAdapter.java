package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.acceptFriendRequest;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.denyFriendRequest;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private final Context mAdapterContext;
    private final List<User> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public FriendRequestAdapter(Context mAdapterContext, List<User> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_friend_request, viewGroup, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder mViewHolder, int mIndex) {


        final User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(user.getName());

        mViewHolder.tv_gender.setText(user.getGender());

        mViewHolder.btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                denyFriendRequest(mAdapterContext, user.getUserId());

            }
        });


        mViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                acceptFriendRequest(mAdapterContext, user.getUserId());

            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name;
        final TextView tv_gender;
        final Button btn_accept;
        final FrameLayout btn_deny;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_gender = (TextView) itemView.findViewById(R.id.tv_gender);
            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            btn_accept = (Button) itemView.findViewById(R.id.btn_accept);
            btn_deny = (FrameLayout) itemView.findViewById(R.id.frame_deny);

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

