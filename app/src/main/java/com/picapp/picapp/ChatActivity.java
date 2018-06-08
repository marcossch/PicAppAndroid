package com.picapp.picapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.picapp.picapp.Models.SessionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private android.support.v7.widget.Toolbar mainToolbar;

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        layout = (LinearLayout) findViewById(R.id.layout1);
        mainToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(SessionData.onChat.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        messageArea.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Cargando mensajes...");
        progressDialog.show();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    // Create object to store in db
                    Map<String, Object> message = new HashMap<>();
                    message.put("user", userId);
                    message.put("message", messageText);
                    message.put("timestamp", Timestamp.now());
                    // Store in both documents
                    storeInDB(userId, SessionData.onChat.getId(), message);
                    storeInDB(SessionData.onChat.getId(), userId, message);
                    // Restore write field
                    messageArea.setText("");
                }
            }
        });

        firebaseFirestore.collection("Chat")
                .document(String.format("%s_%s", userId, SessionData.onChat.getId()))
                .collection("message")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) return;

                for (DocumentChange doc : snapshots.getDocumentChanges()) {
                    switch (doc.getType()) {
                        case ADDED:
                            String user = (String) doc.getDocument().getData().get("user");
                            String messageText = (String) doc.getDocument().getData().get("message");
                            if (user.equals(userId)) addMessageBox(messageText, 1);
                            else addMessageBox(messageText, 0);
                            break;
                        case MODIFIED:
                        case REMOVED:
                            break;
                    }
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //levanta la barra del menu con sus items

        getMenuInflater().inflate(R.menu.chat_menu, menu);

        return true;
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);

        } else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setMaxWidth(550);
        textView.setPadding(15, 15, 15, 15);
        textView.setLayoutParams(lp2);
        textView.setTextColor(Color.WHITE);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void storeInDB(String user1, String user2, Map<String, Object> object){
        firebaseFirestore.collection("Chat")
                            .document(String.format("%s_%s", user1, user2))
                            .collection("message")
                            .document(UUID.randomUUID().toString())
                            .set(object);
    }
}