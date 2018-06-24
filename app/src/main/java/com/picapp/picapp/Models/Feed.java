package com.picapp.picapp.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feed {

    @SerializedName("stories")
    @Expose
    private List<Story> stories = null;

    public List<Story> getStories() {
        return stories;
    }

    public boolean isEmpty(){
        return stories == null;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

}