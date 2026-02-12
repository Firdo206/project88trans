package com.example.project88trans.model;

import java.io.Serializable;
import java.util.List;

public class bus implements Serializable {
    private int id;
    private String name;
    private String image;
    private int capacity;
    private int price;
    private String description;
    private String type_bus;
    private String status;
    private String created_at;
    private String updated_at;
    private List<String> features;
    private float rating;
    private int reviews_count;

    // Constructor
    public bus() {}

    // Getter & Setter untuk field baru
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    // Getter & Setter lainnya tetap sama
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType_bus() { return type_bus; }
    public void setType_bus(String type_bus) { this.type_bus = type_bus; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getReviews_count() { return reviews_count; }
    public void setReviews_count(int reviews_count) { this.reviews_count = reviews_count; }
}