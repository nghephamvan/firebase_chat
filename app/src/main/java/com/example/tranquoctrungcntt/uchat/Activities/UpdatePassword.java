package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kPassword;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountNotValidAnyMore;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;

public class UpdatePassword extends BaseActivity {

    private Toolbar mToolbar;

    private Button btn_update;
    private EditText edt_previous_password;
    private EditText edt_new_password;
    private EditText edt_retype_password;

    private DatabaseReference mCurrentPasswordRef;
    private ValueEventListener mValueEventPassword;

    private String mCurrentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        initViews();

        initClickEvents();

        if (mValueEventPassword != null && mCurrentPasswordRef != null) mCurrentPasswordRef.removeEventListener(mValueEventPassword);

        mCurrentPasswordRef = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId()).child(kPassword);

        mValueEventPassword = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mCurrentPassword = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mCurrentPasswordRef.addValueEventListener(mValueEventPassword);

    }


    private void initClickEvents() {

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String prePassword = edt_previous_password.getText().toString().trim();

                String updatedPassword = edt_new_password.getText().toString().trim();

                String retypePassword = edt_retype_password.getText().toString().trim();

                if (prePassword.isEmpty()) {

                    showMessageDialog(UpdatePassword.this, "Mật khẩu cũ đang trống");

                } else if (!prePassword.equals(mCurrentPassword)) {

                    showMessageDialog(UpdatePassword.this, "Mật khẩu cũ không đúng");

                } else if (updatedPassword.isEmpty()) {

                    showMessageDialog(UpdatePassword.this, "Mật khẩu mới đang trống");

                } else if (updatedPassword.length() < 6) {

                    showMessageDialog(UpdatePassword.this, "Mật khẩu phải có ít nhất 6 ký tự.");

                } else if (retypePassword.isEmpty()) {

                    showMessageDialog(UpdatePassword.this, "Bạn chưa xác nhận mật khẩu mới");

                } else if (!retypePassword.equals(updatedPassword)) {

                    showMessageDialog(UpdatePassword.this, "Nhập lại mật khẩu không đúng");

                } else {

                    if (!updatedPassword.equals(prePassword)) {

                        if (isConnectedToFirebaseService(UpdatePassword.this)) {

                            AlertDialog loadingDialog = getLoadingBuilder(UpdatePassword.this);

                            loadingDialog.show();

                            final FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), prePassword);

                            mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        mFirebaseUser.updatePassword(updatedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    ROOT_REF.child(CHILD_USERS)
                                                            .child(getMyFirebaseUserId()).child(kPassword)
                                                            .setValue(updatedPassword)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    edt_previous_password.setText(null);
                                                                    edt_new_password.setText(null);
                                                                    edt_retype_password.setText(null);

                                                                    showMessageDialog(UpdatePassword.this, "Đổi mật khẩu thành công !");

                                                                }
                                                            });

                                                } else showMessageDialog(UpdatePassword.this, "Đổi mật khẩu không thành công, vui lòng thử lai.");

                                                loadingDialog.dismiss();

                                            }
                                        });


                                    } else {

                                        if (isAccountNotValidAnyMore(task.getException())) {

                                            if (!UpdatePassword.this.isFinishing()) {

                                                new AlertDialog.Builder(UpdatePassword.this)
                                                        .setMessage("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại !")
                                                        .setPositiveButton("Đồng ý", null)
                                                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                            @Override
                                                            public void onDismiss(DialogInterface dialog) {
                                                                logoutFromBaseActivity();
                                                            }
                                                        }).create().show();

                                            }

                                        }

                                        loadingDialog.dismiss();

                                    }
                                }
                            });


                        } else showNoConnectionDialog(UpdatePassword.this);

                    } else finish();

                }


            }
        });
    }

    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Đổi mật khẩu");

        btn_update = (Button) findViewById(R.id.btn_confirm);

        edt_previous_password = (EditText) findViewById(R.id.edt_previous_password);
        edt_new_password = (EditText) findViewById(R.id.edt_new_password);
        edt_retype_password = (EditText) findViewById(R.id.edt_retype_new_password);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mValueEventPassword != null && mCurrentPasswordRef != null) mCurrentPasswordRef.removeEventListener(mValueEventPassword);

    }
}
