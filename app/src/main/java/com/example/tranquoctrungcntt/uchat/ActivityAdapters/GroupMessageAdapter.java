package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
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

import com.example.tranquoctrungcntt.uchat.Activities.GroupChat;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.DialogAdapters.EditHistoryAdapter;
import com.example.tranquoctrungcntt.uchat.DialogAdapters.MessageViewerAdapter;
import com.example.tranquoctrungcntt.uchat.DialogAdapters.RelatedUserAdapter;
import com.example.tranquoctrungcntt.uchat.Models.MessageViewerModel;
import com.example.tranquoctrungcntt.uchat.Objects.EditHistory;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.MessageViewer;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_CHANGE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_LEAVE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_LIKE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_MULTIPLE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_REMOVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_STICKER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_TEXT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_REMOVE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TAKING_BACK_MESSAGE_TIME_LIMIT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kEditHistory;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageContent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageType;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isEmailValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isGroupInstantMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMapAddress;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMediaMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isPhoneNumber;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isSeen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getGroupMemberSnapshot;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMediaOfMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.deleteMember;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GroupActionUtils.showChangeAdminConfirmDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.RelationshipUtils.checkBeforeBLockUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.copyContent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSecondsToHours;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSendTime;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeDivider;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardGroupMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardGroupMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.makeCall;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.openMap;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.openWebBrowser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.sendEmail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewThisGroupMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setMediaUrlToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;
import static com.example.tranquoctrungcntt.uchat.Notification.NotificationUtils.sendNotificationToUser;


