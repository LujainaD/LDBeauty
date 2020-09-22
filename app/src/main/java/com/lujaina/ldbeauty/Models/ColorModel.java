package com.lujaina.ldbeauty.Models;

public class ColorModel {
    private String colorValue;
    private String colorName;

	public ColorModel( String colorValue) {
		this.colorName = colorName;
		this.colorValue = colorValue;
	}

	public String getColorValue() {
		return colorValue;
	}

	public String getColorName() {
		return colorName;
	}
}
