package com.picapp.picapp.AndroidModels;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.picapp.picapp.AccountSettingsActivity;
import com.picapp.picapp.CommentsActivity;
import com.picapp.picapp.FeedActivity;
import com.picapp.picapp.FriendsActivity;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.LoginActivity;
import com.picapp.picapp.MapsActivity;
import com.picapp.picapp.Models.Comment;
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
    private String user_id_post;
    private boolean isProfile = false;
    private boolean isVideo;
    private boolean isFlashes = false;


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
        String profilePic = feed_list.get(position).getProfPic();
        String nameU = feed_list.get(position).getName();
        String title_data = feed_list.get(position).getTitle();
        String location_data = feed_list.get(position).getLocation();
        String imageUrl = feed_list.get(position).getImage();
        //levanto la fecha
        Long milliseconds = feed_list.get(position).getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateString = formatter.format(new Date(milliseconds));

        holder.setDescText(desc_data);

        String[] location = location_data.split(",");
        holder.setLocation(location[0]);
        holder.setTitleText(title_data);
        holder.setDate(dateString);
        holder.setProfileImage(profilePic);
        holder.setUsername(nameU);

        //me fijo si es video o imagen
        if(imageUrl.contains("jpg")){
            isVideo = false;
        } else {
            isVideo = true;
        }

        //seteo el URI
        if(isVideo){
            holder.setVideo(imageUrl);
        } else {
            holder.setImage(imageUrl);
        }

        //levanto el nombre de usuario
        user_id_post = feed_list.get(position).getUser_id();
        //creo retrofit que es la libreria para manejar Apis
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);

        //los usuarios del feed no tienen token asociado
        if (token == null) {
            //Obtengo el token del usuario.
            firebaseFirestore.collection("UserTokens").document(user_id_post).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //si existe este documento
                        if (task.getResult().exists()) {
                            //levanto el token
                            Object aux = task.getResult().get("token");
                            token = aux.toString();
                        } else {
                            Toast.makeText(context, "El usuario no posee un token asociado", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, "FIRESTORE Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

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

        if(!isFlashes) {

            //levanto las reacciones
            final Map<String, String> reactions = feed_list.get(position).getReactions();
            //levanto los comentarios
            final ArrayList<Comment> comments = feed_list.get(position).getComments();
            holder.setReactionCount(reactions.size());


            //-----------Reactions-----------

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

            holder.reacciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (reactions.size() > 0) {
                        Intent reaccionesIntent = new Intent(context, ReaccionesActivity.class);
                        reaccionesIntent.putExtra("token", token);

                        for (String key : reactions.keySet()) {

                            reaccionesIntent.putExtra(key, reactions.get(key));
                        }
                        context.startActivity(reaccionesIntent);
                    } else {
                        Toast.makeText(context, "No hay reacciones para este Story aún", Toast.LENGTH_LONG).show();
                    }


                }
            });

            holder.commentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent commentsIntent = new Intent(context, CommentsActivity.class);
                    commentsIntent.putExtra("image_id", feed_list.get(position).image_id);
                    ArrayList<String> commentsString = new ArrayList<String>();
                    for (Object comment : comments) {
                        commentsString.add(((Comment) comment).getCommentingUserId());
                        commentsString.add(((Comment) comment).getComment());
                        commentsString.add(String.valueOf(((Comment) comment).getTimestamp()));
                    }
                    commentsIntent.putStringArrayListExtra("comments", commentsString);
                    context.startActivity(commentsIntent);
                }
            });

        } else {

            //si es un flash, entonces escondo los botones
            holder.hideFlashesButtons();


        }

    }

    @Override
    public int getItemCount() {
        return feed_list.size();
    }

    public void setIsFlashes(boolean isFlashes) {
        this.isFlashes = isFlashes;
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
        private VideoView video;

        private Button deleteButton;

        private FloatingActionButton like;
        private FloatingActionButton funny;
        private FloatingActionButton boring;
        private FloatingActionButton dislike;
        private FloatingActionsMenu reactionsOptions;
        private TextView reactionsCount;
        private Button reacciones;

        private ImageButton commentsButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            reactionsCount = mView.findViewById(R.id.reactios_count);

            like = (FloatingActionButton) mView.findViewById(R.id.like);
            dislike = (FloatingActionButton) mView.findViewById(R.id.dislike);
            funny = (FloatingActionButton) mView.findViewById(R.id.funny);
            boring = (FloatingActionButton) mView.findViewById(R.id.boring);
            reactionsOptions = mView.findViewById(R.id.floatingActionsMenu);

            reacciones = (Button) mView.findViewById(R.id.reactions);
            commentsButton = (ImageButton) mView.findViewById(R.id.commentButton);

            deleteButton = mView.findViewById(R.id.deleteStory);
            deleteButton.setVisibility(View.GONE);

            image = mView.findViewById(R.id.story_image);
            video = mView.findViewById(R.id.story_video);
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
            image.setVisibility(View.VISIBLE);
            video.setVisibility(View.GONE);
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

        public void setVideo(String videoUri){
            image.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse(videoUri));
            video.start();
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
//            video.setMediaController((MediaController) null);
            video.start();

        }

        public void hideFlashesButtons(){
            reactionsCount.setVisibility(View.GONE);
            reacciones.setVisibility(View.GONE);
            commentsButton.setVisibility(View.GONE);
            reactionsOptions.setVisibility(View.GONE);
        }

    }

}
