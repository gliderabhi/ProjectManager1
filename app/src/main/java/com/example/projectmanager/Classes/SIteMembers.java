package com.example.projectmanager.Classes;

public class SIteMembers {

    private String id,name,priority,designation,siteName,status,imgUrl;

    public SIteMembers(String id, String name, String priority,String designation,String siteName,String status,String imgUrl) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.status=status;
        this.designation=designation;
        this.siteName=siteName;
        this.imgUrl=imgUrl;
     }

    public SIteMembers(String name, String designation, String imgUrl) {
        this.name = name;
        this.designation = designation;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public SIteMembers() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
