package com.itaicuker.cameracaptcha;


import com.fasterxml.jackson.annotation.JsonProperty;

public class BingRequest {
    @JsonProperty("imageInfo")
    private final ImageInfo imageInfo;

    public BingRequest(String url) {
        this.imageInfo = new ImageInfo(url);
    }

    private class ImageInfo {

        @JsonProperty("url")
        private final String url;

        public ImageInfo(String url) {
            this.url = url;
        }
    }
}
