package com.example.tranquoctrungcntt.uchat.Models;

import java.io.Serializable;

public class StickerToShow implements Serializable {

    private String name;
    private String stickerUrl;

    public StickerToShow(String name, String stickerUrl) {
        this.name = name;
        this.stickerUrl = stickerUrl;
    }

    public StickerToShow() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }
}
