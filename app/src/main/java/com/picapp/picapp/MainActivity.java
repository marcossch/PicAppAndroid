package com.picapp.picapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AndroidModels.Picapp;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cambiar esto a activity_main
        setContentView(R.layout.activity_main);

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();

        //chequea si el usuario ya esta logueado
        // si no lo esta lo manda a la pagina de login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            //si no tiene el token cargado, lo hace
            final Picapp picapp = Picapp.getInstance();

            if(picapp.getToken() == null){
                //Obtengo el token del usuario.
                firebaseFirestore.collection("UserTokens").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //si existe este documento
                            if (task.getResult().exists()) {
                                //levanto el token
                                Object aux = task.getResult().get("token");
                                Object aux2 = task.getResult().get("expiresAt");
                                token = aux.toString();

                                picapp.setToken(token);
                                picapp.setExpiresAt(aux2.toString());

                                // User is signed in entonces va al feed
                                Intent feedIntent = new Intent(MainActivity.this, FeedActivity.class);
                                sendTo(feedIntent);

                            } else {
                                Toast.makeText(MainActivity.this, "El usuario no posee un token asociado", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                sendTo(loginIntent);
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "FIRESTORE Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            sendTo(loginIntent);
                        }
                    }
                });
            }
        } else {
            // No user is signed in
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            sendTo(loginIntent);
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

    //cambio de activities principales
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){

                        case R.id.nav_feed :

                            return true;


                        case R.id.nav_flashes:

                            return true;


                        case R.id.nav_profile:

                            return true;

                        default:
                            return false;

                    }
                }
            };

    //--------------Metodos Privados-------------//

    private void logout() {

        mAuth.signOut();
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        sendTo(loginIntent);

    }

    private void sendTo(Intent intent) {

        startActivity(intent);
        finish();

    }

}
