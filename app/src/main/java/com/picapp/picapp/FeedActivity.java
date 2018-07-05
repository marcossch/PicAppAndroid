package com.picapp.picapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.picapp.picapp.AndroidModels.FeedRecyclerAdapter;
import com.picapp.picapp.AndroidModels.FeedStory;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.Comment;
import com.picapp.picapp.Models.Feed;
import com.picapp.picapp.Models.Story;
import com.picapp.picapp.Models.UserLogout;
import com.picapp.picapp.Models.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;

    private BottomNavigationView mMainNav;
    private RecyclerView feed_list_view;
    private List<FeedStory> feed_list;
    private FeedRecyclerAdapter feedRecyclerAdapter;

    private String token;

    private ProgressBar feedProgress;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //que se ve la barra de progreso
        feedProgress = (ProgressBar) findViewById(R.id.feedProgress);
        //que se ve la barra de progreso
        feedProgress.setVisibility(View.VISIBLE);

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        token = picapp.getToken();

        if(token == null) {
            Intent mainIntent = new Intent(FeedActivity.this, MainActivity.class);
            sendTo(mainIntent);
        }

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");

        //barra de navegacion
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(navListener);
        mMainNav.setSelectedItemId(R.id.nav_feed);

        //levanto la lista de visualizacion de stories
        feed_list_view = (RecyclerView) findViewById(R.id.feed_list_view);

        //cargo la lista de stories
        feed_list = new ArrayList<>();
        feedRecyclerAdapter = new FeedRecyclerAdapter(feed_list);
        feed_list_view.setLayoutManager(new LinearLayoutManager(this));
        feed_list_view.setAdapter(feedRecyclerAdapter);
        feedRecyclerAdapter.setToken(token);

        //Agarro los atributos desde firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        //creo retrofit que es la libreria para manejar Apis
        retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);

        Call<Feed> call = webApi.getFeed(token, "Application/json");
        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {

                if (response.code() != 200) {
                    //Toast.makeText(FeedActivity.this, "Error del Server", Toast.LENGTH_LONG).show();
                } else {

                    Feed feed = response.body();

                    List<Story> stories = feed.getStories();
                    for (Story story : stories) {

                        FeedStory feedStory = new FeedStory();
                        feedStory.setDescription(story.getDescription());
                        feedStory.setImage(story.getMedia());
                        feedStory.setTimestamp(story.getTimestamp());
                        feedStory.setTitle(story.getTitle());
                        feedStory.setName(story.getName());
                        feedStory.setProfPic(story.getProfilePic());
                        feedStory.setImage_id(story.getStory_id());
                        feedStory.setUser_id(story.getUsername());

                        //Diferencio la ubicacon para mostrarla bien y para el mapa
                        String ubicacion = story.getLocation();
                        String[] parts = ubicacion.split(",");
                        String loc = "";
                        if (parts.length >= 2) {
                            loc += parts[1];
                        }
                        if (parts.length >= 3) {
                            loc += ", " + parts[2];
                        }
                        feedStory.setLocation(ubicacion);

                        Map<String, String> reactions = story.getReactions();
                        ArrayList<Comment> coments = story.getComments();

                        if (reactions != null) {
                            feedStory.setReactions(story.getReactions());
                        }

                        if (coments != null) {
                            feedStory.setComments(story.getComments());
                        }

                        feed_list.add(feedStory);
                        feedRecyclerAdapter.notifyDataSetChanged();

                    }

                }

                //escondo la barra de progreso
                feedProgress.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Toast.makeText(FeedActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                //escondo la barra de progreso
                feedProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //se cargan las acciones del menu de opciones
        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logout();
                return true;

            case R.id.action_setting_btn:
                Intent accSettings = new Intent(FeedActivity.this, AccountSettingsActivity.class);
                sendTo(accSettings);
                return true;

            case R.id.action_search_btn:
                Intent profSearch = new Intent(FeedActivity.this, SearchActivity.class);
                sendTo(profSearch);
                return true;
        }

        return false;
    }


    //cambio de activities principales
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){

                        case R.id.nav_feed:
                            return true;


                        case R.id.nav_flashes:
                            Intent flashesIntent = new Intent(FeedActivity.this, FlashesActivity.class);
                            sendTo(flashesIntent);
                            return true;


                        case R.id.nav_profile:
                            Intent profileIntent = new Intent(FeedActivity.this, ProfileActivity.class);
                            sendTo(profileIntent);
                            return true;

                        case R.id.nav_chat:
                            Intent chatIntent = new Intent(FeedActivity.this, ChatSelectionActivity.class);
                            sendTo(chatIntent);
                            return true;

                        default:
                            return false;

                    }
                }
            };

    //--------------Metodos Privados-------------//

    private void logout() {

        serverLogut(mAuth.getCurrentUser());
        mAuth.signOut();
        Intent loginIntent = new Intent(FeedActivity.this, LoginActivity.class);
        sendTo(loginIntent);

    }

    private void serverLogut(FirebaseUser user) {

        //creo retrofit que es la libreria para manejar Apis
        retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebApi webApi = retrofit.create(WebApi.class);

        //Creo la request para pasarle en el body
        UserLogout userRequest = new UserLogout();
        userRequest.setUsername(user.getUid());

        Call<UserLogout> call = webApi.logoutUser(userRequest);
        call.enqueue(new Callback<UserLogout>() {
            @Override
            public void onResponse(Call<UserLogout> call, Response<UserLogout> response) {

            }

            @Override
            public void onFailure(Call<UserLogout> call, Throwable t) {
                //se cierra sesion en firebase
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void sendTo(Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }

}
