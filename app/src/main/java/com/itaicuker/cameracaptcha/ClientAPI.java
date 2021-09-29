package com.itaicuker.cameracaptcha;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageKnowledge;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ClientAPI
{
    /**
     * API POST request to upload file to imgur.
     *
     * @return imgurResponse object with imgur url of photo.
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
     * @return ImageKnowledge object with bing results (ImageKnowledge is from the Azure SDK I used)
     */
    @Multipart
    @Headers({"Ocp-Apim-Subscription-Key: " + BING_KEY})
    @POST(BING_BASE_URL + "v7.0/images/visualsearch")
    Call<ImageKnowledge> getReverseImageSearch(
            @Part("knowledgeRequest") RequestBody request);

    String BING_BASE_URL = "https://api.bing.microsoft.com/";
    String BING_KEY = "20a82b96626c4871b8857a92c11f8efa";
    ObjectMapper mapper = new ObjectMapper()    //init jackson object parser.
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()    //init okHttp client, so it won't timeout connection
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
    Retrofit retrofit = new Retrofit.Builder()  //init retrofit object
            .baseUrl("http://localhost/")
            .addConverterFactory(JacksonConverterFactory.create(mapper))    //telling retrofit to use jackson to parse JSON's
            .client(httpClient.build())
            .build();
}
