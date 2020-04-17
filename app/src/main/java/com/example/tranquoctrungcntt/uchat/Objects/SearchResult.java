package com.example.tranquoctrungcntt.uchat.Objects;

public class SearchResult {


    private String searchId;
    private String searchName;
    private String searchAvatar;
    private long searchTime;
    private boolean isSearchGroup;

    public SearchResult(String searchId, String searchName, String searchAvatar, long searchTime, boolean isSearchGroup) {
        this.searchId = searchId;
        this.searchName = searchName;
        this.searchAvatar = searchAvatar;
        this.searchTime = searchTime;
        this.isSearchGroup = isSearchGroup;
    }

    public SearchResult() {
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchAvatar() {
        return searchAvatar;
    }

    public void setSearchAvatar(String searchAvatar) {
        this.searchAvatar = searchAvatar;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    public boolean isSearchGroup() {
        return isSearchGroup;
    }

    public void setSearchGroup(boolean searchGroup) {
        isSearchGroup = searchGroup;
    }
}
