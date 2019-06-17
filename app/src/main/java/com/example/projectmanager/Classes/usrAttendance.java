package com.example.projectmanager.Classes;

public class usrAttendance {
    private String id,name,workDuration;

    public usrAttendance() {
    }

    public usrAttendance(String id, String name, String workDuration) {
        this.id = id;
        this.name = name;
        this.workDuration = workDuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(String workDuration) {
        this.workDuration = workDuration;
    }
}
