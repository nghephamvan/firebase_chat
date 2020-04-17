package com.example.tranquoctrungcntt.uchat.DevAppHelper;

public class StickerToUpload {
    private String name;
    private Integer sticker;

    public StickerToUpload(String name, Integer sticker) {
        this.name = name;
        this.sticker = sticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSticker() {
        return sticker;
    }

    public void setSticker(Integer sticker) {
        this.sticker = sticker;
    }
}
