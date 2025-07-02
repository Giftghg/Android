package com.example.myapplication.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private double price;
    private String originalPrice;
    private String category;
    private String condition; // 新旧程度
    private String images; // JSON格式存储图片URL数组
    private int sellerId;
    private String sellerName;
    private String location;
    private String status; // 在售、已售、下架
    private int viewCount;
    private int favoriteCount;
    private String createTime;
    private String updateTime;

    public Product() {}

    @Ignore
    public Product(String title, String description, double price, String category, int sellerId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.sellerId = sellerId;
        this.status = "在售";
        this.viewCount = 0;
        this.favoriteCount = 0;
        this.createTime = String.valueOf(System.currentTimeMillis());
        this.updateTime = String.valueOf(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(String originalPrice) { this.originalPrice = originalPrice; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
} 