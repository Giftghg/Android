package com.example.myapplication.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.SearchActivity;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.DataGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private TextView searchHint;
    private FloatingActionButton fabFilter;
    private List<Product> allProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        try {
            initViews(view);
            setupRecyclerView();
            setupListeners();
            loadProducts();
        } catch (Exception e) {
            Toast.makeText(getContext(), "首页加载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        searchHint = view.findViewById(R.id.search_hint);
        fabFilter = view.findViewById(R.id.fab_filter);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(allProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        // 搜索栏点击事件 - 跳转到搜索界面
        if (searchHint != null) {
            searchHint.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            });
        }

        if (fabFilter != null) {
            fabFilter.setOnClickListener(v -> {
                Toast.makeText(getContext(), "筛选功能开发中...", Toast.LENGTH_SHORT).show();
            });
        }

        adapter.setOnItemClickListener(product -> {
            Toast.makeText(getContext(), "点击了: " + product.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProducts() {
        try {
            allProducts.clear();
            
            // 加载示例数据
            allProducts.addAll(DataGenerator.generateSampleProducts());
            
            // 加载用户发布的商品
            loadUserProducts();
            
            adapter.updateProducts(allProducts);
        } catch (Exception e) {
            Toast.makeText(getContext(), "加载商品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadUserProducts() {
        try {
            SharedPreferences prefs = getActivity().getSharedPreferences("products", getActivity().MODE_PRIVATE);
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
                    allProducts.add(0, product); // 添加到列表开头
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "加载用户商品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // 每次回到页面时重新加载商品
            loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 