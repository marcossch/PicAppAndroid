package com.picapp.picapp;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.picapp.picapp.Models.SearchAdapter;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText searchView;
    private RecyclerView peopleList;
    private DatabaseReference dataRef;
    private FirebaseUser firUser;
    private ArrayList<String> nameList;
    private ArrayList<String> picList;
    private ArrayList<String> idList;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Levanto el buscador.
        searchView = (EditText) findViewById(R.id.peopleSearch);
        //Levanto la lista de gente
        peopleList = (RecyclerView) findViewById(R.id.peopleList);

        //obtengo los usuarios de firebase
        dataRef = FirebaseDatabase.getInstance().getReference();
        firUser = FirebaseAuth.getInstance().getCurrentUser();

        peopleList.setHasFixedSize(true);
        peopleList.setLayoutManager(new LinearLayoutManager(this));
        peopleList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        picList = new ArrayList<>();
        nameList = new ArrayList<>();
        idList = new ArrayList<>();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());
                }
                else{
                    nameList.clear();
                    picList.clear();
                    idList.clear();
                    peopleList.removeAllViews();
                }
            }
        });

    }

    private void setAdapter(final String searchString) {
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                nameList.clear();
                picList.clear();
                idList.clear();
                peopleList.removeAllViews();
                int count = 0;

                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {
                    Map<String, Object> data = doc.getData();
                    String idCurrentU = firUser.getUid();
                    String idActual = doc.getId();
                    String name = (String) data.get("name");
                    String pic = (String) data.get("image");
                    //dataRef.child("Users").child(idActual).getKey();
                    String lowerName = name.toLowerCase();
                    String lowerSearch = searchString.toLowerCase();
                    if( (!idCurrentU.contains(idActual)) &&
                            (lowerName.substring(0, Math.min(lowerName.length(), lowerSearch.length())).contains(lowerSearch)) ){
                        idList.add(idActual);
                        nameList.add(name);
                        picList.add(pic);
                        count = count + 1;
                    }
                    if(count==10){
                        break;
                    }

                }
                searchAdapter = new SearchAdapter(SearchActivity.this, nameList, picList, idList, "search");
                peopleList.setAdapter(searchAdapter);

            }
        });
    }

}
