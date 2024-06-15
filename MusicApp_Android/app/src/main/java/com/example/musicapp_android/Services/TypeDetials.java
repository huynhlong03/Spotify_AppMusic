package com.example.musicapp_android.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.musicapp_android.Adapter.CustomAdapterAllSong;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.MainActivity;
import com.example.musicapp_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TypeDetials extends AppCompatActivity {

    String id;
    TextView tvName;
    Song song;
    SearchView searchView;
    ArrayList<Artist> lsArtists = new ArrayList<>();
    CustomAdapterAllSong adapter;
    ListView lvAllSong;

    ArrayList<Song> lstSong_Type = new ArrayList<>();
    ArrayList<Song> lstSong = new ArrayList<>();

    ArrayList<Song> searchName_Type = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_detials);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        id = bundle.getString("idType");
        String name = bundle.getString("nameType");

        addControl();
        tvName.setText(name + "'s Song");


        getDataFromFireBase_Song();
        getArtistDataFromFirebase();
        adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,lstSong_Type);
        lvAllSong.setAdapter(adapter);
        addEvent();
    }

    void addControl() {
        tvName = (TextView) findViewById(R.id.textView_TypeDetail);
        lvAllSong = (ListView) findViewById(R.id.lvAllSongs_TypeDetail);
        searchView = (SearchView) findViewById(R.id.searchView_TypeDetail);
        registerForContextMenu(lvAllSong);
    }

    void getDataFromFireBase_Song() {
        song = new Song();
        song.init(new Song.DataLoadListener() {
            @Override
            public void onDataLoaded(ArrayList<Song> songs) {
                lstSong.clear();
                lstSong.addAll(songs);
                for(Song s : lstSong) {
                    if(s.getIdType().equals(id)) {
                        lstSong_Type.add(s);
                    }
                }
                adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,lstSong_Type);
                lvAllSong.setAdapter(adapter);
            }

            @Override
            public void onDataLoadFailed(DatabaseError error) {
                // Handle errors here
                Log.e("Song's Data", "Data load failed: " + error.getMessage());
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

    void addEvent() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchName_Type = new ArrayList<>();
                if (newText.length() >= 0)
                {
                    for (int i =0; i < lstSong_Type.size(); i++)
                    {
                        if (lstSong_Type.get(i).getName().toUpperCase().contains(newText.toUpperCase()))
                        {
                            Song a = new Song();
                            a.setName(lstSong_Type.get(i).getName());
                            a.setIdArtist(lstSong_Type.get(i).getIdArtist());
                            a.setImage(lstSong_Type.get(i).getImage());
                            searchName_Type.add(a);
                        }
                    }
//                    notifyDataSetChanged();
                    adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,searchName_Type);
                    lvAllSong.setAdapter(adapter);
                }
                return false;
            }
        });

        lvAllSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Chuyển đến MainActivity khi click vào item
                Intent intent = new Intent(TypeDetials.this, MainActivity.class);
                //intent.putExtra("song", lstAllSong.get(i)); // Chuyển ID của bài hát
                Bundle bundle = new Bundle();
                bundle.putLong("song_id", l); // Chuyển ID của bài hát
                bundle.putParcelableArrayList("song_list", (ArrayList<? extends Parcelable>) lstSong_Type); // lstSo là danh sách các bài hát của bạn
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