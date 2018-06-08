package com.picapp.picapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import com.picapp.picapp.Models.SelectableUser;
import com.picapp.picapp.Models.SessionData;

import java.util.ArrayList;

public class ChatSelectionActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private String userId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private android.support.v7.widget.Toolbar mainToolbar;


    ListView usersList;
    TextView noUsersText;
    ArrayList<SelectableUser> selectableList = new ArrayList<>();
    ArrayList<String> toShowList = new ArrayList<>();
    Integer counter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_selection);

        //instacia de firebase y firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        counter = 2;
        this.addUserToList("bVorEQY5cWRpiMaclTDKmEfzUlg2");
        this.addUserToList("y39LYqRvTsf5nEut6gpVyShCIN62");
        this.addUserToList("8UvBIcA4uQaydZkTXCXur5ZWdIj1");

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionData.onChat = selectableList.get(position);
                startActivity(new Intent(ChatSelectionActivity.this, ChatActivity.class));
            }
        });

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

    public void doOnSuccess(String s){
        /*try {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();
            String key = "";
            while(i.hasNext()){
                key = i.next().toString();
                if(!key.equals(mAuth.getCurrentUser().getDisplayName())) {
                    selectableList.add(key);
                }
                totalUsers++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        if(toShowList.size() == 0){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toShowList));
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
    }

    private void addUserToList(String id){
        if (id.equals(userId)) return;
        final String userId = id;
        DocumentReference docRef = firebaseFirestore.collection("Users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name");
                        selectableList.add(new SelectableUser(userId, userName));
                        toShowList.add(userName);
                        counter = counter - 1;
                        if (counter == 0){
                            doOnSuccess("Nothing");
                        }
                    }
                }
            }
        });
    }
}
