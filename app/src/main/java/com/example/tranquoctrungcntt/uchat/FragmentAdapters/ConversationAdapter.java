package com.example.tranquoctrungcntt.uchat.FragmentAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Models.Conversation;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_RECEIVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SEEN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isGroupInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isSeen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isUserOnline;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatConversationTime;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeAgoShortly;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.makeCall;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private static final Typeface typefaceNormal = Typeface.create("sans-serif-medium", Typeface.NORMAL);
    private static final Typeface typefaceBold = Typeface.create("sans-serif-medium", Typeface.BOLD);
    private final Context mAdapterContext;
    private final List<Conversation> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public ConversationAdapter(Context mAdapterContext, List<Conversation> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_conversation, viewGroup, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder mViewHolder, int mIndex) {


        final Conversation conversation = mList.get(mIndex);

        final Message message = conversation.getMessage();

        final boolean isMyMessage = message.getSenderId().equals(getMyFirebaseUserId());

        mViewHolder.tv_content.setText(message.getContent());

        mViewHolder.tv_time.setText("‧ " + formatConversationTime(message.getSendTime()));

        if (conversation.isMuteNotifications())
            mViewHolder.img_no_notify.setVisibility(View.VISIBLE);
        else mViewHolder.img_no_notify.setVisibility(View.GONE);

        if (conversation.isGroupConversation()) { // tin nhắn nhóm

            mViewHolder.tv_name.setText(conversation.getGroupDetail().getGroupName());

            setAvatarToView(mAdapterContext, conversation.getGroupDetail().getGroupThumbAvatar(), conversation.getGroupDetail().getGroupName(), mViewHolder.conversation_avatar);

            mViewHolder.btn_callback.setVisibility(View.GONE);
            mViewHolder.btn_callback.setImageDrawable(null);
            mViewHolder.btn_callback.setOnClickListener(null);

            mViewHolder.tv_time_offline.setVisibility(View.GONE);
            mViewHolder.tv_time_offline.setText(null);

            mViewHolder.active_dot.setVisibility(View.GONE);

            if (isMyMessage) {

                showSeenTextMessageStyle(mViewHolder);

                if (isGroupInstantMessage(message)) {

                    mViewHolder.img_status.setVisibility(View.GONE);
                    mViewHolder.img_status.setImageDrawable(null);

                } else formatMessageStatus(conversation, mViewHolder);

            } else {

                if (isSeen(message.getStatus())) {

                    showSeenTextMessageStyle(mViewHolder);

                    mViewHolder.img_status.setVisibility(View.GONE);
                    mViewHolder.img_status.setImageDrawable(null);

                } else {

                    showUnSeenTextMessageStyle(mViewHolder);

                    mViewHolder.img_status.setVisibility(View.VISIBLE);
                    mViewHolder.img_status.setImageResource(R.drawable.ic_unseen_dot);

                }

            }

        } else {

            mViewHolder.tv_name.setText(conversation.getUserProfile().getName());

            setAvatarToView(mAdapterContext, conversation.getUserProfile().getThumbAvatarUrl(), conversation.getUserProfile().getName(), mViewHolder.conversation_avatar);
            // tin nhắn cá nhân

            checkToDisplayActiveStatus(mViewHolder, conversation);

            if (isMyMessage) {

                showSeenTextMessageStyle(mViewHolder);

                formatMessageStatus(conversation, mViewHolder);

            } else {

                if (isSeen(message.getStatus())) {

                    showSeenTextMessageStyle(mViewHolder);

                    mViewHolder.img_status.setVisibility(View.GONE);
                    mViewHolder.img_status.setImageDrawable(null);

                } else {

                    final boolean isMissedCall = message.getType() == MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED || message.getType() == MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED;

                    if (isMissedCall) {

                        showUnSeenMissedCallMessageStyle(mViewHolder);

                    } else showUnSeenTextMessageStyle(mViewHolder);

                    mViewHolder.img_status.setVisibility(View.VISIBLE);
                    mViewHolder.img_status.setImageResource(R.drawable.ic_unseen_dot);

                }

            }

            checkToShowCallBackButton(mViewHolder, message);

        }


    }

    private void formatMessageStatus(Conversation conversation, ConversationViewHolder viewHolder) {
        switch (conversation.getMessage().getStatus()) {

            case MESSAGE_STATUS_SENDING:

                viewHolder.img_status.setImageResource(R.drawable.ic_status_sending);
                viewHolder.img_status.setVisibility(View.VISIBLE);

                break;

            case MESSAGE_STATUS_SENT:

                viewHolder.img_status.setImageResource(R.drawable.ic_status_sent);
                viewHolder.img_status.setVisibility(View.VISIBLE);

                break;

            case MESSAGE_STATUS_RECEIVED:

                viewHolder.img_status.setImageResource(R.drawable.ic_status_received);
                viewHolder.img_status.setVisibility(View.VISIBLE);

                break;

            case MESSAGE_STATUS_SEEN:

                viewHolder.img_status.setVisibility(View.GONE);
                viewHolder.img_status.setImageDrawable(null);

                break;
        }
    }

    private void checkToDisplayActiveStatus(ConversationViewHolder conversationViewHolder, Conversation conversation) {

        Log.d("AAAAAAAAAA5", conversation.getConversationId() + "");

        if (conversation.isBlockedConversation()) {

            conversationViewHolder.active_dot.setVisibility(View.GONE);
            conversationViewHolder.tv_time_offline.setVisibility(View.GONE);
            conversationViewHolder.tv_time_offline.setText(null);

        } else {

            User user = conversation.getUserProfile();

            if (isUserOnline(user.getLastSeen())) {

                conversationViewHolder.active_dot.setVisibility(View.VISIBLE);
                conversationViewHolder.tv_time_offline.setVisibility(View.GONE);
                conversationViewHolder.tv_time_offline.setText(null);

            } else {

                conversationViewHolder.active_dot.setVisibility(View.GONE);

                final String stringValue = formatTimeAgoShortly(user.getLastSeen());
                conversationViewHolder.tv_time_offline.setText(stringValue);
                conversationViewHolder.tv_time_offline.setVisibility(stringValue != null ? View.VISIBLE : View.GONE);

            }
        }


    }

    private void checkToShowCallBackButton(ConversationViewHolder conversationViewHolder, Message message) {

        final boolean isVideoCall = message.getType() == MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED || message.getType() == MESSAGE_TYPE_VIDEO_CALL_SUCCESS;
        final boolean isVoiceCall = message.getType() == MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED || message.getType() == MESSAGE_TYPE_VOICE_CALL_SUCCESS;

        if (isVideoCall || isVoiceCall) {

            final String callPartner = message.getSenderId().equals(getMyFirebaseUserId()) ? message.getReceiverId() : message.getSenderId();

            conversationViewHolder.btn_callback.setVisibility(View.VISIBLE);
            conversationViewHolder.btn_callback.setImageResource(isVideoCall ? R.drawable.ic_video_call_circle : R.drawable.ic_voice_call_circle);
            conversationViewHolder.btn_callback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeCall(mAdapterContext, callPartner, isVideoCall);
                }
            });

        } else {
            conversationViewHolder.btn_callback.setVisibility(View.GONE);
            conversationViewHolder.btn_callback.setImageDrawable(null);
            conversationViewHolder.btn_callback.setOnClickListener(null);
        }

    }

    @Override
    public int getItemCount() { return mList.size(); }

    private void showSeenTextMessageStyle(ConversationViewHolder conversationViewHolder) {

        conversationViewHolder.tv_content.setTextColor(mAdapterContext.getResources().getColor(R.color.dark_grey));
        conversationViewHolder.tv_name.setTextColor(mAdapterContext.getResources().getColor(R.color.black));
        conversationViewHolder.tv_content.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        conversationViewHolder.tv_name.setTypeface(typefaceNormal);
    }

    private void showUnSeenTextMessageStyle(ConversationViewHolder conversationViewHolder) {

        conversationViewHolder.tv_content.setTextColor(mAdapterContext.getResources().getColor(R.color.black));
        conversationViewHolder.tv_name.setTextColor(mAdapterContext.getResources().getColor(R.color.black));
        conversationViewHolder.tv_content.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        conversationViewHolder.tv_name.setTypeface(typefaceBold);

    }

    private void showUnSeenMissedCallMessageStyle(ConversationViewHolder conversationViewHolder) {

        conversationViewHolder.tv_content.setTextColor(mAdapterContext.getResources().getColor(R.color.red));
        conversationViewHolder.tv_name.setTextColor(mAdapterContext.getResources().getColor(R.color.red));
        conversationViewHolder.tv_content.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        conversationViewHolder.tv_name.setTypeface(typefaceBold);

    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        final CircleImageView conversation_avatar;
        final TextView tv_name;
        final TextView tv_time;
        final TextView tv_time_offline;
        final EmojiAppCompatTextView tv_content;
        final ImageView img_no_notify, btn_callback;
        final CircleImageView active_dot;
        final ImageView img_status;


        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            conversation_avatar = (CircleImageView) itemView.findViewById(R.id.conversation_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name_conv);
            tv_content = (EmojiAppCompatTextView) itemView.findViewById(R.id.tv_mess_conv);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time_conv);
            active_dot = (CircleImageView) itemView.findViewById(R.id.civ_active_dot);
            tv_time_offline = (TextView) itemView.findViewById(R.id.tv_offline_time);
            img_no_notify = (ImageView) itemView.findViewById(R.id.img_no_notify);
            btn_callback = (ImageView) itemView.findViewById(R.id.btn_callback);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);

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
