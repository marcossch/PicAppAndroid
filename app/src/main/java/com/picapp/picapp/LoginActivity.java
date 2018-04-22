package com.picapp.picapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPasswordText;
    private Button loginBtn;
    private Button loginRegisterBtn;

    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();

        //levanto todos los botones y textos
        loginEmailText = (EditText) findViewById(R.id.loginEmailText);
        loginPasswordText = (EditText) findViewById(R.id.loginPasswordText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginRegisterBtn = (Button) findViewById(R.id.loginRegisterBtn);
        loginProgress = (ProgressBar) findViewById(R.id.loginProgress);

        loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToRegister();

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail = loginEmailText.getText().toString();
                String loginPassword = loginPasswordText.getText().toString();

                //si el email y la pass son no vacios
                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)){

                    //que se ve la barra de progreso
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                //si se logueo bien lo mando a la actividad principal
                                sendToMain();

                            } else {

                                //aca hay que manejar el error
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                            }

                            //escondo la barra de progreso
                            loginProgress.setVisibility(View.INVISIBLE);

                        }
                    });

                }
                else{
                    Toast.makeText(LoginActivity.this, "Error : campos vacios", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //chequea si el usuario ya esta logueado
        // si lo esta lo manda a la pagina principal
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si el usuario esta logueado lo mando a la actividad principal
            sendToMain();

        }

    }

    private void sendToMain() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    private void sendToRegister() {

        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }
}
