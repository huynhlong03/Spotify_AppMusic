package com.example.musicapp_android.Data;

import com.example.musicapp_android.Model.User;

public class Playlist {
    private String id;
    private String name;
    private String idUser;
    private User user; // Add this field

    public Playlist() {
        // Default constructor required for calls to DataSnapshot.getValue(Playlist.class)
    }

    public Playlist(String id, String name, String idUser) {
        this.id = id;
        this.name = name;
        this.idUser = idUser;
    }

    // Getters and setters

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

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
