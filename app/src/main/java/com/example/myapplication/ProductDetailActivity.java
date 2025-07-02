package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.model.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView ivProduct;
    private TextView tvTitle, tvDescription, tvPrice, tvOriginalPrice, tvCategory, tvCondition, tvLocation, tvSeller;
    private Button btnContact, btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        initViews();
        setupToolbar();
        loadProductData();
        setupListeners();
    }

    private void initViews() {
        ivProduct = findViewById(R.id.iv_product);
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_description);
        tvPrice = findViewById(R.id.tv_price);
        tvOriginalPrice = findViewById(R.id.tv_original_price);
        tvCategory = findViewById(R.id.tv_category);
        tvCondition = findViewById(R.id.tv_condition);
        tvLocation = findViewById(R.id.tv_location);
        tvSeller = findViewById(R.id.tv_seller);
        btnContact = findViewById(R.id.btn_contact);
        btnBuy = findViewById(R.id.btn_buy);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("商品详情");
        }
    }

    private void loadProductData() {
        // 从Intent获取商品数据
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            double price = intent.getDoubleExtra("price", 0.0);
            String category = intent.getStringExtra("category");
            String condition = intent.getStringExtra("condition");
            String location = intent.getStringExtra("location");
            String seller = intent.getStringExtra("seller");
            String originalPrice = intent.getStringExtra("original_price");

            tvTitle.setText(title);
            tvDescription.setText(description);
            tvPrice.setText("¥" + price);
            tvCategory.setText("分类：" + category);
            tvCondition.setText("新旧程度：" + condition);
            tvLocation.setText("交易地点：" + location);
            tvSeller.setText("卖家：" + seller);

            if (originalPrice != null && !originalPrice.isEmpty()) {
                tvOriginalPrice.setText("原价：¥" + originalPrice);
                tvOriginalPrice.setVisibility(android.view.View.VISIBLE);
            } else {
                tvOriginalPrice.setVisibility(android.view.View.GONE);
            }

            // 设置默认图片
            ivProduct.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    private void setupListeners() {
        btnContact.setOnClickListener(v -> {
            // 联系卖家（可以跳转到聊天页面或拨打电话）
            Toast.makeText(this, "联系卖家功能开发中...", Toast.LENGTH_SHORT).show();
        });

        btnBuy.setOnClickListener(v -> {
            // 购买商品
            Toast.makeText(this, "购买功能开发中...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 