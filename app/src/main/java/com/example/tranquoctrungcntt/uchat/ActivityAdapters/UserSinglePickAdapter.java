package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isUserOnline;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeAgoShortly;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;

public class UserSinglePickAdapter extends RecyclerView.Adapter<UserSinglePickAdapter.UserSinglePickViewHolder> {

    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private final Context mAdapterContext;
    private final List<User> mList;

    public UserSinglePickAdapter(Context mAdapterContext, List<User> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

    }

    @NonNull
    @Override
    public UserSinglePickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_user_single_pick, parent, false);
        return new UserSinglePickViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserSinglePickViewHolder mViewHolder, final int mIndex) {

        final User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(user.getName());

        if (isUserOnline(user.getLastSeen())) {

            mViewHolder.active_dot.setVisibility(View.VISIBLE);
            mViewHolder.tv_time_offline.setVisibility(View.GONE);
            mViewHolder.tv_time_offline.setText(null);

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

    public class UserSinglePickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name;
        final CircleImageView active_dot;
        final TextView tv_time_offline;

        public UserSinglePickViewHolder(View itemView) {
            super(itemView);

            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            active_dot = (CircleImageView) itemView.findViewById(R.id.civ_active_dot);
            tv_time_offline = (TextView) itemView.findViewById(R.id.tv_offline_time);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) mItemClickListener.OnItemClick(v, getAdapterPosition());
        }

    }


}
