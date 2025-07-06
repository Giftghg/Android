package com.example.myapplication.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DebugUtil {
    private static final String TAG = "DebugUtil";

    /**
     * 测试网络图片URL是否可访问
     */
    public static void testImageUrl(Context context, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.w(TAG, "图片URL为空");
            return;
        }

        Log.d(TAG, "测试图片URL: " + imageUrl);

        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "图片URL响应码: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "图片URL可访问");
                    if (context != null) {
                        context.getMainExecutor().execute(() -> {
                            Toast.makeText(context, "图片URL可访问: " + imageUrl, Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.w(TAG, "图片URL不可访问，响应码: " + responseCode);
                    if (context != null) {
                        context.getMainExecutor().execute(() -> {
                            Toast.makeText(context, "图片URL不可访问: " + imageUrl, Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "测试图片URL失败: " + imageUrl, e);
                if (context != null) {
                    context.getMainExecutor().execute(() -> {
                        Toast.makeText(context, "测试图片URL失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    /**
     * 测试Flask后端连接
     */
    public static void testFlaskConnection(Context context) {
        String testUrl = "http://10.0.2.2:5000/api/products";
        Log.d(TAG, "测试Flask连接: " + testUrl);

        new Thread(() -> {
            try {
                URL url = new URL(testUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Flask连接响应码: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 读取响应内容
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    Log.d(TAG, "Flask连接成功，响应: " + response.toString());
                    if (context != null) {
                        context.getMainExecutor().execute(() -> {
                            Toast.makeText(context, "Flask连接成功", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.w(TAG, "Flask连接失败，响应码: " + responseCode);
                    if (context != null) {
                        context.getMainExecutor().execute(() -> {
                            Toast.makeText(context, "Flask连接失败，响应码: " + responseCode, Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "测试Flask连接失败", e);
                if (context != null) {
                    context.getMainExecutor().execute(() -> {
                        Toast.makeText(context, "Flask连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    /**
     * 打印商品图片信息
     */
    public static void logProductImageInfo(String productTitle, String imageUrl, String imagesField) {
        Log.d(TAG, "=== 商品图片调试信息 ===");
        Log.d(TAG, "商品标题: " + productTitle);
        Log.d(TAG, "image_url字段: " + imageUrl);
        Log.d(TAG, "images字段: " + imagesField);
        Log.d(TAG, "=========================");
    }
} 