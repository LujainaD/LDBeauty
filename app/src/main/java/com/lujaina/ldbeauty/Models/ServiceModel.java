package com.lujaina.ldbeauty.Models;

public class ServiceModel {
    public String ownerId;
    public String serviceTitle;
    public String serviceURL;
    public String serviceId;
    public String serviceSpecialist;
    public String servicePrice;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceSpecialist() {
        return serviceSpecialist;
    }

    public void setServiceSpecialist(String serviceSpecialist) {
        this.serviceSpecialist = serviceSpecialist;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }
}
