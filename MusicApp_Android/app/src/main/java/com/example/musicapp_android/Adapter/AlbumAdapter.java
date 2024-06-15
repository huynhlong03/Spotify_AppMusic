package com.example.musicapp_android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp_android.Data.HomeAlbum;
import com.example.musicapp_android.Data.HomeArtists;
import com.example.musicapp_android.R;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumsViewHolder>{
    private final List<HomeAlbum> albumList;
    private AlbumAdapter.OnItemClickListener listener;
    private ViewGroup context;

    public AlbumAdapter(List<HomeAlbum> albumList) {
        this.albumList = albumList;
    }



    @NonNull
    @Override
    public AlbumAdapter.AlbumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (parent.getTag().toString().equals("topAlbums")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_album_item, parent, false);
        }

        context = parent;

        return new AlbumAdapter.AlbumsViewHolder(view, listener);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AlbumAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.AlbumsViewHolder holder, int position) {
        HomeAlbum album = albumList.get(position);
        Glide.with(context.getContext())
                .load(album.getImage())
                .error(R.drawable.song_error_image)
                .circleCrop()
                .into(holder.albumImage);
        holder.albumName.setText(albumList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void updateData(List<HomeAlbum> newAlbumList) {
        this.albumList.clear();
        for(int i = 0; i < 3 ; i++) {
            this.albumList.add(newAlbumList.get(i));
        }
        notifyDataSetChanged();
    }


    class AlbumsViewHolder extends RecyclerView.ViewHolder {

        ImageView albumImage;
        TextView albumName;
        TextView nameArtist;

        public AlbumsViewHolder(@NonNull View itemView, final AlbumAdapter.OnItemClickListener listener) {
            super(itemView);

            if (context.getTag().equals("topAlbums")) {
                albumImage = itemView.findViewById(R.id.artistImageView);
                albumName = itemView.findViewById(R.id.songNameTextView);
                nameArtist = itemView.findViewById(R.id.artistNameTextView);
            } else {
                albumImage = itemView.findViewById(R.id.favAlbumImageView);
                albumName = itemView.findViewById(R.id.favAlbumTextView);
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
