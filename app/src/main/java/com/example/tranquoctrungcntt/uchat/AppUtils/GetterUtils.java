package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageStatus.MESSAGE_STATUS_SENT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_FULL_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.KEY_THUMB_SIZE_MEDIA;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kGroupMember;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.StringKey.kMessageId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.getMyGlobalProfile;

public class GetterUtils {

    public static String getMyFirebaseUserId() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        return null;
    }

    public static String getAndroidID(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void getMediaOfMessage(String userToGet, String messageIdToGet, AppConstants.AppInterfaces.FirebaseMediaListCallBack mediaListCallBack) {

        ROOT_REF.child(CHILD_MEDIA)
                .child(getMyFirebaseUserId()).child(userToGet)
                .orderByChild(kMessageId).equalTo(messageIdToGet)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<Media> mListMedia = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Media media = snapshot.getValue(Media.class);
                            mListMedia.add(media);
                        }

                        mediaListCallBack.OnCallBack(mListMedia);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static AlertDialog getLoadingBuilder(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view).setCancelable(false).create();

        return alertDialog;

    }

    public static byte[] getThumbnail(Context context, Uri uriToGet, int quality) {

        InputStream imageStream = null;

        try {
            imageStream = context.getContentResolver().openInputStream(uriToGet);
        } catch (FileNotFoundException e) {
        }

        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] data = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }

    public static void getGroupDetail(String groupId, AppConstants.AppInterfaces.FirebaseGroupDetailCallBack groupDetailCallBack) {

        ROOT_REF.child(CHILD_GROUP_DETAIL)
                .child(getMyFirebaseUserId()).child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        GroupDetail groupDetail = dataSnapshot.getValue(GroupDetail.class);

                        groupDetailCallBack.OnCallBack(groupDetail);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public static void getGroupDetailWithTransaction(String groupId, AppConstants.AppInterfaces.FirebaseGroupDetailCallBack groupDetailCallBack) {

        ROOT_REF.child(CHILD_GROUP_DETAIL)
                .child(getMyFirebaseUserId()).child(groupId)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        GroupDetail groupDetail = dataSnapshot.getValue(GroupDetail.class);

                        groupDetailCallBack.OnCallBack(groupDetail);

                    }
                });

    }

    public static void getSingleUserProfile(String userId, AppConstants.AppInterfaces.FirebaseUserProfileCallBack firebaseUserProfileCallBack) {

        ROOT_REF.child(CHILD_USERS).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final User user = dataSnapshot.getValue(User.class);

                if (user != null && user.getUserId() != null) {

                    firebaseUserProfileCallBack.OnCallBack(dataSnapshot.getValue(User.class));

                    ROOT_REF.child(CHILD_USERS).child(userId).removeEventListener(this);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void getGroupMemberSnapshot(String groupId, AppConstants.AppInterfaces.FirebaseGroupMemberCallBack firebaseGroupMemberCallBack) {

        ROOT_REF.child(CHILD_GROUP_DETAIL)
                .child(getMyFirebaseUserId()).child(groupId).child(kGroupMember)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        firebaseGroupMemberCallBack.OnCallBack(dataSnapshot);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static String getLastAndMiddleName(String inputString) {

        String lastName = inputString.substring(inputString.lastIndexOf(" ")).trim();

        String middleName = inputString.substring(0, inputString.lastIndexOf(" ")).trim();

        for (int index = inputString.lastIndexOf(" ") - 1; index >= 0; index--) {

            if (inputString.charAt(index) == " ".charAt(0)) {

                middleName = inputString.substring(index, inputString.lastIndexOf(" ")).trim();

                break;
            }

        }

        return middleName + " " + lastName;
    }

    public static long getCurrentTimeInMilies() {

        return System.currentTimeMillis();

    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentDate() {
        return Calendar.getInstance().get(Calendar.DATE);
    }

    public static void getEmojiFacesAndPeople(List<String> inputList) {

        inputList.add("\uD83D\uDE00");
        inputList.add("\uD83D\uDE03");
        inputList.add("\uD83D\uDE04");
        inputList.add("\uD83D\uDE01");
        inputList.add("\uD83D\uDE06");
        inputList.add("\uD83D\uDE05");
        inputList.add("\uD83D\uDE02");
        inputList.add("\uD83D\uDE42");
        inputList.add("\uD83D\uDE09");
        inputList.add("\uD83D\uDE0A");
        inputList.add("\uD83D\uDE07");
        inputList.add("\uD83D\uDE0D");
        inputList.add("\uD83D\uDE18");
        inputList.add("\uD83D\uDE17");
        inputList.add("\uD83D\uDE0A");
        inputList.add("\uD83D\uDE1A");
        inputList.add("\uD83D\uDE19");
        inputList.add("\uD83D\uDE0B");
        inputList.add("\uD83D\uDE1B");
        inputList.add("\uD83D\uDE1C");
        inputList.add("\uD83D\uDE1D");
        inputList.add("\uD83D\uDE10");
        inputList.add("\uD83D\uDE11");
        inputList.add("\uD83D\uDE36");
        inputList.add("\uD83D\uDE0F");
        inputList.add("\uD83D\uDE12");
        inputList.add("\uD83D\uDE2C");
        inputList.add("\uD83D\uDE0C");
        inputList.add("\uD83D\uDE14");
        inputList.add("\uD83D\uDE2A");
        inputList.add("\uD83D\uDE34");
        inputList.add("\uD83D\uDE37");
        inputList.add("\uD83D\uDE35");
        inputList.add("\uD83D\uDE0E");
        inputList.add("\uD83D\uDE15");

        //next 5 rows

        inputList.add("\uD83D\uDE1F");
        inputList.add("\uD83D\uDE2E");
        inputList.add("\uD83D\uDE2F");
        inputList.add("\uD83D\uDE32");
        inputList.add("\uD83D\uDE33");
        inputList.add("\uD83D\uDE26");
        inputList.add("\uD83D\uDE27");
        inputList.add("\uD83D\uDE28");
        inputList.add("\uD83D\uDE30");
        inputList.add("\uD83D\uDE25");
        inputList.add("\uD83D\uDE22");
        inputList.add("\uD83D\uDE2D");
        inputList.add("\uD83D\uDE31");
        inputList.add("\uD83D\uDE16");
        inputList.add("\uD83D\uDE23");
        inputList.add("\uD83D\uDE1E");
        inputList.add("\uD83D\uDE13");
        inputList.add("\uD83D\uDE29");
        inputList.add("\uD83D\uDE2B");
        inputList.add("\uD83D\uDE24");
        inputList.add("\uD83D\uDE21");
        inputList.add("\uD83D\uDE20");
        inputList.add("\uD83D\uDE08");
        inputList.add("\uD83D\uDC7F");
        inputList.add("\uD83D\uDC80");
        inputList.add("\uD83D\uDCA9");
        inputList.add("\uD83D\uDC79");
        inputList.add("\uD83D\uDC7A");
        inputList.add("\uD83D\uDC7B");
        inputList.add("\uD83D\uDC7D");
        inputList.add("\uD83D\uDC7E");
        inputList.add("\uD83D\uDE3A");
        inputList.add("\uD83D\uDE38");
        inputList.add("\uD83D\uDE39");
        inputList.add("\uD83D\uDE3B");


        //next 5 rows
        inputList.add("\uD83D\uDE3C");
        inputList.add("\uD83D\uDE3D");
        inputList.add("\uD83D\uDE40");
        inputList.add("\uD83D\uDE3F");
        inputList.add("\uD83D\uDE3E");
        inputList.add("\uD83D\uDE48");
        inputList.add("\uD83D\uDE49");
        inputList.add("\uD83D\uDE4A");
        inputList.add("\uD83D\uDC8B");
        inputList.add("\uD83D\uDC8C");
        inputList.add("\uD83D\uDC98");
        inputList.add("\uD83D\uDC9D");
        inputList.add("\uD83D\uDC96");
        inputList.add("\uD83D\uDC97");//
        inputList.add("\uD83D\uDC93");
        inputList.add("\uD83D\uDC9E");
        inputList.add("\uD83D\uDC95");
        inputList.add("\uD83D\uDC9F");
        inputList.add("\uD83D\uDC94");
        inputList.add("❤");//

        inputList.add("\uD83D\uDC9B");
        inputList.add("\uD83D\uDC9A");
        inputList.add("\uD83D\uDC99");
        inputList.add("\uD83D\uDC9C");
        inputList.add("\uD83D\uDCAF");
        inputList.add("\uD83D\uDCA2");
        inputList.add("\uD83D\uDCA5");
        inputList.add("\uD83D\uDCAB");
        inputList.add("\uD83D\uDCA6");
        inputList.add("\uD83D\uDCA8");
        inputList.add("\uD83D\uDCA3");
        inputList.add("\uD83D\uDCAC");
        inputList.add("\uD83D\uDCAD");
        inputList.add("\uD83D\uDCA4");
        inputList.add("\uD83D\uDC4B");

        //next 5 rows

        inputList.add("✋");
        inputList.add("\uD83D\uDC4C");
        inputList.add("✌");
        inputList.add("\uD83E\uDD1E");
        inputList.add("\uD83E\uDD1F");
        inputList.add("\uD83E\uDD18");
        inputList.add("\uD83E\uDD19");
        inputList.add("\uD83E\uDD1D");
        inputList.add("✍");
        inputList.add("\uD83D\uDD90");
        inputList.add("\uD83D\uDD96");
        inputList.add("\uD83E\uDD1A");
        inputList.add("\uD83D\uDC48");
        inputList.add("\uD83D\uDC49");
        inputList.add("\uD83D\uDC46");
        inputList.add("\uD83D\uDC47");
        inputList.add("☝");
        inputList.add("\uD83D\uDC4D");
        inputList.add("\uD83D\uDC4E");
        inputList.add("✊");
        inputList.add("\uD83E\uDDE0");
        inputList.add("\uD83E\uDDB7");
        inputList.add("\uD83E\uDDB4");
        inputList.add("\uD83D\uDC41");
        inputList.add("\uD83D\uDC4A");
        inputList.add("\uD83D\uDC4F");
        inputList.add("\uD83D\uDE4C");
        inputList.add("\uD83D\uDC50");
        inputList.add("\uD83D\uDE4F");
        inputList.add("\uD83D\uDC85");
        inputList.add("\uD83D\uDCAA");
        inputList.add("\uD83D\uDC42");
        inputList.add("\uD83D\uDC43");
        inputList.add("\uD83D\uDC40");
        inputList.add("\uD83D\uDC45");
        inputList.add("\uD83D\uDC44");
        inputList.add("\uD83D\uDC76");
        inputList.add("\uD83D\uDC66");
        inputList.add("\uD83D\uDC67");
        inputList.add("\uD83D\uDC71");

// next
        inputList.add("\uD83D\uDC68");
        inputList.add("\uD83D\uDC69");
        inputList.add("\uD83D\uDC74");
        inputList.add("\uD83D\uDC75");
        inputList.add("\uD83D\uDE4D");
        inputList.add("\uD83D\uDE4E");
        inputList.add("\uD83D\uDE45");
        inputList.add("\uD83D\uDE46");
        inputList.add("\uD83D\uDC81");
        inputList.add("\uD83D\uDE4B");
        inputList.add("\uD83D\uDE47");
        inputList.add("\uD83D\uDC6E");
        inputList.add("\uD83D\uDC82");
        inputList.add("\uD83D\uDC77");

        inputList.add("\uD83D\uDC69\u200D");
        inputList.add("\uD83D\uDC68\u200D");
        inputList.add("\uD83D\uDC68\u200D\uD83C\uDF93");
        inputList.add("\uD83D\uDC69\u200D\uD83C\uDF93");
        inputList.add("\uD83D\uDC68\u200D\uD83C\uDFEB");
        inputList.add("\uD83D\uDC69\u200D\uD83C\uDFEB");
        inputList.add("\uD83D\uDC68\u200D");
        inputList.add("\uD83D\uDC69\u200D");
        inputList.add("\uD83D\uDC68\u200D\uD83C\uDF3E");
        inputList.add("\uD83D\uDC69\u200D\uD83C\uDF3E");
        inputList.add("\uD83D\uDC68\u200D\uD83C\uDF73");
        inputList.add("\uD83D\uDC69\u200D\uD83C\uDF73");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDD27");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDD27");
        inputList.add("\uD83D\uDC68\u200D\uD83C\uDFED");
        inputList.add("\uD83D\uDC69\u200D\uD83C\uDFED");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDD2C");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDD2C");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDCBB");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDCBB");
        inputList.add("\uD83D\uDC68\u200D\uD83C\uDFA4");
        inputList.add("\uD83D\uDC69\u200D\uD83C\uDFA4");
        inputList.add("\uD83D\uDC68\u200D\uD83C\uDFA8");
        inputList.add("\uD83D\uDC69\u200D\uD83C\uDFA8");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDE80");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDE80");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDE92");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDE92");
        inputList.add("\uD83D\uDC6E\u200D♂️");
        inputList.add("\uD83D\uDC6E\u200D♀️");
        inputList.add("\uD83D\uDD75️");
        inputList.add("\uD83D\uDD75️\u200D♀️");
        inputList.add("\uD83D\uDC82");
        inputList.add("\uD83D\uDC77");
        inputList.add("\uD83D\uDC77\u200D");
        inputList.add("\uD83E\uDDD9");
        inputList.add("\uD83E\uDDD9\u200D");
        inputList.add("\uD83E\uDDDA\u200D");
        inputList.add("\uD83E\uDDDA\u200D");
        inputList.add("\uD83E\uDDDB");
        inputList.add("\uD83E\uDDDB\u200D");
        inputList.add("\uD83E\uDDDC\u200D");
        inputList.add("\uD83E\uDDDC");
        inputList.add("\uD83E\uDDDC\u200D");
        inputList.add("\uD83E\uDD34");
        inputList.add("\uD83D\uDC78");
        inputList.add("\uD83D\uDC72");
        inputList.add("\uD83E\uDDD5");
        inputList.add("\uD83D\uDC70");
        inputList.add("\uD83E\uDD35");
        inputList.add("\uD83E\uDD30");
        inputList.add("\uD83E\uDD31");
        inputList.add("\uD83D\uDC7C");
        inputList.add("\uD83E\uDDB8");
        inputList.add("\uD83E\uDDB9");
        inputList.add("\uD83D\uDC09");
        inputList.add("\uD83D\uDC78");
        inputList.add("\uD83D\uDC73");
        inputList.add("\uD83D\uDC72");
        inputList.add("\uD83D\uDC70");
        inputList.add("\uD83D\uDC7C");
        inputList.add("\uD83C\uDF85");
        inputList.add("\uD83D\uDC86");
        inputList.add("\uD83D\uDC87");
        inputList.add("\uD83D\uDC87\u200D");
        inputList.add("\uD83D\uDEB6");
        inputList.add("\uD83C\uDFC3");
        inputList.add("\uD83D\uDC83");

        //next 34

        inputList.add("\uD83D\uDC6F");
        inputList.add("\uD83C\uDFC7");
        inputList.add("\uD83C\uDFC2");
        inputList.add("\uD83C\uDFC4");
        inputList.add("\uD83D\uDEA3");
        inputList.add("\uD83C\uDFCA");
        inputList.add("\uD83D\uDEB4");
        inputList.add("\uD83D\uDEB5");
        inputList.add("\uD83D\uDEC0");
        inputList.add("\uD83D\uDC6D");
        inputList.add("\uD83D\uDC6B");
        inputList.add("\uD83D\uDC6C");
        inputList.add("\uD83D\uDC8F");
        inputList.add("\uD83D\uDC68\u200D❤\u200D\uD83D\uDC8B\u200D\uD83D\uDC68");
        inputList.add("\uD83D\uDC69\u200D❤\u200D\uD83D\uDC8B\u200D\uD83D\uDC69");
        inputList.add("\uD83D\uDC91");
        inputList.add("\uD83D\uDC68\u200D❤\u200D\uD83D\uDC68");
        inputList.add("\uD83D\uDC69\u200D❤\u200D\uD83D\uDC69");
        inputList.add("\uD83D\uDC6A");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC67");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66");
        inputList.add("\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67");
        inputList.add("\uD83D\uDC64");
        inputList.add("\uD83D\uDC65");
        inputList.add("\uD83D\uDC63");
    }

    public static void getEmojiAnimalsAndPlants(List<String> inputList) {
        //================ANIMALS & PLANT===========================

        inputList.add("\uD83D\uDC35");
        inputList.add("\uD83D\uDC12");
        inputList.add("\uD83D\uDC36");
        inputList.add("\uD83D\uDC15");
        inputList.add("\uD83D\uDC29");
        inputList.add("\uD83D\uDC3A");
        inputList.add("\uD83D\uDC31");
        inputList.add("\uD83D\uDC08");
        inputList.add("\uD83D\uDC2F");
        inputList.add("\uD83D\uDC05");
        inputList.add("\uD83D\uDC06");
        inputList.add("\uD83D\uDC34");
        inputList.add("\uD83D\uDC0E");
        inputList.add("\uD83D\uDC2E");
        inputList.add("\uD83D\uDC02");
        inputList.add("\uD83D\uDC03");
        inputList.add("\uD83D\uDC04");
        inputList.add("\uD83D\uDC37");
        inputList.add("\uD83D\uDC16");
        inputList.add("\uD83D\uDC17");
        inputList.add("\uD83D\uDC3D");
        inputList.add("\uD83D\uDC0F");
        inputList.add("\uD83D\uDC11");
        inputList.add("\uD83D\uDC10");
        inputList.add("\uD83D\uDC2A");
        inputList.add("\uD83D\uDC2B");
        inputList.add("\uD83D\uDC18");
        inputList.add("\uD83D\uDC2D");
        inputList.add("\uD83D\uDC01");
        inputList.add("\uD83D\uDC00");
        inputList.add("\uD83D\uDC39");
        inputList.add("\uD83D\uDC30");
        inputList.add("\uD83D\uDC07");

        //30
        inputList.add("\uD83D\uDC3B");
        inputList.add("\uD83D\uDC28");
        inputList.add("\uD83D\uDC3C");
        inputList.add("\uD83D\uDC3E");
        inputList.add("\uD83D\uDC14");
        inputList.add("\uD83D\uDC13");
        inputList.add("\uD83D\uDC23");
        inputList.add("\uD83D\uDC24");
        inputList.add("\uD83D\uDC26");
        inputList.add("\uD83D\uDC27");
        inputList.add("\uD83D\uDC38");
        inputList.add("\uD83D\uDC0A");
        inputList.add("\uD83D\uDC22");
        inputList.add("\uD83D\uDC0D");
        inputList.add("\uD83D\uDC32");
        inputList.add("\uD83D\uDC09");
        inputList.add("\uD83D\uDC0B");
        inputList.add("\uD83D\uDC33");
        inputList.add("\uD83D\uDC0B");
        inputList.add("\uD83D\uDC2C");
        inputList.add("\uD83D\uDC1F");
        inputList.add("\uD83D\uDC20");
        inputList.add("\uD83D\uDC21");
        inputList.add("\uD83D\uDC19");
        inputList.add("\uD83D\uDC1A");
        inputList.add("\uD83D\uDC0C");
        inputList.add("\uD83D\uDC1B");
        inputList.add("\uD83D\uDC1C");
        inputList.add("\uD83D\uDC1D");
        inputList.add("\uD83D\uDC1E");


        //-----------------------plant----------------------------

        // 19

        inputList.add("\uD83D\uDC90");
        inputList.add("\uD83C\uDF38");
        inputList.add("\uD83D\uDCAE");
        inputList.add("\uD83C\uDF39");
        inputList.add("\uD83C\uDF3A");
        inputList.add("\uD83C\uDF3B");
        inputList.add("\uD83C\uDF3C");
        inputList.add("\uD83C\uDF37");
        inputList.add("\uD83C\uDF31");
        inputList.add("\uD83C\uDF32");
        inputList.add("\uD83C\uDF33");
        inputList.add("\uD83C\uDF34");
        inputList.add("\uD83C\uDF35");
        inputList.add("\uD83C\uDF3E");
        inputList.add("\uD83C\uDF3F");
        inputList.add("\uD83C\uDF40");
        inputList.add("\uD83C\uDF41");
        inputList.add("\uD83C\uDF42");
        inputList.add("\uD83C\uDF43");
    }

    public static void getEmojiFoods(List<String> inputList) {
        inputList.add("\uD83C\uDF47");
        inputList.add("\uD83C\uDF48");
        inputList.add("\uD83C\uDF49");
        inputList.add("\uD83C\uDF4A");
        inputList.add("\uD83C\uDF4B");
        inputList.add("\uD83C\uDF4C");
        inputList.add("\uD83C\uDF4D");
        inputList.add("\uD83C\uDF4E");
        inputList.add("\uD83C\uDF4F");
        inputList.add("\uD83C\uDF50");
        inputList.add("\uD83C\uDF51");
        inputList.add("\uD83C\uDF52");
        inputList.add("\uD83C\uDF53");
        inputList.add("\uD83C\uDF45");
        inputList.add("\uD83C\uDF46");
        inputList.add("\uD83C\uDF3D");
        inputList.add("\uD83C\uDF44");
        inputList.add("\uD83C\uDF30");
        inputList.add("\uD83C\uDF5E");
        inputList.add("\uD83C\uDF56");
        inputList.add("\uD83C\uDF57");
        inputList.add("\uD83C\uDF54");
        inputList.add("\uD83C\uDF5F");
        inputList.add("\uD83C\uDF55");
        inputList.add("\uD83C\uDF73");
        inputList.add("\uD83C\uDF72");
        inputList.add("\uD83C\uDF71");
        inputList.add("\uD83C\uDF58");
        inputList.add("\uD83C\uDF59");
        inputList.add("\uD83C\uDF5A");
        inputList.add("\uD83C\uDF5B");
        inputList.add("\uD83C\uDF5C");
        inputList.add("\uD83C\uDF5D");
        inputList.add("\uD83C\uDF60");
        inputList.add("\uD83C\uDF62");

        //27
        inputList.add("\uD83C\uDF63");
        inputList.add("\uD83C\uDF64");
        inputList.add("\uD83C\uDF65");
        inputList.add("\uD83C\uDF61");
        inputList.add("\uD83C\uDF66");
        inputList.add("\uD83C\uDF67");
        inputList.add("\uD83C\uDF68");
        inputList.add("\uD83C\uDF69");
        inputList.add("\uD83C\uDF6A");
        inputList.add("\uD83C\uDF82");
        inputList.add("\uD83C\uDF70");
        inputList.add("\uD83C\uDF6B");
        inputList.add("\uD83C\uDF6C");
        inputList.add("\uD83C\uDF6D");
        inputList.add("\uD83C\uDF6E");
        inputList.add("\uD83C\uDF6F");
        inputList.add("\uD83C\uDF7C");
        inputList.add("☕");
        inputList.add("\uD83C\uDF75");
        inputList.add("\uD83C\uDF76");
        inputList.add("\uD83C\uDF77");
        inputList.add("\uD83C\uDF78");
        inputList.add("\uD83C\uDF79");
        inputList.add("\uD83C\uDF7A");
        inputList.add("\uD83C\uDF7B");
        inputList.add("\uD83C\uDF74");
        inputList.add("\uD83D\uDD2A");
    }

    public static void getEmojiPlacesAndVehicles(List<String> inputList) {

        //----------places-------------------
        //36
        inputList.add("\uD83C\uDF0D");
        inputList.add("\uD83C\uDF0E");
        inputList.add("\uD83C\uDF0F");
        inputList.add("\uD83C\uDF10");
        inputList.add("\uD83D\uDDFE");
        inputList.add("\uD83C\uDF0B");
        inputList.add("\uD83D\uDDFB");
        inputList.add("\uD83C\uDFE0");
        inputList.add("\uD83C\uDFE1");
        inputList.add("\uD83C\uDFE2");
        inputList.add("\uD83C\uDFE3");
        inputList.add("\uD83C\uDFE4");
        inputList.add("\uD83C\uDFE5");
        inputList.add("\uD83C\uDFE6");
        inputList.add("\uD83C\uDFE8");
        inputList.add("\uD83C\uDFE9");
        inputList.add("\uD83C\uDFEA");
        inputList.add("\uD83C\uDFEB");
        inputList.add("\uD83C\uDFEC");
        inputList.add("\uD83C\uDFED");
        inputList.add("\uD83C\uDFEF");
        inputList.add("\uD83C\uDFF0");
        inputList.add("\uD83D\uDC92");
        inputList.add("\uD83D\uDDFC");
        inputList.add("\uD83D\uDDFD");
        inputList.add("⛪");
        inputList.add("⛲");
        inputList.add("⛺");
        inputList.add("\uD83C\uDF01");
        inputList.add("\uD83C\uDF03");
        inputList.add("\uD83C\uDF04");
        inputList.add("\uD83C\uDF05");
        inputList.add("\uD83C\uDF06");
        inputList.add("\uD83C\uDF07");
        inputList.add("\uD83C\uDF09");
        inputList.add("♨");
        inputList.add("\uD83C\uDFA0");
        inputList.add("\uD83C\uDFA1");
        inputList.add("\uD83C\uDFA2");
        inputList.add("\uD83D\uDC88");
        inputList.add("\uD83C\uDFAA");

        //----------------------------traffic------------------
        //37
        inputList.add("\uD83D\uDE82");
        inputList.add("\uD83D\uDE83");
        inputList.add("\uD83D\uDE84");
        inputList.add("\uD83D\uDE85");
        inputList.add("\uD83D\uDE86");
        inputList.add("\uD83D\uDE87");
        inputList.add("\uD83D\uDE88");
        inputList.add("\uD83D\uDE89");
        inputList.add("\uD83D\uDE8A");
        inputList.add("\uD83D\uDE9D");
        inputList.add("\uD83D\uDE9E");
        inputList.add("\uD83D\uDE8B");
        inputList.add("\uD83D\uDE8C");
        inputList.add("\uD83D\uDE8D");
        inputList.add("\uD83D\uDE8E");
        inputList.add("\uD83D\uDE90");
        inputList.add("\uD83D\uDE91");
        inputList.add("\uD83D\uDE92");
        inputList.add("\uD83D\uDE93");
        inputList.add("\uD83D\uDE94");
        inputList.add("\uD83D\uDE95");
        inputList.add("\uD83D\uDE96");
        inputList.add("\uD83D\uDE97");
        inputList.add("\uD83D\uDE98");
        inputList.add("\uD83D\uDE99");
        inputList.add("\uD83D\uDE9A");
        inputList.add("\uD83D\uDE9B");
        inputList.add("\uD83D\uDE9C");
        inputList.add("\uD83D\uDEB2");
        inputList.add("\uD83D\uDE8F");
        inputList.add("⛽");
        inputList.add("\uD83D\uDEA8");
        inputList.add("\uD83D\uDEA5");
        inputList.add("\uD83D\uDEA6");
        inputList.add("\uD83D\uDEA7");
        inputList.add("⚓");
        inputList.add("⛵");
        inputList.add("\uD83D\uDEA4");
        inputList.add("\uD83D\uDEA2");
        inputList.add("✈");
        inputList.add("\uD83D\uDCBA");
        inputList.add("\uD83D\uDE81");
        inputList.add("\uD83D\uDE9F");
        inputList.add("\uD83D\uDEA0");
        inputList.add("\uD83D\uDEA1");
        inputList.add("\uD83D\uDE80");

    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public static Message buildInstantMessage(String messageId, String receiverId, String groupId, String content, int type, int notificationId, Map<String, Boolean> relatedUserId) {
        return new Message(
                messageId, getMyFirebaseUserId(), receiverId, groupId,
                content, MESSAGE_STATUS_SENT,
                getCurrentTimeInMilies(), 0, type,
                notificationId, 0, null, false, relatedUserId, null, null);
    }

    public static void getLocalOrLoadMyProfile(AppConstants.AppInterfaces.FirebaseUserProfileCallBack firebaseUserProfileCallBack) {

        if (getMyGlobalProfile() != null) {

            firebaseUserProfileCallBack.OnCallBack(getMyGlobalProfile());

        } else {

            getSingleUserProfile(getMyFirebaseUserId(), new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                @Override
                public void OnCallBack(User callbackUserProfile) {

                    firebaseUserProfileCallBack.OnCallBack(callbackUserProfile);

                }
            });
        }
    }

    public static Object getDataFromIntent(Activity activity, String key) {

        if (activity.getIntent().getExtras() != null && activity.getIntent().getExtras().get(key) != null)

            return activity.getIntent().getExtras().get(key);

        return null;

    }

    public static String getUserIdFromQRCode(Context context, String code) {

        if (!code.contains(context.getResources().getString(R.string.project_id) + "_code_"))
            return null;

        return code.replaceAll(context.getResources().getString(R.string.project_id) + "_code_", "");
    }

    public static String generateMediaName(int type) {
        if (type == MEDIA_TYPE_PICTURE) {
            return "image_" + UUID.randomUUID().toString().replaceAll("-", "") + "_" + getCurrentTimeInMilies() + ".jpg";
        } else if (type == MEDIA_TYPE_VIDEO) {
            return "video_" + UUID.randomUUID().toString().replaceAll("-", "") + "_" + getCurrentTimeInMilies() + ".mp4";
        } else {
            return "audio_" + UUID.randomUUID().toString().replaceAll("-", "") + "_" + getCurrentTimeInMilies() + ".3gp";
        }


    }

    public static Map<String, Uri> buildUriMapAfterUpload(Uri fullSizeUri, Uri thumbSizeUri) {
        Map<String, Uri> map = new HashMap<>();

        map.put(KEY_FULL_SIZE_MEDIA, fullSizeUri);
        map.put(KEY_THUMB_SIZE_MEDIA, thumbSizeUri);

        return map;
    }

}
