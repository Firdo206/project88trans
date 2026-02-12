package com.example.project88trans.model;

import com.google.gson.annotations.SerializedName;

public class BusRentalCreate {

    @SerializedName("rental_id")
    private int id;

    @SerializedName("rental_code")
    private String rentalCode;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("status")
    private String status;

    public int getId() {
        return id;
    }

    public String getRentalCode() {
        return rentalCode;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }
}
