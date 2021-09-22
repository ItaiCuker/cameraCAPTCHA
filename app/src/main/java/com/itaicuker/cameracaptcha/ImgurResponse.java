
package com.itaicuker.cameracaptcha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImgurResponse {
    @JsonProperty("data")
    private Data data;
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("status")
    private int status;

    public Data getData() {
        return data;
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

        @JsonProperty("deletehash")
        private String deleteHash;
        @JsonProperty("link")
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
    }

}
