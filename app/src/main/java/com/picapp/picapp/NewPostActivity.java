package com.picapp.picapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.StoryDeleted;
import com.picapp.picapp.Models.StoryRequest;
import com.picapp.picapp.Models.StoryResult;
import com.picapp.picapp.Models.User;
import com.picapp.picapp.Models.UserRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewPostActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;
    private ProgressBar newPostProgress;
    private String user_id;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;

    private Retrofit retrofit;
    private WebApi webApi;

    private ImageView newImage;
    private EditText description;
    private EditText titulo;
    private Uri mainImageURI = null;
    private Switch privacidad;
    private String token = "";
    private StoryRequest storyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

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
        newImage = findViewById(R.id.contenedorPresentacion);
        description = (EditText) findViewById(R.id.descripcion);
        titulo = (EditText) findViewById(R.id.titulo);
        newPostProgress = (ProgressBar) findViewById(R.id.newPostProgress);
        privacidad = (Switch) findViewById(R.id.privacidad);


        //para elegir una imagen
        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bringImagePicker();

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
                newImage.setImageURI(mainImageURI);

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

                //solo actualiza si se cargo una imagen para subir
                if(mainImageURI != null) {
                    updateAccount();
                }
                return true;

        }

        sendToProfile();
        return false;
    }


    //--------------Metodos Privados-------------//

    private void sendToProfile() {
        Intent profileIntent = new Intent(NewPostActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        finish();
    }

    private void bringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(5,6)
                .start(NewPostActivity.this);
    }

    private void updateAccount() {

        //que se ve la barra de progreso
        newPostProgress.setVisibility(View.VISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();

        //actualizo el nombre de usuario
        final String descripcion = description.getText().toString();
        final String title = titulo.getText().toString();
        user_id = user.getUid();


        //defino donde se va a  guardar la imagen
        final Long timestamp = System.currentTimeMillis();
        StorageReference image_path = storageReference.child("stories").child(timestamp.toString() + ".jpg");

        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    final Uri download_uri = task.getResult().getDownloadUrl();
                    uploadPostToServer(mAuth.getCurrentUser(), timestamp, download_uri);

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(NewPostActivity.this, "IMAGE Error: " + error, Toast.LENGTH_LONG).show();
                    //escondo la barra de progreso
                    newPostProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void storeFirestore(final StoryResult story) {

        //comprimo el thumbnail
        File newImageFile = new File(mainImageURI.getPath());

        try {
            compressedImageFile = new Compressor(NewPostActivity.this)
                    .setMaxWidth(125)
                    .setMaxHeight(150)
                    .setQuality(5)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();

        UploadTask uploadTask = storageReference.child("stories/thumbs")
                .child(story.getTimestamp().toString() + ".jpg").putBytes(thumbData);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String thumbUri = taskSnapshot.getDownloadUrl().toString();

                Map<String, Object> postMap = new HashMap<>();
                postMap.put("image_id", story.getStoryId());
                postMap.put("image", story.getMedia());
                postMap.put("thumb", thumbUri);
                postMap.put("user_id", user_id);
                postMap.put("timestamp", story.getTimestamp());
                postMap.put("description", story.getDescription());
                postMap.put("title", story.getTitle());
                postMap.put("location", story.getLocation());
                postMap.put("isPrivate", story.getIsPrivate());

                //cada usuario tiene su propio documento
                firebaseFirestore.collection("Stories").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()) {

                            //escondo la barra de progreso
                            newPostProgress.setVisibility(View.INVISIBLE);
                            sendToProfile();

                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(NewPostActivity.this, "FIRESTORE Error: " + error, Toast.LENGTH_LONG).show();

                            //elimino la story del server
                            serverDeleteStory(story);

                            //escondo la barra de progreso
                            newPostProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String error = e.getMessage();
                Toast.makeText(NewPostActivity.this, "FIRESTORE Error: " + error, Toast.LENGTH_LONG).show();

                //elimino la story del server
                serverDeleteStory(story);

                //escondo la barra de progreso
                newPostProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void serverDeleteStory(StoryResult story) {

        Call<StoryDeleted> call = webApi.deleteStory(story.getStoryId());
        call.enqueue(new Callback<StoryDeleted>() {
            @Override
            public void onResponse(Call<StoryDeleted> call, Response<StoryDeleted> response) {

            }

            @Override
            public void onFailure(Call<StoryDeleted> call, Throwable t) {

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                //escondo la barra de progreso
                newPostProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void uploadPostToServer(final FirebaseUser user, final Long timestamp, final Uri imageUri) {

        //creo retrofit que es la libreria para manejar Apis
        retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webApi = retrofit.create(WebApi.class);

        //Creo la request para pasarle en el body
        storyRequest = new StoryRequest();
        storyRequest.setMedia(imageUri.toString());
        storyRequest.setDescription(description.getText().toString());
        //completar la descripcion
        storyRequest.setLocation("ubicación");
        storyRequest.setTimestamp(timestamp);
        storyRequest.setTitle(titulo.getText().toString());
        storyRequest.setIsPrivate(!(privacidad.isChecked()));

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        token = picapp.getToken();
        callServer();

    }

    private void callServer() {
        Call<StoryResult> call = webApi.postStory(storyRequest, token, "Application/json");
        call.enqueue(new Callback<StoryResult>() {
            @Override
            public void onResponse(Call<StoryResult> call, Response<StoryResult> response) {

                final StoryResult storyResult = response.body();
                storeFirestore(storyResult);
            }

            @Override
            public void onFailure(Call<StoryResult> call, Throwable t) {

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                //escondo la barra de progreso
                newPostProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

}
