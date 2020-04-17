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

import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLastAndMiddleName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class PickedUserAdapter extends RecyclerView.Adapter<PickedUserAdapter.PickedMemberViewHolder> {

    private final Context mAdapterContext;
    private final List<User> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;

    public PickedUserAdapter(Context mAdapterContext, List<User> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

    }

    @NonNull
    @Override
    public PickedMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_picked_user, parent, false);
        return new PickedMemberViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PickedMemberViewHolder mViewHolder, int mIndex) {

        final User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(getLastAndMiddleName(user.getName()));


    }

    @Override
    public int getItemCount() {
        return mList.size();
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

            mItemClickListener.OnItemClick(v, getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {

            mItemClickListener.OnItemLongClick(v, getAdapterPosition());

            return true;
        }
    }


}
