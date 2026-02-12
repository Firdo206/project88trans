package com.example.project88trans.model;

import com.google.gson.annotations.SerializedName;

public class InvoiceData {

    // kode transaksi
    @SerializedName("rental_code")
    private String rental_code;

    // user
    @SerializedName("user_name")
    private String user_name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone_number")
    private String phone_number;

    // payment
    @SerializedName("payment_method")
    private String payment_method;

    @SerializedName("original_price")
    private int original_price;

    @SerializedName("promo_amount")
    private int promo_amount;

    @SerializedName("final_price")
    private int final_price;

    @SerializedName("payment_status")
    private String payment_status;

    @SerializedName("promo_code")
    private String promo_code;

    // bus fields
    @SerializedName("bus_type")
    private String bus_type;

    @SerializedName("start_date")
    private String start_date;

    @SerializedName("end_date")
    private String end_date;

    @SerializedName("destination")
    private String destination;

    @SerializedName("total_days")
    private Integer total_days;

    // tour fields
    @SerializedName("tour_name")
    private String tour_name;

    @SerializedName("duration_days")
    private Integer duration_days;

    @SerializedName("people")
    private Integer people;

    // ===== GETTERS =====
    public String getRentalCode() { return rental_code; }
    public String getUserName() { return user_name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phone_number; }
    public String getPaymentMethod() { return payment_method; }
    public int getOriginalPrice() { return original_price; }
    public int getPromoAmount() { return promo_amount; }
    public int getFinalPrice() { return final_price; }
    public String getPaymentStatus() { return payment_status; }
    public String getPromoCode() { return promo_code; }
    public String getBusType() { return bus_type; }
    public String getStartDate() { return start_date; }
    public String getEndDate() { return end_date; }
    public String getDestination() { return destination; }
    public Integer getTotalDays() { return total_days; }
    public String getTourName() { return tour_name; }
    public Integer getDurationDays() { return duration_days; }
    public Integer getPeople() { return people; }
}
