package com.example.project88trans.model;

public class UserResponse {
    public static class User {
        private String nama;
        private String email;
        private String phone;
        private int id;

        public int getId() { return id; }

        public String getNama() { return nama; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
    }
}
