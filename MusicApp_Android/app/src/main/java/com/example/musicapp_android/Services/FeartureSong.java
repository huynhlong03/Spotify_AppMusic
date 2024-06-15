package com.example.musicapp_android.Services;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicapp_android.Adapter.CustomAdapterAllSong;
import com.example.musicapp_android.Adapter.CustomAdapterFeatureSong;
import com.example.musicapp_android.DangNhap;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.MainActivity;
import com.example.musicapp_android.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeartureSong extends AppCompatActivity {

    ListView lvFeatureSong;

    SearchView searchView;
    ArrayList<Song> lsFeatureSong = new ArrayList<>();
    ArrayList<Artist> lsArtists = new ArrayList<>();

    ArrayList<String>lsDataLV = new ArrayList<>();

    ArrayList<Song> searchName = new ArrayList<>();

    CustomAdapterFeatureSong adapter;
    String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fearture_song);
        addControl();
        FirebaseApp.initializeApp(this);
        SharedPreferences sharedPreferences2 = getSharedPreferences("MyAppPreferences2", Context.MODE_PRIVATE);
        String idUser = sharedPreferences2.getString("idUser", null);
        currentUser = idUser;

        // initData();
        getDataFromFirebase();
        getArtistDataFromFirebase();
        adapter = new CustomAdapterFeatureSong(getApplicationContext(),R.layout.layout_item_allsong,lsFeatureSong);
        lvFeatureSong.setAdapter(adapter);

        addEvent();
    }


    void addControl()
    {
        lvFeatureSong = (ListView) findViewById(R.id.lvFeatureSong);
        searchView = (SearchView) findViewById(R.id.searchViewFeatureSong);
        registerForContextMenu(lvFeatureSong);
    }

    public void getDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Featured");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsFeatureSong.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.child("idUser").getValue(String.class).equals(currentUser)) {
                        Song song = userSnapshot.child("song").getValue(Song.class);
                        lsFeatureSong.add(song);
                    }
                }
                if (lsFeatureSong.isEmpty()) {
                    showMessage("Không có bài hát yêu thích nào");
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
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
                // Update the adapter with the fetched artist data
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
                    for (int i =0; i < lsFeatureSong.size(); i++)
                    {
                        if (lsFeatureSong.get(i).getName().toUpperCase().contains(newText.toUpperCase()))
                        {
                            Song a = new Song();
                            a.setName(lsFeatureSong.get(i).getName());
                            a.setIdArtist(lsFeatureSong.get(i).getIdArtist());
                            a.setImage(lsFeatureSong.get(i).getImage());
                            searchName.add(a);
                        }
                    }
//                    notifyDataSetChanged();
                    adapter = new CustomAdapterFeatureSong(getApplicationContext(),R.layout.layout_item_allsong,searchName);
                    lvFeatureSong.setAdapter(adapter);
                }
                return false;
            }
        });


        lvFeatureSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Chuyển đến MainActivity khi click vào item
                Intent intent = new Intent(FeartureSong.this, MainActivity.class);
                //intent.putExtra("song", lstAllSong.get(i)); // Chuyển ID của bài hát
                Bundle bundle = new Bundle();
                bundle.putLong("song_id", l); // Chuyển ID của bài hát
                bundle.putParcelableArrayList("song_list", (ArrayList<? extends Parcelable>) lsFeatureSong); // lstSo là danh sách các bài hát của bạn
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                showPlaylistsDialog(selectedSong);
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog(selectedSong);
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
    private void showDeleteConfirmationDialog(Song selectedSong) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Song")
                .setMessage("Bạn có chắc muốn xóa bài hát này ra khỏi danh sách yêu thích?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSongFromFeatured(selectedSong);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteSongFromFeatured(Song selectedSong) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Featured");

        myRef.orderByChild("idUser").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.child("song").child("name").getValue(String.class).equals(selectedSong.getName())) {
                        userSnapshot.getRef().removeValue();
                        lsFeatureSong.remove(selectedSong);
                        adapter.notifyDataSetChanged();
                        showMessage("Song removed from favorites");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
    private void showPlaylistsDialog(Song selectedSong) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference playlistsRef = database.getReference("Playlist");

        playlistsRef.orderByChild("idUser").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> playlistNames = new ArrayList<>();
                List<String> playlistIds = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String id = snapshot.child("id").getValue(String.class);
                    playlistNames.add(name);
                    playlistIds.add(id);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(FeartureSong.this); // Use the context of the activity
                builder.setTitle("Select Playlist");
                builder.setItems(playlistNames.toArray(new String[0]), (dialog, which) -> {
                    String selectedPlaylistId = playlistIds.get(which);
                    addSongToPlaylist(selectedSong, selectedPlaylistId);
                });
                builder.show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
    private void addSongToPlaylist(Song selectedSong, String playlistId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference playlistRef = database.getReference("Playlist").child(playlistId).child("Songs");

        // Add idPlaylist to the selected song
        selectedSong.setIdPlaylist(playlistId);

        // Check if the "Songs" node exists
        playlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> songData = new HashMap<>();
                songData.put(selectedSong.getName(), selectedSong);

                playlistRef.updateChildren(songData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Song added to playlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to add song", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}