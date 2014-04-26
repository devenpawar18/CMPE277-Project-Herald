package com.heraldapp.services.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import org.apache.log4j.BasicConfigurator;

/**
 * Sample code to use Yahoo! Search BOSS
 * 
 * Please include the following libraries 1. Apache Log4j 2. oAuth Signpost
 * 
 * @author Deven
 */
public class SignPostService {

	private static final Logger log = Logger.getLogger(SignPostService.class
			.getName());

	protected static String yahooServer = "http://yboss.yahooapis.com/ysearch/news";

	// Please provide your consumer key here
	private static String consumer_key = "dj0yJmk9b3RJOUc1eW15c1dkJmQ9WVdrOWVtaG1VRXRVTnpJbWNHbzlOek14TkRJMk16WXkmcz1jb25zdW1lcnNlY3JldCZ4PWMz";

	// Please provide your consumer secret here
	private static String consumer_secret = "e99287a16ff5a6fe134935b9514b6b1d6aafad46";

	/** The HTTP request object used for the connection */
	private static StHttpRequest httpRequest = new StHttpRequest();

	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";

	/** Call Type */
	private static final String callType = "web";

	private static final int HTTP_STATUS_OK = 200;

	/**
	 * 
	 * @return
	 */
	public int returnHttpData() throws UnsupportedEncodingException, Exception {

		if (this.isConsumerKeyExists() && this.isConsumerSecretExists()) {

			// Start with call Type
			String params = callType;

			// Add query
			params = params.concat("?q=");

			// Encode Query string before concatenating
			params = params.concat(URLEncoder.encode(this.getSearchString(),
					"UTF-8"));

			// Create final URL
			String url = yahooServer + params;

			// Create oAuth Consumer
			OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key,
					consumer_secret);

			// Set the HTTP request correctly
			httpRequest.setOAuthConsumer(consumer);

			try {
				log.info("sending get request to"
						+ URLDecoder.decode(url, ENCODE_FORMAT));
				int responseCode = httpRequest.sendGetRequest(url);

				// Send the request
				if (responseCode == HTTP_STATUS_OK) {
					log.info("Response ");
				} else {
					System.out
							.println("Error in response due to status code = "
									+ responseCode);
				}
				log.info(httpRequest.getResponseBody());

			} catch (UnsupportedEncodingException e) {
				System.out.println("Encoding/Decording error");
			} catch (IOException e) {
				System.out.println("Error with HTTP IO " + e);
			} catch (Exception e) {
				System.out.println("Get Response Body"
						+ httpRequest.getResponseBody() + " " + e);
				return 0;
			}

		} else {
			System.out.println("Key/Secret does not exist");
		}
		return 1;
	}

	private String getSearchString() {
		return "Yahoo";
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

}