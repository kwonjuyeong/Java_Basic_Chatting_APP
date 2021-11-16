package com.example.java_chat_practice;

import java.io.Serializable;

class FriendInfo {
    private String name;
    private String email;

    public FriendInfo() {
    }

    public FriendInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
