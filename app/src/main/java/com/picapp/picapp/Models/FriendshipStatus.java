package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendshipStatus {

    @SerializedName("friendship_state")
    @Expose
    private String friendship_state;

    public String getState() {
        return friendship_state;
    }

    public void setState(String status) {
        this.friendship_state = status;
    }
}
