package com.picapp.picapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;

import android.view.MenuItem;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //barra de navegacion
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        mMainNav.setOnNavigationItemSelectedListener(navListener);
        mMainNav.setSelectedItemId(R.id.nav_profile);

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

                        default:
                            return false;

                    }
                }
            };

        /*Button btn = (Button) findViewById(R.id.floatingActionButton);
        btn.setBackgroundResource(R.drawable.icon_unfollow);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override    public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.icon_follow);
                }
                if (event.getAction() ==MotionEvent.AXIS_PRESSURE){
                    btn.setBackgroundResource(R.drawable.icon_follow);
                }
                else {
                    btn.setBackgroundResource(R.drawable.icon_unfollow);
                }
                return false;
            }
        });*/
    //}


    //--------------Metodos Privados-------------//

    private void sendTo(Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();

    }

}
