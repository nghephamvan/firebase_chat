package com.example.tranquoctrungcntt.uchat.Activities;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.example.tranquoctrungcntt.uchat.Services.SinchService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.SinchError;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_TOKENS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kLastSeen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setMyGlobalProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.hasGooglePlayService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountNotValidAnyMore;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isEnableInternet;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getAndroidID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getCurrentTimeInMilies;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLoadingBuilder;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getNotificationManager;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.isSinchTokenRegistered;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.updateShouldShowRequestPermissionWhenLogin;
import static com.example.tranquoctrungcntt.uchat.AppUtils.SharedPreferenceUtils.updatedSinchTokenRegisteredStatus;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToLoginScreen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToNoGGPlayServiceScreenIfNeeded;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.keepSyncAll;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.logoutFirebaseUser;

public class BaseActivity extends AppCompatActivity implements ServiceConnection, PushTokenRegistrationCallback {

    private final BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            TextView textView = (TextView) ((Activity) context).findViewById(R.id.tv_no_internet);

            if (textView != null) {
                if (isEnableInternet(context)) {
                    textView.setVisibility(View.GONE);
                } else textView.setVisibility(View.VISIBLE);
            }

        }
    };

    private final IntentFilter intentConnection = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isBound = false;

        if (hasGooglePlayService(BaseActivity.this)) {

            if (isAccountValid()) {

                bindService(new Intent(BaseActivity.this, SinchService.class), this, BIND_AUTO_CREATE);

                checkIfPasswordChanged();

            } else goToLoginScreen(BaseActivity.this);

        } else goToNoGGPlayServiceScreenIfNeeded(BaseActivity.this);

    }


    protected void onConnectToSinchService() {
        // for sub class to make call
    }

    protected void onDisconnectToSinchService() {
        // for sub class to end call
    }

    private void checkIfPasswordChanged() {

        getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
            @Override
            public void OnCallBack(User callbackUserProfile) {

                final String email = callbackUserProfile.getEmail();
                final String password = callbackUserProfile.getPassword();

                final AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (isAccountNotValidAnyMore(e)) {

                            if (!BaseActivity.this.isFinishing()) {

                                new AlertDialog.Builder(BaseActivity.this)
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

                    }
                });
            }
        });

    }

    protected void logoutFromBaseActivity() {

        if (isConnectedToFirebaseService(BaseActivity.this)) {

            AlertDialog loadingDialog = getLoadingBuilder(BaseActivity.this);

            loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    goToLoginScreen(BaseActivity.this);
                }
            });

            loadingDialog.show();

            ROOT_REF.child(CHILD_TOKENS).child(getMyFirebaseUserId()).child(getAndroidID(getApplicationContext()))
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId()).child(kLastSeen)
                            .setValue(getCurrentTimeInMilies()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            getNotificationManager(BaseActivity.this).cancelAll();

                            stopCallService();

                            keepSyncAll(false);

                            logoutFirebaseUser();

                            setMyGlobalProfile(null);

                            updateShouldShowRequestPermissionWhenLogin(BaseActivity.this, true);

                            loadingDialog.dismiss();

                        }
                    });

                }
            });

        } else showNoConnectionDialog(this);


    }

    private void stopCallService() {

        if (mSinchServiceInterface != null) {
            mSinchServiceInterface.unregisterPushToken();
            mSinchServiceInterface.stopSinchClient();
        }

        updatedSinchTokenRegisteredStatus(BaseActivity.this, false);
    }


    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mConnectionReceiver, intentConnection);

        if (!hasGooglePlayService(this)) {
            goToNoGGPlayServiceScreenIfNeeded(BaseActivity.this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mConnectionReceiver);

        hideKeyboard(BaseActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound) unbindService(this);

    }

    public SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        if (SinchService.class.getName().equals(componentName.getClassName())) {

            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;

            if (isSinchTokenRegistered(BaseActivity.this)) {

                //push token already registered

            } else mSinchServiceInterface.registerPushToken(this);

            mSinchServiceInterface.startSinchClient();

            onConnectToSinchService();

            isBound = true;

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

        if (SinchService.class.getName().equals(componentName.getClassName())) {

            onDisconnectToSinchService();

            mSinchServiceInterface = null;

            isBound = false;
        }
    }

    @Override
    public void tokenRegistered() {
        updatedSinchTokenRegisteredStatus(BaseActivity.this, true);
    }

    @Override
    public void tokenRegistrationFailed(SinchError sinchError) {

        updatedSinchTokenRegisteredStatus(BaseActivity.this, false);

        if (mSinchServiceInterface != null) {

            mSinchServiceInterface.registerPushToken(BaseActivity.this);

        }
    }
}
