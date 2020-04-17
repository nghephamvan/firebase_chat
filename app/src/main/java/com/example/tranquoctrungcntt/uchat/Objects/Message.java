package com.example.tranquoctrungcntt.uchat.Objects;


import java.util.Map;

public class Message {


    private String messageId;
    private String senderId;
    private String receiverId;
    private String groupId;
    private String content;
    private String status;

    private long sendTime;
    private long seenTime;

    private int type;
    private int notificationId;
    private int callDuration;
    private String sticker;

    private boolean isForwardedMessage;

    private Map<String, Boolean> relatedUserId;
    private Map<String, MessageViewer> messageViewer;
    private Map<String, EditHistory> editHistory;

    public Message() { }

    public Message(String messageId,
                   String senderId,
                   String receiverId,
                   String groupId,
                   String content,
                   String status,
                   long sendTime,
                   long seenTime,
                   int type,
                   int notificationId,
                   int callDuration,
                   String sticker,
                   boolean isForwardedMessage,
                   Map<String, Boolean> relatedUserId,
                   Map<String, MessageViewer> messageViewer,
                   Map<String, EditHistory> editHistory) {

        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.content = content;
        this.status = status;
        this.sendTime = sendTime;
        this.seenTime = seenTime;
        this.type = type;
        this.notificationId = notificationId;
        this.callDuration = callDuration;
        this.sticker = sticker;
        this.isForwardedMessage = isForwardedMessage;
        this.relatedUserId = relatedUserId;
        this.messageViewer = messageViewer;
        this.editHistory = editHistory;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getSeenTime() {
        return seenTime;
    }

    public void setSeenTime(long seenTime) {
        this.seenTime = seenTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(int callDuration) {
        this.callDuration = callDuration;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public boolean isForwardedMessage() {
        return isForwardedMessage;
    }

    public void setForwardedMessage(boolean forwardedMessage) {
        isForwardedMessage = forwardedMessage;
    }

    public Map<String, Boolean> getRelatedUserId() {
        return relatedUserId;
    }

    public void setRelatedUserId(Map<String, Boolean> relatedUserId) {
        this.relatedUserId = relatedUserId;
    }

    public Map<String, MessageViewer> getMessageViewer() {
        return messageViewer;
    }

    public void setMessageViewer(Map<String, MessageViewer> messageViewer) {
        this.messageViewer = messageViewer;
    }

    public Map<String, EditHistory> getEditHistory() {
        return editHistory;
    }

    public void setEditHistory(Map<String, EditHistory> editHistory) {
        this.editHistory = editHistory;
    }
}
