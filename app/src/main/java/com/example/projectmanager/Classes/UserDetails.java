package com.example.projectmanager.Classes;

import com.firebase.ui.auth.data.model.User;

public class UserDetails {
   private  String name,address,mobileNo,title,password,imageUrl,id,sex;

    public UserDetails(String name, String address, String mobileNo, String title, String imageUrl,String Id,String sex) {
        this.name = name;
        this.address = address;
        this.mobileNo = mobileNo;
        this.title = title;
        this.imageUrl = imageUrl;
        this.id=Id;
        this.sex=sex;
    }

    public UserDetails(String name, String title, String imageUrl) {
        this.name = name;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDetails(){

    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
