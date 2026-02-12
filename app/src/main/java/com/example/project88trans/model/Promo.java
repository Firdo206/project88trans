package com.example.project88trans.model;

public class Promo {
    private int id;
    private String name;
    private String code;
    private int amount;
    private String start_date;
    private String end_date;
    private int slot;

    private double totalAfterDiscount;

    public double getTotalAfterDiscount() {
        return totalAfterDiscount;
    }

    public void setTotalAfterDiscount(double totalAfterDiscount) {
        this.totalAfterDiscount = totalAfterDiscount;
    }


    // Getter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public int getAmount() { return amount; }
    public String getStart_date() { return start_date; }
    public String getEnd_date() { return end_date; }
    public int getSlot() { return slot; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setStart_date(String start_date) { this.start_date = start_date; }
    public void setEnd_date(String end_date) { this.end_date = end_date; }
    public void setSlot(int slot) { this.slot = slot; }
}
