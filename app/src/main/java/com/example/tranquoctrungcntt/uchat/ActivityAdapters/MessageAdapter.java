package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.emoji.widget.EmojiAppCompatEditText;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.Chat;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.DialogAdapters.EditHistoryAdapter;
import com.example.tranquoctrungcntt.uchat.Objects.EditHistory;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_RECEIVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SEEN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_LIKE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_MULTIPLE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_REMOVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_STICKER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_TEXT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VOICE_CALL_SUCCESS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_WAVE_HAND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_REMOVE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TAKING_BACK_MESSAGE_TIME_LIMIT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kEditHistory;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageContent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageType;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isEmailValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMapAddress;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMediaMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isPhoneNumber;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMediaOfMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.copyContent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSecondsToHours;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSendTime;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSmallNumber;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeDivider;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardSingleMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardSingleMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.makeCall;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.openMap;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.openWebBrowser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.sendEmail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewThisSingleUserMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setMediaUrlToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_ME = 0;

    private final Chat mAdapterContext;
    private final List<Message> mList;
    private final SeekBarUpdater seekbarUpdater = new SeekBarUpdater();
    private final BottomSheetDialog mOptionsDialog;
    private final User mUserProfile;
    private final String mChattingUserId;
    private final Map<String, ArrayList<Media>> mMediaMap;

    private MessageViewHolder mSeenImageHolder;
    private MessageViewHolder mSelectedBackgroundHolder;
    private MessageViewHolder mCurrentStatusViewHolder;

    private String mCurrentStatusViewId;
    private String mShowingOptionMessageId;
    private String mShowingPhoneNumberOptionMessageId;
    private String mShowingEditHistoryMessageId;

    private AlertDialog mPhoneNumberOptionDialog;
    private AlertDialog mEditHistoryDialog;

    private boolean isPreparing;

    private String mPlayingAudioMessageId;
    private MediaPlayer mAudioPlayer;
    private MessageViewHolder mPlayingAudioHolder;

    public MessageAdapter(Chat mAdapterContext, List<Message> mList, String mChattingUserId, User mUserProfile) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mUserProfile = mUserProfile;
        this.mChattingUserId = mChattingUserId;

        mSeenImageHolder = null;
        mSelectedBackgroundHolder = null;

        mCurrentStatusViewId = null;
        mCurrentStatusViewHolder = null;

        mPlayingAudioMessageId = null;
        mPlayingAudioHolder = null;

        mAudioPlayer = null;

        mShowingOptionMessageId = null;

        mShowingPhoneNumberOptionMessageId = null;
        mPhoneNumberOptionDialog = null;

        mEditHistoryDialog = null;
        mShowingEditHistoryMessageId = null;

        mMediaMap = new HashMap<>();

        isPreparing = false;

        mOptionsDialog = new BottomSheetDialog(mAdapterContext);
        mOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mOptionsDialog.setContentView(R.layout.dialog_message_options);
        mOptionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if (mSelectedBackgroundHolder != null) {

                    unselectView(mSelectedBackgroundHolder);

                    mSelectedBackgroundHolder = null;

                    mShowingOptionMessageId = null;

                }
            }
        });


    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType == TYPE_ME ? R.layout.row_message_right : R.layout.row_message_left, viewGroup, false);
        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {

        if (mList.get(position).getSenderId().equals(getMyFirebaseUserId()))

            return TYPE_ME;

        return TYPE_USER;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder mViewHolder, final int mIndex) {

        final Message message = mList.get(mIndex);

        showViewForMessageType(message, mViewHolder);

        mViewHolder.linear_forwarded_message.setVisibility(message.isForwardedMessage() ? View.VISIBLE : View.GONE);

        mViewHolder.tv_message_content.setText(message.getContent() + "");

        mViewHolder.tv_send_time.setText(formatSendTime(message.getSendTime()));

        mViewHolder.tv_status.setText(formatMessageStatusToString(message));

        if (getItemViewType(mIndex) == TYPE_ME)

            formatMessageStatusToImage(mViewHolder, mIndex);

        else checkToShowAvatar(mViewHolder, mIndex);

        onClickMessage(mViewHolder, mIndex);

        checkToShowDivider(mViewHolder, mIndex);

        keepSelectedView(message, mViewHolder);

        if (message.getEditHistory() != null && message.getType() == MESSAGE_TYPE_TEXT)
            mViewHolder.img_edited.setVisibility(View.VISIBLE);
        else mViewHolder.img_edited.setVisibility(View.GONE);

    }

    private void keepSelectedView(Message message, MessageViewHolder messageViewHolder) {

        if (mCurrentStatusViewId != null && mCurrentStatusViewId.equals(message.getMessageId())) {

            messageViewHolder.tv_status.setVisibility(View.VISIBLE);
            messageViewHolder.tv_send_time.setVisibility(View.VISIBLE);

            switch (message.getType()) {

                case MESSAGE_TYPE_AUDIO:

                    messageViewHolder.linear_audio.setSelected(true);

                    break;

                case MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED:
                case MESSAGE_TYPE_VIDEO_CALL_SUCCESS:
                case MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED:
                case MESSAGE_TYPE_VOICE_CALL_SUCCESS:

                    messageViewHolder.linear_call.setSelected(true);

                case MESSAGE_TYPE_TEXT:

                    messageViewHolder.tv_message_content.setSelected(true);

                    break;
            }

        } else {

            messageViewHolder.tv_status.setVisibility(View.GONE);
            messageViewHolder.tv_send_time.setVisibility(View.GONE);

            unselectView(messageViewHolder);

        }
    }

    private void showViewForMessageType(Message message, MessageViewHolder messageViewHolder) {

        switch (message.getType()) {

            case MESSAGE_TYPE_AUDIO:

                showViewTypeAudio(messageViewHolder);

                if (mPlayingAudioMessageId != null && message.getMessageId().equals(mPlayingAudioMessageId) && !isPreparing) {

                    mPlayingAudioHolder = messageViewHolder;

                    updatePlayingView();

                } else updateNonPlayingView(message.getMessageId(), messageViewHolder);

                break;

            case MESSAGE_TYPE_VIDEO:

                showViewTypeSingleVideo(messageViewHolder);
                setThumbnail(messageViewHolder, message.getMessageId());

                break;

            case MESSAGE_TYPE_PICTURE:

                showViewTypeSinglePicture(messageViewHolder);

                setThumbnail(messageViewHolder, message.getMessageId());

                break;

            case MESSAGE_TYPE_MULTIPLE_MEDIA:

                showViewTypeMultipleMedia(messageViewHolder);

                loadMediaList(messageViewHolder, message);

                break;

            case MESSAGE_TYPE_TEXT:

                showViewTypeText(messageViewHolder);

                break;

            case MESSAGE_TYPE_LIKE:

                showViewTypeLike(messageViewHolder);

                break;

            case MESSAGE_TYPE_STICKER:

                showViewTypeSticker(messageViewHolder);

                setMediaUrlToView(mAdapterContext, message.getSticker(), messageViewHolder.img_sticker);

                break;

            case MESSAGE_TYPE_REMOVED:

                showViewTypeRemove(messageViewHolder);

                mMediaMap.remove(message.getMessageId());


                break;

            case MESSAGE_TYPE_WAVE_HAND:

                showViewTypeWaveHand(messageViewHolder);

                break;

            default:

                showViewTypeCall(messageViewHolder);

                processCallMessageContent(messageViewHolder, message);


        }
    }

    private void setThumbnail(final MessageViewHolder messageViewHolder, final String messageId) {

        if (mMediaMap.get(messageId) != null) {

            setMediaUrlToView(mAdapterContext, mMediaMap.get(messageId).get(0).getThumbContentUrl(), messageViewHolder.riv_single_media);

        } else {

            getMediaOfMessage(mChattingUserId, messageId, new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                @Override
                public void OnCallBack(ArrayList<Media> callbackMediaList) {

                    if (callbackMediaList.size() > 0) {
                        mMediaMap.put(messageId, callbackMediaList);
                        setMediaUrlToView(mAdapterContext, callbackMediaList.get(0).getThumbContentUrl(), messageViewHolder.riv_single_media);
                    }
                }
            });

        }

    }


    private void checkToShowDivider(MessageViewHolder messageViewHolder, int index) {

        final Message currentMessage = (Message) mList.get(index);

        if (index == 0) {

            Calendar currentCalendar = Calendar.getInstance();

            currentCalendar.setTimeInMillis(currentMessage.getSendTime());

            int sendDate = currentCalendar.get(Calendar.DATE);
            int sendMonth = currentCalendar.get(Calendar.MONTH) + 1;
            int sendYear = currentCalendar.get(Calendar.YEAR);

            messageViewHolder.tv_time_divider.setVisibility(View.VISIBLE);
            messageViewHolder.tv_time_divider.setText("NGÀY " + sendDate + " THÁNG " + sendMonth + " NĂM " + sendYear);

        } else {

            final Message preMessage = (Message) mList.get(index - 1);

            final String shouldDiv = formatTimeDivider(preMessage, currentMessage);

            messageViewHolder.tv_time_divider.setVisibility(shouldDiv != null ? View.VISIBLE : View.GONE);
            messageViewHolder.tv_time_divider.setText(shouldDiv);

        }
    }

    private void unselectView(MessageViewHolder messageViewHolder) {

        messageViewHolder.tv_message_content.setSelected(false);
        messageViewHolder.linear_call.setSelected(false);
        messageViewHolder.linear_audio.setSelected(false);
    }

    private void showViewTypeText(MessageViewHolder messageViewHolder) {

        messageViewHolder.tv_message_content.setVisibility(View.VISIBLE);

        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);

    }

    private void showViewTypeSticker(MessageViewHolder messageViewHolder) {

        messageViewHolder.img_sticker.setVisibility(View.VISIBLE);

        messageViewHolder.img_like.setVisibility(View.GONE);
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content

    }

    private void showViewTypeLike(MessageViewHolder messageViewHolder) {


        messageViewHolder.img_like.setVisibility(View.VISIBLE); // like icon
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content

    }

    private void showViewTypeWaveHand(MessageViewHolder messageViewHolder) {

        messageViewHolder.img_wavehand.setVisibility(View.VISIBLE); // like icon
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content

    }

    private void showViewTypeMultipleMedia(MessageViewHolder messageViewHolder) {


        messageViewHolder.rv_multiple_media.setVisibility(View.VISIBLE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon

    }

    private void showViewTypeSinglePicture(MessageViewHolder messageViewHolder) {


        messageViewHolder.frame_single_media.setVisibility(View.VISIBLE);
        messageViewHolder.img_play_video.setVisibility(View.GONE);
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon

    }

    private void showViewTypeSingleVideo(MessageViewHolder messageViewHolder) {

        messageViewHolder.frame_single_media.setVisibility(View.VISIBLE);
        messageViewHolder.img_play_video.setVisibility(View.VISIBLE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon

    }

    private void showViewTypeAudio(MessageViewHolder messageViewHolder) {

        messageViewHolder.linear_audio.setVisibility(View.VISIBLE);
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon

    }

    private void showViewTypeCall(MessageViewHolder messageViewHolder) {

        messageViewHolder.linear_call.setVisibility(View.VISIBLE);
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon

    }

    private void showViewTypeRemove(MessageViewHolder messageViewHolder) {

        messageViewHolder.tv_removed_message.setVisibility(View.VISIBLE);
        messageViewHolder.img_wavehand.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.linear_call.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon

    }

    private void checkToShowAvatar(MessageViewHolder messageViewHolder, int indexToCheck) {

        final Message currentMessage = (Message) mList.get(indexToCheck);

        if (indexToCheck > 0) {

            final Message preMessage = (Message) mList.get(indexToCheck - 1);
            final boolean isUserMess = !preMessage.getSenderId().equals(getMyFirebaseUserId());

            if (isUserMess) {

                final int preMessageType = preMessage.getType();
                final int currentMessageType = currentMessage.getType();

                if (preMessageType == currentMessageType
                        && (currentMessageType == MESSAGE_TYPE_TEXT || currentMessageType == MESSAGE_TYPE_LIKE)) {

                    Calendar currentCalendar = Calendar.getInstance();
                    currentCalendar.setTimeInMillis(currentMessage.getSendTime());

                    Calendar preCalendar = Calendar.getInstance();
                    preCalendar.setTimeInMillis(preMessage.getSendTime());

                    int preHour = preCalendar.get(Calendar.HOUR_OF_DAY);
                    int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);

                    if (preHour == currentHour) {

                        messageViewHolder.civ_avatar.setVisibility(View.GONE);

                    } else {

                        setAvatarToView(mAdapterContext, mUserProfile.getThumbAvatarUrl(), mUserProfile.getName(), messageViewHolder.civ_avatar);

                        messageViewHolder.civ_avatar.setVisibility(View.VISIBLE);
                    }

                } else {

                    setAvatarToView(mAdapterContext, mUserProfile.getThumbAvatarUrl(), mUserProfile.getName(), messageViewHolder.civ_avatar);

                    messageViewHolder.civ_avatar.setVisibility(View.VISIBLE);

                }

            } else {

                setAvatarToView(mAdapterContext, mUserProfile.getThumbAvatarUrl(), mUserProfile.getName(), messageViewHolder.civ_avatar);

                messageViewHolder.civ_avatar.setVisibility(View.VISIBLE);

            }

        } else {

            setAvatarToView(mAdapterContext, mUserProfile.getThumbAvatarUrl(), mUserProfile.getName(), messageViewHolder.civ_avatar);

            messageViewHolder.civ_avatar.setVisibility(View.VISIBLE);
        }

    }

    private void hideStatusView(MessageViewHolder messageViewHolder) {

        Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (messageViewHolder.tv_status.getVisibility() == View.VISIBLE)
                    messageViewHolder.tv_status.setVisibility(View.GONE);

                if (messageViewHolder.tv_send_time.getVisibility() == View.VISIBLE)
                    messageViewHolder.tv_send_time.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        messageViewHolder.tv_status.animate().setDuration(200).translationY(messageViewHolder.tv_status.getHeight()).setListener(animListener);
        messageViewHolder.tv_send_time.animate().setDuration(200).translationY(messageViewHolder.tv_send_time.getHeight()).setListener(animListener);

        unselectView(messageViewHolder);

        mCurrentStatusViewId = null;
        mCurrentStatusViewHolder = null;

    }

    private void missCallStyle(MessageViewHolder messageViewHolder) {
        messageViewHolder.tv_call_content.setTextColor(mAdapterContext.getResources().getColor(R.color.red));
        messageViewHolder.tv_call_duration.setTextColor(mAdapterContext.getResources().getColor(R.color.red));
        messageViewHolder.btn_callback.setTextColor(mAdapterContext.getResources().getColor(R.color.red));

    }

    private void successfulCallStyle(MessageViewHolder messageViewHolder) {
        messageViewHolder.tv_call_content.setTextColor(mAdapterContext.getResources().getColor(R.color.black));
        messageViewHolder.tv_call_duration.setTextColor(mAdapterContext.getResources().getColor(R.color.dark_grey));
        messageViewHolder.btn_callback.setTextColor(mAdapterContext.getResources().getColor(R.color.black));

    }

    public void loadMoreMessages(ArrayList<Message> newItems) {

        mList.addAll(0, newItems);
        notifyItemRangeInserted(0, newItems.size());
        notifyItemRangeChanged(newItems.size() - 1, newItems.size());


    }

    private void processCallMessageContent(MessageViewHolder messageViewHolder, final Message message) {

        messageViewHolder.tv_call_duration.setText(formatSecondsToHours(message.getCallDuration()) + " giây");

        messageViewHolder.tv_call_content.setText(message.getContent());

        messageViewHolder.btn_callback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (message.getType()) {

                    case MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED:
                    case MESSAGE_TYPE_VOICE_CALL_SUCCESS:

                        makeCall(mAdapterContext, mChattingUserId, false);

                        break;

                    case MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED:
                    case MESSAGE_TYPE_VIDEO_CALL_SUCCESS:

                        makeCall(mAdapterContext, mChattingUserId, true);

                        break;
                }
            }
        });

        switch (message.getType()) {

            case MESSAGE_TYPE_VOICE_CALL_SUCCESS:
            case MESSAGE_TYPE_VIDEO_CALL_SUCCESS:

                successfulCallStyle(messageViewHolder);

                break;

            case MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED:

                missCallStyle(messageViewHolder);

                break;

        }

    }

    private void loadMediaList(final MessageViewHolder messageViewHolder, final Message message) {


        if (mMediaMap.get(message.getMessageId()) != null) {

            MultipleMediaAdapter mediaAdapter = new MultipleMediaAdapter(mAdapterContext, mMediaMap.get(message.getMessageId()), new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
                @Override
                public void OnItemClick(View v, int position) {

                    viewThisSingleUserMedia(mAdapterContext,
                            mMediaMap.get(message.getMessageId()).get(position).getMediaId(),
                            mChattingUserId);

                }

                @Override
                public void OnItemLongClick(View v, int position) {

                    showMultipleMediaOptions(message, mMediaMap.get(message.getMessageId()).get(position));

                }

            });


            messageViewHolder.rv_multiple_media.setAdapter(mediaAdapter);


        } else {

            getMediaOfMessage(mChattingUserId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                @Override
                public void OnCallBack(ArrayList<Media> callbackMediaList) {

                    if (callbackMediaList.size() > 0) {

                        mMediaMap.put(message.getMessageId(), callbackMediaList);

                        MultipleMediaAdapter mediaAdapter = new MultipleMediaAdapter(mAdapterContext, mMediaMap.get(message.getMessageId()), new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
                            @Override
                            public void OnItemClick(View v, int position) {

                                viewThisSingleUserMedia(mAdapterContext,
                                        callbackMediaList.get(position).getMediaId(),
                                        mChattingUserId);

                            }

                            @Override
                            public void OnItemLongClick(View v, int position) {

                                showMultipleMediaOptions(message, callbackMediaList.get(position));

                            }

                        });

                        messageViewHolder.rv_multiple_media.setAdapter(mediaAdapter);

                    }
                }
            });


        }

    }

    private String formatMessageStatusToString(Message message) {

        switch (message.getStatus()) {

            case MESSAGE_STATUS_SENDING:

                return "Đang gửi";

            case MESSAGE_STATUS_SENT:

                return "Đã gửi";

            case MESSAGE_STATUS_RECEIVED:

                return "Đã nhận";

            case MESSAGE_STATUS_SEEN:

                Calendar message_calendar = Calendar.getInstance();

                message_calendar.setTimeInMillis(message.getSeenTime());

                int seen_date = message_calendar.get(Calendar.DATE);
                int seen_month = message_calendar.get(Calendar.MONTH) + 1;
                int seen_year = message_calendar.get(Calendar.YEAR);
                int seen_hour = message_calendar.get(Calendar.HOUR_OF_DAY);
                int seen_minute = message_calendar.get(Calendar.MINUTE);

                Calendar now_calendar = Calendar.getInstance();

                int date_now = now_calendar.get(Calendar.DATE);
                int month_now = now_calendar.get(Calendar.MONTH) + 1;
                int year_now = now_calendar.get(Calendar.YEAR);

                String date_string = formatSmallNumber(seen_date);
                String month_string = formatSmallNumber(seen_month);
                String year_string = formatSmallNumber(seen_year);
                String hour_string = formatSmallNumber(seen_hour);
                String minute_string = formatSmallNumber(seen_minute);

                if (seen_date == date_now && seen_month == month_now && seen_year == year_now) {
                    return "Đã xem " + hour_string + ":" + minute_string;

                } else if (seen_date != date_now && seen_month == month_now && seen_year == year_now) {
                    return "Đã xem " + date_string + "/" + month_string + " Lúc " + hour_string + ":" + minute_string;

                } else if (seen_month != month_now && seen_year == year_now) {

                    return "Đã xem " + date_string + "/" + month_string + " Lúc " + hour_string + ":" + minute_string;

                } else
                    return "Đã xem " + date_string + "/" + month_string + "/" + year_string;

            default:
                return "BUG STATUS TO STRING";
        }

    }

    private void formatMessageStatusToImage(MessageViewHolder messageViewHolder, int currentPosition) {

        Message message = (Message) mList.get(currentPosition);

        switch (message.getStatus()) {

            case MESSAGE_STATUS_SENDING:

                messageViewHolder.civ_seen.setVisibility(View.GONE);
                messageViewHolder.civ_seen.setImageDrawable(null);
                messageViewHolder.img_status.setVisibility(View.VISIBLE);
                messageViewHolder.img_status.setImageResource(R.drawable.ic_status_sending);

                break;
            case MESSAGE_STATUS_SENT:

                messageViewHolder.civ_seen.setVisibility(View.GONE);
                messageViewHolder.civ_seen.setImageDrawable(null);
                messageViewHolder.img_status.setVisibility(View.VISIBLE);
                messageViewHolder.img_status.setImageResource(R.drawable.ic_status_sent);

                break;

            case MESSAGE_STATUS_RECEIVED:

                messageViewHolder.civ_seen.setVisibility(View.GONE);
                messageViewHolder.civ_seen.setImageDrawable(null);
                messageViewHolder.img_status.setVisibility(View.VISIBLE);
                messageViewHolder.img_status.setImageResource(R.drawable.ic_status_received);

                break;

            case MESSAGE_STATUS_SEEN:

                messageViewHolder.img_status.setVisibility(View.GONE);
                messageViewHolder.img_status.setImageDrawable(null);

                if (currentPosition == mList.size() - 1) {

//                    Log.d("AAAA", currentPosition + "");

                    if (mSeenImageHolder != null) {

                        mSeenImageHolder.civ_seen.setVisibility(View.GONE);
                        mSeenImageHolder.civ_seen.setImageDrawable(null);

                    }

                    setAvatarToView(mAdapterContext, mUserProfile.getThumbAvatarUrl(), mUserProfile.getName(), messageViewHolder.civ_seen);

                    messageViewHolder.civ_seen.setVisibility(View.VISIBLE);

                    //hide previous seen images

                    mSeenImageHolder = messageViewHolder;

                } else {

                    messageViewHolder.civ_seen.setVisibility(View.GONE);
                    messageViewHolder.civ_seen.setImageDrawable(null);

                }

                break;

        }


    }

    private View.OnClickListener generateShowHideStatusClick(MessageViewHolder messageViewHolder, Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (messageViewHolder.tv_send_time.getVisibility() == View.VISIBLE
                        && messageViewHolder.tv_status.getVisibility() == View.VISIBLE) {

                    hideStatusView(messageViewHolder);

                } else showStatusView(message.getMessageId(), message.getType(), messageViewHolder);

            }
        };
    }

    private View.OnClickListener generateViewMediaClick(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaMap.get(message.getMessageId()) != null) {
                    viewThisSingleUserMedia(mAdapterContext,
                            mMediaMap.get(message.getMessageId()).get(0).getMediaId(),
                            mChattingUserId);
                }

            }
        };
    }

    private View.OnLongClickListener generateShowOptionClick(MessageViewHolder messageViewHolder, Message message) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                showOptions(messageViewHolder, message);

                return true;
            }
        };
    }

    private void onClickMessage(final MessageViewHolder messageViewHolder, int position) {

        final Message message = mList.get(position);

        switch (message.getType()) {

            case MESSAGE_TYPE_AUDIO:

                messageViewHolder.linear_audio.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));
                messageViewHolder.linear_audio.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));


                messageViewHolder.linear_call.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_wavehand.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);


                messageViewHolder.linear_call.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_wavehand.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);


                break;

            case MESSAGE_TYPE_VIDEO:

                View.OnClickListener viewMediaClick = generateViewMediaClick(message);
                View.OnLongClickListener showOptionClick = generateShowOptionClick(messageViewHolder, message);

                messageViewHolder.img_play_video.setOnClickListener(viewMediaClick);
                messageViewHolder.img_play_video.setOnLongClickListener(showOptionClick);

                messageViewHolder.riv_single_media.setOnClickListener(viewMediaClick);
                messageViewHolder.riv_single_media.setOnLongClickListener(showOptionClick);

                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.linear_call.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_wavehand.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.linear_call.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_wavehand.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);

                break;

            case MESSAGE_TYPE_PICTURE:

                messageViewHolder.riv_single_media.setOnClickListener(generateViewMediaClick(message));
                messageViewHolder.riv_single_media.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.linear_call.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_wavehand.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.linear_call.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_wavehand.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);

                break;

            case MESSAGE_TYPE_MULTIPLE_MEDIA:

                break;

            case MESSAGE_TYPE_TEXT:

                messageViewHolder.tv_message_content.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.tv_message_content.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                clickLinkInContent(message.getMessageId(), messageViewHolder.tv_message_content);

                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.linear_call.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_wavehand.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.linear_call.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_wavehand.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);

                break;

            case MESSAGE_TYPE_LIKE:
                messageViewHolder.img_like.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.img_like.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.linear_call.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_wavehand.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.linear_call.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_wavehand.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);

                break;

            case MESSAGE_TYPE_STICKER:

                messageViewHolder.img_sticker.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.img_sticker.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.linear_call.setOnClickListener(null);
                messageViewHolder.img_wavehand.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.linear_call.setOnLongClickListener(null);
                messageViewHolder.img_wavehand.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);
                break;

            case MESSAGE_TYPE_REMOVED:

                messageViewHolder.tv_removed_message.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.tv_removed_message.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.linear_call.setOnClickListener(null);
                messageViewHolder.img_wavehand.setOnClickListener(null);

                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.linear_call.setOnLongClickListener(null);
                messageViewHolder.img_wavehand.setOnLongClickListener(null);

                break;

            case MESSAGE_TYPE_WAVE_HAND:

                messageViewHolder.img_wavehand.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.img_wavehand.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.tv_removed_message.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.linear_call.setOnClickListener(null);

                messageViewHolder.tv_removed_message.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.linear_call.setOnLongClickListener(null);


                break;

            default:

                messageViewHolder.linear_call.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.linear_call.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.img_wavehand.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);

                messageViewHolder.img_wavehand.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);


        }

        if (!message.getSenderId().equals(getMyFirebaseUserId())) {
            messageViewHolder.civ_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewUserProfile(mAdapterContext, message.getSenderId());
                }
            });
        }

    }

    private void showStatusView(String messageId, int messageType, MessageViewHolder messageViewHolder) {


        Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (messageViewHolder.tv_status.getVisibility() != View.VISIBLE)
                    messageViewHolder.tv_status.setVisibility(View.VISIBLE);


                if (messageViewHolder.tv_send_time.getVisibility() != View.VISIBLE)
                    messageViewHolder.tv_send_time.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        messageViewHolder.tv_status.animate().setDuration(200).translationY(0).setListener(animListener);
        messageViewHolder.tv_send_time.animate().setDuration(200).translationY(0).setListener(animListener);

        switch (messageType) {

            case MESSAGE_TYPE_AUDIO:

                messageViewHolder.linear_audio.setSelected(true);

                break;

            case MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VIDEO_CALL_SUCCESS:
            case MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VOICE_CALL_SUCCESS:

                messageViewHolder.linear_call.setSelected(true);

            case MESSAGE_TYPE_TEXT:
                messageViewHolder.tv_message_content.setSelected(true);
                break;
        }

        if (mCurrentStatusViewId != null && mCurrentStatusViewHolder != null) {

            hideStatusView(mCurrentStatusViewHolder);

        }

        mCurrentStatusViewId = messageId;
        mCurrentStatusViewHolder = messageViewHolder;
    }

    private View.OnClickListener clickForward(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMediaMessage(message.getType())) {

                    getMediaOfMessage(mChattingUserId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                        @Override
                        public void OnCallBack(ArrayList<Media> callbackMediaList) {

                            if (callbackMediaList.size() > 0) {
                                forwardSingleMedia(mAdapterContext, callbackMediaList.get(0).getMediaId(), mChattingUserId);
                            }
                        }
                    });


                } else forwardSingleMessage(mAdapterContext, message.getMessageId(), mChattingUserId);

                mOptionsDialog.dismiss();

            }
        };
    }

    private View.OnClickListener clickDelete(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteMessage(message.getMessageId());

                mOptionsDialog.dismiss();
            }
        };
    }

    private View.OnClickListener clickTakeBack(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeBackMessage(message);

                mOptionsDialog.dismiss();
            }
        };
    }

    private View.OnClickListener clickCopy(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyContent(mAdapterContext, message.getContent());

                mOptionsDialog.dismiss();
            }
        };
    }

    private View.OnClickListener clickDownload(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaMap.get(message.getMessageId()) != null && mMediaMap.get(message.getMessageId()).size() > 0) {

                    mAdapterContext.downloadMediaFromActivity(mMediaMap.get(message.getMessageId()).get(0));

                } else {

                    getMediaOfMessage(mChattingUserId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                        @Override
                        public void OnCallBack(ArrayList<Media> callbackMediaList) {

                            if (callbackMediaList.size() > 0) {
                                mMediaMap.put(message.getMessageId(), callbackMediaList);
                                mAdapterContext.downloadMediaFromActivity(callbackMediaList.get(0));
                            }

                        }
                    });

                }

                mOptionsDialog.dismiss();

            }
        };
    }

    private View.OnClickListener clickEditMessage(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showEditMessageDialog(message);

                mOptionsDialog.dismiss();

            }
        };
    }

    private View.OnClickListener clickEditHistory(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showEditHistoryDialog(message);

                mOptionsDialog.dismiss();

            }
        };
    }

    private void showOptions(MessageViewHolder messageViewHolder, final Message message) {

        LinearLayout btn_forward = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_forward);
        LinearLayout btn_copy = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_copy);
        LinearLayout btn_download = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_download);
        LinearLayout btn_delete = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_delete);
        LinearLayout btn_takeback = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_take_back);
        LinearLayout btn_edit_message = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_message);
        LinearLayout btn_edit_history = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_history);

        final boolean ableToTakeBack = getCurrentTimeInMilies() - message.getSendTime() <= TAKING_BACK_MESSAGE_TIME_LIMIT;

        mSelectedBackgroundHolder = null;

        btn_delete.setVisibility(View.VISIBLE);
        btn_delete.setOnClickListener(clickDelete(message));

        switch (message.getType()) {

            case MESSAGE_TYPE_LIKE:
            case MESSAGE_TYPE_STICKER:

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeBack) {
                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));
                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }

                btn_copy.setVisibility(View.GONE);
                btn_copy.setOnClickListener(null);

                btn_edit_history.setVisibility(View.GONE);
                btn_edit_history.setOnClickListener(null);

                btn_edit_message.setVisibility(View.GONE);
                btn_edit_message.setOnClickListener(null);

                btn_download.setVisibility(View.GONE);
                btn_download.setOnClickListener(null);

                btn_forward.setVisibility(View.GONE);
                btn_forward.setOnClickListener(null);

                break;

            case MESSAGE_TYPE_WAVE_HAND:
            case MESSAGE_TYPE_REMOVED:

                btn_forward.setVisibility(View.GONE);
                btn_forward.setOnClickListener(null);

                btn_copy.setVisibility(View.GONE);
                btn_copy.setOnClickListener(null);

                btn_edit_history.setVisibility(View.GONE);
                btn_edit_history.setOnClickListener(null);

                btn_edit_message.setVisibility(View.GONE);
                btn_edit_message.setOnClickListener(null);

                btn_download.setVisibility(View.GONE);
                btn_download.setOnClickListener(null);

                btn_takeback.setVisibility(View.GONE);
                btn_takeback.setOnClickListener(null);

                break;

            case MESSAGE_TYPE_TEXT:

                mSelectedBackgroundHolder = messageViewHolder;
                mSelectedBackgroundHolder.tv_message_content.setSelected(true);

                btn_forward.setVisibility(View.VISIBLE);
                btn_forward.setOnClickListener(clickForward(message));

                btn_copy.setVisibility(View.VISIBLE);
                btn_copy.setOnClickListener(clickCopy(message));

                if (message.getEditHistory() != null && !message.isForwardedMessage()) {
                    btn_edit_history.setVisibility(View.VISIBLE);
                    btn_edit_history.setOnClickListener(clickEditHistory(message));
                } else {
                    btn_edit_history.setVisibility(View.GONE);
                    btn_edit_history.setOnClickListener(null);
                }

                if (message.getSenderId().equals(getMyFirebaseUserId()) && !message.isForwardedMessage()) {
                    btn_edit_message.setVisibility(View.VISIBLE);
                    btn_edit_message.setOnClickListener(clickEditMessage(message));
                } else {
                    btn_edit_message.setVisibility(View.GONE);
                    btn_edit_message.setOnClickListener(null);
                }

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeBack) {
                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));
                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }

                btn_download.setVisibility(View.GONE);
                btn_download.setOnClickListener(null);

                break;

            case MESSAGE_TYPE_PICTURE:
            case MESSAGE_TYPE_VIDEO:

                btn_download.setVisibility(View.VISIBLE);
                btn_download.setOnClickListener(clickDownload(message));

                btn_forward.setVisibility(View.VISIBLE);
                btn_forward.setOnClickListener(clickForward(message));

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeBack) {
                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));
                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }


                btn_copy.setVisibility(View.GONE);
                btn_copy.setOnClickListener(null);

                btn_edit_history.setVisibility(View.GONE);
                btn_edit_history.setOnClickListener(null);

                btn_edit_message.setVisibility(View.GONE);
                btn_edit_message.setOnClickListener(null);

                break;

            case MESSAGE_TYPE_AUDIO:

                mSelectedBackgroundHolder = messageViewHolder;
                mSelectedBackgroundHolder.linear_audio.setSelected(true);

                btn_forward.setVisibility(View.VISIBLE);
                btn_forward.setOnClickListener(clickForward(message));

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeBack) {
                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));
                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }

                btn_copy.setVisibility(View.GONE);
                btn_copy.setOnClickListener(null);

                btn_edit_history.setVisibility(View.GONE);
                btn_edit_history.setOnClickListener(null);

                btn_edit_message.setVisibility(View.GONE);
                btn_edit_message.setOnClickListener(null);

                btn_download.setVisibility(View.GONE);
                btn_download.setOnClickListener(null);

                break;

            case MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VOICE_CALL_SUCCESS:
            case MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED:
            case MESSAGE_TYPE_VIDEO_CALL_SUCCESS:

                mSelectedBackgroundHolder = messageViewHolder;
                mSelectedBackgroundHolder.linear_call.setSelected(true);

                btn_forward.setVisibility(View.GONE);
                btn_forward.setOnClickListener(null);

                btn_copy.setVisibility(View.GONE);
                btn_copy.setOnClickListener(null);

                btn_edit_history.setVisibility(View.GONE);
                btn_edit_history.setOnClickListener(null);

                btn_edit_message.setVisibility(View.GONE);
                btn_edit_message.setOnClickListener(null);

                btn_download.setVisibility(View.GONE);
                btn_download.setOnClickListener(null);

                btn_takeback.setVisibility(View.GONE);
                btn_takeback.setOnClickListener(null);

                break;

        }

        mShowingOptionMessageId = message.getMessageId();

        mOptionsDialog.show();

    }

    private void showMultipleMediaOptions(final Message message, final Media mediaToShow) {

        LinearLayout btn_forward = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_forward);
        LinearLayout btn_delete = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_delete);
        LinearLayout btn_download = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_download);

        LinearLayout btn_takeback = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_take_back);

        LinearLayout btn_copy = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_copy);
        LinearLayout btn_edit_message = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_message);
        LinearLayout btn_edit_history = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_history);

        btn_edit_message.setVisibility(View.GONE);
        btn_edit_message.setOnClickListener(null);

        btn_edit_history.setVisibility(View.GONE);
        btn_edit_history.setOnClickListener(null);

        btn_copy.setVisibility(View.GONE);
        btn_copy.setOnClickListener(null);

        btn_forward.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.VISIBLE);
        btn_download.setVisibility(View.VISIBLE);

        final boolean ableToTakeBack = getCurrentTimeInMilies() - message.getSendTime() <= TAKING_BACK_MESSAGE_TIME_LIMIT;

        if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeBack) {
            btn_takeback.setVisibility(View.VISIBLE);
            btn_takeback.setOnClickListener(clickTakeBack(message));
        } else {
            btn_takeback.setVisibility(View.GONE);
            btn_takeback.setOnClickListener(null);
        }


        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forwardSingleMedia(mAdapterContext, mediaToShow.getMediaId(), mChattingUserId);

                mOptionsDialog.dismiss();

            }
        });

        btn_delete.setOnClickListener(clickDelete(message));

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapterContext.downloadMediaFromActivity(mediaToShow);

                mOptionsDialog.dismiss();

            }
        });


        mSelectedBackgroundHolder = null;

        mShowingOptionMessageId = message.getMessageId();

        mOptionsDialog.show();

    }

    private void showEditHistoryDialog(Message message) {

        final View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.dialog_edit_history, null);

        final ArrayList<EditHistory> arrayList = new ArrayList<>();

        arrayList.addAll(message.getEditHistory().values());

        Collections.sort(arrayList, Collections.reverseOrder(new Comparator<EditHistory>() {
            @Override
            public int compare(EditHistory o1, EditHistory o2) {
                return Long.compare(o1.getEditTime(), o2.getEditTime());
            }
        }));


        final EditHistoryAdapter adapter = new EditHistoryAdapter(mAdapterContext, arrayList);

        final RecyclerView recyclerView = view.findViewById(R.id.rv_edit_history);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(mAdapterContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        mEditHistoryDialog = new AlertDialog.Builder(mAdapterContext)
                .setTitle("Lịch sử chỉnh sửa")
                .setView(view)
                .setPositiveButton("Đóng", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mShowingEditHistoryMessageId = null;
                    }
                }).create();

        mShowingEditHistoryMessageId = message.getMessageId();

        mEditHistoryDialog.show();

    }

    private void showEditMessageDialog(Message currentMessage) {

        final View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.dialog_edit_message, null);

        final EmojiAppCompatEditText emojiAppCompatEditText = view.findViewById(R.id.edt_edit_message);

        emojiAppCompatEditText.setText(currentMessage.getContent());

        new AlertDialog.Builder(mAdapterContext)
                .setTitle("Chỉnh sửa tin nhắn")
                .setView(view)
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isConnectedToFirebaseService(mAdapterContext)) {

                            if (!emojiAppCompatEditText.getText().toString().trim().isEmpty()
                                    && !emojiAppCompatEditText.getText().toString().trim().equals(currentMessage.getContent())) {

                                final Map<String, Object> editMap = new HashMap<>();

                                final String key = ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mChattingUserId)
                                        .child(currentMessage.getMessageId()).child(kEditHistory).push().getKey();

                                final String previousContent = currentMessage.getContent();
                                final String updatedContent = emojiAppCompatEditText.getText().toString().trim();

                                final EditHistory editHistory = new EditHistory(previousContent, getCurrentTimeInMilies());

                                editMap.put(kMessageContent, updatedContent);
                                editMap.put("/" + kEditHistory + "/" + key, editHistory);

                                DatabaseReference myMessageRef = ROOT_REF.child(CHILD_MESSAGES)
                                        .child(getMyFirebaseUserId()).child(mChattingUserId)
                                        .child(currentMessage.getMessageId());

                                myMessageRef.updateChildren(editMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        DatabaseReference chattingUserMessageRef = ROOT_REF.child(CHILD_MESSAGES)
                                                .child(mChattingUserId).child(getMyFirebaseUserId())
                                                .child(currentMessage.getMessageId());

                                        chattingUserMessageRef.runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                                final Message message = dataSnapshot.getValue(Message.class);

                                                if (message != null) {
                                                    chattingUserMessageRef.updateChildren(editMap);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        } else showNoConnectionDialog(mAdapterContext);

                    }
                }).setNegativeButton("Huỷ", null).create().show();


    }

    private void takeBackMessage(final Message message) {

        if (isConnectedToFirebaseService(mAdapterContext)) {

            if (getCurrentTimeInMilies() - message.getSendTime() <= TAKING_BACK_MESSAGE_TIME_LIMIT) {

                final Map<String, Object> removeMessageMap = new HashMap<>();

                removeMessageMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + mChattingUserId + "/" + message.getMessageId() + "/" + kMessageContent, "Đã thu hồi tin nhắn");
                removeMessageMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + mChattingUserId + "/" + message.getMessageId() + "/" + kMessageType, MESSAGE_TYPE_REMOVED);
                removeMessageMap.put("/" + CHILD_MESSAGES + "/" + mChattingUserId + "/" + getMyFirebaseUserId() + "/" + message.getMessageId(), null);

                ROOT_REF.updateChildren(removeMessageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        sendNotificationToUser(
                                mChattingUserId,
                                getMyFirebaseUserId(),
                                null,
                                message.getMessageId(),
                                null,
                                null,
                                null,
                                NOTIFICATION_TYPE_REMOVE_MESSAGE,
                                message.getNotificationId());

                    }
                });


                getMediaOfMessage(mChattingUserId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                    @Override
                    public void OnCallBack(ArrayList<Media> callbackMediaList) {

                        final Map<String, Object> removeMediaMap = new HashMap<>();

                        for (Media media : callbackMediaList) {
                            removeMediaMap.put("/" + CHILD_MEDIA + "/" + getMyFirebaseUserId() + "/" + mChattingUserId + "/" + media.getMediaId(), null);
                            removeMediaMap.put("/" + CHILD_MEDIA + "/" + mChattingUserId + "/" + getMyFirebaseUserId() + "/" + media.getMediaId(), null);
                        }

                        ROOT_REF.updateChildren(removeMediaMap);
                    }
                });


            } else showLongToast(mAdapterContext, "Đã quá thời gian thu hồi !");


        } else showNoConnectionDialog(mAdapterContext);


    }

    private void deleteMessage(String messageId) {

        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mChattingUserId)
                .child(messageId).removeValue();

    }

    public void stopActionWithRemovedMessage(Message message) {

        getNotificationManager(mAdapterContext).cancel(message.getNotificationId());

        if (mPlayingAudioMessageId != null && message.getMessageId().equals(mPlayingAudioMessageId)) releaseMediaPlayer();

        if (mShowingPhoneNumberOptionMessageId != null && message.getMessageId().equals(mShowingPhoneNumberOptionMessageId)) {

            if (mPhoneNumberOptionDialog != null && mPhoneNumberOptionDialog.isShowing()) {
                // already reset value when dialog dissmiss
                mPhoneNumberOptionDialog.dismiss();
            }

        }

        if (mShowingOptionMessageId != null && mShowingOptionMessageId.equals(message.getMessageId())) {
            if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
                // already reset value when dialog dissmiss
                mOptionsDialog.dismiss();
            }
        }

        if (mShowingEditHistoryMessageId != null && mShowingEditHistoryMessageId.equals(message.getMessageId())) {
            if (mEditHistoryDialog != null && mEditHistoryDialog.isShowing()) {
                // already reset value when dialog dissmiss
                mEditHistoryDialog.dismiss();
            }
        }

        if (mCurrentStatusViewId != null
                && mCurrentStatusViewHolder != null
                && mCurrentStatusViewId.equals(message.getMessageId())) {

            hideStatusView(mCurrentStatusViewHolder);

        }


    }

    private void updateNonPlayingView(String messageId, MessageViewHolder messageViewHolder) {

        messageViewHolder.pb_audio_player.removeCallbacks(seekbarUpdater);
        messageViewHolder.pb_audio_player.setProgress(0);

        messageViewHolder.img_play_audio.setImageResource(R.drawable.ic_play_audio_filled_color);
        messageViewHolder.img_play_audio.setVisibility(View.VISIBLE);

        messageViewHolder.pb_preparing_audio.setVisibility(View.GONE);

        if (mMediaMap.get(messageId) != null) {

            messageViewHolder.tv_audio_duration.setText(formatSecondsToHours(mMediaMap.get(messageId).get(0).getDuration()));

        } else {

            getMediaOfMessage(mChattingUserId, messageId, new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                @Override
                public void OnCallBack(ArrayList<Media> callbackMediaList) {

                    if (callbackMediaList.size() > 0) {
                        mMediaMap.put(messageId, callbackMediaList);
                        messageViewHolder.tv_audio_duration.setText(formatSecondsToHours(callbackMediaList.get(0).getDuration()));
                    }
                }
            });

        }
    }

    private void updatePlayingView() {

        int dur = mAudioPlayer.getDuration() / 1000;
        int current = mAudioPlayer.getCurrentPosition() / 1000;

        mPlayingAudioHolder.pb_audio_player.setMax(dur);
        mPlayingAudioHolder.pb_audio_player.setProgress(current);

        mPlayingAudioHolder.tv_audio_duration.setText(formatSecondsToHours(dur - current));

        if (mAudioPlayer != null && mAudioPlayer.isPlaying()) {
            mPlayingAudioHolder.pb_audio_player.postDelayed(seekbarUpdater, 500);
            mPlayingAudioHolder.img_play_audio.setImageResource(R.drawable.ic_pause_audio_filled_color);
        } else {
            mPlayingAudioHolder.pb_audio_player.removeCallbacks(seekbarUpdater);
            mPlayingAudioHolder.img_play_audio.setImageResource(R.drawable.ic_play_audio_filled_color);
        }
    }


    private void startMediaPlayer(String audioResId) {

        try {
            mAudioPlayer = new MediaPlayer();
            mAudioPlayer.setDataSource(audioResId);
            mAudioPlayer.prepareAsync();
            mAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mAudioPlayer.start();

                    mPlayingAudioHolder.img_play_audio.setVisibility(View.VISIBLE);
                    mPlayingAudioHolder.pb_preparing_audio.setVisibility(View.GONE);

                    updatePlayingView();

                    isPreparing = false;

                }
            });

            mAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) { releaseMediaPlayer(); }
            });

        } catch (IOException e) { releaseMediaPlayer(); }

    }

    public void releaseMediaPlayer() {

        if (mPlayingAudioHolder != null) {

            mPlayingAudioHolder.pb_audio_player.removeCallbacks(seekbarUpdater);
            mPlayingAudioHolder.pb_audio_player.setProgress(0);

            mPlayingAudioHolder.img_play_audio.setImageResource(R.drawable.ic_play_audio_filled_color);
            mPlayingAudioHolder.img_play_audio.setVisibility(View.VISIBLE);

            mPlayingAudioHolder.pb_preparing_audio.setVisibility(View.GONE);

            if (mMediaMap.get(mPlayingAudioMessageId) != null)
                mPlayingAudioHolder.tv_audio_duration.setText(formatSecondsToHours(mMediaMap.get(mPlayingAudioMessageId).get(0).getDuration()));

            mPlayingAudioHolder = null;

        }

        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
            mAudioPlayer.release();
            mAudioPlayer = null;
        }

        if (mPlayingAudioMessageId != null) mPlayingAudioMessageId = null;

        isPreparing = false;

    }

    private void clickLinkInContent(final String messageId, EmojiAppCompatTextView emojiAppCompatTextView) {
        BetterLinkMovementMethod.linkify(Linkify.ALL, emojiAppCompatTextView)
                .setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
                    @Override
                    public boolean onClick(TextView textView, final String url) {

                        if (isPhoneNumber(url)) {


                            final ArrayList<String> options = new ArrayList<>();

                            options.add("Gọi điện");
                            options.add("Nhắn tin SMS");
                            options.add("Lưu vào danh bạ");
                            options.add("Sao chép số");
                            options.add("Hủy");

                            ArrayAdapter<String> optionAdapter = new ArrayAdapter(mAdapterContext, android.R.layout.simple_list_item_1, options);

                            mPhoneNumberOptionDialog = new AlertDialog.Builder(mAdapterContext)
                                    .setTitle(url.trim().replaceAll("tel:", ""))
                                    .setAdapter(optionAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {

                                                case 0:
                                                    Intent intentcall = new Intent(Intent.ACTION_DIAL);
                                                    intentcall.setData(Uri.parse(url));
                                                    mAdapterContext.startActivity(intentcall);
                                                    break;

                                                case 1:
                                                    Uri uri = Uri.parse("smsto:" + url.trim().replaceAll("tel:", ""));
                                                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                                                    it.putExtra("sms_body", "");
                                                    mAdapterContext.startActivity(it);
                                                    break;

                                                case 2:
                                                    Intent intentsave = new Intent(ContactsContract.Intents.Insert.ACTION);
                                                    intentsave.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                                    intentsave.putExtra(ContactsContract.Intents.Insert.PHONE,
                                                            url.trim().replaceAll("tel:", ""));
                                                    mAdapterContext.startActivity(intentsave);
                                                    break;

                                                case 3:
                                                    copyContent(mAdapterContext, url.trim().replaceAll("tel:", ""));
                                                    break;

                                                default:

                                            }

                                        }
                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            mShowingPhoneNumberOptionMessageId = null;
                                        }
                                    }).create();

                            mShowingPhoneNumberOptionMessageId = messageId;

                            mPhoneNumberOptionDialog.show();

                        } else if (isEmailValid(url))

                            sendEmail(mAdapterContext, url);

                        else if (isMapAddress(url))

                            openMap(mAdapterContext, url);

                        else openWebBrowser(mAdapterContext, url);

                        return true;
                    }
                });
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        final EmojiAppCompatTextView tv_message_content;

        final LinearLayout linear_call;
        final LinearLayout linear_audio;
        final LinearLayout linear_forwarded_message;

        final CircleImageView civ_avatar;
        final ImageView img_status;
        final CircleImageView civ_seen;

        final ImageView img_like;
        final ImageView img_sticker;
        final ImageView img_play_video;
        final ImageView img_play_audio;


        final TextView tv_removed_message;
        final TextView tv_call_content;
        final TextView tv_call_duration;
        final TextView tv_time_divider;
        final TextView tv_audio_duration;
        final TextView tv_send_time;
        final TextView tv_status;

        final Button btn_callback;
        final RoundedImageView riv_single_media;
        final FrameLayout frame_single_media;
        final FrameLayout frame_play_audio;
        final ProgressBar pb_audio_player;
        final ProgressBar pb_preparing_audio;
        final RecyclerView rv_multiple_media;
        final ImageView img_wavehand;
        final ImageView img_edited;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_seen = (CircleImageView) itemView.findViewById(R.id.civ_seen);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);
            civ_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);

            img_play_video = (ImageView) itemView.findViewById(R.id.img_play_video);
            img_play_audio = (ImageView) itemView.findViewById(R.id.img_play_audio);
            img_like = (ImageView) itemView.findViewById(R.id.img_like);
            img_sticker = (ImageView) itemView.findViewById(R.id.img_sticker);
            img_wavehand = (ImageView) itemView.findViewById(R.id.img_wavehand);
            img_edited = (ImageView) itemView.findViewById(R.id.img_edited);

            riv_single_media = (RoundedImageView) itemView.findViewById(R.id.riv_single_picture);

            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
            tv_time_divider = (TextView) itemView.findViewById(R.id.tv_time_divider);
            tv_message_content = (EmojiAppCompatTextView) itemView.findViewById(R.id.tv_message_content);
            tv_status = (TextView) itemView.findViewById(R.id.tv_message_status);
            tv_call_content = (TextView) itemView.findViewById(R.id.tv_call_content);
            tv_call_duration = (TextView) itemView.findViewById(R.id.tv_call_duration);
            tv_removed_message = (TextView) itemView.findViewById(R.id.tv_removed_message);
            tv_audio_duration = (TextView) itemView.findViewById(R.id.tv_duration);

            btn_callback = (Button) itemView.findViewById(R.id.btn_callback);

            rv_multiple_media = (RecyclerView) itemView.findViewById(R.id.rv_multiple_media);
            rv_multiple_media.setLayoutManager(new GridLayoutManager(mAdapterContext, 2, GridLayoutManager.VERTICAL, false));
            rv_multiple_media.setItemAnimator(null);

            linear_call = (LinearLayout) itemView.findViewById(R.id.linear_call);
            linear_audio = (LinearLayout) itemView.findViewById(R.id.linear_audio);
            linear_forwarded_message = (LinearLayout) itemView.findViewById(R.id.linear_forwarded_message);

            pb_audio_player = (ProgressBar) itemView.findViewById(R.id.pb_audio_player);
            pb_preparing_audio = (ProgressBar) itemView.findViewById(R.id.pb_preparing_audio);

            frame_single_media = (FrameLayout) itemView.findViewById(R.id.frame_single_media);
            frame_play_audio = (FrameLayout) itemView.findViewById(R.id.frame_play_audio);

            frame_play_audio.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (!isPreparing) {

                final Message audioMessage = mList.get(getAdapterPosition());

                if (audioMessage.getMessageId().equals(mPlayingAudioMessageId)) {

                    if (mAudioPlayer.isPlaying()) mAudioPlayer.pause();
                    else mAudioPlayer.start();

                    updatePlayingView();

                } else {

                    isPreparing = true;
                    // start another audio playback

                    if (mPlayingAudioHolder != null) {

                        mPlayingAudioHolder.pb_audio_player.removeCallbacks(seekbarUpdater);
                        mPlayingAudioHolder.pb_audio_player.setProgress(0);

                        mPlayingAudioHolder.img_play_audio.setImageResource(R.drawable.ic_play_audio_filled_color);
                        mPlayingAudioHolder.img_play_audio.setVisibility(View.VISIBLE);

                        mPlayingAudioHolder.pb_preparing_audio.setVisibility(View.GONE);

                        if (mMediaMap.get(mPlayingAudioMessageId) != null)
                            mPlayingAudioHolder.tv_audio_duration.setText(formatSecondsToHours(mMediaMap.get(mPlayingAudioMessageId).get(0).getDuration()));

                        mPlayingAudioHolder = null;

                    }

                    if (mAudioPlayer != null) {
                        mAudioPlayer.stop();
                        mAudioPlayer.release();
                        mAudioPlayer = null;
                    }

                    mPlayingAudioHolder = this;

                    mPlayingAudioMessageId = audioMessage.getMessageId();

                    mPlayingAudioHolder.img_play_audio.setVisibility(View.GONE);

                    mPlayingAudioHolder.pb_preparing_audio.setVisibility(View.VISIBLE);

                    Media media = mMediaMap.get(mPlayingAudioMessageId).get(0);

                    startMediaPlayer(media.getContentUrl());

                }

            }

        }

    }

    private class SeekBarUpdater implements Runnable {
        @Override
        public void run() {
            if (mPlayingAudioHolder != null && mAudioPlayer.isPlaying()) {

                int dur = mAudioPlayer.getDuration() / 1000;
                int current = mAudioPlayer.getCurrentPosition() / 1000;
                mPlayingAudioHolder.tv_audio_duration.setText(formatSecondsToHours(dur - current));

                mPlayingAudioHolder.pb_audio_player.setProgress(current);
                mPlayingAudioHolder.pb_audio_player.postDelayed(this, 500);

            }
        }
    }


}
