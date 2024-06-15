package com.example.musicapp_android.Data;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Artist {
    private String id;
    private String name;
    private String image;
    private Integer fans;
    private ArrayList<Artist> artistArrayList;

    // Default constructor required for calls to DataSnapshot.getValue(Artist.class)
    public Artist() {
    }

    // Constructor
    public Artist(String id, String name, String image, Integer fans) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.fans = fans;
    }

    // Getters and Setters
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

    public Integer getFans() {
        return fans;
    }

    public void setFans(Integer fans) {
        this.fans = fans;
    }

    public ArrayList<Artist> getArtistArrayList() {
        return artistArrayList;
    }

    public void setArtistArrayList(ArrayList<Artist> artistArrayList) {
        this.artistArrayList = artistArrayList;
    }

    public String findArtist(String id) {
        for(Artist a : artistArrayList) {
            if(a.getId().equals(id))
                return a.getName();
        }
        return null;
    }


//    public void init() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Artist");
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                artistArrayList = new ArrayList<>();
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    Artist artist = userSnapshot.getValue(Artist.class);
//                    if (artist != null) {
//                        artistArrayList.add(artist);
//                    }
//                }
//                // Notify adapter or UI about data changes if needed
//                // For example, if you are using an adapter, call adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle possible errors
//            }
//        });
//    }

    public void init(final DataLoadListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Artist");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                artistArrayList = new ArrayList<>();
                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    if (artist != null) {
                        artistArrayList.add(artist);
                    }
                }
                // Notify listener about data changes
                if (listener != null) {
                    listener.onDataLoaded(artistArrayList);
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
        void onDataLoaded(ArrayList<Artist> artists);
        void onDataLoadFailed(DatabaseError error);
    }
}
