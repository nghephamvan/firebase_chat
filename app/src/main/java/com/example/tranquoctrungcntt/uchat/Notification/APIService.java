package com.example.tranquoctrungcntt.uchat.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAkbm-8ww:APA91bF13brz0elIg6yp_C56XxkK5Zhcm_Cu2K07zjNdPQCAv4DNA4HR9hKBsAug9vIv9r1hB8y8_HrHJAHoNYBItpSmMb3qEo0a9y5ItuDffNCgmnvYDl6w1qF3mpdW5da_PBOVgAJ4"

            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationPackage body);
}
