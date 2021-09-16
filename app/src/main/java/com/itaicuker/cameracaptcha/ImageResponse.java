
package com.itaicuker.cameracaptcha;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResponse {

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

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("datetime")
        @Expose
        private int datetime;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("animated")
        @Expose
        private boolean animated;
        @SerializedName("width")
        @Expose
        private int width;
        @SerializedName("height")
        @Expose
        private int height;
        @SerializedName("size")
        @Expose
        private int size;
        @SerializedName("views")
        @Expose
        private int views;
        @SerializedName("bandwidth")
        @Expose
        private int bandwidth;
        @SerializedName("vote")
        @Expose
        private String vote;
        @SerializedName("favorite")
        @Expose
        private boolean favorite;
        @SerializedName("nsfw")
        @Expose
        private String nsfw;
        @SerializedName("section")
        @Expose
        private String section;
        @SerializedName("account_url")
        @Expose
        private String accountUrl;
        @SerializedName("account_id")
        @Expose
        private int accountId;
        @SerializedName("is_ad")
        @Expose
        private boolean isAd;
        @SerializedName("in_most_viral")
        @Expose
        private boolean inMostViral;
        @SerializedName("tags")
        @Expose
        private List<String> tags = null;
        @SerializedName("ad_type")
        @Expose
        private int adType;
        @SerializedName("ad_url")
        @Expose
        private String adUrl;
        @SerializedName("in_gallery")
        @Expose
        private boolean inGallery;
        @SerializedName("deletehash")
        @Expose
        private String deleteHash;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("link")
        @Expose
        private String link;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDatetime() {
            return datetime;
        }

        public void setDatetime(int datetime) {
            this.datetime = datetime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isAnimated() {
            return animated;
        }

        public void setAnimated(boolean animated) {
            this.animated = animated;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(int bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getVote() {
            return vote;
        }

        public void setVote(String vote) {
            this.vote = vote;
        }

        public boolean isFavorite() {
            return favorite;
        }

        public void setFavorite(boolean favorite) {
            this.favorite = favorite;
        }

        public String getNsfw() {
            return nsfw;
        }

        public void setNsfw(String nsfw) {
            this.nsfw = nsfw;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getAccountUrl() {
            return accountUrl;
        }

        public void setAccountUrl(String accountUrl) {
            this.accountUrl = accountUrl;
        }

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }

        public boolean isIsAd() {
            return isAd;
        }

        public void setIsAd(boolean isAd) {
            this.isAd = isAd;
        }

        public boolean isInMostViral() {
            return inMostViral;
        }

        public void setInMostViral(boolean inMostViral) {
            this.inMostViral = inMostViral;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public int getAdType() {
            return adType;
        }

        public void setAdType(int adType) {
            this.adType = adType;
        }

        public String getAdUrl() {
            return adUrl;
        }

        public void setAdUrl(String adUrl) {
            this.adUrl = adUrl;
        }

        public boolean isInGallery() {
            return inGallery;
        }

        public void setInGallery(boolean inGallery) {
            this.inGallery = inGallery;
        }

        public String getDeleteHash() {
            return deleteHash;
        }

        public void setDeleteHash(String deleteHash) {
            this.deleteHash = deleteHash;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }

}
