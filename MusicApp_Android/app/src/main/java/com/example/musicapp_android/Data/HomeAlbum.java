package com.example.musicapp_android.Data;

public class HomeAlbum {
    private String image;
    private String name;

    public String getNameArtist() {
        return nameArtist;
    }

    public void setNameArtist(String nameArtist) {
        this.nameArtist = nameArtist;
    }

    public HomeAlbum(String image, String name, String nameArtist) {
        this.image = image;
        this.name = name;
        this.nameArtist = nameArtist;
    }

    private String nameArtist;

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

    public HomeAlbum() {
    }

}
