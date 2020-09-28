package com.lujaina.ldbeauty.Models;

public class CategoryModel {
    public String ownerId;
    public String salonName;
    public String categoryTitle;
    public String categoryURL;
    public String categoryId;

    public String getCategoryId() {

        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryURL() {
        return categoryURL;
    }

    public void setCategoryURL(String categoryURL) {
        this.categoryURL = categoryURL;
    }
}
