package com.lujaina.ldbeauty.Models;

import java.util.Date;

public class AppointmentModel {
    private String categoryId;
    private String serviceId;
    private String ownerId;
    private String timeId;
    private String dateId;
    private String pickedDate;
    private String pickedTime;
    private String day;
    private String monthe;
    private String year;



    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTimeId() {
        return timeId;
    }

    public void setTimeId(String timeId) {
        this.timeId = timeId;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }



    public void setPickedDate(String pickedDate) {
        this.pickedDate = pickedDate;
    }

    public String getPickedDate() {
        return pickedDate;
    }


    public String getPickedTime() {
        return pickedTime;
    }

    public void setPickedTime(String pickedTime) {
        this.pickedTime = pickedTime;
    }


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonthe() {
        return monthe;
    }

    public void setMonthe(String monthe) {
        this.monthe = monthe;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
