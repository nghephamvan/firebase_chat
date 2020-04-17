package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.GroupMemberPage;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.GroupMemberModel;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.GroupRole.ROLE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.PickMemberViewHolder> {

    private final GroupMemberPage mAdapterContext;
    private final List<GroupMemberModel> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public GroupMemberAdapter(GroupMemberPage mAdapterContext, List<GroupMemberModel> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;

    }

    @NonNull
    @Override
    public PickMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_group_member, parent, false);
        return new PickMemberViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PickMemberViewHolder mViewHolder, int mIndex) {

        GroupMemberModel member = mList.get(mIndex);

        User memberProfile = member.getMember();
        User adderProfile = member.getAdder();

        setAvatarToView(mAdapterContext, memberProfile.getThumbAvatarUrl(), memberProfile.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(memberProfile.getName());

        if (member.getRole().equals(ROLE_ADMIN)) {

            mViewHolder.tv_role.setText("Quản trị viên");
            mViewHolder.civ_admin.setVisibility(View.VISIBLE);

        } else {

            if (adderProfile.getUserId().equals(getMyFirebaseUserId()))
                mViewHolder.tv_role.setText("Do bạn thêm vào");
            else mViewHolder.tv_role.setText("Người thêm: " + adderProfile.getName());

            mViewHolder.civ_admin.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class PickMemberViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name, tv_role;
        final CircleImageView civ_admin;

        public PickMemberViewHolder(View itemView) {
            super(itemView);

            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_role = (TextView) itemView.findViewById(R.id.tv_role);
            civ_admin = (CircleImageView) itemView.findViewById(R.id.civ_admin);

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
