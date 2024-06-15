package com.example.musicapp_android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp_android.Data.HomeArtists;
import com.example.musicapp_android.R;

import java.util.ArrayList;
import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder> {

    private final List<HomeArtists> artistList;
    private OnItemClickListener listener;
    private ViewGroup context;

    public ArtistsAdapter(List<HomeArtists> artistList) {
        this.artistList = artistList;
    }



    @NonNull
    @Override
    public ArtistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (parent.getTag().toString().equals("topArtists")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artists_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_artists_item, parent, false);
        }

        context = parent;

        return new ArtistsViewHolder(view, listener);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsViewHolder holder, int position) {
        HomeArtists artist = artistList.get(position);
        Glide.with(context.getContext())
                .load(artist.getImage())
                .error(R.drawable.song_error_image)
                .circleCrop()
                .into(holder.artistImage);
                //.into(holder.artistImage);
        holder.artistName.setText(artistList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public void updateData(List<HomeArtists> newArtistList) {
        this.artistList.clear();
        for(int i = 0; i < 3 ; i++) {
            this.artistList.add(newArtistList.get(i));
        }
        notifyDataSetChanged();
    }


    class ArtistsViewHolder extends RecyclerView.ViewHolder {

        ImageView artistImage;
        TextView artistName;

        public ArtistsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            if (context.getTag().equals("topArtists")) {
                artistImage = itemView.findViewById(R.id.artistImageView);
                artistName = itemView.findViewById(R.id.nameTextView);
            } else {
                artistImage = itemView.findViewById(R.id.favArtistImageView);
                artistName = itemView.findViewById(R.id.favArtistTextView);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
