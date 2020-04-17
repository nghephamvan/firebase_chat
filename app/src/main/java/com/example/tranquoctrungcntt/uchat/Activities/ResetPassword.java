package com.example.tranquoctrungcntt.uchat.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;


public class ResetPassword extends NoLoginBaseActivity {

    private Toolbar mToolbar;
    private Button btn_reset;
    private EditText edt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();

        initClickEvents();

    }

    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quên mật khẩu");

        btn_reset = (Button) findViewById(R.id.btn_confirm);
        edt_email = (EditText) findViewById(R.id.edt_email);

    }

    private void initClickEvents() {

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = edt_email.getText().toString().trim();

                if (email.isEmpty()) {

                    showMessageDialog(ResetPassword.this, "Email không được trống");

                } else if (!email.contains("@")) {

                    showMessageDialog(ResetPassword.this, "Email không hợp lệ");

                } else {

                    sendResetPasswordEmail(email);

                    hideKeyboard(ResetPassword.this);

                }


            }
        });
    }

    private void sendResetPasswordEmail(String email) {

        if (isConnectedToFirebaseService(ResetPassword.this)) {

            AlertDialog loadingDialog = getLoadingBuilder(ResetPassword.this);

            loadingDialog.show();

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            loadingDialog.dismiss();

                            if (task.isSuccessful()) {

                                showEmailSentSuccessfullyDialog();

                            } else showMessageDialog(ResetPassword.this, "Gửi email lấy lại mật khẩu không thành công, vui lòng thử lại !");

                        }
                    });

        } else showNoConnectionDialog(ResetPassword.this);

    }

    private void showEmailSentSuccessfullyDialog() {

        if (!ResetPassword.this.isFinishing()) {

            new AlertDialog.Builder(ResetPassword.this)
                    .setTitle("Email đã gửi")
                    .setMessage("Chúng tôi đã gửi một email để giúp bạn lấy lại mật khẩu đến địa chỉ email mà bạn đã nhập." +
                            "Vui lòng kiểm tra hòm thư của bạn.")
                    .setPositiveButton("Đồng ý", null).create().show();

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

}
