package com.herald.entity;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.herald.ApplicationEx;

/**
 * News Entity that contains all information about a specific news
 * 
 * @author DEVEN
 * 
 */
public class News implements Entitiy, Parcelable {
	private String title;
	private String newsAbstract;
	private String url;
	private String source;
	private String sourceURL;
	private long date;
	private String language;
	private String newsType;

	public News() {

	}

	public String getNewsType() {
		return newsType;
	}

	public void setNewsType(String newsType) {
		this.newsType = newsType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNewsAbstract() {
		return newsAbstract;
	}

	public void setNewsAbstract(String newsAbstract) {
		this.newsAbstract = newsAbstract;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public JSONObject serializeJSON() throws Exception {
		return null;
	}

	/**
	 * Method used to deserialize json for Weather object
	 */
	@Override
	public void deserializeJSON(JSONObject myNewsObject) throws Exception {

		if (ApplicationEx.newsType != null)
			this.setNewsType(ApplicationEx.newsType);

		this.setTitle(myNewsObject.has("title") ? myNewsObject
				.getString("title") : "");
		this.setNewsAbstract(myNewsObject.has("abstract") ? myNewsObject
				.getString("abstract") : "");
		this.setUrl(myNewsObject.has("url") ? myNewsObject.getString("url")
				: "");
		this.setSource(myNewsObject.has("source") ? myNewsObject
				.getString("source") : "");
		this.setSourceURL(myNewsObject.has("sourceurl") ? myNewsObject
				.getString("sourceurl") : "");
		this.setDate(myNewsObject.has("date") ? myNewsObject.getLong("date")
				: -1);
		this.setLanguage(myNewsObject.has("language") ? myNewsObject
				.getString("language") : "");

	}

	/**
	 * 
	 * @return creator
	 */
	public static Parcelable.Creator<News> getCreator() {
		return CREATOR;
	}

	private News(Parcel in) {
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
		out.writeString(newsType);
		out.writeString(title);
		out.writeString(newsAbstract);
		out.writeString(url);
		out.writeString(source);
		out.writeString(sourceURL);
		out.writeLong(date);
		out.writeString(language);

	}

	/**
	 * read Reason Object from Parcel
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		newsType = in.readString();
		title = in.readString();
		newsAbstract = in.readString();
		url = in.readString();
		source = in.readString();
		sourceURL = in.readString();
		date = in.readLong();
		language = in.readString();
	}

	public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
		public News createFromParcel(Parcel in) {
			return new News(in);
		}

		public News[] newArray(int size) {
			return new News[size];
		}
	};

}
