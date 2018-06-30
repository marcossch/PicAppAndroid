package com.picapp.picapp.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlashFeed {

    @SerializedName("flashes")
    @Expose
    private List<Flash> flashes = null;

    public List<Flash> getFlashes() {
        return flashes;
    }

    public void setFlashes(List<Flash> flashes) {
        this.flashes = flashes;
    }

}