package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.OtherClasses.FullScreenViewPager;
import com.example.tranquoctrungcntt.uchat.PagerAdapters.FullScreenViewPagerAdapter;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_MEDIA_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_VIDEO;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.startDownloadingMedia;


public class ViewMedia extends BaseActivity {

    private Toolbar mToolbar;

    private Jzvd.JZAutoFullscreenListener mSensorEventListener;

    private SensorManager mSensorManager;

    private FullScreenViewPager vp_fullscreen;
    private ArrayList<Media> mMediaList;
    private FullScreenViewPagerAdapter mMediaAdapter;

    private AlertDialog mDetailDialog;

    private CircleImageView civ_avatar;

    private TextView tv_sender_name;
    private TextView tv_media_send_date;
    private TextView tv_media_type;
    private TextView tv_media_send_time;
    private TextView tv_media_name;

    private ChildEventListener mMediaChildEvent;
    private DatabaseReference mMediaRef;

    private String mMediaId;
    private String mUserOrGroupId;
    private String mIntentKey;
    private String mShowingMediaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_view_media);

        if (mMediaChildEvent != null && mMediaRef != null) mMediaRef.removeEventListener(mMediaChildEvent);

        initViews();

        initDetailDialog();

        mMediaId = (String) getDataFromIntent(ViewMedia.this, INTENT_KEY_MEDIA_ID);

        if (mMediaId != null) {

            final String userId = (String) getDataFromIntent(ViewMedia.this, INTENT_KEY_USER_ID);
            final String groupId = (String) getDataFromIntent(ViewMedia.this, INTENT_KEY_GROUP_ID);

            if (userId != null) {

                mIntentKey = INTENT_KEY_USER_ID;

                mUserOrGroupId = userId;

                mMediaRef = ROOT_REF.child(CHILD_MEDIA).child(getMyFirebaseUserId()).child(mUserOrGroupId);

                loadMedia();

            } else if (groupId != null) {

                mIntentKey = INTENT_KEY_GROUP_ID;

                mUserOrGroupId = groupId;

                mMediaRef = ROOT_REF.child(CHILD_MEDIA).child(getMyFirebaseUserId()).child(mUserOrGroupId);

                loadMedia();

            } else finish();

        } else finish();


    }


    public void showOrHideToolbar() {
        if (mToolbar.getVisibility() == View.VISIBLE) {
            mToolbar.setVisibility(View.GONE);
        } else mToolbar.setVisibility(View.VISIBLE);
    }

    private void initViews() {

        mShowingMediaId = null;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new Jzvd.JZAutoFullscreenListener();

        mMediaList = new ArrayList<>();
        mMediaAdapter = new FullScreenViewPagerAdapter(ViewMedia.this, mMediaList);

        vp_fullscreen = (FullScreenViewPager) findViewById(R.id.vp_fullscreen);
        vp_fullscreen.setAdapter(mMediaAdapter);
        vp_fullscreen.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { JzvdStd.goOnPlayOnPause(); }

            @Override
            public void onPageSelected(int i) { mShowingMediaId = mMediaList.get(i).getMediaId(); }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });

    }

    private void loadMedia() {

        mMediaChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Media media = dataSnapshot.getValue(Media.class);

                if (media.getType() != MEDIA_TYPE_AUDIO) {

                    mMediaList.add(media);
                    mMediaAdapter.notifyDataSetChanged();

                    if (mMediaList.get(mMediaList.size() - 1).getMediaId().equals(mMediaId))
                        vp_fullscreen.setCurrentItem(mMediaList.size() - 1, false);

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                if (mShowingMediaId != null && mShowingMediaId.equals(dataSnapshot.getKey())) {

                    if (mDetailDialog.isShowing()) mDetailDialog.dismiss();

                    finish();

                } else {

                    for (int index = 0; index < mMediaList.size(); index++) {

                        if (mMediaList.get(index).getMediaId().equals(dataSnapshot.getKey())) {

                            mMediaList.remove(index);
                            mMediaAdapter.notifyDataSetChanged();

                            if (mMediaList.isEmpty()) finish();

                            break;
                        }
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

    }


    @Override
    public void onBackPressed() {

        if (JzvdStd.backPress()) return;

        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaChildEvent != null && mMediaRef != null) mMediaRef.removeEventListener(mMediaChildEvent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSensorManager.unregisterListener(mSensorEventListener);

        Jzvd.releaseAllVideos();

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (mMediaList.get(vp_fullscreen.getCurrentItem()).getType() == MEDIA_TYPE_VIDEO)
                mToolbar.setVisibility(View.GONE);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (mMediaList.get(vp_fullscreen.getCurrentItem()).getType() == MEDIA_TYPE_VIDEO)
                mToolbar.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                JzvdStd.releaseAllVideos();

                finish();

                break;

            case R.id.item_forward:

                if (mIntentKey.equals(INTENT_KEY_GROUP_ID))

                    forwardGroupMedia(ViewMedia.this,
                            mMediaList.get(vp_fullscreen.getCurrentItem()).getMediaId(), mUserOrGroupId);

                else if (mIntentKey.equals(INTENT_KEY_USER_ID))

                    forwardSingleMedia(ViewMedia.this,
                            mMediaList.get(vp_fullscreen.getCurrentItem()).getMediaId(), mUserOrGroupId);


                break;
            case R.id.item_download:
                if (Build.VERSION.SDK_INT >= 23) {

                    if (isDownloadMediaPermissionsGranted(ViewMedia.this)) {

                        startDownloadingMedia(ViewMedia.this, mMediaList.get(vp_fullscreen.getCurrentItem()));

                    } else ActivityCompat.requestPermissions(ViewMedia.this,
                            PERMISSIONS_ACCESS_GALLERY, DOWNLOAD_MEDIA_CODE);

                } else
                    startDownloadingMedia(ViewMedia.this, mMediaList.get(vp_fullscreen.getCurrentItem()));

                break;

            case R.id.item_detail:

                Media media = mMediaList.get(vp_fullscreen.getCurrentItem());

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
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDetailDialog() {
        View view = LayoutInflater.from(ViewMedia.this).inflate(R.layout.dialog_media_detail, null);


        civ_avatar = (CircleImageView) view.findViewById(R.id.civ_avatar);
        tv_sender_name = (TextView) view.findViewById(R.id.tv_sender_name);
        tv_media_send_date = (TextView) view.findViewById(R.id.tv_send_date);
        tv_media_type = (TextView) view.findViewById(R.id.tv_media_type);
        tv_media_send_time = (TextView) view.findViewById(R.id.tv_send_time);
        tv_media_name = (TextView) view.findViewById(R.id.tv_media_name);

        mDetailDialog = new AlertDialog.Builder(ViewMedia.this)
                .setTitle("Chi tiết")
                .setView(view)
                .setPositiveButton("Đóng", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mShowingMediaId = null;
                    }
                }).create();
    }

    private void showMediaDetailDialog(Media media, String name, String avatar) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(media.getSendTime());

        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String send_date = formatSmallNumber(date) + "/" + formatSmallNumber(month) + "/" + year;
        String send_time = formatSmallNumber(hour) + ":" + formatSmallNumber(minute);

        setAvatarToView(ViewMedia.this, avatar, name, civ_avatar);

        if (media.getSenderId().equals(getMyFirebaseUserId())) {
            tv_sender_name.setText(name + " (bạn)");
        } else tv_sender_name.setText(name);

        tv_media_send_date.setText(send_date);
        tv_media_send_time.setText(send_time);
        tv_media_name.setText(media.getMediaName());

        if (media.getType() == MEDIA_TYPE_PICTURE)
            tv_media_type.setText("Hình ảnh");
        else tv_media_type.setText("Video");


        mDetailDialog.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == DOWNLOAD_MEDIA_CODE) {

            if (isAllPermissionsGrantedInResult(grantResults)) {

                startDownloadingMedia(ViewMedia.this, mMediaList.get(vp_fullscreen.getCurrentItem()));

            } else showDownloadMediaRequestPermissionDialog(ViewMedia.this);
        }


    }
}
