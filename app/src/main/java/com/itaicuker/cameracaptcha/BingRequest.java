package com.itaicuker.cameracaptcha;


/**
 * this class emulates a JSON object
 */
public class BingRequest {

    public BingRequest(String url) {
        ImageInfo imageInfo = new ImageInfo(url);
    }

    private static class ImageInfo {

        public ImageInfo(String url) {
        }
    }
}
