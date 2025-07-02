package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.DataGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryFragment extends Fragment {
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private String selectedCategory = "全部";

    private final String[] categories = {
        "全部", "数码产品", "服装鞋帽", "图书音像", "家居用品", "运动户外", "美妆护肤", "其他"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupCategoryButtons(view);
        loadProductsByCategory(selectedCategory);
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(DataGenerator.generateSampleProducts());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(product -> {
            Toast.makeText(getContext(), "点击了: " + product.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCategoryButtons(View view) {
        GridLayout categoryGrid = view.findViewById(R.id.category_grid);
        
        for (String category : categories) {
            TextView categoryButton = new TextView(getContext());
            categoryButton.setText(category);
            categoryButton.setPadding(32, 16, 32, 16);
            categoryButton.setBackgroundResource(R.drawable.category_button_background);
            categoryButton.setTextColor(getResources().getColor(android.R.color.black));
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(8, 8, 8, 8);
            categoryButton.setLayoutParams(params);
            
            categoryButton.setOnClickListener(v -> {
                selectedCategory = category;
                loadProductsByCategory(category);
                updateCategoryButtonSelection(categoryGrid, categoryButton);
            });
            
            categoryGrid.addView(categoryButton);
        }
    }

    private void updateCategoryButtonSelection(GridLayout categoryGrid, TextView selectedButton) {
        for (int i = 0; i < categoryGrid.getChildCount(); i++) {
            View child = categoryGrid.getChildAt(i);
            if (child instanceof TextView) {
                TextView button = (TextView) child;
                if (button == selectedButton) {
                    button.setBackgroundResource(R.drawable.category_button_selected);
                    button.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    button.setBackgroundResource(R.drawable.category_button_background);
                    button.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        }
    }

    private void loadProductsByCategory(String category) {
        List<Product> allProducts = DataGenerator.generateSampleProducts();
        List<Product> filteredProducts;
        
        if ("全部".equals(category)) {
            filteredProducts = allProducts;
        } else {
            filteredProducts = allProducts.stream()
                    .filter(product -> category.equals(product.getCategory()))
                    .collect(Collectors.toList());
        }
        
        adapter.updateProducts(filteredProducts);
    }
} 