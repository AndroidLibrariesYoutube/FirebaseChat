package com.drapps.firebasechat.model;

public class ChatRoom {

    private String user_id, channel_id;

    public ChatRoom() {
    }

    public ChatRoom(String user_id, String channel_id) {
        this.user_id = user_id;
        this.channel_id = channel_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
}
