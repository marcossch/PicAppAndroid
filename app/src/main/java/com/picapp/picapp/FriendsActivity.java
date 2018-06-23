package com.picapp.picapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.FriendsList;
import com.picapp.picapp.Models.SearchAdapter;
import com.picapp.picapp.Models.UserAccount;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;
    private RecyclerView peopleList;
    private DatabaseReference dataRef;
    private ArrayList<String> nameList;
    private ArrayList<String> picList;
    private ArrayList<String> idList;
    private SearchAdapter searchAdapter;
    private String id;
    private FirebaseUser firUser;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Levanto la lista de gente
        peopleList = (RecyclerView) findViewById(R.id.peopleList);
        firUser = FirebaseAuth.getInstance().getCurrentUser();

        //obtengo los usuarios de firebase
        dataRef = FirebaseDatabase.getInstance().getReference();

        peopleList.setHasFixedSize(true);
        peopleList.setLayoutManager(new LinearLayoutManager(this));
        peopleList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        picList = new ArrayList<>();
        nameList = new ArrayList<>();
        idList = new ArrayList<>();

        setAdapter();

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
        sendToProfile();
        return false;
    }

    private void setAdapter() {

        final Picapp picapp = Picapp.getInstance();
        final String token = picapp.getToken();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);
        nameList.clear();
        picList.clear();
        idList.clear();

        id = getIntent().getStringExtra("id");
        Call<FriendsList> friends = webApi.getUserFriends(id, token, "Application/json");
        friends.enqueue(new Callback<FriendsList>() {

            @Override
            public void onResponse(Call<FriendsList> call, Response<FriendsList> response) {
                for(UserAccount user : response.body().getUsers()){
                    idList.add(user.getUsername());
                    nameList.add(user.getName());
                    picList.add(user.getProfilePic());
                }
                searchAdapter = new SearchAdapter(FriendsActivity.this, nameList, picList, idList, "profile");
                peopleList.setAdapter(searchAdapter);
            }

            @Override
            public void onFailure(Call<FriendsList> call, Throwable t) {
                Log.d("SHOW FRIENDS", "-----> No se pudo obtener la lista de amigos <-----");
            }
        });
    }

    //--------------Metodos Privados-------------//

    private void sendToProfile() {

        if(id.contains(firUser.getUid())){
            intent = new Intent(FriendsActivity.this, ProfileActivity.class);
            Log.d("FRIENDS", "--------------> VAMOS A PROFILE ACTIVITY <--------------");
        }
        else{
            intent = new Intent(FriendsActivity.this, FriendProfileActivity.class);
            intent.putExtra("id",id);
            Log.d("FRIENDS", "--------------> VAMOS A FRIEND PROFILE ACTIVITY <--------------");
        }
        String from = getIntent().getStringExtra("from");
        intent.putExtra("from", from);
        startActivity(intent);
        finish();
    }

}
