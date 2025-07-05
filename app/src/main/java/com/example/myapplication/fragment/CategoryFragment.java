package com.example.myapplication.fragment;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.ApiClient;
import com.example.myapplication.viewmodel.UserViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryFragment extends Fragment {
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private String selectedCategory = "全部";
    private List<Product> allProducts = new ArrayList<>();

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
        loadAllProducts();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        adapter = new ProductAdapter(new ArrayList<>(), userViewModel);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(product -> {
            // 跳转到商品详情页面
            Intent intent = new Intent(getActivity(), com.example.myapplication.ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
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

    private void loadAllProducts() {
        try {
            allProducts.clear();
            // 从API获取所有商品
            ApiClient.getProducts(new ApiClient.ApiCallback<List<JSONObject>>() {
                @Override
                public void onSuccess(List<JSONObject> productsJson) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                for (JSONObject productJson : productsJson) {
                                    Product product = parseProductFromJson(productJson);
                                    allProducts.add(product);
                                }
                                // 加载完成后，按当前选中的分类显示
                                loadProductsByCategory(selectedCategory);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "解析商品数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                android.util.Log.e("CategoryFragment", "解析商品数据失败", e);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "加载商品失败: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "加载商品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadProductsByCategory(String category) {
        List<Product> filteredProducts;
        
        if ("全部".equals(category)) {
            filteredProducts = new ArrayList<>(allProducts);
        } else {
            filteredProducts = allProducts.stream()
                    .filter(product -> category.equals(product.getCategory()))
                    .collect(Collectors.toList());
        }
        adapter.updateProducts(filteredProducts);
    }
} 