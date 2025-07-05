package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://10.34.68.221:5000/api"; // 使用您的电脑IP
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public static void register(String username, String password, String email, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "开始注册用户: " + username);
                JSONObject requestBody = new JSONObject();
                requestBody.put("username", username);
                requestBody.put("password", password);
                requestBody.put("email", email);

                Log.d(TAG, "请求URL: " + BASE_URL + "/register");
                Log.d(TAG, "请求体: " + requestBody.toString());
                
                String response = makeRequest("POST", "/register", requestBody.toString(), null);
                Log.d(TAG, "服务器响应: " + response);
                
                JSONObject jsonResponse = new JSONObject(response);
                callback.onSuccess(jsonResponse);
            } catch (Exception e) {
                Log.e(TAG, "注册失败", e);
                callback.onError("注册失败: " + e.getMessage());
            }
        });
    }

    public static void login(String username, String password, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("username", username);
                requestBody.put("password", password);

                String response = makeRequest("POST", "/login", requestBody.toString(), null);
                JSONObject jsonResponse = new JSONObject(response);
                callback.onSuccess(jsonResponse);
            } catch (Exception e) {
                Log.e(TAG, "登录失败", e);
                callback.onError("登录失败: " + e.getMessage());
            }
        });
    }

    public static void getProducts(ApiCallback<List<JSONObject>> callback) {
        executor.execute(() -> {
            try {
                String response = makeRequest("GET", "/products", null, null);
                JSONObject jsonResponse = new JSONObject(response);
                
                // 检查是否有分页结构
                if (jsonResponse.has("products")) {
                    JSONArray productsArray = jsonResponse.getJSONArray("products");
                    List<JSONObject> products = new ArrayList<>();
                    for (int i = 0; i < productsArray.length(); i++) {
                        products.add(productsArray.getJSONObject(i));
                    }
                    callback.onSuccess(products);
                } else {
                    // 如果没有分页结构，直接解析为商品数组
                    List<JSONObject> products = new ArrayList<>();
                    try {
                        JSONArray productsArray = new JSONArray(response);
                        for (int i = 0; i < productsArray.length(); i++) {
                            products.add(productsArray.getJSONObject(i));
                        }
                    } catch (Exception e) {
                        // 如果不是JSONArray，则作为单个商品对象处理
                        products.add(jsonResponse);
                    }
                    callback.onSuccess(products);
                }
            } catch (Exception e) {
                Log.e(TAG, "获取商品列表失败", e);
                callback.onError("获取商品列表失败: " + e.getMessage());
            }
        });
    }

    public static void createProduct(String title, String description, double price, 
                                   String category, String condition, String imageUrl, 
                                   String token, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("title", title);
                requestBody.put("description", description);
                requestBody.put("price", price);
                requestBody.put("category", category);
                requestBody.put("condition", condition);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    requestBody.put("image_url", imageUrl);
                }

                String response = makeRequest("POST", "/products", requestBody.toString(), token);
                JSONObject jsonResponse = new JSONObject(response);
                callback.onSuccess(jsonResponse);
            } catch (Exception e) {
                Log.e(TAG, "发布商品失败", e);
                callback.onError("发布商品失败: " + e.getMessage());
            }
        });
    }

    public static void getProductDetail(int productId, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                String response = makeRequest("GET", "/products/" + productId, null, null);
                JSONObject jsonResponse = new JSONObject(response);
                callback.onSuccess(jsonResponse);
            } catch (Exception e) {
                Log.e(TAG, "获取商品详情失败", e);
                callback.onError("获取商品详情失败: " + e.getMessage());
            }
        });
    }

    public static void getUserProfile(String token, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                String response = makeRequest("GET", "/user/profile", null, token);
                JSONObject jsonResponse = new JSONObject(response);
                callback.onSuccess(jsonResponse);
            } catch (Exception e) {
                Log.e(TAG, "获取用户信息失败", e);
                callback.onError("获取用户信息失败: " + e.getMessage());
            }
        });
    }

    public static void getUserProducts(String token, ApiCallback<List<JSONObject>> callback) {
        executor.execute(() -> {
            try {
                String response = makeRequest("GET", "/user/products", null, token);
                JSONObject jsonResponse = new JSONObject(response);
                
                // 检查是否有分页结构
                if (jsonResponse.has("products")) {
                    JSONArray productsArray = jsonResponse.getJSONArray("products");
                    List<JSONObject> products = new ArrayList<>();
                    for (int i = 0; i < productsArray.length(); i++) {
                        products.add(productsArray.getJSONObject(i));
                    }
                    callback.onSuccess(products);
                } else {
                    // 如果没有分页结构，直接解析为商品数组
                    List<JSONObject> products = new ArrayList<>();
                    try {
                        JSONArray productsArray = new JSONArray(response);
                        for (int i = 0; i < productsArray.length(); i++) {
                            products.add(productsArray.getJSONObject(i));
                        }
                    } catch (Exception e) {
                        // 如果不是JSONArray，则作为单个商品对象处理
                        products.add(jsonResponse);
                    }
                    callback.onSuccess(products);
                }
            } catch (Exception e) {
                Log.e(TAG, "获取用户商品失败", e);
                callback.onError("获取用户商品失败: " + e.getMessage());
            }
        });
    }

    public static void getCategories(ApiCallback<List<String>> callback) {
        executor.execute(() -> {
            try {
                String response = makeRequest("GET", "/categories", null, null);
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray categoriesArray = jsonResponse.getJSONArray("categories");
                
                List<String> categories = new ArrayList<>();
                for (int i = 0; i < categoriesArray.length(); i++) {
                    categories.add(categoriesArray.getString(i));
                }
                callback.onSuccess(categories);
            } catch (Exception e) {
                Log.e(TAG, "获取分类失败", e);
                callback.onError("获取分类失败: " + e.getMessage());
            }
        });
    }

    public static void getConditions(ApiCallback<List<String>> callback) {
        executor.execute(() -> {
            try {
                String response = makeRequest("GET", "/conditions", null, null);
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray conditionsArray = jsonResponse.getJSONArray("conditions");
                
                List<String> conditions = new ArrayList<>();
                for (int i = 0; i < conditionsArray.length(); i++) {
                    conditions.add(conditionsArray.getString(i));
                }
                callback.onSuccess(conditions);
            } catch (Exception e) {
                Log.e(TAG, "获取成色失败", e);
                callback.onError("获取成色失败: " + e.getMessage());
            }
        });
    }

    public static void getProductsByCategory(String category, ApiCallback<List<JSONObject>> callback) {
        executor.execute(() -> {
            try {
                String endpoint = "/products";
                if (!"全部".equals(category)) {
                    endpoint += "?category=" + java.net.URLEncoder.encode(category, "UTF-8");
                }
                String response = makeRequest("GET", endpoint, null, null);
                JSONObject jsonResponse = new JSONObject(response);
                
                // 检查是否有分页结构
                if (jsonResponse.has("products")) {
                    JSONArray productsArray = jsonResponse.getJSONArray("products");
                    List<JSONObject> products = new ArrayList<>();
                    for (int i = 0; i < productsArray.length(); i++) {
                        products.add(productsArray.getJSONObject(i));
                    }
                    callback.onSuccess(products);
                } else {
                    // 如果没有分页结构，直接解析为商品数组
                    List<JSONObject> products = new ArrayList<>();
                    try {
                        JSONArray productsArray = new JSONArray(response);
                        for (int i = 0; i < productsArray.length(); i++) {
                            products.add(productsArray.getJSONObject(i));
                        }
                    } catch (Exception e) {
                        // 如果不是JSONArray，则作为单个商品对象处理
                        products.add(jsonResponse);
                    }
                    callback.onSuccess(products);
                }
            } catch (Exception e) {
                Log.e(TAG, "获取分类商品失败", e);
                callback.onError("获取分类商品失败: " + e.getMessage());
            }
        });
    }

    public static String makeRequest(String method, String endpoint, String requestBody, String token) throws IOException {
        String fullUrl = BASE_URL + endpoint;
        Log.d(TAG, "发起请求: " + method + " " + fullUrl);
        
        URL url = new URL(fullUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setConnectTimeout(10000); // 10秒连接超时
        connection.setReadTimeout(10000);    // 10秒读取超时
        
        if (token != null) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        if (requestBody != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                Log.d(TAG, "发送请求体: " + requestBody);
            }
        }

        int responseCode = connection.getResponseCode();
        Log.d(TAG, "响应码: " + responseCode);
        
        InputStream inputStream = responseCode >= 200 && responseCode < 300 
            ? connection.getInputStream() 
            : connection.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        String responseText = response.toString();
        Log.d(TAG, "响应内容: " + responseText);

        if (responseCode >= 200 && responseCode < 300) {
            return responseText;
        } else {
            throw new IOException("HTTP " + responseCode + ": " + responseText);
        }
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getString("api_token", null);
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("api_token", token).apply();
    }

    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("api_token").apply();
    }
} 