package com.picapp.picapp;

    import android.app.Dialog;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.Bitmap;
    import android.location.Location;
    import android.location.LocationManager;
    import android.media.MediaPlayer;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.Settings;
    import android.support.annotation.NonNull;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.content.ContextCompat;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.MediaController;
    import android.widget.ProgressBar;
    import android.widget.Switch;
    import android.widget.Toast;
    import android.widget.VideoView;

    import com.google.android.gms.common.ConnectionResult;
    import com.google.android.gms.common.GoogleApiAvailability;
    import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
    import com.google.android.gms.common.GooglePlayServicesRepairableException;
    import com.google.android.gms.common.api.Status;
    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.location.places.Place;
    import com.google.android.gms.location.places.ui.PlaceAutocomplete;
    import com.google.android.gms.location.places.ui.PlacePicker;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.google.firebase.storage.UploadTask;
    import com.picapp.picapp.AndroidModels.Picapp;
    import com.picapp.picapp.Interfaces.WebApi;
    import com.picapp.picapp.Models.FlashRequest;
    import com.picapp.picapp.Models.FlashResult;
    import com.picapp.picapp.Models.StoryDeleted;
    import com.picapp.picapp.Models.StoryRequest;
    import com.picapp.picapp.Models.StoryResult;
    import com.theartofdev.edmodo.cropper.CropImage;
    import com.theartofdev.edmodo.cropper.CropImageView;

    import java.util.HashMap;
    import java.util.Map;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

