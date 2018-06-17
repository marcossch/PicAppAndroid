package com.picapp.picapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.picapp.picapp.Models.SelectableUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OtherProfileActivity extends AppCompatActivity {

    private String name;
    private String id;
    private String pic;
    private FirebaseFirestore firebaseFirestore;
    private android.support.design.widget.FloatingActionButton friendshipBtn;
    private FirebaseUser currentUser;
    private ArrayList<String> recibidas;
    private ArrayList<String> enviadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        recibidas = new ArrayList<>();
        enviadas = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Setteo los parametros pasados por parametro
        setParams();
        TextView nameView = findViewById(R.id.userName);
        nameView.setText(this.name);
        ImageView imgView = findViewById(R.id.contenedorFotoPerfil);
        Glide.with(this).load(this.pic).into(imgView);

        //Obtengo el boton para enviar solicitudes de amistad
        friendshipBtn = findViewById(R.id.agregarAmigos);
        //Obtengo las solicitudes de amistad enviadas, y recibidas
        DocumentReference docRef = firebaseFirestore.collection("Solicitudes").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        recibidas = (ArrayList<String>) document.getData().get("recibidas");
                        enviadas = (ArrayList<String>) document.getData().get("enviadas");
                        //Toast.makeText(OtherProfileActivity.this, document.getData().get("enviadas").toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        Toast.makeText(OtherProfileActivity.this, enviadas.toString(), Toast.LENGTH_LONG).show();

/*        firebaseFirestore.collection("Solicitudes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {
                }
            }
        });         */
        friendshipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //friendshipBtn.setBackgroundResource(R.drawable.icon_delete_friend);
                //friendshipBtn.setBackgroundTintList(getResources().getColorStateList(R.color.yellow));
                //storeFirestore();
            }
        });



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

    private void storeFirestore() {

        Map<String, ArrayList<String>> userMap = new HashMap<>();
        ArrayList<String> a = new ArrayList<>();
        a.add("amigoasd");
        a.add("fqweeo");
        userMap.put("enviadas",a );

        //cada usuario tiene su propio documento
        firebaseFirestore.collection("Solicitudes").document(currentUser.getUid()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                } else {

                    String error = task.getException().getMessage();
                }
            }
        });

    }
}
