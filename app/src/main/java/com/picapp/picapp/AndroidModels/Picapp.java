package com.picapp.picapp.AndroidModels;


public class Picapp{
    private static Picapp instance;

    // Global variable
    public String token = null;

    // Restrict the constructor from being instantiated
    private Picapp(){}

    public void setToken(String newToken) {
        this.token = newToken;
    }
    public String getToken() {
        return token;
    }

    public static synchronized Picapp getInstance(){
        if(instance==null){
            instance=new Picapp();
        }
        return instance;
    }
}