package com.itaicuker.cameracaptcha;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BingRequest
{
    @SerializedName("imageInfo")
    @Expose
    private ImageInfo imageInfo;

    public BingRequest(String url) {
        this.imageInfo = new ImageInfo(url);
    }

    private class ImageInfo
    {

        @SerializedName("url")
        @Expose
        private String url;

        public ImageInfo(String url) {
            this.url = url;
        }
    }
}
