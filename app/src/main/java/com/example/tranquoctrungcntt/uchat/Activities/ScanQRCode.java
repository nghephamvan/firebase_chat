package com.example.tranquoctrungcntt.uchat.Activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.tranquoctrungcntt.uchat.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_SCAN_QR_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.SCAN_QR_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getUserIdFromQRCode;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isAllPermissionsGrantedInResult;
import static com.example.tranquoctrungcntt.uchat.AppUtils.PermissionUtils.isScanQRCodePermissionsGranted;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewUserProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;

public class ScanQRCode extends BaseActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Button btn_back;
    private FrameLayout frameLayout;
    private AlertDialog mErrorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                ViewFinderView finderView = new ViewFinderView(context);
                finderView.setLaserColor(getResources().getColor(R.color.colorPrimary));
                finderView.setBorderColor(getResources().getColor(R.color.colorPrimary));
                finderView.setBorderCornerRounded(true);
                finderView.setBorderCornerRadius(15);

                return finderView;
            }
        };

        frameLayout = (FrameLayout) findViewById(R.id.frame_scanner_camera);
        frameLayout.addView(mScannerView);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mErrorDialog = new AlertDialog.Builder(ScanQRCode.this)
                .setMessage("Quét mã không thành không, Mã QR không thuộc ứng dụng. Vui lòng thử lại !")
                .setPositiveButton("Đã hiểu", null).create();


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void handleResult(Result rawResult) {

        if (isConnectedToFirebaseService(ScanQRCode.this)) {

            if (!mErrorDialog.isShowing()) {

                String userId = getUserIdFromQRCode(ScanQRCode.this, rawResult.toString());

                if (userId != null) {

                    if (userId.equals(getMyFirebaseUserId())) {

                        Intent it = new Intent(ScanQRCode.this, MyProfile.class);
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ScanQRCode.this);
                        startActivity(it, activityOptions.toBundle());

                    } else viewUserProfile(ScanQRCode.this, userId);

                    finish();

                } else if (!ScanQRCode.this.isFinishing()) mErrorDialog.show();

            }


        } else showLongToast(ScanQRCode.this, "Không có kết nối internet !");

        mScannerView.resumeCameraPreview(ScanQRCode.this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.

        if (Build.VERSION.SDK_INT >= 23) {

            if (isScanQRCodePermissionsGranted(ScanQRCode.this)) {

                mScannerView.startCamera();

            } else
                ActivityCompat.requestPermissions(ScanQRCode.this, PERMISSIONS_SCAN_QR_CODE, SCAN_QR_CODE);

        } else mScannerView.startCamera();


    }

    @Override
    protected void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SCAN_QR_CODE && isAllPermissionsGrantedInResult(grantResults))

            mScannerView.startCamera();

        else finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
}
