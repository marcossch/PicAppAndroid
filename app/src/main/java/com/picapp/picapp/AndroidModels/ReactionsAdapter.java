package com.picapp.picapp.AndroidModels;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.picapp.picapp.R;

import java.util.List;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsAdapter.ViewHolder> {

    public List<ReactionStory> reactions_list;
    public Context context;

    public ReactionsAdapter(List<ReactionStory> reactions_list) {

        this.reactions_list = reactions_list;

    }


    @NonNull
    @Override
    public ReactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reaction_item, parent, false);
        context = parent.getContext();
        return new ReactionsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final @NonNull ReactionsAdapter.ViewHolder holder, final int position) {

        String reaction = reactions_list.get(position).getReaction();
        String username = reactions_list.get(position).getUsername();

        holder.setTitleText(username);
        holder.setImageReaction(reaction);

    }

    @Override
    public int getItemCount() {
        return reactions_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView username;
        private ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            image = mView.findViewById(R.id.user_reaction_image);
            username = mView.findViewById(R.id.username);

        }

        public void setTitleText(String titleText) {
            username.setText(titleText);
        }

        public void setImageReaction(String imageReaction) {
            switch (imageReaction) {
                case "like":
                    image.setImageResource(R.mipmap.ic_like);
                    return;
                case "dislike":
                    image.setImageResource(R.mipmap.ic_dislike);
                    return;
                case "funny":
                    image.setImageResource(R.mipmap.ic_funny);
                    return;
                case "boring":
                    image.setImageResource(R.mipmap.ic_borring);
                    return;
            }
        }

    }

}