public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {

    private static final int TYPE_GROUP = 1;
    private static final int TYPE_ME = 0;
    private static final int WHO_SEEN_LIMIT = 5;

    private final GroupChat mAdapterContext;
    private final List<Message> mList;
    private final String mGroupId;
    private final SeekBarUpdater seekbarUpdater;
    private final Map<String, User> mUserProfileMap;
    private final Map<String, ArrayList<Media>> mMediaMap;

    private final BottomSheetDialog mOptionsDialog;

    private GroupMessageViewHolder mSeenImageHolder;
    private GroupMessageViewHolder mSelectedBackgroundHolder;
    private GroupMessageViewHolder mCurrentStatusViewHolder;

    private String mCurrentStatusViewId;
    private String mShowingOptionMessageId;
    private String mShowingPhoneNumberOptionMessageId;
    private String mShowingEditHistoryMessageId;

    private AlertDialog mPhoneNumberOptionDialog;
    private AlertDialog mEditHistoryDialog;

    private boolean isDelaying;
    private boolean isPreparing;

    private MediaPlayer mAudioPlayer;
    private GroupMessageViewHolder mPlayingAudioHolder;
    private String mPlayingAudioMessageId;

    public GroupMessageAdapter(GroupChat mAdapterContext, List<Message> mList, Map<String, User> mUserProfileMap, String mGroupId) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mGroupId = mGroupId;
        this.mUserProfileMap = mUserProfileMap;

        mMediaMap = new HashMap<>();

        mCurrentStatusViewId = null;
        mCurrentStatusViewHolder = null;

        mPlayingAudioMessageId = null;
        mPlayingAudioHolder = null;

        mSeenImageHolder = null;
        mSelectedBackgroundHolder = null;

        mShowingOptionMessageId = null;

        mShowingPhoneNumberOptionMessageId = null;
        mPhoneNumberOptionDialog = null;

        mEditHistoryDialog = null;
        mShowingEditHistoryMessageId = null;

        seekbarUpdater = new SeekBarUpdater();

        isPreparing = false;
        isDelaying = false;

        mOptionsDialog = new BottomSheetDialog(mAdapterContext);
        mOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mOptionsDialog.setContentView(R.layout.dialog_group_message_options);
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

    private void unselectView(GroupMessageViewHolder messageViewHolder) {
        messageViewHolder.tv_message_content.setSelected(false);
        messageViewHolder.linear_audio.setSelected(false);
    }

    @Override
    public int getItemViewType(int position) {

        if (mList.get(position).getSenderId().equals(getMyFirebaseUserId()))

            return TYPE_ME;

        return TYPE_GROUP;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType == TYPE_ME ? R.layout.row_group_message_right : R.layout.row_group_message_left, viewGroup, false);

        return new GroupMessageViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final GroupMessageViewHolder mViewHolder, final int mIndex) {

        final int viewType = getItemViewType(mIndex);

        final Message message = mList.get(mIndex);

        if (isGroupInstantMessage(message)) {

            mViewHolder.linear_message_root.setVisibility(View.GONE);

            mViewHolder.tv_instant_message.setVisibility(View.VISIBLE);

            mViewHolder.tv_status.setText(null);

            mViewHolder.tv_instant_message_status.setText(isSeen(message.getStatus()) ? "Đã xem" : "Đã chuyển");

            if (message.getType() == MESSAGE_TYPE_CHANGE_ADMIN) {

                processChangeAdminContent(mViewHolder.tv_instant_message, message);

            } else processUnChangeContent(mViewHolder.tv_instant_message, message);

            if (viewType == TYPE_GROUP) {

                mViewHolder.civ_avatar.setVisibility(View.GONE);
                mViewHolder.tv_name.setVisibility(View.GONE);

            } else hideStatusImage(mViewHolder);

            if (isSeen(message.getStatus())) {

                if (message.getMessageViewer() != null) {

                    if (mIndex == mList.size() - 1) {

                        showShortMessageViewerList(mViewHolder, message.getMessageViewer());

                    } else hideShortMessageViewerList(mViewHolder);

                } else hideShortMessageViewerList(mViewHolder);

            } else hideShortMessageViewerList(mViewHolder);

            onClickInstantMessage(mViewHolder, mIndex);

        } else {

            mViewHolder.linear_message_root.setVisibility(View.VISIBLE);

            mViewHolder.tv_instant_message.setVisibility(View.GONE);

            mViewHolder.tv_message_content.setText(message.getContent() + "");

            mViewHolder.tv_instant_message_status.setText(null);

            showViewForNormalMessageType(message, mViewHolder);

            formatMessageStatusGroup(mViewHolder, mIndex);

            onClickNormalMessage(mViewHolder, mIndex);

            if (viewType == TYPE_GROUP) checkToShowAvatar(mViewHolder, mIndex);

            if (message.getEditHistory() != null && message.getType() == MESSAGE_TYPE_TEXT)
                mViewHolder.img_edited.setVisibility(View.VISIBLE);
            else mViewHolder.img_edited.setVisibility(View.GONE);

        }

        mViewHolder.tv_send_time.setText(formatSendTime(message.getSendTime()));

        checkToShowTimeDivider(mViewHolder, mIndex);

        keepSelectedView(message, mViewHolder);

    }

    private void keepSelectedView(Message message, GroupMessageViewHolder messageViewHolder) {

        if (mCurrentStatusViewId != null && mCurrentStatusViewId.equals(message.getMessageId())) {

            messageViewHolder.tv_status.setVisibility(View.VISIBLE);
            messageViewHolder.tv_send_time.setVisibility(View.VISIBLE);

            if (message.getType() == MESSAGE_TYPE_AUDIO)
                messageViewHolder.linear_audio.setSelected(true);
            else if (message.getType() == MESSAGE_TYPE_TEXT)
                messageViewHolder.tv_message_content.setSelected(true);

        } else {

            messageViewHolder.tv_status.setVisibility(View.GONE);
            messageViewHolder.tv_send_time.setVisibility(View.GONE);

            unselectView(messageViewHolder);

        }

    }

    private void showViewForNormalMessageType(Message message, GroupMessageViewHolder messageViewHolder) {
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

        }

    }

    private void showShortMessageViewerList(GroupMessageViewHolder messageViewHolder, Map<String, MessageViewer> messageViewerMap) {

        final ArrayList<MessageViewerModel> messageViewerProfileList = new ArrayList<>();

        for (MessageViewer messageViewer : messageViewerMap.values()) {

            if (mUserProfileMap.get(messageViewer.getViewerId()) != null) {

                final MessageViewerModel messageViewerModel = new MessageViewerModel(mUserProfileMap.get(messageViewer.getViewerId()), messageViewer.getViewTime());

                messageViewerProfileList.add(0, messageViewerModel);

                if (messageViewerProfileList.size() == messageViewerMap.size()) {

                    showMessageViewerRv(messageViewHolder, messageViewerProfileList);

                }

            } else {

                getSingleUserProfile(messageViewer.getViewerId(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User messageViewerProfile) {

                        mUserProfileMap.put(messageViewer.getViewerId(), messageViewerProfile);

                        final MessageViewerModel messageViewerModel = new MessageViewerModel(messageViewerProfile, messageViewer.getViewTime());

                        messageViewerProfileList.add(0, messageViewerModel);

                        if (messageViewerProfileList.size() == messageViewerMap.size()) {

                            showMessageViewerRv(messageViewHolder, messageViewerProfileList);

                        }

                    }
                });

            }
        }
    }

    private void showMessageViewerRv(GroupMessageViewHolder messageViewHolder, ArrayList<MessageViewerModel> messageViewerProfileList) {

//        messageViewHolder.tv_status.setText(messageViewerProfileList.size() + " người đã xem");

        if (messageViewerProfileList.size() > WHO_SEEN_LIMIT) {

            messageViewHolder.tv_num_message_viewer.setVisibility(View.VISIBLE);
            messageViewHolder.tv_num_message_viewer.setText("+" + (messageViewerProfileList.size() - WHO_SEEN_LIMIT));

        } else {

            messageViewHolder.tv_num_message_viewer.setVisibility(View.GONE);
            messageViewHolder.tv_num_message_viewer.setText(null);

        }

        MessageViewerAdapter adapter = new MessageViewerAdapter(mAdapterContext, messageViewerProfileList, false);

        hideShortMessageViewerList(mSeenImageHolder);

        messageViewHolder.rv_message_viewer.setVisibility(View.VISIBLE);
        messageViewHolder.rv_message_viewer.setAdapter(adapter);

        messageViewHolder.linear_message_viewer.setVisibility(View.VISIBLE);

        mSeenImageHolder = messageViewHolder;

    }

    private void setThumbnail(GroupMessageViewHolder messageViewHolder, String messageId) {

        if (mMediaMap.get(messageId) != null) {

            setMediaUrlToView(mAdapterContext, mMediaMap.get(messageId).get(0).getThumbContentUrl(), messageViewHolder.riv_single_media);

        } else {

            getMediaOfMessage(mGroupId, messageId, new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
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

    private void showViewTypeText(GroupMessageViewHolder messageViewHolder) {

        messageViewHolder.tv_message_content.setVisibility(View.VISIBLE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);

    }

    private void showViewTypeLike(GroupMessageViewHolder messageViewHolder) {


        messageViewHolder.img_like.setVisibility(View.VISIBLE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);
    }

    private void showViewTypeSticker(GroupMessageViewHolder messageViewHolder) {


        messageViewHolder.img_sticker.setVisibility(View.VISIBLE);

        messageViewHolder.img_like.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);
    }

    private void showViewTypeMultipleMedia(GroupMessageViewHolder messageViewHolder) {

        messageViewHolder.rv_multiple_media.setVisibility(View.VISIBLE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);
    }

    private void showViewTypeSinglePicture(GroupMessageViewHolder messageViewHolder) {

        messageViewHolder.frame_single_media.setVisibility(View.VISIBLE);
        messageViewHolder.img_play_video.setVisibility(View.GONE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);
    }

    private void showViewTypeSingleVideo(GroupMessageViewHolder messageViewHolder) {

        messageViewHolder.frame_single_media.setVisibility(View.VISIBLE);// linear picture
        messageViewHolder.img_play_video.setVisibility(View.VISIBLE);

        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);
    }

    private void showViewTypeAudio(GroupMessageViewHolder messageViewHolder) {

        messageViewHolder.linear_audio.setVisibility(View.VISIBLE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.tv_removed_message.setVisibility(View.GONE);
        messageViewHolder.tv_message_content.setVisibility(View.GONE); // content
        messageViewHolder.img_like.setVisibility(View.GONE); // like icon
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);
    }

    private void showViewTypeRemove(GroupMessageViewHolder messageViewHolder) {

        messageViewHolder.tv_removed_message.setVisibility(View.VISIBLE);
        messageViewHolder.img_sticker.setVisibility(View.GONE); // like icon
        messageViewHolder.tv_message_content.setVisibility(View.GONE);
        messageViewHolder.img_like.setVisibility(View.GONE);
        messageViewHolder.frame_single_media.setVisibility(View.GONE);
        messageViewHolder.rv_multiple_media.setVisibility(View.GONE);
        messageViewHolder.linear_audio.setVisibility(View.GONE);
        messageViewHolder.tv_instant_message.setVisibility(View.GONE);

    }

    private void showMemberInteractionOptionDialog(String userId, boolean isAdmin) { //  admin or members


        final String option0 = "Xem trang cá nhân";
        final String option1 = "Chọn làm quản trị viên";
        final String option2 = "Nhắn tin";
        final String option3 = "Gọi thoại";
        final String option4 = "Gọi video";
        final String option5 = "Xoá khỏi nhóm";
        final String option6 = "Chặn";

        final ArrayList<String> arrayList = new ArrayList();

        arrayList.add(option0);
        if (isAdmin) arrayList.add(option1);
        arrayList.add(option2);
        arrayList.add(option3);
        arrayList.add(option4);
        if (isAdmin) arrayList.add(option5);
        arrayList.add(option6);

        final ArrayAdapter<String> adapter = new ArrayAdapter(mAdapterContext, android.R.layout.simple_list_item_1, arrayList);

        new AlertDialog.Builder(mAdapterContext)
                .setTitle("Tuỳ chọn")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (arrayList.get(which)) {

                            case option0:
                                viewUserProfile(mAdapterContext, userId);
                                break;

                            case option1:
                                showChangeAdminConfirmDialog(mAdapterContext, userId, mGroupId);
                                break;

                            case option2:
                                goToChat(mAdapterContext, mUserProfileMap.get(userId));
                                break;

                            case option3:
                                makeCall(mAdapterContext, userId, false);
                                break;

                            case option4:
                                makeCall(mAdapterContext, userId, true);
                                break;

                            case option5:
                                deleteMember(mAdapterContext, userId, mGroupId);
                                break;

                            case option6:
                                checkBeforeBLockUser(mAdapterContext, userId);
                                break;

                            default:
                        }

                    }
                }).create().show();

    }

    private void checkToShowAvatar(GroupMessageViewHolder messageViewHolder, int indexToCheck) {

        final Message currentMessage = (Message) mList.get(indexToCheck);
        final String currentSenderId = currentMessage.getSenderId();

        if (indexToCheck > 0) {


            final Message preMessage = (Message) mList.get(indexToCheck - 1);
            final String preSenderId = preMessage.getSenderId();

            final boolean fromSameUser = preSenderId.equals(currentSenderId);

            if (fromSameUser) {

                int preType = preMessage.getType();
                int currentType = currentMessage.getType();

                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.setTimeInMillis(currentMessage.getSendTime());

                Calendar preCalendar = Calendar.getInstance();
                preCalendar.setTimeInMillis(preMessage.getSendTime());

                int preHour = preCalendar.get(Calendar.HOUR_OF_DAY);
                int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);

                if (preType == currentType && (currentType == MESSAGE_TYPE_TEXT || currentType == MESSAGE_TYPE_LIKE) && preHour == currentHour) {

                    messageViewHolder.civ_avatar.setVisibility(View.GONE);
                    messageViewHolder.tv_name.setVisibility(View.GONE);

                } else {

                    loadUserProfile(messageViewHolder, currentMessage.getSenderId());
                    messageViewHolder.civ_avatar.setVisibility(View.VISIBLE);
                    messageViewHolder.tv_name.setVisibility(View.VISIBLE);

                }

            } else {

                loadUserProfile(messageViewHolder, currentMessage.getSenderId());
                messageViewHolder.civ_avatar.setVisibility(View.VISIBLE);
                messageViewHolder.tv_name.setVisibility(View.VISIBLE);

            }


        } else {

            loadUserProfile(messageViewHolder, currentMessage.getSenderId());
            messageViewHolder.civ_avatar.setVisibility(View.VISIBLE);
            messageViewHolder.tv_name.setVisibility(View.VISIBLE);

        }


    }

    private void checkToShowTimeDivider(GroupMessageViewHolder messageViewHolder, int index) {


        final Message message = (Message) mList.get(index);

        if (index == 0) {

            Calendar messageCalendar = Calendar.getInstance();

            messageCalendar.setTimeInMillis(message.getSendTime());

            int send_date = messageCalendar.get(Calendar.DATE);
            int send_month = messageCalendar.get(Calendar.MONTH) + 1;
            int send_year = messageCalendar.get(Calendar.YEAR);

            messageViewHolder.tv_time_divider.setVisibility(View.VISIBLE);
            messageViewHolder.tv_time_divider.setText("NGÀY " + send_date + " THÁNG " + send_month + " NĂM " + send_year);

        } else {


            final Message preMessage = (Message) mList.get(index - 1);

            final String shouldDiv = (String) formatTimeDivider(preMessage, message);

            messageViewHolder.tv_time_divider.setVisibility(shouldDiv != null ? View.VISIBLE : View.GONE);
            messageViewHolder.tv_time_divider.setText(shouldDiv);


        }
    }

    // ---------------------CONTENT PROCESSING-----------------------//

    public void loadMoreMessages(ArrayList<Message> newItems) {
        mList.addAll(0, newItems);
        notifyItemRangeInserted(0, newItems.size());
        notifyItemRangeChanged(newItems.size() - 1, newItems.size());
    }

    private View.OnClickListener clickForward(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMediaMessage(message.getType())) {

                    getMediaOfMessage(mGroupId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                        @Override
                        public void OnCallBack(ArrayList<Media> callbackMediaList) {

                            if (callbackMediaList.size() > 0) {
                                forwardGroupMedia(mAdapterContext, callbackMediaList.get(0).getMediaId(), mGroupId);
                            }

                        }
                    });

                } else forwardGroupMessage(mAdapterContext, message.getMessageId(), mGroupId);

                mOptionsDialog.dismiss();

            }
        };
    }

    private View.OnClickListener clickDelete(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteMessage(message);

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

                    getMediaOfMessage(mGroupId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
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

    private View.OnClickListener clickRelatedUser(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRelatedUserDialog(message.getRelatedUserId());

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

    private View.OnClickListener clickMessageViewer(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMessageViewerDetailList(message.getMessageViewer());

                mOptionsDialog.dismiss();
            }
        };
    }

    private void showOptions(GroupMessageViewHolder messageViewHolder, Message message) {

        LinearLayout btn_forward = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_forward);
        LinearLayout btn_copy = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_copy);
        LinearLayout btn_download = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_download);
        LinearLayout btn_delete = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_delete);
        LinearLayout btn_takeback = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_take_back);
        LinearLayout btn_related_user = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_related_user);
        LinearLayout btn_edit_message = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_message);
        LinearLayout btn_edit_history = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_history);
        LinearLayout btn_message_viewer = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_message_viewer);

        final boolean ableToTakeback = getCurrentTimeInMilies() - message.getSendTime() <= TAKING_BACK_MESSAGE_TIME_LIMIT;

        mSelectedBackgroundHolder = null;

        btn_delete.setVisibility(View.VISIBLE);
        btn_delete.setOnClickListener(clickDelete(message));

        if (message.getMessageViewer() != null) {
            btn_message_viewer.setVisibility(View.VISIBLE);
            btn_message_viewer.setOnClickListener(clickMessageViewer(message));
        } else {
            btn_message_viewer.setVisibility(View.GONE);
            btn_message_viewer.setOnClickListener(null);
        }

        switch (message.getType()) {

            case MESSAGE_TYPE_LIKE:
            case MESSAGE_TYPE_STICKER:

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeback) {
                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));
                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }

                btn_related_user.setVisibility(View.GONE);
                btn_related_user.setOnClickListener(null);

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

                break;


            case MESSAGE_TYPE_UPDATE_MEMBER:
            case MESSAGE_TYPE_LEAVE_GROUP:
            case MESSAGE_TYPE_CHANGE_ADMIN:

                btn_related_user.setVisibility(View.VISIBLE);
                btn_related_user.setOnClickListener(clickRelatedUser(message));

                btn_takeback.setVisibility(View.GONE);
                btn_takeback.setOnClickListener(null);

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

                break;

            case MESSAGE_TYPE_UPDATE_GROUP:
            case MESSAGE_TYPE_REMOVED:

                btn_related_user.setVisibility(View.GONE);
                btn_related_user.setOnClickListener(null);

                btn_takeback.setVisibility(View.GONE);
                btn_takeback.setOnClickListener(null);

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

                break;

            case MESSAGE_TYPE_TEXT:

                mSelectedBackgroundHolder = messageViewHolder;
                mSelectedBackgroundHolder.tv_message_content.setSelected(true);

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeback) {

                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));

                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }

                btn_forward.setVisibility(View.VISIBLE);
                btn_forward.setOnClickListener(clickForward(message));

                btn_copy.setVisibility(View.VISIBLE);
                btn_copy.setOnClickListener(clickCopy(message));

                if (message.getSenderId().equals(getMyFirebaseUserId()) && !message.isForwardedMessage()) {
                    btn_edit_message.setVisibility(View.VISIBLE);
                    btn_edit_message.setOnClickListener(clickEditMessage(message));
                } else {
                    btn_edit_message.setVisibility(View.GONE);
                    btn_edit_message.setOnClickListener(null);
                }

                if (message.getEditHistory() != null && !message.isForwardedMessage()) {
                    btn_edit_history.setVisibility(View.VISIBLE);
                    btn_edit_history.setOnClickListener(clickEditHistory(message));
                } else {
                    btn_edit_history.setVisibility(View.GONE);
                    btn_edit_history.setOnClickListener(null);
                }

                btn_download.setVisibility(View.GONE);
                btn_download.setOnClickListener(null);

                btn_related_user.setVisibility(View.GONE);
                btn_related_user.setOnClickListener(null);

                break;

            case MESSAGE_TYPE_PICTURE:
            case MESSAGE_TYPE_VIDEO:

                btn_download.setVisibility(View.VISIBLE);
                btn_download.setOnClickListener(clickDownload(message));

                btn_forward.setVisibility(View.VISIBLE);
                btn_forward.setOnClickListener(clickForward(message));

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeback) {
                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));
                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }

                btn_copy.setVisibility(View.GONE);
                btn_copy.setOnClickListener(null);

                btn_related_user.setVisibility(View.GONE);
                btn_related_user.setOnClickListener(null);

                btn_edit_message.setVisibility(View.GONE);
                btn_edit_message.setOnClickListener(null);

                btn_edit_history.setVisibility(View.GONE);
                btn_edit_history.setOnClickListener(null);

                break;

            case MESSAGE_TYPE_AUDIO:

                mSelectedBackgroundHolder = messageViewHolder;
                mSelectedBackgroundHolder.linear_audio.setSelected(true);

                btn_forward.setVisibility(View.VISIBLE);
                btn_forward.setOnClickListener(clickForward(message));

                if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeback) {
                    btn_takeback.setVisibility(View.VISIBLE);
                    btn_takeback.setOnClickListener(clickTakeBack(message));
                } else {
                    btn_takeback.setVisibility(View.GONE);
                    btn_takeback.setOnClickListener(null);
                }

                btn_copy.setVisibility(View.GONE);
                btn_copy.setOnClickListener(null);

                btn_download.setVisibility(View.GONE);
                btn_download.setOnClickListener(null);

                btn_related_user.setVisibility(View.GONE);
                btn_related_user.setOnClickListener(null);

                btn_edit_message.setVisibility(View.GONE);
                btn_edit_message.setOnClickListener(null);

                btn_edit_history.setVisibility(View.GONE);
                btn_edit_history.setOnClickListener(null);

                break;

        }


        mShowingOptionMessageId = message.getMessageId();

        mOptionsDialog.show();

    }

    private void showMultipleMediaOptions(Message message, Media mediaToShow) {


        LinearLayout btn_forward = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_forward);
        LinearLayout btn_copy = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_copy);
        LinearLayout btn_download = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_download);
        LinearLayout btn_delete = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_delete);
        LinearLayout btn_takeback = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_take_back);
        LinearLayout btn_related_user = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_related_user);
        LinearLayout btn_edit_message = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_message);
        LinearLayout btn_edit_history = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_history);
        LinearLayout btn_message_viewer = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_message_viewer);

        final boolean ableToTakeback = getCurrentTimeInMilies() - message.getSendTime() <= TAKING_BACK_MESSAGE_TIME_LIMIT;

        btn_forward.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.VISIBLE);
        btn_download.setVisibility(View.VISIBLE);

        if (message.getSenderId().equals(getMyFirebaseUserId()) && ableToTakeback) {
            btn_takeback.setVisibility(View.VISIBLE);
            btn_takeback.setOnClickListener(clickTakeBack(message));
        } else {
            btn_takeback.setVisibility(View.GONE);
            btn_takeback.setOnClickListener(null);
        }

        if (message.getMessageViewer() != null) {
            btn_message_viewer.setVisibility(View.VISIBLE);
            btn_message_viewer.setOnClickListener(clickMessageViewer(message));
        } else {
            btn_message_viewer.setVisibility(View.GONE);
            btn_message_viewer.setOnClickListener(null);
        }

        btn_copy.setVisibility(View.GONE);
        btn_copy.setOnClickListener(null);

        btn_related_user.setVisibility(View.GONE);
        btn_related_user.setOnClickListener(null);

        btn_edit_history.setVisibility(View.GONE);
        btn_edit_history.setOnClickListener(null);

        btn_edit_message.setVisibility(View.GONE);
        btn_edit_message.setOnClickListener(null);

        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forwardGroupMedia(mAdapterContext, mediaToShow.getMediaId(), mGroupId);

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

                            getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                                @Override
                                public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                                    if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                                        if (!emojiAppCompatEditText.getText().toString().trim().isEmpty()
                                                && !emojiAppCompatEditText.getText().toString().trim().equals(currentMessage.getContent())) {

                                            final Map<String, Object> editMap = new HashMap<>();

                                            final String key = ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mGroupId)
                                                    .child(currentMessage.getMessageId()).child(kEditHistory).push().getKey();

                                            final String previousContent = currentMessage.getContent();
                                            final String updatedContent = emojiAppCompatEditText.getText().toString().trim();

                                            final EditHistory editHistory = new EditHistory(previousContent, getCurrentTimeInMilies());

                                            editMap.put(kMessageContent, updatedContent);
                                            editMap.put("/" + kEditHistory + "/" + key, editHistory);

                                            DatabaseReference myMessageRef = ROOT_REF.child(CHILD_MESSAGES)
                                                    .child(getMyFirebaseUserId()).child(mGroupId)
                                                    .child(currentMessage.getMessageId());

                                            myMessageRef.updateChildren(editMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    for (DataSnapshot snapshot : groupMemberSnapshot.getChildren()) {

                                                        if (!snapshot.getKey().equals(getMyFirebaseUserId())) {

                                                            DatabaseReference memberMessageRef = ROOT_REF.child(CHILD_MESSAGES)
                                                                    .child(snapshot.getKey()).child(mGroupId)
                                                                    .child(currentMessage.getMessageId());

                                                            memberMessageRef.runTransaction(new Transaction.Handler() {
                                                                @NonNull
                                                                @Override
                                                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                                    return Transaction.success(mutableData);
                                                                }

                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                                                    final Message message = dataSnapshot.getValue(Message.class);

                                                                    if (message != null) {
                                                                        memberMessageRef.updateChildren(editMap);
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                }
                                            });
                                        }


                                    } else showMessageDialog(mAdapterContext, "Bạn không còn là thành viên của nhóm này !");

                                }
                            });

                        } else showNoConnectionDialog(mAdapterContext);
                    }
                }).setNegativeButton("Huỷ", null).create().show();


    }

    private void loadMediaList(GroupMessageViewHolder messageViewHolder, Message message) {

        if (mMediaMap.get(message.getMessageId()) != null) {

            MultipleMediaAdapter mediaAdapter = new MultipleMediaAdapter(mAdapterContext, mMediaMap.get(message.getMessageId()),
                    new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
                        @Override
                        public void OnItemClick(View v, int position) {
                            viewThisGroupMedia(mAdapterContext, mMediaMap.get(message.getMessageId()).get(position).getMediaId(), mGroupId);
                        }

                        @Override
                        public void OnItemLongClick(View v, int position) {
                            showMultipleMediaOptions(message, mMediaMap.get(message.getMessageId()).get(position));
                        }

                    });

            messageViewHolder.rv_multiple_media.setAdapter(mediaAdapter);

        } else {

            getMediaOfMessage(mGroupId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                @Override
                public void OnCallBack(ArrayList<Media> callbackMediaList) {

                    if (callbackMediaList.size() > 0) {

                        mMediaMap.put(message.getMessageId(), callbackMediaList);

                        MultipleMediaAdapter mediaAdapter = new MultipleMediaAdapter(mAdapterContext, callbackMediaList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
                                    @Override
                                    public void OnItemClick(View v, int position) {
                                        viewThisGroupMedia(mAdapterContext, callbackMediaList.get(position).getMediaId(), mGroupId);
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

    private void deleteMessage(Message message) {
        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mGroupId)
                .child(message.getMessageId()).removeValue();
    }

    private void takeBackMessage(Message message) {

        if (isConnectedToFirebaseService(mAdapterContext)) {

            if (getCurrentTimeInMilies() - message.getSendTime() <= TAKING_BACK_MESSAGE_TIME_LIMIT) {

                getGroupMemberSnapshot(mGroupId, new AppConstants.AppInterfaces.FirebaseGroupMemberCallBack() {
                    @Override
                    public void OnCallBack(DataSnapshot groupMemberSnapshot) {

                        if (groupMemberSnapshot.hasChild(getMyFirebaseUserId())) {

                            Map<String, Object> removeMessageMap = new HashMap<>();

                            removeMessageMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + mGroupId + "/" + message.getMessageId() + "/" + kMessageContent, "Đã thu hồi tin nhắn");
                            removeMessageMap.put("/" + CHILD_MESSAGES + "/" + getMyFirebaseUserId() + "/" + mGroupId + "/" + message.getMessageId() + "/" + kMessageType, MESSAGE_TYPE_REMOVED);

                            for (DataSnapshot snapshot : groupMemberSnapshot.getChildren()) {
                                if (!snapshot.getKey().equals(getMyFirebaseUserId())) {
                                    removeMessageMap.put("/" + CHILD_MESSAGES + "/" + snapshot.getKey() + "/" + mGroupId + "/" + message.getMessageId(), null);
                                }
                            }

                            ROOT_REF.updateChildren(removeMessageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    for (DataSnapshot snapshot : groupMemberSnapshot.getChildren()) {

                                        if (!snapshot.getKey().equals(getMyFirebaseUserId())) {

                                            sendNotificationToUser(
                                                    snapshot.getKey(),
                                                    getMyFirebaseUserId(),
                                                    mGroupId,
                                                    message.getMessageId(),
                                                    null,
                                                    null,
                                                    null,
                                                    NOTIFICATION_TYPE_REMOVE_MESSAGE,
                                                    message.getNotificationId());

                                        }
                                    }

                                }
                            });


                            getMediaOfMessage(mGroupId, message.getMessageId(), new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
                                @Override
                                public void OnCallBack(ArrayList<Media> callbackMediaList) {

                                    final Map<String, Object> removeMediaMap = new HashMap<>();

                                    for (DataSnapshot memberSnapshot : groupMemberSnapshot.getChildren()) {

                                        for (Media media : callbackMediaList) {

                                            removeMediaMap.put("/" + CHILD_MEDIA + "/" + memberSnapshot.getKey() + "/" + mGroupId + "/" + media.getMediaId(), null);

                                        }

                                    }

                                    ROOT_REF.updateChildren(removeMediaMap);

                                }
                            });

                        } else showMessageDialog(mAdapterContext, "Bạn không còn là thành viên của nhóm này!");

                    }
                });

            } else showLongToast(mAdapterContext, "Đã quá thời gian thu hồi !");

        } else showNoConnectionDialog(mAdapterContext);


    }

    public void stopActionWithThisMessage(Message message) {

        getNotificationManager(mAdapterContext).cancel(message.getNotificationId());

        if (mPlayingAudioMessageId != null && message.getMessageId().equals(mPlayingAudioMessageId)) releaseMediaPlayer();

        if (mShowingPhoneNumberOptionMessageId != null && message.getMessageId().equals(mShowingPhoneNumberOptionMessageId)) {
            if (mPhoneNumberOptionDialog != null && mPhoneNumberOptionDialog.isShowing()) {
                mPhoneNumberOptionDialog.dismiss();
            }
        }

        if (mShowingOptionMessageId != null && mShowingOptionMessageId.equals(message.getMessageId())) {
            if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
                mOptionsDialog.dismiss();
            }
        }

        if (mShowingEditHistoryMessageId != null && mShowingEditHistoryMessageId.equals(message.getMessageId())) {
            if (mEditHistoryDialog != null && mEditHistoryDialog.isShowing()) {
                mEditHistoryDialog.dismiss();
            }
        }

        if (mCurrentStatusViewId != null && mCurrentStatusViewHolder != null
                && mCurrentStatusViewId.equals(message.getMessageId())) {

            hideStatusView(mCurrentStatusViewHolder);

        }

    }

    private void loadUserProfile(GroupMessageViewHolder messageViewHolder, String userId) {


        if (mUserProfileMap.get(userId) != null) {

            User user = mUserProfileMap.get(userId);

            setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), messageViewHolder.civ_avatar);

            messageViewHolder.tv_name.setText(user.getName());

        } else {

            getSingleUserProfile(userId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                @Override
                public void OnCallBack(User callbackUserProfile) {

                    mUserProfileMap.put(userId, callbackUserProfile);

                    setAvatarToView(mAdapterContext, callbackUserProfile.getThumbAvatarUrl(), callbackUserProfile.getName(), messageViewHolder.civ_avatar);

                    messageViewHolder.tv_name.setText(callbackUserProfile.getName());

                }
            });

        }


    }

    private void processChangeAdminContent(TextView textView, Message message) {

        String previousAdminId = message.getSenderId();

        if (previousAdminId.equals(getMyFirebaseUserId())) {

            String newAdminId = "";

            for (String relatedUserId : message.getRelatedUserId().keySet()) newAdminId = relatedUserId;

            if (mUserProfileMap.get(newAdminId) != null) {

                textView.setText("Bạn đã chọn " + mUserProfileMap.get(newAdminId).getName() + " làm quản trị viên");

            } else {

                String tempNewAdminId = newAdminId;

                getSingleUserProfile(newAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User newAdminProfile) {

                        mUserProfileMap.put(tempNewAdminId, newAdminProfile);

                        textView.setText("Bạn đã chọn " + newAdminProfile.getName() + " làm quản trị viên");

                    }
                });

            }

        } else {

            final boolean newAdminIsMe = message.getRelatedUserId().get(getMyFirebaseUserId()) != null;

            if (newAdminIsMe) { // if newAdminId  == my Id

                if (mUserProfileMap.get(previousAdminId) != null) {

                    textView.setText(mUserProfileMap.get(previousAdminId).getName() + " đã chọn bạn làm quản trị viên");

                } else {

                    getSingleUserProfile(previousAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                        @Override
                        public void OnCallBack(User previousAdminProfile) {

                            mUserProfileMap.put(previousAdminId, previousAdminProfile);

                            textView.setText(previousAdminProfile.getName() + " đã chọn bạn làm quản trị viên");

                        }
                    });

                }

            } else {

                if (mUserProfileMap.get(previousAdminId) != null) {

                    String newAdminId = "";

                    for (String relatedUserId : message.getRelatedUserId().keySet()) newAdminId = relatedUserId;

                    if (mUserProfileMap.get(newAdminId) != null) {

                        textView.setText(mUserProfileMap.get(previousAdminId).getName() + " đã chọn " + mUserProfileMap.get(newAdminId).getName() + " làm quản trị viên");

                    } else {

                        final String tempNewAdminId = newAdminId;

                        getSingleUserProfile(newAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                            @Override
                            public void OnCallBack(User newAdminProfile) {

                                mUserProfileMap.put(tempNewAdminId, newAdminProfile);

                                textView.setText(mUserProfileMap.get(previousAdminId).getName() + " đã chọn " + newAdminProfile.getName() + " làm quản trị viên");

                            }
                        });


                    }
                } else {

                    getSingleUserProfile(previousAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                        @Override
                        public void OnCallBack(User previousAdminProfile) {

                            mUserProfileMap.put(previousAdminId, previousAdminProfile);

                            String newAdminId = "";

                            for (String relatedUserId : message.getRelatedUserId().keySet()) newAdminId = relatedUserId;

                            if (mUserProfileMap.get(newAdminId) != null) {

                                textView.setText(previousAdminProfile.getName() + " đã chọn " + mUserProfileMap.get(newAdminId).getName() + " làm quản trị viên");

                            } else {

                                final String tempNewAdminId = newAdminId;

                                getSingleUserProfile(newAdminId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                    @Override
                                    public void OnCallBack(User newAdminProfile) {

                                        mUserProfileMap.put(tempNewAdminId, newAdminProfile);

                                        textView.setText(previousAdminProfile.getName() + " đã chọn " + newAdminProfile.getName() + " làm quản trị viên");

                                    }
                                });


                            }
                        }
                    });

                }

            }

        }

    }

    private void processUnChangeContent(TextView textView, Message message) {

        final String updaterUserId = message.getSenderId();
        final String contentToShow = message.getContent().toLowerCase();

        if (updaterUserId.equals(getMyFirebaseUserId())) {

            textView.setText("Bạn" + " " + contentToShow);

        } else {

            if (mUserProfileMap.get(updaterUserId) != null) {

                textView.setText(mUserProfileMap.get(updaterUserId).getName() + " " + contentToShow);

            } else {

                getSingleUserProfile(updaterUserId, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User callbackUserProfile) {

                        mUserProfileMap.put(updaterUserId, callbackUserProfile);

                        textView.setText(callbackUserProfile.getName() + " " + contentToShow);

                    }
                });
            }

        }

    }

    // ---------------------STATUS PROCESSING-----------------------//

    private void loadMessageViewerDetailList(Map<String, MessageViewer> messageViewerMap) {

        if (messageViewerMap == null) {

            showMessageDialog(mAdapterContext, "Chưa có thành viên nào xem tin nhắn");

        } else {

            final ArrayList<MessageViewerModel> messageViewerProfileList = new ArrayList<>();

            for (MessageViewer messageViewer : messageViewerMap.values()) {

                if (mUserProfileMap.get(messageViewer.getViewerId()) != null) {

                    final MessageViewerModel messageViewerModel = new MessageViewerModel(mUserProfileMap.get(messageViewer.getViewerId()), messageViewer.getViewTime());

                    messageViewerProfileList.add(0, messageViewerModel);

                    if (messageViewerProfileList.size() == messageViewerMap.size()) {

                        showMessageViewerDetailDialog(messageViewerProfileList);

                    }

                } else {

                    getSingleUserProfile(messageViewer.getViewerId(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                        @Override
                        public void OnCallBack(User messageViewerProfile) {

                            mUserProfileMap.put(messageViewer.getViewerId(), messageViewerProfile);

                            final MessageViewerModel messageViewerModel = new MessageViewerModel(messageViewerProfile, messageViewer.getViewTime());

                            messageViewerProfileList.add(0, messageViewerModel);

                            if (messageViewerProfileList.size() == messageViewerMap.size()) {

                                showMessageViewerDetailDialog(messageViewerProfileList);

                            }
                        }
                    });


                }

            }
        }

    }

    private void showMessageViewerDetailDialog(ArrayList<MessageViewerModel> messageViewerList) {

        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.dialog_message_viewer, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_message_viewer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mAdapterContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(null);

        MessageViewerAdapter adapter = new MessageViewerAdapter(mAdapterContext, messageViewerList, true);

        recyclerView.setAdapter(adapter);

        new AlertDialog.Builder(mAdapterContext)
                .setTitle(messageViewerList.size() + " người đã xem")
                .setView(view)
                .setPositiveButton("Đóng", null)
                .create().show();

    }

    private void showRelatedUserDialog(Map<String, Boolean> relatedUserMap) {

        if (relatedUserMap != null) {

            View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.dialog_related_user, null);

            ArrayList<User> relatedUserList = new ArrayList<>();
            RelatedUserAdapter relatedUserAdapter = new RelatedUserAdapter(mAdapterContext, relatedUserList);

            RecyclerView relatedUserRv = (RecyclerView) view.findViewById(R.id.rv_related_user);
            relatedUserRv.setHasFixedSize(true);
            relatedUserRv.setLayoutManager(new LinearLayoutManager(mAdapterContext, LinearLayoutManager.VERTICAL, false));
            relatedUserRv.setAdapter(relatedUserAdapter);

            for (String userKey : relatedUserMap.keySet()) {

                if (mUserProfileMap.get(userKey) != null) {

                    relatedUserList.add(mUserProfileMap.get(userKey));

                    if (relatedUserList.size() == relatedUserMap.size()) {

                        relatedUserAdapter.notifyDataSetChanged();

                        new AlertDialog.Builder(mAdapterContext)
                                .setTitle(relatedUserMap.size() + " thành viên")
                                .setView(view)
                                .setPositiveButton("Đóng", null)
                                .create().show();

                    }

                } else {

                    getSingleUserProfile(userKey, new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                        @Override
                        public void OnCallBack(User messageViewerProfile) {

                            mUserProfileMap.put(userKey, messageViewerProfile);

                            relatedUserList.add(mUserProfileMap.get(userKey));

                            if (relatedUserList.size() == relatedUserMap.size()) {

                                relatedUserAdapter.notifyDataSetChanged();

                                new AlertDialog.Builder(mAdapterContext)
                                        .setTitle(relatedUserMap.size() + " thành viên")
                                        .setView(view)
                                        .setPositiveButton("Đóng", null)
                                        .create().show();

                            }
                        }
                    });
                }
            }
        }
    }

    private void formatMessageStatusGroup(GroupMessageViewHolder messageViewHolder, int indexToFormat) {

        final Message messageToFormat = mList.get(indexToFormat);

        switch (messageToFormat.getStatus()) {

            case MESSAGE_STATUS_SENDING:

                messageViewHolder.tv_status.setText("Đang gửi");

                if (getItemViewType(indexToFormat) == TYPE_ME) {

                    messageViewHolder.img_status.setVisibility(View.VISIBLE);
                    messageViewHolder.img_status.setImageResource(R.drawable.ic_status_sending);

                }

                hideShortMessageViewerList(messageViewHolder);

                break;

            case MESSAGE_STATUS_SENT:

                messageViewHolder.tv_status.setText("Đã gửi");

                if (getItemViewType(indexToFormat) == TYPE_ME) {

                    messageViewHolder.img_status.setVisibility(View.VISIBLE);
                    messageViewHolder.img_status.setImageResource(R.drawable.ic_status_sent);
                }

                hideShortMessageViewerList(messageViewHolder);

                break;
            case MESSAGE_STATUS_RECEIVED:

                messageViewHolder.tv_status.setText("Đã nhận");

                if (getItemViewType(indexToFormat) == TYPE_ME) {

                    messageViewHolder.img_status.setVisibility(View.VISIBLE);
                    messageViewHolder.img_status.setImageResource(R.drawable.ic_status_received);
                }

                hideShortMessageViewerList(messageViewHolder);

                break;

            case MESSAGE_STATUS_SEEN:

                if (getItemViewType(indexToFormat) == TYPE_ME) hideStatusImage(messageViewHolder);

                if (messageToFormat.getMessageViewer() != null) {

                    messageViewHolder.tv_status.setText("Đã xem");

                    if (indexToFormat == mList.size() - 1) {

                        showShortMessageViewerList(messageViewHolder, messageToFormat.getMessageViewer());

                    } else hideShortMessageViewerList(messageViewHolder);

                } else hideShortMessageViewerList(messageViewHolder);

                break;
        }


    }

    private void hideStatusImage(GroupMessageViewHolder messageViewHolder) {
        messageViewHolder.img_status.setVisibility(View.INVISIBLE);
        messageViewHolder.img_status.setImageDrawable(null);

    }

    private void hideShortMessageViewerList(GroupMessageViewHolder messageViewHolder) {

        if (messageViewHolder != null) {

            messageViewHolder.rv_message_viewer.setVisibility(View.GONE);
            messageViewHolder.rv_message_viewer.setAdapter(null);
            messageViewHolder.tv_num_message_viewer.setVisibility(View.GONE);
            messageViewHolder.tv_num_message_viewer.setText(null);
            messageViewHolder.linear_message_viewer.setVisibility(View.GONE);

        }


    }

    public void hideStatusView(GroupMessageViewHolder messageViewHolder) {

        Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (messageViewHolder.tv_instant_message_status.getVisibility() == View.VISIBLE)
                    messageViewHolder.tv_instant_message_status.setVisibility(View.GONE);

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

        if (messageViewHolder.tv_instant_message_status.getVisibility() == View.VISIBLE)
            messageViewHolder.tv_instant_message_status.animate().setDuration(200).translationY(messageViewHolder.tv_instant_message_status.getHeight()).setListener(animListener);

        if (messageViewHolder.tv_status.getVisibility() == View.VISIBLE)
            messageViewHolder.tv_status.animate().setDuration(200).translationY(messageViewHolder.tv_status.getHeight()).setListener(animListener);

        if (messageViewHolder.tv_send_time.getVisibility() == View.VISIBLE)
            messageViewHolder.tv_send_time.animate().setDuration(200).translationY(messageViewHolder.tv_send_time.getHeight()).setListener(animListener);

        unselectView(messageViewHolder);

        mCurrentStatusViewHolder = null;
        mCurrentStatusViewId = null;

    }

    private View.OnClickListener generateShowHideStatusClick(GroupMessageViewHolder messageViewHolder, Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (messageViewHolder.tv_send_time.getVisibility() == View.VISIBLE
                        && messageViewHolder.tv_status.getVisibility() == View.VISIBLE
                        || (messageViewHolder.tv_send_time.getVisibility() == View.VISIBLE
                        && messageViewHolder.tv_instant_message_status.getVisibility() == View.VISIBLE)) {

                    hideStatusView(messageViewHolder);

                } else showStatusView(message, messageViewHolder);

            }
        };
    }

    private View.OnLongClickListener generateShowOptionClick(GroupMessageViewHolder messageViewHolder, Message message) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showOptions(messageViewHolder, message);
                return true;
            }
        };
    }

    private View.OnClickListener generateViewMediaClick(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaMap.get(message.getMessageId()) != null) {
                    viewThisGroupMedia(mAdapterContext, mMediaMap.get(message.getMessageId()).get(0).getMediaId(), mGroupId);
                }
            }
        };

    }

    private void onClickInstantMessage(GroupMessageViewHolder messageViewHolder, int clickedIndex) {

        final Message message = mList.get(clickedIndex);

        messageViewHolder.tv_instant_message.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));
        messageViewHolder.tv_instant_message.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));

        messageViewHolder.tv_message_content.setOnClickListener(null);
        messageViewHolder.img_like.setOnClickListener(null);
        messageViewHolder.img_sticker.setOnClickListener(null);
        messageViewHolder.linear_audio.setOnClickListener(null);
        messageViewHolder.tv_removed_message.setOnClickListener(null);
        messageViewHolder.img_play_video.setOnClickListener(null);
        messageViewHolder.riv_single_media.setOnClickListener(null);

        messageViewHolder.tv_message_content.setOnLongClickListener(null);
        messageViewHolder.riv_single_media.setOnLongClickListener(null);
        messageViewHolder.img_like.setOnLongClickListener(null);
        messageViewHolder.img_sticker.setOnLongClickListener(null);
        messageViewHolder.img_play_video.setOnLongClickListener(null);
        messageViewHolder.linear_audio.setOnLongClickListener(null);
        messageViewHolder.tv_removed_message.setOnLongClickListener(null);

        if (!message.getSenderId().equals(getMyFirebaseUserId())) {

            messageViewHolder.civ_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isDelaying) {

                        isDelaying = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isDelaying = false;
                            }
                        }, 1000);

                        boolean isAdmin = mAdapterContext.getGroupAdminId() != null && mAdapterContext.getGroupAdminId().equals(getMyFirebaseUserId());
                        showMemberInteractionOptionDialog(message.getSenderId(), isAdmin);
                    }


                }
            });

        }

    }

    private void onClickNormalMessage(GroupMessageViewHolder messageViewHolder, int clickedIndex) {

        final Message message = mList.get(clickedIndex);

        messageViewHolder.tv_instant_message.setOnLongClickListener(null);
        messageViewHolder.tv_instant_message.setOnClickListener(null);

        switch (message.getType()) {

            case MESSAGE_TYPE_AUDIO:

                messageViewHolder.linear_audio.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.linear_audio.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);

                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
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
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);

                break;

            case MESSAGE_TYPE_PICTURE:
                messageViewHolder.riv_single_media.setOnClickListener(generateViewMediaClick(message));
                messageViewHolder.riv_single_media.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
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
                messageViewHolder.img_like.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);

                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.tv_removed_message.setOnLongClickListener(null);

                break;

            case MESSAGE_TYPE_LIKE:
                messageViewHolder.img_like.setOnClickListener(generateShowHideStatusClick(messageViewHolder, message));
                messageViewHolder.img_like.setOnLongClickListener(generateShowOptionClick(messageViewHolder, message));

                messageViewHolder.tv_message_content.setOnClickListener(null);
                messageViewHolder.riv_single_media.setOnClickListener(null);
                messageViewHolder.img_play_video.setOnClickListener(null);
                messageViewHolder.linear_audio.setOnClickListener(null);
                messageViewHolder.img_sticker.setOnClickListener(null);
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
                messageViewHolder.img_sticker.setOnLongClickListener(null);
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
                messageViewHolder.tv_removed_message.setOnClickListener(null);

                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);
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

                messageViewHolder.img_sticker.setOnLongClickListener(null);
                messageViewHolder.img_like.setOnLongClickListener(null);
                messageViewHolder.tv_message_content.setOnLongClickListener(null);
                messageViewHolder.riv_single_media.setOnLongClickListener(null);
                messageViewHolder.img_play_video.setOnLongClickListener(null);
                messageViewHolder.linear_audio.setOnLongClickListener(null);


                break;


        }


        if (!message.getSenderId().equals(getMyFirebaseUserId())) {

            messageViewHolder.civ_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isDelaying) {

                        isDelaying = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isDelaying = false;
                            }
                        }, 1000);

                        boolean isAdmin = mAdapterContext.getGroupAdminId() != null && mAdapterContext.getGroupAdminId().equals(getMyFirebaseUserId());
                        showMemberInteractionOptionDialog(message.getSenderId(), isAdmin);
                    }


                }
            });

        }


    }

    private void showStatusView(Message message, GroupMessageViewHolder messageViewHolder) {

        Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (isGroupInstantMessage(message)) {

                    if (messageViewHolder.tv_instant_message_status.getVisibility() != View.VISIBLE) {
                        messageViewHolder.tv_instant_message_status.setVisibility(View.VISIBLE);
                    }

                } else if (messageViewHolder.tv_status.getVisibility() != View.VISIBLE) {
                    messageViewHolder.tv_status.setVisibility(View.VISIBLE);
                }

                if (messageViewHolder.tv_send_time.getVisibility() != View.VISIBLE) {
                    messageViewHolder.tv_send_time.setVisibility(View.VISIBLE);
                }


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

        if (isGroupInstantMessage(message))
            messageViewHolder.tv_instant_message_status.animate().setDuration(200).translationY(0).setListener(animListener);
        else messageViewHolder.tv_status.animate().setDuration(200).translationY(0).setListener(animListener);

        messageViewHolder.tv_send_time.animate().setDuration(200).translationY(0).setListener(animListener);

        if (message.getType() == MESSAGE_TYPE_AUDIO)
            messageViewHolder.linear_audio.setSelected(true);
        else if (message.getType() == MESSAGE_TYPE_TEXT)
            messageViewHolder.tv_message_content.setSelected(true);

        if (mCurrentStatusViewId != null && mCurrentStatusViewHolder != null) {
            hideStatusView(mCurrentStatusViewHolder);
        }

        mCurrentStatusViewId = message.getMessageId();
        mCurrentStatusViewHolder = messageViewHolder;

    }

    private void updateNonPlayingView(String messageId, GroupMessageViewHolder messageViewHolder) {


        messageViewHolder.pb_audio_player.removeCallbacks(seekbarUpdater);
        messageViewHolder.pb_audio_player.setProgress(0);

        messageViewHolder.img_play_audio.setImageResource(R.drawable.ic_play_audio_filled_color);
        messageViewHolder.img_play_audio.setVisibility(View.VISIBLE);

        messageViewHolder.pb_preparing_audio.setVisibility(View.GONE);

        if (mMediaMap.get(messageId) != null) {

            messageViewHolder.tv_audio_duration.setText(formatSecondsToHours(mMediaMap.get(messageId).get(0).getDuration()));

        } else {

            getMediaOfMessage(mGroupId, messageId, new AppConstants.AppInterfaces.FirebaseMediaListCallBack() {
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

    private void startMediaPlayer(String audioSource) {

        try {
            mAudioPlayer = new MediaPlayer();
            mAudioPlayer.setDataSource(audioSource);
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

    private void clickLinkInContent(String messageId, EmojiAppCompatTextView emojiAppCompatTextView) {
        BetterLinkMovementMethod.linkify(Linkify.ALL, emojiAppCompatTextView)
                .setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
                    @Override
                    public boolean onClick(TextView textView, String url) {

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

    public class GroupMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CircleImageView civ_avatar;

        final ImageView img_status;
        final ImageView img_like;
        final ImageView img_sticker;
        final ImageView img_edited;
        final ImageView img_play_video;
        final ImageView img_play_audio;

        final RoundedImageView riv_single_media;

        final TextView tv_send_time;
        final TextView tv_status;
        final TextView tv_name;
        final TextView tv_time_divider;
        final TextView tv_removed_message;
        final TextView tv_audio_duration;
        final TextView tv_num_message_viewer;
        final TextView tv_instant_message;
        final TextView tv_instant_message_status;

        final EmojiAppCompatTextView tv_message_content;

        final FrameLayout frame_single_media;
        final FrameLayout frame_play_audio;

        //removed message
        final LinearLayout linear_message_root;
        final LinearLayout linear_audio;
        final LinearLayout linear_message_viewer;

        final ProgressBar pb_audio_player;
        final ProgressBar pb_preparing_audio;

        final RecyclerView rv_multiple_media;
        final RecyclerView rv_message_viewer;


        public GroupMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            civ_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);

            img_like = (ImageView) itemView.findViewById(R.id.img_like);
            img_sticker = (ImageView) itemView.findViewById(R.id.img_sticker);
            img_play_video = (ImageView) itemView.findViewById(R.id.img_play_video);
            img_play_audio = (ImageView) itemView.findViewById(R.id.img_play_audio);
            img_edited = (ImageView) itemView.findViewById(R.id.img_edited);

            riv_single_media = (RoundedImageView) itemView.findViewById(R.id.riv_single_picture);

            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
            tv_time_divider = (TextView) itemView.findViewById(R.id.tv_time_divider);
            tv_status = (TextView) itemView.findViewById(R.id.tv_message_status);
            tv_removed_message = (TextView) itemView.findViewById(R.id.tv_removed_message);
            tv_num_message_viewer = (TextView) itemView.findViewById(R.id.tv_num_message_viewer);
            tv_audio_duration = (TextView) itemView.findViewById(R.id.tv_duration);
            tv_message_content = (EmojiAppCompatTextView) itemView.findViewById(R.id.tv_message_content);

            tv_instant_message = (TextView) itemView.findViewById(R.id.tv_instant_message);
            tv_instant_message_status = (TextView) itemView.findViewById(R.id.tv_instant_message_status);

            frame_single_media = (FrameLayout) itemView.findViewById(R.id.frame_single_media);
            frame_play_audio = (FrameLayout) itemView.findViewById(R.id.frame_play_audio);

            rv_multiple_media = (RecyclerView) itemView.findViewById(R.id.rv_multiple_media);
            rv_multiple_media.setLayoutManager(new GridLayoutManager(mAdapterContext, 2, GridLayoutManager.VERTICAL, false));
            rv_multiple_media.setItemAnimator(null);

            rv_message_viewer = (RecyclerView) itemView.findViewById(R.id.rv_message_viewer);
            rv_message_viewer.setLayoutManager(new LinearLayoutManager(mAdapterContext, LinearLayoutManager.HORIZONTAL, false));
            rv_message_viewer.setItemAnimator(null);

            linear_message_root = (LinearLayout) itemView.findViewById(R.id.linear_message_root);
            linear_message_viewer = (LinearLayout) itemView.findViewById(R.id.linear_message_viewer);
            linear_audio = (LinearLayout) itemView.findViewById(R.id.linear_audio);

            pb_audio_player = (ProgressBar) itemView.findViewById(R.id.pb_audio_player);
            pb_preparing_audio = (ProgressBar) itemView.findViewById(R.id.pb_preparing_audio);

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

                mPlayingAudioHolder.pb_audio_player.setProgress(current);
                mPlayingAudioHolder.pb_audio_player.postDelayed(this, 500);
                mPlayingAudioHolder.tv_audio_duration.setText(formatSecondsToHours(dur - current));

            }
        }
    }


}
