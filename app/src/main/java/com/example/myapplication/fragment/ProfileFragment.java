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
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.User;
import com.example.myapplication.viewmodel.UserViewModel;
import com.example.myapplication.util.DataGenerator;

import java.util.List;

public class ProfileFragment extends Fragment {
    private TextView tvUsername;
    private Button btnLogout, btnMyProducts, btnSettings;
    private RecyclerView rvMyProducts;
    private ProductAdapter adapter;
    private UserViewModel userViewModel;
    private int loginUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupListeners();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        loginUserId = getLoginUserId();
        loadUserInfo();
        
        // 调试：打印所有用户id和用户名
        new Thread(() -> {
            List<User> allUsers = userViewModel.getAllUsers().getValue();
            if (allUsers != null) {
                for (User user : allUsers) {
                    android.util.Log.d("UserDebug", "id=" + user.getId() + ", username=" + user.getUsername());
                }
            }
        }).start();
        
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        adapter = new ProductAdapter(new java.util.ArrayList<>(), userViewModel);
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
        if (loginUserId == -1) {
            tvUsername.setText("未登录");
            return;
        }
        new Thread(() -> {
            User user = userViewModel.getUserByIdSync(loginUserId);
            getActivity().runOnUiThread(() -> {
                if (user != null) {
                    tvUsername.setText(user.getUsername());
                } else {
                    tvUsername.setText("未知用户");
                }
            });
        }).start();
    }

    private int getLoginUserId() {
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", getActivity().MODE_PRIVATE);
        return prefs.getInt("login_user_id", -1);
    }

    private void logout() {
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("login_user_id");
        editor.apply();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
} 