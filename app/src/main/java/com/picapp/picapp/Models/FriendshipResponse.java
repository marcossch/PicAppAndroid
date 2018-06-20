package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendshipResponse {
    @SerializedName("target_user_id")
    @Expose
    private String id;

    public String getState() {
        return id;
    }

    public void setState(String id) {
        this.id = id;
    }

}
