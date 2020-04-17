package com.example.tranquoctrungcntt.uchat.Objects;

import java.io.Serializable;

public class GroupMember implements Serializable {

    private String memberId;
    private String adderId;
    private String role;

    public GroupMember() {
    }

    public GroupMember(String memberId, String adderId, String role) {
        this.memberId = memberId;
        this.adderId = adderId;
        this.role = role;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAdderId() {
        return adderId;
    }

    public void setAdderId(String adderId) {
        this.adderId = adderId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
