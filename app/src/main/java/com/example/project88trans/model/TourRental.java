package com.example.project88trans.model;

import com.google.gson.annotations.SerializedName;

public class TourRental {

    @SerializedName("id")
    private int id;

    @SerializedName("rental_code")
    private String rentalId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("package_id")
    private int packageId;

    @SerializedName("number_of_people")
    private int numberOfPeople;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("date")
    private String date;

    public int getId() {
        return id;
    }

    public String getRentalCode() {
        return rentalId;
    }

    public int getUserId() {
        return userId;
    }

    public int getPackageId() {
        return packageId;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}
