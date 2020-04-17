package com.example.tranquoctrungcntt.uchat.Models;

import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;

import java.io.Serializable;

public class Conversation implements Serializable {

    private String conversationId;
    private Object conversationProfile;
    private boolean isBlockedConversation;
    private boolean isMuteNotifications;
    private Message message;

    public Conversation() { }

    public Conversation(String conversationId, Object conversationProfile, boolean isBlockedConversation, boolean isMuteNotifications, Message message) {
        this.conversationId = conversationId;
        this.conversationProfile = conversationProfile;
        this.isBlockedConversation = isBlockedConversation;
        this.isMuteNotifications = isMuteNotifications;
        this.message = message;
    }

    public boolean isGroupConversation() {
        return message.getGroupId() != null;
    }

    public boolean isSingleConversation() {
        return message.getGroupId() == null;
    }

    public boolean isBlockedConversation() {
        return isBlockedConversation;
    }

    public void setBlockedConversation(boolean blockedConversation) {
        isBlockedConversation = blockedConversation;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Object getConversationProfile() {
        return conversationProfile;
    }

    public void setConversationProfile(Object conversationProfile) {
        this.conversationProfile = conversationProfile;
    }

    public boolean isMuteNotifications() {
        return isMuteNotifications;
    }

    public void setMuteNotifications(boolean muteNotifications) {
        isMuteNotifications = muteNotifications;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUserProfile() {
        return (User) conversationProfile;
    }

    public GroupDetail getGroupDetail() {
        return (GroupDetail) conversationProfile;
    }
}

