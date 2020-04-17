package com.example.tranquoctrungcntt.uchat.FragmentAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isUserOnline;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeAgoShortly;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;

public class AllFriendAdapter extends RecyclerView.Adapter<AllFriendAdapter.FriendViewHolder> {

    private final Context mAdapterContext;
    private final List<User> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;


    public AllFriendAdapter(Context mAdapterContext, List<User> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_all_friend, viewGroup, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder mViewHolder, int mIndex) {

        final User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(mList.get(mIndex).getName());

        mViewHolder.tv_status.setText(mList.get(mIndex).getStatus());

        mViewHolder.tv_status.setVisibility(mList.get(mIndex).getStatus() != null ? View.VISIBLE : View.GONE);

        if (mIndex > 0) {

            if (mList.get(mIndex).getName().charAt(0) != mList.get(mIndex - 1).getName().charAt(0)) {

                mViewHolder.tv_alphabet.setVisibility(View.VISIBLE);
                mViewHolder.tv_alphabet.setText(mList.get(mIndex).getName().charAt(0) + "");

            } else {

                mViewHolder.tv_alphabet.setVisibility(View.GONE);
                mViewHolder.tv_alphabet.setText(null);
            }

        } else {

            mViewHolder.tv_alphabet.setVisibility(View.VISIBLE);
            mViewHolder.tv_alphabet.setText(mList.get(mIndex).getName().charAt(0) + "");

        }

        if (isUserOnline(user.getLastSeen())) {

            mViewHolder.active_dot.setVisibility(View.VISIBLE);
            mViewHolder.tv_time_offline.setVisibility(View.GONE);

        } else {

            mViewHolder.active_dot.setVisibility(View.GONE);

            final String stringValue = formatTimeAgoShortly(user.getLastSeen());

            mViewHolder.tv_time_offline.setText(stringValue);
            mViewHolder.tv_time_offline.setVisibility(stringValue != null ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name, tv_alphabet, tv_time_offline;
        final EmojiAppCompatTextView tv_status;
        final CircleImageView active_dot;


        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);


            user_avatar = (CircleImageView) itemView.findViewById(R.id.friend_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_status = (EmojiAppCompatTextView) itemView.findViewById(R.id.tv_message_status);
            tv_alphabet = (TextView) itemView.findViewById(R.id.tv_alphabet);
            active_dot = (CircleImageView) itemView.findViewById(R.id.civ_active_dot);

            tv_time_offline = (TextView) itemView.findViewById(R.id.tv_offline_time);


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
