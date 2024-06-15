package com.example.musicapp_android.Services;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicapp_android.Adapter.CustomAdapterAllSong;
import com.example.musicapp_android.DangNhap;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.Song;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicapp_android.MainActivity;
import com.example.musicapp_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAllSong extends AppCompatActivity {

    Button btnCircle;
    SearchView searchView;
    CustomAdapterAllSong adapter;
    ListView lvAllSong;
    ArrayList<Artist> lsArtists = new ArrayList<>();
    ArrayList<Song> lstAllSong = new ArrayList<>();

    ArrayList<Song> searchName = new ArrayList<>();
    String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_song);
        SharedPreferences sharedPreferences2 = getSharedPreferences("MyAppPreferences2", Context.MODE_PRIVATE);
        String idUser = sharedPreferences2.getString("idUser", null); // null là giá trị mặc định nếu không tìm thấy
        currentUser = idUser; // Lưu idUser vào biến currentUser hoặc biến phù hợp

        addControl();
        getDataFromFirebase();
        getArtistDataFromFirebase();

        adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,lstAllSong);
        lvAllSong.setAdapter(adapter);
        addEvent();
    }


    public void getDataFromFirebase ()
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Song");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstAllSong.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    Song song = userSnapshot.getValue(Song.class);
                    lstAllSong.add(song);
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
    void addControl()
    {
        lvAllSong = (ListView) findViewById(R.id.lvAllSong);
        searchView = (SearchView) findViewById(R.id.searchView);
        registerForContextMenu(lvAllSong);

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
                    for (int i =0; i < lstAllSong.size(); i++)
                    {
                        if (lstAllSong.get(i).getName().toUpperCase().contains(newText.toUpperCase()))
                        {
                            Song a = new Song();
                            a.setName(lstAllSong.get(i).getName());
                            a.setIdArtist(lstAllSong.get(i).getIdArtist());
                            a.setImage(lstAllSong.get(i).getImage());
                            searchName.add(a);
                        }
                    }
//                    notifyDataSetChanged();
                    adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,searchName);
                    lvAllSong.setAdapter(adapter);
                }
                return false;
            }
        });


        lvAllSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Chuyển đến MainActivity khi click vào item
                Intent intent = new Intent(ActivityAllSong.this, MainActivity.class);
                //intent.putExtra("song", lstAllSong.get(i)); // Chuyển ID của bài hát
                Bundle bundle = new Bundle();
                bundle.putLong("song_id", l); // Chuyển ID của bài hát
                bundle.putParcelableArrayList("song_list", (ArrayList<? extends Parcelable>) lstAllSong); // lstSo là danh sách các bài hát của bạn
                intent.putExtras(bundle);
                startActivity(intent);
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
                addSongToFeatured(selectedSong);
                return true;
            case R.id.context_option_2:
                showPlaylistsDialog(selectedSong);
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
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAllSong.this); // Use the context of the activity
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
    private void addSongToFeatured(Song selectedSong) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference featuredRef = database.getReference("Featured");

        featuredRef.orderByChild("song/name").equalTo(selectedSong.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isDuplicate = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the song already exists in the "Featured" node
                    String userId = snapshot.child("idUser").getValue(String.class);
                    if (userId != null && userId.equals(currentUser)) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    // If not a duplicate, add the song to "Featured"
                    Map<String, Object> songData = new HashMap<>();
                    songData.put("song", selectedSong);
                    songData.put("idUser", currentUser);
                    featuredRef.push().setValue(songData);
                    Toast.makeText(getApplicationContext(), "Song added to featured", Toast.LENGTH_SHORT).show();
                } else {
                    // If duplicate, update the existing song data
                    Toast.makeText(getApplicationContext(), "Song already exists in featured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
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
    private void shareSong(Song song) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody =  song.getLinkSong();
        intent.putExtra(Intent.EXTRA_SUBJECT, song.getName());
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Share Song"));
    }
}