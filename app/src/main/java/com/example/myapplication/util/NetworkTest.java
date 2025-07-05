package com.example.myapplication.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkTest {
    private static final String TAG = "NetworkTest";
    private static final String BASE_URL = "http://10.34.68.221:5000/api";
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public interface NetworkTestCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public static void testConnection(Context context, NetworkTestCallback callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "开始测试网络连接...");
                
                // 测试1: 基本连接
                testBasicConnection(callback);
                
                // 测试2: API健康检查
                testApiHealth(callback);
                
                // 测试3: 注册接口
                testRegisterEndpoint(callback);
                
            } catch (Exception e) {
                Log.e(TAG, "网络测试失败", e);
                callback.onError("网络测试失败: " + e.getMessage());
            }
        });
    }

    private static void testBasicConnection(NetworkTestCallback callback) {
        try {
            Log.d(TAG, "测试1: 基本网络连接");
            String testUrl = BASE_URL.replace("/api", "");
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "基本连接测试 - 响应码: " + responseCode);
            
            if (responseCode == 200) {
                callback.onSuccess("✓ 基本网络连接正常");
            } else {
                callback.onError("✗ 基本网络连接失败，响应码: " + responseCode);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "基本连接测试失败", e);
            callback.onError("✗ 基本网络连接失败: " + e.getMessage());
        }
    }

    private static void testApiHealth(NetworkTestCallback callback) {
        try {
            Log.d(TAG, "测试2: API健康检查");
            String response = ApiClient.makeRequest("GET", "/health", null, null);
            Log.d(TAG, "API健康检查响应: " + response);
            callback.onSuccess("✓ API服务正常");
            
        } catch (Exception e) {
            Log.e(TAG, "API健康检查失败", e);
            callback.onError("✗ API服务异常: " + e.getMessage());
        }
    }

    private static void testRegisterEndpoint(NetworkTestCallback callback) {
        try {
            Log.d(TAG, "测试3: 注册接口测试");
            JSONObject testData = new JSONObject();
            testData.put("username", "test_user");
            testData.put("password", "test_password");
            testData.put("email", "test@example.com");
            
            String response = ApiClient.makeRequest("POST", "/register", testData.toString(), null);
            Log.d(TAG, "注册接口测试响应: " + response);
            
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("error")) {
                // 如果返回错误，说明接口正常工作（用户名已存在等）
                callback.onSuccess("✓ 注册接口正常（预期错误: " + jsonResponse.getString("error") + "）");
            } else {
                callback.onSuccess("✓ 注册接口正常");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "注册接口测试失败", e);
            callback.onError("✗ 注册接口异常: " + e.getMessage());
        }
    }

    public static void showNetworkStatus(Context context) {
        testConnection(context, new NetworkTestCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "网络测试成功: " + message);
                // 在主线程显示Toast
                if (context != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "网络测试失败: " + error);
                // 在主线程显示Toast
                if (context != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
} 