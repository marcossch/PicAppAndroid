package com.picapp.picapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AndroidModels.CommentStory;
import com.picapp.picapp.AndroidModels.CommentsRecyclerAdapter;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.AndroidModels.ReactionStory;
import com.picapp.picapp.AndroidModels.ReactionsAdapter;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.Comment;
import com.picapp.picapp.Models.CommentRequest;
import com.picapp.picapp.Models.Reaction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentsActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;
    private RecyclerView comments_list_view;
    private List<CommentStory> comments_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private AutoCompleteTextView message_text;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //levanto el token
        final Picapp picapp = Picapp.getInstance();
        final String token = picapp.getToken();

        if(token == null) {
            Intent mainIntent = new Intent(CommentsActivity.this, MainActivity.class);
            sendTo(mainIntent);
        }

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //instacia de firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //creo retrofit que es la libreria para manejar Apis
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);


        //levanto la lista de visualizacion de stories
        comments_list_view = (RecyclerView) findViewById(R.id.comentarios_list_view);

        //levanto las cosas de un nuevo comment
        message_text = findViewById(R.id.message_text);
        sendButton = findViewById(R.id.send_button);


        //cargo la lista de stories
        comments_list = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(comments_list);
        comments_list_view.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        comments_list_view.setAdapter(commentsRecyclerAdapter);

        //levanto los parametros pasados
        Intent myIntent = getIntent();
        Bundle parametros = myIntent.getExtras();
        final String image_id = (String) parametros.get("image_id");
        final ArrayList<String> comments = myIntent.getStringArrayListExtra("comments");

        for (int i = 0; i < comments.size(); i++) {

            final CommentStory commentStory = new CommentStory();
            commentStory.setComment(comments.get(i+1));
            commentStory.setTimestamp(comments.get(i+2));

            //sacar cuando este el server listo
            //Obtengo la foto de perfil
            firebaseFirestore.collection("Users").document(comments.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //si existe este documento
                        if (task.getResult().exists()) {

                            //levanto la imagen del usuario
                            commentStory.setImage(Uri.parse(task.getResult().getString("image")));
                            commentStory.setUsername(task.getResult().getString("name"));
                            comments_list.add(commentStory);
                            commentsRecyclerAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(CommentsActivity.this, "El usuario no posee una foto de perfil", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(CommentsActivity.this, "FIRESTORE Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
            i += 2;
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //si hay algun comentario
                if (message_text.getText().length() > 0){

                    final CommentRequest comment = new CommentRequest();
                    comment.setComment(String.valueOf(message_text.getText()));
                    final Long timestamp = System.currentTimeMillis();
                    comment.setTimestamp(timestamp);

                    Call<Comment> call = webApi.postComment(comment, image_id,
                            token, "Application/json");
                    call.enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                            //agregar el comment
                        }

                        @Override
                        public void onFailure(Call<Comment> call, Throwable t) {
                            Log.d("UPDATE USER: ", "-----> No se pudo cargar el comentario <-----" + t.getMessage());
                        }
                    });
                    Intent feedIntent = new Intent(CommentsActivity.this, FeedActivity.class);
                    sendTo(feedIntent);
                }
            }
        });

    }

    private void sendTo(Intent intent) {
        startActivity(intent);
        finish();
    }
}
