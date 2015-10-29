package edu.rit.csci759.mobile;

public class RowItem {
	private String temp;
	private String lightIntensity;
	
	public RowItem(String temp, String lightIntensity) {
		this.temp = temp;
		this.lightIntensity = lightIntensity;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getLightIntensity() {
		return lightIntensity;
	}
	public void setLightIntensity(String lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	@Override
	public String toString() {
		return temp + "\n" + lightIntensity;
	}	
}
