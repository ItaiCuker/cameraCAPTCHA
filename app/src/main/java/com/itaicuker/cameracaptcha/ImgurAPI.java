package com.itaicuker.cameracaptcha;


import java.util.concurrent.TimeUnit;

import okhttp3.Response;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    String API_BASE_URL = "https://api.imgur.com/3/";

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5,TimeUnit.MINUTES)
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();
}
