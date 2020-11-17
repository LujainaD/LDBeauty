package com.lujaina.ldbeauty.Models;

public class PayPalModel {
    private String order;
    private String price;

    public PayPalModel(String order, String price) {
        this.order = order;
        this.price = price;
    }

    public PayPalModel() {
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String toString(){
        return this.order+ " "+ this.price +"R.O";

    }
}
