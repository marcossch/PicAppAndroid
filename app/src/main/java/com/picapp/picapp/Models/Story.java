package com.picapp.picapp.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Story {

    @SerializedName("comments")
    @Expose
    private ArrayList<Comment> comments = null;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("is_private")
    @Expose
    private String isPrivate;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("media")
    @Expose
    private String media;
    @SerializedName("reactions")
    @Expose
    private Map<String, String> reactions = null;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("story_id")
    @Expose
    private String story_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;


    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Map<String, String> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, String> reactions) {
        this.reactions = reactions;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

}