public class NewPostVideoActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 999;
    private android.support.v7.widget.Toolbar mainToolbar;
    private ProgressBar newPostProgress;
    private String user_id;

    //Location
    private String TAG = "LOCATION";
    private String Ubicacion = "";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private Button locButton;
    private Button filterButton;
    private double longActual;
    private double latActual;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;

    private Retrofit retrofit;
    private WebApi webApi;

    private ImageView newImage;
    private VideoView newVideo;
    private EditText description;
    private EditText titulo;
    private Uri mainImageURI = null;
    private Switch privacidad;
    private Switch flash;
    private String token = "";
    private StoryRequest storyRequest;
    private FlashRequest flashRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        token = picapp.getToken();

        if (token == null) {
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
        getSupportActionBar().setTitle("PicApp - Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //levanto las imagenes, text y barra de progreso
        newImage = findViewById(R.id.contenedorPresentacion);
        newImage.setVisibility(View.GONE);
        newVideo = findViewById(R.id.videoPresentacion);
        newVideo.setVisibility(View.VISIBLE);
        description = (EditText) findViewById(R.id.descripcion);
        titulo = (EditText) findViewById(R.id.titulo);
        newPostProgress = (ProgressBar) findViewById(R.id.newPostProgress);
        privacidad = (Switch) findViewById(R.id.privacidad);
        flash = (Switch) findViewById(R.id.flash);
        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setVisibility(View.GONE);

        //para elegir un video
        newVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mainImageURI == null) {
                    bringVideoPicker();
                }
                return false;
            }
        });

        //Obtengo la instancia del cliente para la ubicacion
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Obtego los permisos necesarios + activar gps
        getLocationPermission();
        //Chequeo de permisos(no es necesario pero android studio se queja)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(NewPostVideoActivity.this, "Error al obtener permisos para la ubicacion actual", Toast.LENGTH_LONG).show();

        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latActual = location.getLatitude();
                            longActual = location.getLatitude();
                        }
                    }
                });

        locButton = findViewById(R.id.locationButton);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServicesOk()) {

                    Intent builder = null;
                    try {
                        builder = new PlacePicker.IntentBuilder().build(NewPostVideoActivity.this);
                        startActivityForResult(builder, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        Log.d(TAG, "Error de google places. Excepcion reparable.");
                        Toast.makeText(NewPostVideoActivity.this, "Error al inicializar google places. \n" +
                                "Asegurese de tener correctamente instalado y actualizado el servicio.", Toast.LENGTH_LONG).show();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Log.d(TAG, "Error de google places. Excepcion inesperada.");
                        Toast.makeText(NewPostVideoActivity.this, "No selecciono ninguna ubicacion \n", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void getLocationPermission() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {

                mainImageURI = data.getData();
                newVideo.setVideoURI(mainImageURI);
                MediaController mediaController = new MediaController(NewPostVideoActivity.this);
                mediaController.setAnchorView(newVideo);
                newVideo.setMediaController(mediaController);

                newVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
//            video.setMediaController((MediaController) null);
                newVideo.start();

            }
        }


        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Ubicacion = (String) place.getName();
                locButton.setText(Ubicacion);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.d(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Operacion cancelada.");
                Toast.makeText(NewPostVideoActivity.this, "Operación cancelada. " +
                        "Intente nuevamente en unos minutos.", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                Ubicacion = (String) place.getAddress();
                locButton.setText(Ubicacion);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.d(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Operacion cancelada.");
                Toast.makeText(NewPostVideoActivity.this, "Operación cancelada. " +
                        "Intente nuevamente en unos minutos.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.new_post_menu, menu);

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

            case R.id.media_switch:
                item.setTitle("Foto");
                sendToFoto();
                return true;

        }

        sendToProfile();
        return false;
    }


    //--------------Metodos Privados-------------//

    private void sendToProfile() {
        Intent profileIntent = new Intent(NewPostVideoActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        finish();
    }


    private void bringVideoPicker() {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
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
        StorageReference image_path = storageReference.child("stories").child(timestamp.toString() + ".mp4");

        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    final Uri download_uri = task.getResult().getDownloadUrl();
                    uploadPostToServer(mAuth.getCurrentUser(), timestamp, download_uri);

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(NewPostVideoActivity.this, "IMAGE Error: " + error, Toast.LENGTH_LONG).show();
                    //escondo la barra de progreso
                    newPostProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void storeFirestore(final StoryResult story) {

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("image_id", story.getStoryId());
        postMap.put("image", story.getMedia());
        postMap.put("thumb", "thumbUri");
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
                    Toast.makeText(NewPostVideoActivity.this, "FIRESTORE Error: " + error, Toast.LENGTH_LONG).show();

                    //elimino la story del server
                    serverDeleteStory(story);

                    //escondo la barra de progreso
                    newPostProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void storeFirestoreFlash(final FlashResult story) {

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("image_id", story.getFlashId());
        postMap.put("image", story.getMedia());
        postMap.put("thumb", "thumbUri");
        postMap.put("user_id", user_id);
        postMap.put("timestamp", story.getTimestamp());
        postMap.put("description", story.getDescription());
        postMap.put("title", story.getTitle());
        postMap.put("location", story.getLocation());
        postMap.put("isPrivate", "false");

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
                    Toast.makeText(NewPostVideoActivity.this, "FIRESTORE Error: " + error, Toast.LENGTH_LONG).show();

                    //escondo la barra de progreso
                    newPostProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void serverDeleteStory(StoryResult story) {

        Call<StoryDeleted> call = webApi.deleteStory(story.getStoryId(), token, "Application/json");
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
        storyRequest.setLocation(Ubicacion);
        storyRequest.setTimestamp(timestamp);
        storyRequest.setTitle(titulo.getText().toString());
        storyRequest.setIsPrivate(!(privacidad.isChecked()));

        //Creo la request para pasarle en el body
        flashRequest = new FlashRequest();
        flashRequest.setMedia(imageUri.toString());
        flashRequest.setDescription(description.getText().toString());
        //completar la descripcion
        flashRequest.setLocation(Ubicacion);
        flashRequest.setTimestamp(timestamp);
        flashRequest.setTitle(titulo.getText().toString());

        callServer();

    }

    private void callServer() {

        //chequeo de flash o story
        if(flash.isChecked()){
            Call<FlashResult> call = webApi.postFlash(flashRequest, token, "Application/json");

            call.enqueue(new Callback<FlashResult>() {
                @Override
                public void onResponse(Call<FlashResult> call, Response<FlashResult> response) {

                    final FlashResult storyResult = response.body();
                    storeFirestoreFlash(storyResult);
                }

                @Override
                public void onFailure(Call<FlashResult> call, Throwable t) {

                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    //escondo la barra de progreso
                    newPostProgress.setVisibility(View.INVISIBLE);
                }
            });

        }
        else{
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

    private void sendToFeed() {
        Intent feedIntent = new Intent(NewPostVideoActivity.this, FeedActivity.class);
        startActivity(feedIntent);
        finish();
    }

    private void sendToFoto() {
        Intent videoIntent = new Intent(NewPostVideoActivity.this, NewPostActivity.class);
        startActivity(videoIntent);
        finish();
    }

    public boolean isServicesOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable( NewPostVideoActivity.this );
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "El servicio de google maps esta OK");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "Ocurrio un error con el servicio de google, pero se puede arreglar");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(NewPostVideoActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "No se puede conectar al servicio de google places.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
