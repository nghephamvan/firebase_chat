package com.example.tranquoctrungcntt.uchat.OtherClasses;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;

import com.example.tranquoctrungcntt.uchat.Activities.SplashScreen;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.Locale;
import java.util.TimeZone;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().purgeOutstandingWrites();

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        Configuration langConfig = getApplicationContext().getResources().getConfiguration();

        Locale locale = new Locale("vi");

        Locale.setDefault(locale);

        langConfig.locale = locale;

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+7"));

        getApplicationContext().getResources().updateConfiguration(langConfig, getApplicationContext().getResources().getDisplayMetrics());

        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> handleUncaughtException(thread, e));

    }

    public void handleUncaughtException(Thread thread, Throwable e) {

        Log.d("CCCCCCCCCCCCCCC", e + "");

        Intent it = new Intent(getApplicationContext(), SplashScreen.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);

        System.exit(0);

    }

}
