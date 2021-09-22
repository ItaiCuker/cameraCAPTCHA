
package com.itaicuker.cameracaptcha;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImgurResponse {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("status")
    @Expose
    private int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Data {


        @SerializedName("deletehash")
        @Expose
        private String deleteHash;
        @SerializedName("link")
        @Expose
        private String link;

        public String getDeleteHash() {
            return deleteHash;
        }

        public void setDeleteHash(String deleteHash) {
            this.deleteHash = deleteHash;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }

}
