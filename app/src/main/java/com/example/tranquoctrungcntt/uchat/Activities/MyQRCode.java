package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.example.tranquoctrungcntt.uchat.Services.DownloadService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_FULL_SIZE_QR_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_THUMB_QR_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_USER_QR_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.MAX_PROCESSING_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.DOWNLOAD_MEDIA_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_ACCESS_GALLERY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_STORAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_FULL_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_THUMB_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kQRCodeUrl;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kThumbQRCodeUrl;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildUriMapAfterUpload;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.generateMediaName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isDownloadMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showDownloadMediaRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;


public class MyQRCode extends BaseActivity {

    private Toolbar mToolbar;

    private LinearLayout layout_no_qr;
    private LinearLayout layout_have_qr;

    private Button btn_generate;
    private TextView tv_share;
    private TextView tv_save;
    private ImageView img_qr_code;

    private UploadTask uploadTask;
    private UploadTask thumbUploadTask;

    private ValueEventListener mProfileValueEvent;
    private DatabaseReference mProfileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);

        initViews();

        initClickEvents();

        if (mProfileValueEvent != null && mProfileRef != null) mProfileRef.removeEventListener(mProfileValueEvent);

        mProfileRef = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId());

        mProfileValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final User user = dataSnapshot.getValue(User.class);

                if (user.getQrCodeUrl() != null) {

                    Bitmap myBitmap = QRCode.from(getResources().getString(R.string.project_id) + "_code_" + getMyFirebaseUserId()).bitmap();

                    img_qr_code.setImageBitmap(myBitmap);

                    layout_no_qr.setVisibility(View.GONE);

                    layout_have_qr.setVisibility(View.VISIBLE);

                } else {

                    img_qr_code.setImageResource(R.drawable.ic_place_holder);

                    layout_no_qr.setVisibility(View.VISIBLE);

                    layout_have_qr.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mProfileRef.addValueEventListener(mProfileValueEvent);
    }

    private void initClickEvents() {
        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createQRCode();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {

                    if (isDownloadMediaPermissionsGranted(MyQRCode.this)) {

                        downloadQRCode();

                    } else ActivityCompat.requestPermissions(MyQRCode.this,
                            PERMISSIONS_ACCESS_GALLERY, DOWNLOAD_MEDIA_CODE);

                } else downloadQRCode();
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyQRCode.this, ShareMyQRCode.class));
            }
        });
    }

    private void createQRCode() {

        Handler loadingHandler = new Handler();

        AlertDialog loadingDialog = getLoadingBuilder(MyQRCode.this);

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                if (uploadTask != null && !uploadTask.isComplete()) {
                    uploadTask.cancel();
                    uploadTask = null;

                }
                if (thumbUploadTask != null && !thumbUploadTask.isComplete()) {
                    thumbUploadTask.cancel();
                    thumbUploadTask = null;
                }

                loadingHandler.removeCallbacksAndMessages(null);
            }
        });

        loadingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                loadingHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (loadingDialog.isShowing()) {

                            loadingDialog.dismiss();

                            showMessageDialog(MyQRCode.this, "Đã vượt quá thời gian xử lý !");

                        }

                    }
                }, MAX_PROCESSING_TIME);

            }
        });

        loadingDialog.show();

        String imageName = generateMediaName(MEDIA_TYPE_PICTURE);

        uploadQRCode(imageName, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
            @Override
            public void OnCallBack(Map<String, Uri> callbackUrl) {

                loadingDialog.dismiss();

                if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                    final Map<String, Object> map = new HashMap<>();

                    map.put(kQRCodeUrl, callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "");
                    map.put(kThumbQRCodeUrl, callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                    ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId()).updateChildren(map);

                } else showMessageDialog(MyQRCode.this, "Tạo mã QR không thành công !");

            }
        });

    }

    private void downloadQRCode() {

        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                showLongToast(MyQRCode.this, "Đang tải xuống mã QR của bạn...!");

                Intent it = new Intent(MyQRCode.this, DownloadService.class);
                it.putExtra(DownloadService.DOWNLOAD_TYPE, MEDIA_TYPE_PICTURE);
                it.putExtra(DownloadService.DOWNLOAD_CONTENT, callbackUserProfile.getQrCodeUrl());

                startService(it);

            }
        });

    }

    private void initViews() {


        uploadTask = null;
        thumbUploadTask = null;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mã QR của tôi");

        layout_no_qr = (LinearLayout) findViewById(R.id.layout_no_code);
        btn_generate = (Button) findViewById(R.id.btn_create_qrcode);

        layout_have_qr = (LinearLayout) findViewById(R.id.layout_have_code);

        img_qr_code = (ImageView) findViewById(R.id.img_qr_code);

        tv_share = (TextView) findViewById(R.id.tv_share_qr);
        tv_save = (TextView) findViewById(R.id.tv_save_qr);


    }

    private void uploadQRCode(String imageName, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBackk) {


        Bitmap myBitmap = QRCode.from(getResources().getString(R.string.project_id) + "_code_" + getMyFirebaseUserId()).bitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final StorageReference imageRef = ROOT_STORAGE.child(STORAGE_USER_QR_CODE)
                .child(STORAGE_FULL_SIZE_QR_CODE)
                .child(getMyFirebaseUserId())
                .child(imageName);

        final StorageReference thumbRef = ROOT_STORAGE.child(STORAGE_USER_QR_CODE)
                .child(STORAGE_THUMB_QR_CODE)
                .child(getMyFirebaseUserId())
                .child(imageName);


        uploadTask = imageRef.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (task.isSuccessful()) return imageRef.getDownloadUrl();

                urlCallBackk.OnCallBack(buildUriMapAfterUpload(null, null));

                throw task.getException();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> imageTask) {


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] data = baos.toByteArray();

                thumbUploadTask = thumbRef.putBytes(data);
                thumbUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (task.isSuccessful()) return thumbRef.getDownloadUrl();

                        urlCallBackk.OnCallBack(buildUriMapAfterUpload(null, null));

                        throw task.getException();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> thumbTask) {

                        final Uri imageResult = imageTask.isSuccessful() ? imageTask.getResult() : null;
                        final Uri thumbResult = thumbTask.isSuccessful() ? thumbTask.getResult() : null;

                        urlCallBackk.OnCallBack(buildUriMapAfterUpload(imageResult, thumbResult));
                    }
                });


            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (uploadTask != null && !uploadTask.isComplete()) {
            uploadTask.cancel();
            uploadTask = null;
        }

        if (thumbUploadTask != null && !thumbUploadTask.isComplete()) {
            thumbUploadTask.cancel();
            thumbUploadTask = null;
        }

        if (mProfileValueEvent != null && mProfileRef != null) mProfileRef.removeEventListener(mProfileValueEvent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == DOWNLOAD_MEDIA_CODE) {

            if (isAllPermissionsGrantedInResult(grantResults)) {

                downloadQRCode();

            } else showDownloadMediaRequestPermissionDialog(MyQRCode.this);

        }

    }
}
