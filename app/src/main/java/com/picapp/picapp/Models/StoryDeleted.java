package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoryDeleted {

    @SerializedName("target_story_id")
    @Expose
    private String target_story_id;

    public String getStoryId() {
        return target_story_id;
    }

    public void setStoryId(String expiresAt) {
        this.target_story_id = target_story_id;
    }

}
