package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.SearchMessage;
import com.example.tranquoctrungcntt.uchat.DialogAdapters.EditHistoryAdapter;
import com.example.tranquoctrungcntt.uchat.DialogAdapters.MessageViewerAdapter;
import com.example.tranquoctrungcntt.uchat.Models.MessageViewerModel;
import com.example.tranquoctrungcntt.uchat.Objects.EditHistory;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.MessageViewer;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_RECEIVED;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SEEN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_TEXT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isEmailValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isMapAddress;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isPhoneNumber;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.copyContent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSendTime;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeDivider;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardGroupMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardSingleMessage;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.openMap;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.openWebBrowser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.sendEmail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;


public class SearchMessageAdapter extends RecyclerView.Adapter<SearchMessageAdapter.SearchMessageViewHolder> {

    private static final int TYPE_GROUP = 1;
    private static final int TYPE_ME = 0;
    private final SearchMessage mAdapterContext;
    private final List<Message> mList;
    private final Map<String, User> mUserProfileMap;
    private final BottomSheetDialog mOptionsDialog;
    private final String mUserOrGroupId;
    private final boolean isGroup;
    private SearchMessageViewHolder mSelectedBackgroundHolder;
    private SearchMessageViewHolder mCurrentStatusViewHolder;
    private String mCurrentStatusViewId;
    private String mShowingOptionMessageId;
    private String mShowingPhoneNumberOptionMessageId;
    private String mShowingEditHistoryMessageId;
    private AlertDialog mPhoneNumberOptionDialog;
    private AlertDialog mEditHistoryDialog;

    public SearchMessageAdapter(SearchMessage mAdapterContext, List<Message> mList, Map<String, User> mUserProfileMap, String mUserOrGroupId, boolean isGroup) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mUserProfileMap = mUserProfileMap;
        this.mUserOrGroupId = mUserOrGroupId;
        this.isGroup = isGroup;

        mCurrentStatusViewId = null;
        mCurrentStatusViewHolder = null;

        mSelectedBackgroundHolder = null;

        mShowingOptionMessageId = null;

        mShowingPhoneNumberOptionMessageId = null;
        mPhoneNumberOptionDialog = null;

        mEditHistoryDialog = null;
        mShowingEditHistoryMessageId = null;

