package com.example.project88trans.model;

import com.google.gson.annotations.SerializedName;

public class BusRental {

    @SerializedName("id")
    private int id;

    @SerializedName("rental_code")
    private String rentalCode;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("total_days")
    private int totalDays;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("status")
    private String status;

    @SerializedName("date")
    private String date;

    public int getId() { return id; }
    public String getRentalCode() { return rentalCode; }
    public int getUserId() { return userId; }
    public int getTotalDays() { return totalDays; }
    public double getTotalPrice() { return totalPrice; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
}
