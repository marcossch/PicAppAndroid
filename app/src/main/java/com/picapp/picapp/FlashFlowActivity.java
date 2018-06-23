package com.picapp.picapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.picapp.picapp.AndroidModels.FlashesAdapter;

import java.util.ArrayList;

public class FlashFlowActivity extends AppCompatActivity {

    private HorizontalInfiniteCycleViewPager infiniteCycle;
    private FlashesAdapter flashAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_flow);

        infiniteCycle = findViewById(R.id.flashView);

        //levanto los parametros pasados
        final Intent myIntent = getIntent();
        Bundle parametros = myIntent.getExtras();
        ArrayList<String> flashList = parametros.getStringArrayList("flashes");
        ArrayList<String> flashListNames = parametros.getStringArrayList("flashesNames");
        ArrayList<String> flashListDates = parametros.getStringArrayList("flashesDates");
        ArrayList<String> flashListLocaions = parametros.getStringArrayList("flashesLocations");

        //Toast.makeText(FlashFlowActivity.this, flashList.get(0), Toast.LENGTH_LONG).show();

        //cargo la lista de stories
        flashAdapter = new FlashesAdapter(flashList, flashListNames, flashListDates, flashListLocaions, getBaseContext());
        infiniteCycle.setAdapter(flashAdapter);
        infiniteCycle.notifyDataSetChanged();

    }
}
