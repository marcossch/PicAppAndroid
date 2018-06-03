package com.picapp.picapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status_code")
    @Expose
    private int status_code;

    public String getMessage() { return message; }

    public int getCode() {
        return status_code;
    }

}