package com.picapp.picapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText profileName;

    private android.support.v7.widget.Toolbar mainToolbar;

    private FirebaseAuth mAuth;

    private Uri mainImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //levanto las imagenes y text
        profileImage = findViewById(R.id.contenedorPresentacion);
        profileName = (EditText) findViewById(R.id.account_name);

        //cargo la imagen de perfil actual y el nombre actual
        FirebaseUser user = mAuth.getCurrentUser();
        profileName.setText(user.getDisplayName());


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

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(6,5)
                                .start(AccountSettingsActivity.this);

                    }

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

                updateAccount();

                return true;

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

    private void updateAccount() {

        String user_name = profileName.getText().toString();


    }
}
