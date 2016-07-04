package com.example.mayankrai.bluetooth;

import android.graphics.Bitmap;


/**
 * Created by Mayank.Rai on 6/25/2016.
 */
public class ModelAdvertisement {

	int advertisementImage;
	String titleText;
	String discriptionText;
	
	public int getAdvertisementImage() {
		return advertisementImage;
	}
	public void setAdvertisementImage(int advertisementImage) {
		this.advertisementImage = advertisementImage;
	}
	public String getTitleText() {
		return titleText;
	}
	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}
	public String getDiscriptionText() {
		return discriptionText;
	}
	public void setDiscriptionText(String discriptionText) {
		this.discriptionText = discriptionText;
	}
}
