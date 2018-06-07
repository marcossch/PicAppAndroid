package com.picapp.picapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;


public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FloatingActionButton fab;
    private String name;
    private Uri picURL;
    private ImageView fotoP;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private android.support.v7.widget.Toolbar mainToolbar;

    private FloatingActionButton addPostButton;
    private ProgressBar profileProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //que se ve la barra de progreso
        profileProgress = (ProgressBar) findViewById(R.id.profileProgress);
        //profileProgress.setVisibility(View.VISIBLE);

        //Levantamos la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");

        //Agarro los atributos desde firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (user != null) {
            name = user.getDisplayName();
            picURL = user.getPhotoUrl();
        }

        //Cargo el nombre que esta guardado en firebase
        changeProfileName();

        //Cargo la foto de perfil del usuario
        fotoP = findViewById(R.id.contenedorFotoPerfil);
        fotoP.setImageDrawable(getDrawable(R.drawable.cameranext));
        changeProfilePic();

        //Click en el boton de amigos te lleva a ver tus amigos
        Button friends = (Button) findViewById(R.id.friendsNumber);

        //Boton para agregar un post
        FloatingActionButton addPostButton = (FloatingActionButton) findViewById(R.id.agregarFoto);

        //barra de navegacion
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(navListener);
        mMainNav.setSelectedItemId(R.id.nav_profile);

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent friendsIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
                sendTo(friendsIntent);

            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newPost = new Intent(ProfileActivity.this, NewPostActivity.class);
                sendTo(newPost);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                picURL = result.getUri();
                fotoP.setImageURI(picURL);

                //escondo la barra de progreso
                profileProgress.setVisibility(View.INVISIBLE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    //cambio de activities principales
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){

                        case R.id.nav_feed :
                            Intent feedIntent = new Intent(ProfileActivity.this, FeedActivity.class);
                            sendTo(feedIntent);
                            return true;


                        case R.id.nav_flashes:
                            Intent flashesIntent = new Intent(ProfileActivity.this, FlashesActivity.class);
                            sendTo(flashesIntent);
                            return true;


                        case R.id.nav_profile:
                            return true;

                        case R.id.nav_chat:
                            Intent chatIntent = new Intent(ProfileActivity.this, ChatSelectionActivity.class);
                            sendTo(chatIntent);
                            return true;

                        default:
                            return false;

                    }
                }
            };


    //--------------Metodos Privados-------------//

    private void sendTo(Intent intent) {
        startActivity(intent);
    }

    private void changeProfileName() {
        TextView txtCambiado = (TextView)findViewById(R.id.userName);
        txtCambiado.setText(name);
    }

    private void changeProfilePic(){
        String user_id = user.getUid();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //si existe este documento
                    if (task.getResult().exists()) {

                        //levanto la imagen del usuario
                        String image = task.getResult().getString("image");
                        picURL = Uri.parse(image);

                        //uso la libreria Glide para cargar la imagen a la App
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.cameranext);
                        Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(fotoP);
                    }
                    else{
                        Toast.makeText(ProfileActivity.this, "El usuario no tiene imagen de perfil",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "FIRESTORE Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    //Boton de seguimiento
        /*fab = (FloatingActionButton) findViewById(R.id.fabFriends);
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP){
                    ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{0xB5137F80});
                    fab.setBackgroundTintList(csl);
                    fab.setPressed(true);
                }

                return true;
            }
        });*/

}
