package com.example.java_chat_practice;

import java.util.ArrayList;

public class ChattingRoomInfo {
    private ArrayList<String> userlist;

    public ChattingRoomInfo(ArrayList<String> userlist) {

        this.userlist = userlist;
    }

    public ArrayList<String> getUserlist() {
        return userlist;
    }
}
