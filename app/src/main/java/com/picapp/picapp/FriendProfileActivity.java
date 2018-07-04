package com.picapp.picapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.picapp.picapp.AndroidModels.FeedRecyclerAdapter;
import com.picapp.picapp.AndroidModels.FeedStory;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.Comment;
import com.picapp.picapp.Models.FriendshipResponse;
import com.picapp.picapp.Models.Story;
import com.picapp.picapp.Models.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendProfileActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private String name;
    private String picURL;
    private ImageView fotoP;
    private TextView amigos;
    private TextView publicaciones;
    private android.support.v7.widget.Toolbar mainToolbar;
    private String username;
    private String latlng="";
    private String user_id;

    private RecyclerView profile_list_view;
    private List<FeedStory> profile_list;
    private FeedRecyclerAdapter profileRecyclerAdapter;
    private ImageButton mapBtn;
    private Retrofit retrofit;
    private FloatingActionButton addPostButton;
    private android.support.design.widget.FloatingActionButton deleteFriendsBtn;
    Intent sendTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        final String token = picapp.getToken();

        if(token == null) {
            Intent mainIntent = new Intent(FriendProfileActivity.this, MainActivity.class);
            sendTo(mainIntent);
        }

        //levantamos la foto default
        fotoP = findViewById(R.id.contenedorFotoPerfil);
        fotoP.setImageDrawable(getDrawable(R.drawable.cameranext));

        //levanto la cantidad de publicaciones y amigos
        amigos = findViewById(R.id.frdNumber);
        publicaciones = findViewById(R.id.pubNumber);

        //Agarro los atributos desde firebase
        user_id = getIntent().getStringExtra("id");

        mapBtn = findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendToMap();
            }
        });

        //Click en el boton de amigos te lleva a ver tus amigos
        Button friends = (Button) findViewById(R.id.frdNumber);

        //Boton para agregar un post
        //FloatingActionButton addPostButton = (FloatingActionButton) findViewById(R.id.agregarFoto);

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent friendsIntent = new Intent(FriendProfileActivity.this, FriendsActivity.class);
                friendsIntent.putExtra("id",user_id);
                sendTo(friendsIntent);

            }
        });


        //-------------levanto las publicaciones del usuario--------------

        //levanto la lista de visualizacion de stories
        profile_list_view = (RecyclerView) findViewById(R.id.profile_list_view);

        //cargo la lista de stories
        profile_list = new ArrayList<>();
        profileRecyclerAdapter = new FeedRecyclerAdapter(profile_list);
        profileRecyclerAdapter.setToken(token);
        profile_list_view.setLayoutManager(new LinearLayoutManager(FriendProfileActivity.this));
        profile_list_view.setAdapter(profileRecyclerAdapter);

        //creo retrofit que es la libreria para manejar Apis
        retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);

        Call<UserProfile> call = webApi.getUserProfile(user_id, token, "Application/json");
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                UserProfile userP = response.body();
                //levanto la info del usuario
                name = userP.getName();
                changeProfileName();
                picURL = userP.getProfilePic();
                changeProfilePic();
                amigos.setText(userP.getNumberOfFriends().toString());
                publicaciones.setText(userP.getNumberOfStories().toString());
                username = userP.getUsername();

                List<Story> stories = userP.getStories();
                for (Story story : stories){

                    FeedStory feedStory = new FeedStory();
                    feedStory.setDescription(story.getDescription());
                    feedStory.setImage(story.getMedia());
                    feedStory.setTimestamp(story.getTimestamp());
                    feedStory.setTitle(story.getTitle());
                    feedStory.setUser_id(story.getUsername());
                    feedStory.setProfPic(picURL);
                    feedStory.setImage_id(story.getStory_id());
                    feedStory.setName(name);

                    //Diferencio la ubicacon para mostrarla bien y para el mapa
                    String ubicacion = story.getLocation();
                    String[] parts = ubicacion.split(",");
                    String loc = "";
                    if(parts.length >= 2){
                        loc += parts[1];
                    }
                    if(parts.length >= 3){
                        loc += ", "+parts[2];
                    }
                    //Toast.makeText(FriendProfileActivity.this, "ubicacion seteada: "+loc, Toast.LENGTH_LONG).show();
                    feedStory.setLocation(ubicacion);
                    if(parts.length>2) {
                        String lat = parts[parts.length - 2].substring(10, parts[parts.length - 2].length());
                        String lng = parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 1);
                        latlng = latlng + lat + "," + lng + ";";
                    }
                    //Toast.makeText(FriendProfileActivity.this, "latlng: "+ latlng, Toast.LENGTH_LONG).show();
                    Map<String, String> reactions = story.getReactions();
                    ArrayList<Comment> coments = story.getComments();

                    if (reactions != null){
                        feedStory.setReactions(story.getReactions());
                    }

                    if (coments != null){
                        feedStory.setComments(story.getComments());
                    }

                    profile_list.add(feedStory);
                    profileRecyclerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(FriendProfileActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //Preparo las cosas para el delete
        deleteFriendsBtn = findViewById(R.id.borrarAmigos);
        final String id = getIntent().getStringExtra("id");

        deleteFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<FriendshipResponse> friendshipResponse = webApi.deleteFriendship(id, token, "Application/json");
                friendshipResponse.enqueue(new Callback<FriendshipResponse>() {
                    @Override
                    public void onResponse(Call<FriendshipResponse> call, Response<FriendshipResponse> response) {
                        sendToFeed();
                    }

                    @Override
                    public void onFailure(Call<FriendshipResponse> call, Throwable t) {
                        Log.d("DELETE FRIEND", "-----> No se pudo cancelar la amistad <-----");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.friends_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //se cargan las acciones del menu de opciones
        switch (item.getItemId()) {

            case R.id.action_search_btn:
                //usar el buscador
                return true;

        }
        sendToFeed();
        return false;
    }


    private void sendToFeed() {
        String from = getIntent().getStringExtra("from");
        if(from.contains("profile")){
            Log.d("FRIEND PROFILE", "-----> VOLVEMOS A EL PERFIL <-----");
            sendTo =  new Intent(FriendProfileActivity.this, ProfileActivity.class);
        } else{
            Log.d("FRIEND PROFILE", "-----> VOLVEMOS AL FEED <-----");
            sendTo =  new Intent(FriendProfileActivity.this, FeedActivity.class);
        }
        startActivity(sendTo);
        finish();
    }

    //--------------Metodos Privados-------------//

    private void sendTo(Intent intent) {
        startActivity(intent);
    }

    private void sendToMap() {
        Intent mapIntent = new Intent(FriendProfileActivity.this, MapsActivity.class);
        mapIntent.putExtra("LatLong", latlng);
        mapIntent.putExtra("id",user_id);
        startActivity(mapIntent);
        finish();
    }

    private void changeProfileName() {
        TextView txtCambiado = (TextView)findViewById(R.id.userName);
        txtCambiado.setText(name);
    }

    private void changeProfilePic(){
        Glide.with(FriendProfileActivity.this).load(picURL).into(fotoP);
    }

}
