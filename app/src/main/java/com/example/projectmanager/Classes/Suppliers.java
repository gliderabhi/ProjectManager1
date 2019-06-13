package com.example.projectmanager.Classes;

public class Suppliers {

    String SupName,address,pan,GST,paymentDetails,ACNo,IFSC,BankName;

    Suppliers(){

    }

    public Suppliers(String supName, String address, String pan, String GST, String paymentDetails, String ACNo, String IFSC, String bankName) {
        SupName = supName;
        this.address = address;
        this.pan = pan;
        this.GST = GST;
        this.paymentDetails = paymentDetails;
        this.ACNo = ACNo;
        this.IFSC = IFSC;
        BankName = bankName;
    }

    public String getSupName() {
        return SupName;
    }

    public void setSupName(String supName) {
        SupName = supName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getGST() {
        return GST;
    }

    public void setGST(String GST) {
        this.GST = GST;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getACNo() {
        return ACNo;
    }

    public void setACNo(String ACNo) {
        this.ACNo = ACNo;
    }

    public String getIFSC() {
        return IFSC;
    }

    public void setIFSC(String IFSC) {
        this.IFSC = IFSC;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }
}
