package com.example.projectmanager.Classes;

public class UserSite {

    private String id,progress,name;

    public UserSite() {
    }

    public UserSite(String id, String progress,String name) {
        this.id = id;
        this.progress = progress;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}
