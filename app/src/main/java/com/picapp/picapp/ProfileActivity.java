package com.picapp.picapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
    }


}
