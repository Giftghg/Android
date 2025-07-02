package com.example.myapplication.util;

import com.example.myapplication.model.Product;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {
    
    public static List<User> generateSampleUsers() {
        List<User> users = new ArrayList<>();
        
        users.add(new User("张三", "zhangsan@example.com", "13800138001"));
        users.add(new User("李四", "lisi@example.com", "13800138002"));
        users.add(new User("王五", "wangwu@example.com", "13800138003"));
        
        return users;
    }
    
    public static List<Product> generateSampleProducts() {
        List<Product> products = new ArrayList<>();
        
        // 数码产品
        products.add(new Product("iPhone 13 Pro Max 256GB", 
                "99新，无划痕，电池健康度95%，配件齐全，原装充电器数据线", 
                5999.0, "数码产品", 1));
        products.get(0).setOriginalPrice("7999");
        products.get(0).setCondition("9成新");
        products.get(0).setLocation("北京市朝阳区");
        products.get(0).setSellerName("张三");
        
        // 服装
        products.add(new Product("Nike Air Jordan 1 高帮篮球鞋", 
                "全新，尺码42，经典配色，适合收藏或日常穿着", 
                899.0, "服装鞋帽", 2));
        products.get(1).setOriginalPrice("1299");
        products.get(1).setCondition("全新");
        products.get(1).setLocation("上海市浦东新区");
        products.get(1).setSellerName("李四");
        
        // 图书
        products.add(new Product("Java核心技术 第11版", 
                "正版图书，9成新，无笔记，适合学习Java编程", 
                45.0, "图书音像", 3));
        products.get(2).setOriginalPrice("89");
        products.get(2).setCondition("9成新");
        products.get(2).setLocation("广州市天河区");
        products.get(2).setSellerName("王五");
        
        // 家居用品
        products.add(new Product("宜家书桌 白色", 
                "8成新，尺寸120x60cm，搬家急售，自提", 
                150.0, "家居用品", 1));
        products.get(3).setOriginalPrice("299");
        products.get(3).setCondition("8成新");
        products.get(3).setLocation("深圳市南山区");
        products.get(3).setSellerName("张三");
        
        // 运动户外
        products.add(new Product("迪卡侬山地自行车", 
                "7成新，24速，适合入门骑行，送头盔和锁", 
                680.0, "运动户外", 2));
        products.get(4).setOriginalPrice("1299");
        products.get(4).setCondition("7成新");
        products.get(4).setLocation("杭州市西湖区");
        products.get(4).setSellerName("李四");
        
        return products;
    }
} 