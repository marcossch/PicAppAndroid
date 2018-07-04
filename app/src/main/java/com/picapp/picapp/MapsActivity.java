package com.picapp.picapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private String TAG = "MAPS";
    private GoogleMap gmap;
    private android.support.v7.widget.Toolbar mainToolbar;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FirebaseUser firUser;
    private Intent intent;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("id");
        firUser = FirebaseAuth.getInstance().getCurrentUser();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_menu, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Levanto las ubicaciones de las distintas publicaciones
        String strLL = getIntent().getStringExtra("LatLong");
        String[] latlng = strLL.split(";");

        gmap = googleMap;
        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        if(strLL.isEmpty()){
            Toast.makeText(MapsActivity.this, "No se encontraron ubicaciones en las stories.", Toast.LENGTH_LONG).show();
            return;
        }

        Double cLat = 0.0;
        Double cLng = 0.0;
        for(String ll : latlng){
            String[] auxLL = ll.split(",");
            if(ll.contains("res")){
                continue;
            }
            Double lat = Double.parseDouble(auxLL[0]);
            Double lng = Double.parseDouble(auxLL[1]);
            cLat += lat;
            cLng += lng;
            LatLng newLL = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(newLL);
            gmap.addMarker(markerOptions);
        }

        Double length = Double.valueOf(latlng.length);
        cLat = cLat/length;
        cLng = cLng/length;
        LatLng newLL = new LatLng(cLat, cLng);

        CameraPosition.Builder camBuilder = CameraPosition.builder();
        camBuilder.bearing(45);
        camBuilder.tilt(30);
        camBuilder.target(newLL);
        CameraPosition cp = camBuilder.build();
        gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //se cargan las acciones del menu de opciones
        switch (item.getItemId()) {

            case R.id.action_search_btn:
                //usar el buscador
                return true;

        }
        sendToProfile();
        return false;
    }

    //--------------Metodos Privados-------------//

    private void sendToProfile() {
        if(id.contains(firUser.getUid())){
            intent = new Intent(MapsActivity.this, ProfileActivity.class);
            Log.d("FRIENDS", "--------------> VAMOS A PROFILE ACTIVITY <--------------");
        }
        else{
            intent = new Intent(MapsActivity.this, FriendProfileActivity.class);
            intent.putExtra("id",id);
            Log.d("FRIENDS", "--------------> VAMOS A FRIEND PROFILE ACTIVITY <--------------");
        }
        startActivity(intent);
        finish();
    }
}