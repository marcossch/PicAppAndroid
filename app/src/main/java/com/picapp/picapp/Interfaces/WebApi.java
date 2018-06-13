package com.picapp.picapp.Interfaces;

import com.picapp.picapp.Models.Reaction;
import com.picapp.picapp.Models.StoryDeleted;
import com.picapp.picapp.Models.StoryRequest;
import com.picapp.picapp.Models.StoryResult;
import com.picapp.picapp.Models.User;
import com.picapp.picapp.Models.Error;
import com.picapp.picapp.Models.UserAccount;
import com.picapp.picapp.Models.UserLogout;
import com.picapp.picapp.Models.UserProfile;
import com.picapp.picapp.Models.UserRequest;
import com.picapp.picapp.Models.UserUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WebApi {

    String BASE_URL = "https://picapp-app-server.herokuapp.com/";

    //--------------User Management -------------------------

    @POST("users/signup")
    Call<UserRequest> postUser(@Body UserRequest userRequest);

    @POST("users/login")
    Call<User> loginUser(@Body UserRequest userRequest);

    @POST("users/logout")
    Call<UserLogout> logoutUser(@Body UserLogout UserLogout);

    @PUT("users/{userid}/myaccount")
    Call<Error> updateUser(@Body UserUpdate userRequest, @Path("userid") String user,
                           @Header("token") String token, @Header("Content-Type") String content);

    @GET("users/{userid}/myaccount")
    Call<UserAccount> getUserAccount(@Path("userid") String user,
                                     @Header("token") String token, @Header("Content-Type") String content);

    //--------------Profile -------------------------

    @GET("users/{userid}")
    Call<UserProfile> getUserProfile(@Path("userid") String user,
                                     @Header("token") String token, @Header("Content-Type") String content);

    //--------------Stories -------------------------
    @POST("stories")
    Call<StoryResult> postStory(@Body StoryRequest StoryRequest,
                                @Header("token") String token, @Header("Content-Type") String content);

    @DELETE("stories/{storyid}")
    Call<StoryDeleted> deleteStory(@Path("storyid") String story,
                                   @Header("token") String token, @Header("Content-Type") String content);

    //--------------Reactions -------------------------
    @POST("stories/{storyid}/reactions")
    Call<Reaction> postReaction(@Body Reaction Reaction, @Path("storyid") String story,
                             @Header("token") String token, @Header("Content-Type") String content);
}
