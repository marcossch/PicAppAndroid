package com.picapp.picapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cambiar esto a activity_main
        setContentView(R.layout.activity_main);

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");

    }

    @Override
    protected void onStart() {
        super.onStart();

        //chequea si el usuario ya esta logueado
        // si no lo esta lo manda a la pagina de login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is signed in
        } else {
            // No user is signed in
            sendToLogin();
        }
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
        }

        return false;
    }


    //--------------Metodos Privados-------------//

    private void logout() {

        mAuth.signOut();
        sendToLogin();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

}
