package com.example.project88trans.model;

public class user {
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String role;

    private int id; // ubah dari String ke int

    public int getId() { return id; }

    public String getFirstName() { return first_name; }
    public String getLastName() { return last_name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phone; }
    public String getRole() { return role; }
}