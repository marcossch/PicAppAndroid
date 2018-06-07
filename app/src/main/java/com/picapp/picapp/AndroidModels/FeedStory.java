package com.picapp.picapp.AndroidModels;

public class FeedStory {

    public String description, image, image_id, location, thumb, title, user_id;
    public Long timestamp;
    public Boolean isPrivate;

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
//
//    public Boolean getPrivate() {
//        return isPrivate;
//    }
//
//    public void setPrivate(Boolean aPrivate) {
//        isPrivate = aPrivate;
//    }
}
