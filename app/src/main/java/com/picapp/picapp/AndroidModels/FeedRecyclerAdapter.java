package com.picapp.picapp.AndroidModels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AccountSettingsActivity;
import com.picapp.picapp.FriendsActivity;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.LoginActivity;
import com.picapp.picapp.MapsActivity;
import com.picapp.picapp.Models.Error;
import com.picapp.picapp.Models.Reaction;
import com.picapp.picapp.Models.StoryDeleted;
import com.picapp.picapp.Models.UserAccount;
import com.picapp.picapp.Models.UserUpdate;
import com.picapp.picapp.ProfileActivity;
import com.picapp.picapp.R;
import com.picapp.picapp.ReaccionesActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder>{

    public List<FeedStory> feed_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    public String token = null;
    private FirebaseAuth mAuth;
    private String user_id;
    private boolean isProfile = false;


    public FeedRecyclerAdapter(List<FeedStory> feed_list){

        this.feed_list = feed_list;
        //instacia de firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

    }

    public void setToken(String token) {
        this.token = token;
    }

    public void isProfile() {
        isProfile = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, final int position) {

        String desc_data = feed_list.get(position).getDescription();
        String title_data = feed_list.get(position).getTitle();
        String location_data = feed_list.get(position).getLocation();
        String imageUrl = feed_list.get(position).getImage();
        //levanto la fecha
        Long milliseconds = feed_list.get(position).getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateString = formatter.format(new Date(milliseconds));
        final Map<String, String> reactions = feed_list.get(position).getReactions();

        holder.setDescText(desc_data);
        holder.setLocation(location_data);
        holder.setTitleText(title_data);
        holder.setImage(imageUrl);
        holder.setDate(dateString);
        holder.setReactionCount(reactions.size());


        //levanto el nombre de usuario
        final String user_id = feed_list.get(position).getUser_id();
        //creo retrofit que es la libreria para manejar Apis
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);

        if (token == null) {
            //Obtengo el token del usuario.
            firebaseFirestore.collection("UserTokens").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //si existe este documento
                        if (task.getResult().exists()) {
                            //levanto el token
                            Object aux = task.getResult().get("token");
                            String token = aux.toString();
                            Call<UserAccount> call = webApi.getUserAccount(user_id, token, "Application/json");
                            call.enqueue(new Callback<UserAccount>() {
                                @Override
                                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                                    holder.setUsername(response.body().getName());
                                }

                                @Override
                                public void onFailure(Call<UserAccount> call, Throwable t) {
                                    Log.d("UPDATE USER: ", "-----> No se pudo levantar el nombre de usuario <-----");
                                }
                            });
                        } else {
                            Toast.makeText(context, "El usuario no posee un token asociado", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, "FIRESTORE Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Call<UserAccount> call = webApi.getUserAccount(user_id, token, "Application/json");
            call.enqueue(new Callback<UserAccount>() {
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    holder.setUsername(response.body().getName());
                }

                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                    Log.d("UPDATE USER: ", "-----> No se pudo levantar el nombre de usuario <-----");
                }
            });
        }

        //cambiar cuando el feed se integre al server
        String profilePic = feed_list.get(position).getProfPic();

        if (profilePic == null) {

            //Obtengo la foto de perfil
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //si existe este documento
                        if (task.getResult().exists()) {

                            //levanto la imagen del usuario
                            final String profileImage = task.getResult().getString("image");
                            holder.setProfileImage(profileImage);

                        } else {
                            Toast.makeText(context, "El usuario no posee una foto de perfil", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, "FIRESTORE Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            holder.setProfileImage(profilePic);
        }

        //-----------likes-----------

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si el usuario ya reacciono, no puede volver a reaccionar
                if (!reactions.containsKey(user_id)) {

                    final Reaction reaction = new Reaction();
                    reaction.setReactingUserId(user_id);
                    reaction.setReaction("like");

                    Call<Reaction> call = webApi.postReaction(reaction, feed_list.get(position).getImage_id(),
                            token, "Application/json");
                    call.enqueue(new Callback<Reaction>() {
                        @Override
                        public void onResponse(Call<Reaction> call, Response<Reaction> response) {
                            reactions.put(user_id, reaction.getReaction());
                            holder.setReactionCount(reactions.size());
                        }

                        @Override
                        public void onFailure(Call<Reaction> call, Throwable t) {
                            Log.d("UPDATE USER: ", "-----> No se pudo cargar la reaccion <-----" + t.getMessage());
                        }
                    });

                }
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si el usuario ya reacciono, no puede volver a reaccionar
                if (!reactions.containsKey(user_id)) {

                    final Reaction reaction = new Reaction();
                    reaction.setReactingUserId(user_id);
                    reaction.setReaction("dislike");

                    Call<Reaction> call = webApi.postReaction(reaction, feed_list.get(position).getImage_id(),
                            token, "Application/json");
                    call.enqueue(new Callback<Reaction>() {
                        @Override
                        public void onResponse(Call<Reaction> call, Response<Reaction> response) {
                            reactions.put(user_id, reaction.getReaction());
                            holder.setReactionCount(reactions.size());
                        }

                        @Override
                        public void onFailure(Call<Reaction> call, Throwable t) {
                            Log.d("UPDATE USER: ", "-----> No se pudo cargar la reaccion <-----" + t.getMessage());
                        }
                    });

                }
            }
        });

        holder.funny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si el usuario ya reacciono, no puede volver a reaccionar
                if (!reactions.containsKey(user_id)) {

                    final Reaction reaction = new Reaction();
                    reaction.setReactingUserId(user_id);
                    reaction.setReaction("funny");

                    Call<Reaction> call = webApi.postReaction(reaction, feed_list.get(position).getImage_id(),
                            token, "Application/json");
                    call.enqueue(new Callback<Reaction>() {
                        @Override
                        public void onResponse(Call<Reaction> call, Response<Reaction> response) {
                            reactions.put(user_id, reaction.getReaction());
                            holder.setReactionCount(reactions.size());
                        }

                        @Override
                        public void onFailure(Call<Reaction> call, Throwable t) {
                            Log.d("UPDATE USER: ", "-----> No se pudo cargar la reaccion <-----" + t.getMessage());
                        }
                    });

                }
            }
        });

        holder.boring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si el usuario ya reacciono, no puede volver a reaccionar
                if (!reactions.containsKey(user_id)) {

                    final Reaction reaction = new Reaction();
                    reaction.setReactingUserId(user_id);
                    reaction.setReaction("boring");

                    Call<Reaction> call = webApi.postReaction(reaction, feed_list.get(position).getImage_id(),
                            token, "Application/json");
                    call.enqueue(new Callback<Reaction>() {
                        @Override
                        public void onResponse(Call<Reaction> call, Response<Reaction> response) {
                            reactions.put(user_id, reaction.getReaction());
                            holder.setReactionCount(reactions.size());
                        }

                        @Override
                        public void onFailure(Call<Reaction> call, Throwable t) {
                            Log.d("UPDATE USER: ", "-----> No se pudo cargar la reaccion <-----" + t.getMessage());
                        }
                    });

                }
            }
        });

        //elimiar imagenes, solo si esta en el perfil propio
        if (isProfile) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Call<StoryDeleted> call = webApi.deleteStory(feed_list.get(position).getImage_id(), token, "Application/json");
                    call.enqueue(new Callback<StoryDeleted>() {
                        @Override
                        public void onResponse(Call<StoryDeleted> call, Response<StoryDeleted> response) {
                            feed_list.remove(position);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<StoryDeleted> call, Throwable t) {

                            Toast.makeText(context, "Server ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }

        holder.reacciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent friendsIntent = new Intent(context, ReaccionesActivity.class);

                for (String key : reactions.keySet()) {

                    friendsIntent.putExtra(key, reactions.get(key));
                }
                context.startActivity(friendsIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return feed_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView descView;
        private TextView titleView;
        private TextView locationView;
        private ImageView image;
        private ImageView pImage;
        private AppCompatTextView nameText;
        private TextView story_date;

        private Button deleteButton;

        private FloatingActionButton like;
        private FloatingActionButton funny;
        private FloatingActionButton boring;
        private FloatingActionButton dislike;
        private TextView reactionsCount;
        private Button reacciones;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            reactionsCount = mView.findViewById(R.id.reactios_count);

            like = (FloatingActionButton) mView.findViewById(R.id.like);
            dislike = (FloatingActionButton) mView.findViewById(R.id.dislike);
            funny = (FloatingActionButton) mView.findViewById(R.id.funny);
            boring = (FloatingActionButton) mView.findViewById(R.id.boring);

            reacciones = (Button) mView.findViewById(R.id.reactions);

            deleteButton = mView.findViewById(R.id.deleteStory);
            deleteButton.setVisibility(View.INVISIBLE);

        }

        public void setTitleText(String titleText){
            titleView = mView.findViewById(R.id.title);
            titleView.setText(titleText);
        }

        public void setDescText(String descText){
            descView = mView.findViewById(R.id.description);
            descView.setText(descText);
        }

        public void setLocation(String location) {
            locationView = mView.findViewById(R.id.location);
            locationView.setText(location);
        }

        public void setImage(String imageUri){
            image = mView.findViewById(R.id.story_image);
            Glide.with(context).load(imageUri).into(image);
        }

        public void setProfileImage(String imageUri){
            pImage = mView.findViewById(R.id.user_profile_image);
            Glide.with(context).load(imageUri).into(pImage);
        }

        public void setUsername(String name) {
            nameText = mView.findViewById(R.id.username);
            nameText.setText(name);
        }

        public void setDate(String date){
            story_date = mView.findViewById(R.id.date);
            story_date.setText(date);
        }

        public void setReactionCount(int reactionCount) {
            reactionsCount = mView.findViewById(R.id.reactios_count);
            reactionsCount.setText(String.valueOf(reactionCount));
        }
    }

}
