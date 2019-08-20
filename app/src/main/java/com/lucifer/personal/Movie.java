package com.lucifer.personal;

public class Movie {
    public String director;
    public String duration;
    public String imgUrl;
    public String lang;
    public String movieName;
    public String star;
    public String type;
    public String utubeId;
    public String videoUrl;
    public String dubbed;
    public String description;

    public Movie(){}

    public Movie(String director, String duration, String imgUrl, String lang, String movieName, String star, String type, String utubeId, String videoUrl, String description, String dubbed){
        this.director = director;
        this.duration = duration;
        this.imgUrl = imgUrl;
        this.lang = lang;
        this.movieName = movieName;
        this.star = star;
        this.type = type;
        this.utubeId = utubeId;
        this.videoUrl = videoUrl;
        this.description = description;
        this.dubbed = dubbed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUtubeId() {
        return utubeId;
    }

    public void setUtubeId(String utubeId) {
        this.utubeId = utubeId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDubbed() {
        return dubbed;
    }

    public void setDubbed(String dubbed) {
        this.dubbed = dubbed;
    }

}
