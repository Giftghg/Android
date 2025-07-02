package com.example.myapplication.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private String messageType; // text, image, system
    private String status; // sent, delivered, read
    private String createTime;
    private int productId; // 关联的商品ID

    public Message() {}

    @Ignore
    public Message(int senderId, int receiverId, String content, int productId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.productId = productId;
        this.messageType = "text";
        this.status = "sent";
        this.createTime = String.valueOf(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
} 