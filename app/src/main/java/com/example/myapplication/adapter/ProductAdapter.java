package com.example.myapplication.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ProductDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Product;
import com.example.myapplication.viewmodel.UserViewModel;
import com.example.myapplication.model.User;
import com.example.myapplication.util.ImageLoader;
import com.example.myapplication.util.DebugUtil;
import android.os.Handler;
import android.os.Looper;
import java.util.HashMap;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnItemClickListener listener;
    private UserViewModel userViewModel;
    private Map<Integer, String> sellerNameCache = new HashMap<>();

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> products, UserViewModel userViewModel) {
        this.products = products;
        this.userViewModel = userViewModel;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(newProducts);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvTitle, tvPrice, tvLocation, tvSeller;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.product_image);
            tvTitle = itemView.findViewById(R.id.product_title);
            tvPrice = itemView.findViewById(R.id.product_price);
            tvLocation = itemView.findViewById(R.id.product_location);
            tvSeller = itemView.findViewById(R.id.seller_name);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(products.get(position));
                }
            });
        }

        public void bind(Product product) {
            tvTitle.setText(product.getTitle());
            tvPrice.setText("¥" + product.getPrice());
            tvLocation.setText(product.getLocation() != null ? product.getLocation() : "北京市朝阳区");
            tvSeller.setText("卖家：" + (product.getSellerName() != null ? product.getSellerName() : "用户"));
            
            // 显示商品图片（取images字段的第一张）
            String images = product.getImages();
            android.util.Log.d("ProductAdapter", "商品: " + product.getTitle() + ", images字段: " + images);
            
            if (images != null && !images.isEmpty()) {
                try {
                    JSONArray arr = new JSONArray(images);
                    android.util.Log.d("ProductAdapter", "解析JSON数组成功，长度: " + arr.length());
                    
                    if (arr.length() > 0) {
                        String uriStr = arr.getString(0);
                        android.util.Log.d("ProductAdapter", "图片URL: " + uriStr);
                        
                        if (uriStr != null && !uriStr.isEmpty()) {
                            // 使用改进的图片加载方法
                            android.util.Log.d("ProductAdapter", "开始加载图片: " + uriStr);
                            ImageLoader.loadImage(itemView.getContext(), ivProduct, uriStr, R.drawable.ic_image_placeholder);
                        } else {
                            android.util.Log.w("ProductAdapter", "图片URL为空");
                            ivProduct.setImageResource(R.drawable.ic_image_placeholder);
                        }
                    } else {
                        android.util.Log.w("ProductAdapter", "JSON数组为空");
                        ivProduct.setImageResource(R.drawable.ic_image_placeholder);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProductAdapter", "解析图片数据失败", e);
                    ivProduct.setImageResource(R.drawable.ic_image_placeholder);
                }
            } else {
                android.util.Log.w("ProductAdapter", "images字段为空");
                ivProduct.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        private String formatTime(String timeStamp) {
            try {
                long time = Long.parseLong(timeStamp);
                long currentTime = System.currentTimeMillis();
                long diff = currentTime - time;
                
                if (diff < 60000) { // 小于1分钟
                    return "刚刚";
                } else if (diff < 3600000) { // 小于1小时
                    return (diff / 60000) + "分钟前";
                } else if (diff < 86400000) { // 小于1天
                    return (diff / 3600000) + "小时前";
                } else if (diff < 2592000000L) { // 小于30天
                    return (diff / 86400000) + "天前";
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    return sdf.format(new Date(time));
                }
            } catch (Exception e) {
                return "未知时间";
            }
        }
    }
} 