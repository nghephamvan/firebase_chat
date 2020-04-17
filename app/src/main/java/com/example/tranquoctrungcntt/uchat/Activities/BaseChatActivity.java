package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.emoji.widget.EmojiAppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.tranquoctrungcntt.uchat.BuildConfig;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.OtherClasses.MyGlideEngine;
import com.example.tranquoctrungcntt.uchat.PagerAdapters.StickerEmojiPagerAdapter;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.HANDLER_LOADMORE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.VIDEO_MAX_SIZE_IN_MB;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.CAPTURE_IMAGE_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_ACCESS_GALLERY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_CAPTURE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PICK_IMAGES_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PICK_VIDEOS_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.RECORD_VIDEO_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setChattingUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isCaptureMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isPickMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSecondsToHours;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showKeyboard;

public class BaseChatActivity extends BaseActivity {

    protected final Handler mRecordHandler = new Handler();

    protected final LoadMoreHandler mLoadMoreHandler = new LoadMoreHandler();

    protected Media mediaToDownload;

    protected ProgressBar pb_sending_media;
    protected ProgressBar pb_sending_audio;
    protected ImageView img_gallery;
    protected ImageView img_microphone;

    protected CardView card_unseen;
    protected TextView tv_num_unseen;

    protected CircleImageView civ_avatar;
    protected CircleImageView civ_muted;
    protected TextView tv_sub_infor;
    protected TextView tv_name;

    protected LinearLayoutManager mLinearLayoutManager;

    protected EmojiAppCompatEditText edt_content; // input content

    protected FrameLayout btn_mms;
    protected FrameLayout btn_record_audio;
    protected FrameLayout btn_send_message;
    protected FrameLayout btn_like;
    protected FrameLayout btn_right_arrow;
    protected FrameLayout btn_back;
    protected FrameLayout btn_more;

    protected LinearLayout linear_send_messages;
    protected LinearLayout linear_new_messages;
    protected LinearLayout linear_audio_recorder;
    protected LinearLayout linear_player_audio_recorder;
    protected LinearLayout linear_loadmore;

    protected boolean isRecording;
    protected int recorderSecond;

    protected String mRecordFilePath;
    protected MediaRecorder mRecorder;
    protected MediaPlayer mRecordPlayer;

    protected ProgressBar pb_audio_recorder;
    protected ProgressBar pb_audio_player;

    protected TextView tv_close_audio_recorder;
    protected TextView tv_timer_audio_recorder;
    protected TextView tv_start_recorder;
    protected TextView tv_send_record;

    protected ImageView img_play_audio_recorder;

    protected String mNewestKey; // keep the new key
    protected String mCheckerKey; // check if there are the same message to not add
    protected String mPicturePath;

    protected boolean isMute;
    protected boolean isLoadingMore;
    protected boolean ableToLoadMore;

    protected BottomSheetDialog mSendMediaOptionsDialog;

    /* EMOJI */

    protected ViewPager vp_stickers_emoji;

    protected LinearLayout linear_stickers_emoji;

    protected TextView tv_tab_stickers;
    protected TextView tv_tab_emoji;

    protected FrameLayout btn_show_emoji;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPicturePath = null;
        mediaToDownload = null;

        mNewestKey = "";
        mCheckerKey = "";

        isLoadingMore = false;
        ableToLoadMore = false;

