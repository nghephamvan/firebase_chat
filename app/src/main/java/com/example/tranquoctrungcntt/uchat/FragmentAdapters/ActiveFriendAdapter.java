package com.example.tranquoctrungcntt.uchat.FragmentAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_WAVE_HAND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_SINGLE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkHasChildBlock;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.markMessageIsSent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.getNotificationsId;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class ActiveFriendAdapter extends RecyclerView.Adapter<ActiveFriendAdapter.OnlineFriendsViewHolder> {

    private final Context mAdapterContext;
    private final List<User> mList;
    private final Map<String, Boolean> mMapWaveHand;
    private final Animation shake;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public ActiveFriendAdapter(Context mAdapterContext, List<User> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        mMapWaveHand = new HashMap<>();

        shake = AnimationUtils.loadAnimation(mAdapterContext, R.anim.shake);
        shake.setRepeatCount(10);

        isDelaying = false;

    }

    @NonNull
    @Override
    public OnlineFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_active_friend, parent, false);
        return new OnlineFriendsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull OnlineFriendsViewHolder mViewHolder, int mIndex) {

        final User user = mList.get(mIndex);

        setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), mViewHolder.user_avatar);

        mViewHolder.tv_name.setText(user.getName());

        if (mMapWaveHand.get(user.getUserId()) != null && mMapWaveHand.get(user.getUserId())) {

            mViewHolder.btn_wavehand.setImageResource(R.drawable.ic_wave_hand_active);
            mViewHolder.btn_wavehand.setClickable(false);

        } else {

            mViewHolder.btn_wavehand.setImageResource(R.drawable.ic_wave_hand_nonactive);
            mViewHolder.btn_wavehand.setClickable(true);
            mViewHolder.btn_wavehand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    waveHand(mIndex);

                    mViewHolder.btn_wavehand.startAnimation(shake);


                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void waveHand(int index) {

        final String receiverId = mList.get(index).getUserId();

        final String messageId = ROOT_REF.child(CHILD_MESSAGES).push().getKey();

        final int notificationId = getNotificationsId();

        final long sendTime = getCurrentTimeInMilies();

        final Message myMess = new Message(
                messageId, getMyFirebaseUserId(), receiverId, null,
                "Đã vãy tay chào \uD83D\uDC4B", MESSAGE_STATUS_SENDING,
                sendTime, 0, MESSAGE_TYPE_WAVE_HAND,
                notificationId, 0, null, false, null, null, null);

        final Message userMess = new Message(
                messageId, getMyFirebaseUserId(), receiverId, null,
                "Đã vãy tay chào bạn \uD83D\uDC4B", MESSAGE_STATUS_SENT,
                sendTime, 0, MESSAGE_TYPE_WAVE_HAND,
                notificationId, 0, null, false, null, null, null);


        if (isConnectedToFirebaseService(mAdapterContext)) {

            waveHandOnline(messageId, receiverId, myMess, userMess, notificationId, index);

        } else {

            waveHandOffline(messageId, receiverId, myMess, userMess, notificationId, index);

        }

    }

    private void waveHandOffline(String messageId, String receiverId,
                                 Message myMess, Message userMess,
                                 int notificationId, int index) {

        mMapWaveHand.put(receiverId, true);

        notifyItemChanged(index);

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(receiverId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        checkHasChildBlock(receiverId, new AppConstants.AppInterfaces.FirebaseBooleanCallBack() {
                            @Override
                            public void OnCallBack(boolean hasChildBlock) {

                                if (hasChildBlock) {

                                    mMapWaveHand.remove(receiverId);

                                    notifyItemChanged(index);

                                    showMessageDialog(mAdapterContext, "Lỗi trong khi gửi tin nhắn");

                                } else {

                                    markMessageIsSent(messageId, receiverId, null);

                                    ROOT_REF.child(CHILD_MESSAGES)
                                            .child(receiverId).child(getMyFirebaseUserId())
                                            .child(messageId).setValue(userMess)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                        @Override
                                                        public void OnCallBack(User callbackUserProfile) {

                                                            sendNotificationToUser(
                                                                    receiverId,
                                                                    getMyFirebaseUserId(),
                                                                    null,
                                                                    messageId,
                                                                    "Đã vãy tay chào bạn \uD83D\uDC4B",
                                                                    callbackUserProfile.getName(),
                                                                    callbackUserProfile.getThumbAvatarUrl(),
                                                                    NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                                    notificationId);

                                                        }
                                                    });


                                                }
                                            });


                                }
                            }
                        });


                    }

                });

    }

    private void waveHandOnline(String messageId, String receiverId, Message myMess, Message userMess, int notificationId, int index) {


        mMapWaveHand.put(receiverId, true);

        notifyItemChanged(index);

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(receiverId)
                .child(messageId).setValue(myMess)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ROOT_REF.child(CHILD_MESSAGES)
                                .child(getMyFirebaseUserId()).child(receiverId)
                                .child(messageId).child(kMessageStatus).setValue(MESSAGE_STATUS_SENT)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        ROOT_REF.child(CHILD_MESSAGES)
                                                .child(receiverId).child(getMyFirebaseUserId())
                                                .child(messageId).setValue(userMess)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                                            @Override
                                                            public void OnCallBack(User callbackUserProfile) {

                                                                sendNotificationToUser(
                                                                        receiverId,
                                                                        getMyFirebaseUserId(),
                                                                        null,
                                                                        messageId,
                                                                        "Đã vãy tay chào bạn \uD83D\uDC4B",
                                                                        callbackUserProfile.getName(),
                                                                        callbackUserProfile.getThumbAvatarUrl(),
                                                                        NOTIFICATION_TYPE_SINGLE_MESSAGE,
                                                                        notificationId);

                                                            }
                                                        });

                                                    }
                                                });

                                    }
                                });


                    }


                });
    }

    public class OnlineFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView user_avatar;
        final TextView tv_name;
        final ImageView btn_wavehand;

        public OnlineFriendsViewHolder(View itemView) {
            super(itemView);

            user_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            btn_wavehand = (ImageView) itemView.findViewById(R.id.img_wavehand);

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
