package com.picapp.picapp.Interfaces;

import com.picapp.picapp.Models.User;
import com.picapp.picapp.Models.UserLogout;
import com.picapp.picapp.Models.UserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WebApi {

    String BASE_URL = "https://picapp-app-server.herokuapp.com/";

    @POST("users/signup")
    Call<UserRequest> postUser(@Body UserRequest userRequest);

    @POST("users/login")
    Call<User> loginUser(@Body UserRequest userRequest);

    @POST("users/logout")
    Call<UserLogout> logoutUser(@Body UserLogout UserLogout);

}
