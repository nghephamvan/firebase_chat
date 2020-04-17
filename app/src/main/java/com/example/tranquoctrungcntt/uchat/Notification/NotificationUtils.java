package com.example.tranquoctrungcntt.uchat.Notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_TOKENS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NotificationType.NOTIFICATION_TYPE_REMOVE_MESSAGE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLastAndMiddleName;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;


public class NotificationUtils {


    public static final APIService API_SERVICE = Client.getClient().create(APIService.class);


    public static int getNotificationsId() {
        return (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    }

    public static void sendPayloadToServer(final DataToSend data, final String token) {

        NotificationPackage notificationPackage = new NotificationPackage(data, token);

        API_SERVICE.sendNotification(notificationPackage).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {


            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });


    }

    public static void sendNotificationToUser(final String receiverId,
                                              final String notificationSenderId,
                                              final String groupId,
                                              final String messageId,
                                              final String notificationContent,
                                              final String notificationTitle,
                                              final String notificationAvatar,
                                              final int notificationType,
                                              final int notificationId) {


        if (notificationType == NOTIFICATION_TYPE_REMOVE_MESSAGE) {
            final DataToSend removeDataToSend = new DataToSend(
                    receiverId, notificationSenderId, groupId,
                    messageId, notificationTitle, notificationAvatar, notificationContent,
                    notificationType, notificationId);

            ROOT_REF.child(CHILD_TOKENS).child(receiverId)
                    .runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                            return Transaction.success(mutableData);

                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Token token = snapshot.getValue(Token.class);

                                sendPayloadToServer(removeDataToSend, token.getToken());

                            }
                        }
                    });
        } else {

            if (groupId != null) {

                getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User callbackUserProfile) {

                        final String content = getLastAndMiddleName(callbackUserProfile.getName()) + ":" + " " + notificationContent;

                        final DataToSend groupDataToSend = new DataToSend(
                                receiverId, notificationSenderId, groupId,
                                messageId, notificationTitle, notificationAvatar, content,
                                notificationType, notificationId);

                        ROOT_REF.child(CHILD_TOKENS).child(receiverId).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                                return Transaction.success(mutableData);

                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {


                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    final Token token = snapshot.getValue(Token.class);

                                    sendPayloadToServer(groupDataToSend, token.getToken());

                                }


                            }
                        });

                    }
                });


            } else {

                final DataToSend singleDataToSend = new DataToSend(
                        receiverId, notificationSenderId, groupId,
                        messageId, notificationTitle, notificationAvatar, notificationContent,
                        notificationType, notificationId);

                ROOT_REF.child(CHILD_TOKENS).child(receiverId).runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            final Token token = snapshot.getValue(Token.class);
                            sendPayloadToServer(singleDataToSend, token.getToken());

                        }


                    }
                });

            }

        }

    }


}
