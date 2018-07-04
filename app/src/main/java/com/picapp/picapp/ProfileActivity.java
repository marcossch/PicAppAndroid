package com.picapp.picapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AndroidModels.FeedRecyclerAdapter;
import com.picapp.picapp.AndroidModels.FeedStory;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.Comment;
import com.picapp.picapp.Models.Story;
import com.picapp.picapp.Models.UserAccount;
import com.picapp.picapp.Models.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FloatingActionButton fab;
    private String name;
    private String picURL;
    private ImageView fotoP;
    private TextView amigos;
    private TextView publicaciones;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private android.support.v7.widget.Toolbar mainToolbar;
    private String username;

    private RecyclerView profile_list_view;
    private List<FeedStory> profile_list;
    private FeedRecyclerAdapter profileRecyclerAdapter;
    private ImageButton mapBtn;

    private Retrofit retrofit;

    private ProgressBar profileProgress;
    private String latlng="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        String token = picapp.getToken();

        if(token == null) {
            Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
            sendTo(mainIntent);
        }

        //que se ve la barra de progreso
        profileProgress = (ProgressBar) findViewById(R.id.profileProgress);
        //que se ve la barra de progreso
        profileProgress.setVisibility(View.VISIBLE);

        //Levantamos la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");

        //levantamos la foto default
        fotoP = findViewById(R.id.contenedorFotoPerfil);
        fotoP.setImageDrawable(getDrawable(R.drawable.cameranext));

        //levanto la cantidad de publicaciones y amigos
        amigos = findViewById(R.id.friendsNumber);
        publicaciones = findViewById(R.id.pubNumber);

        //Agarro los atributos desde firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (user != null) {
            //Cargo el nombre que esta guardado en firebase
            name = user.getDisplayName();
            changeProfileName();
        }

        mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendToMap();
            }
        });

        //Click en el boton de amigos te lleva a ver tus amigos
        Button friends = (Button) findViewById(R.id.friendsNumber);

        //Boton para agregar un post
        FloatingActionButton addPostButton = (FloatingActionButton) findViewById(R.id.agregarFoto);

        //barra de navegacion
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(navListener);
        mMainNav.setSelectedItemId(R.id.nav_profile);

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent friendsIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
                friendsIntent.putExtra("id",user.getUid());
                sendTo(friendsIntent);

            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newPost = new Intent(ProfileActivity.this, NewPostActivity.class);
                sendTo(newPost);

            }
        });

        //-------------levanto las publicaciones del usuario--------------

        //levanto la lista de visualizacion de stories
        profile_list_view = (RecyclerView) findViewById(R.id.profile_list_view);


        //cargo la lista de stories
        profile_list = new ArrayList<>();
        profileRecyclerAdapter = new FeedRecyclerAdapter(profile_list);
        profileRecyclerAdapter.setToken(token);
        profileRecyclerAdapter.isProfile();
        profile_list_view.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        profile_list_view.setAdapter(profileRecyclerAdapter);

        //creo retrofit que es la libreria para manejar Apis
        retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);

        //levanto el nombre del usuario
        Call<UserAccount> callUsername = webApi.getUserAccount(user_id, token, "Application/json");
        callUsername.enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                username = response.body().getName();
            }

            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {
                Log.d("UPDATE USER: ", "-----> No se pudo levantar el nombre de usuario <-----");
            }
        });

        Call<UserProfile> call = webApi.getUserProfile(user_id, token, "Application/json");
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                if (response.code() != 200) {
                    Toast.makeText(ProfileActivity.this, "Error del Server", Toast.LENGTH_LONG).show();
                } else {

                    UserProfile userP = response.body();

                    //levanto la info del usuario
                    name = userP.getName();
                    changeProfileName();
                    picURL = userP.getProfilePic();
                    changeProfilePic();
                    amigos.setText(userP.getNumberOfFriends().toString());
                    publicaciones.setText(userP.getNumberOfStories().toString());

                    List<Story> stories = userP.getStories();
                    for (Story story : stories) {

                        FeedStory feedStory = new FeedStory();
                        feedStory.setDescription(story.getDescription());
                        feedStory.setImage(story.getMedia());
                        feedStory.setTimestamp(story.getTimestamp());
                        feedStory.setTitle(story.getTitle());
                        feedStory.setUser_id(story.getUsername());
                        feedStory.setProfPic(picURL);
                        feedStory.setImage_id(story.getStory_id());
                        feedStory.setName(username);

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
                        if (parts.length > 2) {
                            String lat = parts[parts.length - 2].substring(10, parts[parts.length - 2].length());
                            String lng = parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 1);
                            latlng = latlng + lat + "," + lng + ";";
                        }

                        Map<String, String> reactions = story.getReactions();
                        ArrayList<Comment> coments = story.getComments();

                        if (reactions != null) {
                            feedStory.setReactions(story.getReactions());
                        }

                        if (coments != null) {
                            feedStory.setComments(story.getComments());
                        }

                        profile_list.add(feedStory);
                        profileRecyclerAdapter.notifyDataSetChanged();

                    }

                }

                profileProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                profileProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    //cambio de activities principales
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){

                        case R.id.nav_feed :
                            Intent feedIntent = new Intent(ProfileActivity.this, FeedActivity.class);
                            sendTo(feedIntent);
                            return true;


                        case R.id.nav_flashes:
                            Intent flashesIntent = new Intent(ProfileActivity.this, FlashesActivity.class);
                            sendTo(flashesIntent);
                            return true;


                        case R.id.nav_profile:
                            return true;

                        case R.id.nav_chat:
                            Intent chatIntent = new Intent(ProfileActivity.this, ChatSelectionActivity.class);
                            sendTo(chatIntent);
                            return true;

                        default:
                            return false;

                    }
                }
            };


    //--------------Metodos Privados-------------//

    private void sendTo(Intent intent) {
        startActivity(intent);
    }

    private void sendToMap() {
        Intent mapIntent = new Intent(ProfileActivity.this, MapsActivity.class);
        mapIntent.putExtra("LatLong", latlng);
        mapIntent.putExtra("id", user.getUid());
        startActivity(mapIntent);
        finish();
    }

    private void changeProfileName() {
        TextView txtCambiado = (TextView)findViewById(R.id.userName);
        txtCambiado.setText(name);
    }

    private void changeProfilePic(){
        Glide.with(ProfileActivity.this).load(picURL).into(fotoP);
    }

}
