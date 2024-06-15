package com.example.musicapp_android.Data;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Type {
    private String id;
    private String name;
    private String image;
    private ArrayList<Type> typeArrayList;

    public ArrayList<Type> getTypeArrayList() {
        return typeArrayList;
    }

    public void setTypeArrayList(ArrayList<Type> typeArrayList) {
        this.typeArrayList = typeArrayList;
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

    public Type() {
    }

    public Type(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public void init(final Type.DataLoadListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Type");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                typeArrayList = new ArrayList<>();
                for (DataSnapshot typeSnapshot : snapshot.getChildren()) {
                    Type type = typeSnapshot.getValue(Type.class);
                    if (type != null) {
                        typeArrayList.add(type);
                    }
                }
                // Notify listener about data changes
                if (listener != null) {
                    listener.onDataLoaded(typeArrayList);
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
        void onDataLoaded(ArrayList<Type> types);
        void onDataLoadFailed(DatabaseError error);
    }
}
