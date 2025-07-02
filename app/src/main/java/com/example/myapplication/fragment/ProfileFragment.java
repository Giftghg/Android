package com.example.myapplication.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.util.DataGenerator;

public class ProfileFragment extends Fragment {
    private TextView tvUsername;
    private Button btnLogout, btnMyProducts, btnSettings;
    private RecyclerView rvMyProducts;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadUserInfo();
        
        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnMyProducts = view.findViewById(R.id.btn_my_products);
        btnSettings = view.findViewById(R.id.btn_settings);
        rvMyProducts = view.findViewById(R.id.rv_my_products);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(DataGenerator.generateSampleProducts());
        rvMyProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyProducts.setAdapter(adapter);
        
        adapter.setOnItemClickListener(product -> {
            Toast.makeText(getContext(), "点击了: " + product.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            logout();
        });

        btnMyProducts.setOnClickListener(v -> {
            // 显示我的商品
            rvMyProducts.setVisibility(View.VISIBLE);
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "设置功能开发中...", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", getActivity().MODE_PRIVATE);
        String username = prefs.getString("username", "用户");
        tvUsername.setText(username);
    }

    private void logout() {
        // 清除登录状态
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // 跳转到登录页面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
} 