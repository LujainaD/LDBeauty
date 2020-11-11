package com.lujaina.ldbeauty.Models;

public class OfferModel {
    private String salonOwnerId;
    private String salonName;

    private String offerId;
    private String title;
    private String services;
    private String previousPrice;
    private String currentPrice;


    public String getSalonOwnerId() {
        return salonOwnerId;
    }

    public void setSalonOwnerId(String salonOwnerId) {
        this.salonOwnerId = salonOwnerId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(String previousPrice) {
        this.previousPrice = previousPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

}
