package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.model.User;
import com.example.myapplication.viewmodel.UserViewModel;
import com.example.myapplication.util.ApiClient;
import com.example.myapplication.util.NetworkTest;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSwitchMode;
    private boolean isLoginMode = true;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getLoginUserId() != -1 && ApiClient.getToken(this) != null) {
            startMainActivity();
        }
        initViews();
        setupListeners();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSwitchMode = findViewById(R.id.tv_switch_mode);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (isLoginMode) {
                login();
            } else {
                register();
            }
        });

        tvSwitchMode.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateUI();
        });

        // 添加长按测试网络连接
        btnLogin.setOnLongClickListener(v -> {
            testNetworkConnection();
            return true;
        });
    }

    private void updateUI() {
        if (isLoginMode) {
            btnLogin.setText("登录");
            tvSwitchMode.setText("没有账号？点击注册");
        } else {
            btnLogin.setText("注册");
            tvSwitchMode.setText("已有账号？点击登录");
        }
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载提示
        Toast.makeText(this, "正在登录...", Toast.LENGTH_SHORT).show();

        ApiClient.login(username, password, new ApiClient.ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                runOnUiThread(() -> {
                    try {
                        String token = result.getString("token");
                        int userId = result.getInt("user_id");
                        String username = result.getString("username");
                        
                        // 保存登录信息
                        ApiClient.saveToken(LoginActivity.this, token);
                        saveLoginUserId(userId, username);
                        
                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        startMainActivity();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "登录响应解析失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "密码长度至少6位", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载提示
        Toast.makeText(this, "正在注册...", Toast.LENGTH_SHORT).show();

        // 生成一个简单的邮箱（实际应用中应该让用户输入）
        String email = username + "@example.com";

        ApiClient.register(username, password, email, new ApiClient.ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                runOnUiThread(() -> {
                    try {
                        String token = result.getString("token");
                        int userId = result.getInt("user_id");
                        String username = result.getString("username");
                        
                        // 保存登录信息
                        ApiClient.saveToken(LoginActivity.this, token);
                        saveLoginUserId(userId, username);
                        
                        Toast.makeText(LoginActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        startMainActivity();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "注册响应解析失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void saveLoginUserId(int userId, String username) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit()
            .putInt("login_user_id", userId)
            .putString("username", username)
            .commit();
    }

    private int getLoginUserId() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getInt("login_user_id", -1);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void testNetworkConnection() {
        Toast.makeText(this, "正在测试网络连接...", Toast.LENGTH_SHORT).show();
        
        NetworkTest.testConnection(this, new NetworkTest.NetworkTestCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
} 