package com.example.myapplication.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.model.Product;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PublishFragment extends Fragment {
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText priceInput;
    private TextInputEditText locationInput;
    private Spinner categorySpinner;
    private Button publishButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        
        initViews(view);
        setupCategorySpinner();
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        titleInput = view.findViewById(R.id.et_title);
        descriptionInput = view.findViewById(R.id.et_description);
        priceInput = view.findViewById(R.id.et_price);
        locationInput = view.findViewById(R.id.et_location);
        categorySpinner = view.findViewById(R.id.spinner_category);
        publishButton = view.findViewById(R.id.btn_publish);
    }

    private void setupCategorySpinner() {
        String[] categories = {"数码产品", "服装鞋帽", "图书音像", "家居用品", "运动户外", "美妆护肤", "其他"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        publishButton.setOnClickListener(v -> {
            if (validateInput()) {
                publishProduct();
            }
        });
    }

    private boolean validateInput() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleInput.setError("请输入商品标题");
            return false;
        }

        if (description.isEmpty()) {
            descriptionInput.setError("请输入商品描述");
            return false;
        }

        if (price.isEmpty()) {
            priceInput.setError("请输入价格");
            return false;
        }

        try {
            double priceValue = Double.parseDouble(price);
            if (priceValue <= 0) {
                priceInput.setError("价格必须大于0");
                return false;
            }
        } catch (NumberFormatException e) {
            priceInput.setError("请输入有效的价格");
            return false;
        }

        if (location.isEmpty()) {
            locationInput.setError("请输入位置");
            return false;
        }

        return true;
    }

    private void publishProduct() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        double price = Double.parseDouble(priceInput.getText().toString().trim());
        String location = locationInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        
        // 获取当前用户
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", getActivity().MODE_PRIVATE);
        String sellerName = prefs.getString("username", "用户");

        // 创建商品对象
        Product product = new Product(title, description, price, category, 1);
        product.setLocation(location);
        product.setSellerName(sellerName);
        product.setCondition("9成新");

        // 保存商品到SharedPreferences（简化版本，实际应该保存到数据库）
        saveProduct(product);

        Toast.makeText(getContext(), "商品发布成功！", Toast.LENGTH_SHORT).show();
        clearInputs();
    }

    private void saveProduct(Product product) {
        SharedPreferences prefs = getActivity().getSharedPreferences("products", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // 获取已保存的商品数量
        int productCount = prefs.getInt("product_count", 0);
        productCount++;
        
        // 保存商品信息
        editor.putString("product_" + productCount + "_title", product.getTitle());
        editor.putString("product_" + productCount + "_description", product.getDescription());
        editor.putFloat("product_" + productCount + "_price", (float) product.getPrice());
        editor.putString("product_" + productCount + "_category", product.getCategory());
        editor.putString("product_" + productCount + "_location", product.getLocation());
        editor.putString("product_" + productCount + "_seller", product.getSellerName());
        editor.putString("product_" + productCount + "_condition", product.getCondition());
        editor.putLong("product_" + productCount + "_time", System.currentTimeMillis());
        
        // 更新商品数量
        editor.putInt("product_count", productCount);
        editor.apply();
    }

    private void clearInputs() {
        titleInput.setText("");
        descriptionInput.setText("");
        priceInput.setText("");
        locationInput.setText("");
        categorySpinner.setSelection(0);
    }
} 