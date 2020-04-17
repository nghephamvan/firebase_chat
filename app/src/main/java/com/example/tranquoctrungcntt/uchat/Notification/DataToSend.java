package com.example.tranquoctrungcntt.uchat.Notification;

public class DataToSend {

    private String receiverId;
    private String notificationSenderId;
    private String groupId;
    private String messageId;
    private String title;
    private String avatar;
    private String content;
    private int notificationType;
    private int notificationId;


    public DataToSend(String receiverId, String notificationSenderId, String groupId, String messageId, String title, String avatar, String content, int notificationType, int notificationId) {
        this.receiverId = receiverId;
        this.notificationSenderId = notificationSenderId;
        this.groupId = groupId;
        this.messageId = messageId;
        this.title = title;
        this.avatar = avatar;
        this.content = content;
        this.notificationType = notificationType;
        this.notificationId = notificationId;
    }

    public DataToSend() { }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getNotificationSenderId() {
        return notificationSenderId;
    }

    public void setNotificationSenderId(String notificationSenderId) {
        this.notificationSenderId = notificationSenderId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
