package com.lucifer.personal;

public class LiveTv {
    public String country;
    public String description;
    public String imgUrl;
    public String language;
    public String launched;
    public String name;
    public String network;
    public String owner;
    public String videoId;

    public LiveTv(){

    }

    public LiveTv(String country, String description, String imgUrl, String language, String launched, String name, String network, String owner, String videoId) {
        this.country = country;
        this.description = description;
        this.imgUrl = imgUrl;
        this.language = language;
        this.launched = launched;
        this.name = name;
        this.network = network;
        this.owner = owner;
        this.videoId = videoId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLaunched() {
        return launched;
    }

    public void setLaunched(String launched) {
        this.launched = launched;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
