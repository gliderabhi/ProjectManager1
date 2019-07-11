package com.example.projectmanager.Classes;

public class MaterialsDetails {
    private String name,measuringUnit;
    private int quantity;
    private int cost;

    public MaterialsDetails(String name, String measuringUnit, int quantity, int cost) {
        this.name = name;
        this.measuringUnit = measuringUnit;
        this.quantity = quantity;
        this.cost = cost;
    }

    public MaterialsDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasuringUnit() {
        return measuringUnit;
    }

    public void setMeasuringUnit(String measuringUnit) {
        this.measuringUnit = measuringUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}