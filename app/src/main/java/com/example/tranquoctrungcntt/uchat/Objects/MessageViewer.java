package com.example.tranquoctrungcntt.uchat.Objects;

public class MessageViewer {
    private String viewerId;
    private long viewTime;

    public MessageViewer() {
    }

    public MessageViewer(String viewerId, long viewTime) {
        this.viewerId = viewerId;
        this.viewTime = viewTime;
    }

    public String getViewerId() {
        return viewerId;
    }

    public void setViewerId(String viewerId) {
        this.viewerId = viewerId;
    }

    public long getViewTime() {
        return viewTime;
    }

    public void setViewTime(long viewTime) {
        this.viewTime = viewTime;
    }
}
