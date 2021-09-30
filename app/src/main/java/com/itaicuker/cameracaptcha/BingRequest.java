package com.itaicuker.cameracaptcha;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * this class emulates a JSON object
 */
public class BingRequest {

    @JsonProperty("imageInfo")
    private ImageInfo imageInfo;

    public BingRequest(String url) {
         imageInfo = new ImageInfo(url);
    }

    public class ImageInfo {

        @JsonProperty("url")
        private String url;

        public ImageInfo(String url) {
            this.url = url;
        }
    }
}
