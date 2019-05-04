package com.coded.chatApp.Models;

public class UserLoc {

    private String userId;
    private String lon;
    private String lat;

    public UserLoc() {
    }

    public UserLoc(String userId, String lon, String lat) {
        this.userId = userId;
        this.lon = lon;
        this.lat = lat;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
