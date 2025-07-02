package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSwitchMode;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        setupListeners();
        
        // 检查是否已登录
        if (isLoggedIn()) {
            startMainActivity();
        }
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

        // 简单的登录验证（实际应用中应该连接数据库）
        if (username.equals("admin") && password.equals("123456")) {
            saveLoginState(username);
            startMainActivity();
        } else {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
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

        // 简单的注册逻辑（实际应用中应该保存到数据库）
        saveLoginState(username);
        Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
        startMainActivity();
    }

    private void saveLoginState(String username) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("username", username);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} 