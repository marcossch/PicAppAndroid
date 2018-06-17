package com.picapp.picapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class OtherProfileActivity extends AppCompatActivity {

    private String name;
    private String id;
    private String pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        //Setteo los parametros pasados por parametro
        setParams();
        TextView nameView = findViewById(R.id.userName);
        nameView.setText(this.name);
        ImageView imgView = findViewById(R.id.contenedorFotoPerfil);
        Glide.with(this).load(this.pic).into(imgView);

    }

    public void setParams(){
        Intent myIntent = getIntent(); // gets the previously created intent
        String userName = myIntent.getStringExtra("name");
        this.name = userName;
        String profilePic = myIntent.getStringExtra("pic");
        this.pic = profilePic;
        String id = myIntent.getStringExtra("id");
        this.id = id;
    }
}
