package com.example.myapplication.activity;

import android.content.Intent;
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
import com.example.myapplication.util.ApiClient;
import com.example.myapplication.viewmodel.UserViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

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
            // 跳转到商品详情页面
            Intent intent = new Intent(this, com.example.myapplication.ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
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
            // 从API获取所有商品
            ApiClient.getProducts(new ApiClient.ApiCallback<List<JSONObject>>() {
                @Override
                public void onSuccess(List<JSONObject> productsJson) {
                    runOnUiThread(() -> {
                        try {
                            for (JSONObject productJson : productsJson) {
                                Product product = parseProductFromJson(productJson);
                                allProducts.add(product);
                            }
                            adapter.updateProducts(allProducts);
                        } catch (Exception e) {
                            Toast.makeText(SearchActivity.this, "解析商品数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            android.util.Log.e("SearchActivity", "解析商品数据失败", e);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(SearchActivity.this, "加载商品失败: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "加载商品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Product parseProductFromJson(JSONObject productJson) throws Exception {
        int id = productJson.getInt("id");
        String title = productJson.getString("title");
        String description = productJson.getString("description");
        double price = productJson.getDouble("price");
        String category = productJson.getString("category");
        String condition = productJson.getString("condition");
        int sellerId = productJson.getInt("seller_id");
        String sellerName = productJson.getString("seller_name");
        String status = productJson.optString("status", "");
        String imageUrl = productJson.optString("image_url", "");
        String createTime = productJson.getString("created_at");

        Product product = new Product(title, description, (float) price, category, sellerId);
        product.setId(id);
        product.setCondition(condition);
        product.setSellerName(sellerName);
        product.setStatus(status);
        product.setCreateTime(createTime);
        product.setLocation("北京市朝阳区"); // 默认位置

        // 处理图片
        if (!imageUrl.isEmpty()) {
            product.setImages("[" + imageUrl + "]");
        }

        return product;
    }

    private void searchProducts(String keyword) {
        try {
            List<Product> filteredProducts = allProducts.stream()
                    .filter(product -> product.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                     product.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                                     product.getCategory().toLowerCase().contains(keyword.toLowerCase()) ||
                                     (product.getSellerName() != null && product.getSellerName().toLowerCase().contains(keyword.toLowerCase())))
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