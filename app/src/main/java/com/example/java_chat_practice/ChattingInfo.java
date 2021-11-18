package com.example.java_chat_practice;

public class ChattingInfo {
    private String text;
    private String senderUID;
    private String receiverUID;
    public String getText() {
        return text;
    }

    public ChattingInfo() {
    }

    public ChattingInfo(String text, String senderUID) {
        this.text = text;
        this.senderUID = senderUID;

    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }
}
