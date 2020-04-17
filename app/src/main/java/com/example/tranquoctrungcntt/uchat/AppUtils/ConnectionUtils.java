package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.isConnectedToFirebaseDatabase;

public class ConnectionUtils {


    public static boolean isConnectedToFirebaseService(Context context) {
        return isEnableInternet(context) && isConnectedToFirebaseDatabase();
    }

    public static void showNoConnectionDialog(Context context) {

        if (!((Activity) context).isFinishing()) {

            new AlertDialog.Builder(context)
                    .setTitle("Lỗi kết nối")
                    .setMessage("Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại đường truyền.")
                    .setPositiveButton("Đã hiểu", null).create().show();

        }


    }

    public static boolean isEnableInternet(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();

    }

}
