package com.example.myapplication.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.DataGenerator;
import com.example.myapplication.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("搜索商品");
        }

        initViews();
        setupRecyclerView();
        setupSearchView();
        loadAllProducts();
        // 自动获取焦点并弹出软键盘
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.post(() -> {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchView.findFocus(), android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void initViews() {
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        adapter = new ProductAdapter(allProducts, userViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(product -> {
            Toast.makeText(this, "点击了: " + product.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("搜索二手商品...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adapter.updateProducts(allProducts);
                } else {
                    searchProducts(newText);
                }
                return true;
            }
        });
    }

    private void loadAllProducts() {
        try {
            allProducts.clear();
            // 只加载用户发布的商品，不加载测试数据
            loadUserProducts();
            adapter.updateProducts(allProducts);
        } catch (Exception e) {
            Toast.makeText(this, "加载商品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadUserProducts() {
        try {
            SharedPreferences prefs = getSharedPreferences("products", MODE_PRIVATE);
            int productCount = prefs.getInt("product_count", 0);
            
            for (int i = 1; i <= productCount; i++) {
                String title = prefs.getString("product_" + i + "_title", "");
                String description = prefs.getString("product_" + i + "_description", "");
                float price = prefs.getFloat("product_" + i + "_price", 0);
                String category = prefs.getString("product_" + i + "_category", "");
                String location = prefs.getString("product_" + i + "_location", "");
                String seller = prefs.getString("product_" + i + "_seller", "");
                String condition = prefs.getString("product_" + i + "_condition", "");
                long time = prefs.getLong("product_" + i + "_time", 0);
                
                if (!title.isEmpty()) {
                    Product product = new Product(title, description, price, category, 1);
                    product.setLocation(location);
                    product.setSellerName(seller);
                    product.setCondition(condition);
                    product.setCreateTime(String.valueOf(time));
                    allProducts.add(0, product);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载用户商品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void searchProducts(String keyword) {
        try {
            List<Product> filteredProducts = allProducts.stream()
                    .filter(product -> product.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                     product.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                                     product.getCategory().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            adapter.updateProducts(filteredProducts);
        } catch (Exception e) {
            Toast.makeText(this, "搜索失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 