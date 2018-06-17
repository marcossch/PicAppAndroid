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
import com.picapp.picapp.MainActivity;
import com.picapp.picapp.OtherProfileActivity;
import com.picapp.picapp.R;
import com.picapp.picapp.RegisterActivity;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;
    private ArrayList<String> nameList;
    private ArrayList<String> picList;
    private ArrayList<String> idList;


    class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        Button name;

        public SearchViewHolder(View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.profile_name);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> nList, ArrayList<String> pList, ArrayList<String> idList){
        this.context = context;
        this.nameList = nList;
        this.picList = pList;
        this.idList = idList;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder holder, final int position) {
        holder.name.setText(nameList.get(position));
        Glide.with(context).load(picList.get(position)).into(holder.profileImg);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profIntent = new Intent(context, OtherProfileActivity.class);
                profIntent.putExtra("name", holder.name.getText());
                profIntent.putExtra("pic", picList.get(position));
                profIntent.putExtra("id", idList.get(position));
                context.startActivity(profIntent);
                //finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.nameList.size();
    }
}
