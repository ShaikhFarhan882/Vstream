package com.example.vstream;

public class Member {

    private String videoTitle;
    private String videoURL;
    private String videoCategory;
    private String currentUserId;

    //Default Constructor
    public Member() {

    }

    public Member(String videoTitle, String videoURL,String videoCategory,String currentUserId) {

        if(videoTitle.trim().equals("")){
            videoTitle = "Not available";
        }
        this.videoTitle = videoTitle;
        this.videoURL = videoURL;
        this.videoCategory = videoCategory;
        this.currentUserId = currentUserId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}

