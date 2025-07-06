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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.SearchActivity;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.DataGenerator;
import com.example.myapplication.util.ApiClient;
import com.example.myapplication.util.DebugUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.myapplication.viewmodel.UserViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private TextView searchHint;
    private FloatingActionButton fabFilter;
    private List<Product> allProducts = new ArrayList<>();
    private String selectedCategory = "全部";
    private String selectedCondition = "全部";

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
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        adapter = new ProductAdapter(allProducts, userViewModel);
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
                showFilterDialog();
            });
        }

        adapter.setOnItemClickListener(product -> {
            // 跳转到商品详情页面
            Intent intent = new Intent(getActivity(), com.example.myapplication.ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });
    }

    private void showFilterDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("筛选商品");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        // 获取筛选控件
        TextView tvCategory = dialogView.findViewById(R.id.tv_category);
        TextView tvCondition = dialogView.findViewById(R.id.tv_condition);

        // 设置当前选中的值
        tvCategory.setText(selectedCategory);
        tvCondition.setText(selectedCondition);

        // 分类选择
        tvCategory.setOnClickListener(v -> {
            String[] categories = {"全部", "数码产品", "服装鞋帽", "图书音像", "家居用品", "运动户外", "美妆护肤", "其他"};
            new android.app.AlertDialog.Builder(getContext())
                .setTitle("选择分类")
                .setItems(categories, (dialog, which) -> {
                    selectedCategory = categories[which];
                    tvCategory.setText(selectedCategory);
                })
                .show();
        });

        // 成色选择
        tvCondition.setOnClickListener(v -> {
            String[] conditions = {"全部", "全新", "九成新", "八成新", "七成新", "六成新", "五成新"};
            new android.app.AlertDialog.Builder(getContext())
                .setTitle("选择成色")
                .setItems(conditions, (dialog, which) -> {
                    selectedCondition = conditions[which];
                    tvCondition.setText(selectedCondition);
                })
                .show();
        });

        builder.setPositiveButton("确定", (dialog, which) -> {
            applyFilter();
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void applyFilter() {
        // 根据筛选条件过滤商品
        List<Product> filteredProducts = allProducts.stream()
            .filter(product -> {
                boolean categoryMatch = "全部".equals(selectedCategory) || 
                                     selectedCategory.equals(product.getCategory());
                boolean conditionMatch = "全部".equals(selectedCondition) || 
                                      selectedCondition.equals(product.getCondition());
                return categoryMatch && conditionMatch;
            })
            .collect(java.util.stream.Collectors.toList());
        
        adapter.updateProducts(filteredProducts);
        
        String filterText = "筛选结果: " + filteredProducts.size() + " 件商品";
        if (!"全部".equals(selectedCategory)) {
            filterText += " (分类: " + selectedCategory + ")";
        }
        if (!"全部".equals(selectedCondition)) {
            filterText += " (成色: " + selectedCondition + ")";
        }
        
        Toast.makeText(getContext(), filterText, Toast.LENGTH_SHORT).show();
    }

    private void loadProducts() {
        try {
            allProducts.clear();
            // 从API获取商品列表
            loadProductsFromApi();
        } catch (Exception e) {
            Toast.makeText(getContext(), "加载商品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadProductsFromApi() {
        ApiClient.getProducts(new ApiClient.ApiCallback<List<JSONObject>>() {
            @Override
            public void onSuccess(List<JSONObject> products) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            allProducts.clear();
                            for (JSONObject productJson : products) {
                                Product product = new Product(
                                    productJson.getString("title"),
                                    productJson.getString("description"),
                                    (float) productJson.getDouble("price"),
                                    productJson.getString("category"),
                                    productJson.getInt("seller_id")
                                );
                                product.setId(productJson.getInt("id"));
                                product.setSellerName(productJson.getString("seller_name"));
                                product.setCondition(productJson.getString("condition"));
                                product.setCreateTime(productJson.getString("created_at"));
                                product.setLocation("北京市朝阳区"); // 默认位置
                                
                                // 处理图片URL
                                String imageUrl = "";
                                if (productJson.has("image_url") && !productJson.isNull("image_url")) {
                                    imageUrl = productJson.getString("image_url");
                                    if (!imageUrl.isEmpty()) {
                                        product.setImages("[" + imageUrl + "]");
                                        // 调试：测试图片URL
                                        DebugUtil.testImageUrl(getContext(), imageUrl);
                                        DebugUtil.logProductImageInfo(product.getTitle(), imageUrl, product.getImages());
                                    }
                                }
                                allProducts.add(product);
                            }
                            adapter.updateProducts(allProducts);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "解析商品数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            android.util.Log.e("HomeFragment", "解析商品数据失败", e);
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