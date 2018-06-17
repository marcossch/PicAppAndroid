package com.picapp.picapp.AndroidModels;

import android.net.Uri;

public class CommentStory{
        public String username;
        public String comment;
        public String timestamp;
        public Uri image;

        public CommentStory() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public Uri getImage() {
            return image;
        }

        public void setImage(Uri image) {
            this.image = image;
        }
}