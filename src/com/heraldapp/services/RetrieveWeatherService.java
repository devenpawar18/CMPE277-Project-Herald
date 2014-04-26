package com.heraldapp.services;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.heraldapp.ApplicationEx;
import com.heraldapp.data.DatabaseHandler;
import com.heraldapp.entity.Weather;
import com.heraldapp.services.utils.HTTPRequest;
import com.heraldapp.utils.Constants;

/**
 * Service to retrieve weather details of the destination location.
 */
public class RetrieveWeatherService implements Runnable {
	/**
	 * Listener for WeatherService
	 */
	public interface RetrieveWeatherServiceListener {
		void onRetrieveWeatherFinished(Weather weather);

		void onRetrieveWeatherFailed(int error, String message);
	}

	private static final String TAG = "RetrieveWeatherService";
	/** Retrieve Weather URL */
	private static String RETRIEVE_WEATHER_URL = "";
	private RetrieveWeatherServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private Context context;
	private String destinationLocation;
	private Weather weather = new Weather();
	private DatabaseHandler db;

	public RetrieveWeatherService(Context context, String destinationLocation) {
		this.context = context;
		this.destinationLocation = destinationLocation;
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {
		// if (destinationLocation.contains(" "))
		// destinationLocation = destinationLocation.replace(" ", "%20");

		Message message = new Message();
		try {
			destinationLocation = URLEncoder.encode(destinationLocation,
					"utf-8");
			RETRIEVE_WEATHER_URL = Services.WEATHER_API_URL
					+ ApplicationEx.currentLocation.getLattitude() + "&lon="
					+ ApplicationEx.currentLocation.getLongitude();
			HTTPRequest request = new HTTPRequest(RETRIEVE_WEATHER_URL, context);
			Log.d("WEATHER Service", "URL::" + RETRIEVE_WEATHER_URL);
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			if (jsonResponse.contains("html"))
				message.what = Constants.HeraldDialogCodes.NETWORK_ERROR;
			message.what = statusCode;
			Log.d(TAG, "run::" + jsonResponse);
			weatherHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			weatherHandler.sendMessage(message);
			Log.e(TAG, "Event Service exception::" + e);
		}

	}

	private Handler weatherHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.HeraldDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					weather = parseRetrievedWeather(jsonResponse);
					listener.onRetrieveWeatherFinished(weather);
				} else {
					listener.onRetrieveWeatherFailed(
							Constants.HeraldDialogCodes.NETWORK_ERROR,
							Constants.HeraldDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.HeraldDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveWeatherFailed(
						Constants.HeraldDialogCodes.DATA_NOT_FOUND,
						Constants.HeraldDialogMessages.NOT_FOUND);
				break;
			case Constants.HeraldDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveWeatherFailed(
						Constants.HeraldDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.HeraldDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.HeraldDialogCodes.NETWORK_ERROR:
				listener.onRetrieveWeatherFailed(
						Constants.HeraldDialogCodes.NETWORK_ERROR,
						Constants.HeraldDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveWeatherFailed(
						Constants.HeraldDialogCodes.NETWORK_ERROR,
						Constants.HeraldDialogMessages.NETWORK_ERROR);
				break;
			}
		}
	};

	/**
	 * Get listener
	 * 
	 * @return
	 */
	public RetrieveWeatherServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveWeatherServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * Store Weather details in database
	 * 
	 * @param response
	 * @return
	 */
	private Weather parseRetrievedWeather(String response) {

		try {
			db = new DatabaseHandler(context);
			db.openInternalDB();
			JSONObject jsonObject = new JSONObject(jsonResponse);
			Weather weather = new Weather();
			weather.deserializeJSON(jsonObject);
			db.deleteAllTableEntries(DatabaseHandler.TABLE_WEATHER);
			db.addWeather(weather);
			return weather;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveWeatherFailed(
					Constants.HeraldDialogCodes.DATA_NOT_FOUND,
					Constants.HeraldDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeDB();
		}
		return null;
	}

}
