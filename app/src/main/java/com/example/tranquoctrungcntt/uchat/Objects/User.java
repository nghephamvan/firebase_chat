package com.example.tranquoctrungcntt.uchat.Objects;

import java.io.Serializable;

public class User implements Serializable {

    private String userId;
    private String email;
    private String password;
    private String name;
    private String status;
    private String dateofbirth;
    private String gender;
    private String joinedDate;

    private String avatarUrl;
    private String thumbAvatarUrl;
    private String qrCodeUrl;
    private String thumbQRCodeUrl;

    private boolean isVerified;
    private long lastSeen;

    public User(String userId, String email, String password, String name, String status, String dateofbirth, String gender, String joinedDate, String avatarUrl, String thumbAvatarUrl, String qrCodeUrl, String thumbQRCodeUrl, boolean isVerified, long lastSeen) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = status;
        this.dateofbirth = dateofbirth;
        this.gender = gender;
        this.joinedDate = joinedDate;
        this.avatarUrl = avatarUrl;
        this.thumbAvatarUrl = thumbAvatarUrl;
        this.qrCodeUrl = qrCodeUrl;
        this.thumbQRCodeUrl = thumbQRCodeUrl;
        this.isVerified = isVerified;
        this.lastSeen = lastSeen;
    }

    public User() { }

    public User(User user) {
        this.userId = user.userId;
        this.email = user.email;
        this.password = user.password;
        this.name = user.name;
        this.status = user.status;
        this.dateofbirth = user.dateofbirth;
        this.gender = user.gender;
        this.joinedDate = user.joinedDate;
        this.avatarUrl = user.avatarUrl;
        this.thumbAvatarUrl = user.thumbAvatarUrl;
        this.qrCodeUrl = user.qrCodeUrl;
        this.thumbQRCodeUrl = user.thumbQRCodeUrl;
        this.isVerified = user.isVerified;
        this.lastSeen = user.lastSeen;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getThumbQRCodeUrl() {
        return thumbQRCodeUrl;
    }

    public void setThumbQRCodeUrl(String thumbQRCodeUrl) {
        this.thumbQRCodeUrl = thumbQRCodeUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getThumbAvatarUrl() {
        return thumbAvatarUrl;
    }

    public void setThumbAvatarUrl(String thumbAvatarUrl) {
        this.thumbAvatarUrl = thumbAvatarUrl;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }


}
