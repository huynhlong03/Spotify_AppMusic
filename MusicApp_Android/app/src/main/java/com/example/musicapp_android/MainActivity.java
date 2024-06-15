package com.example.musicapp_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.Fragment.AccountFragment;
import com.example.musicapp_android.Fragment.Home;
import com.example.musicapp_android.Fragment.LibraryMainFragment;
import com.example.musicapp_android.Fragment.MusicFragment;
import com.example.musicapp_android.Fragment.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new Home()).commit();
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        fragment = new Home();
                        break;
                    case R.id.search:
                        fragment = new Search();
                        break;
                    case R.id.library:
                        fragment = new LibraryMainFragment();
                        break;
                    case R.id.music:
                        fragment = new MusicFragment();
                        break;
                    case R.id.account:
                        fragment = new AccountFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
                return true;
            }

        });


        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        long songId = intent.getLongExtra("song_id", -1); // -1 là giá trị mặc định nếu không có dữ liệu được chuyển
        // Kiểm tra xem có dữ liệu được chuyển không
        if (songId != -1) {
            //navView.setSelectedItemId(R.id.music);
            //Song song = intent.getParcelableExtra("song");
            Bundle bundle = intent.getExtras();
            songId = bundle.getLong("song_id", -1); // -1 là giá trị mặc định nếu không có dữ liệu được chuyển
            ArrayList<Song> lstSong = bundle.getParcelableArrayList("song_list");
            // Load Fragment vào container
            Fragment fragment = MusicFragment.newInstance(songId, lstSong);
            loadFragment(fragment);
        }
    }
    // Phương thức để load Fragment vào container
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}