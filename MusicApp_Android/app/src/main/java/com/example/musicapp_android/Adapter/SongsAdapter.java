package com.example.musicapp_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.musicapp_android.Data.HomeArtists;
import com.example.musicapp_android.Data.HomeSongs;
import com.example.musicapp_android.R;

import java.util.Arrays;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {

    private final List<HomeSongs> songList;

    private Context context;

    public SongsAdapter(List<HomeSongs> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_item, parent, false);

        context = parent.getContext();

        return new SongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        Glide.with(context)
                .load(songList.get(position).getSongCover())
                .transform(new RoundedCorners(25))
                .error(R.drawable.song_error_image)
                .into(holder.coverImage);
        holder.name.setText(songList.get(position).getSongName());
        holder.artist.setText(songList.get(position).getSongSinger());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void updateData(List<HomeSongs> newSongList) {
        this.songList.clear();
        for(int i = 0; i < 3; i++) {
            this.songList.add(newSongList.get(i));
        }
        notifyDataSetChanged();
    }


    static class SongsViewHolder extends RecyclerView.ViewHolder {

        public ImageView coverImage;
        public TextView name;
        public TextView artist;

        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);

            coverImage = itemView.findViewById(R.id.artistImageView);
            name = itemView.findViewById(R.id.songNameTextView);
            artist = itemView.findViewById(R.id.artistNameTextView);
        }
    }
}
