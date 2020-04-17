package com.example.tranquoctrungcntt.uchat.Models;

import com.example.tranquoctrungcntt.uchat.Objects.User;

import java.io.Serializable;

public class MutualFriend implements Serializable {

    private User friendProfile;
    private boolean isMutualFriend;

    public MutualFriend() {
    }

    public MutualFriend(User friendProfile, boolean isMutualFriend) {
        this.friendProfile = friendProfile;
        this.isMutualFriend = isMutualFriend;
    }

    public User getFriendProfile() {
        return friendProfile;
    }

    public void setFriendProfile(User friendProfile) {
        this.friendProfile = friendProfile;
    }

    public boolean isMutualFriend() {
        return isMutualFriend;
    }

    public void setMutualFriend(boolean mutualFriend) {
        isMutualFriend = mutualFriend;
    }
}
