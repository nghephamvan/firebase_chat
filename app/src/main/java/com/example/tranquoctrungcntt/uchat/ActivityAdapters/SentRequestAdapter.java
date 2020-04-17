package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.showCancelRequestConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class SentRequestAdapter extends RecyclerView.Adapter<SentRequestAdapter.SentRequestViewHolder> {

    private final Context mAdapterContext;
    private final List<User> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public SentRequestAdapter(Context mAdapterContext, List<User> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public SentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_sent_request, viewGroup, false);
        return new SentRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SentRequestViewHolder mViewholder, int mIndex) {


        final User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewholder.user_avatar);

        mViewholder.tv_name.setText(user.getName());
        mViewholder.tv_gender.setText(user.getGender());


        mViewholder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCancelRequestConfirmDialog(mAdapterContext, user.getUserId());

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class SentRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name, tv_gender;
        final Button btn_cancel;

        public SentRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_gender = (TextView) itemView.findViewById(R.id.tv_gender);
            btn_cancel = (Button) itemView.findViewById(R.id.btn_cancel);

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
