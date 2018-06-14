package com.picapp.picapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.picapp.picapp.AndroidModels.FeedRecyclerAdapter;
import com.picapp.picapp.AndroidModels.FeedStory;
import com.picapp.picapp.AndroidModels.ReactionStory;
import com.picapp.picapp.AndroidModels.ReactionsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReaccionesActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;
    private RecyclerView reactions_list_view;
    private List<ReactionStory> reactions_list;
    private ReactionsAdapter reactionsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reacciones);

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //levanto la lista de visualizacion de stories
        reactions_list_view = (RecyclerView) findViewById(R.id.reacciones_list_view);


        //cargo la lista de stories
        reactions_list = new ArrayList<>();
        reactionsRecyclerAdapter = new ReactionsAdapter(reactions_list);
        reactions_list_view.setLayoutManager(new LinearLayoutManager(ReaccionesActivity.this));
        reactions_list_view.setAdapter(reactionsRecyclerAdapter);

        //levanto los parametros pasados
        Intent myIntent = getIntent();
        Bundle parametros = myIntent.getExtras();

        for (String username : parametros.keySet()){

            ReactionStory reactionStory = new ReactionStory();
            reactionStory.setUsername(username);
            reactionStory.setReaction(myIntent.getStringExtra(username));

            reactions_list.add(reactionStory);
            reactionsRecyclerAdapter.notifyDataSetChanged();

        }
    }
}
