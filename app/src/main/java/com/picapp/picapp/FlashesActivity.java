package com.picapp.picapp;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AndroidModels.FeedRecyclerAdapter;
import com.picapp.picapp.AndroidModels.FeedStory;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.Comment;
import com.picapp.picapp.Models.Feed;
import com.picapp.picapp.Models.Flash;
import com.picapp.picapp.Models.FlashFeed;
import com.picapp.picapp.Models.Story;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlashesActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;

    private BottomNavigationView mMainNav;
    private RecyclerView feed_list_view;
    private List<FeedStory> feed_list;
    private FeedRecyclerAdapter feedRecyclerAdapter;

    private ArrayList<String> flashesV = new ArrayList<String>();
    private ArrayList<String> flashesName = new ArrayList<String>();
    private ArrayList<String> flashesLocation = new ArrayList<String>();
    private ArrayList<String> flashesDate = new ArrayList<String>();

    private String token;

    private ProgressBar flashesProgress;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashes);

        //que se ve la barra de progreso
        flashesProgress = (ProgressBar) findViewById(R.id.flashesFeedProgress);
        //que se ve la barra de progreso
        flashesProgress.setVisibility(View.VISIBLE);

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        token = picapp.getToken();

        if(token == null) {
            Intent mainIntent = new Intent(FlashesActivity.this, MainActivity.class);
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
        mMainNav.setSelectedItemId(R.id.nav_flashes);

        //levanto la lista de visualizacion de stories
        feed_list_view = (RecyclerView) findViewById(R.id.flashesFeed_list_view);

        //cargo la lista de stories
        feed_list = new ArrayList<>();
        feedRecyclerAdapter = new FeedRecyclerAdapter(feed_list);
        feed_list_view.setLayoutManager(new LinearLayoutManager(this));
        feed_list_view.setAdapter(feedRecyclerAdapter);
        feedRecyclerAdapter.setToken(token);
        feedRecyclerAdapter.setIsFlashes(true);

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

        //descomentar cuando este
        Call<FlashFeed> call = webApi.getFlashFeed(token, "Application/json");

        //sacar esta linea cuando este terminado el server
        //Call<Feed> call = webApi.getFeed(token, "Application/json");

        call.enqueue(new Callback<FlashFeed>() {
            @Override
            public void onResponse(Call<FlashFeed> call, Response<FlashFeed> response) {

                if (response.code() != 200) {
                    //Toast.makeText(FlashesActivity.this, "Error del Server", Toast.LENGTH_LONG).show();
                } else {

                    FlashFeed feed = response.body();

                    List<Flash> stories = feed.getFlashes();
                    for (Flash story : stories) {

                        FeedStory feedStory = new FeedStory();
                        feedStory.setDescription(story.getDescription());
                        feedStory.setImage(story.getMedia());
                        feedStory.setLocation(story.getLocation());
                        feedStory.setTimestamp(story.getTimestamp());
                        feedStory.setTitle(story.getTitle());
                        feedStory.setName(story.getName());
                        feedStory.setProfPic(story.getProfilePic());
                        feedStory.setImage_id(story.getFlashId());
                        feedStory.setUser_id(story.getUsername());

                        if (story.getMedia().contains("jpg")) {

                            //levanto la fecha
                            Long milliseconds = story.getTimestamp();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String dateString = formatter.format(new Date(milliseconds));

                            flashesV.add(story.getMedia());
                            flashesDate.add(dateString);
                            flashesName.add(story.getName());
                            flashesLocation.add(story.getLocation());
                        }

                        feed_list.add(feedStory);
                        feedRecyclerAdapter.notifyDataSetChanged();

                    }

                }

                //escondo la barra de progreso
                flashesProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<FlashFeed> call, Throwable t) {
                Toast.makeText(FlashesActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                //escondo la barra de progreso
                flashesProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.flashes_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //se cargan las acciones del menu de opciones
        switch (item.getItemId()) {
            case R.id.flashView:
                //si terminaron de cargar las fotos
                if(flashesProgress.getVisibility() == View.INVISIBLE){
                    Intent flashView = new Intent(FlashesActivity.this, FlashFlowActivity.class);
                    flashView.putStringArrayListExtra("flashes", flashesV);
                    flashView.putStringArrayListExtra("flashesNames", flashesName);
                    flashView.putStringArrayListExtra("flashesDates", flashesDate);
                    flashView.putStringArrayListExtra("flashesLocations", flashesLocation);
                    sendTo(flashView);
                }
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

                        case R.id.nav_feed :
                            Intent feedIntent = new Intent(FlashesActivity.this, FeedActivity.class);
                            sendTo(feedIntent);
                            return true;


                        case R.id.nav_flashes:
                            return true;


                        case R.id.nav_chat:
                            Intent chatIntent = new Intent(FlashesActivity.this, ChatSelectionActivity.class);
                            sendTo(chatIntent);
                            return true;

                        case R.id.nav_profile:
                            Intent profileIntent = new Intent(FlashesActivity.this, ProfileActivity.class);
                            sendTo(profileIntent);
                            return true;

                        default:
                            return false;

                    }
                }
            };

    //--------------Metodos Privados-------------//

    private void sendTo(Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }
}
