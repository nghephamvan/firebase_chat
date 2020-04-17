package com.example.tranquoctrungcntt.uchat.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tranquoctrungcntt.uchat.R;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setChattingUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setMyGlobalProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.hasGooglePlayService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isEnableInternet;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToNoGGPlayServiceScreenIfNeeded;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.logoutFirebaseUser;

public class NoLoginBaseActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setChattingUserId(null);

        setMyGlobalProfile(null);

        logoutFirebaseUser();

        if (!hasGooglePlayService(this)) {
            goToNoGGPlayServiceScreenIfNeeded(this);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mConnectionReceiver, intentConnection);

        if (!hasGooglePlayService(this)) {
            goToNoGGPlayServiceScreenIfNeeded(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mConnectionReceiver);

        hideKeyboard(NoLoginBaseActivity.this);

    }


}
