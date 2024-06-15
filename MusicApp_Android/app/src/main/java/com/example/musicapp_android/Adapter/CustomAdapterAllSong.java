package com.example.musicapp_android.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomAdapterAllSong extends ArrayAdapter
{

    Context context;
    int layoutItem;
    ArrayList<Song> lsAllSong = new ArrayList<>();

    ArrayList<Artist> lsAllArtists = new ArrayList<>();
    private HashMap<String, String> artistMap;
    public CustomAdapterAllSong(@NonNull Context context, int layout, ArrayList<Song> lsAllSong) {
        super(context, layout, lsAllSong);
        this.context = context;
        this.layoutItem = layout;
        this.lsAllSong = lsAllSong;
        this.artistMap = new HashMap<>();
    }

    public void updateArtists(ArrayList<Artist> lsAllArtists) {
        this.lsAllArtists = lsAllArtists;
        artistMap.clear();
        for (Artist artist : lsAllArtists) {
            artistMap.put(artist.getId(), artist.getName());
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        int count = 0;
        for (Song song : lsAllSong) {
            if (!song.isHidden()) {
                count++;
            }
        }
        return count;
    }

    @Nullable
    @Override
    public Song getItem(int position) {
        int visiblePosition = 0;
        for (Song song : lsAllSong) {
            if (!song.isHidden()) {
                if (visiblePosition == position) {
                    return song;
                }
                visiblePosition++;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


//        Song song = lsAllSong.get(position);
        Song song = getItem(position);




        if (convertView == null)
        {
            convertView= LayoutInflater.from(context).inflate(layoutItem,null);
        }

        if (song == null || song.isHidden()) {
            position--;

            if (position < 0 || position >= getCount()) {
                convertView.setVisibility(View.GONE);
                return convertView;
            }
            // Lấy lại bài hát mới sau khi giảm vị trí
            song = getItem(position);
        }


        ImageView imgSong = (ImageView) convertView.findViewById(R.id.imgSong);
        Picasso.get().load(song.getImage()).resize(50,50).into(imgSong);

        TextView txtNameSong = (TextView) convertView.findViewById(R.id.txtNameSong);
        txtNameSong.setText(song.getName());

        TextView txtArtist = (TextView) convertView.findViewById(R.id.txtArtist);

        String artistName = artistMap.get(song.getIdArtist());
        txtArtist.setText(artistName != null ? artistName : "Unknown Artist");



        Button btnCircle = (Button) convertView.findViewById(R.id.btnCircle);
        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context parentContext = parent.getContext();
                if (parentContext instanceof Activity) {
                    Activity activity = (Activity) parentContext;
                    activity.openContextMenu(btnCircle);
                }
            }
        });
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }

        if (activity != null) {
            activity.registerForContextMenu(btnCircle);
        }
        return convertView;

    }


}
