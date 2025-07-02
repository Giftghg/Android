package com.example.myapplication;

import android.app.Application;

public class SecondHandApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 暂时注释掉数据库初始化，避免启动问题
        // AppDatabase database = AppDatabase.getInstance(this);
        // new InitializeDataTask().execute();
    }
} 