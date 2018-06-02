package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Code")
    @Expose
    private int code;

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}