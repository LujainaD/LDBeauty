package com.lujaina.ldbeauty.Models;

public class AddInfoModel {
    private String salonOwnerId;
    private String infoId;
    private String title;
    private String body;
    private String backgroundColor;
    private boolean isShrink = true;


    public String getSalonOwnerId() {
        return salonOwnerId;
    }

    public void setSalonOwnerId(String salonOwnerId) {
        this.salonOwnerId = salonOwnerId;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    public boolean isShrink() {
        return isShrink;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }
}
