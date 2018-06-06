package com.picapp.picapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class FlashesActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashes);

        //barra de navegacion
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(navListener);
        mMainNav.setSelectedItemId(R.id.nav_flashes);

    }

    //cambio de activities principales
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){

                        case R.id.nav_feed :
                            Intent feedIntent = new Intent(FlashesActivity.this, FeedActivity.class);
                            sendTo(feedIntent);
                            return true;


                        case R.id.nav_flashes:
                            return true;


                        case R.id.nav_chat:
                            Intent chatIntent = new Intent(FlashesActivity.this, ChatSelectionActivity.class);
                            sendTo(chatIntent);
                            return true;

                        case R.id.nav_profile:
                            Intent profileIntent = new Intent(FlashesActivity.this, ProfileActivity.class);
                            sendTo(profileIntent);
                            return true;

                        default:
                            return false;

                    }
                }
            };

    //--------------Metodos Privados-------------//

    private void sendTo(Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();

    }
}
