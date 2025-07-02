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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;

public class PublishFragment extends Fragment {
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText priceInput;
    private TextInputEditText locationInput;
    private Spinner categorySpinner;
    private Spinner conditionSpinner;
    private RecyclerView rvImages;
    private Button publishButton;
    private Button addImageButton;
    private List<Uri> imageUris = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private static final int REQUEST_CODE_PICK_IMAGES = 1001;

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
        conditionSpinner = view.findViewById(R.id.spinner_condition);
        rvImages = view.findViewById(R.id.rv_images);
        addImageButton = view.findViewById(R.id.btn_add_image);
        publishButton = view.findViewById(R.id.btn_publish);
        // 初始化图片RecyclerView
        imageAdapter = new ImageAdapter(imageUris);
        rvImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(imageAdapter);
    }

    private void setupCategorySpinner() {
        String[] categories = {"数码产品", "服装鞋帽", "图书音像", "家居用品", "运动户外", "美妆护肤", "其他"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        // 新旧程度Spinner
        String[] conditions = {"全新", "9成新", "8成新", "7成新", "6成新", "5成新及以下"};
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, conditions);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
    }

    private void setupListeners() {
        publishButton.setOnClickListener(v -> {
            if (validateInput()) {
                publishProduct();
            }
        });
        addImageButton.setOnClickListener(v -> {
            pickImages();
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

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_PICK_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    imageUris.add(data.getData());
                }
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void publishProduct() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        double price = Double.parseDouble(priceInput.getText().toString().trim());
        String location = locationInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String condition = conditionSpinner.getSelectedItem().toString();
        // 获取当前用户
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", getActivity().MODE_PRIVATE);
        int userId = prefs.getInt("login_user_id", -1);
        String sellerName = prefs.getString("username", "用户");
        android.util.Log.d("UserDebug", "当前登录用户id=" + userId + ", 用户名=" + sellerName);
        // 创建商品对象
        android.util.Log.d("ProductDebug", "发布商品sellerId=" + userId);
        Product product = new Product(title, description, price, category, userId);
        product.setLocation(location);
        product.setSellerName(sellerName);
        product.setCondition(condition);
        // 保存图片URI为JSON字符串
        if (!imageUris.isEmpty()) {
            List<String> uriStrings = new ArrayList<>();
            for (Uri uri : imageUris) {
                uriStrings.add(uri.toString());
            }
            product.setImages(new org.json.JSONArray(uriStrings).toString());
        }
        // 保存商品到SharedPreferences（简化版本，实际应该保存到数据库）
        saveProduct(product);
        Toast.makeText(getContext(), "商品发布成功！", Toast.LENGTH_SHORT).show();
        clearInputs();

        // 新增：发布后直接跳转到详情页
        Intent intent = new Intent(getActivity(), com.example.myapplication.ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("seller_id", product.getSellerId());
        intent.putExtra("status", product.getStatus());
        intent.putExtra("title", product.getTitle());
        intent.putExtra("description", product.getDescription());
        intent.putExtra("price", product.getPrice());
        intent.putExtra("category", product.getCategory());
        intent.putExtra("condition", product.getCondition());
        intent.putExtra("location", product.getLocation());
        intent.putExtra("seller", product.getSellerName());
        intent.putExtra("original_price", product.getOriginalPrice());
        intent.putExtra("images", product.getImages());
        startActivity(intent);
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
        editor.putString("product_" + productCount + "_images", product.getImages());
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
        conditionSpinner.setSelection(0);
        imageUris.clear();
        imageAdapter.notifyDataSetChanged();
    }

    private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private List<Uri> imageUris;
        public ImageAdapter(List<Uri> imageUris) { this.imageUris = imageUris; }
        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            int size = (int) (parent.getResources().getDisplayMetrics().density * 80);
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