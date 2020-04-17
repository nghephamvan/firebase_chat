package com.example.tranquoctrungcntt.uchat.Objects;

public class EditHistory {
    private String previousContent;
    private long editTime;

    public EditHistory() { }

    public EditHistory(String previousContent, long editTime) {
        this.previousContent = previousContent;
        this.editTime = editTime;
    }

    public String getPreviousContent() {
        return previousContent;
    }

    public void setPreviousContent(String previousContent) {
        this.previousContent = previousContent;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }
}
