package com.example.musicapp_android.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.example.musicapp_android.Data.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private Runnable updateSeekBar;
    private Handler handler = new Handler();
    private Random random = new Random();
    private Boolean playing = false;
    ArrayList<Song> lstSo = new ArrayList<>();
    private boolean isRedAgain = false;
    private boolean isRedRandom = false;
    private float volume = 50;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_LOGOUT");
        registerReceiver(logoutReceiver, filter);
    }

    // BroadcastReceiver để xử lý broadcast ACTION_LOGOUT
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Dừng phát nhạc khi nhận được broadcast logout
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    };

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Dừng và loại bỏ Runnable
        handler.removeCallbacks(updateSeekBar);
        // Hủy đăng ký BroadcastReceiver trước khi dịch vụ bị hủy
        unregisterReceiver(logoutReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        int position = -1;
        int randomNumber = 0;
        if (action != null) {
            switch (action) {
                case "ACTION_PLAY":
                    if (mediaPlayer != null && !mediaPlayer.isPlaying() && playing) {
                        //mediaPlayer.seekTo(currentSongPosition);
                        mediaPlayer.start();
                        handler.post(updateSeekBar);
                    }
                    break;
                case "ACTION_PAUSE":
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break; // Thêm lệnh break ở đây
                case "ACTION_NEW":
                    Song song = intent.getParcelableExtra("song");
                    addSong(song);
                    playing = true;
                    break;
                case "ACTION_VOLUME":
                    volume = intent.getFloatExtra("volume", 0);
                    if(mediaPlayer != null)
                        mediaPlayer.setVolume(volume, volume);
                    break;
                case "ACTION_PROGRESS":
                    int progress = intent.getIntExtra("progress", 0);
                    mediaPlayer.seekTo(progress);
                    break;
                case "ACTION_RANDOM_BTN_NEXT":
                    // Xử lý chuyển bài hát tiếp theo
                    position = -1; // Khởi tạo vị trí mặc định là -1 (nếu không tìm thấy)

                    for (int i = 0; i < lstSo.size(); i++) {
                        if (lstSo.get(i).getName().equals(currentSong.getName())) {
                            position = i; // Gán vị trí của phần tử phù hợp
                            break; // Thoát khỏi vòng lặp khi tìm thấy
                        }
                    }
                    randomNumber = random.nextInt(lstSo.size() - 1);
                    if(isRedRandom)
                        addSong(lstSo.get(randomNumber));
                    else
                    {
                        if (position + 1 == lstSo.size()) {
                            addSong(lstSo.get(0));
                        } else {
                            addSong(lstSo.get(position + 1));
                        }
                    }
                    break;
                case "ACTION_RANDOM_BTN_PREVIOUS":
                    // Xử lý chuyển bài hát trước đó
                    position = -1; // Khởi tạo vị trí mặc định là -1 (nếu không tìm thấy)

                    for (int i = 0; i < lstSo.size(); i++) {
                        if (lstSo.get(i).getName().equals(currentSong.getName())) {
                            position = i; // Gán vị trí của phần tử phù hợp
                            break; // Thoát khỏi vòng lặp khi tìm thấy
                        }
                    }

                    randomNumber = random.nextInt(lstSo.size() - 1);
                    if(isRedRandom)
                        addSong(lstSo.get(randomNumber));
                    else
                    {
                        if (position - 1 == -1) {
                            addSong(lstSo.get(lstSo.size() - 1));
                        } else {
                            addSong(lstSo.get(position - 1));
                        }
                    }
                    break;
                case "ACTION_RANDOM":
                    isRedRandom = intent.getBooleanExtra("isRedRandom", false);
                    break;
                case "ACTION_AGAIN":
                    isRedAgain = intent.getBooleanExtra("isRedAgain", false);
                    break;
                case "ACTION_SEND_LIST":
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        ArrayList<Song> songList = bundle.getParcelableArrayList("song_list");
                        if (songList != null) {
                            lstSo = songList;
                        }
                    }
                    break;
                case "ACTION_INFORMATION":
                    sendSongUpdateUIBroadcast();
                    break;
            }
        }
        return START_STICKY;
    }

    public void addSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }

        String url = "https://music.youtube.com/watch?v=t7bQwwqW-Hc";

        try {
            mediaPlayer.setDataSource(song.getLinkSong());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                currentSong = song;
                sendSongUpdateBroadcast();
                sendSongDurationBroadcast(mp.getDuration());
                // Bắt đầu cập nhật SeekBar
                handler.post(updateSeekBar);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        // Khởi tạo Runnable để cập nhật SeekBar
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    sendSongProgressBroadcast(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        };

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                int randomNumber = random.nextInt(lstSo.size() - 1);
                int position = -1; // Khởi tạo vị trí mặc định là -1 (nếu không tìm thấy)

                for (int i = 0; i < lstSo.size(); i++) {
                    if (lstSo.get(i).getName().equals(song.getName())) {
                        position = i; // Gán vị trí của phần tử phù hợp
                        break; // Thoát khỏi vòng lặp khi tìm thấy
                    }
                }
                if(isRedAgain)
                    addSong(lstSo.get(position));
                else if(isRedRandom)
                    addSong(lstSo.get(randomNumber));
                else
                {
                    if (position + 1 == lstSo.size()) {
                        addSong(lstSo.get(0));
                    } else {
                        addSong(lstSo.get(position + 1));
                    }
                }
            }
        });

    }

    private void sendSongUpdateBroadcast() {
        Intent intent = new Intent("ACTION_SONG_UPDATE");
        intent.putExtra("song", currentSong);
        sendBroadcast(intent);
    }

    private void sendSongUpdateUIBroadcast() {
        if(currentSong != null)
        {
            Intent intent = new Intent("ACTION_SONG_UI_UPDATE");
            intent.putExtra("song", currentSong);
            intent.putExtra("current_position", mediaPlayer.getCurrentPosition());
            intent.putExtra("duration", mediaPlayer.getDuration());
            intent.putExtra("is_playing", mediaPlayer.isPlaying());
            intent.putExtra("isRedRandom", isRedRandom);
            intent.putExtra("isRedAgain", isRedAgain);
            intent.putExtra("volume", volume);
            sendBroadcast(intent);
        }
    }


    private void sendSongDurationBroadcast(int duration) {
        Intent intent = new Intent("ACTION_SONG_DURATION");
        intent.putExtra("duration", duration);
        sendBroadcast(intent);
    }

    private void sendSongProgressBroadcast(int currentPosition) {
        Intent intent = new Intent("ACTION_SONG_PROGRESS");
        intent.putExtra("current_position", currentPosition);
        sendBroadcast(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}