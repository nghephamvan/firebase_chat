package com.example.tranquoctrungcntt.uchat.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.BuildConfig;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_UPDATE_PROFILE_TIMER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_UPDATE_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_EXIST_USER_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_FULL_SIZE_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_THUMB_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.MAX_PROCESSING_TIME;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TIME_TO_WAIT_FOR_UPDATING_PROFILE_HOUR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.TIME_TO_WAIT_FOR_UPDATING_PROFILE_MILIES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.CAPTURE_IMAGE_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_ACCESS_GALLERY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_CAPTURE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PICK_IMAGES_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_STORAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_FULL_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_THUMB_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isValidSingleName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.buildUriMapAfterUpload;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.generateMediaName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getThumbnail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isCaptureMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isPickMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showCaptureImageRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showPickImagesRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setMediaUrlToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;


public class UpdateProfile extends BaseActivity {

    private Toolbar mToolbar;

    private CircleImageView civ_avatar;

    private EditText edt_name;
    private EditText edt_dateofbirth;
    private EditText edt_gender;

    private Button btn_update;

    private UploadTask uploadTask;
    private UploadTask thumbUploadTask;

    private ValueEventListener mProfileValueEvent;
    private DatabaseReference mProfileRef;

    private int mDay, mMonth, mYear;
    private String mPicturePath;
    private User mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initViews();

        initClickEvents();

        if (mProfileValueEvent != null && mProfileRef != null) mProfileRef.removeEventListener(mProfileValueEvent);

