package com.example.musicapp_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp_android.Model.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DangKi extends AppCompatActivity {

    EditText edtId, edtUserName, edtPassword, edtEmail, edtDisplayName, edtAvatar, edtPhone;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        addControls();
        addEvents();
    }

    void addControls() {
        edtId = findViewById(R.id.edt_id);
        edtUserName = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        edtEmail = findViewById(R.id.edt_email);
        edtDisplayName = findViewById(R.id.edt_display_name);
        edtAvatar = findViewById(R.id.edt_avatar);
        edtPhone = findViewById(R.id.edt_phone);
        btnRegister = findViewById(R.id.btn_register);
    }

    void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy thông tin từ các EditText
                String id = edtId.getText().toString().trim();
                String userName = edtUserName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String displayName = edtDisplayName.getText().toString().trim();
                String avatar = edtAvatar.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                // Kiểm tra nếu các trường không rỗng
                if (!userName.isEmpty() && !password.isEmpty() && !email.isEmpty() && !displayName.isEmpty() && !avatar.isEmpty() && !phone.isEmpty()) {
                    // Tạo đối tượng User
                    User user = new User(id, displayName, avatar, email, userName, password, phone);

                    // Lưu thông tin người dùng vào Firebase
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
                    userRef.child("user" + (System.currentTimeMillis() / 1000)).setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Đăng ký thành công, hiển thị thông báo và chuyển về màn hình đăng nhập
                                Toast.makeText(DangKi.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DangKi.this, DangNhap.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Đăng ký thất bại, hiển thị thông báo lỗi
                                Toast.makeText(DangKi.this, "Đăng ký thất bại: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Hiển thị thông báo nếu có trường nào đó trống
                    Toast.makeText(DangKi.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
