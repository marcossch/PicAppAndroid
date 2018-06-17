package com.picapp.picapp.AndroidModels;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.picapp.picapp.CommentsActivity;
import com.picapp.picapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<CommentStory> comments_list;
    public Context context;

    public CommentsRecyclerAdapter(List<CommentStory> comments_list) {

        this.comments_list = comments_list;

    }


    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment_item, parent, false);
        context = parent.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final @NonNull CommentsRecyclerAdapter.ViewHolder holder, final int position) {

        String commentText = comments_list.get(position).getComment();
        String name = comments_list.get(position).getUsername();
        Uri image = comments_list.get(position).getImage();

        //levanto la fecha
        Long milliseconds = Long.valueOf(comments_list.get(position).getTimestamp());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateString = formatter.format(new Date(milliseconds));

        holder.setTitleText(name);
        holder.setImage(image);
        holder.setCommentText(commentText);
        holder.setTimestamp(dateString);

    }

    @Override
    public int getItemCount() {
        return comments_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView username;
        private ImageView image;
        private TextView timestamp;
        private TextView comment;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            image = mView.findViewById(R.id.user_comment_image);
            username = mView.findViewById(R.id.username);
            timestamp = mView.findViewById(R.id.timestamp);
            comment = mView.findViewById(R.id.commentText);

        }

        public void setTitleText(String titleText) {
            username.setText(titleText);
        }

        public void setImage(Uri imageP) {
            Glide.with(context).load(imageP).into(image);
        }

        public void setCommentText(String commentText) {
            this.comment.setText(commentText);
        }

        public void setTimestamp(String timestamp) {
            this.timestamp.setText(timestamp);
        }
    }

}