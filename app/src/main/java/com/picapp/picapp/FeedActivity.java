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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.picapp.picapp.AndroidModels.FeedRecyclerAdapter;
import com.picapp.picapp.AndroidModels.FeedStory;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.UserLogout;

import java.util.ArrayList;
import java.util.List;

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

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

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

        //Agarro los atributos desde firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //levanta toda la data del feed de firebase en realtime
        firebaseFirestore.collection("Stories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        FeedStory feedStory = doc.getDocument().toObject(FeedStory.class);
                        feed_list.add(feedStory);
                        feedRecyclerAdapter.notifyDataSetChanged();

                    }

                }

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
