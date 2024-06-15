package com.example.musicapp_android.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp_android.Model.Login;
import com.example.musicapp_android.Model.User;
import com.example.musicapp_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class AccountFragment extends Fragment {
    private ImageView imgAvatar;
    private TextView tvDisplayName;
    private TextView tvEmail;
    private TextView tvUserId;
    private Button btnLogout;
    private Button btnDoiMatKhau;
    private Button btnHuy;
    private EditText txt_mkCu;
    private EditText txt_mkMoi;
    private EditText txt_mkMoiAgain;
    User user;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        addControls(view);
        addEvents();

        return view;
    }



    void addControls(View view) {
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvUserId = view.findViewById(R.id.tvUserId);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDoiMatKhau = view.findViewById(R.id.btnDoiMatKhau);
        btnHuy = view.findViewById(R.id.btnHuy);
        txt_mkCu = view.findViewById(R.id.txt_mkCu);
        txt_mkMoi = view.findViewById(R.id.txt_mkCu);
        txt_mkMoiAgain = view.findViewById(R.id.txt_mkMoiAgain);
    }

    void addEvents() {

        // Giả sử bạn đã lưu trữ thông tin người dùng trong SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", "");

        // Chuyển đổi chuỗi JSON thành đối tượng User
        Gson gson = new Gson();
        user = gson.fromJson(userJson, User.class);

        // Lấy thông tin người dùng từ SharedPreferences
        String displayName = user.getDisplayName();
        String email = user.getEmail();
        String userId = user.getId();
        String avatarUrl = user.getAvatar();

        // Cập nhật giao diện với thông tin người dùng
        tvDisplayName.setText(displayName);
        tvEmail.setText(email);
        tvUserId.setText("User ID: " + userId);

        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(this).load(avatarUrl).placeholder(R.drawable.happy).into(imgAvatar);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent("ACTION_LOGOUT");
                getActivity().sendBroadcast(logoutIntent);
                // Xóa thông tin người dùng trong SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Chuyển đến màn hình đăng nhập
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish(); // Đóng màn hình hiện tại để người dùng không thể quay lại bằng nút Back
            }
        });

        btnDoiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnDoiMatKhau.getText().toString().trim().equals("Đổi mật khẩu")) {
                    txt_mkCu.setVisibility(View.VISIBLE);
                    btnHuy.setVisibility(View.VISIBLE);
                    btnDoiMatKhau.setText("Xác nhận mật khẩu hiện tại");
                } else if (btnDoiMatKhau.getText().toString().trim().equals("Xác nhận mật khẩu hiện tại")) {
                    if (user != null) {
                        if (user.getPassword().equals(txt_mkCu.getText().toString().trim())) {
                            // Tạo một đối tượng LayoutParams cho Button
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnHuy.getLayoutParams();
                            // Thiết lập margin top là 16dp
                            params.setMargins(16, 1100, 16, 16);
                            // Áp dụng các thay đổi vào Button
                            btnHuy.setLayoutParams(params);
                            txt_mkCu.setText("");
                            txt_mkCu.setVisibility(View.GONE);
                            txt_mkMoi.setVisibility(View.VISIBLE);
                            txt_mkMoiAgain.setVisibility(View.VISIBLE);
                            btnDoiMatKhau.setText("Xác nhận mật khẩu mới");
                        } else {
                            Toast.makeText(getContext(), "Mật khẩu sai!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (btnDoiMatKhau.getText().toString().trim().equals("Xác nhận mật khẩu mới")) {
                    // Lấy giá trị của mật khẩu mới và xác nhận lại mật khẩu
                    String newPassword = txt_mkMoi.getText().toString().trim();
                    String newPasswordAgain = txt_mkMoiAgain.getText().toString().trim();
                    if (newPassword.equals(newPasswordAgain)) {
                        btnDoiMatKhau.setText("Đổi mật khẩu");
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("User");
                        Query query = myRef.orderByChild("userName").equalTo(user.getUserName());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().child("password").setValue(newPassword)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    // Mật khẩu đã được cập nhật thành công
                                                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Xảy ra lỗi khi cập nhật mật khẩu
                                                    Toast.makeText(getContext(), "Failed to update password: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.err.println("Query cancelled: " + databaseError.toException());
                            }
                        });
                        txt_mkCu.setText("");
                        txt_mkMoi.setText("");
                        txt_mkMoiAgain.setText("");
                        txt_mkCu.setVisibility(View.GONE);
                        txt_mkMoi.setVisibility(View.GONE);
                        txt_mkMoiAgain.setVisibility(View.GONE);
                        btnHuy.setVisibility(View.GONE);
                        btnDoiMatKhau.setText("Đổi mật khẩu");
                    } else {
                        Toast.makeText(getContext(), "Mật khẩu mới không khớp!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_mkCu.setText("");
                txt_mkMoi.setText("");
                txt_mkMoiAgain.setText("");
                txt_mkCu.setVisibility(View.GONE);
                txt_mkMoi.setVisibility(View.GONE);
                txt_mkMoiAgain.setVisibility(View.GONE);
                btnHuy.setVisibility(View.GONE);
                btnDoiMatKhau.setText("Đổi mật khẩu");
            }
        });
    }
}