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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.musicapp_android.Adapter.CustomAdapterAllSong;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.HomeSongs;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.MainActivity;
import com.example.musicapp_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ArtistDetails extends AppCompatActivity {

    TextView tvName, tvFans;
    Song song;
    SearchView searchView;
    ArrayList<Artist> lsArtists = new ArrayList<>();
    CustomAdapterAllSong adapter;
    ListView lvAllSong;
    ImageView imgArtist;
    String id;

    ArrayList<Song> lstSong_Artist = new ArrayList<>();
    ArrayList<Song> lstSong = new ArrayList<>();

    ArrayList<Song> searchName_Artist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        id = bundle.getString("idArtist");
        String name = bundle.getString("nameArtist");
        String image = bundle.getString("imageArtist");
        Integer fans = bundle.getInt("fans");

        addControl();
        Glide.with(getApplicationContext())
                .load(image)
                .transform(new RoundedCorners(25))
//                .error(R.drawable.song_error_image)
                .into(imgArtist);
        tvName.setText("Artist's name: " + name);
        tvFans.setText("Artist's fans: " + fans);
        getDataFromFireBase_Song();
        getArtistDataFromFirebase();
        adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,lstSong_Artist);
        lvAllSong.setAdapter(adapter);
        addEvent();
    }

    void addControl()
    {
        lvAllSong = (ListView) findViewById(R.id.lvAllSongs_ArtistDetail);
        searchView = (SearchView) findViewById(R.id.searchView_ArtistDetail);
        tvName = (TextView) findViewById(R.id.tvName_ArtistDetail);
        tvFans = (TextView) findViewById(R.id.tvFans_ArtistDetail);
        imgArtist = (ImageView) findViewById(R.id.imageView2);
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
                    if(s.getIdArtist().equals(id)) {
                        lstSong_Artist.add(s);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDataLoadFailed(DatabaseError error) {
                // Handle errors here
                Log.e("Song's Data", "Data load failed: " + error.getMessage());
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
                searchName_Artist = new ArrayList<>();
                if (newText.length() >= 0)
                {
                    for (int i =0; i < lstSong_Artist.size(); i++)
                    {
                        if (lstSong_Artist.get(i).getName().toUpperCase().contains(newText.toUpperCase()))
                        {
                            Song a = new Song();
                            a.setName(lstSong_Artist.get(i).getName());
                            a.setIdArtist(lstSong_Artist.get(i).getIdArtist());
                            a.setImage(lstSong_Artist.get(i).getImage());
                            searchName_Artist.add(a);
                        }
                    }
//                    notifyDataSetChanged();
                    adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,searchName_Artist);
                    lvAllSong.setAdapter(adapter);
                }
                return false;
            }
        });


        lvAllSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Chuyển đến MainActivity khi click vào item
                Intent intent = new Intent(ArtistDetails.this, MainActivity.class);
                //intent.putExtra("song", lstAllSong.get(i)); // Chuyển ID của bài hát
                Bundle bundle = new Bundle();
                bundle.putLong("song_id", l); // Chuyển ID của bài hát
                bundle.putParcelableArrayList("song_list", (ArrayList<? extends Parcelable>) lstSong_Artist); // lstSo là danh sách các bài hát của bạn
                intent.putExtras(bundle);
                startActivity(intent);
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