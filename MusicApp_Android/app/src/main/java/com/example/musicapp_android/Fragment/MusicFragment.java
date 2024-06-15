package com.example.musicapp_android.Fragment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.Target;
import com.example.musicapp_android.Adapter.SongListAdapter;
import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Data.Song;
import com.example.musicapp_android.R;
import com.example.musicapp_android.Services.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MusicFragment extends Fragment {


    private static final String ARG_SONG = "selected_song";
    private ImageButton btnPlay, btnPause, btnNext, btnPrevious, btnRandom, btnAgain, btnVolume, btnVolumeMute;
    private SeekBar seekBar;
    private TextView songTitle, songArtist;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private ObjectAnimator rotationAnimator;
    private CircleImageView albumArt;
    private ListView lstSong;
    private ImageView imgViw;
    private SeekBar skb_volume;
    private LinearLayout lut_name;
    ArrayList<Song> lstSo = new ArrayList<>();
    ArrayList<Artist> lstAr = new ArrayList<>();
    SongListAdapter adapter;
    Song song;
    private boolean isRedRandom = false;
    private boolean isRedAgain = false;
    private boolean isVolume = false;
    Intent intent;
    private int process = 50;
    private ProgressBar loadingIndicator;
    Boolean is_playing= false;

    private Song selectedSong;

    private BroadcastReceiver songReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_SONG_UPDATE".equals(intent.getAction())) {
                Song song = intent.getParcelableExtra("song");
                if (song != null) {
                    updateUI(song);
                }
            }
        }
    };


    private BroadcastReceiver UISongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_SONG_UI_UPDATE".equals(intent.getAction())) {
                song = intent.getParcelableExtra("song");
                int current_position = intent.getIntExtra("current_position", 0);
                int duration = intent.getIntExtra("duration", 0);
                is_playing = intent.getBooleanExtra("is_playing", false);
                isRedRandom = intent.getBooleanExtra("isRedRandom", false);
                isRedAgain = intent.getBooleanExtra("isRedAgain", false);
                float volume = intent.getFloatExtra("volume", 0);

                seekBar.setMax(duration);
                seekBar.setProgress(current_position);

                if(isRedRandom)
                    btnRandom.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                else
                    btnRandom.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

                if(isRedAgain)
                    btnAgain.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                else
                    btnAgain.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

                if(is_playing) {
                    btnPlay.setVisibility(View.GONE);
                    btnPause.setVisibility(View.VISIBLE);

                    // Khởi tạo ObjectAnimator để xoay hình ảnh album
                    rotationAnimator = ObjectAnimator.ofFloat(albumArt, "rotation", 0f, 360f);
                    rotationAnimator.setDuration(10000); // Thời gian để xoay một vòng (10 giây)
                    rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                    rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);
                    rotationAnimator.start();
                }
                else {
                    btnPlay.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.GONE);

                    // Khởi tạo ObjectAnimator để xoay hình ảnh album
                    rotationAnimator = ObjectAnimator.ofFloat(albumArt, "rotation", 0f, 360f);
                    rotationAnimator.setDuration(10000); // Thời gian để xoay một vòng (10 giây)
                    rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                    rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);
                    rotationAnimator.pause();
                }

                songTitle.setText(song.getName());
                for (Artist ar: lstAr) {
                    if(ar.getId().equals(song.getIdArtist()))
                    {
                        songArtist.setText(ar.getName());
                        break;
                    }
                }

                skb_volume.setProgress((int) (volume * 100));
            }
        }
    };

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_SONG_PROGRESS".equals(intent.getAction())) {
                int currentPosition = intent.getIntExtra("current_position", 0);
                // Cập nhật giao diện người dùng với vị trí hiện tại của bài hát
                // Ví dụ: cập nhật SeekBar
                seekBar.setProgress(currentPosition);
            }
        }
    };

    private BroadcastReceiver durationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_SONG_DURATION".equals(intent.getAction())) {
                int duration = intent.getIntExtra("duration", 0);
                seekBar.setMax(duration);
            }
        }
    };

    private void updateUI(Song song) {
        songTitle.setText(song.getName());
        for (Artist ar: lstAr) {
            if(ar.getId().equals(song.getIdArtist()))
            {
                songArtist.setText(ar.getName());
                break;
            }
        }

        hideLoadingIndicator();
        handleMusicPlay();

        // Khởi tạo ObjectAnimator để xoay hình ảnh album
        rotationAnimator = ObjectAnimator.ofFloat(albumArt, "rotation", 0f, 360f);
        rotationAnimator.setDuration(10000); // Thời gian để xoay một vòng (10 giây)
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);

        // Bắt đầu xoay hình ảnh album
        rotationAnimator.start();

        float volume = skb_volume.getProgress() / 100f;
        intent.putExtra("volume", volume);
        intent.setAction("ACTION_VOLUME");
        getActivity().startService(intent);

        Glide.with(getContext())
                .load(song.getImage())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transform(new RoundedCorners(20))
                .placeholder(R.drawable.happy)
                .error(R.drawable.song_error_image)
                .into(albumArt);
        // Other UI updates as needed
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_SONG_DURATION");
        filter.addAction("ACTION_SONG_PROGRESS");
        filter.addAction("ACTION_SONG_UPDATE");
        filter.addAction("ACTION_SONG_UI_UPDATE");
        getActivity().registerReceiver(songReceiver, filter);
        getActivity().registerReceiver(progressReceiver, filter);
        getActivity().registerReceiver(durationReceiver, filter);
        getActivity().registerReceiver(UISongReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(songReceiver);
        getActivity().unregisterReceiver(durationReceiver);
        getActivity().unregisterReceiver(progressReceiver);
        getActivity().unregisterReceiver(UISongReceiver);
    }

    private static final String ARG_SONG_ID = "song_id";
    private static final String ARG_SONG_LIST = "song_list";

    private long songId = -1;

    public MusicFragment() {
        // Required empty public constructor
    }

    // Tạo một newInstance() method để truyền dữ liệu vào Fragment
    public static MusicFragment newInstance(long songId, ArrayList<Song> lstSong) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SONG_ID, songId);
        args.putParcelableArrayList(ARG_SONG_LIST, (ArrayList<? extends Parcelable>) lstSong);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Lấy dữ liệu từ Bundle và lưu vào biến instance của Fragment
            songId = getArguments().getLong(ARG_SONG_ID);
            lstSo = getArguments().getParcelableArrayList(ARG_SONG_LIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        addControls(view);
        addEvents();
        if(songId == -1)
            getDataSongFromFirebase();
        else {
            loadLstSong(lstSo);
            showLoadingIndicator();
            handleMusicPause();
            song = lstSo.get((int) songId);
            intent.putExtra("song", song);
            intent.setAction("ACTION_NEW");
            getActivity().startService(intent);
        }
        getDataArtistFromFirebase();
        return view;
    }

    public void addControls(View view) {
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPause = view.findViewById(R.id.btnPause);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnRandom = view.findViewById(R.id.btnRandom);
        btnAgain = view.findViewById(R.id.btnAgain);
        seekBar = view.findViewById(R.id.seekBar);
        songTitle = view.findViewById(R.id.songTitle);
        songArtist = view.findViewById(R.id.songArtist);
        albumArt = view.findViewById(R.id.albumArt);
        lstSong = view.findViewById(R.id.lst_dsBaiHat);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        imgViw = view.findViewById(R.id.img_song);
        lut_name = view.findViewById(R.id.lut_name);
        btnVolume = view.findViewById(R.id.btnVolume);
        btnVolumeMute = view.findViewById(R.id.btnVolumeMute);
        skb_volume = view.findViewById(R.id.skb_volume);
    }

    public void addEvents() {

        // Set the current progress of the SeekBar to 50 (midpoint for demonstration)
        skb_volume.setProgress(process);
        // Intent
        intent = new Intent(getActivity(), MusicService.class);

        intent.setAction("ACTION_INFORMATION");
        getActivity().startService(intent);

        // Xác định trạng thái ban đầu của các nút
        btnPlay.setVisibility(View.VISIBLE); // Ban đầu hiển thị nút Play
        btnPause.setVisibility(View.GONE);   // Ban đầu ẩn nút Pause

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    intent.putExtra("progress", progress);
                    intent.setAction("ACTION_PROGRESS");
                    getActivity().startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        lstSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showLoadingIndicator();
                handleMusicPause();
                song = lstSo.get(i);
                intent.putExtra("song", song);
                intent.setAction("ACTION_NEW");
                getActivity().startService(intent);
            }
        });


        // Thiết lập onClickListener cho nút Play
        btnPlay.setOnClickListener(v -> {
            if(song != null)
            {
                handleMusicPlay();
                rotationAnimator.start();
                intent.setAction("ACTION_PLAY");
                getActivity().startService(intent);
            }
        });

        // Thiết lập onClickListener cho nút Pause
        btnPause.setOnClickListener(v -> {
            handleMusicPause();
            rotationAnimator.pause();
            intent.setAction("ACTION_PAUSE");
            getActivity().startService(intent);
        });

        btnNext.setOnClickListener(v -> {
            if(song != null) {
                intent.setAction("ACTION_RANDOM_BTN_NEXT");
                getActivity().startService(intent);
                showLoadingIndicator();
                handleMusicPause();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if(song != null) {
                intent.setAction("ACTION_RANDOM_BTN_PREVIOUS");
                getActivity().startService(intent);
                showLoadingIndicator();
                handleMusicPause();
            }
        });


        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRedRandom) {
                    btnRandom.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                } else {
                    btnRandom.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                }
                isRedRandom = !isRedRandom;
                intent.putExtra("isRedRandom", isRedRandom);
                intent.setAction("ACTION_RANDOM");
                getActivity().startService(intent);
            }
        });

        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                if (isRedAgain) {
                    btnAgain.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                } else {
                    btnAgain.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                }
                isRedAgain = !isRedAgain;
                intent.putExtra("isRedAgain", isRedAgain);
                intent.setAction("ACTION_AGAIN");
                getActivity().startService(intent);
            }

        });

        btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the volume for both left and right channels
                setProcess(0);
                btnVolumeMute.setVisibility(View.VISIBLE);
                btnVolume.setVisibility(View.GONE);
            }
        });

        btnVolumeMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProcess(process);
                btnVolumeMute.setVisibility(View.GONE);
                btnVolume.setVisibility(View.VISIBLE);
            }
        });

        // Set up a listener for changes to the SeekBar
        skb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress != 0)
                {
                    process = progress;
                }
                setProcess(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void setProcess(int process){
        skb_volume.setProgress(process);
        float volume = skb_volume.getProgress() / 100f;
        intent.putExtra("volume", volume);
        intent.setAction("ACTION_VOLUME");
        getActivity().startService(intent);

    }

    private void showLoadingIndicator() {
        loadingIndicator.setVisibility(View.VISIBLE);
        lut_name.setVisibility(View.GONE);
    }

    private void hideLoadingIndicator() {
        loadingIndicator.setVisibility(View.GONE);
        lut_name.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
    }

    public void getDataSongFromFirebase()
    {
        // Lấy dữ liệu từ Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Song");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstSo.clear();
                for (DataSnapshot songSnapshot: snapshot.getChildren()) {
                    Song song = songSnapshot.getValue(Song.class);
                    lstSo.add(song);
                }
                loadLstSong(lstSo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "onCancelled", error.toException());
            }
        });
    }

    private void loadLstSong(ArrayList<Song> lst){
        adapter=new SongListAdapter(getActivity(), R.layout.layout_song, lstSo);
        lstSong.setAdapter(adapter);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("song_list", (ArrayList<? extends Parcelable>) lstSo); // lstSo là danh sách các bài hát của bạn
        intent.putExtras(bundle);
        intent.setAction("ACTION_SEND_LIST");
        getActivity().startService(intent);
    }

    public void getDataArtistFromFirebase()
    {
        // Lấy dữ liệu từ Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Artist");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstAr.clear();
                for (DataSnapshot songSnapshot: snapshot.getChildren()) {
                    Artist artist = songSnapshot.getValue(Artist.class);
                    lstAr.add(artist);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "onCancelled", error.toException());
            }
        });
    }


    private void handleMusicPause() {
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
    }

    private void handleMusicPlay() {
        btnPlay.setVisibility(View.GONE);
        btnPause.setVisibility(View.VISIBLE);

    }
}