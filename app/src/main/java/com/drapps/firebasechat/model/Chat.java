package com.drapps.firebasechat.model;

import com.google.firebase.database.Exclude;

import java.util.Map;

public class Chat {
    private String text, sender_id, receiver_id, receiver_device_token;
    private int type;
    private String timestamp;


    public Chat() {
    }

    public Chat(String text, String sender_id, String receiver_id, String receiver_device_token, int type, String timestamp) {
        this.text = text;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.receiver_device_token = receiver_device_token;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver_device_token() {
        return receiver_device_token;
    }

    public void setReceiver_device_token(String receiver_device_token) {
        this.receiver_device_token = receiver_device_token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}