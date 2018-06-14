package com.picapp.picapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText searchView;
    private RecyclerView peopleList;
    private DatabaseReference dataRef;
    private FirebaseUser firUser;
    private ArrayList<String> nameList;
    private ArrayList<String> picList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Levanto el buscador.
        searchView = (EditText) findViewById(R.id.peopleSearch);
        //Levanto la lista de gente
        peopleList = (RecyclerView) findViewById(R.id.peopleList);

        //obtengo los usuarios de firebase
        dataRef = FirebaseDatabase.getInstance().getReference();
        firUser = FirebaseAuth.getInstance().getCurrentUser();

        peopleList.setHasFixedSize(true);
        peopleList.setLayoutManager(new LinearLayoutManager(this));
        peopleList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        picList = new ArrayList<>();
        nameList = new ArrayList<>();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Toast.makeText(getApplicationContext(), "LA S ES: "+s, Toast.LENGTH_LONG).show();

                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());
                }
            }
        });


    }

    private void setAdapter(final String searchString) {
        dataRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String uid = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    String img = snapshot.child("image").getValue(String.class);
                    Toast.makeText(getApplicationContext(), "Nombre:"+uid, Toast.LENGTH_LONG).show();

                    if(name.contains(searchString)){
                        nameList.add(name);
                        Toast.makeText(getApplicationContext(), "Nombre:"+name, Toast.LENGTH_LONG).show();
                        picList.add(img);
                        count = count + 1;
                    }

                    if(count == 10){
                        break;
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
