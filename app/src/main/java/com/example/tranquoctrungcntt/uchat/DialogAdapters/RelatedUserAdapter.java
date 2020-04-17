package com.example.tranquoctrungcntt.uchat.DialogAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class RelatedUserAdapter extends RecyclerView.Adapter<RelatedUserAdapter.RelatedUserViewHolder> {

    private final Context mAdapterContext;
    private final List<User> mList;

    public RelatedUserAdapter(Context mAdapterContext, List<User> mList) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RelatedUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {

        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_related_user, viewGroup, false);
        return new RelatedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedUserViewHolder mViewholder, int mIndex) {

        final User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewholder.user_avatar);

        mViewholder.tv_name.setText(user.getName() + (user.getUserId().equals(getMyFirebaseUserId()) ? " (bạn)" : ""));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class RelatedUserViewHolder extends RecyclerView.ViewHolder {

        final CircleImageView user_avatar;
        final TextView tv_name;

        public RelatedUserViewHolder(@NonNull View itemView) {
            super(itemView);

            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);

        }


    }
}
