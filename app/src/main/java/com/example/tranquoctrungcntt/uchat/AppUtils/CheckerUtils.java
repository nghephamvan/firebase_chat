package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SEEN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_AUDIO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_CHANGE_ADMIN;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_LEAVE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_MULTIPLE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_GROUP;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_UPDATE_MEMBER;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.VIDEO_MAX_SIZE_IN_MB;


public class CheckerUtils {

    public static boolean isValidVideo(Context context, Uri uriToCheck) {

        Cursor returnCursor = context.getContentResolver().query(uriToCheck, null, null, null, null);

        if (returnCursor != null) {
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            if (returnCursor.getLong(sizeIndex) > (VIDEO_MAX_SIZE_IN_MB * 1024 * 1024)
                    || returnCursor.getLong(sizeIndex) == 0) {
                returnCursor.close();
                return false;
            }

            returnCursor.close();
            return true;
        }

        return false;
    }

    public static boolean isValidImage(Context context, Uri uriToCheck) {

        Cursor returnCursor = context.getContentResolver().query(uriToCheck, null, null, null, null);

        if (returnCursor != null) {
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            if (returnCursor.getLong(sizeIndex) == 0) {
                returnCursor.close();
                return false;
            }
            returnCursor.close();
            return true;
        }

        return false;
    }

    public static boolean isValidSingleName(String txt) {

        if (txt.toLowerCase().equalsIgnoreCase("Người dùng Uchat"))
            return false;
        else if (!txt.contains(" "))
            return false;
        else {
            String firstName = txt.substring(0, txt.indexOf(" "));
            String lastName = txt.substring(txt.lastIndexOf(" "));

            if (firstName.trim().length() < 2) return false;
            else if (lastName.trim().length() < 2) return false;

            Pattern pattern = Pattern.compile("^[a-zA-Z\\s\\p{L}]*$");
            Matcher matcher = pattern.matcher(txt);

            return matcher.matches() && !txt.contains("\u03c0");
        }

    }

    public static boolean isPhoneNumber(String url) {
        return url.startsWith("tel:");
    }

    public static boolean isEmailValid(String email) {
        return email.startsWith("mailto:");
    }

    public static boolean isMapAddress(String url) {

        return url.contains("goo.gl/maps") || url.startsWith("geo:");
    }

    public static boolean isMatchingUser(String keyword, User userToCheck) {

        return userToCheck.getName().toLowerCase().contains(keyword.toLowerCase())
                || userToCheck.getEmail().equalsIgnoreCase(keyword);

    }

    public static boolean isSeen(String status) {
        return status.equals(MESSAGE_STATUS_SEEN);
    }

    public static boolean isMediaMessage(int type) {
        return type == MESSAGE_TYPE_VIDEO
                || type == MESSAGE_TYPE_AUDIO
                || type == MESSAGE_TYPE_PICTURE
                || type == MESSAGE_TYPE_MULTIPLE_MEDIA;
    }

    public static boolean hasGooglePlayService(Context context) {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int result = googleApiAvailability.isGooglePlayServicesAvailable(context);

        return result == ConnectionResult.SUCCESS;

    }

    public static boolean isAccountValid() {
        return FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
    }

    public static boolean ableToUseApp(Context context) {
        return isAccountValid() && hasGooglePlayService(context);
    }

    public static boolean isAccountNotValidAnyMore(Exception e) {
        return e instanceof FirebaseAuthInvalidCredentialsException
                || e instanceof FirebaseAuthRecentLoginRequiredException;
    }

    public static boolean isGroupInstantMessage(Message message) {
        return message.getType() == MESSAGE_TYPE_LEAVE_GROUP
                || message.getType() == MESSAGE_TYPE_CHANGE_ADMIN
                || message.getType() == MESSAGE_TYPE_UPDATE_MEMBER
                || message.getType() == MESSAGE_TYPE_UPDATE_GROUP;
    }

    public static boolean isGroupMessage(Message message) {
        return message.getGroupId() != null;
    }

    public static boolean isUserOnline(long lastSeen) {
        return lastSeen == -1;
    }

}
