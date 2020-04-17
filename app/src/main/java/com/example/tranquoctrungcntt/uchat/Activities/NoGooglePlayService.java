package com.example.tranquoctrungcntt.uchat.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showMessageDialog;

public class NoGooglePlayService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_google_play_service);

        checkGooglePlayServiceAgain();

        Button btn_try = (Button) findViewById(R.id.btn_try_again);
        btn_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkGooglePlayServiceAgain();
            }
        });
    }

    private void checkGooglePlayServiceAgain() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int result = googleApiAvailability.isGooglePlayServicesAvailable(NoGooglePlayService.this);

        if (result == ConnectionResult.SUCCESS) {

            Intent it = new Intent(NoGooglePlayService.this, SplashScreen.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
            finish();

        } else if (googleApiAvailability.isUserResolvableError(result)) {

            final Dialog errorDialog = googleApiAvailability.getErrorDialog(NoGooglePlayService.this, result, 123);

            if (errorDialog != null) errorDialog.show();

        } else
            showMessageDialog(NoGooglePlayService.this, "Uchat không thể chạy trên các thiết bị không có Google Play Service. Chúng thôi rất tiếc vì sự hạn chế này. Xin cảm ơn đã cài đặt Uchat !");

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
