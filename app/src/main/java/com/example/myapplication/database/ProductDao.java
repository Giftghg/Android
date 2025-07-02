package com.example.myapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE id = :id")
    LiveData<Product> getProductById(int id);

    @Query("SELECT * FROM products ORDER BY createTime DESC")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createTime DESC")
    LiveData<List<Product>> getProductsBySeller(int sellerId);

    @Query("SELECT * FROM products WHERE category = :category ORDER BY createTime DESC")
    LiveData<List<Product>> getProductsByCategory(String category);

    @Query("SELECT * FROM products WHERE status = :status ORDER BY createTime DESC")
    LiveData<List<Product>> getProductsByStatus(String status);

    @Query("SELECT * FROM products WHERE title LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%' ORDER BY createTime DESC")
    LiveData<List<Product>> searchProducts(String keyword);

    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice ORDER BY createTime DESC")
    LiveData<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice);

    @Query("SELECT * FROM products WHERE location LIKE '%' || :location || '%' ORDER BY createTime DESC")
    LiveData<List<Product>> getProductsByLocation(String location);

    @Query("UPDATE products SET viewCount = viewCount + 1 WHERE id = :productId")
    void incrementViewCount(int productId);

    @Query("UPDATE products SET favoriteCount = favoriteCount + 1 WHERE id = :productId")
    void incrementFavoriteCount(int productId);

    @Query("UPDATE products SET favoriteCount = favoriteCount - 1 WHERE id = :productId")
    void decrementFavoriteCount(int productId);

    @Query("SELECT * FROM products WHERE buyerId = :buyerId ORDER BY createTime DESC")
    LiveData<List<Product>> getProductsByBuyer(int buyerId);

    @Query("UPDATE products SET status = :status, buyerId = :buyerId WHERE id = :productId")
    void updateProductStatusAndBuyer(int productId, String status, int buyerId);
} 