package com.example.tranquoctrungcntt.uchat.Services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.generateMediaName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;


public class DownloadService extends Service {

    public static final String DOWNLOAD_CONTENT = "DownloadContent";
    public static final String DOWNLOAD_TYPE = "DownloadType";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String downloadUrlOfImage = intent.getExtras().getString(DOWNLOAD_CONTENT);
        final int type = intent.getExtras().getInt(DOWNLOAD_TYPE);

        final String filename = generateMediaName(type);

        File direct = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + "/" + "Uchat" + "/");

        if (direct.exists()) {

            File file = new File(direct + "/" + filename);

            if (file.exists()) {

                showLongToast(getApplicationContext(), "Tập tin đã tồn tại !");

            } else DownloadNow(downloadUrlOfImage, filename);

        } else {

            final boolean isSuccess = direct.mkdir();

            if (isSuccess) {

                DownloadNow(downloadUrlOfImage, filename);

            } else showLongToast(getApplicationContext(), "Tải tập tin thất bại, vui lòng thử lại.");

        }

        stopSelf();

        return START_NOT_STICKY;
    }

    private void DownloadNow(String downloadUrlOfImage, String filename) {
        DownloadManager dm = (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(downloadUrlOfImage);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setTitle(filename)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                        File.separator + "Uchat" + File.separator + filename);
        dm.enqueue(request);
    }
}
