package com.itaicuker.cameracaptcha;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BingResponse {
    @SerializedName("ImageKnowledge")
    @Expose
    public ImageKnowledge imageKnowledge;

    public class ImageKnowledge {
        @SerializedName("tags")
        @Expose
        public List<ImageTag> tags;

        public List<ImageTag> getTags() {
            return tags;
        }
    }

    public class ImageTag {
        @SerializedName("actions")
        @Expose
        public List<ImageAction> actions = null;

        public List<ImageAction> getActions() {
            return actions;
        }
    }

    public class ImageAction {
        @SerializedName("actionType")
        @Expose
        public String actionType;

        public String ActionType() {
            return actionType;
        }
    }

    public class ImageModuleAction {
        @SerializedName("data")
        @Expose
        public ImagesModule data;

        public ImagesModule getData() {
            return data;
        }
    }

    public class ImagesModule {
        @SerializedName("value")
        @Expose
        public List<ImageObject> value = null;

        public List<ImageObject> getValue() {
            return value;
        }
    }

    public class ImageObject {
        @SerializedName("contentUrl")
        @Expose
        public String contentUrl;

        public String getContentUrl() {
            return contentUrl;
        }
    }

    public ImageKnowledge getImageKnowledge() {
        return imageKnowledge;
    }
}