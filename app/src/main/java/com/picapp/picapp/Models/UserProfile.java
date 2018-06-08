package com.picapp.picapp.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfile {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("number of friends")
    @Expose
    private Long numberOfFriends;
    @SerializedName("number of stories")
    @Expose
    private Long numberOfStories;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("stories")
    @Expose
    private List<Story> stories = null;
    @SerializedName("username")
    @Expose
    private String username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumberOfFriends() {
        return numberOfFriends;
    }

    public void setNumberOfFriends(Long numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
    }

    public Long getNumberOfStories() {
        return numberOfStories;
    }

    public void setNumberOfStories(Long numberOfStories) {
        this.numberOfStories = numberOfStories;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}