package com.example.musicapp_android.Data;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Album {
    private String id;
    private String idArtist;

    public String getIdArtist() {
        return idArtist;
    }

    public void setIdArtist(String idArtist) {
        this.idArtist = idArtist;
    }

    public ArrayList<Album> getAlbumArrayList() {
        return albumArrayList;
    }

    public void setAlbumArrayList(ArrayList<Album> albumArrayList) {
        this.albumArrayList = albumArrayList;
    }

    private ArrayList<Album> albumArrayList;

    public Album() {
    }

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

    private String name;

    public Album(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    private String image;

    public void init(final Album.DataLoadListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Album");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albumArrayList = new ArrayList<>();
                for (DataSnapshot albumSnapshot : snapshot.getChildren()) {
                    Album album = albumSnapshot.getValue(Album.class);
                    if (album != null) {
                        albumArrayList.add(album);
                    }
                }
                // Notify listener about data changes
                if (listener != null) {
                    listener.onDataLoaded(albumArrayList);
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
        void onDataLoaded(ArrayList<Album> artists);
        void onDataLoadFailed(DatabaseError error);
    }


}
