package com.lucifer.personal;

public class News {
    String title,description,contentUrl,imageUrl,publishedAt,sourceName,sourceUrl;

    public News(){
        //default constructor
    }

    public News(String title,String description,String contentUrl,String imageUrl,String publishedAt,String sourceName,String sourceUrl){
        this.title = title;
        this.description=description;
        this.contentUrl=contentUrl;
        this.imageUrl=imageUrl;
        this.publishedAt=publishedAt;
        this.sourceName=sourceName;
        this.sourceUrl=sourceUrl;
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

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
