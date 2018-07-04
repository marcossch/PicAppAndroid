package com.picapp.picapp.AndroidModels;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.picapp.picapp.R;

import java.util.ArrayList;

public class FlashesAdapter extends PagerAdapter {

    ArrayList<String> listFlases;
    ArrayList<String> listFlasesName;
    ArrayList<String> listFlasesDate;
    ArrayList<String> listFlasesLocation;

    public FlashesAdapter(ArrayList<String> listFlases, ArrayList<String> listFlasesName, ArrayList<String> listFlasesDate, ArrayList<String> listFlasesLocation, Context context) {
        this.listFlases = listFlases;
        this.listFlasesName = listFlasesName;
        this.listFlasesDate = listFlasesDate;
        this.listFlasesLocation = listFlasesLocation;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    Context context;
    LayoutInflater layoutInflater;

    @Override
    public int getCount() {
        return listFlases.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.card_item_flashes, container, false);
        ImageView image = view.findViewById(R.id.imageFlashView);
        VideoView video = view.findViewById(R.id.videoFlashView);
        TextView name = view.findViewById(R.id.username);
        TextView location = view.findViewById(R.id.location);
        TextView date = view.findViewById(R.id.date);

//        if(listFlases.get(position).contains("jpg")){
            Glide.with(context).load(Uri.parse(listFlases.get(position))).into(image);
//        } else {
//            image.setVisibility(View.GONE);
//            video.setVisibility(View.VISIBLE);
//            video.setVideoURI(Uri.parse(listFlases.get(position)));
//
//            MediaController mediaController = new MediaController(context);
//            mediaController.setAnchorView(video);
//            video.setMediaController(mediaController);
//
//            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.setLooping(true);
//                }
//            });
//            video.setMediaController((MediaController) null);
//            video.start();
//        }

        name.setText(listFlasesName.get(position));
        date.setText(listFlasesDate.get(position));

        String location_data = listFlasesLocation.get(position);
        String[] locationD = location_data.split(",");
        location.setText(locationD[0]);

        container.addView(view);
        return view;
    }
}
