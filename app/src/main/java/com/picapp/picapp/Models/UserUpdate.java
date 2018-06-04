package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserUpdate {

    @SerializedName("name")
    @Expose
    private String username;

    @SerializedName("profile_pic")
    @Expose
    private String imgUrl;

    public String getUsername() {
        return username;
    }

    public String getImg() { return imgUrl; }

    public void setUsername(String username) { this.username = username; }

    public void setProfilePhoto(String imgUrl) { this.imgUrl = imgUrl; }

}