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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.User;
import com.picapp.picapp.Models.UserRequest;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPasswordText;
    private Button loginBtn;
    private Button loginRegisterBtn;

    private ProgressBar loginProgress;

    private Retrofit retrofit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleBtn;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //instacia de firebase y firestore
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Inicializa la referencia de almacenage
        storageReference = FirebaseStorage.getInstance().getReference();

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
                Toast.makeText(LoginActivity.this, "Error : Falló la conexión", Toast.LENGTH_LONG).show();
            }
        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //levanto todos los botones y textos
        loginEmailText = (EditText) findViewById(R.id.loginEmailText);
        loginPasswordText = (EditText) findViewById(R.id.loginPasswordText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginRegisterBtn = (Button) findViewById(R.id.loginRegisterBtn);
        loginProgress = (ProgressBar) findViewById(R.id.loginProgress);

        hideSoftKeyboard(loginEmailText);
        hideSoftKeyboard(loginPasswordText);

        loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendToRegister();

            }
        });

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        //que se ve la barra de progreso
                        loginProgress.setVisibility(View.VISIBLE);

                        signIn();

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail = loginEmailText.getText().toString();
                final String loginPassword = loginPasswordText.getText().toString();

                //si el email y la pass son no vacios
                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)){

                    //que se ve la barra de progreso
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                //actualizar el server
                                serverUpdate(mAuth.getCurrentUser(), loginPassword);

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
            sendToFeed();

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
                            boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (newUser){
                                //no se puede crear una cuenta en el login
                                user.delete();
                                Toast.makeText(LoginActivity.this, "Error : No existe la cuenta.", Toast.LENGTH_LONG).show();
                                //escondo la barra de progreso
                                loginProgress.setVisibility(View.INVISIBLE);
                            }
                            else {
                                serverUpdate(user, "Google");
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.v("Status", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error : Authentication Failed.", Toast.LENGTH_LONG).show();

                            //escondo la barra de progreso
                            loginProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }


    private void serverUpdate(final FirebaseUser user, final String password) {

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

        Call<User> call = webApi.loginUser(userRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                User serverUser = response.body();

                storeFirestore(serverUser.getToken().getToken(), serverUser.getToken().getExpiresAt(), user.getUid());

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //se cierra sesion en firebase
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "SERVER Login ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();

                //escondo la barra de progreso
                loginProgress.setVisibility(View.INVISIBLE);
            }
        });



    }

    private void storeFirestore(final Integer token, Double expiresAt, String user_id) {

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token.toString());
        tokenMap.put("expiresAt", expiresAt.toString());

        //cada usuario tiene su propio documento
        firebaseFirestore.collection("UserTokens").document(user_id).set(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    // Store device id
                    HashMap<String, String> deviceData = new HashMap<>();
                    deviceData.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                    firebaseFirestore.collection("Devices").document(mAuth.getUid()).set(deviceData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Picapp picapp = Picapp.getInstance();
                            picapp.setToken(token.toString());
                            sendToFeed();
                        }
                    });

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "FIRESTORE Error: " + error, Toast.LENGTH_LONG).show();

                }

                //escondo la barra de progreso
                loginProgress.setVisibility(View.INVISIBLE);

                sendToFeed();

            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void sendToFeed() {

        Intent feedIntent = new Intent(LoginActivity.this, FeedActivity.class);
        startActivity(feedIntent);
        finish();

    }

    private void sendToRegister() {

        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }

    //escondo el teclado
    public void hideSoftKeyboard(EditText myEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
