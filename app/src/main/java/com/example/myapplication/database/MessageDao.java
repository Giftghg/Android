package com.example.myapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Update
    void update(Message message);

    @Delete
    void delete(Message message);

    @Query("SELECT * FROM messages WHERE id = :id")
    LiveData<Message> getMessageById(int id);

    @Query("SELECT * FROM messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY createTime ASC")
    LiveData<List<Message>> getMessagesBetweenUsers(int userId1, int userId2);

    @Query("SELECT * FROM messages WHERE receiverId = :userId AND status = 'sent' ORDER BY createTime DESC")
    LiveData<List<Message>> getUnreadMessages(int userId);

    @Query("UPDATE messages SET status = 'read' WHERE receiverId = :userId AND status = 'sent'")
    void markMessagesAsRead(int userId);

    @Query("SELECT * FROM messages WHERE productId = :productId ORDER BY createTime DESC")
    LiveData<List<Message>> getMessagesByProduct(int productId);

    @Query("SELECT DISTINCT productId FROM messages WHERE senderId = :userId OR receiverId = :userId")
    LiveData<List<Integer>> getProductIdsForUser(int userId);
} 