package com.example.tranquoctrungcntt.uchat.Activities;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.CONNECT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_KEEP_CONNECTION_ALIVE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setConnectedToFirebase;


public class BaseCallActivity extends BaseActivity {

    protected ValueEventListener mValueEventConnection = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            final boolean isConnected = dataSnapshot.getValue(Boolean.class);

            setConnectedToFirebase(isConnected);

            showInternetConnectionView(isConnected);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ROOT_REF.child(CHILD_KEEP_CONNECTION_ALIVE).keepSynced(true);

        CONNECT_REF.removeEventListener(mValueEventConnection);

        CONNECT_REF.addValueEventListener(mValueEventConnection);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ROOT_REF.child(CHILD_KEEP_CONNECTION_ALIVE).keepSynced(false);

        CONNECT_REF.removeEventListener(mValueEventConnection);

    }


    protected void showInternetConnectionView(boolean isConnectedToFirebase) {
        // for sub class
    }


    @Override
    public void onBackPressed() {

    }
}
