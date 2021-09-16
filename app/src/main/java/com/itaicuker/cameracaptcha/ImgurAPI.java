package com.itaicuker.cameracaptcha;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ImgurAPI
{
    @Multipart
    @Headers("Authorization: Client-ID 51de28e6e78f328")

    @POST("upload")
    Call<ImageResponse> postImage(
            @Part MultipartBody.Part file
    );

    @HTTP(method = "DELETE", path = "image/{{imageDeleteHash}}", hasBody = true)
    Call<ResponseBody> deleteImage(
            @Body RequestBody body
    );

    public static final String BASE_URL = "https://api.imgur.com/3/";

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
