package com.heraldapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Utils class of Herald App that contains common and utility methods.
 * 
 * @author DEVEN
 * 
 */
public class HeraldUtils {

	/**
	 * 
	 * @param checkNull
	 * @return empty string if the value is null
	 */
	public static String checkForNull(String checkNull) {
		if (checkNull != null && !checkNull.equalsIgnoreCase("null"))
			return checkNull;
		else
			return "";
	}

	/**
	 * method to check for network availability. returns true for available and
	 * false for unavailable
	 */
	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiNetwork != null && wifiNetwork.isAvailable() == true
				&& wifiNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else if (mobileNetwork != null && mobileNetwork.isAvailable() == true
				&& mobileNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else
			return false;
	}

	/**
	 * Shows the status in Dialog.
	 * 
	 * @param context
	 * @param message
	 */
	public static void showStatus(Activity activity, String message,
			String title) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setCancelable(false);
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		dialog.show();
	}

	/**
	 * Will construct the AlertDialog.Builder object for convenience
	 * 
	 * @param context
	 *            Application Context
	 * @param message
	 *            Message to be displayed.
	 * @return AlertDialog.Builder object with the message set.
	 */
	public static AlertDialog.Builder getDialogForStatus(Activity activity,
			String message, String title) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setCancelable(false);
		if (!TextUtils.isEmpty(title)) {
			dialog.setTitle(title);
		}

		dialog.setMessage(message);
		return dialog;
	}

	/**
	 * 
	 * @param position
	 * @return specific news string on the basis of user selection
	 */
	public static String getNewsQuery(int position) {
		String query = "";
		switch (position) {
		case 0:
			query = "Business";
			break;
		case 1:
			query = "Sports";
			break;
		case 2:
			query = "Economy";
			break;
		case 3:
			query = "Education";
			break;
		case 4:
			query = "Healthcare";
			break;
		case 5:
			query = "SocialMedia";
			break;
		case 6:
			query = "Career";
			break;
		case 7:
			query = "Innovation";
			break;
		case 8:
			query = "Weather";
			break;
		default:
			break;
		}

		return query;
	}

	/**
	 * Converts long to date
	 * 
	 * @param timeStamp
	 *            date
	 * @return date converts the date in required format
	 */
	public static String convertToDate(long timeStamp) {
		if (timeStamp <= 0) {
			return "";
		}
		Date date = new Date(timeStamp);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String strDate = formatter.format(date);
		return strDate;
	}

}
