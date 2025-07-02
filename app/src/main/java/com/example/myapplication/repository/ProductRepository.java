package com.example.myapplication.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.ProductDao;
import com.example.myapplication.model.Product;

import java.util.List;

public class ProductRepository {
    private ProductDao productDao;
    private LiveData<List<Product>> allProducts;

    public ProductRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        productDao = database.productDao();
        allProducts = productDao.getAllProducts();
    }

    public void insert(Product product) {
        new InsertProductAsyncTask(productDao).execute(product);
    }

    public void update(Product product) {
        new UpdateProductAsyncTask(productDao).execute(product);
    }

    public void delete(Product product) {
        new DeleteProductAsyncTask(productDao).execute(product);
    }

    public LiveData<Product> getProductById(int id) {
        return productDao.getProductById(id);
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<List<Product>> getProductsBySeller(int sellerId) {
        return productDao.getProductsBySeller(sellerId);
    }

    public LiveData<List<Product>> getProductsByCategory(String category) {
        return productDao.getProductsByCategory(category);
    }

    public LiveData<List<Product>> getProductsByStatus(String status) {
        return productDao.getProductsByStatus(status);
    }

    public LiveData<List<Product>> searchProducts(String keyword) {
        return productDao.searchProducts(keyword);
    }

    public LiveData<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice) {
        return productDao.getProductsByPriceRange(minPrice, maxPrice);
    }

    public LiveData<List<Product>> getProductsByLocation(String location) {
        return productDao.getProductsByLocation(location);
    }

    public void incrementViewCount(int productId) {
        new IncrementViewCountAsyncTask(productDao).execute(productId);
    }

    public void incrementFavoriteCount(int productId) {
        new IncrementFavoriteCountAsyncTask(productDao).execute(productId);
    }

    public void decrementFavoriteCount(int productId) {
        new DecrementFavoriteCountAsyncTask(productDao).execute(productId);
    }

    public void updateProductStatusAndBuyer(int productId, String status, int buyerId) {
        new UpdateProductStatusAndBuyerAsyncTask(productDao, status, buyerId).execute(productId);
    }

    public LiveData<List<Product>> getProductsByBuyer(int buyerId) {
        return productDao.getProductsByBuyer(buyerId);
    }

    private static class InsertProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        InsertProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.insert(products[0]);
            return null;
        }
    }

    private static class UpdateProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        UpdateProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.update(products[0]);
            return null;
        }
    }

    private static class DeleteProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        DeleteProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.delete(products[0]);
            return null;
        }
    }

    private static class IncrementViewCountAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ProductDao productDao;

        IncrementViewCountAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Integer... productIds) {
            productDao.incrementViewCount(productIds[0]);
            return null;
        }
    }

    private static class IncrementFavoriteCountAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ProductDao productDao;

        IncrementFavoriteCountAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Integer... productIds) {
            productDao.incrementFavoriteCount(productIds[0]);
            return null;
        }
    }

    private static class DecrementFavoriteCountAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ProductDao productDao;

        DecrementFavoriteCountAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Integer... productIds) {
            productDao.decrementFavoriteCount(productIds[0]);
            return null;
        }
    }

    private static class UpdateProductStatusAndBuyerAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ProductDao productDao;
        private String status;
        private int buyerId;
        UpdateProductStatusAndBuyerAsyncTask(ProductDao productDao, String status, int buyerId) {
            this.productDao = productDao;
            this.status = status;
            this.buyerId = buyerId;
        }
        @Override
        protected Void doInBackground(Integer... productIds) {
            productDao.updateProductStatusAndBuyer(productIds[0], status, buyerId);
            return null;
        }
    }
} 