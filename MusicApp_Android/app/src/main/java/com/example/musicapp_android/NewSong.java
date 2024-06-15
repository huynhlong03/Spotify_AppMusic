package com.example.musicapp_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.musicapp_android.Adapter.CustomAdapterNewSong;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewSong extends AppCompatActivity {

    ListView lvNewSong;
    SearchView searchView;

    CustomAdapterNewSong adapter;

    ArrayList<Artist> lsArtists = new ArrayList<>();
    ArrayList<Song> lstNewSong = new ArrayList<>();
    ArrayList<Song> searchName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_song);
        addControl();
        getDataFromFirebase();
        getArtistDataFromFirebase();

        adapter = new CustomAdapterNewSong(getApplicationContext(), R.layout.layout_item_allsong,lstNewSong);
        lvNewSong.setAdapter(adapter);

        addEvent();
    }

    void addControl()
    {
        lvNewSong = (ListView) findViewById(R.id.lvNewSong);
        searchView = (SearchView) findViewById(R.id.searchViewNewSong);
        registerForContextMenu(lvNewSong);
    }

    public void getDataFromFirebase ()
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Song");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstNewSong.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    Song song = userSnapshot.getValue(Song.class);
                    lstNewSong.add(song);
                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getArtistDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistRef = database.getReference("Artist");

        artistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsArtists.clear();
                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    lsArtists.add(artist);
                }

                adapter.updateArtists(lsArtists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    void addEvent()
    {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchName = new ArrayList<>();
                if (newText.length() > 0)
                {
                    for (int i =0; i < lstNewSong.size(); i++)
                    {
                        if (lstNewSong.get(i).getName().toUpperCase().contains(newText.toUpperCase()))
                        {
                            Song a = new Song();
                            a.setName(lstNewSong.get(i).getName());
                            a.setIdArtist(lstNewSong.get(i).getIdArtist());
                            a.setImage(lstNewSong.get(i).getImage());
                            searchName.add(a);
                        }
                    }
//                    notifyDataSetChanged();
                    adapter = new CustomAdapterNewSong(getApplicationContext(), R.layout.layout_item_allsong,searchName);
                    lvNewSong.setAdapter(adapter);
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Song selectedSong = (Song) adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.context_option_1:
                // Handle option 1
                return true;
            case R.id.context_option_2:
                // Handle option 2
                return true;
            case R.id.context_option_hide:

                if (selectedSong != null) {
                    selectedSong.setHidden(true);
                    adapter.notifyDataSetChanged();
                }
                return true;
            case R.id.context_option_share:

                if (selectedSong != null) {
                    shareSong(selectedSong);
                }
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void shareSong(Song song) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody =  song.getLinkSong();
        intent.putExtra(Intent.EXTRA_SUBJECT, song.getName());
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Share Song"));
    }
}