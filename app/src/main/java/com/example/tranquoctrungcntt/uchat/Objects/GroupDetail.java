package com.example.tranquoctrungcntt.uchat.Objects;

import java.io.Serializable;
import java.util.Map;

public class GroupDetail implements Serializable {

    private String groupId;
    private String adminId;
    private String groupName;
    private String groupDescription;
    private String groupAvatar;
    private String groupThumbAvatar;
    private String createdDate;

    private Map<String, GroupMember> member;

    private boolean isCensorMode;

    public GroupDetail() {
    }

    public GroupDetail(String groupId, String adminId, String groupName, String groupDescription, String groupAvatar, String groupThumbAvatar, String createdDate, Map<String, GroupMember> member, boolean isCensorMode) {
        this.groupId = groupId;
        this.adminId = adminId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupAvatar = groupAvatar;
        this.groupThumbAvatar = groupThumbAvatar;
        this.createdDate = createdDate;
        this.member = member;
        this.isCensorMode = isCensorMode;
    }

    public Map<String, GroupMember> getMember() {
        return member;
    }

    public void setMember(Map<String, GroupMember> member) {
        this.member = member;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getGroupThumbAvatar() {
        return groupThumbAvatar;
    }

    public void setGroupThumbAvatar(String groupThumbAvatar) {
        this.groupThumbAvatar = groupThumbAvatar;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isCensorMode() {
        return isCensorMode;
    }

    public void setCensorMode(boolean censorMode) {
        isCensorMode = censorMode;
    }
}
