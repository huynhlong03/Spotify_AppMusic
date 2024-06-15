package com.example.musicapp_android.Data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Song implements Parcelable {
    private String id;
    private String name;
    private String image;
    private String idArtist;
    private String idTheme;
    private String idType;
    private String idAlbum;
    private String idPlaylist;
    private String linkSong;

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

    public String getIdArtist() {
        return idArtist;
    }

    public void setIdArtist(String idArtist) {
        this.idArtist = idArtist;
    }

    public String getIdTheme() {
        return idTheme;
    }

    public void setIdTheme(String idTheme) {
        this.idTheme = idTheme;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(String idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public String getLinkSong() {
        return linkSong;
    }

    public void setLinkSong(String linkSong) {
        this.linkSong = linkSong;
    }

    public Song() {

    }

    protected Song(Parcel in) {
        id = in.readString();
        name = in.readString();
        image = in.readString();
        idArtist = in.readString();
        idTheme = in.readString();
        idType = in.readString();
        idAlbum = in.readString();
        idPlaylist = in.readString();
        linkSong = in.readString();
    }

    public Song(String id, String idAlbum, String idArtist, String idPlaylist, String idTheme, String idType, String image, String linkSong, String name) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.idArtist = idArtist;
        this.idTheme = idTheme;
        this.idType = idType;
        this.idAlbum = idAlbum;
        this.idPlaylist = idPlaylist;
        this.linkSong = linkSong;
    }


    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(idArtist);
        parcel.writeString(idTheme);
        parcel.writeString(idType);
        parcel.writeString(idAlbum);
        parcel.writeString(idPlaylist);
        parcel.writeString(linkSong);
    }

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }

    private ArrayList<Song> songArrayList;

    private boolean hidden;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void init(final Song.DataLoadListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Song");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songArrayList = new ArrayList<>();
                for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                    Song song = songSnapshot.getValue(Song.class);
                    if (song != null) {
                        songArrayList.add(song);
                    }
                }
                // Notify listener about data changes
                if (listener != null) {
                    listener.onDataLoaded(songArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                if (listener != null) {
                    listener.onDataLoadFailed(error);
                }
            }
        });
    }

    // Interface to handle data load callback
    public interface DataLoadListener {
        void onDataLoaded(ArrayList<Song> songs);
        void onDataLoadFailed(DatabaseError error);
    }
}

