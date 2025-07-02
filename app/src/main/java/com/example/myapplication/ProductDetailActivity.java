package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

import com.example.myapplication.model.Product;
import com.example.myapplication.viewmodel.ProductViewModel;
import android.content.SharedPreferences;
import com.example.myapplication.viewmodel.UserViewModel;
import com.example.myapplication.model.User;
import android.os.Handler;
import android.os.Looper;

public class ProductDetailActivity extends AppCompatActivity {
    private RecyclerView rvProductImages;
    private ProductImagesAdapter imagesAdapter;
    private List<Uri> imageUris = new ArrayList<>();
    private TextView tvTitle, tvDescription, tvPrice, tvOriginalPrice, tvCategory, tvCondition, tvLocation, tvSeller;
    private Button btnContact, btnBuy;
    private ProductViewModel productViewModel;
    private UserViewModel userViewModel;
    private int productId = -1;
    private int sellerId = -1;
    private int buyerId = -1;
    private String status = "在售";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        initViews();
        setupToolbar();
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        buyerId = getLoginUserId();
        loadProductData();
        setupListeners();
    }

    private void initViews() {
        rvProductImages = findViewById(R.id.rv_product_images);
        imagesAdapter = new ProductImagesAdapter(imageUris);
        rvProductImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProductImages.setAdapter(imagesAdapter);
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
            productId = intent.getIntExtra("product_id", -1);
            sellerId = intent.getIntExtra("seller_id", -1);
            status = intent.getStringExtra("status");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            double price = intent.getDoubleExtra("price", 0.0);
            String category = intent.getStringExtra("category");
            String condition = intent.getStringExtra("condition");
            String location = intent.getStringExtra("location");
            String seller = intent.getStringExtra("seller");
            String originalPrice = intent.getStringExtra("original_price");
            String images = intent.getStringExtra("images");

            tvTitle.setText(title);
            tvDescription.setText(description);
            tvPrice.setText("¥" + price);
            tvCategory.setText("分类：" + category);
            tvCondition.setText("新旧程度：" + condition);
            tvLocation.setText("交易地点：" + location);
            tvSeller.setText("卖家：" + sellerId);
            android.util.Log.d("ProductDebug", "商品详情页sellerId=" + sellerId);
            // 异步查用户名
            new Thread(() -> {
                User user = userViewModel.getUserByIdSync(sellerId);
                String name = (user != null) ? user.getUsername() : "未知用户";
                android.util.Log.d("ProductDebug", "通过sellerId查到的用户名=" + name);
                new Handler(Looper.getMainLooper()).post(() -> tvSeller.setText("卖家：" + name));
            }).start();

            if (originalPrice != null && !originalPrice.isEmpty()) {
                tvOriginalPrice.setText("原价：¥" + originalPrice);
                tvOriginalPrice.setVisibility(android.view.View.VISIBLE);
            } else {
                tvOriginalPrice.setVisibility(android.view.View.GONE);
            }

            imageUris.clear();
            if (images != null && !images.isEmpty()) {
                try {
                    JSONArray arr = new JSONArray(images);
                    for (int i = 0; i < arr.length(); i++) {
                        imageUris.add(Uri.parse(arr.getString(i)));
                    }
                } catch (JSONException e) {
                    // ignore
                }
            }
            imagesAdapter.notifyDataSetChanged();

            // 控制购买按钮显示逻辑
            if (buyerId == sellerId || !"在售".equals(status)) {
                btnBuy.setEnabled(false);
                btnBuy.setText("不可购买");
            } else {
                btnBuy.setEnabled(true);
                btnBuy.setText("购买");
            }
        }
    }

    private void setupListeners() {
        btnContact.setOnClickListener(v -> {
            // 联系卖家（可以跳转到聊天页面或拨打电话）
            Toast.makeText(this, "联系卖家功能开发中...", Toast.LENGTH_SHORT).show();
        });

        btnBuy.setOnClickListener(v -> {
            if (productId == -1 || buyerId == -1) {
                Toast.makeText(this, "数据异常，无法购买", Toast.LENGTH_SHORT).show();
                return;
            }
            productViewModel.updateProductStatusAndBuyer(productId, "已售", buyerId);
            btnBuy.setEnabled(false);
            btnBuy.setText("已购买");
            Toast.makeText(this, "购买成功！", Toast.LENGTH_SHORT).show();
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

    private int getLoginUserId() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getInt("login_user_id", -1);
    }

    // 新增图片适配器内部类
    private static class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.ImageViewHolder> {
        private List<Uri> imageUris;
        public ProductImagesAdapter(List<Uri> imageUris) { this.imageUris = imageUris; }
        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            int size = (int) (parent.getResources().getDisplayMetrics().density * 180);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            return new ImageViewHolder(imageView);
        }
        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.imageView.setImageURI(imageUris.get(position));
        }
        @Override
        public int getItemCount() { return imageUris.size(); }
        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
} 