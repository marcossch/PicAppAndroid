package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reaction {

    @SerializedName("reacting_user_id")
    @Expose
    private String reactingUserId;
    @SerializedName("reaction")
    @Expose
    private String reaction;

    public String getReactingUserId() {
        return reactingUserId;
    }

    public void setReactingUserId(String reactingUserId) {
        this.reactingUserId = reactingUserId;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

}