        mProfileRef = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId());

        mProfileValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    mProfile = dataSnapshot.getValue(User.class);

                    if (mProfile != null) {

                        edt_name.setText(mProfile.getName());
                        edt_gender.setText(mProfile.getGender());
                        edt_dateofbirth.setText(mProfile.getDateofbirth());

                        setAvatarToView(UpdateProfile.this, mProfile.getThumbAvatarUrl(), mProfile.getName(), civ_avatar);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mProfileRef.addValueEventListener(mProfileValueEvent);

    }


    private void initClickEvents() {


        edt_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenderDialog();
            }
        });

        edt_dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateOfBirthDialog();
            }
        });

        civ_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGetAvatarOptions();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnectedToFirebaseService(UpdateProfile.this)) {

                    ROOT_REF.child(CHILD_UPDATE_PROFILE_TIMER)
                            .child(getMyFirebaseUserId()).child(CHILD_UPDATE_TIME)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    long timeToWait = -1;

                                    if (dataSnapshot.exists()) {

                                        long timeValue = dataSnapshot.getValue(Long.class);
                                        long timePassed = getCurrentTimeInMilies() - timeValue;

                                        if (timePassed <= TIME_TO_WAIT_FOR_UPDATING_PROFILE_MILIES)
                                            timeToWait = TIME_TO_WAIT_FOR_UPDATING_PROFILE_MILIES - timePassed;

                                    }

                                    if (timeToWait == -1) checkDifferenceInforToUpdate();

                                    else {

                                        long timeToWaitHour = TimeUnit.MILLISECONDS.toHours(timeToWait);
                                        long timeToWaitMinute = TimeUnit.MILLISECONDS.toMinutes(timeToWait);

                                        String content = timeToWaitHour <= 1 ?
                                                "Bạn phải chờ " + timeToWaitMinute + " phút nữa để có thể cập nhật tiếp !"
                                                : "Bạn phải chờ " + timeToWaitHour + " giờ nữa để có thể cập nhật tiếp !";

                                        showMessageDialog(UpdateProfile.this, content);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                } else showNoConnectionDialog(UpdateProfile.this);

                hideKeyboard(UpdateProfile.this);
            }
        });

    }

    private void uploadImageAndUpdateProfile(String updatedFullName, String updatedDateofbirth, String updatedGender) {

        if (mPicturePath == null) {

            updateProfile(updatedFullName,
                    updatedDateofbirth,
                    updatedGender,
                    mProfile.getAvatarUrl(),
                    mProfile.getThumbAvatarUrl());

        } else {

            Handler loadingHandler = new Handler();

            AlertDialog loadingDialog = getLoadingBuilder(UpdateProfile.this);

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

                                showMessageDialog(UpdateProfile.this, "Đã vượt quá thời gian xử lý !");

                            }
                        }
                    }, MAX_PROCESSING_TIME);

                }
            });

            loadingDialog.show();

            final String imageName = generateMediaName(MEDIA_TYPE_PICTURE);

            uploadImage(imageName, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                @Override
                public void OnCallBack(Map<String, Uri> callbackUrl) {

                    loadingDialog.dismiss();

                    if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                        updateProfile(updatedFullName, updatedDateofbirth, updatedGender,
                                callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "",
                                callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                    } else showMessageDialog(UpdateProfile.this, "Tải ảnh lên không thành công !");


                }
            });
        }
    }

    private void uploadImage(String imageName, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBack) {


        final StorageReference imageRef = ROOT_STORAGE.child(STORAGE_EXIST_USER_AVATAR).child(STORAGE_FULL_SIZE_AVATAR).child(getMyFirebaseUserId()).child(imageName);

        final StorageReference thumbRef = ROOT_STORAGE.child(STORAGE_EXIST_USER_AVATAR).child(STORAGE_THUMB_AVATAR).child(getMyFirebaseUserId()).child(imageName);

        uploadTask = imageRef.putFile(Uri.parse(mPicturePath));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (task.isSuccessful()) return imageRef.getDownloadUrl();

                urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

                throw task.getException();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> imageTask) {

                thumbUploadTask = thumbRef.putBytes(getThumbnail(UpdateProfile.this, Uri.parse(mPicturePath), 60));
                thumbUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (task.isSuccessful()) return thumbRef.getDownloadUrl();

                        urlCallBack.OnCallBack(buildUriMapAfterUpload(null, null));

                        throw task.getException();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> thumbTask) {

                        final Uri imageResult = imageTask.isSuccessful() ? imageTask.getResult() : null;
                        final Uri thumbResult = thumbTask.isSuccessful() ? thumbTask.getResult() : null;

                        urlCallBack.OnCallBack(buildUriMapAfterUpload(imageResult, thumbResult));

                    }
                });

            }
        });


    }

    private void updateProfile(String updatedFullName,
                               String updatedDateofbirth,
                               String updatedGender,
                               String updatedAvatarUrl,
                               String updatedThumbAvatarUrl) {


        AlertDialog loadingDialog = getLoadingBuilder(UpdateProfile.this);

        loadingDialog.show();

        final Map<String, Object> mapData = new HashMap<>();

        mapData.put("/" + CHILD_USERS + "/" + getMyFirebaseUserId() + "/dateofbirth", updatedDateofbirth);
        mapData.put("/" + CHILD_USERS + "/" + getMyFirebaseUserId() + "/name", formatName(updatedFullName));
        mapData.put("/" + CHILD_USERS + "/" + getMyFirebaseUserId() + "/gender", updatedGender);
        mapData.put("/" + CHILD_USERS + "/" + getMyFirebaseUserId() + "/avatarUrl", updatedAvatarUrl);
        mapData.put("/" + CHILD_USERS + "/" + getMyFirebaseUserId() + "/thumbAvatarUrl", updatedThumbAvatarUrl);
        mapData.put("/" + CHILD_UPDATE_PROFILE_TIMER + "/" + getMyFirebaseUserId() + "/" + CHILD_UPDATE_TIME, getCurrentTimeInMilies());

        ROOT_REF.updateChildren(mapData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mPicturePath = null;

                showMessageDialog(UpdateProfile.this, "Cập nhật thành công !");

                loadingDialog.dismiss();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case CAPTURE_IMAGE_CODE:

                if (isAllPermissionsGrantedInResult(grantResults)) {

                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            captureImageIntentSdk24();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else captureImageIntent(requestCode);

                } else showCaptureImageRequestPermissionDialog(this);


                break;
            case PICK_IMAGES_CODE:

                if (isAllPermissionsGrantedInResult(grantResults))
                    pickImageIntent();
                else showPickImagesRequestPermissionDialog(this);

                break;

        }


    }

    private void showUpdateConfirmDialog(String new_fullname, String new_dateofbirth, String new_gender) {

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật thông tin")
                .setMessage("Để bạn bè có thể nhận ra bạn, bạn phải chờ " + TIME_TO_WAIT_FOR_UPDATING_PROFILE_HOUR + "h để có thể tiếp tục cập nhật thông tin." +
                        " Bạn có chắc chắn muốn cập nhật không ?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (isConnectedToFirebaseService(UpdateProfile.this)) {

                            uploadImageAndUpdateProfile(new_fullname, new_dateofbirth, new_gender);

                        } else showNoConnectionDialog(UpdateProfile.this);

                    }
                }).setNegativeButton("Không", null).create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case CAPTURE_IMAGE_CODE:

                    if (Build.VERSION.SDK_INT < 24) mPicturePath = data != null ? data.getDataString() : null;

                    if (mPicturePath != null) setMediaUrlToView(UpdateProfile.this, mPicturePath, civ_avatar);

                    break;

                case PICK_IMAGES_CODE:

                    mPicturePath = data != null ? data.getDataString() : null;

                    if (mPicturePath != null) setMediaUrlToView(UpdateProfile.this, mPicturePath, civ_avatar);

                    break;
            }

        }

    }

    private void showGetAvatarOptions() {

        PopupMenu popupMenu = new PopupMenu(UpdateProfile.this, civ_avatar);

        MenuInflater inflater = new MenuInflater(UpdateProfile.this);
        inflater.inflate(R.menu.avatar_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.item_pick_avatar:

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (isPickMediaPermissionsGranted(UpdateProfile.this))

                                pickImageIntent();

                            else ActivityCompat.requestPermissions(
                                    UpdateProfile.this, PERMISSIONS_ACCESS_GALLERY, PICK_IMAGES_CODE);

                        } else pickImageIntent();

                        break;

                    case R.id.item_capture_avatar:

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (isCaptureMediaPermissionsGranted(UpdateProfile.this)) {

                                if (Build.VERSION.SDK_INT >= 24) {

                                    try {
                                        captureImageIntentSdk24();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else captureImageIntent(CAPTURE_IMAGE_CODE);

                            } else ActivityCompat.requestPermissions(
                                    UpdateProfile.this, PERMISSIONS_CAPTURE_MEDIA, CAPTURE_IMAGE_CODE);

                        } else captureImageIntent(CAPTURE_IMAGE_CODE);

                        break;
                }
                return true;
            }
        });

        popupMenu.show();

    }

    private void captureImageIntent(int mRequestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, mRequestCode);
    }

    private void pickImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGES_CODE);
    }

    private File createProfilePctureFile() throws IOException {
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

    private void captureImageIntentSdk24() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(UpdateProfile.this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            try {
                photoFile = createProfilePctureFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(UpdateProfile.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createProfilePctureFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_CODE);
            }
        }
    }

    private void initViews() {

        mPicturePath = null;
        mProfile = null;
        uploadTask = null;
        thumbUploadTask = null;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cập nhật thông tin");

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_dateofbirth = (EditText) findViewById(R.id.edt_dateofbirth);
        edt_gender = (EditText) findViewById(R.id.edt_gender);
        edt_name.requestFocus();

        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);
        btn_update = (Button) findViewById(R.id.btn_confirm);


    }

    private void showDateOfBirthDialog() {


        mYear = Integer.parseInt(edt_dateofbirth.getText().toString().trim().substring(6));
        mMonth = Integer.parseInt(edt_dateofbirth.getText().toString().trim().substring(3, 5)) - 1;
        mDay = Integer.parseInt(edt_dateofbirth.getText().toString().trim().substring(0, 2));

        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfile.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        if (year > Calendar.getInstance().get(Calendar.YEAR) - 7 || year < 1900)

                            showMessageDialog(UpdateProfile.this, "Sinh nhật không hợp lệ !");

                        else {

                            Calendar calendar = Calendar.getInstance();

                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;

                            calendar.set(mYear, mMonth, mDay);

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                            edt_dateofbirth.setText(formatter.format(calendar.getTime()));

                        }


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();


    }

    private void showGenderDialog() {

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Nam");
        arrayList.add("Nữ");

        ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProfile.this, android.R.layout.simple_list_item_1, arrayList);

        new AlertDialog.Builder(UpdateProfile.this)
                .setTitle("Chọn giới tính")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edt_gender.setText(arrayList.get(which));
                    }
                }).create().show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void checkDifferenceInforToUpdate() {

        final String fullName = edt_name.getText().toString().trim();
        final String dateofbirth = edt_dateofbirth.getText().toString().trim();
        final String gender = edt_gender.getText().toString().trim();

        if (fullName.isEmpty())

            showMessageDialog(UpdateProfile.this, "Bạn chưa nhập họ tên");

        else if (!isValidSingleName(fullName))

            showMessageDialog(UpdateProfile.this, "Họ tên không hợp lệ");

        else if (dateofbirth.isEmpty())

            showMessageDialog(UpdateProfile.this, "Bạn chưa chọn ngày sinh");

        else if (gender.isEmpty())

            showMessageDialog(UpdateProfile.this, "Bạn chưa chọn giới tính");

        else if (isAbleToUpdate(fullName, dateofbirth, gender))

            showUpdateConfirmDialog(fullName, dateofbirth, gender);

        else finish();


    }

    private boolean isAbleToUpdate(String nameToCheck, String dateofbirthToCheck, String genderToCheck) {

        if (!nameToCheck.equalsIgnoreCase(mProfile.getName())) return true;

        if (!dateofbirthToCheck.equalsIgnoreCase(mProfile.getDateofbirth())) return true;

        if (!genderToCheck.equalsIgnoreCase(mProfile.getGender())) return true;

        if (mPicturePath != null) return true;

        return false;

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


}
