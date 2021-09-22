package com.itaicuker.cameracaptcha;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageAction;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageKnowledge;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageModuleAction;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptchaClient {
    private static final ClientAPI CLIENT_API = ClientAPI.retrofit.create(ClientAPI.class);

    static void uploadImage(File photoFile) {
        Log.d("monkey", "in testExecute");
        Call<ImgurResponse> call =
                CLIENT_API.postImage(
                        MultipartBody.Part.createFormData(
                                "image",
                                photoFile.getName(),
                                RequestBody.create(photoFile, MediaType.parse("image/*"))));
        call.enqueue(new Callback<ImgurResponse>() {
            @Override
            public void onResponse(Call<ImgurResponse> call, retrofit2.Response<ImgurResponse> response) {
                ImgurResponse tmp = response.body();
                if (response.isSuccessful()) {
                    Log.d("Imgur API", "upload success! =" + tmp.getStatus());
                    reverseImageSearch(tmp.getData().getLink());
                } else
                    Log.d("Imgur API", "upload no success! =" + tmp.getStatus());
            }

            @Override
            public void onFailure(Call<ImgurResponse> call, Throwable t) {
                Log.d("Imgur API", "upload fail! =" + t.toString());
            }
        });
    }

    static void reverseImageSearch(String link) {
        Call<ImageKnowledge> call =
                null;
        try {
            call = CLIENT_API.getReverseImageSearch(
                    RequestBody.create(
                            ClientAPI.mapper.writeValueAsString(new BingRequest(link)),
                            MediaType.parse("application/json")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback<ImageKnowledge>() {
            @Override
            public void onResponse(Call<ImageKnowledge> call, Response<ImageKnowledge> response) {
                if (response.isSuccessful()) {
                    List<ImageObject> lst = null;
                    for (ImageAction tmp : response.body().tags().get(0).actions()) {
                        if (tmp.actionType().equals("VisualSearch")) {
                            lst = ((ImageModuleAction) tmp).data().value();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageKnowledge> call, Throwable t) {

            }
        });
    }
}
