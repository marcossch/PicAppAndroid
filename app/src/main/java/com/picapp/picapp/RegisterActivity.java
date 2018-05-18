package com.picapp.picapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.User;
import com.picapp.picapp.Models.UserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmailText;
    private EditText registerPasswordText;
    private EditText registerConfirmPasswordText;
    private Button registerLoginBtn;

    private ProgressBar registerProgress;

    private Retrofit retrofit;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleBtn;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instacia de firebase
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //levanta el boton de inicio de google
        mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);

        // Build a mGoogleApiClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(RegisterActivity.this, "Error : Falló la conexión", Toast.LENGTH_LONG).show();
            }
        })
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();

        //levanto todos los botones y textos
        registerEmailText = (EditText) findViewById(R.id.registerEmailText);
        registerPasswordText = (EditText) findViewById(R.id.registerPasswordText);
        registerConfirmPasswordText = (EditText) findViewById(R.id.registerConfirmPasswordText);
        registerLoginBtn = (Button) findViewById(R.id.registerRegisterBtn);
        registerProgress = (ProgressBar) findViewById(R.id.registerProgress);

        hideSoftKeyboard(registerEmailText);
        hideSoftKeyboard(registerPasswordText);
        hideSoftKeyboard(registerConfirmPasswordText);


        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
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

                        signIn();

                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Error : las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                        //escondo la barra de progreso
                        registerProgress.setVisibility(View.INVISIBLE);
                    }

                }
                else{
                    Toast.makeText(RegisterActivity.this, "Error : campos vacios", Toast.LENGTH_LONG).show();
                    //escondo la barra de progreso
                    registerProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

        registerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToLogin();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("Error", "Google sign in failed", e);
                // ...
            }
        }
    }


    //--------------Metodos Privados-------------//

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("Satus", "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            firebaseUpdate(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.v("Status", "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Error : Authentication Failed.", Toast.LENGTH_LONG).show();

                            //escondo la barra de progreso
                            registerProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }


    private void firebaseUpdate(final FirebaseUser user) {

        //cargar el nombre de usuario y contraseña ya validados
        String registerEmail = registerEmailText.getText().toString();
        String registerPassword = registerPasswordText.getText().toString();

        //cargo nombre de usuario y pass en firebase
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(registerEmail)
                .build();

        user.updateProfile(profileUpdates);
        user.updatePassword(registerPassword);

        serverUpdate(user, registerPassword);

    }

    private void serverUpdate(final FirebaseUser user, String password){

        //mandar mensaje con los tokens correspondientes al server

        //creo retrofit que es la libreria para manejar Apis
        retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebApi webApi = retrofit.create(WebApi.class);

        //Creo la request para pasarle en el body
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(user.getUid());
        userRequest.setPassword(password);

        Call<UserRequest> call = webApi.postUser(userRequest);
        call.enqueue(new Callback<UserRequest>() {
            @Override
            public void onResponse(Call<UserRequest> call, Response<UserRequest> response) {

                //escondo la barra de progreso
                registerProgress.setVisibility(View.INVISIBLE);

                mAuth.signOut();
                sendToLogin();
            }

            @Override
            public void onFailure(Call<UserRequest> call, Throwable t) {
                //se elimina el usuario creado en firebase
                user.delete();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                //escondo la barra de progreso
                registerProgress.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    //escondo el teclado
    public void hideSoftKeyboard(EditText myEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
