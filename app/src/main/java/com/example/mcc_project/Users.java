package com.example.mcc_project;

public class Users {
    String name, sbuID, phone;

    public Users() {
    }

    public Users(String name, String sbuID, String phone) {
        this.name = name;
        this.sbuID = sbuID;
        this.phone = phone;
    }

    //GETTERS

    public String getName() {
        return name;
    }

    public String getSbuID() {
        return sbuID;
    }

    public String getPhone() {
        return phone;
    }

    //SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setSbuID(String sbuID) {
        this.sbuID = sbuID;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
