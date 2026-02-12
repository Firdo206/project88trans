package com.example.project88trans.model;

import com.google.gson.annotations.SerializedName;

public class TourRentalCreate {

    @SerializedName("rental_id")
    private int id;

    @SerializedName("rental_code")
    private String rentalCode;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("number_of_people")
    private int numberOfPeople;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("status")
    private String status;

    // optional jika API mengembalikan user/package (aman untuk forward-compatibility)
    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("package_id")
    private Integer packageId;

    public TourRentalCreate() {
        // konstruktor kosong untuk Gson
    }

    public TourRentalCreate(int id, String rentalCode, double totalPrice, int numberOfPeople,
                            String startDate, String status, Integer userId, Integer packageId) {
        this.id = id;
        this.rentalCode = rentalCode;
        this.totalPrice = totalPrice;
        this.numberOfPeople = numberOfPeople;
        this.startDate = startDate;
        this.status = status;
        this.userId = userId;
        this.packageId = packageId;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getRentalCode() {
        return rentalCode;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStatus() {
        return status;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setRentalCode(String rentalCode) {
        this.rentalCode = rentalCode;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    @Override
    public String toString() {
        return "TourRentalCreate{" +
                "id=" + id +
                ", rentalCode='" + rentalCode + '\'' +
                ", totalPrice=" + totalPrice +
                ", numberOfPeople=" + numberOfPeople +
                ", startDate='" + startDate + '\'' +
                ", status='" + status + '\'' +
                ", userId=" + userId +
                ", packageId=" + packageId +
                '}';
    }
}
