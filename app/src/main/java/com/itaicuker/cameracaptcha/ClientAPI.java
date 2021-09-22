package com.itaicuker.cameracaptcha;


import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ClientAPI
{
    /**
     * API POST request to upload file to imgur.
     * @param file
     * @return imageResponse object with imgur url of photo.
     */
    String IMGUR_BASE_URL = "https://api.imgur.com/3/";
    String IMGUR_KEY = "51de28e6e78f328";
    @Multipart
    @Headers("Authorization: Client-ID " + IMGUR_KEY)
    @POST(IMGUR_BASE_URL + "upload")
    Call<ImgurResponse> postImage(
            @Part MultipartBody.Part file
    );

    /**
     * API GET request to get results from bing reverse image
     * @param url
     * @return JSON with bing results
     */
    String BING_BASE_URL = "https://api.bing.microsoft.com/";
    String BING_KEY = "20a82b96626c4871b8857a92c11f8efa";
    @Multipart
    @Headers({"Ocp-Apim-Subscription-Key: " + BING_KEY})
    @POST(BING_BASE_URL + "v7.0/images/visualsearch")
    Call<BingResponse> getReverseImageSearch(
            @Part("knowledgeRequest") RequestBody request);

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5,TimeUnit.MINUTES)
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();
}
