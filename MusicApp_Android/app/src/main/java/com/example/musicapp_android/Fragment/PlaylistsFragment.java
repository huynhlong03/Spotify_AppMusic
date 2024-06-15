package com.example.musicapp_android.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.musicapp_android.Adapter.PlaylistAdapter;
import com.example.musicapp_android.DangNhap;
import com.example.musicapp_android.Data.Playlist;
import com.example.musicapp_android.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlaylistsFragment extends Fragment {

    View view;
    ListView playlistListView;
    ImageButton createPlaylistButton;
    ArrayList<Playlist> lsPlayplist = new ArrayList<>();
    PlaylistAdapter adapter;

    String currentUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.playlists_fragment, container, false);
        playlistListView = view.findViewById(R.id.playlistListView);
        registerForContextMenu(playlistListView);
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("MyAppPreferences2", Context.MODE_PRIVATE);
        String idUser = sharedPreferences2.getString("idUser", null); // null là giá trị mặc định nếu không tìm thấy
        currentUser = idUser; // Lưu idUser vào biến currentUser hoặc biến phù hợp
        FirebaseApp.initializeApp(getActivity());
        adapter = new PlaylistAdapter(getActivity(), R.layout.layout_playlist, lsPlayplist);
        playlistListView.setAdapter(adapter);
        getDataFromFirebase();
        createPlaylistButton = view.findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameInputDialog();
            }
        });
        addevents();
        return view;
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_name, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                if (!name.isEmpty()) {
                    Toast.makeText(getActivity(), "Name entered: " + name, Toast.LENGTH_SHORT).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Playlist");

                    myRef.child("Playlist" + lsPlayplist.size()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int newId = lsPlayplist.size() + 1;
                                while (dataSnapshot.hasChild("Playlist" + newId)) {
                                    newId++;
                                }
                                String id = "Playlist" + newId;
                                Playlist playlist = new Playlist(id, name, currentUser);

                                myRef.child(id).setValue(playlist);
                            } else {
                                String id = "Playlist" + lsPlayplist.size();
                                Playlist playlist = new Playlist(id, name, currentUser);
                                myRef.child(id).setValue(playlist);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference playlistRef = database.getReference("Playlist");

        playlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsPlayplist.clear();
                for (DataSnapshot playlistSnapshot : snapshot.getChildren()) {
                    Playlist playlist = playlistSnapshot.getValue(Playlist.class);
                    if (playlist != null && playlist.getIdUser().equals(currentUser)) {
                        lsPlayplist.add(playlist); // Nếu có, thêm playlist vào danh sách hiển thị
                    }
                }
                adapter = new PlaylistAdapter(getActivity(), R.layout.layout_playlist, lsPlayplist);
                playlistListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
    void addevents() {
        playlistListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                        .setTitle("Delete PlayList")
                        .setMessage("Bạn có muốn xóa Playlist?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Playlist");
                                String playlistId = getPlaylistIdFromPosition(i);
                                Log.d("playlistId : ",playlistId);

                                if (playlistId != null) {
                                    Query query=myRef.child(playlistId);
                                    myRef.child(playlistId);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            snapshot.getRef().removeValue();
                                            Toast.makeText(getContext(), "Playlist deleted", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle possible errors
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Không", null)
                        .show();

                return true;
            }
        });
        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playlistId = getPlaylistIdFromPosition(position);
                showSongsInNewFragment(playlistId);
            }
        });
    }
    private String getPlaylistIdFromPosition(int position) {
        // Implement this method to return the ID of the playlist based on the position
        // This could involve querying your data source or list to get the corresponding ID
        return lsPlayplist.get(position).getId();  // Adjust this line according to your actual data source
    }
    private void showSongsInNewFragment(String playlistId) {
        SongPlaylistFragment songPlaylistFragment = SongPlaylistFragment.newInstance(playlistId);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, songPlaylistFragment)
                .addToBackStack(null)
                .commit();
    }
    //


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        if (item.getItemId() == R.id.Edit) {
            // Show dialog to edit playlist name
            showEditPlaylistDialog(position);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }
    private void showEditPlaylistDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_name, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editTextName.getText().toString().trim();
                if (!newName.isEmpty()) {
                    // Update the playlist name
                    updatePlaylistName(position, newName);
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updatePlaylistName(int position, String newName) {
        String playlistId = getPlaylistIdFromPosition(position);
        if (playlistId != null) {
            DatabaseReference playlistRef = FirebaseDatabase.getInstance().getReference("Playlist").child(playlistId);
            playlistRef.child("name").setValue(newName);
            Toast.makeText(getContext(), "Playlist name updated", Toast.LENGTH_SHORT).show();
        }
    }

}
