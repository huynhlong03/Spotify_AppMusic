package com.example.musicapp_android.Data;

public class HomeSongs {

    private String songCover;
    private String songName;
    private String songSinger;

    public HomeSongs(String songCover, String songName, String songSinger) {
        this.songCover = songCover;
        this.songName = songName;
        this.songSinger = songSinger;
    }

    public String getSongCover() {
        return songCover;
    }

    public void setSongCover(String songCover) {
        this.songCover = songCover;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongSinger() {
        return songSinger;
    }

    public void setSongSinger(String songSinger) {
        this.songSinger = songSinger;
    }
}
