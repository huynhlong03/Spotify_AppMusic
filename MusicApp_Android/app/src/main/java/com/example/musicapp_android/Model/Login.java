package com.example.musicapp_android.Model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp_android.DangKi;
import com.example.musicapp_android.DangNhap;
import com.example.musicapp_android.MainActivity;
import com.example.musicapp_android.R;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment);
    }
    public void onRegisterClicked(View view) {
        // Chuyển sang activity Đăng kí khi nút đăng kí được nhấn
        Intent intent = new Intent(getApplicationContext(), DangKi.class);
        startActivity(intent);
    }
    public void onRequestTokenClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), DangNhap.class);
        startActivity(intent);
    }
}
