package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendsList {

    @SerializedName("friends")
    @Expose
    private List<UserAccount> friendsList;

    public List<UserAccount> getUsers() {
        return friendsList;
    }

    public void setList(List<UserAccount> list) {
        this.friendsList = list;
    }
}
