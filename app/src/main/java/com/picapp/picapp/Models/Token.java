package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("expiresAt")
    @Expose
    private Integer expiresAt;
    @SerializedName("token")
    @Expose
    private Integer token;

    public Integer getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Integer expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

}