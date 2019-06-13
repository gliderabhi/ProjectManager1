package com.example.projectmanager.Classes;

import java.util.Date;

public class Site {

    private String name, client,siteLoc,priority,creator,progress;
    private String start, end,id;

    public Site(String name, String client, String siteLoc, String priority, String start, String end,String creator,String progress,String id) {
        this.name = name;
        this.client = client;
        this.siteLoc = siteLoc;
        this.priority = priority;
        this.start = start;
        this.end = end;
        this.creator=creator;
        this.progress=progress;
        this.id=id;
    }

    public Site() {
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSiteLoc() {
        return siteLoc;
    }

    public void setSiteLoc(String siteLoc) {
        this.siteLoc = siteLoc;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
