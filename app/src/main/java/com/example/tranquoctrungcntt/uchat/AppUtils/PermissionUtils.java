package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_ACCESS_GALLERY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_CAPTURE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_SCAN_QR_CODE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_VIDEO_CALL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.PermissionConstant.PERMISSIONS_VOICE_CALL;

public class PermissionUtils {


    public static void showRecordAudioRequestPermissionDialog(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Bạn cần cấp quyền để có thể ghi âm. " +
                        "Vui lòng cấp quyền trong lần yêu cầu tiếp theo hoặc vào cài đặt và cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Đồng ý", null).create().show();

    }


    public static void showDownloadMediaRequestPermissionDialog(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Bạn cần cấp quyền để có thể tải ảnh và video. Vui lòng cấp quyền trong " +
                        "lần yêu cầu tiếp theo hoặc vào cài đặt và cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Đồng ý", null).create().show();

    }

    public static void showPickImagesRequestPermissionDialog(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Bạn cần cấp quyền để có thể chọn ảnh. Vui lòng cấp quyền trong " +
                        "lần yêu cầu tiếp theo hoặc vào cài đặt và cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Đồng ý", null).create().show();

    }

    public static void showPickVideosRequestPermissionDialog(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Bạn cần cấp quyền để có thể chọn video. Vui lòng cấp quyền trong " +
                        "lần yêu cầu tiếp theo hoặc vào cài đặt và cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Đồng ý", null).create().show();

    }

    public static void showCaptureImageRequestPermissionDialog(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Bạn cần cấp quyền để có thể chụp ảnh. Vui lòng cấp quyền trong " +
                        "lần yêu cầu tiếp theo hoặc vào cài đặt và cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Đồng ý", null).create().show();

    }

    public static void showRecordVideoRequestPermissionDialog(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Yêu cầu cấp quyền")
                .setMessage("Bạn cần cấp quyền để có thể quay video. Vui lòng cấp quyền trong " +
                        "lần yêu cầu tiếp theo hoặc vào cài đặt và cấp quyền cho Uchat. Xin cảm ơn !")
                .setPositiveButton("Đồng ý", null).create().show();

    }

    public static boolean isScanQRCodePermissionsGranted(Context context) {

        for (String permission : PERMISSIONS_SCAN_QR_CODE) {

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;

    }

    public static boolean isDownloadMediaPermissionsGranted(Context context) {

        for (String permission : PERMISSIONS_ACCESS_GALLERY) {

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;

    }

    public static boolean isAudioRecordPermissionsGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;


    }

    public static boolean isVideoCallPermissionsGranted(Context context) {

        for (String permission : PERMISSIONS_VIDEO_CALL) {

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;

    }

    public static boolean isVoiceCallPermissionsGranted(Context context) {

        for (String permission : PERMISSIONS_VOICE_CALL) {

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean isPickMediaPermissionsGranted(Context context) {

        for (String permission : PERMISSIONS_ACCESS_GALLERY) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean isCaptureMediaPermissionsGranted(Context context) {

        for (String permission : PERMISSIONS_CAPTURE_MEDIA) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAllPermissionsGrantedInResult(int[] grantResults) {

        boolean isValid = grantResults.length > 0;

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return isValid;
    }


}
