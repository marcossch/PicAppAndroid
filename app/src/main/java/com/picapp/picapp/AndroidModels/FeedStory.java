package com.picapp.picapp.AndroidModels;

import com.picapp.picapp.Models.Comment;
import com.picapp.picapp.Models.Reactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedStory {

    public String description;
    public String image;
    public String image_id;
    public String location;
    public String thumb;
    public String title;
    public String user_id;
    public String profPic = null;
    public String name;
    public String profile_pic;
    public Long timestamp;
    public Boolean isPrivate;
    public Map<String, String> reactions = new HashMap<>();
    public ArrayList<Comment> comments;

    public FeedStory(
//            String description, String image, String image_id, String location, String thumb, String title, String user_id, Long timestamp, Boolean isPrivate
    ) {
//        this.description = description;
//        this.image = image;
//        this.image_id = image_id;
//        this.location = location;
//        this.thumb = thumb;
//        this.title = title;
//        this.user_id = user_id;
//        this.timestamp = timestamp;
//        this.isPrivate = isPrivate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    public void setReactions(Map<String, String> reactions) {
        this.reactions = reactions;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Map<String,String> getReactions() {
        return reactions;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

//
//    public Boolean getPrivate() {
//        return isPrivate;
//    }
//
//    public void setPrivate(Boolean aPrivate) {
//        isPrivate = aPrivate;
//    }
}
