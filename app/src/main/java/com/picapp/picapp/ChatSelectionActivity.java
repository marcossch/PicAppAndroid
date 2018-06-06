package com.picapp.picapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.picapp.picapp.Models.SessionData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatSelectionActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private String userId;
    private FirebaseAuth mAuth;

    ListView usersList;
    TextView noUsersText;
    ArrayList<String> list = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_selection);

        //instacia de firebase y firestore
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);

        progressDialog = new ProgressDialog(ChatSelectionActivity.this);
        progressDialog.setMessage("Cargando amigos...");
        progressDialog.show();

        /* The following should be a request to Application Server for active user friends
        and doOnSuccess should process the list of friends and add it to the list. Now we mock it*/
        list.add("bVorEQY5cWRpiMaclTDKmEfzUlg2");
        list.add("y39LYqRvTsf5nEut6gpVyShCIN62");
        totalUsers = list.size();
        doOnSuccess("Nothing");

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionData.onChat = list.get(position);
                startActivity(new Intent(ChatSelectionActivity.this, ChatActivity.class));
            }
        });

        //barra de navegacion
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(navListener);
        mMainNav.setSelectedItemId(R.id.nav_chat);
    }

    public void doOnSuccess(String s){
        /*try {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();
            String key = "";
            while(i.hasNext()){
                key = i.next().toString();
                if(!key.equals(mAuth.getCurrentUser().getDisplayName())) {
                    list.add(key);
                }
                totalUsers++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
        }

        progressDialog.dismiss();
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
        finish();

    }
}
