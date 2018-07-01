package com.picapp.picapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.FriendshipResponse;
import com.picapp.picapp.Models.FriendshipStatus;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OtherProfileActivity extends AppCompatActivity {

    private String name;
    private String id;
    private String pic;
    private String status;
    private FirebaseFirestore firebaseFirestore;
    private android.support.design.widget.FloatingActionButton addFriendsBtn;
    private android.support.design.widget.FloatingActionButton deleteFriendsBtn;
    private FirebaseUser currentUser;
    private ArrayList<String> recibidas;
    private ArrayList<String> enviadas;
    private Retrofit retrofit;
    private android.support.v7.widget.Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        //levanta la toolbar
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PicApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Levanto el token desde la applicacion.
        final Picapp picapp = Picapp.getInstance();
        final String token = picapp.getToken();

        if(token == null) {
            Log.d("TOKEN: ", "-----> El token es Null. <-----");
        }
        
        //Inicializaciones:
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);
        
        recibidas = new ArrayList<>();
        enviadas = new ArrayList<>();
        
        //Setteo los parametros pasados por parametro
        setParams();
        //Agrego los parametros al nuevo perfil
        TextView nameView = findViewById(R.id.userName);
        nameView.setText(this.name);
        ImageView imgView = findViewById(R.id.contenedorFotoPerfil);
        Glide.with(this).load(this.pic).into(imgView);

        //Obtengo los botones de solicitudes
        addFriendsBtn = findViewById(R.id.agregarAmigos);
        deleteFriendsBtn = findViewById(R.id.borrarAmigos);

        //levanto el nombre del usuario
        Call<FriendshipStatus> friendshipStatus = webApi.getFriendshipStatus(id, token, "Application/json");
        friendshipStatus.enqueue(new Callback<FriendshipStatus>() {
            @Override
            public void onResponse(Call<FriendshipStatus> call, Response<FriendshipStatus> response) {
                status = response.body().getState();
                setButtonConditions(status);
            }

            @Override
            public void onFailure(Call<FriendshipStatus> call, Throwable t) {
                Log.d("FRIENDSHIP STATUS", "-----> No se pudo obtener el estado de amistad <-----");
            }
        });

        addFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<FriendshipResponse> friendshipResponse = webApi.postFriendship(id, token, "Application/json");
                friendshipResponse.enqueue(new Callback<FriendshipResponse>() {

                    @Override
                    public void onResponse(Call<FriendshipResponse> call, Response<FriendshipResponse> response) {
                        //levanto el nombre del usuario
                        Call<FriendshipStatus> friendshipStatus = webApi.getFriendshipStatus(id, token, "Application/json");
                        friendshipStatus.enqueue(new Callback<FriendshipStatus>() {
                            @Override
                            public void onResponse(Call<FriendshipStatus> call, Response<FriendshipStatus> response) {
                                status = response.body().getState();
                                // Send notification
                                if (status.equals("not_friends")) {
                                    HashMap<String, String> notificationData = new HashMap<>();
                                    notificationData.put("from", currentUser.getUid());
                                    firebaseFirestore.collection("Notifications")
                                            .document("request")
                                            .collection(id)
                                            .document(UUID.randomUUID().toString())
                                            .set(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            setButtonConditions(status);
                                            if (status.contains("friends")) {
                                                sendToFeed();
                                            }
                                        }
                                    });
                                } else {
                                    status = response.body().getState();
                                    setButtonConditions(status);
                                    if (status.contains("friends")) {
                                        sendToFeed();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<FriendshipStatus> call, Throwable t) {
                                Log.d("FRIENDSHIP STATUS", "-----> No se pudo obtener el estado de amistad <-----");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<FriendshipResponse> call, Throwable t) {
                        Log.d("ADD FRIEND", "-----> No se pudo enviar la solicitud de amistad <-----");
                    }
                });
            }
        });

        deleteFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<FriendshipResponse> friendshipResponse = webApi.deleteFriendship(id, token, "Application/json");
                friendshipResponse.enqueue(new Callback<FriendshipResponse>() {
                    @Override
                    public void onResponse(Call<FriendshipResponse> call, Response<FriendshipResponse> response) {
                        setButtonConditions("not_friends");
                    }

                    @Override
                    public void onFailure(Call<FriendshipResponse> call, Throwable t) {
                        Log.d("DELETE FRIEND", "-----> No se pudo cancelar la amistad <-----");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.friends_menu, menu);
        return true;
    }

    public void setParams(){
        Intent myIntent = getIntent(); // gets the previously created intent
        String userName = myIntent.getStringExtra("name");
        this.name = userName;
        String profilePic = myIntent.getStringExtra("pic");
        this.pic = profilePic;
        String id = myIntent.getStringExtra("id");
        this.id = id;
    }

    public void setButtonConditions(String state) {
        TextView aux = findViewById(R.id.instrucciones);
        aux.setText("");
        switch(state){
            //Cuando no son amigos, lo unico que podes hacer es enviarla
            case "not_friends":
                deleteFriendsBtn.setVisibility(View.INVISIBLE);
                deleteFriendsBtn.setClickable(false);
                addFriendsBtn.setVisibility(View.VISIBLE);
                addFriendsBtn.setClickable(true);
                break;

            //Cuando recibiste una, podes eliminarla o aceptarla
            case "received":
                Toast.makeText(OtherProfileActivity.this, "El usuario "+name+" te envio una solicitud de amistad.", Toast.LENGTH_LONG).show();
                aux.setText("Puedes aceptar la solicitud de amistad con el boton verde, o eliminarla con el rojo");
                deleteFriendsBtn.setVisibility(View.VISIBLE);
                deleteFriendsBtn.setClickable(true);
                addFriendsBtn.setVisibility(View.VISIBLE);
                addFriendsBtn.setClickable(true);
                break;

            //Cuando son amigos o la enviaste, lo unico que podes hacer es eliminarla
            case "sent":
                deleteFriendsBtn.setVisibility(View.VISIBLE);
                deleteFriendsBtn.setClickable(true);
                addFriendsBtn.setVisibility(View.INVISIBLE);
                addFriendsBtn.setClickable(false);
                break;

            case "friends":
                deleteFriendsBtn.setVisibility(View.VISIBLE);
                deleteFriendsBtn.setClickable(true);
                addFriendsBtn.setVisibility(View.INVISIBLE);
                addFriendsBtn.setClickable(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //se cargan las acciones del menu de opciones
        switch (item.getItemId()) {

            case R.id.action_search_btn:
                //usar el buscador
                return true;

        }
        sendToFeed();
        return false;
    }

    //--------------Metodos Privados-------------//

    private void sendToFeed() {
        Intent feedIntent = new Intent(OtherProfileActivity.this, FeedActivity.class);
        startActivity(feedIntent);
        finish();
    }

}
