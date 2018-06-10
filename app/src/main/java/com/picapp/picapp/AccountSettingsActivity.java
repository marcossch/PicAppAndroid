package com.picapp.picapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.Error;
import com.picapp.picapp.Models.User;
import com.picapp.picapp.Models.UserUpdate;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountSettingsActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText profileName;
    private ProgressBar settingsProgress;
    private String user_id;

    private boolean imagenModificada = false;

    private android.support.v7.widget.Toolbar mainToolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private Uri mainImageURI = null;
    private String token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        token = picapp.getToken();

        if(token == null) {
            sendToFeed();
        }

        //instacia de firebase y firestore
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Inicializa la referencia de almacenage
        storageReference = FirebaseStorage.getInstance().getReference();

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //levanto las imagenes, text y barra de progreso
        profileImage = findViewById(R.id.contenedorPresentacion);
        profileName = (EditText) findViewById(R.id.account_name);
        settingsProgress = (ProgressBar) findViewById(R.id.settingsProgress);

        //cargo la imagen de perfil actual y el nombre actual
        FirebaseUser user = mAuth.getCurrentUser();
        user_id = user.getUid();
        profileName.setText(user.getDisplayName());
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    //si existe este documento
                    if(task.getResult().exists()){

                        //levanto la imagen del usuario
                        String image = task.getResult().getString("image");
                        mainImageURI = Uri.parse(image);

                        //uso la libreria Glide para cargar la imagen a la App
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.cameranext);
                        Glide.with(AccountSettingsActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);


                    } else {

                        Toast.makeText(AccountSettingsActivity.this, "No hay una foto de perfil cargada", Toast.LENGTH_LONG).show();

                    }


                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSettingsActivity.this, "FIRESTORE Retrieve Error: " + error, Toast.LENGTH_LONG).show();


                }

                //escondo la barra de progreso
                settingsProgress.setVisibility(View.INVISIBLE);

            }
        });


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

                        bringImagePicker();

                    }

                } else {

                    //si tiene menos que marshmellow no hace falta pedir permisos
                    bringImagePicker();

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                 mainImageURI = result.getUri();
                 profileImage.setImageURI(mainImageURI);

                 imagenModificada = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

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

                //solo actualiza si ya cargo la imagen original
                if(mainImageURI != null) {
                    updateAccount();
                }

                return true;

        }

        sendToFeed();
        return false;
    }


    //--------------Metodos Privados-------------//

    private void bringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(6,5)
                .start(AccountSettingsActivity.this);
    }

    private void sendToFeed() {
        Intent feedIntent = new Intent(AccountSettingsActivity.this, FeedActivity.class);
        startActivity(feedIntent);
        finish();
    }

    private void updateAccount() {

        //que se ve la barra de progreso
        settingsProgress.setVisibility(View.VISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();

        //actualizo el nombre de usuario
        final String new_user_name = profileName.getText().toString();
        user_id = user.getUid();

        //primero actualizo el nombre de perfil
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(new_user_name)
                .build();
        user.updateProfile(profileUpdates);

        if(imagenModificada) {

            //defino donde se va a  guardar la imagen
            StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

            image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        Uri download_uri = task.getResult().getDownloadUrl();
                        storeFirestore(task, new_user_name, download_uri);
                        serverUpdate(new_user_name, user_id, download_uri);

                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(AccountSettingsActivity.this, "IMAGE Error: " + error, Toast.LENGTH_LONG).show();

                    }
                }
            });

        } else{

            //solo actualizo el nombre
            storeFirestore(null, new_user_name, mainImageURI);
            serverUpdate(new_user_name, user_id, mainImageURI);

        }

    }

    private void serverUpdate(String new_user_name, final String user_id, Uri download_uri) {
        //creo retrofit que es la libreria para manejar Apis
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);

        //Creo la request para pasarle en el body
        final UserUpdate userUpd = new UserUpdate();
        userUpd.setUsername(new_user_name);
        userUpd.setProfilePhoto(download_uri.toString());

        executeUpdate(userUpd,webApi, token);
    }

    private void executeUpdate(UserUpdate userUpd, WebApi webApi, String tok){
        Call<Error> call = webApi.updateUser(userUpd ,user_id, tok, "Application/json");
        call.enqueue(new Callback<Error>() {
            @Override
            public void onResponse(Call<Error> call, Response<Error> response) {
                String err = response.toString();
                Log.d("Response", "Debe ser 200------------------>"+err);
            }

            @Override
            public void onFailure(Call<Error> call, Throwable t) {
                Log.d("UPDATE USER: ", "-----> No se pudo actualizar los datos del usuario <-----");
            }
        });
    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String user_name, Uri imageUri) {

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", imageUri.toString());

        //cada usuario tiene su propio documento
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSettingsActivity.this, "FIRESTORE Error: " + error, Toast.LENGTH_LONG).show();

                }

                //escondo la barra de progreso
                settingsProgress.setVisibility(View.INVISIBLE);

                sendToFeed();

            }
        });

    }
}
