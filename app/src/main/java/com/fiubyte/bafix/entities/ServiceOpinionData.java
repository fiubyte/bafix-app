package com.fiubyte.bafix.entities;

public class ServiceOpinionData {
    private String userName;
    private int userRating;
    private String userOpinion;

    public ServiceOpinionData(String userName, int userRating, String userOpinion) {
        this.userName = userName;
        this.userRating = userRating;
        this.userOpinion = userOpinion;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserRating() {
        return userRating;
    }

    public String getUserOpinion() {
        return userOpinion;
    }
}
