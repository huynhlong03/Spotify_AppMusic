package com.example.musicapp_android.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongListAdapter extends BaseAdapter {
    private ArrayList<Song> fLagCountryArrayList;
    private LayoutInflater layoutInflater;
    private int layoutItem;

    public SongListAdapter(Activity context, int layoutItem, ArrayList<Song> fLagCountryArrayList) {
        this.layoutInflater = context.getLayoutInflater();
        this.fLagCountryArrayList = fLagCountryArrayList;
        this.layoutItem = layoutItem;
    }

    @Override
    public int getCount() {
        return fLagCountryArrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return fLagCountryArrayList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Song song = fLagCountryArrayList.get(i);
        View rowView = layoutInflater.inflate(layoutItem, null, true);
        ImageView imgSong = rowView.findViewById(R.id.img_song);
        Picasso.get().load(song.getImage()).into(imgSong);
        TextView name = rowView.findViewById(R.id.textViewName);
        name.setText(String.valueOf(song.getName()));
        return rowView;
    }
}
