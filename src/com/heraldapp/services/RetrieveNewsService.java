package com.heraldapp.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import org.apache.log4j.BasicConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.heraldapp.data.DatabaseHandler;
import com.heraldapp.entity.News;
import com.heraldapp.services.utils.SignPostService;
import com.heraldapp.services.utils.StHttpRequest;
import com.heraldapp.utils.Constants;

/**
 * Service to retrieve events list near a particular location.
 */
public class RetrieveNewsService implements Runnable {
	/**
	 * Listener for RetrieveEventsService
	 */
	public interface RetrieveEventsServiceListener {
		void onRetrieveEventsFinished(List<News> newsList);

		void onRetrieveEventsFailed(int error, String message);
	}

	private static final String TAG = "RetrieveLocationService";
	/** Route Location URL */
	private RetrieveEventsServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private String query = "";
	private Context context;
	private List<News> newsList = new ArrayList<News>();
	private DatabaseHandler db;

	private static final Logger log = Logger.getLogger(SignPostService.class
			.getName());

	protected static String yahooServer = "http://yboss.yahooapis.com/ysearch/news";

	/**
	 * Please provide your consumer key here
	 */
	private static String consumer_key = "dj0yJmk9b3RJOUc1eW15c1dkJmQ9WVdrOWVtaG1VRXRVTnpJbWNHbzlOek14TkRJMk16WXkmcz1jb25zdW1lcnNlY3JldCZ4PWMz";

	/**
	 * Please provide your consumer secret here
	 */
	private static String consumer_secret = "e99287a16ff5a6fe134935b9514b6b1d6aafad46";

	/** The HTTP request object used for the connection */
	private static StHttpRequest httpRequest = new StHttpRequest();

	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";

	/** Call Type */
	private static final String callType = "";

	private static final int HTTP_STATUS_OK = 200;

	public RetrieveNewsService(Context context, String query) {
		this.context = context;
		this.query = query;
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {

		try {
			BasicConfigurator.configure();
			returnHttpData();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void returnHttpData() throws UnsupportedEncodingException, Exception {
		Message message = new Message();
		if (this.isConsumerKeyExists() && this.isConsumerSecretExists()) {

			/**
			 * Start with call Type
			 */
			String params = callType;

			/**
			 * Add query
			 */
			params = params.concat("?q=");

			/**
			 * Encode Query string before concatenating
			 */
			params = params.concat(URLEncoder.encode(this.getSearchString(),
					"UTF-8"));

			/**
			 * Create final URL
			 */
			String url = yahooServer + params;

			/**
			 * Create oAuth Consumer
			 */
			OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key,
					consumer_secret);

			/**
			 * Set the HTTP request correctly
			 */
			httpRequest.setOAuthConsumer(consumer);

			try {
				log.info("sending get request to"
						+ URLDecoder.decode(url, ENCODE_FORMAT));
				statusCode = httpRequest.sendGetRequest(url);

				/**
				 * Send the request
				 */
				if (statusCode == HTTP_STATUS_OK) {
					log.info("Response ");
				} else {
					System.out
							.println("Error in response due to status code = "
									+ statusCode);
				}

				jsonResponse = httpRequest.getResponseBody();
				message.what = statusCode;
				Log.d(TAG, "News Response ::" + jsonResponse);
				newsHandler.sendMessage(message);
				// log.info(httpRequest.getResponseBody());

			} catch (UnsupportedEncodingException e) {
				System.out.println("Encoding/Decording error");
				message.what = statusCode;
				newsHandler.sendMessage(message);
			} catch (IOException e) {
				System.out.println("Error with HTTP IO " + e);
				message.what = statusCode;
				newsHandler.sendMessage(message);
			} catch (Exception e) {
				System.out.println("Get Response Body"
						+ httpRequest.getResponseBody() + " " + e);
				message.what = statusCode;
				newsHandler.sendMessage(message);
			}

		} else {
			System.out.println("Key/Secret does not exist");
			message.what = statusCode;
			newsHandler.sendMessage(message);
		}
	}

	private String getSearchString() {
		return query;
	}

	private boolean isConsumerKeyExists() {
		if (consumer_key.isEmpty()) {
			System.out
					.println("Consumer Key is missing. Please provide the key");
			return false;
		}
		return true;
	}

	private boolean isConsumerSecretExists() {
		if (consumer_secret.isEmpty()) {
			System.out
					.println("Consumer Secret is missing. Please provide the key");
			return false;
		}
		return true;
	}

	private Handler newsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.HeraldDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					newsList = parseRetrievedSrcLocation(jsonResponse);
					listener.onRetrieveEventsFinished(newsList);
				} else {
					listener.onRetrieveEventsFailed(
							Constants.HeraldDialogCodes.NETWORK_ERROR,
							Constants.HeraldDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.HeraldDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveEventsFailed(
						Constants.HeraldDialogCodes.DATA_NOT_FOUND,
						Constants.HeraldDialogMessages.NOT_FOUND);
				break;
			case Constants.HeraldDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveEventsFailed(
						Constants.HeraldDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.HeraldDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.HeraldDialogCodes.NETWORK_ERROR:
				listener.onRetrieveEventsFailed(
						Constants.HeraldDialogCodes.NETWORK_ERROR,
						Constants.HeraldDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveEventsFailed(
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
	public RetrieveEventsServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveEventsServiceListener listener) {
		this.listener = listener;
	}

	private List<News> parseRetrievedSrcLocation(String response) {

		try {
			db = new DatabaseHandler(context);
			db.openInternalDB();
			db.deleteTableEntries(DatabaseHandler.TABLE_NEWS, query);
			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONObject myNewsObject = null;
			JSONObject bossObject = jsonObject.getJSONObject("bossresponse");
			JSONObject newsObject = bossObject.getJSONObject("news");
			JSONArray newsArray = newsObject.getJSONArray("results");

			for (int i = 0; i < newsArray.length(); i++) {
				News news = new News();
				myNewsObject = newsArray.getJSONObject(i);
				news.deserializeJSON(myNewsObject);
				newsList.add(news);
				db.addNews(news);
			}
			return newsList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveEventsFailed(
					Constants.HeraldDialogCodes.DATA_NOT_FOUND,
					Constants.HeraldDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}
}
