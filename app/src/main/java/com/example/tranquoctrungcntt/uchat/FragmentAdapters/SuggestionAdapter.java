package com.example.tranquoctrungcntt.uchat.FragmentAdapters;


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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_BLACKLIST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_CANCEL_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_RECEIVER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_REQUEST_SENDER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEND_FRIEND_REQUEST_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_NEW_FRIEND_REQUEST;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_MILIES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LOADING = 1;
    private static final int TYPE_USER = 0;

    private final Context mAdapterContext;
    private final List<User> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public SuggestionAdapter(Context mAdapterContext, List<User> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_LOADING)
            return new SuggestionViewHolder(LayoutInflater.from(mAdapterContext)
                    .inflate(R.layout.progressbar_layout, viewGroup, false));

        return new SuggestionViewHolder(LayoutInflater.from(mAdapterContext)
                .inflate(R.layout.row_suggestion, viewGroup, false));


    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).getUserId() == null)
            return TYPE_LOADING;
        return TYPE_USER;
    }

    public void removeProgressbar() {
        mList.remove(mList.size() - 1);
        notifyItemRemoved(mList.size() - 1);
    }

    public void addProgressbar() {
        mList.add(new User());
        notifyItemInserted(mList.size() - 1);
    }

    public void loadMoreItems(ArrayList<User> newItems) {

        mList.addAll(newItems);
        notifyItemRangeInserted(mList.size() - newItems.size(), newItems.size());

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mRootViewHolder, int mIndex) {

        if (getItemViewType(mIndex) != TYPE_LOADING) {

            SuggestionViewHolder mViewHolder = (SuggestionViewHolder) mRootViewHolder;

            final User user = (User) mList.get(mIndex);

            setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

            mViewHolder.tv_name.setText(user.getName());

            mViewHolder.tv_gender.setText(user.getGender());

            mViewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ROOT_REF.child(CHILD_RELATIONSHIPS)
                            .child(getMyFirebaseUserId()).child(user.getUserId())
                            .child(CHILD_BLACKLIST)
                            .setValue(true);

                }
            });

            mViewHolder.btn_send_req.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addFriend(mIndex);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void addFriend(int index) {

        final String userIdToCheck = ((User) mList.get(index)).getUserId();

        if (isConnectedToFirebaseService(mAdapterContext)) {

            ROOT_REF.child(CHILD_RELATIONSHIPS)
                    .child(getMyFirebaseUserId()).child(userIdToCheck)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(CHILD_FRIEND)
                                    || dataSnapshot.hasChild(CHILD_I_BLOCKED_USER)
                                    || dataSnapshot.hasChild(CHILD_USER_BLOCKED_ME)
                                    || dataSnapshot.hasChild(CHILD_REQUEST_RECEIVER)
                                    || dataSnapshot.hasChild(CHILD_REQUEST_SENDER)) {

                                mList.remove(index);
                                notifyItemRemoved(index);
                                notifyItemRangeChanged(index, getItemCount() - index);

                                showMessageDialog(mAdapterContext, "Không thể gửi lời mời kết bạn cho người dùng này");

                            } else {

                                ROOT_REF.child(CHILD_SEND_FRIEND_REQUEST_TIMER)
                                        .child(getMyFirebaseUserId()).child(userIdToCheck)
                                        .child(CHILD_CANCEL_TIME)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                long timeToWait = -1;

                                                if (dataSnapshot.exists()) {

                                                    long timeValue = dataSnapshot.getValue(Long.class);
                                                    long timePassed = getCurrentTimeInMilies() - timeValue;

                                                    if (timePassed <= TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_MILIES)
                                                        timeToWait = TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_MILIES - timePassed;

                                                }

                                                if (timeToWait == -1) {

                                                    sendRequest(userIdToCheck);

                                                } else {

                                                    long timeleft = TimeUnit.MILLISECONDS.toMinutes(timeToWait);
                                                    String content = "Bạn phải chờ " + timeleft + " phút nữa để có thể gửi lại lời mời !";

                                                    showMessageDialog(mAdapterContext, content);

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        } else showNoConnectionDialog(mAdapterContext);

    }

    private void sendRequest(String userIdToSend) {

        ROOT_REF.child(CHILD_RELATIONSHIPS)
                .child(getMyFirebaseUserId()).child(userIdToSend)
                .child(CHILD_REQUEST_SENDER).setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.child(CHILD_RELATIONSHIPS)
                                .child(getMyFirebaseUserId()).child(userIdToSend)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(CHILD_FRIEND)
                                                || dataSnapshot.hasChild(CHILD_I_BLOCKED_USER)
                                                || dataSnapshot.hasChild(CHILD_USER_BLOCKED_ME)
                                                || dataSnapshot.hasChild(CHILD_REQUEST_RECEIVER)) {

                                            ROOT_REF.child(CHILD_RELATIONSHIPS)
                                                    .child(getMyFirebaseUserId()).child(userIdToSend)
                                                    .child(CHILD_REQUEST_SENDER)
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    showMessageDialog(mAdapterContext, "Lỗi xử lý, vui lòng thử lại !");

                                                }
                                            });


                                        } else {

                                            Map<String, Object> mapSentReq = new HashMap<>();

                                            mapSentReq.put("/" + CHILD_RELATIONSHIPS + "/" + userIdToSend + "/" + getMyFirebaseUserId() + "/" + CHILD_REQUEST_RECEIVER, true);
                                            mapSentReq.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userIdToSend + "/" + CHILD_REQUEST_SENDER, true);
                                            mapSentReq.put("/" + CHILD_RELATIONSHIPS + "/" + getMyFirebaseUserId() + "/" + userIdToSend + "/" + CHILD_BLACKLIST, null);
                                            mapSentReq.put("/" + CHILD_SEND_FRIEND_REQUEST_TIMER + "/" + getMyFirebaseUserId() + "/" + userIdToSend + "/" + CHILD_CANCEL_TIME, null);

                                            ROOT_REF.updateChildren(mapSentReq).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                        @Override
                                                        public void OnCallBack(User callbackUserProfile) {

                                                            sendNotificationToUser(
                                                                    userIdToSend,
                                                                    getMyFirebaseUserId(),
                                                                    null,
                                                                    null,
                                                                    "Đã gửi cho bạn lời mời kết bạn.",
                                                                    callbackUserProfile.getName(),
                                                                    callbackUserProfile.getThumbAvatarUrl(),
                                                                    NOTIFICATION_TYPE_NEW_FRIEND_REQUEST,
                                                                    getNotificationsId());

                                                        }
                                                    });

                                                }
                                            });


                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }
                });

    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name, tv_gender;
        final Button btn_send_req;
        final FrameLayout btn_delete;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_gender = (TextView) itemView.findViewById(R.id.tv_gender);
            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            btn_send_req = (Button) itemView.findViewById(R.id.btn_add);
            btn_delete = (FrameLayout) itemView.findViewById(R.id.frame_delete);

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
            mItemClickListener.OnItemLongClick(v, getAdapterPosition());
            return true;
        }
    }

}


