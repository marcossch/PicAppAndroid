package com.picapp.picapp.Interfaces;

import com.picapp.picapp.Models.Comment;
import com.picapp.picapp.Models.CommentRequest;
import com.picapp.picapp.Models.Feed;
import com.picapp.picapp.Models.FlashFeed;
import com.picapp.picapp.Models.FlashRequest;
import com.picapp.picapp.Models.FlashResult;
import com.picapp.picapp.Models.FriendsList;
import com.picapp.picapp.Models.FriendshipResponse;
import com.picapp.picapp.Models.FriendshipStatus;
import com.picapp.picapp.Models.Reaction;
import com.picapp.picapp.Models.StoryDeleted;
import com.picapp.picapp.Models.StoryRequest;
import com.picapp.picapp.Models.StoryResult;
import com.picapp.picapp.Models.User;
import com.picapp.picapp.Models.Error;
import com.picapp.picapp.Models.UserAccount;
import com.picapp.picapp.Models.UserLogout;
import com.picapp.picapp.Models.UserPreview;
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

    @GET("users/{userid}/preview")
    Call<UserPreview> getPreview(@Path("userid") String user,
                                 @Header("token") String token, @Header("Content-Type") String content);
    @GET("users/{userid}/friends")
    Call<FriendsList> getUserFriends(@Path("userid") String user,
                                     @Header("token") String token, @Header("Content-Type") String content);

    //--------------Friendship ----------------------

    @GET("users/{userid}/friendship")
    Call<FriendshipStatus> getFriendshipStatus(@Path("userid") String user,
                                         @Header("token") String token, @Header("Content-Type") String content);

    @GET("users/{userid}/friendship")
    Call<FriendshipStatus> getFriendshipStatusCustom(@Path("userid") String user, @Header("token") String token);

    @POST("users/{userid}/friendship")
     Call<FriendshipResponse> postFriendship(@Path("userid") String user,
                                             @Header("token") String token, @Header("Content-Type") String content);

    @DELETE("users/{userid}/friendship")
    Call<FriendshipResponse> deleteFriendship(@Path("userid") String user,
                                            @Header("token") String token, @Header("Content-Type") String content);

    //--------------Stories -------------------------
    @POST("stories")
    Call<StoryResult> postStory(@Body StoryRequest StoryRequest,
                                @Header("token") String token, @Header("Content-Type") String content);

    @DELETE("stories/{storyid}")
    Call<StoryDeleted> deleteStory(@Path("storyid") String story,
                                   @Header("token") String token, @Header("Content-Type") String content);

    @GET("feed")
    Call<Feed> getFeed(@Header("token") String token, @Header("Content-Type") String content);

    //--------------Flashes -------------------------
    @POST("flashes")
    Call<FlashResult> postFlash(@Body FlashRequest FlashRequest,
                                @Header("token") String token, @Header("Content-Type") String content);

    @GET("flashfeed")
    Call<FlashFeed> getFlashFeed(@Header("token") String token, @Header("Content-Type") String content);

    //--------------Reactions -------------------------
    @POST("stories/{storyid}/reactions")
    Call<Reaction> postReaction(@Body Reaction Reaction, @Path("storyid") String story,
                             @Header("token") String token, @Header("Content-Type") String content);

    @POST("stories/{storyid}/comments")
    Call<Comment> postComment(@Body CommentRequest CommentRequest, @Path("storyid") String story,
                              @Header("token") String token, @Header("Content-Type") String content);
}
