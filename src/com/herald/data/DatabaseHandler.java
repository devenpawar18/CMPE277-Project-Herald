package com.herald.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.herald.entity.News;
import com.herald.entity.Weather;

/**
 * 
 * @author DEVEN Database Handler class to manage database activities
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "herald.db";

	/**
	 * Database Tables & Columns
	 */

	private static final String KEY_ID = "id";

	/**
	 * Table News
	 */
	public static final String TABLE_NEWS = "news";
	private static final String KEY_TYPE = "TYPE";
	private static final String KEY_TITLE = "TITLE";
	private static final String KEY_NEWS_ABSTRACT = "NEWS_ABSTRACT";
	private static final String KEY_URL = "URL";
	private static final String KEY_SOURCE = "SOURCE";
	private static final String KEY_SOURCE_URL = "SOURCE_URL";
	private static final String KEY_DATE = "DATE";
	private static final String KEY_LANGUAGE = "LANGUAGE";

	/**
	 * Table Weather
	 */
	public static final String TABLE_WEATHER = "weather";
	private static final String KEY_NAME = "NAME";
	private static final String KEY_CURRENT_TEMP = "CURRENT_TEMP";
	private static final String KEY_MIN_TEMP = "MIN_TEMP";
	private static final String KEY_MAX_TEMP = "MAX_TEMP";
	private static final String KEY_MAIN = "MAIN";
	private static final String KEY_DESCRIPTION = "DESCRIPTION";
	private static final String KEY_HUMIDITY = "HUMIDITY";

	private Context context;

	private SQLiteDatabase m_db;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out
				.println("###############################Creating tables########################");
		String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT," + KEY_TITLE
				+ " TEXT," + KEY_NEWS_ABSTRACT + " TEXT," + KEY_URL + " TEXT,"
				+ KEY_SOURCE + " TEXT," + KEY_SOURCE_URL + " TEXT," + KEY_DATE
				+ " INTEGER," + KEY_LANGUAGE + " TEXT" + ")";

		String CREATE_WEATHER_TABLE = "CREATE TABLE " + TABLE_WEATHER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_CURRENT_TEMP + " INTEGER," + KEY_MIN_TEMP + " INTEGER,"
				+ KEY_MAX_TEMP + " INTEGER," + KEY_MAIN + " TEXT,"
				+ KEY_DESCRIPTION + " TEXT," + KEY_HUMIDITY + " INTEGER" + ")";

		db.execSQL(CREATE_NEWS_TABLE);
		db.execSQL(CREATE_WEATHER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/**
		 * Drop older table if existed
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);

		/**
		 * Create tables again
		 */
		onCreate(db);
	}

	/**
	 * Opens the Database.
	 */
	public void openInternalDB() {
		if ((m_db == null) || (m_db.isOpen() == false)) {
			m_db = this.getWritableDatabase();
		}
	}

	/**
	 * Closes the Database.
	 */
	public void closeDB() {
		if (m_db != null) {
			m_db.close();
			m_db = null;
		}
	}

	/**
	 * Deletes one Record from the Table with given table Name & Condition.
	 * 
	 * @param tableName
	 * @param whereConition
	 * @param values
	 */
	public void deleteRow(String tableName, String whereConition,
			String[] values) {
		SQLiteDatabase db = this.getWritableDatabase();
		int deletedItems = 0;
		deletedItems = db.delete(tableName, whereConition, values);
		Log.d("", "===No. of deleted items===" + deletedItems);
		db.close();
	}

	/**
	 * Deletes table entries of the table with given Table Name BASED ON QUERY
	 * 
	 * @param TABLE_NAME
	 */
	public void deleteTableEntries(String TABLE_NAME, String newsType) {
		int rows = m_db.delete(TABLE_NAME, KEY_TYPE + "=?",
				new String[] { newsType });
		System.out.println("****deleted rows******" + rows);
	}

	/**
	 * Deletes all table entries of the table with given Table Name.
	 * 
	 * @param TABLE_NAME
	 */
	public void deleteAllTableEntries(String TABLE_NAME) {
		int rows = m_db.delete(TABLE_NAME, null, null);
		System.out.println("****deleted rows******" + rows);
	}

	/**
	 * Adds News to Database.
	 */
	public void addNews(News news) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, news.getNewsType());
		values.put(KEY_TITLE, news.getTitle());
		values.put(KEY_NEWS_ABSTRACT, news.getNewsAbstract());
		values.put(KEY_URL, news.getUrl());
		values.put(KEY_SOURCE, news.getSource());
		values.put(KEY_SOURCE_URL, news.getSourceURL());
		values.put(KEY_DATE, news.getDate());
		values.put(KEY_LANGUAGE, news.getLanguage());

		db.insert(TABLE_NEWS, null, values);
		db.close();
	}

	/**
	 * Adds Weather Object to Database.
	 */
	public void addWeather(Weather weather) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, weather.getName());
		values.put(KEY_CURRENT_TEMP, weather.getCurrentTemp());
		values.put(KEY_MIN_TEMP, weather.getMinTemp());
		values.put(KEY_MAX_TEMP, weather.getMaxTemp());
		values.put(KEY_MAIN, weather.getMain());
		values.put(KEY_DESCRIPTION, weather.getDescription());
		values.put(KEY_HUMIDITY, weather.getHumidity());

		db.insert(TABLE_WEATHER, null, values);
		db.close();
	}

	/**
	 * 
	 * @return numbers of rows in Table Data
	 */
	public int getCursorCount() {
		String selectQuery = "SELECT  * FROM " + TABLE_NEWS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 
	 * @return News list from database.
	 */
	public List<News> getNewsList(String newsType) {

		List<News> newsList = new ArrayList<News>();
		// String selectQuery = "SELECT  * FROM " + TABLE_NEWS;
		SQLiteDatabase db = this.getWritableDatabase();

		// Cursor cursor = db.rawQuery(selectQuery, null);
		Cursor cursor = db.query(TABLE_NEWS, null, KEY_TYPE + "=?",
				new String[] { newsType }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				News news = new News();
				news.setNewsType(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_TYPE)));
				news.setTitle(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_TITLE)));
				news.setNewsAbstract(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_NEWS_ABSTRACT)));
				news.setUrl(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_URL)));
				news.setSource(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_SOURCE)));
				news.setSourceURL(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_SOURCE_URL)));
				news.setDate(cursor.getLong(cursor
						.getColumnIndex(DatabaseHandler.KEY_DATE)));
				news.setLanguage(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_LANGUAGE)));
				newsList.add(news);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return newsList;
	}

	/**
	 * 
	 * @return Weather Object from database.
	 */
	public Weather getWeatherInfo() {

		String selectQuery = "SELECT  * FROM " + TABLE_WEATHER;
		SQLiteDatabase db = this.getWritableDatabase();
		Weather weather = null;

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				weather = new Weather();
				weather.setName(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_NAME)));
				weather.setCurrentTemp(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_CURRENT_TEMP)));
				weather.setMinTemp(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_MIN_TEMP)));
				weather.setMaxTemp(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_MAX_TEMP)));
				weather.setMain(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_MAIN)));
				weather.setDescription(cursor.getString(cursor
						.getColumnIndex(DatabaseHandler.KEY_DESCRIPTION)));
				weather.setHumidity(cursor.getDouble(cursor
						.getColumnIndex(DatabaseHandler.KEY_HUMIDITY)));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return weather;
	}

}
