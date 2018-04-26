package com.picapp.picapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmailText;
    private EditText registerPasswordText;
    private EditText registerConfirmPasswordText;
    private Button registerBtn;
    private Button registerLoginBtn;

    private ProgressBar registerProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();

        //levanto todos los botones y textos
        registerEmailText = (EditText) findViewById(R.id.registerEmailText);
        registerPasswordText = (EditText) findViewById(R.id.registerPasswordText);
        registerConfirmPasswordText = (EditText) findViewById(R.id.registerConfirmPasswordText);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerLoginBtn = (Button) findViewById(R.id.registerRegisterBtn);
        registerProgress = (ProgressBar) findViewById(R.id.registerProgress);

        hideSoftKeyboard(registerEmailText);
        hideSoftKeyboard(registerPasswordText);
        hideSoftKeyboard(registerConfirmPasswordText);

        registerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToLogin();

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String registerEmail = registerEmailText.getText().toString();
                String registerPassword = registerPasswordText.getText().toString();
                String registerConfirmPassword = registerConfirmPasswordText.getText().toString();

                //si el email y la pass son no vacios
                if(!TextUtils.isEmpty(registerEmail) && !TextUtils.isEmpty(registerPassword) && !TextUtils.isEmpty(registerConfirmPassword)){

                    if(registerPassword.equals(registerConfirmPassword)) {

                        //que se ve la barra de progreso
                        registerProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(registerEmail, registerPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    //si se registro bien lo mando a la actividad principal
                                    //esto hay que cambiarlo
                                    sendToLogin();

                                } else {

                                    //aca hay que manejar el error
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                                }

                                //escondo la barra de progreso
                                registerProgress.setVisibility(View.INVISIBLE);

                            }
                        });
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Error : las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(RegisterActivity.this, "Error : campos vacios", Toast.LENGTH_LONG).show();
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

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);

    }

    //escondo el teclado
    public void hideSoftKeyboard(EditText myEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
