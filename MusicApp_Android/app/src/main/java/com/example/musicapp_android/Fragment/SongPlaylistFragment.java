package com.example.musicapp_android.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.musicapp_android.Adapter.CustomAdapterAllSong;
import com.example.musicapp_android.Adapter.SongPlayListAdapter;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.MainActivity;
import com.example.musicapp_android.R;
import com.example.musicapp_android.Services.ActivityAllSong;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SongPlaylistFragment extends Fragment {

    private static final String ARG_PLAYLIST_ID = "playlist_id";
    private String playlistId;
    private ListView songsListView;
    CustomAdapterAllSong adapter;
    private SongPlayListAdapter songAdapter;
    private ArrayList<Song> songList;
    TextView tvtile;
    private static final int EDIT = 0, DELETE = 1;

    public static SongPlaylistFragment newInstance(String playlistId) {
        SongPlaylistFragment fragment = new SongPlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_playlist, container, false);
        tvtile = view.findViewById(R.id.title);
        songsListView = view.findViewById(R.id.songsListView);
        registerForContextMenu(songsListView);
        songList = new ArrayList<>();
        songAdapter = new SongPlayListAdapter(getActivity(), R.layout.list_item, songList);
        songsListView.setAdapter(songAdapter);
        if (getArguments() != null) {
            playlistId = getArguments().getString(ARG_PLAYLIST_ID);
            fetchSongs();
        }

        songsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songList.get(position);
                showDeleteConfirmationDialog(song);
                return true;
            }
        });
        addEvents();
        return view;
    }


    private void addEvents() {
        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Chuyển đến MainActivity khi click vào item
                Intent intent = new Intent(getActivity(), MainActivity.class);
                //intent.putExtra("song", lstAllSong.get(i)); // Chuyển ID của bài hát
                Bundle bundle = new Bundle();
                bundle.putLong("song_id", l); // Chuyển ID của bài hát
                bundle.putParcelableArrayList("song_list", (ArrayList<? extends Parcelable>) songList); // lstSo là danh sách các bài hát của bạn
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void fetchSongs() {
        DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference("Playlist/" + playlistId + "/Songs");

        songsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList.clear();
                for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                    Song song = songSnapshot.getValue(Song.class);
                    songList.add(song);
                }

                if (songList.isEmpty()) {
                    tvtile.setText("Không Có bài nhạc nào trong Playlist");
                    //Toast.makeText(getActivity(), "Không có bài hát trong playlist của bạn", Toast.LENGTH_SHORT).show();
                }

                songAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void showDeleteConfirmationDialog(Song song) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                .setTitle("Delete Song")
                .setMessage("Bạn có muốn xóa bài hát này khỏi playlist không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSong(song);
                    }
                })
                .setNegativeButton("Không", null)
                .create();

        dialog.show();

        // Access the NegativeButton and set its text color
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    private void deleteSong(Song song) {
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("Playlist/" + playlistId + "/Songs").child(song.getName());
        songRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                songList.remove(song);
                songAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Đã xóa bài hát khỏi playlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Xóa bài hát không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.baseline_edit_24);
        menu.setHeaderTitle("Save Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Save");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Save");
    }
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                // TODO: Add save edit code
                break;
            case DELETE:

                break;
        }

        return super.onContextItemSelected(item);
    }
}
