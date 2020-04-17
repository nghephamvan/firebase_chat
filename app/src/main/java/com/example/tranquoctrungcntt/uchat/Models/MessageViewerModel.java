package com.example.tranquoctrungcntt.uchat.Models;

import com.example.tranquoctrungcntt.uchat.Objects.User;

public class MessageViewerModel {

    private User user;
    private long seenTime;

    public MessageViewerModel(User user, long seenTime) {
        this.user = user;
        this.seenTime = seenTime;
    }


    public MessageViewerModel() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getSeenTime() {
        return seenTime;
    }

    public void setSeenTime(long seenTime) {
        this.seenTime = seenTime;
    }
}
