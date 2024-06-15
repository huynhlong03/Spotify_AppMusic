package com.example.musicapp_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.R;

import java.util.List;

public class SongPlayListAdapter extends ArrayAdapter<Song> {

    private Context context;
    private int resource;
    private List<Song> songList;

    public SongPlayListAdapter(@NonNull Context context, int resource, @NonNull List<Song> songList) {
        super(context, resource, songList);
        this.context = context;
        this.resource = resource;
        this.songList = songList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Song song = songList.get(position);

        ImageView songImageView = convertView.findViewById(R.id.songImageView);
        TextView songNameTextView = convertView.findViewById(R.id.songNameTextView);

        songNameTextView.setText(song.getName());
        Glide.with(context).load(song.getImage()).into(songImageView);

        return convertView;
    }
}
