package com.example.musicapp_android.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.musicapp_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class LibraryMainFragment extends Fragment {
    View view;
    BottomNavigationView librarynav_view;

    Fragment fragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.library_main, container, false);

        librarynav_view = view.findViewById(R.id.librarynav_view);
        librarynav_view.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.topartist:
                    fragment = new ArtistsFragment();
                    break;
                case R.id.playlist:
                    fragment = new PlaylistsFragment();
                    break;

            }
            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer2, fragment).commit();
            return true;
        });

        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer2, new PlaylistsFragment()).commit();

        return view;
    }
}