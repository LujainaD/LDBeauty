package com.lujaina.ldbeauty.Models;

public class AppointmentModel {
    private String categoryId;
    private String serviceId;
    private String offerId;
    private String ownerId;
    private String recordId;
    private String pickedTime;
    private String appointmentDate;
	private String salonName;
	private String isChosen;


	private boolean isSelected;


	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}


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

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getPickedTime() {
		return pickedTime;
	}

	public void setPickedTime(String pickedTime) {
		this.pickedTime = pickedTime;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getSalonName() {
		return salonName;
	}

	public void setSalonName(String salonName) {
		this.salonName = salonName;
	}

	public String getIsChosen() {
		return isChosen;
	}

	public void setIsChosen(String isChosen) {
		this.isChosen = isChosen;
	}
}
