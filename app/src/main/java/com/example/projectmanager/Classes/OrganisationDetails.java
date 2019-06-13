package com.example.projectmanager.Classes;

public class OrganisationDetails {

    private String orgName,orgAddress,orgEmail,orgMob,orgGSTIN;

    public OrganisationDetails() {
    }

    public OrganisationDetails(String orgName, String orgAddress, String orgEmail, String orgMob, String orgGSTIN) {
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.orgEmail = orgEmail;
        this.orgMob = orgMob;
        this.orgGSTIN = orgGSTIN;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public String getOrgMob() {
        return orgMob;
    }

    public void setOrgMob(String orgMob) {
        this.orgMob = orgMob;
    }

    public String getOrgGSTIN() {
        return orgGSTIN;
    }

    public void setOrgGSTIN(String orgGSTIN) {
        this.orgGSTIN = orgGSTIN;
    }
}
