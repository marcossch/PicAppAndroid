package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("expiresAt")
    @Expose
    private Double expiresAt;
    @SerializedName("token")
    @Expose
    private Integer token;

    public Double getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Double expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

}