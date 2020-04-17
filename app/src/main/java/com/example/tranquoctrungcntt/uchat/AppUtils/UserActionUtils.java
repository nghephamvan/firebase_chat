package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.tranquoctrungcntt.uchat.Activities.Chat;
import com.example.tranquoctrungcntt.uchat.Activities.ForwardMessage;
import com.example.tranquoctrungcntt.uchat.Activities.GroupChat;
import com.example.tranquoctrungcntt.uchat.Activities.GroupDetailPage;
import com.example.tranquoctrungcntt.uchat.Activities.Login;
import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.Activities.NoGooglePlayService;
import com.example.tranquoctrungcntt.uchat.Activities.UserProfile;
import com.example.tranquoctrungcntt.uchat.Activities.VideoCallScreen;
import com.example.tranquoctrungcntt.uchat.Activities.ViewMedia;
import com.example.tranquoctrungcntt.uchat.Activities.VoiceCallScreen;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.example.tranquoctrungcntt.uchat.Services.DownloadService;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_BLOCK_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_FRIEND;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_BLOCKED_USER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_I_PREVENTED_THIS_USER_FROM_MAKING_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MUTE_NOTIFICATIONS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_RELATIONSHIPS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEARCH_HISTORY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEND_FRIEND_REQUEST_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SETTINGS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_TOKENS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_UPDATE_PROFILE_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_BLOCKED_ME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALLEE_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_MEDIA_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_MESSAGE_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_PROFILE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MY_AVATAR_COLOR_GENERATOR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENDING;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;


public class UserActionUtils {

