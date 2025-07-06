package com.example.myapplication.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UploadUtil {
    private static final String TAG = "UploadUtil";
    private static final String BASE_URL = "http://10.0.2.2:5000/api"; // 模拟器访问宿主机
    private static final String UPLOAD_ENDPOINT = "/upload_image";

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public static void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "开始上传图片: " + imageUri);
                
                // 创建连接
                URL url = new URL(BASE_URL + UPLOAD_ENDPOINT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                
                // 设置multipart/form-data边界
                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                
                // 获取图片输入流
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    callback.onError("无法读取图片文件");
                    return;
                }
                
                // 构建multipart请求体
                OutputStream outputStream = connection.getOutputStream();
                
                // 写入文件部分
                outputStream.write(("--" + boundary + "\r\n").getBytes());
                outputStream.write(("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n").getBytes());
                outputStream.write("Content-Type: image/jpeg\r\n\r\n".getBytes());
                
                // 写入图片数据
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                // 写入结束边界
                outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                
                // 获取响应
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "上传响应码: " + responseCode);
                
                InputStream responseStream;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    responseStream = connection.getInputStream();
                } else {
                    responseStream = connection.getErrorStream();
                }
                
                // 读取响应内容
                StringBuilder response = new StringBuilder();
                byte[] responseBuffer = new byte[1024];
                int responseBytesRead;
                while ((responseBytesRead = responseStream.read(responseBuffer)) != -1) {
                    response.append(new String(responseBuffer, 0, responseBytesRead, StandardCharsets.UTF_8));
                }
                responseStream.close();
                
                Log.d(TAG, "上传响应内容: " + response.toString());
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 解析响应获取图片URL
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String imageUrl = jsonResponse.getString("image_url");
                    Log.d(TAG, "图片上传成功，URL: " + imageUrl);
                    callback.onSuccess(imageUrl);
                } else {
                    Log.e(TAG, "图片上传失败，响应码: " + responseCode + ", 响应: " + response.toString());
                    callback.onError("图片上传失败: HTTP " + responseCode);
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "图片上传异常", e);
                callback.onError("图片上传失败: " + e.getMessage());
            }
        }).start();
    }
} 