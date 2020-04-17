package com.example.tranquoctrungcntt.uchat.Models;

import com.example.tranquoctrungcntt.uchat.Objects.User;

public class GroupMemberModel {

    private User member;
    private User adder;
    private String role;

    public GroupMemberModel() {
    }

    public GroupMemberModel(User member, User adder, String role) {
        this.member = member;
        this.adder = adder;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public User getAdder() {
        return adder;
    }

    public void setAdder(User adder) {
        this.adder = adder;
    }
}
