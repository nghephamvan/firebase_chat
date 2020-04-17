package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.CONNECT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_KEEP_CONNECTION_ALIVE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kPassword;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kVerified;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setConnectedToFirebase;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.getRememberedAccountMap;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.isAccountRemembered;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.setRememberAccountMap;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.updateRememberAccountStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToHomeScreen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.logoutFirebaseUser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;


public class Login extends NoLoginBaseActivity {

    private Button btn_login;

    private TextView btn_register;
    private TextView tv_forgot_password;

    private EditText edt_email;
    private EditText edt_password;

    private CheckBox cb_remember;

    private ValueEventListener mValueEventConnection = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            final boolean isConnected = dataSnapshot.getValue(Boolean.class);

            setConnectedToFirebase(isConnected);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ROOT_REF.child(CHILD_KEEP_CONNECTION_ALIVE).keepSynced(true);

        initViews();

        initClickEvents();

        cb_remember.setChecked(isAccountRemembered(Login.this));

        if (isAccountRemembered(Login.this)) {

            Map<String, String> map = getRememberedAccountMap(Login.this);

            edt_email.setText(map.get("email"));
            edt_password.setText(map.get("password"));

        }

        CONNECT_REF.removeEventListener(mValueEventConnection);

        CONNECT_REF.addValueEventListener(mValueEventConnection);

    }

    private void initClickEvents() {

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = edt_email.getText().toString().trim();
                final String password = edt_password.getText().toString().trim();

                if (email.isEmpty())

                    showMessageDialog(Login.this, "Email không được trống");

                else if (!email.contains("@"))

                    showMessageDialog(Login.this, "Email không hợp lệ");

                else if (password.isEmpty())

                    showMessageDialog(Login.this, "Mật khẩu không được trống");

                else if (isConnectedToFirebaseService(Login.this))

                    login(email, password);

                else showNoConnectionDialog(Login.this);


            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ResetPassword.class));
            }
        });

    }

    private void login(String inputEmail, String inputPassword) {


        AlertDialog loadingDialog = getLoadingBuilder(Login.this);

        loadingDialog.show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        loadingDialog.dismiss();

                        if (task.isSuccessful()) {

                            if (task.getResult().getUser().isEmailVerified()) {

                                final String loggedInUserId = task.getResult().getUser().getUid();

                                Map<String, Object> mapLogin = new HashMap<>();
                                mapLogin.put(kPassword, inputPassword);
                                mapLogin.put(kVerified, true);

                                ROOT_REF.child(CHILD_USERS).child(loggedInUserId).updateChildren(mapLogin)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                moveToHomeScreenStuff();

                                            }
                                        });

                            } else {

                                logoutFirebaseUser();

                                showNotVerifiedUserDialog(inputEmail, inputPassword);

                            }

                        } else showMessageDialog(Login.this, "Lỗi đăng nhập, vui lòng thử lại !");

                    }
                });


    }

    private void moveToHomeScreenStuff() {

        if (cb_remember.isChecked()) {

            setRememberAccountMap(Login.this, edt_email.getText().toString().trim(), edt_password.getText().toString().trim());

        } else setRememberAccountMap(Login.this, "", "");

        updateRememberAccountStatus(Login.this, cb_remember.isChecked());

        goToHomeScreen(Login.this);

    }

    private void showNotVerifiedUserDialog(String email, String password) {

        if (!Login.this.isFinishing()) {

            final AlertDialog alertDialog = new AlertDialog.Builder(Login.this)
                    .setTitle("Xác thực email")
                    .setMessage("Tài khoản của bạn chưa được xác thực. Bạn có muốn chúng tôi gửi lại email xác thực không ?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (isConnectedToFirebaseService(Login.this)) {

                                sendEmailForVerifying(email, password);

                            } else showNoConnectionDialog(Login.this);

                        }
                    })
                    .setNegativeButton("Không", null).create();


            alertDialog.show();

        }

    }

    private void sendEmailForVerifying(String email, String password) {


        AlertDialog loadingDialog = getLoadingBuilder(Login.this);

        loadingDialog.show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        loadingDialog.dismiss();

                        if (task.isSuccessful()) {

                            task.getResult().getUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())

                                                showSuccessToSendEmailDialog();

                                            else showFailToSendEmailDialog(email, password);

                                        }
                                    });

                        } else showFailToSendEmailDialog(email, password);

                    }
                });


    }

    private void showSuccessToSendEmailDialog() {

        if (!Login.this.isFinishing()) {
            new AlertDialog.Builder(Login.this)
                    .setTitle("Email đã gửi")
                    .setMessage("Email xác thực đã được gửi đến địa chỉ email mà bạn đã đăng ký,vui lòng nhấn vào liên kết bên trong email để xác thực tài khoản  !")
                    .setPositiveButton("Đồng ý", null).create().show();
        }

    }

    private void showFailToSendEmailDialog(String email, String password) {

        if (!Login.this.isFinishing()) {

            final AlertDialog alertDialog = new AlertDialog.Builder(Login.this)
                    .setTitle("Gửi email thất bại")
                    .setMessage("Gửi email xác thực không thành công, bạn có muốn chúng tôi gửi lại không ?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (isConnectedToFirebaseService(Login.this)) {

                                sendEmailForVerifying(email, password);

                            } else showNoConnectionDialog(Login.this);

                        }
                    })
                    .setNegativeButton("Không", null).create();


            alertDialog.show();
        }
    }

    private void initViews() {

        btn_login = (Button) findViewById(R.id.btn_login);

        btn_register = (TextView) findViewById(R.id.btn_register);

        edt_email = (EditText) findViewById(R.id.edt_email);

        edt_password = (EditText) findViewById(R.id.edt_password);

        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);

        cb_remember = (CheckBox) findViewById(R.id.cb_remember);

    }

    //-----------ORTHER METHODS-------------------------


    @Override
    protected void onDestroy() {
        super.onDestroy();

        ROOT_REF.child(CHILD_KEEP_CONNECTION_ALIVE).keepSynced(false);

        CONNECT_REF.removeEventListener(mValueEventConnection);

    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

