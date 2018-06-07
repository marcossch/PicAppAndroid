package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;
    @SerializedName("commenting_user_id")
    @Expose
    private String commentingUserId;
    @SerializedName("comment")
    @Expose
    private String comment;

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentingUserId() {
        return commentingUserId;
    }

    public void setCommentingUserId(String commentingUserId) {
        this.commentingUserId = commentingUserId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}