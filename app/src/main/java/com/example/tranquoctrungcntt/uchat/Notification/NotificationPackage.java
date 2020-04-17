package com.example.tranquoctrungcntt.uchat.Notification;

public class NotificationPackage {

    private DataToSend data;
    private String to;

    public NotificationPackage(DataToSend data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationPackage() {
    }

    public DataToSend getData() {
        return data;
    }

    public void setData(DataToSend data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
