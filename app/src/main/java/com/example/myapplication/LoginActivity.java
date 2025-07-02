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
        if (getLoginUserId() != -1) {
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
        new Thread(() -> {
            User user = userViewModel.getUserByUsername(username);
            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(user.getPassword())) {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    saveLoginUserId(user.getId(), user.getUsername());
                    startMainActivity();
                }
            });
        }).start();
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
        new Thread(() -> {
            User existUser = userViewModel.getUserByUsername(username);
            runOnUiThread(() -> {
                if (existUser != null) {
                    Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
                } else {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    userViewModel.insert(newUser);
                    // 延迟200ms后查找新用户并用ID更新用户名
                    new Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        new Thread(() -> {
                            User loginUser = userViewModel.getUserByUsername(username);
                            if (loginUser != null) {
                                // 更新用户名为ID字符串
                                loginUser.setUsername(String.valueOf(loginUser.getId()));
                                userViewModel.update(loginUser);
                            }
                            runOnUiThread(() -> {
                                if (loginUser != null) {
                                    saveLoginUserId(loginUser.getId(), loginUser.getUsername());
                                    Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    startMainActivity();
                                } else {
                                    Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).start();
                    }, 200);
                }
            });
        }).start();
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
} 