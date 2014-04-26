package com.heraldapp.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Weather Entity that contains all information about users current city
 * 
 * @author DEVEN
 * 
 */
public class Weather implements Entitiy, Parcelable {
	private String name;
	private double currentTemp;
	private double minTemp;
	private double maxTemp;
	private String main;
	private String description;
	private double humidity;
	private int errorCode;

	public Weather() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCurrentTemp() {
		return currentTemp;
	}

	public void setCurrentTemp(double currentTemp) {
		this.currentTemp = currentTemp;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}

	public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public JSONObject serializeJSON() throws Exception {
		return null;
	}

	/**
	 * Method used to deserialize json for Weather object
	 */
	@Override
	public void deserializeJSON(JSONObject jsonObject) throws Exception {
		JSONObject myWeatherObject = null;
		JSONArray weatherArray = jsonObject.getJSONArray("weather");
		for (int i = 0; i < weatherArray.length(); i++) {
			myWeatherObject = weatherArray.getJSONObject(0);
		}
		this.setMain(myWeatherObject.has("main") ? myWeatherObject
				.getString("main") : "");
		this.setDescription(myWeatherObject.has("description") ? myWeatherObject
				.getString("description") : "");
		this.setErrorCode(jsonObject.has("cod") ? jsonObject.getInt("cod") : -1);
		this.setName(jsonObject.has("name") ? jsonObject.getString("name") : "");

		JSONObject mainObject = jsonObject.getJSONObject("main");
		this.setCurrentTemp(mainObject.has("temp") ? mainObject
				.getDouble("temp") : -1);
		this.setMinTemp(mainObject.has("temp_min") ? mainObject
				.getDouble("temp_min") : -1);
		this.setMaxTemp(mainObject.has("temp_max") ? mainObject
				.getDouble("temp_max") : -1);
		this.setHumidity(mainObject.has("humidity") ? mainObject
				.getDouble("humidity") : -1);

	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<Weather> getCreator() {
		return CREATOR;
	}

	private Weather(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * write Location Object to parcel
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeDouble(minTemp);
		out.writeDouble(maxTemp);
		out.writeDouble(currentTemp);
		out.writeString(main);
		out.writeString(description);
		out.writeDouble(humidity);
		out.writeInt(errorCode);
		out.writeString(name);

	}

	/**
	 * read Reason Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		minTemp = in.readDouble();
		maxTemp = in.readDouble();
		currentTemp = in.readDouble();
		main = in.readString();
		description = in.readString();
		humidity = in.readDouble();
		errorCode = in.readInt();
		name = in.readString();
	}

	public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
		public Weather createFromParcel(Parcel in) {
			return new Weather(in);
		}

		public Weather[] newArray(int size) {
			return new Weather[size];
		}
	};

}
