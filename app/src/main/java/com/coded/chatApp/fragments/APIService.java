package com.coded.chatApp.fragments;

import com.coded.chatApp.Notifications.MyResponse;
import com.coded.chatApp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAjeoUOfA:APA91bEU2KXdeGzDOst0Hu8bu033M8Eg1J_OxluW2uYFwgGDLx_tFvukvdXiBadMdyVoagKLCcZuIVlcs5QvZSB3T-c1BI39f6ryrTU3a-ctYZJ0zfPb2X177kKPSzzV9-od1wmAuaLl"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);


}