    public static void logoutFirebaseUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) FirebaseAuth.getInstance().signOut();
    }

    public static void startDownloadingMedia(Context context, Media media) {

        showLongToast(context, "Đang tải xuống tập tin...!");

        Intent it = new Intent(context, DownloadService.class);
        it.putExtra(DownloadService.DOWNLOAD_TYPE, media.getType());
        it.putExtra(DownloadService.DOWNLOAD_CONTENT, media.getContentUrl());
        context.startService(it);

    }

    public static void keepSyncAll(boolean keepsync) {

        ROOT_REF.child(CHILD_MEDIA).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_SEARCH_HISTORY).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_SEND_FRIEND_REQUEST_TIMER).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_BLOCK_TIMER).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_UPDATE_PROFILE_TIMER).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_TOKENS).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_RELATIONSHIPS).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_SETTINGS).child(getMyFirebaseUserId()).keepSynced(keepsync);

        ROOT_REF.child(CHILD_GROUP_DETAIL).child(getMyFirebaseUserId()).keepSynced(keepsync);

    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager methodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (methodManager != null && view != null)
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager methodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (methodManager != null && view != null)
            methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showLongToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public static void showMessageDialog(Context context, String content) {

        if (!((Activity) context).isFinishing()) {

            new AlertDialog.Builder(context)
                    .setMessage(content)
                    .setPositiveButton("OK", null).create().show();

        }
    }

    public static void setMediaUrlToView(Context context, String url, ImageView view) {

        Glide.with(context.getApplicationContext())
                .load(url)
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(view);

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void setAvatarToView(Context context, String avatarUrl, String name, ImageView view) {

        Glide.with(context.getApplicationContext()).clear(view);

        if (avatarUrl != null) {

            Glide.with(context.getApplicationContext())
                    .load(avatarUrl)
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(view);

        } else {

            String firstLetter = name != null ? name.charAt(0) + "" : "U";
            String fullName = name != null ? name.replaceAll(" ", "") : "Uchat";
            TextDrawable avatarDrawable = TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(firstLetter.toUpperCase(), MY_AVATAR_COLOR_GENERATOR.getColor(fullName));

            Glide.with(context.getApplicationContext())
                    .load(drawableToBitmap(avatarDrawable))
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.ic_place_holder)
                    .dontAnimate()
                    .into(view);

        }
    }

    public static void disableChangeAnimation(RecyclerView view) {
        ((SimpleItemAnimator) view.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public static void markMessageIsSent(String messageId, String receiverId, String groupId) {

        final DatabaseReference statusRef = ROOT_REF.child(CHILD_MESSAGES)
                .child(getMyFirebaseUserId()).child(groupId != null ? groupId : receiverId)
                .child(messageId).child(kMessageStatus);

        statusRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                final String currentStatus = dataSnapshot.getValue(String.class);

                if (currentStatus != null) {
                    if (currentStatus.equals(MESSAGE_STATUS_SENDING)) {
                        statusRef.setValue(MESSAGE_STATUS_SENT);
                    }
                }


            }
        });

    }

    public static int searchMessage(ArrayList<Message> messages, String messageId) {

        for (int index = 0; index < messages.size(); index++) {
            if (messages.get(index).getMessageId().equals(messageId)) {
                return index;
            }
        }

        return -1;
    }

    public static int searchUser(ArrayList<User> users, String userId) {

        for (int index = 0; index < users.size(); index++) {
            if (users.get(index).getUserId().equals(userId)) {
                return index;
            }
        }

        return -1;
    }

    public static boolean hasItemInList(int i) {
        return i != -1;
    }

    public static class ForwardAction {

        public static void forwardSingleMedia(Context context, String mediaId, String userId) {

            Intent it_forward = new Intent(context, ForwardMessage.class);
            it_forward.putExtra(INTENT_KEY_MEDIA_ID, mediaId);
            it_forward.putExtra(INTENT_KEY_USER_ID, userId);
            context.startActivity(it_forward);

        }

        public static void forwardGroupMedia(Context context, String mediaId, String groupId) {
            Intent it_forward = new Intent(context, ForwardMessage.class);
            it_forward.putExtra(INTENT_KEY_MEDIA_ID, mediaId);
            it_forward.putExtra(INTENT_KEY_GROUP_ID, groupId);
            context.startActivity(it_forward);

        }

        public static void forwardSingleMessage(Context context, String messageId, String userId) {

            Intent it = new Intent(context, ForwardMessage.class);
            it.putExtra(INTENT_KEY_MESSAGE_ID, messageId);
            it.putExtra(INTENT_KEY_USER_ID, userId);
            context.startActivity(it);

        }

        public static void forwardGroupMessage(Context context, String messageId, String groupId) {

            Intent it = new Intent(context, ForwardMessage.class);
            it.putExtra(INTENT_KEY_MESSAGE_ID, messageId);
            it.putExtra(INTENT_KEY_GROUP_ID, groupId);
            context.startActivity(it);

        }

    }

    public static class ViewAction {

        public static void viewUserProfile(Context context, String userId) {

            Intent it = new Intent(context, UserProfile.class);
            it.putExtra(INTENT_KEY_USER_ID, userId);
            context.startActivity(it);

        }

        public static void viewGroupDetail(Context context, String groupId) {
            Intent it = new Intent(context, GroupDetailPage.class);
            it.putExtra(INTENT_KEY_GROUP_ID, groupId);
            context.startActivity(it);
        }

        public static void viewAvatar(Context context, String avatarUrl) {

            if (avatarUrl != null) {

                final Dialog singlePhotoDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_ActionBar);
                singlePhotoDialog.setCanceledOnTouchOutside(false);

                singlePhotoDialog.setContentView(R.layout.dialog_view_avatar);
                PhotoView photoView = (PhotoView) singlePhotoDialog.findViewById(R.id.full_size_avatar);
                ImageView btn_close = (ImageView) singlePhotoDialog.findViewById(R.id.img_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        singlePhotoDialog.dismiss();
                    }
                });

                ProgressBar progressBar = singlePhotoDialog.findViewById(R.id.pb_loading);

                Glide.with(context.getApplicationContext())
                        .load(avatarUrl)
                        .thumbnail(0.5f)
                        .error(R.drawable.ic_place_holder)
                        .placeholder(R.drawable.ic_place_holder)
                        .fitCenter()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                        boolean isFirstResource) {

                                progressBar.setVisibility(View.GONE);
                                photoView.setVisibility(View.VISIBLE);

                                photoView.setImageResource(R.drawable.ic_place_holder);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {

                                progressBar.setVisibility(View.GONE);
                                photoView.setVisibility(View.VISIBLE);

                                return false;
                            }

                        }).into(photoView);


                singlePhotoDialog.show();


            } else showMessageDialog(context, "Chưa có ảnh đại diện !");
        }

        public static void viewThisSingleUserMedia(Context context, String mediaId, String userId) {
            Intent it = new Intent(context, ViewMedia.class);
            it.putExtra(INTENT_KEY_MEDIA_ID, mediaId);
            it.putExtra(INTENT_KEY_USER_ID, userId);
            context.startActivity(it);
        }

        public static void viewThisGroupMedia(Context context, String mediaId, String groupId) {

            Intent it = new Intent(context, ViewMedia.class);
            it.putExtra(INTENT_KEY_MEDIA_ID, mediaId);
            it.putExtra(INTENT_KEY_GROUP_ID, groupId);
            context.startActivity(it);
        }

    }

    public static class LinkAction {

        public static void openWebBrowser(Context context, String url) {
            new FinestWebView.Builder(((Activity) context))
                    .toolbarColor(context.getResources().getColor(R.color.white))
                    .titleColor(context.getResources().getColor(R.color.black))
                    .statusBarColor(context.getResources().getColor(R.color.white))
                    .iconDefaultColor(context.getResources().getColor(R.color.colorPrimary))
                    .iconDisabledColor(context.getResources().getColor(R.color.colorPrimary))
                    .iconPressedColor(context.getResources().getColor(R.color.colorPrimary))
                    .stringResOpenWith(R.string.openwithchrome)
                    .stringResCopiedToClipboard(R.string.copied)
                    .stringResCopyLink(R.string.copylink)
                    .stringResRefresh(R.string.refresh)
                    .stringResShareVia(R.string.share)
                    .urlColor(context.getResources().getColor(R.color.dark_grey))
                    .show(url);
        }

        public static void sendEmail(Context context, String email) {
            Intent emailIntent;
            emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hổ trợ Uchat");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Xin chào !");
            context.startActivity(emailIntent);
        }

        public static void openMap(Context context, String address) {
            Uri gmmIntentUri = Uri.parse(address);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }

    }

    public static class SettingAction {
        public static void preventNotifyMe(Context context, String userId) {

            if (isConnectedToFirebaseService(context)) {
                ROOT_REF.child(CHILD_SETTINGS)
                        .child(getMyFirebaseUserId()).child(userId)
                        .child(CHILD_MUTE_NOTIFICATIONS).setValue(true);
            } else showNoConnectionDialog(context);

        }

        public static void allowNotifyMe(Context context, String userId) {
            if (isConnectedToFirebaseService(context)) {
                ROOT_REF.child(CHILD_SETTINGS)
                        .child(getMyFirebaseUserId()).child(userId)
                        .child(CHILD_MUTE_NOTIFICATIONS).removeValue();
            } else showNoConnectionDialog(context);


        }

        public static void preventMakeGroup(Context context, String userId) {

            if (isConnectedToFirebaseService(context)) {

                Map<String, Object> map = new HashMap<>();

                map.put("/" + CHILD_SETTINGS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_I_PREVENTED_THIS_USER_FROM_MAKING_GROUP, true);
                map.put("/" + CHILD_SETTINGS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP, true);

                ROOT_REF.updateChildren(map);

            } else showNoConnectionDialog(context);


        }

        public static void allowMakeGroup(Context context, String userId) {

            if (isConnectedToFirebaseService(context)) {

                Map<String, Object> map = new HashMap<>();

                map.put("/" + CHILD_SETTINGS + "/" + getMyFirebaseUserId() + "/" + userId + "/" + CHILD_I_PREVENTED_THIS_USER_FROM_MAKING_GROUP, null);
                map.put("/" + CHILD_SETTINGS + "/" + userId + "/" + getMyFirebaseUserId() + "/" + CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP, null);

                ROOT_REF.updateChildren(map);

            } else showNoConnectionDialog(context);


        }

    }

    public static class IntentAction {
        public static void goToLoginScreen(Activity activity) {
            Intent it = new Intent(activity, Login.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(it);
            activity.finish();
        }

        public static void goToHomeScreen(Activity activity) {
            Intent it = new Intent(activity, MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(it);
            activity.finish();
        }

        public static void goToNoGGPlayServiceScreenIfNeeded(Activity activity) {
            Intent it = new Intent(activity, NoGooglePlayService.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(it);
            activity.finish();
        }

        public static void makeCall(Context context, String userIdToCall, boolean isVideoCall) {

            if (isConnectedToFirebaseService(context)) {

                ROOT_REF.child(CHILD_RELATIONSHIPS)
                        .child(getMyFirebaseUserId()).child(userIdToCall)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild(CHILD_USER_BLOCKED_ME) || dataSnapshot.hasChild(CHILD_I_BLOCKED_USER)) {

                                    showMessageDialog(context, "Bạn tạm thời không thể gọi cho người này !");

                                } else if (dataSnapshot.hasChild(CHILD_FRIEND)) {

                                    Intent it = new Intent(context, isVideoCall ? VideoCallScreen.class : VoiceCallScreen.class);

                                    it.putExtra(INTENT_KEY_CALLEE_ID, userIdToCall);

                                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    context.startActivity(it);

                                } else showMessageDialog(context, "Bạn phải kết bạn với người này để có thể gọi điện !");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            } else showNoConnectionDialog(context);

        }

        public static void goToChat(Context context, User user) {
            Intent it = new Intent(context, Chat.class);
            it.putExtra(INTENT_KEY_USER_PROFILE, user);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(it);
        }

        public static void goToGroupChat(Context context, GroupDetail groupDetail) {
            Intent it = new Intent(context, GroupChat.class);
            it.putExtra(INTENT_KEY_GROUP_DETAIL, groupDetail);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(it);
        }
    }
}
