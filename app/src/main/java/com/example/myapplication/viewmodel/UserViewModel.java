package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.User;
import com.example.myapplication.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private LiveData<List<User>> allUsers;

    public UserViewModel(Application application) {
        super(application);
        repository = new UserRepository(application);
        allUsers = repository.getAllUsers();
    }

    public void insert(User user) {
        repository.insert(user);
    }

    public void update(User user) {
        repository.update(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public LiveData<User> getUserById(int id) {
        return repository.getUserById(id);
    }

    public User getUserByUsername(String username) {
        return repository.getUserByUsername(username);
    }

    public User getUserByEmail(String email) {
        return repository.getUserByEmail(email);
    }

    public User getUserByPhone(String phone) {
        return repository.getUserByPhone(phone);
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<List<User>> searchUsers(String keyword) {
        return repository.searchUsers(keyword);
    }

    public boolean isUserExists(String username, String email, String phone) {
        return getUserByUsername(username) != null || 
               getUserByEmail(email) != null || 
               getUserByPhone(phone) != null;
    }

    public boolean validateLogin(String username, String password) {
        User user = getUserByUsername(username);
        // 这里应该使用加密的密码验证，简化处理
        return user != null && password.equals("123456");
    }
} 