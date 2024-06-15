package com.example.musicapp_android.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp_android.Adapter.SearchTypeAdapter;
import com.example.musicapp_android.Adapter.TypeAdapter;
import com.example.musicapp_android.Data.HomeType;
import com.example.musicapp_android.Data.Type;
import com.example.musicapp_android.R;
import com.example.musicapp_android.Services.TypeDetials;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Arrays;


public class Search extends Fragment {
    View view;
    ConstraintLayout search_layout;
    SearchTypeAdapter searchTypeAdapter;
    RecyclerView TypesRecycleView;
    ArrayList<Type> typeArrayList = new ArrayList<>();
    Type type;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_fragment,container,false);
        search_layout = view.findViewById(R.id.search_layout);
        TypesRecycleView = view.findViewById(R.id.topTypeRecyclerView_Search);
        searchTypeAdapter = new SearchTypeAdapter(new ArrayList<>());
        TypesRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numberOfColumns);
        TypesRecycleView.setLayoutManager(layoutManager);
        TypesRecycleView.setAdapter(searchTypeAdapter);
        getDataFromFireBase_Type();
        addEvent();
        return view;

    }

    void addEvent() {
        searchTypeAdapter.setOnItemClickListener(new SearchTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Type type = typeArrayList.get(position);
                Intent intent = new Intent(getActivity(), TypeDetials.class);
                Bundle bundle = new Bundle();

                bundle.putString("idType", type.getId());
                bundle.putString("nameType", type.getName());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    void getDataFromFireBase_Type() {
        type = new Type();
        type.init(new Type.DataLoadListener() {
            @Override
            public void onDataLoaded(ArrayList<Type> types) {
                typeArrayList.clear();
                typeArrayList.addAll(types);

                // Convert the artistArrayList to HomeArtists
                HomeType[] homeTypes = new HomeType[typeArrayList.size()];
                for (int i = 0; i < typeArrayList.size(); i++) {
                    Type a = typeArrayList.get(i);
                    homeTypes[i] = new HomeType(a.getName());
                }

                // Update the adapter with the new data
                searchTypeAdapter.updateData(Arrays.asList(homeTypes));
            }

            @Override
            public void onDataLoadFailed(DatabaseError error) {
                // Handle errors here
                Log.e("Song's Data", "Data load failed: " + error.getMessage());
            }
        });
    }
}