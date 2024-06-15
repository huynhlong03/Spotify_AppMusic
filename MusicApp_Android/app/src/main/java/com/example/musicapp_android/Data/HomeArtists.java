package com.example.musicapp_android.Data;

public class HomeArtists {

    private String image;
    private String name;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HomeArtists() {
    }

    public HomeArtists(String image, String name) {
        this.image = image;
        this.name = name;
    }
}
