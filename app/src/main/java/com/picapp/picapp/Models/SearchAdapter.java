package com.picapp.picapp.Models;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.FeedActivity;
import com.picapp.picapp.FriendProfileActivity;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.MainActivity;
import com.picapp.picapp.OtherProfileActivity;
import com.picapp.picapp.ProfileActivity;
import com.picapp.picapp.R;
import com.picapp.picapp.RegisterActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;
    private ArrayList<String> nameList;
    private ArrayList<String> picList;
    private ArrayList<String> idList;
    private Intent profIntent;
    private FirebaseUser user;
    private String from;


    class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        Button name;

        public SearchViewHolder(View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.profile_name);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> nList, ArrayList<String> pList, ArrayList<String> idList, String from){
        this.context = context;
        this.nameList = nList;
        this.picList = pList;
        this.idList = idList;
        this.from = from;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder holder, final int position) {

        //Inicializo
        final Picapp picapp = Picapp.getInstance();
        final String token = picapp.getToken();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final WebApi webApi = retrofit.create(WebApi.class);

        holder.name.setText(nameList.get(position));
        Glide.with(context).load(picList.get(position)).into(holder.profileImg);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<FriendshipStatus> friendshipStatus = webApi.getFriendshipStatus((String) idList.get(position), token, "Application/json");
                friendshipStatus.enqueue(new Callback<FriendshipStatus>() {
                    @Override
                    public void onResponse(Call<FriendshipStatus> call, Response<FriendshipStatus> response) {
                        Log.d("FRIENDSHIP STATUS", response.body().getState());
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        if(idList.get(position).contains(user.getUid())){
                            Toast.makeText(context, "Macri Gato", Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (response.body().getState().equals("friends")) {
                                profIntent = new Intent(context, FriendProfileActivity.class);
                                Log.d("FRIENDSHIP STATUS", "-----------Vamos a profile friend------------");
                            } else {
                                profIntent = new Intent(context, OtherProfileActivity.class);
                                Log.d("FRIENDSHIP STATUS", "-----------Vamos a other friend------------");
                            }
                            sendTo(holder, position);
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendshipStatus> call, Throwable t) {
                        Log.d("FRIENDSHIP STATUS", "-----> No se pudo obtener el estado de amistad <-----");
                    }
                });

            }
        });
    }

    private void sendTo(SearchViewHolder holder, int position) {
        profIntent.putExtra("name", holder.name.getText());
        profIntent.putExtra("from", from);
        profIntent.putExtra("pic", picList.get(position));
        profIntent.putExtra("id", idList.get(position));
        context.startActivity(profIntent);
    }

    @Override
    public int getItemCount() {
        return this.nameList.size();
    }
}
