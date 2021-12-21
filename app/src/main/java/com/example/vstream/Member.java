package com.example.vstream;

public class Member {

    private String videoTitle;
    private String videoURL;

    //Default Constructor
    public Member() {

    }

    public Member(String videoTitle, String videoURL) {
        this.videoTitle = videoTitle;
        this.videoURL = videoURL;;
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

}

