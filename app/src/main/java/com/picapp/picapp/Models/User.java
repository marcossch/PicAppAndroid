package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("token")
    @Expose
    private Token token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

}

//package com.picapp.picapp;
//
//public class User {
//
//
//    private String username;
//    private Token token;
//
//    public User(String username, Token token) {
//        this.username = username;
//        this.token = token;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public Token getToken() {
//        return token;
//    }
//
//    private class Token{
//        private String token;
//        private String expiresAt;
//
//    }
//}
