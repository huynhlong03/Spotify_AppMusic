package com.example.musicapp_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp_android.Data.Artist;
import com.example.musicapp_android.Model.User;
import com.example.musicapp_android.Services.FeartureSong;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DangNhap extends AppCompatActivity {
    EditText edtUser, edtPass;
    Button btnLogin;
    ListView lst;
    ArrayList<User> lstUser = new ArrayList<>();
    public  String idUser = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        addControls();
        getDataFromFirebase();
        addEvents();
    }

    void addControls() {
        edtPass = (EditText) findViewById(R.id.edt_Password);
        edtUser = (EditText) findViewById(R.id.edt_Username);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        lst = (ListView) findViewById(R.id.lst);

    }

    void addEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean flag = false;
                for (User u:
                        lstUser) {
                    if(u.getUserName().equals(edtUser.getText().toString().trim())  && u.getPassword().equals(edtPass.getText().toString().trim()))
                    {
                        // Chuyển đổi đối tượng User thành chuỗi JSON
                        Gson gson = new Gson();
                        String userJson = gson.toJson(u);

                        // Lưu chuỗi JSON vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", userJson);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        flag = true;
                        SharedPreferences sharedPreferences2 = getSharedPreferences("MyAppPreferences2", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                        editor2.putString("idUser", u.getId());
                        editor2.apply();

                        idUser = u.getId();

                    }
                }
                if(!flag)
                {
                    Toast.makeText(getApplicationContext(), "Sai mật khẩu, vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getDataFromFirebase()
    {
        // Lấy dữ liệu từ Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstUser.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    lstUser.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "onCancelled", error.toException());
            }
        });
    }
}