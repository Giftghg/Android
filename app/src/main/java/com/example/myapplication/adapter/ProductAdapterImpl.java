package com.example.myapplication.adapter;

import com.example.myapplication.model.Product;
import com.example.myapplication.viewmodel.UserViewModel;

import java.util.List;

public class ProductAdapterImpl extends ProductAdapter {
    public ProductAdapterImpl(List<Product> products, UserViewModel userViewModel) {
        super(products, userViewModel);
    }
}
