package com.fiubyte.bafix.entities;

public class ServiceOpinionData {
    private String userName;
    private int userRating;
    private String userOpinion;

    private Boolean approved;

    public ServiceOpinionData(String userName, int userRating, String userOpinion, Boolean approved) {
        this.userName = userName;
        this.userRating = userRating;
        this.userOpinion = userOpinion;
        this.approved = approved;
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

    public Boolean isApproved() { return approved; }
}
