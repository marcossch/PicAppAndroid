package com.picapp.picapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AccountSettingsActivity extends AppCompatActivity {

    private ImageView profileImage;
    private android.support.v7.widget.Toolbar mainToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");

        //levanto las imagenes y text
        profileImage = findViewById(R.id.contenedorPresentacion);

        //tendria que cargar la imagen de perfil del server actual

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //si el usuario tiene marshmellow o superior
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    //si no tiene este permiso lo tengo que pedir
                    if(ContextCompat.checkSelfPermission(AccountSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(AccountSettingsActivity.this, "Permission Denied.", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AccountSettingsActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else{

                        Toast.makeText(AccountSettingsActivity.this, "You already have permission.", Toast.LENGTH_LONG).show();

                    }

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //se cargan las acciones del menu de opciones
        switch (item.getItemId()) {

            case R.id.save_changes:
                //actualizar el servidor
                //actualizar firebase

            case R.id.cancel:

        }

        sendToFeed();
        return false;
    }


    //--------------Metodos Privados-------------//

    private void sendToFeed() {
        Intent feedIntent = new Intent(AccountSettingsActivity.this, FeedActivity.class);
        startActivity(feedIntent);
        finish();
    }
}
