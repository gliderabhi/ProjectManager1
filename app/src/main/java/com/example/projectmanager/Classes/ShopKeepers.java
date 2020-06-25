package com.example.projectmanager.Classes;

import java.util.HashMap;

public class ShopKeepers {
    String name ;
    HashMap<String, Integer> products ;
    public ShopKeepers(){

    }
    public ShopKeepers(String name, HashMap<String, Integer> products) {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, Integer> products) {
        this.products = products;
    }
}
