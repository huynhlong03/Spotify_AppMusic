package com.example.musicapp_android.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicapp_android.Data.Playlist;
import com.example.musicapp_android.R;

import java.util.ArrayList;

public class PlaylistAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Playlist> playlistArrayList;

    public PlaylistAdapter(Context context, int layout, ArrayList<Playlist> playlistArrayList) {
        this.context = context;
        this.layout = layout;
        this.playlistArrayList = playlistArrayList;
    }

    @Override
    public int getCount() {
        return playlistArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);
        TextView nameplaylist = view.findViewById(R.id.playlist_name);
        //TextView nameuserplaylist=view.findViewById(R.id.playlist_user);
        TextView id = null;
        //ImageView imgplaylist = view.findViewById(R.id.imgplaylist);
        Playlist playlist = playlistArrayList.get(i);
        //id.setText(playlist.getId());
        //nameuserplaylist.setText(playlist.getIdUser());
//        if (playlist.getUser() != null) {
//            nameuserplaylist.setText(playlist.getUser().getUserName());
//        } else {
//            nameuserplaylist.setText("Unknown User");
//        }
        nameplaylist.setText(playlist.getName());

//        tvnamesinger.setText(music.getSinger());
//        imgmusic.setImageResource(music.getId());
//        imgplaylist.setImageResource(playlist);
        return view;
    }
}
