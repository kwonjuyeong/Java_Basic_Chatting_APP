package com.example.java_chat_practice;

import java.io.Serializable;

class FriendInfo {
    private String name;
    private String email;
    private String uid;

    public FriendInfo() {
    }

    public FriendInfo(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
