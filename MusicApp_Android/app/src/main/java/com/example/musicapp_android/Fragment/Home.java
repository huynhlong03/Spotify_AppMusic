package com.example.musicapp_android.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.musicapp_android.Adapter.AlbumAdapter;
import com.example.musicapp_android.Adapter.ArtistsAdapter;
import com.example.musicapp_android.Adapter.SongsAdapter;
import com.example.musicapp_android.Adapter.TypeAdapter;
import com.example.musicapp_android.Data.Album;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.HomeAlbum;
import com.example.musicapp_android.Data.HomeArtists;
import com.example.musicapp_android.Data.HomeSongs;
import com.example.musicapp_android.Data.HomeType;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.Data.Type;
import com.example.musicapp_android.R;
import com.example.musicapp_android.Services.ActivityAllSong;
import com.example.musicapp_android.Services.AlbumDetails;
import com.example.musicapp_android.Services.ArtistDetails;
import com.example.musicapp_android.Services.FeartureSong;
import com.example.musicapp_android.Services.TypeDetials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Home extends Fragment {

    Button btnAllSong, btnFeatureSong, btnNewSong;
    ArrayList<Artist> artistArrayList = new ArrayList<>();
    ArrayList<Album> albumArrayList = new ArrayList<>();
    Artist artist;
    Album album;
    Song song;
    //Type type;

    ArrayList<Song> songArrayList = new ArrayList<>();
    //ArrayList<Type> typeArrayList = new ArrayList<>();

    RecyclerView topAlbumsRecycleView;
    RecyclerView topSingersRecycleView;
    //RecyclerView topTypesRecycleView;
    ArtistsAdapter artistsAdapter;
    SongsAdapter songsAdapter;
    AlbumAdapter albumAdapter;
    //TypeAdapter typeAdapter;
    //Button btnHomeArtist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseApp.initializeApp(getContext());
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        addControls(view);

        artistsAdapter = new ArtistsAdapter(new ArrayList<>());
        songsAdapter = new SongsAdapter(new ArrayList<>());
        albumAdapter = new AlbumAdapter(new ArrayList<>());
        //typeAdapter = new TypeAdapter(new ArrayList<>());

        topAlbumsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        topSingersRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        //topTypesRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

//        int numberOfColumns = 2;
//        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numberOfColumns);
//        topTypesRecycleView.setLayoutManager(layoutManager);
        topSingersRecycleView.setAdapter(artistsAdapter);
        topAlbumsRecycleView.setAdapter(albumAdapter);
        //topTypesRecycleView.setAdapter(typeAdapter);

        getDataFromFireBase_Artist();
        getDataFromFireBase_Song();
        //getDataFromFireBase_Type();
        getDataFromFireBase_Album();

        addEvent();

        return view;
    }

    void addControls(View view) {
        topAlbumsRecycleView = view.findViewById(R.id.topAlbumRecycleView);
        topSingersRecycleView = view.findViewById(R.id.topArtistsRecycleView);
        //topTypesRecycleView = view.findViewById(R.id.topTypeRecyclerView);
        //btnHomeArtist = view.findViewById(R.id.btnHomeArtist);
        btnAllSong = (Button) view.findViewById(R.id.btnAllSong);
        btnFeatureSong = (Button) view.findViewById(R.id.btnFeatureSong);
        btnNewSong = (Button) view.findViewById(R.id.btnNewSong);
    }

    void addEvent() {
        artistsAdapter.setOnItemClickListener(new ArtistsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Artist artist = artistArrayList.get(position);
                Intent intent = new Intent(getActivity(), ArtistDetails.class);
                Bundle bundle = new Bundle();

                bundle.putString("idArtist", artist.getId());
                bundle.putString("nameArtist", artist.getName());
                bundle.putString("imageArtist", artist.getImage());
                bundle.putInt("fans", artist.getFans());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        albumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Album album = albumArrayList.get(position);
                Intent intent = new Intent(getActivity(), AlbumDetails.class);
                Bundle bundle = new Bundle();
                String nameArtist = artist.findArtist(album.getIdArtist());
                bundle.putString("idAlbum", album.getId());
                bundle.putString("nameAlbum", album.getName());
                bundle.putString("imageAlbum", album.getImage());
                bundle.putString("nameArtist", nameArtist);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        typeAdapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Type type = typeArrayList.get(position);
//                Intent intent = new Intent(getActivity(), TypeDetials.class);
//                Bundle bundle = new Bundle();
//
//                bundle.putString("idType", type.getId());
//                bundle.putString("nameType", type.getName());
//
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

        btnAllSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityAllSong.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Mở activity mới thành công", Toast.LENGTH_SHORT).show();
            }
        });
        btnFeatureSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FeartureSong.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Mở activity mới thành công", Toast.LENGTH_SHORT).show();
            }
        });

        btnNewSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FeartureSong.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Mở activity mới thành công", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void getDataFromFireBase_Artist() {
        artist = new Artist();
        artist.init(new Artist.DataLoadListener() {
            @Override
            public void onDataLoaded(ArrayList<Artist> artists) {
                artistArrayList.clear();
                artistArrayList.addAll(artists);

                // Convert the artistArrayList to HomeArtists
                HomeArtists[] singers = new HomeArtists[artistArrayList.size()];
                for (int i = 0; i < artistArrayList.size(); i++) {
                    Artist a = artistArrayList.get(i);
                    singers[i] = new HomeArtists(a.getImage(), a.getName());
                }

                // Update the adapter with the new data
                artistsAdapter.updateData(Arrays.asList(singers));
            }

            @Override
            public void onDataLoadFailed(DatabaseError error) {
                // Handle errors here
                Log.e("Artist's Data", "Data load failed: " + error.getMessage());
            }
        });
    }

    void getDataFromFireBase_Song() {
        song = new Song();
        song.init(new Song.DataLoadListener() {
            @Override
            public void onDataLoaded(ArrayList<Song> songs) {
                songArrayList.clear();
                songArrayList.addAll(songs);

                // Convert the artistArrayList to HomeArtists
                HomeSongs[] homeSongs = new HomeSongs[songArrayList.size()];
                for (int i = 0; i < songArrayList.size(); i++) {
                    Song a = songArrayList.get(i);
                    homeSongs[i] = new HomeSongs(a.getImage(), a.getName(), artist.findArtist(a.getIdArtist()));
                }

                // Update the adapter with the new data
                songsAdapter.updateData(Arrays.asList(homeSongs));
            }

            @Override
            public void onDataLoadFailed(DatabaseError error) {
                // Handle errors here
                Log.e("Song's Data", "Data load failed: " + error.getMessage());
            }
        });
    }

    void getDataFromFireBase_Album() {
        album = new Album();
        album.init(new Album.DataLoadListener() {
            @Override
            public void onDataLoaded(ArrayList<Album> albums) {
                albumArrayList.clear();
                albumArrayList.addAll(albums);

                // Convert the artistArrayList to HomeArtists
                HomeAlbum[] album = new HomeAlbum[albumArrayList.size()];
                for (int i = 0; i < albumArrayList.size(); i++) {
                    Album a = albumArrayList.get(i);
                    String artistName = artist.findArtist(a.getIdArtist());
                    album[i] = new HomeAlbum(a.getImage(), a.getName(), artistName);
                }

                // Update the adapter with the new data
                albumAdapter.updateData(Arrays.asList(album));
            }

            @Override
            public void onDataLoadFailed(DatabaseError error) {
                // Handle errors here
                Log.e("Artist's Data", "Data load failed: " + error.getMessage());
            }
        });
    }

//    void getDataFromFireBase_Type() {
//        type = new Type();
//        type.init(new Type.DataLoadListener() {
//            @Override
//            public void onDataLoaded(ArrayList<Type> types) {
//                typeArrayList.clear();
//                typeArrayList.addAll(types);
//
//                // Convert the artistArrayList to HomeArtists
//                HomeType[] homeTypes = new HomeType[typeArrayList.size()];
//                for (int i = 0; i < typeArrayList.size(); i++) {
//                    Type a = typeArrayList.get(i);
//                    homeTypes[i] = new HomeType(a.getName());
//                }
//
//                // Update the adapter with the new data
//                typeAdapter.updateData(Arrays.asList(homeTypes));
//            }
//
//            @Override
//            public void onDataLoadFailed(DatabaseError error) {
//                // Handle errors here
//                Log.e("Song's Data", "Data load failed: " + error.getMessage());
//            }
//        });
//    }
}