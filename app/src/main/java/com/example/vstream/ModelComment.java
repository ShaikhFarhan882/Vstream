package com.example.vstream;

public class ModelComment {
    //Attribute names which are present in the database
    String Date,Time;
    String userId,userImage;
    String userMessage,userName;

    //Default Constructor
    public ModelComment(){

    }

    public ModelComment(String date, String time, String userId, String userImage, String userMessage, String userName) {
        Date = date;
        Time = time;
        this.userId = userId;
        this.userImage = userImage;
        this.userMessage = userMessage;
        this.userName = userName;
    }

    //Getter Setter

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

