package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.Product;
import com.example.myapplication.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private LiveData<List<Product>> allProducts;

    public ProductViewModel(Application application) {
        super(application);
        repository = new ProductRepository(application);
        allProducts = repository.getAllProducts();
    }

    public void insert(Product product) {
        repository.insert(product);
    }

    public void update(Product product) {
        repository.update(product);
    }

    public void delete(Product product) {
        repository.delete(product);
    }

    public LiveData<Product> getProductById(int id) {
        return repository.getProductById(id);
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<List<Product>> getProductsBySeller(int sellerId) {
        return repository.getProductsBySeller(sellerId);
    }

    public LiveData<List<Product>> getProductsByCategory(String category) {
        return repository.getProductsByCategory(category);
    }

    public LiveData<List<Product>> getProductsByStatus(String status) {
        return repository.getProductsByStatus(status);
    }

    public LiveData<List<Product>> searchProducts(String keyword) {
        return repository.searchProducts(keyword);
    }

    public LiveData<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice) {
        return repository.getProductsByPriceRange(minPrice, maxPrice);
    }

    public LiveData<List<Product>> getProductsByLocation(String location) {
        return repository.getProductsByLocation(location);
    }

    public void incrementViewCount(int productId) {
        repository.incrementViewCount(productId);
    }

    public void incrementFavoriteCount(int productId) {
        repository.incrementFavoriteCount(productId);
    }

    public void decrementFavoriteCount(int productId) {
        repository.decrementFavoriteCount(productId);
    }

    public void createProduct(String title, String description, double price, 
                            String category, int sellerId, String sellerName) {
        Product product = new Product(title, description, price, category, sellerId);
        product.setSellerName(sellerName);
        insert(product);
    }

    public void updateProductStatusAndBuyer(int productId, String status, int buyerId) {
        repository.updateProductStatusAndBuyer(productId, status, buyerId);
    }

    public LiveData<List<Product>> getProductsByBuyer(int buyerId) {
        return repository.getProductsByBuyer(buyerId);
    }
} 