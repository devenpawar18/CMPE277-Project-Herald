package com.heraldapp.entity;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Location ENtity that contains the latitude and longitude of a specific
 * location
 * 
 * @author DEVEN
 * 
 */
public class Location implements Entitiy, Parcelable {
	private double lattitude;
	private double longitude;

	public Location() {

	}

	public double getLattitude() {
		return lattitude;
	}

	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public JSONObject serializeJSON() throws Exception {
		return null;
	}

	/**
	 * Method used to deserialize json for Location object
	 */
	@Override
	public void deserializeJSON(JSONObject jsonObject) throws Exception {
		this.setLongitude(jsonObject.has("lng") ? jsonObject.getDouble("lng")
				: -1);
		this.setLattitude(jsonObject.has("lat") ? jsonObject.getDouble("lat")
				: -1);

	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<Location> getCreator() {
		return CREATOR;
	}

	private Location(Parcel in) {
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
		out.writeDouble(lattitude);
		out.writeDouble(longitude);
	}

	/**
	 * read Reason Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		lattitude = in.readDouble();
		longitude = in.readDouble();
	}

	public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
		public Location createFromParcel(Parcel in) {
			return new Location(in);
		}

		public Location[] newArray(int size) {
			return new Location[size];
		}
	};

}
