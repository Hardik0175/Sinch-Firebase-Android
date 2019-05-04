package com.coded.chatApp.Models;

public class User {

    private String id;
    private String username;
    private String imageurl;
    private String status;
    private String phonenumber;

    public User(String id, String username, String imageurl, String status, String phonenumber) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.status = status;
        this.phonenumber = phonenumber;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
