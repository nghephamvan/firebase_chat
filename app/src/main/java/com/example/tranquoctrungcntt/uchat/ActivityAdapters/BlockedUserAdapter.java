package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.showUnlockUserConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.BlockedUserViewHolder> {

    private final Context mAdapterContext;
    private final List<User> mList;

    public BlockedUserAdapter(Context mAdapterContext, List<User> mList) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public BlockedUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_blocked_user, viewGroup, false);
        return new BlockedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlockedUserViewHolder mViewHolder, final int mIndex) {


        User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(user.getName());

        mViewHolder.btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUnlockUserConfirmDialog(mAdapterContext, user.getUserId());

            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class BlockedUserViewHolder extends RecyclerView.ViewHolder {

        final CircleImageView user_avatar;
        final TextView tv_name;
        final FrameLayout btn_unlock;

        public BlockedUserViewHolder(@NonNull View itemView) {
            super(itemView);

            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            btn_unlock = (FrameLayout) itemView.findViewById(R.id.frame_unlock);

        }
    }

}
