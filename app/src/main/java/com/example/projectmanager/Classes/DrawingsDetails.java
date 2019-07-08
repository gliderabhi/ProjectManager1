package com.example.projectmanager.Classes;

public class DrawingsDetails {

    private String title,remarks, picUrl,type;

    public DrawingsDetails(String title, String remarks, String picUrl, String type) {
        this.title = title;
        this.remarks = remarks;
        this.picUrl = picUrl;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DrawingsDetails() {
    }


    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
