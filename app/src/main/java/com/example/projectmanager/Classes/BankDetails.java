package com.example.projectmanager.Classes;

public class BankDetails {
    private String bankName,acc_no,acc_IFSC,bankBranch,accountName;

    public BankDetails() {
    }

    public BankDetails(String accountName, String acc_no, String acc_IFSC, String bankName, String bankBranch) {
        this.bankName = bankName;
        this.acc_no = acc_no;
        this.acc_IFSC = acc_IFSC;
        this.bankBranch = bankBranch;
        this.accountName = accountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAcc_no() {
        return acc_no;
    }

    public void setAcc_no(String acc_no) {
        this.acc_no = acc_no;
    }

    public String getAcc_IFSC() {
        return acc_IFSC;
    }

    public void setAcc_IFSC(String acc_IFSC) {
        this.acc_IFSC = acc_IFSC;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
