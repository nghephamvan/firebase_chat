package com.example.tranquoctrungcntt.uchat.Activities;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tranquoctrungcntt.uchat.R;

import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.hasGooglePlayService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToHomeScreen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToLoginScreen;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToNoGGPlayServiceScreenIfNeeded;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (hasGooglePlayService(SplashScreen.this)) {

                    if (isAccountValid()) {

                        goToHomeScreen(SplashScreen.this);

                    } else goToLoginScreen(SplashScreen.this);

                } else goToNoGGPlayServiceScreenIfNeeded(SplashScreen.this);

                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }
        }, 750);


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
