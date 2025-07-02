package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> images;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public ImageAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(images.get(position), position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(String imagePath, int position) {
            // 设置默认图片
            imageView.setImageResource(R.drawable.ic_image_placeholder);

            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(position);
                }
            });
        }
    }
} 