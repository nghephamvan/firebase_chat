package com.example.tranquoctrungcntt.uchat.FragmentAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ManageGroupViewHolder> {


    private final Context mAdapterContext;
    private final List<GroupDetail> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public GroupAdapter(Context mAdapterContext, List<GroupDetail> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public ManageGroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_group, viewGroup, false);
        return new ManageGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageGroupViewHolder mViewHolder, int mIndex) {


        final GroupDetail groupDetail = mList.get(mIndex);

        setAvatarToView(mAdapterContext, groupDetail.getGroupThumbAvatar(), groupDetail.getGroupName(), mViewHolder.group_avatar);

        mViewHolder.tv_name.setText(groupDetail.getGroupName());
        mViewHolder.tv_num_member.setText(groupDetail.getMember() != null ? groupDetail.getMember().size() + " thành viên" : null);
        mViewHolder.tv_num_member.setVisibility(groupDetail.getMember() != null ? View.VISIBLE : View.GONE);

        if (mIndex > 0) {

            if (groupDetail.getGroupName().charAt(0) != mList.get(mIndex - 1).getGroupName().charAt(0)) {
                mViewHolder.tv_alphabet.setVisibility(View.VISIBLE);
                mViewHolder.tv_alphabet.setText(groupDetail.getGroupName().charAt(0) + "");
            } else {
                mViewHolder.tv_alphabet.setVisibility(View.GONE);
                mViewHolder.tv_alphabet.setText(null);
            }

        } else {

            mViewHolder.tv_alphabet.setVisibility(View.VISIBLE);
            mViewHolder.tv_alphabet.setText(groupDetail.getGroupName().charAt(0) + "");

        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ManageGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView group_avatar;

        final TextView tv_name;
        final TextView tv_alphabet;
        final TextView tv_num_member;

        public ManageGroupViewHolder(@NonNull View itemView) {
            super(itemView);


            group_avatar = (CircleImageView) itemView.findViewById(R.id.civ_group_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_num_member = (TextView) itemView.findViewById(R.id.tv_num_member);
            tv_alphabet = (TextView) itemView.findViewById(R.id.tv_alphabet);

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
