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
import com.example.myapplication.util.ApiClient;
import com.example.myapplication.util.ImageLoader;
import com.example.myapplication.util.UploadUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;

import org.json.JSONObject;

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
        
        // 获取当前用户token
        String token = ApiClient.getToken(getContext());
        if (token == null) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示发布中提示
        Toast.makeText(getContext(), "正在发布商品...", Toast.LENGTH_SHORT).show();

        // 如果有图片，先上传图片
        if (!imageUris.isEmpty()) {
            uploadImageAndPublish(title, description, price, category, condition, token);
        } else {
            // 没有图片，直接发布商品
            publishProductWithoutImage(title, description, price, category, condition, token);
        }
    }

    private void uploadImageAndPublish(String title, String description, double price, 
                                     String category, String condition, String token) {
        // 上传第一张图片
        UploadUtil.uploadImage(getContext(), imageUris.get(0), new UploadUtil.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                // 图片上传成功，发布商品
                publishProductWithImage(title, description, price, category, condition, imageUrl, token);
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "图片上传失败: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void publishProductWithImage(String title, String description, double price, 
                                       String category, String condition, String imageUrl, String token) {
        ApiClient.createProduct(title, description, price, category, condition, imageUrl, token, 
            new ApiClient.ApiCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                int productId = result.getInt("product_id");
                                Toast.makeText(getContext(), "商品发布成功！", Toast.LENGTH_SHORT).show();
                                clearInputs();

                                // 发布成功后跳转到商品详情页
                                Intent intent = new Intent(getActivity(), com.example.myapplication.ProductDetailActivity.class);
                                intent.putExtra("product_id", productId);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "发布成功但解析响应失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
    }

    private void publishProductWithoutImage(String title, String description, double price, 
                                          String category, String condition, String token) {
        ApiClient.createProduct(title, description, price, category, condition, null, token, 
            new ApiClient.ApiCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                int productId = result.getInt("product_id");
                                Toast.makeText(getContext(), "商品发布成功！", Toast.LENGTH_SHORT).show();
                                clearInputs();

                                // 发布成功后跳转到商品详情页
                                Intent intent = new Intent(getActivity(), com.example.myapplication.ProductDetailActivity.class);
                                intent.putExtra("product_id", productId);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "发布成功但解析响应失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
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
            try {
                Uri imageUri = imageUris.get(position);
                if (imageUri != null) {
                    // 使用改进的图片加载方法
                    ImageLoader.loadImage(holder.imageView.getContext(), holder.imageView, imageUri, R.drawable.ic_image_placeholder);
                } else {
                    // 设置默认图片
                    holder.imageView.setImageResource(R.drawable.ic_image_placeholder);
                }
            } catch (Exception e) {
                // 设置默认图片
                holder.imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
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