        mSendMediaOptionsDialog = new BottomSheetDialog(this);
        mSendMediaOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSendMediaOptionsDialog.setContentView(R.layout.dialog_send_media_options);

    }


    protected void initMutualViews() {

        tv_num_unseen = (TextView) findViewById(R.id.tv_num_unseen);
        card_unseen = (CardView) findViewById(R.id.card_unseen);

        civ_muted = (CircleImageView) findViewById(R.id.civ_muted);
        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sub_infor = (TextView) findViewById(R.id.tv_sub_infor);

        pb_sending_media = (ProgressBar) findViewById(R.id.pb_sending_media);
        pb_sending_audio = (ProgressBar) findViewById(R.id.pb_sending_audio);

        img_gallery = (ImageView) findViewById(R.id.img_gallery);
        img_microphone = (ImageView) findViewById(R.id.img_microphone);

        linear_send_messages = (LinearLayout) findViewById(R.id.linear_send_messages);
        linear_new_messages = (LinearLayout) findViewById(R.id.linear_new_messages);
        linear_loadmore = (LinearLayout) findViewById(R.id.linear_loadmore);

        /// audio recorder part

        btn_record_audio = (FrameLayout) findViewById(R.id.frame_record_audio);
        btn_send_message = (FrameLayout) findViewById(R.id.frame_send_messages);
        btn_like = (FrameLayout) findViewById(R.id.frame_like);
        btn_mms = (FrameLayout) findViewById(R.id.frame_mms);
        btn_right_arrow = (FrameLayout) findViewById(R.id.frame_right_arrow);
        btn_back = (FrameLayout) findViewById(R.id.frame_back);

        linear_audio_recorder = (LinearLayout) findViewById(R.id.linear_audio_recorder);
        pb_audio_recorder = (ProgressBar) findViewById(R.id.pb_recording);
        tv_close_audio_recorder = (TextView) findViewById(R.id.tv_close_audio_recorder);
        tv_timer_audio_recorder = (TextView) findViewById(R.id.tv_timer_audio_recorder);
        linear_player_audio_recorder = (LinearLayout) findViewById(R.id.linear_player_audio_recorder);
        img_play_audio_recorder = (ImageView) findViewById(R.id.img_play_audio_recorder);
        pb_audio_player = (ProgressBar) findViewById(R.id.pb_recorder_player);
        tv_start_recorder = (TextView) findViewById(R.id.tv_start_recorder);
        tv_send_record = (TextView) findViewById(R.id.tv_send_record);

        tv_send_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnectedToFirebaseService(BaseChatActivity.this)) {

                    final MediaPlayer mediaPlayer = new MediaPlayer();

                    try {
                        mediaPlayer.setDataSource(mRecordFilePath);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {

                                uploadAndSendAudio(mRecordFilePath, mediaPlayer.getDuration() / 1000);

                                mediaPlayer.stop();

                                mediaPlayer.release();

                                hideAudioRecorder();

                                linear_send_messages.setVisibility(View.VISIBLE);
                            }
                        });

                    } catch (Exception e) { mediaPlayer.release(); }

                } else showNoConnectionDialog(BaseChatActivity.this);

            }
        });

        tv_start_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isRecording) {

                    try {

                        initAudioRecorderView();

                        mRecordFilePath = getExternalCacheDir().getAbsolutePath() + "/" + getMyFirebaseUserId() + "_" + getCurrentTimeInMilies() + ".3gp";

                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                        mRecorder.setOutputFile(mRecordFilePath);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setMaxDuration(120 * 1000);
                        mRecorder.prepare();
                        mRecorder.start();

                        tv_start_recorder.setText("Dừng");

                        runOnUiThread(new Runnable() {
                            public void run() {

                                recorderSecond++;

                                tv_timer_audio_recorder.setText(formatSecondsToHours(recorderSecond));

                                pb_audio_recorder.setProgress(recorderSecond);

                                if (recorderSecond < 120) {

                                    mRecordHandler.postDelayed(this, 1000);

                                } else stopRecording();

                            }
                        });

                        isRecording = true;

                    } catch (IOException e) { hideAudioRecorder(); }

                } else if (recorderSecond > 0) stopRecording();

            }
        });

        img_play_audio_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRecordPlayer != null) {

                    if (mRecordPlayer.isPlaying()) {

                        mRecordHandler.removeCallbacksAndMessages(null);

                        mRecordPlayer.pause();

                        img_play_audio_recorder.setImageResource(R.drawable.ic_play_audio_filled_color);

                    } else startAudioPlayer();

                } else playNewAudioFile(true);


            }
        });

        tv_close_audio_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideAudioRecorder();

                linear_send_messages.setVisibility(View.VISIBLE);

            }
        });

    }

    protected void startAudioPlayer() {

        mRecordPlayer.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mRecordPlayer != null && mRecordPlayer.isPlaying()) {

                    final int current = mRecordPlayer.getCurrentPosition() / 1000;

                    pb_audio_player.setProgress(current);

                }

                mRecordHandler.postDelayed(this, 500);

            }
        });

        img_play_audio_recorder.setImageResource(R.drawable.ic_pause_audio_filled_color);
    }


    @Override
    public void onBackPressed() {

        if (linear_stickers_emoji.getVisibility() == View.VISIBLE)

            hideEmoji();

        else super.onBackPressed();

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        if (card_unseen != null && card_unseen.getVisibility() == View.VISIBLE) {
            card_unseen.setVisibility(View.GONE);
            tv_num_unseen.setText(null);
        }
    }

    protected void initEmoji() {

        vp_stickers_emoji = (ViewPager) findViewById(R.id.vp_stickers_emoji);
        linear_stickers_emoji = (LinearLayout) findViewById(R.id.linear_stickers_emoji);
        tv_tab_stickers = (TextView) findViewById(R.id.tv_tab_stickers);
        tv_tab_emoji = (TextView) findViewById(R.id.tv_tab_emoji);

        btn_show_emoji = (FrameLayout) findViewById(R.id.frame_show_emoji);
        btn_show_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linear_stickers_emoji.getVisibility() == View.VISIBLE) {

                    showKeyboard(BaseChatActivity.this);

                    hideEmoji();

                } else {

                    showEmoji();

                    hideKeyboard(BaseChatActivity.this);
                }

            }
        });

        tv_tab_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_stickers_emoji.setCurrentItem(1);
                tv_tab_stickers.setBackground(null);
                tv_tab_emoji.setBackgroundResource(R.drawable.filled_light_grey_bg);
            }
        });
        tv_tab_stickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_stickers_emoji.setCurrentItem(0);
                tv_tab_emoji.setBackground(null);
                tv_tab_stickers.setBackgroundResource(R.drawable.filled_light_grey_bg);
            }
        });

        final StickerEmojiPagerAdapter stickerAndEmojiPagerAdapter = new StickerEmojiPagerAdapter(getSupportFragmentManager());

        vp_stickers_emoji.setOffscreenPageLimit(2);
        vp_stickers_emoji.setAdapter(stickerAndEmojiPagerAdapter);
        vp_stickers_emoji.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if (i == 0) {
                    tv_tab_emoji.setBackground(null);
                    tv_tab_stickers.setBackgroundResource(R.drawable.filled_light_grey_bg);
                } else {
                    tv_tab_stickers.setBackground(null);
                    tv_tab_emoji.setBackgroundResource(R.drawable.filled_light_grey_bg);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }

    protected void showEmoji() {

        linear_stickers_emoji.setVisibility(View.VISIBLE);
        int contentLength = edt_content.getText().toString().length();
        edt_content.requestFocus();
        edt_content.setSelection(contentLength);

    }

    protected void hideEmoji() {
        linear_stickers_emoji.setVisibility(View.GONE);
        int contentLength = edt_content.getText().toString().length();
        edt_content.requestFocus();
        edt_content.setSelection(contentLength);

    }

    public void appendEmoji(String emoji) {
        edt_content.append(emoji);
    }

    public void deleteOneEmoji() {
        edt_content.setFocusable(true);
        edt_content.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    protected void showSendMediaOptionsDialog() {

        LinearLayout linear_pick_images = (LinearLayout) mSendMediaOptionsDialog.findViewById(R.id.linear_pick_images);
        LinearLayout linear_capture_images = (LinearLayout) mSendMediaOptionsDialog.findViewById(R.id.linear_capture_image);
        LinearLayout linear_pick_videos = (LinearLayout) mSendMediaOptionsDialog.findViewById(R.id.linear_pick_videos);
        LinearLayout linear_record_videos = (LinearLayout) mSendMediaOptionsDialog.findViewById(R.id.linear_record_video);

        linear_pick_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= 23) {

                    if (isPickMediaPermissionsGranted(BaseChatActivity.this))

                        pickImagesFromGallery();

                    else ActivityCompat.requestPermissions(
                            BaseChatActivity.this, PERMISSIONS_ACCESS_GALLERY, PICK_IMAGES_CODE);

                } else pickImagesFromGallery();

                mSendMediaOptionsDialog.dismiss();


            }
        });

        linear_capture_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {

                    if (isCaptureMediaPermissionsGranted(BaseChatActivity.this)) {

                        if (Build.VERSION.SDK_INT >= 24) {
                            try {
                                captureImageSdk24();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else captureImageIntent();

                    } else
                        ActivityCompat.requestPermissions(BaseChatActivity.this, PERMISSIONS_CAPTURE_MEDIA, CAPTURE_IMAGE_CODE);

                } else captureImageIntent();

                mSendMediaOptionsDialog.dismiss();
            }
        });

        linear_pick_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {

                    if (isPickMediaPermissionsGranted(BaseChatActivity.this))

                        pickVideosFromGallery();

                    else
                        ActivityCompat.requestPermissions(BaseChatActivity.this, PERMISSIONS_ACCESS_GALLERY, PICK_VIDEOS_CODE);

                } else pickVideosFromGallery();

                mSendMediaOptionsDialog.dismiss();


            }
        });

        linear_record_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {

                    if (isCaptureMediaPermissionsGranted(BaseChatActivity.this))

                        recordVideoIntent();

                    else
                        ActivityCompat.requestPermissions(BaseChatActivity.this, PERMISSIONS_CAPTURE_MEDIA, RECORD_VIDEO_CODE);

                } else recordVideoIntent();

                mSendMediaOptionsDialog.dismiss();

            }
        });

        mSendMediaOptionsDialog.show();
    }

    protected void recordVideoIntent() {

        long fileSizeInBytes = VIDEO_MAX_SIZE_IN_MB * 1024 * 1024;

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, fileSizeInBytes);
        startActivityForResult(takeVideoIntent, RECORD_VIDEO_CODE);

    }

    protected void captureImageIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_CODE);
    }

    protected void pickVideosFromGallery() {
        Matisse.from(this)
                .choose(MimeType.ofVideo())
                .countable(true)
                .maxSelectable(4)
                .showSingleMediaType(true)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .theme(R.style.Matisse_Zhihu)
                .imageEngine(new MyGlideEngine())
                .forResult(PICK_VIDEOS_CODE);


    }

    protected void pickImagesFromGallery() {

        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(6)
                .showSingleMediaType(true)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .theme(R.style.Matisse_Zhihu)
                .imageEngine(new MyGlideEngine())
                .forResult(PICK_IMAGES_CODE);
    }

    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mPicturePath = "file:" + image.getAbsolutePath();
        return image;
    }

    protected void captureImageSdk24() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(BaseChatActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_CODE);
            }
        }
    }

    //----------------------------

    protected void initAudioRecorderView() {

        isRecording = false;

        recorderSecond = -1;

        mRecordFilePath = null;


        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        if (mRecordPlayer != null) {
            mRecordPlayer.stop();
            mRecordPlayer.release();
            mRecordPlayer = null;
        }


        mRecordHandler.removeCallbacksAndMessages(null);

        pb_audio_recorder.setProgress(0);
        pb_audio_recorder.setMax(120);

        pb_audio_player.setProgress(0);
        pb_audio_player.setMax(120);

        tv_timer_audio_recorder.setText("00:00");
        tv_timer_audio_recorder.setVisibility(View.VISIBLE);

        linear_player_audio_recorder.setVisibility(View.GONE);
        img_play_audio_recorder.setImageResource(R.drawable.ic_play_audio_filled_color);

        pb_audio_player.setProgress(0);

        tv_start_recorder.setText("Bắt đầu");
        tv_start_recorder.setVisibility(View.VISIBLE);

        tv_send_record.setVisibility(View.GONE);

    }

    protected void showRecordAudioDialog() {

        stopMessageAudioPlayer();

        initAudioRecorderView();

        linear_audio_recorder.setVisibility(View.VISIBLE);

        linear_send_messages.setVisibility(View.GONE);

    }

    protected void hideAudioRecorder() {

        initAudioRecorderView();

        linear_audio_recorder.setVisibility(View.GONE);

    }

    protected void stopMessageAudioPlayer() {
        //for sub class
    }

    protected void stopRecording() {

        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        if (mRecordPlayer != null) {
            mRecordPlayer.stop();
            mRecordPlayer.release();
            mRecordPlayer = null;
        }

        mRecordHandler.removeCallbacksAndMessages(null);

        tv_start_recorder.setText("Bắt đầu");
        tv_start_recorder.setVisibility(View.GONE);
        tv_timer_audio_recorder.setVisibility(View.GONE);
        linear_player_audio_recorder.setVisibility(View.VISIBLE);

        tv_send_record.setVisibility(View.VISIBLE);

        pb_audio_recorder.setProgress(0);

        isRecording = false;

        playNewAudioFile(false);

    }

    protected void playNewAudioFile(boolean startAfterPrepare) {

        try {
            mRecordPlayer = new MediaPlayer();
            mRecordPlayer.setDataSource(mRecordFilePath);
            mRecordPlayer.prepareAsync();
            mRecordPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    pb_audio_player.setProgress(0);
                    pb_audio_player.setMax(mp.getDuration() / 1000);

                    if (startAfterPrepare) startAudioPlayer(); //just prepare right after record  - not start


                }
            });

            mRecordPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    pb_audio_player.setProgress(0);
                    pb_audio_player.setMax(mp.getDuration() / 1000);

                    img_play_audio_recorder.setImageResource(R.drawable.ic_play_audio_filled_color);

                    if (mRecordPlayer != null) {
                        mRecordPlayer.stop();
                        mRecordPlayer.release();
                        mRecordPlayer = null;
                    }

                    if (mRecordPlayer != null) {
                        mRecordPlayer.stop();
                        mRecordPlayer.release();
                        mRecordPlayer = null;
                    }

                    mRecordHandler.removeCallbacksAndMessages(null);

                }
            });

        } catch (IOException e) { hideAudioRecorder(); }

    }

    protected void handleLoadMore(android.os.Message msg) {
        //for subclass
    }

    protected void getLoadMoreItem(FirebaseCallBackMessageList firebaseCallBackMessageList) {
        //for subclass
    }

    protected void uploadAndSendAudio(String audioFilePath, int audioDuration) {}


    protected void showSendingAudioView() {
        btn_record_audio.setClickable(false);

        img_microphone.setVisibility(View.GONE);
        pb_sending_audio.setVisibility(View.VISIBLE);
    }

    protected void hideSendingAudioView() {
        btn_record_audio.setClickable(true);

        img_microphone.setVisibility(View.VISIBLE);
        pb_sending_audio.setVisibility(View.GONE);
    }

    protected void showSendingMediaView() {
        btn_mms.setClickable(false);
        img_gallery.setVisibility(View.GONE);
        pb_sending_media.setVisibility(View.VISIBLE);
    }

    protected void hideSendingMediaView() {
        btn_mms.setClickable(true);

        img_gallery.setVisibility(View.VISIBLE);
        pb_sending_media.setVisibility(View.GONE);
    }


    @Override
    protected void onStop() {
        super.onStop();

        setChattingUserId(null);

    }

    protected interface FirebaseCallBackMessageList {
        void OnCallBack(ArrayList<Message> messages);
    }

    protected class LoadMoreHandler extends Handler {

        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == HANDLER_LOADMORE_MESSAGE) handleLoadMore(msg);

        }
    }

    protected class LoadMoreThread extends Thread {
        @Override
        public void run() {

            try { Thread.sleep(1500); } catch (InterruptedException e) { }

            getLoadMoreItem(new FirebaseCallBackMessageList() {
                @Override
                public void OnCallBack(ArrayList<Message> messages) {

                    android.os.Message message = mLoadMoreHandler.obtainMessage(HANDLER_LOADMORE_MESSAGE, messages);

                    mLoadMoreHandler.sendMessage(message);

                }
            });


        }
    }
}
