package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.CensorMember;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.GroupMemberModel;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class CensorMemberAdapter extends RecyclerView.Adapter<CensorMemberAdapter.CensorMemberViewHolder> {


    private final CensorMember mAdapterContext;
    private final List<GroupMemberModel> mList;
    private final Map<String, GroupMemberModel> mAcceptedMap;
    private final Map<String, Boolean> mDeniedMap;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public CensorMemberAdapter(CensorMember mAdapterContext,
                               List<GroupMemberModel> mList,
                               Map<String, GroupMemberModel> mAcceptedMap,
                               Map<String, Boolean> mDeniedMap,
                               AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mAcceptedMap = mAcceptedMap;
        this.mDeniedMap = mDeniedMap;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;

    }

    @NonNull
    @Override
    public CensorMemberViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_censor_member, viewGroup, false);
        return new CensorMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CensorMemberViewHolder mViewHolder, int mIndex) {


        final User addedUserProfile = mList.get(mIndex).getMember();

        final User adderProfile = mList.get(mIndex).getAdder();

        setAvatarToView(mAdapterContext, addedUserProfile.getThumbAvatarUrl(), addedUserProfile.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(addedUserProfile.getName());

        if (adderProfile.getUserId().equals(getMyFirebaseUserId()))
            mViewHolder.tv_adder_name.setText("Do bạn thêm vào");
        else mViewHolder.tv_adder_name.setText("Người thêm: " + adderProfile.getName());


        bindStatus(mViewHolder, mIndex);
    }


    public void bindStatus(CensorMemberViewHolder censorMemberViewHolder, int index) {

        final GroupMemberModel groupMemberModel = mList.get(index);

        if (mAcceptedMap.get(groupMemberModel.getMember().getUserId()) != null
                || mDeniedMap.get(groupMemberModel.getMember().getUserId()) != null) {

            censorMemberViewHolder.btn_deny.setClickable(false);
            censorMemberViewHolder.btn_deny.setVisibility(View.GONE);
            censorMemberViewHolder.btn_accept.setClickable(false);
            censorMemberViewHolder.btn_accept.setBackgroundResource(0);
            censorMemberViewHolder.btn_accept.setTextColor(mAdapterContext.getResources().getColor(R.color.grey));

            if (mAcceptedMap.get(groupMemberModel.getMember().getUserId()) != null) {

                censorMemberViewHolder.btn_accept.setText("Cho phép");
                censorMemberViewHolder.btn_accept.setTextColor(mAdapterContext.getResources().getColor(R.color.green));

            } else if (mDeniedMap.get(groupMemberModel.getMember().getUserId()) != null) {

                censorMemberViewHolder.btn_accept.setText("Từ chối");
                censorMemberViewHolder.btn_accept.setTextColor(mAdapterContext.getResources().getColor(R.color.red));

            }


        } else {

            censorMemberViewHolder.btn_deny.setClickable(true);
            censorMemberViewHolder.btn_deny.setVisibility(View.VISIBLE);
            censorMemberViewHolder.btn_deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDeniedMap.put(groupMemberModel.getMember().getUserId(), true);

                    notifyItemChanged(index);
                }
            });

            censorMemberViewHolder.btn_accept.setClickable(true);
            censorMemberViewHolder.btn_accept.setText("Cho phép");
            censorMemberViewHolder.btn_accept.setBackgroundResource(R.drawable.filled_light_grey_bg);
            censorMemberViewHolder.btn_accept.setTextColor(mAdapterContext.getResources().getColor(R.color.black));
            censorMemberViewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isConnectedToFirebaseService(mAdapterContext)) {

                        mAcceptedMap.put(groupMemberModel.getMember().getUserId(), groupMemberModel);

                        notifyItemChanged(index);


                    } else showNoConnectionDialog(mAdapterContext);

                }
            });

        }


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class CensorMemberViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name, tv_adder_name;
        final Button btn_accept;
        final FrameLayout btn_deny;

        public CensorMemberViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_adder_name = (TextView) itemView.findViewById(R.id.tv_adder_name);
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

