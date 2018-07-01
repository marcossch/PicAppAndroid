package com.picapp.picapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.FriendsList;
import com.picapp.picapp.Models.SearchAdapter;
import com.picapp.picapp.Models.SelectableUser;
import com.picapp.picapp.Models.SessionData;
import com.picapp.picapp.Models.UserAccount;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatSelectionActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private String userId;
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mainToolbar;


    ListView usersList;
    TextView noUsersText;
    ArrayList<SelectableUser> selectableList = new ArrayList<>();
    ArrayList<String> toShowList = new ArrayList<>();
    ProgressDialog progressDialog;
    ArrayList<String> nameList;
    ArrayList<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_selection);

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);

        progressDialog = new ProgressDialog(ChatSelectionActivity.this);
        progressDialog.setMessage("Cargando amigos...");
        progressDialog.show();

        /* The following should be a request to Application Server for active user friends
        and doOnSuccess should process the selectableList of friends and add it to the selectableList. Now we mock it*/
        nameList = new ArrayList<>();
        idList = new ArrayList<>();

        setAdapter();

        //barra de navegacion
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(navListener);
        mMainNav.setSelectedItemId(R.id.nav_chat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.chat_menu, menu);

        return true;
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
        idList.clear();

        Call<FriendsList> friends = webApi.getUserFriends(userId, token, "Application/json");
        friends.enqueue(new Callback<FriendsList>() {

            @Override
            public void onResponse(Call<FriendsList> call, Response<FriendsList> response) {
                for(UserAccount user : response.body().getUsers()){
                    selectableList.add(new SelectableUser(user.getUsername(), user.getName()));
                    toShowList.add(user.getName());
                }
                progressDialog.dismiss();
                doOnSuccess();
            }

            @Override
            public void onFailure(Call<FriendsList> call, Throwable t) {
                Log.d("SHOW FRIENDS", "-----> No se pudo obtener la lista de amigos <-----");
            }
        });
    }

    public void doOnSuccess(){
        if(toShowList.size() == 0){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toShowList));
            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionData.onChat = selectableList.get(position);
                startActivity(new Intent(ChatSelectionActivity.this, ChatActivity.class));
            }
        });
        }

    }

    //cambio de activities principales
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){

                        case R.id.nav_feed :
                            Intent feedIntent = new Intent(ChatSelectionActivity.this, FeedActivity.class);
                            sendTo(feedIntent);
                            return true;


                        case R.id.nav_flashes:
                            Intent flashesIntent = new Intent(ChatSelectionActivity.this, FlashesActivity.class);
                            sendTo(flashesIntent);
                            return true;

                        case R.id.nav_chat:
                            return true;


                        case R.id.nav_profile:
                            Intent profileIntent = new Intent(ChatSelectionActivity.this, ProfileActivity.class);
                            sendTo(profileIntent);
                            return true;

                        default:
                            return false;

                    }
                }
            };


    private void sendTo(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}
