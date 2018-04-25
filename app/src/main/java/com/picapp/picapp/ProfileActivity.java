package com.picapp.picapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
<<<<<<< HEAD
=======
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
>>>>>>> 84ae8bea04735abd0e0c1e0810b6fcd52562e08e

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
<<<<<<< HEAD
    }
=======

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


>>>>>>> 84ae8bea04735abd0e0c1e0810b6fcd52562e08e
}
