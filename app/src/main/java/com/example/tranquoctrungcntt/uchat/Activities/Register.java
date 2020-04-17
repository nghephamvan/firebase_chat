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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_FULL_SIZE_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_NEW_USER_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_THUMB_AVATAR;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.MAX_PROCESSING_TIME;
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
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentDate;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentMonth;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentYear;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getThumbnail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isCaptureMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isPickMediaPermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showCaptureImageRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.showPickImagesRequestPermissionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatSmallNumber;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.logoutFirebaseUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setMediaUrlToView;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;


public class Register extends NoLoginBaseActivity {

    private Toolbar mToolbar;

    private CircleImageView civ_avatar;
    private EditText edt_email;
    private EditText edt_name;
    private EditText edt_password;
    private EditText edt_retype_password;
    private EditText edt_dateofbirth;
    private EditText edt_gender;

    private String mPicturePath;
    private int mDay, mMonth, mYear;

    private UploadTask uploadTask;
    private UploadTask thumbUploadTask;

    private AppCompatButton btn_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        clickButtons();

    }

    private void clickButtons() {

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

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = edt_name.getText().toString().trim();
                final String password = edt_password.getText().toString().trim();
                final String retypePassword = edt_retype_password.getText().toString().trim();
                final String dateOfBirth = edt_dateofbirth.getText().toString().trim();
                final String gender = edt_gender.getText().toString().trim();
                final String email = edt_email.getText().toString().trim();

                if (email.isEmpty())

                    showMessageDialog(Register.this, "Bạn chưa nhập địa chỉ email");

                else if (!email.contains("@"))

                    showMessageDialog(Register.this, "Email không hợp lệ");

                else if (fullName.isEmpty())

                    showMessageDialog(Register.this, "Bạn chưa nhập họ tên");

                else if (!isValidSingleName(fullName))

                    showMessageDialog(Register.this, "Họ tên không hợp lệ");

                else if (dateOfBirth.isEmpty())

                    showMessageDialog(Register.this, "Bạn chưa chọn ngày sinh");

                else if (gender.isEmpty())

                    showMessageDialog(Register.this, "Bạn chưa chọn giới tính");

                else if (password.isEmpty())

                    showMessageDialog(Register.this, "Bạn chưa nhập mật khẩu");

                else if (password.length() < 6)

                    showMessageDialog(Register.this, "Mật khẩu phải có ít nhất 6 ký tự");

                else if (retypePassword.isEmpty())

                    showMessageDialog(Register.this, "Bạn chưa nhập lại mật khẩu");

                else if (!retypePassword.equals(password))

                    showMessageDialog(Register.this, "Nhập lại mật khẩu không đúng");

                else {

                    if (isConnectedToFirebaseService(Register.this)) {

                        AlertDialog loadingDialog = getLoadingBuilder(Register.this);

                        loadingDialog.show();

                        checkEmail(email, new FirebaseCallBackEmailChecker() {
                            @Override
                            public void OnCallBack(EmailChecker value) {

                                loadingDialog.dismiss();

                                if (value == EmailChecker.Valid)

                                    uploadAndCreateAccount(fullName, email, password, dateOfBirth, gender);

                                else if (value == EmailChecker.Invalid)

                                    showMessageDialog(Register.this, "Email không hợp lệ !");

                                else if (value == EmailChecker.Exists) showMessageDialog(Register.this, "Tài khoản đã tồn tại !");

                            }
                        });

                    } else showNoConnectionDialog(Register.this);

                }

            }
        });
    }

    private void initViews() {

        mPicturePath = null;
        uploadTask = null;
        thumbUploadTask = null;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_retype_password = (EditText) findViewById(R.id.edt_retype_password);
        edt_dateofbirth = (EditText) findViewById(R.id.edt_dateofbirth);
        edt_gender = (EditText) findViewById(R.id.edt_gender);

        btn_register = (AppCompatButton) findViewById(R.id.btn_register);

    }

    private void showDateOfBirthDialog() {


        if (edt_dateofbirth.getText().toString().trim().isEmpty()) {

            Calendar c = Calendar.getInstance();

            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        if (year > Calendar.getInstance().get(Calendar.YEAR) - 7 || year < 1900)

                            showMessageDialog(Register.this, "Sinh nhật không hợp lệ !");

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
        ArrayAdapter<String> adapter = new ArrayAdapter(Register.this, android.R.layout.simple_list_item_1, arrayList);

        new AlertDialog.Builder(Register.this)
                .setTitle("Chọn giới tính")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edt_gender.setText(arrayList.get(which));
                    }

                }).create().show();


    }

    private void checkEmail(String emailToCheck, FirebaseCallBackEmailChecker callback) {

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(emailToCheck)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().getSignInMethods().isEmpty())

                                callback.OnCallBack(EmailChecker.Valid);

                            else callback.OnCallBack(EmailChecker.Exists);

                        } else callback.OnCallBack(EmailChecker.Invalid);
                    }
                });

    }

    private void uploadAndCreateAccount(String fullname,
                                        String email,
                                        String password,
                                        String dateofbirth,
                                        String gender) {


        if (mPicturePath == null) {

            register(fullname, email, password, gender, dateofbirth, null, null);

        } else {

            Handler loadingHandler = new Handler();

            AlertDialog loadingDialog = getLoadingBuilder(Register.this);

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

                                showMessageDialog(Register.this, "Đã vượt quá thời gian xử lý !");

                            }

                        }
                    }, MAX_PROCESSING_TIME);

                }
            });

            loadingDialog.show();

            String imageName = generateMediaName(MEDIA_TYPE_PICTURE);

            uploadImage(imageName, new AppConstants.AppInterfaces.FirebaseUrlCallBack() {
                @Override
                public void OnCallBack(Map<String, Uri> callbackUrl) {

                    loadingDialog.dismiss();

                    if (callbackUrl.get(KEY_FULL_SIZE_MEDIA) != null && callbackUrl.get(KEY_THUMB_SIZE_MEDIA) != null) {

                        register(fullname, email, password, gender, dateofbirth,
                                callbackUrl.get(KEY_FULL_SIZE_MEDIA) + "",
                                callbackUrl.get(KEY_THUMB_SIZE_MEDIA) + "");

                    } else showMessageDialog(Register.this, "Tải ảnh lên không thành công !");

                }
            });

        }


    }

    private void uploadImage(String imageName, AppConstants.AppInterfaces.FirebaseUrlCallBack urlCallBack) {

        final StorageReference imageRef = ROOT_STORAGE.child(STORAGE_NEW_USER_AVATAR).child(STORAGE_FULL_SIZE_AVATAR).child(imageName);

        final StorageReference thumbRef = ROOT_STORAGE.child(STORAGE_NEW_USER_AVATAR).child(STORAGE_THUMB_AVATAR).child(imageName);

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

                thumbUploadTask = thumbRef.putBytes(getThumbnail(Register.this, Uri.parse(mPicturePath), 60));
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

    private void resetForm() {

        mPicturePath = null;
        edt_dateofbirth.setText(null);
        edt_email.setText(null);
        edt_gender.setText(null);
        edt_name.setText(null);
        edt_password.setText(null);
        edt_retype_password.setText(null);
        civ_avatar.setImageResource(R.drawable.ic_default_user_avatar);

    }

    private void register(String fullname, String email, String password,
                          String gender, String dateofbirth,
                          String avatarUrl, String avatarThumbUrl) {


        AlertDialog loadingDialog = getLoadingBuilder(Register.this);

        loadingDialog.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        loadingDialog.dismiss();

                        if (task.isSuccessful()) {

                            User registeredUser = new User(task.getResult().getUser().getUid()
                                    , email, password, formatName(fullname),
                                    null, dateofbirth, gender, getDateJoined(),
                                    avatarUrl, avatarThumbUrl, null, null,
                                    task.getResult().getUser().isEmailVerified(),
                                    getCurrentTimeInMilies());

                            ROOT_REF.child(CHILD_USERS).child(registeredUser.getUserId()).setValue(registeredUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            resetForm();

                                            sendVerifyEmail(email, password);

                                        }
                                    });

                        } else showMessageDialog(Register.this, "Lỗi đăng ký, vui lòng thử lại !");

                    }
                });


    }

    private void sendVerifyEmail(String email, String password) {

        AlertDialog loadingDialog = getLoadingBuilder(Register.this);

        loadingDialog.show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        loadingDialog.dismiss();

                        if (task.isSuccessful()) {

                            task.getResult().getUser().sendEmailVerification()
                                    .addOnCompleteListener(Register.this, new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {

                                            if (task.isSuccessful())

                                                showSuccessToSendEmailDialog();

                                            else showFailToSendEmailDialog(email, password);

                                        }
                                    });

                        } else showFailToSendEmailDialog(email, password);

                    }
                });

    }


    private void showFailToSendEmailDialog(String email, String password) {

        if (!Register.this.isFinishing()) {

            final AlertDialog alertDialog = new AlertDialog.Builder(Register.this)
                    .setTitle("Gửi email thất bại")
                    .setMessage("Gửi email xác thực không thành công, bạn có muốn chúng tôi gửi lại không ?" +
                            "\nBạn cũng có thể nhận email xác thực bằng cách đăng nhập.")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (isConnectedToFirebaseService(Register.this)) {

                                sendVerifyEmail(email, password);

                            } else showNoConnectionDialog(Register.this);

                        }
                    })
                    .setNegativeButton("Không", null).create();


            alertDialog.show();

        }
    }

    private void showSuccessToSendEmailDialog() {

        new AlertDialog.Builder(Register.this)
                .setTitle("Email đã gửi")
                .setMessage("Email xác thực đã được gửi đến địa chỉ email mà bạn đã đăng ký, vui lòng nhấn vào liên kết bên trong email để xác thực tài khoản.")
                .setPositiveButton("Đồng ý", null).create().show();


    }

    private String getDateJoined() {
        return formatSmallNumber(getCurrentDate()) + "/" + formatSmallNumber(getCurrentMonth()) + "/" + formatSmallNumber(getCurrentYear());
    }

    private void showGetAvatarOptions() {

        PopupMenu popupMenu = new PopupMenu(Register.this, civ_avatar);

        MenuInflater inflater = new MenuInflater(Register.this);
        inflater.inflate(R.menu.avatar_options, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.item_pick_avatar:

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (isPickMediaPermissionsGranted(Register.this))

                                pickImageIntent();

                            else ActivityCompat.requestPermissions(
                                    Register.this, PERMISSIONS_ACCESS_GALLERY, PICK_IMAGES_CODE);

                        } else pickImageIntent();

                        break;

                    case R.id.item_capture_avatar:

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (isCaptureMediaPermissionsGranted(Register.this))

                                if (Build.VERSION.SDK_INT >= 24) {
                                    try {
                                        captureImageIntentSdk24();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else captureImageIntent(CAPTURE_IMAGE_CODE);

                            else ActivityCompat.requestPermissions(Register.this,
                                    PERMISSIONS_CAPTURE_MEDIA, CAPTURE_IMAGE_CODE);

                        } else captureImageIntent(CAPTURE_IMAGE_CODE);

                        break;
                }
                return true;
            }
        });

        popupMenu.show();

    }

    private void captureImageIntent(int requestcode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, requestcode);
    }

    private void pickImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGES_CODE);
    }

    private void captureImageIntentSdk24() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Register.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case CAPTURE_IMAGE_CODE:

                    if (Build.VERSION.SDK_INT < 24) mPicturePath = data != null ? data.getDataString() : null;

                    if (mPicturePath != null) setMediaUrlToView(Register.this, mPicturePath, civ_avatar);

                    break;

                case PICK_IMAGES_CODE:

                    mPicturePath = data != null ? data.getDataString() : null;

                    if (mPicturePath != null) setMediaUrlToView(Register.this, mPicturePath, civ_avatar);

                    break;
            }

        }
    }

    private File createImageFile() throws IOException {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
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

        logoutFirebaseUser();

    }

    private enum EmailChecker {Exists, Invalid, Valid}

    private interface FirebaseCallBackEmailChecker {
        void OnCallBack(EmailChecker value);
    }


}