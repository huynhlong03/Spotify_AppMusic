package com.example.musicapp_android.Model;

public class User {
        private String id;
        private String displayName;
        private String password;
        private String avatar;
        private String email;

        public User() {
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getDisplayName() {
                return displayName;
        }

        public void setDisplayName(String displayName) {
                this.displayName = displayName;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public String getAvatar() {
                return avatar;
        }

        public void setAvatar(String avatar) {
                this.avatar = avatar;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getPhone() {
                return phone;
        }

        public void setPhone(String phone) {
                this.phone = phone;
        }

        public String getUserName() {
                return userName;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }

        public User(String id, String displayName, String avatar, String email, String userName, String password, String phone) {
                this.id = id;
                this.displayName = displayName;
                this.avatar = avatar;
                this.email = email;
                this.userName = userName;
                this.password = password;
                this.phone = phone;

        }

        private String phone;
        private String userName;
}