        mOptionsDialog = new BottomSheetDialog(mAdapterContext);
        mOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mOptionsDialog.setContentView(R.layout.dialog_search_message_options);
        mOptionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if (mSelectedBackgroundHolder != null) {

                    mSelectedBackgroundHolder.tv_message_content.setSelected(false);

                    mSelectedBackgroundHolder = null;
                    mShowingOptionMessageId = null;

                }

            }
        });


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
    public SearchMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewtype == TYPE_ME ? R.layout.row_search_message_right : R.layout.row_search_message_left, viewGroup, false);

        return new SearchMessageViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final SearchMessageViewHolder mViewHolder, final int mIndex) {

        int viewType = getItemViewType(mIndex);

        final Message message = (Message) mList.get(mIndex);

        mViewHolder.tv_message_content.setText(message.getContent());

        mViewHolder.tv_send_time.setText(formatSendTime(message.getSendTime()));

        formatMessageStatusGroup(mViewHolder, mIndex);

        if (viewType == TYPE_GROUP) checkToShowAvatar(mViewHolder, mIndex);

        if (message.getEditHistory() != null)
            mViewHolder.img_edited.setVisibility(View.VISIBLE);
        else mViewHolder.img_edited.setVisibility(View.GONE);

        checkToShowTimeDivider(mViewHolder, mIndex);

        onClickMessage(mViewHolder, mIndex);

        keepSelectedView(message, mViewHolder);

    }

    private void keepSelectedView(Message message, SearchMessageViewHolder viewholder) {

        if (mCurrentStatusViewId != null && mCurrentStatusViewId.equals(message.getMessageId())) {

            viewholder.tv_status.setVisibility(View.VISIBLE);
            viewholder.tv_send_time.setVisibility(View.VISIBLE);
            viewholder.tv_message_content.setSelected(true);

        } else {

            viewholder.tv_status.setVisibility(View.GONE);
            viewholder.tv_send_time.setVisibility(View.GONE);
            viewholder.tv_message_content.setSelected(false);


        }
    }


    private void checkToShowAvatar(SearchMessageViewHolder viewholder, int indexToCheck) {

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

                if (preType == currentType && (currentType == MESSAGE_TYPE_TEXT) && preHour == currentHour) {

                    viewholder.civ_avatar.setVisibility(View.GONE);
                    viewholder.tv_name.setVisibility(View.GONE);

                } else {

                    loadUserProfile(viewholder, currentMessage.getSenderId());
                    viewholder.civ_avatar.setVisibility(View.VISIBLE);
                    viewholder.tv_name.setVisibility(View.VISIBLE);

                }

            } else {

                loadUserProfile(viewholder, currentMessage.getSenderId());
                viewholder.civ_avatar.setVisibility(View.VISIBLE);
                viewholder.tv_name.setVisibility(View.VISIBLE);

            }


        } else {

            loadUserProfile(viewholder, currentMessage.getSenderId());
            viewholder.civ_avatar.setVisibility(View.VISIBLE);
            viewholder.tv_name.setVisibility(View.VISIBLE);

        }


    }

    private void checkToShowTimeDivider(SearchMessageViewHolder viewholder, int index) {


        final Message message = (Message) mList.get(index);

        if (index == 0) {

            Calendar messageCalendar = Calendar.getInstance();

            messageCalendar.setTimeInMillis(message.getSendTime());

            int send_date = messageCalendar.get(Calendar.DATE);
            int send_month = messageCalendar.get(Calendar.MONTH) + 1;
            int send_year = messageCalendar.get(Calendar.YEAR);

            viewholder.tv_time_divider.setVisibility(View.VISIBLE);
            viewholder.tv_time_divider.setText("NGÀY " + send_date + " THÁNG " + send_month + " NĂM " + send_year);

        } else {


            final Message preMessage = (Message) mList.get(index - 1);

            final String shouldDiv = (String) formatTimeDivider(preMessage, message);

            viewholder.tv_time_divider.setVisibility(shouldDiv != null ? View.VISIBLE : View.GONE);
            viewholder.tv_time_divider.setText(shouldDiv);


        }
    }

    // ---------------------CONTENT PROCESSING-----------------------//

    private View.OnClickListener clickForward(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isGroup) forwardGroupMessage(mAdapterContext, message.getMessageId(), mUserOrGroupId);
                else forwardSingleMessage(mAdapterContext, message.getMessageId(), mUserOrGroupId);

                mOptionsDialog.dismiss();

            }
        };
    }

    private void deleteMessage(Message message) {
        ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(mUserOrGroupId)
                .child(message.getMessageId()).removeValue();
    }

    private View.OnClickListener clickDetele(Message message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteMessage(message);

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

    private void showOptions(SearchMessageViewHolder viewholder, Message message) {

        LinearLayout btn_forward = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_forward);

        LinearLayout btn_copy = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_copy);

        LinearLayout btn_delete = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_delete);

        LinearLayout btn_edit_history = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_edit_history);

        LinearLayout btn_message_viewer = (LinearLayout) mOptionsDialog.findViewById(R.id.linear_message_viewer);

        mSelectedBackgroundHolder = null;

        btn_delete.setVisibility(View.VISIBLE);
        btn_delete.setOnClickListener(clickDetele(message));

        if (message.getMessageViewer() != null) {
            btn_message_viewer.setVisibility(View.VISIBLE);
            btn_message_viewer.setOnClickListener(clickMessageViewer(message));
        } else {
            btn_message_viewer.setVisibility(View.GONE);
            btn_message_viewer.setOnClickListener(null);
        }

        mSelectedBackgroundHolder = viewholder;
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

    private void loadUserProfile(SearchMessageViewHolder viewholder, String userId) {


        if (mUserProfileMap.get(userId) != null) {

            User user = mUserProfileMap.get(userId);

            setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), viewholder.civ_avatar);

            viewholder.tv_name.setText(user.getName());

        } else {

            ROOT_REF.child(CHILD_USERS).child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            User user = dataSnapshot.getValue(User.class);

                            mUserProfileMap.put(userId, user);

                            setAvatarToView(mAdapterContext, user.getThumbAvatarUrl(), user.getName(), viewholder.civ_avatar);

                            viewholder.tv_name.setText(user.getName());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }


    }

    private void loadMessageViewerDetailList(Map<String, MessageViewer> messageViewerMap) {

        if (messageViewerMap == null) {

            showMessageDialog(mAdapterContext, "Chưa có thành viên nào xem tin nhắn");

        } else {

            final ArrayList<MessageViewerModel> messageViewerProfileList = new ArrayList<>();

            for (MessageViewer messageViewer : messageViewerMap.values()) {

                if (mUserProfileMap.get(messageViewer.getViewerId()) != null) {

                    final MessageViewerModel messageViewerModel = new MessageViewerModel(mUserProfileMap.get(messageViewer.getViewerId()), messageViewer.getViewTime());

                    messageViewerProfileList.add(0, messageViewerModel);

                    if (messageViewerProfileList.size() == messageViewerMap.size()) showMessageViewerDetailDialog(messageViewerProfileList);


                } else {


                    ROOT_REF.child(CHILD_USERS).child(messageViewer.getViewerId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotUser) {

                                    mUserProfileMap.put(messageViewer.getViewerId(), snapshotUser.getValue(User.class));

                                    final MessageViewerModel messageViewerModel = new MessageViewerModel(mUserProfileMap.get(messageViewer.getViewerId()), messageViewer.getViewTime());

                                    messageViewerProfileList.add(0, messageViewerModel);

                                    if (messageViewerProfileList.size() == messageViewerMap.size())
                                        showMessageViewerDetailDialog(messageViewerProfileList);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void formatMessageStatusGroup(SearchMessageViewHolder viewholder, int indexToFormat) {

        final Message messageToFormat = mList.get(indexToFormat);

        switch (messageToFormat.getStatus()) {

            case MESSAGE_STATUS_SENDING:

                viewholder.tv_status.setText("Đang gửi");


                break;

            case MESSAGE_STATUS_SENT:

                viewholder.tv_status.setText("Đã gửi");


                break;
            case MESSAGE_STATUS_RECEIVED:

                viewholder.tv_status.setText("Đã nhận");

                break;

            case MESSAGE_STATUS_SEEN:


                if (isGroup) {

                    if (messageToFormat.getMessageViewer() != null) {

                        viewholder.tv_status.setText(messageToFormat.getMessageViewer().size() + " người đã xem");

                    } else viewholder.tv_status.setText(null);

                } else viewholder.tv_status.setText("Đã xem");

                break;
        }


    }


    public void hideStatusView(SearchMessageViewHolder messageViewHolder) {

        Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                messageViewHolder.tv_status.setVisibility(View.GONE);
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

        messageViewHolder.tv_message_content.setSelected(false);

        mCurrentStatusViewHolder = null;
        mCurrentStatusViewId = null;

    }

    private void onClickMessage(SearchMessageViewHolder messageViewHolder, int clickedIndex) {

        final Message message = (Message) mList.get(clickedIndex);

        View.OnClickListener showOrHideStatusClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (messageViewHolder.tv_send_time.getVisibility() == View.VISIBLE
                        && messageViewHolder.tv_status.getVisibility() == View.VISIBLE) {

                    hideStatusView(messageViewHolder);

                } else {

                    Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            messageViewHolder.tv_status.setVisibility(View.VISIBLE);
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

                    messageViewHolder.tv_message_content.setSelected(true);

                    if (mCurrentStatusViewId != null && mCurrentStatusViewHolder != null) hideStatusView(mCurrentStatusViewHolder);

                    mCurrentStatusViewHolder = messageViewHolder;
                    mCurrentStatusViewId = message.getMessageId();

                }

            }
        };

        messageViewHolder.tv_message_content.setOnClickListener(showOrHideStatusClick);

        View.OnLongClickListener showOptionClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showOptions(messageViewHolder, message);
                return true;
            }
        };

        messageViewHolder.tv_message_content.setOnLongClickListener(showOptionClick);

        clickLinkInContent(message.getMessageId(), messageViewHolder.tv_message_content);

    }

    private void clickLinkInContent(String messageId, EmojiAppCompatTextView view) {
        BetterLinkMovementMethod.linkify(Linkify.ALL, view)
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

    public void stopActionWithThisMessage(Message message) {

        getNotificationManager(mAdapterContext).cancel(message.getNotificationId());

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


        if (mCurrentStatusViewId != null
                && mCurrentStatusViewHolder != null
                && mCurrentStatusViewId.equals(message.getMessageId()))
            hideStatusView(mCurrentStatusViewHolder);

    }

    public class SearchMessageViewHolder extends RecyclerView.ViewHolder {

        final CircleImageView civ_avatar;
        final ImageView img_edited;

        final TextView tv_send_time;
        final TextView tv_status;
        final TextView tv_name;
        final TextView tv_time_divider;
        final EmojiAppCompatTextView tv_message_content;


        public SearchMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            civ_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            img_edited = (ImageView) itemView.findViewById(R.id.img_edited);

            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
            tv_time_divider = (TextView) itemView.findViewById(R.id.tv_time_divider);
            tv_status = (TextView) itemView.findViewById(R.id.tv_message_status);


            tv_message_content = (EmojiAppCompatTextView) itemView.findViewById(R.id.tv_message_content);


        }

    }

}
