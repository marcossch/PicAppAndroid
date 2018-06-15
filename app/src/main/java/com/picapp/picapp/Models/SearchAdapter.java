package com.picapp.picapp.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.picapp.picapp.ProfileActivity;
import com.picapp.picapp.R;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;
    private ArrayList<String> nameList;
    private ArrayList<String> picList;


    class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView name;

        public SearchViewHolder(View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.profile_name);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> nList, ArrayList<String> pList ){
        this.context = context;
        this.nameList = nList;
        this.picList = pList;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        Glide.with(context).load(picList.get(position)).into(holder.profileImg);
    }


    @Override
    public int getItemCount() {
        return this.nameList.size();
    }
}
