package com.example.tranquoctrungcntt.uchat.Objects;

public class Media {

    private String mediaId;
    private String messageId;
    private String senderId;
    private String receiverId;
    private String groupId;
    private String mediaName;
    private String contentUrl;
    private String thumbContentUrl;

    private int type;
    private int duration;

    private long sendTime;

    public Media() {
    }

    public Media(String mediaId, String messageId, String senderId, String receiverId, String groupId, String mediaName, String contentUrl, String thumbContentUrl, int type, int duration, long sendTime) {
        this.mediaId = mediaId;
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.mediaName = mediaName;
        this.contentUrl = contentUrl;
        this.thumbContentUrl = thumbContentUrl;
        this.type = type;
        this.duration = duration;
        this.sendTime = sendTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getThumbContentUrl() {
        return thumbContentUrl;
    }

    public void setThumbContentUrl(String thumbContentUrl) {
        this.thumbContentUrl = thumbContentUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
}
