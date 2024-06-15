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
import com.example.musicapp_android.Data.Album;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.HomeAlbum;
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

public class AlbumDetails extends AppCompatActivity {

    TextView tvName, tvArtist;
    SearchView searchView;
    CustomAdapterAllSong adapter;
    ArrayList<Artist> lsArtists = new ArrayList<>();
    Song song;
    String idArtist;
    ListView lvAllSong;
    ImageView imgAlbum;
    Album album;
    String id;

    ArrayList<Song> lstSong_Album = new ArrayList<>();
    ArrayList<Song> lstSong = new ArrayList<>();

    ArrayList<Song> searchName_Album = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        id = bundle.getString("idAlbum");
        String name = bundle.getString("nameAlbum");
        String image = bundle.getString("imageAlbum");
        String nameArtist = bundle.getString("nameArtist");

        addControl();
        Glide.with(getApplicationContext())
                .load(image)
                .transform(new RoundedCorners(25))
//                .error(R.drawable.song_error_image)
                .into(imgAlbum);
        tvName.setText("Album's name: " + name);
        tvArtist.setText("Artist's name: " + nameArtist);
        getDataFromFireBase_Song();
        getArtistDataFromFirebase();
        adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,lstSong_Album);
        lvAllSong.setAdapter(adapter);
        addEvent();
    }

    void addControl()
    {
        lvAllSong = (ListView) findViewById(R.id.lvAllSongs_AlbumDetail);
        searchView = (SearchView) findViewById(R.id.searchView_AlbumDetail);
        tvName = (TextView) findViewById(R.id.tvName_AlbumDetail);
        tvArtist = (TextView) findViewById(R.id.tvArtist_AlbumDetail);
        imgAlbum = (ImageView) findViewById(R.id.imageView3);
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
                    if(s.getIdAlbum().equals(id)) {
                        lstSong_Album.add(s);
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
                searchName_Album = new ArrayList<>();
                if (newText.length() >= 0)
                {
                    for (int i =0; i < lstSong_Album.size(); i++)
                    {
                        if (lstSong_Album.get(i).getName().toUpperCase().contains(newText.toUpperCase()))
                        {
                            Song a = new Song();
                            a.setName(lstSong_Album.get(i).getName());
                            a.setIdArtist(lstSong_Album.get(i).getIdArtist());
                            a.setImage(lstSong_Album.get(i).getImage());
                            searchName_Album.add(a);
                        }
                    }
//                    notifyDataSetChanged();
                    adapter = new CustomAdapterAllSong(getApplicationContext(),R.layout.layout_item_allsong,searchName_Album);
                    lvAllSong.setAdapter(adapter);
                }
                return false;
            }
        });

        lvAllSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Chuyển đến MainActivity khi click vào item
                Intent intent = new Intent(AlbumDetails.this, MainActivity.class);
                //intent.putExtra("song", lstAllSong.get(i)); // Chuyển ID của bài hát
                Bundle bundle = new Bundle();
                bundle.putLong("song_id", l); // Chuyển ID của bài hát
                bundle.putParcelableArrayList("song_list", (ArrayList<? extends Parcelable>) lstSong_Album); // lstSo là danh sách các bài hát của bạn
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