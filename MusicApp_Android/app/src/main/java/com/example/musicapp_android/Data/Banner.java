package com.example.musicapp_android.Data;

public class Banner {
    private String id;
    private String name;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdSong() {
        return idSong;
    }

    public void setIdSong(String idSong) {
        this.idSong = idSong;
    }

    public Banner() {
    }

    public Banner(String id, String name, String image, String idSong) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.idSong = idSong;
    }

    private String idSong;
}
