package com.example.myapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.model.User;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.Message;

@Database(entities = {User.class, Product.class, Message.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract MessageDao messageDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "second_hand_db"
            ).fallbackToDestructiveMigration()
             .build();
        }
        return instance;
    }
} 