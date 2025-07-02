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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> products) {
        this.products = products;
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
                
                // 跳转到商品详情页面
                Product product = products.get(position);
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("title", product.getTitle());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("category", product.getCategory());
                intent.putExtra("condition", product.getCondition() != null ? product.getCondition() : "9成新");
                intent.putExtra("location", product.getLocation() != null ? product.getLocation() : "北京市朝阳区");
                intent.putExtra("seller", product.getSellerName() != null ? product.getSellerName() : "卖家");
                intent.putExtra("original_price", product.getOriginalPrice());
                v.getContext().startActivity(intent);
            });
        }

        public void bind(Product product) {
            tvTitle.setText(product.getTitle());
            tvPrice.setText("¥" + product.getPrice());
            tvLocation.setText(product.getLocation() != null ? product.getLocation() : "北京市朝阳区");
            tvSeller.setText(product.getSellerName() != null ? product.getSellerName() : "卖家");
            
            // 设置默认图片
            ivProduct.setImageResource(R.drawable.ic_launcher_foreground);
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