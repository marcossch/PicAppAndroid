package com.picapp.picapp.AndroidModels;

import com.picapp.picapp.R;

public class Images {
    private static Images instance;

    // Global variable
    public int[] drawable = {R.drawable.ic_arrow_back_black_24dp, R.drawable.ic_arrow_back_black_24dp,
            R.drawable.ic_arrow_back_black_24dp, R.drawable.ic_arrow_back_black_24dp,
            R.drawable.ic_arrow_back_black_24dp};

    // Restrict the constructor from being instantiated
    private Images(){}

//    public void setToken(String newToken) {
//        this.token = newToken;
//    }
    public int[] getImages() {
        return drawable;
    }

    public static synchronized Images getInstance(){
        if(instance==null){
            instance=new Images();
        }
        return instance;
    }
}
