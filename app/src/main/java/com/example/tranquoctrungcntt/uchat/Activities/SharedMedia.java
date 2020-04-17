package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.SharedMediaAdapter;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.DOWNLOAD_MEDIA_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_ACCESS_GALLERY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getSingleUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isDownloadMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showDownloadMediaRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSmallNumber;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardGroupMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ForwardAction.forwardSingleMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewThisGroupMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewThisSingleUserMedia;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.startDownloadingMedia;


public class SharedMedia extends BaseActivity {

    private Toolbar mToolbar;

    private RecyclerView rv_media;
    private ArrayList<Media> mMediaList;
    private SharedMediaAdapter mMediaAdapter;

    private AlertDialog mOptionsDialog;
    private AlertDialog mMediaDetailDialog;

    private CircleImageView civ_avatar;

    private TextView tv_sender_name;
    private TextView tv_send_date;
    private TextView tv_media_type;
    private TextView tv_send_time;
    private TextView tv_media_name;

    private ChildEventListener mMediaChildEvent;
    private DatabaseReference mMediaRef;

    private Media mShowingOptionMedia;

    private String mChattingId;
    private String mIntentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_media);

        if (mMediaChildEvent != null && mMediaRef != null) mMediaRef.removeEventListener(mMediaChildEvent);

        initViews();

        initDetailDialog();


        if (getDataFromIntent(this, INTENT_KEY_USER_ID) != null) {

            mIntentKey = INTENT_KEY_USER_ID;

        } else if (getDataFromIntent(this, INTENT_KEY_GROUP_ID) != null) {

            mIntentKey = INTENT_KEY_GROUP_ID;

        } else mIntentKey = null;


        if (mIntentKey != null) {

            mChattingId = (String) getDataFromIntent(this, mIntentKey);

            mMediaRef = ROOT_REF.child(CHILD_MEDIA).child(getMyFirebaseUserId()).child(mChattingId);

            mMediaChildEvent = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Media media = dataSnapshot.getValue(Media.class);


                    if (media.getType() != MEDIA_TYPE_AUDIO) {

                        mMediaList.add(media);
                        mMediaAdapter.notifyItemInserted(mMediaList.size() - 1);
                        getSupportActionBar().setSubtitle(mMediaList.size() + " ảnh và video");

                    }


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                    if (mShowingOptionMedia != null && dataSnapshot.getKey().equals(mShowingOptionMedia.getMediaId())) {

                        if (mOptionsDialog != null && mOptionsDialog.isShowing()) mOptionsDialog.dismiss();

                        if (mMediaDetailDialog != null && mMediaDetailDialog.isShowing()) mMediaDetailDialog.dismiss();

                        mShowingOptionMedia = null;
                    }

                    for (int index = 0; index < mMediaList.size(); index++) {

                        if (mMediaList.get(index).getMediaId().equals(dataSnapshot.getKey())) {

                            mMediaList.remove(index);
                            mMediaAdapter.notifyItemRemoved(index);
                            mMediaAdapter.notifyItemRangeChanged(index, mMediaAdapter.getItemCount());

                            getSupportActionBar().setSubtitle(mMediaList.size() + " ảnh và video");

                            break;

                        }
                    }

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mMediaRef.addChildEventListener(mMediaChildEvent);

        } else finish();

    }

    private void initViews() {

        mShowingOptionMedia = null;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ảnh chung");

        mMediaList = new ArrayList<>();

        mMediaAdapter = new SharedMediaAdapter(SharedMedia.this, mMediaList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {

                if (mMediaList.get(position).getContentUrl() != null) {

                    if (mIntentKey.equals(INTENT_KEY_USER_ID))

                        viewThisSingleUserMedia(SharedMedia.this, mMediaList.get(position).getMediaId(), mChattingId);

                    else viewThisGroupMedia(SharedMedia.this, mMediaList.get(position).getMediaId(), mChattingId);

                }
            }

            @Override
            public void OnItemLongClick(View v, int position) {
                showOptions(mMediaList.get(position));
            }


        });


        rv_media = (RecyclerView) findViewById(R.id.rv_media);
        rv_media.setHasFixedSize(true);
        rv_media.setLayoutManager(new GridLayoutManager(SharedMedia.this, 4, GridLayoutManager.VERTICAL, false));
        rv_media.setAdapter(mMediaAdapter);
        rv_media.setItemAnimator(null);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaChildEvent != null && mMediaRef != null) mMediaRef.removeEventListener(mMediaChildEvent);

    }

    private void showOptions(Media media) {

        mShowingOptionMedia = media;

        ArrayList<String> options = new ArrayList<>();

        final String option0 = "Xem chi tiết";
        final String option1 = "Chuyển tiếp";
        final String option2 = "Tải về";
        final String option3 = "Hủy";

        options.add(option0);
        options.add(option1);
        options.add(option2);
        options.add(option3);

        ArrayAdapter<String> optionAdapter = new ArrayAdapter(SharedMedia.this, android.R.layout.simple_list_item_1, options);

        mOptionsDialog = new AlertDialog.Builder(SharedMedia.this)
                .setTitle("Tuỳ chọn")
                .setAdapter(optionAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:

                                if (media.getSenderId().equals(getMyFirebaseUserId())) {

                                    getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                        @Override
                                        public void OnCallBack(User callbackUserProfile) {
                                            showMediaDetailDialog(media, callbackUserProfile.getName(), callbackUserProfile.getThumbAvatarUrl());
                                        }
                                    });

                                } else {

                                    getSingleUserProfile(media.getSenderId(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                                        @Override
                                        public void OnCallBack(User callbackUserProfile) {
                                            showMediaDetailDialog(media, callbackUserProfile.getName(), callbackUserProfile.getThumbAvatarUrl());

                                        }
                                    });

                                }

                                break;

                            case 1:

                                if (mIntentKey.equals(INTENT_KEY_GROUP_ID))

                                    forwardGroupMedia(SharedMedia.this, media.getMediaId(), mChattingId);

                                else if (mIntentKey.equals(INTENT_KEY_USER_ID))

                                    forwardSingleMedia(SharedMedia.this, media.getMediaId(), mChattingId);

                                break;

                            case 2:

                                if (Build.VERSION.SDK_INT >= 23) {

                                    if (isDownloadMediaPermissionsGranted(SharedMedia.this)) {

                                        startDownloadingMedia(SharedMedia.this, media);

                                    } else ActivityCompat.requestPermissions(SharedMedia.this,
                                            PERMISSIONS_ACCESS_GALLERY, DOWNLOAD_MEDIA_CODE);

                                } else startDownloadingMedia(SharedMedia.this, media);

                                break;

                            default:

                        }

                    }
                }).create();

        mOptionsDialog.show();

    }

    private void initDetailDialog() {

        View view = LayoutInflater.from(SharedMedia.this).inflate(R.layout.dialog_media_detail, null);

        civ_avatar = (CircleImageView) view.findViewById(R.id.civ_avatar);
        tv_sender_name = (TextView) view.findViewById(R.id.tv_sender_name);
        tv_send_date = (TextView) view.findViewById(R.id.tv_send_date);
        tv_media_type = (TextView) view.findViewById(R.id.tv_media_type);
        tv_send_time = (TextView) view.findViewById(R.id.tv_send_time);
        tv_media_name = (TextView) view.findViewById(R.id.tv_media_name);

        mMediaDetailDialog = new AlertDialog.Builder(SharedMedia.this)
                .setTitle("Chi tiết")
                .setView(view).setPositiveButton("Đóng", null)
                .create();
    }

    private void showMediaDetailDialog(Media media, String name, String avatar) {

        mShowingOptionMedia = media;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(media.getSendTime());

        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String send_date = formatSmallNumber(date) + "/" + formatSmallNumber(month) + "/" + year;
        String send_time = formatSmallNumber(hour) + ":" + formatSmallNumber(minute);

        setAvatarToView(SharedMedia.this, avatar, name, civ_avatar);

        if (media.getSenderId().equals(getMyFirebaseUserId())) {
            tv_sender_name.setText(name + " (bạn)");
        } else tv_sender_name.setText(name);

        tv_send_date.setText(send_date);
        tv_send_time.setText(send_time);

        tv_media_name.setText(media.getMediaName());

        if (media.getType() == MEDIA_TYPE_PICTURE)
            tv_media_type.setText("Hình ảnh");
        else tv_media_type.setText("Video");


        mMediaDetailDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == DOWNLOAD_MEDIA_CODE) {

            if (isAllPermissionsGrantedInResult(grantResults)) {

                startDownloadingMedia(SharedMedia.this, mShowingOptionMedia);

            } else showDownloadMediaRequestPermissionDialog(SharedMedia.this);
        }


    }
}
