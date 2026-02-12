package com.example.project88trans.model;

import com.google.gson.annotations.SerializedName;

public class Payment {

    @SerializedName("rental_type")
    private String rentalType;

    @SerializedName("rental_id")
    private int rentalId;

    @SerializedName("promo_code")
    private String promoCode;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("total_amount")
    private double totalAmount;

    public Payment(String rentalType, int rentalId, String promoCode, String paymentMethod, double totalAmount) {
        this.rentalType = rentalType;
        this.rentalId = rentalId;
        this.promoCode = promoCode;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
    }

    // Getter & Setter
    public String getRentalType() { return rentalType; }
    public void setRentalType(String rentalType) { this.rentalType = rentalType; }

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
