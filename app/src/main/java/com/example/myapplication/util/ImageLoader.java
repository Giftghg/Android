package com.example.myapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    /**
     * 安全地加载图片到ImageView
     * @param context 上下文
     * @param imageView 目标ImageView
     * @param imageUrl 图片URL或URI字符串
     * @param defaultResourceId 默认图片资源ID
     */
    public static void loadImage(Context context, ImageView imageView, String imageUrl, int defaultResourceId) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.d(TAG, "图片URL为空，使用默认图片");
            imageView.setImageResource(defaultResourceId);
            return;
        }

        Log.d(TAG, "开始加载图片: " + imageUrl);
        
        try {
            Uri imageUri = Uri.parse(imageUrl);
            Log.d(TAG, "解析URI成功: " + imageUri);
            loadImage(context, imageView, imageUri, defaultResourceId);
        } catch (Exception e) {
            Log.e(TAG, "解析图片URL失败: " + imageUrl, e);
            imageView.setImageResource(defaultResourceId);
        }
    }

    /**
     * 安全地加载图片到ImageView
     * @param context 上下文
     * @param imageView 目标ImageView
     * @param imageUri 图片URI
     * @param defaultResourceId 默认图片资源ID
     */
    public static void loadImage(Context context, ImageView imageView, Uri imageUri, int defaultResourceId) {
        if (imageUri == null) {
            Log.d(TAG, "图片URI为空，使用默认图片");
            imageView.setImageResource(defaultResourceId);
            return;
        }

        String uriString = imageUri.toString();
        Log.d(TAG, "加载图片URI: " + uriString);

        try {
            // 检查是否是content URI格式
            if (uriString.startsWith("content://")) {
                Log.d(TAG, "检测到content URI，使用特殊处理");
                loadContentUri(context, imageView, imageUri, defaultResourceId);
            } else if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                Log.d(TAG, "检测到网络图片URI: " + uriString);
                // 对于网络图片，使用异步加载
                loadNetworkImage(context, imageView, imageUri, defaultResourceId);
            } else if (uriString.startsWith("file://")) {
                Log.d(TAG, "检测到文件URI");
                // 对于文件URI，直接设置
                imageView.setImageURI(imageUri);
            } else {
                Log.d(TAG, "未知URI格式，尝试直接设置");
                // 对于其他格式，尝试直接设置
                imageView.setImageURI(imageUri);
            }
        } catch (Exception e) {
            Log.e(TAG, "加载图片失败: " + imageUri, e);
            imageView.setImageResource(defaultResourceId);
        }
    }

    /**
     * 加载content URI图片，使用多种方法尝试
     */
    private static void loadContentUri(Context context, ImageView imageView, Uri imageUri, int defaultResourceId) {
        try {
            Log.d(TAG, "尝试加载content URI: " + imageUri);
            
            // 方法1：尝试直接使用setImageURI
            try {
                imageView.setImageURI(imageUri);
                Log.d(TAG, "方法1成功：直接使用setImageURI");
                return;
            } catch (Exception e) {
                Log.w(TAG, "方法1失败：直接使用setImageURI", e);
            }
            
            // 方法2：使用ContentResolver读取并解码为Bitmap
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    Log.d(TAG, "成功打开content URI输入流");
                    
                    // 解码图片并设置到ImageView
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    
                    if (bitmap != null) {
                        Log.d(TAG, "成功解码图片，尺寸: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                        imageView.setImageBitmap(bitmap);
                        return;
                    } else {
                        Log.w(TAG, "图片解码失败");
                    }
                } else {
                    Log.w(TAG, "无法打开content URI输入流");
                }
            } catch (Exception e) {
                Log.w(TAG, "方法2失败：使用ContentResolver读取", e);
            }
            
            // 方法3：复制到临时文件然后加载
            try {
                File tempFile = copyContentUriToTempFile(context, imageUri);
                if (tempFile != null && tempFile.exists()) {
                    Uri fileUri = Uri.fromFile(tempFile);
                    imageView.setImageURI(fileUri);
                    Log.d(TAG, "方法3成功：复制到临时文件");
                    return;
                }
            } catch (Exception e) {
                Log.w(TAG, "方法3失败：复制到临时文件", e);
            }
            
            // 所有方法都失败，使用默认图片
            Log.w(TAG, "所有方法都失败，使用默认图片");
            imageView.setImageResource(defaultResourceId);
            
        } catch (SecurityException e) {
            Log.e(TAG, "权限不足，无法访问content URI: " + imageUri, e);
            imageView.setImageResource(defaultResourceId);
        } catch (Exception e) {
            Log.e(TAG, "处理content URI失败: " + imageUri, e);
            imageView.setImageResource(defaultResourceId);
        }
    }

    /**
     * 将content URI复制到临时文件
     */
    private static File copyContentUriToTempFile(Context context, Uri contentUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(contentUri);
            if (inputStream == null) {
                Log.w(TAG, "无法打开content URI输入流");
                return null;
            }

            // 创建临时文件
            File tempFile = File.createTempFile("image_", ".jpg", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            // 复制数据
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            Log.d(TAG, "成功复制到临时文件: " + tempFile.getAbsolutePath());
            return tempFile;
        } catch (Exception e) {
            Log.e(TAG, "复制content URI到临时文件失败", e);
            return null;
        }
    }

    /**
     * 测试content URI是否可访问
     */
    public static boolean testContentUri(Context context, Uri uri) {
        if (uri == null) {
            Log.d(TAG, "URI为空，无法测试");
            return false;
        }

        String uriString = uri.toString();
        Log.d(TAG, "测试content URI: " + uriString);

        if (!uriString.startsWith("content://")) {
            Log.d(TAG, "不是content URI，跳过测试");
            return true;
        }

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                inputStream.close();
                Log.d(TAG, "content URI测试成功");
                return true;
            } else {
                Log.w(TAG, "content URI测试失败：无法打开输入流");
                return false;
            }
        } catch (SecurityException e) {
            Log.e(TAG, "content URI测试失败：权限不足", e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "content URI测试失败", e);
            return false;
        }
    }

    /**
     * 异步加载网络图片
     */
    private static void loadNetworkImage(Context context, ImageView imageView, Uri imageUri, int defaultResourceId) {
        new Thread(() -> {
            try {
                Log.d(TAG, "开始异步加载网络图片: " + imageUri);
                
                URL url = new URL(imageUri.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "网络图片响应码: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    
                    if (bitmap != null) {
                        Log.d(TAG, "网络图片加载成功，尺寸: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                        context.getMainExecutor().execute(() -> {
                            imageView.setImageBitmap(bitmap);
                        });
                    } else {
                        Log.w(TAG, "网络图片解码失败");
                        context.getMainExecutor().execute(() -> {
                            imageView.setImageResource(defaultResourceId);
                        });
                    }
                } else {
                    Log.w(TAG, "网络图片请求失败，响应码: " + responseCode);
                    context.getMainExecutor().execute(() -> {
                        imageView.setImageResource(defaultResourceId);
                    });
                }
                
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "加载网络图片失败: " + imageUri, e);
                context.getMainExecutor().execute(() -> {
                    imageView.setImageResource(defaultResourceId);
                });
            }
        }).start();
    }

    /**
     * 检查URI是否有效
     * @param context 上下文
     * @param uri 要检查的URI
     * @return 是否有效
     */
    public static boolean isValidImageUri(Context context, Uri uri) {
        if (uri == null) {
            Log.d(TAG, "URI为空，无效");
            return false;
        }

        String uriString = uri.toString();
        Log.d(TAG, "检查URI有效性: " + uriString);

        try {
            if (uriString.startsWith("content://")) {
                // 对于content URI，尝试打开输入流
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    inputStream.close();
                    Log.d(TAG, "content URI有效");
                    return true;
                }
                Log.w(TAG, "content URI无效");
                return false;
            } else if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                Log.d(TAG, "网络URI，假设有效");
                return true;
            } else if (uriString.startsWith("file://")) {
                Log.d(TAG, "文件URI，假设有效");
                return true;
            } else {
                Log.d(TAG, "其他格式URI，假设有效");
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "检查URI有效性失败: " + uri, e);
            return false;
        }
    }
} 