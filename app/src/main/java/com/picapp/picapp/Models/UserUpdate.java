package com.picapp.picapp.Models;

import android.net.Uri;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;

public class UserUpdate {

    @SerializedName("name")
    @Expose
    private String username;

    @SerializedName("profile_pic")
    @Expose
    private Uri imgUrl;

    public String getUsername() {
        return username;
    }

    public Uri getImg() { return this.imgUrl; }

    public void setUsername(String username) { this.username = username; }

    public void setProfilePhoto(Uri imgUrl) { this.imgUrl = imgUrl; }

}