package com.picapp.picapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.picapp.picapp.AndroidModels.FeedRecyclerAdapter;
import com.picapp.picapp.AndroidModels.FeedStory;
import com.picapp.picapp.AndroidModels.ReactionStory;
import com.picapp.picapp.AndroidModels.ReactionsAdapter;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.Reaction;
import com.picapp.picapp.Models.UserPreview;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReaccionesActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;
    private RecyclerView reactions_list_view;
    private List<ReactionStory> reactions_list;
    private ReactionsAdapter reactionsRecyclerAdapter;
    private String name;
    private ProgressBar reactionsProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reacciones);

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //que se ve la barra de progreso
        reactionsProgress = (ProgressBar) findViewById(R.id.reactionsProgress);
        //que se ve la barra de progreso
        reactionsProgress.setVisibility(View.VISIBLE);

        //levanto la lista de visualizacion de stories
        reactions_list_view = (RecyclerView) findViewById(R.id.reacciones_list_view);


        //cargo la lista de stories
        reactions_list = new ArrayList<>();
        reactionsRecyclerAdapter = new ReactionsAdapter(reactions_list);
        reactions_list_view.setLayoutManager(new LinearLayoutManager(ReaccionesActivity.this));
        reactions_list_view.setAdapter(reactionsRecyclerAdapter);

        //creo retrofit que es la libreria para manejar Apis
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);


        //levanto los parametros pasados
        final Intent myIntent = getIntent();
        Bundle parametros = myIntent.getExtras();
        String token = (String) parametros.get("token");

        for (final String username : parametros.keySet()){

            if(username != "token" && username.length()>0){

                Call<UserPreview> call = webApi.getPreview(username, token, "Application/json");
                call.enqueue(new Callback<UserPreview>() {
                    @Override
                    public void onResponse(Call<UserPreview> call, Response<UserPreview> response) {

                        //muestro la barra de progreso
                        reactionsProgress.setVisibility(View.VISIBLE);

                        UserPreview userPreview = response.body();
                        if(userPreview != null){
                            name = response.body().getName();
                            ReactionStory reactionStory = new ReactionStory();
                            reactionStory.setUsername(name);
                            reactionStory.setReaction(myIntent.getStringExtra(username));

                            reactions_list.add(reactionStory);
                            reactionsRecyclerAdapter.notifyDataSetChanged();
                        }
                        //oculto la barra de progreso
                        reactionsProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<UserPreview> call, Throwable t) {
                        Log.d("UPDATE USER: ", "-----> No se pudo cargar la preview <-----" + t.getMessage());
                        //oculto la barra de progreso
                        reactionsProgress.setVisibility(View.INVISIBLE);
                    }
                });

            }

        }
    }
}
