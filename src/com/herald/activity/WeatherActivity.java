package com.herald.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.herald.ApplicationEx;
import com.herald.R;
import com.herald.data.DatabaseHandler;
import com.herald.entity.Weather;
import com.herald.services.RetrieveWeatherService;
import com.herald.services.RetrieveWeatherService.RetrieveWeatherServiceListener;
import com.herald.utils.Constants;
import com.herald.utils.HeraldUtils;

/**
 * Displays Weather of the users current city
 * 
 * @author DEVEN
 * 
 */
public class WeatherActivity extends Activity implements
		RetrieveWeatherServiceListener, AnimationListener {
	private ActionBar actionBarSherlock;
	private TextView weatherTextView;
	private TextView descTextView;
	private ImageView weatherImageView;
	private TextView cTempTextView;
	private TextView minTempTextView;
	private TextView maxTempTextView;
	private TextView humidityTextView;
	private ProgressDialog pd;
	private String myAddress = "";
	private DatabaseHandler db;
	private Animation anim;
	private RelativeLayout parentLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);

		actionBarSherlock = getActionBar();

		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);

		parentLayout = (RelativeLayout) findViewById(R.id.parent_layout);
		weatherTextView = (TextView) findViewById(R.id.main_value);
		descTextView = (TextView) findViewById(R.id.desc_value);
		cTempTextView = (TextView) findViewById(R.id.c_temp_value);
		minTempTextView = (TextView) findViewById(R.id.min_temp_value);
		maxTempTextView = (TextView) findViewById(R.id.max_temp_value);
		humidityTextView = (TextView) findViewById(R.id.humidity_value);
		weatherImageView = (ImageView) findViewById(R.id.weather_icon);

		// load the animation
		anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_in);

		// set animation listener
		anim.setAnimationListener(this);
		/**
		 * Checks if the network connection is available, if yes then hit the
		 * web service call for weather else take the cached data from the
		 * SQLite db.
		 */
		if (HeraldUtils.isConnectionAvailable(this)) {
			try {
				LocationManager locationManager;
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				updateWithNewLocation(location);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			parentLayout.setAnimation(anim);
			Toast.makeText(this,
					"Network error. Please check your connection...",
					Toast.LENGTH_SHORT).show();

			db = new DatabaseHandler(getApplicationContext());
			db.openInternalDB();
			Weather weather = db.getWeatherInfo();
			setWeatherInfo(weather);
			db.closeDB();
		}

	}

	/**
	 * Web service call for Weather
	 */
	public void getWeather() {
		pd = ProgressDialog.show(WeatherActivity.this, "",
				"Loading Weather...", true);
		RetrieveWeatherService service = new RetrieveWeatherService(
				getApplicationContext(), ApplicationEx.myCity);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/**
	 * handles back press
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Success callback
	 */
	@Override
	public void onRetrieveWeatherFinished(Weather weather) {

		if (pd != null || pd.isShowing())
			pd.cancel();
		if (weather != null) {
			if (weather.getErrorCode() == 404) {
				Toast.makeText(WeatherActivity.this, "Place not found",
						Toast.LENGTH_SHORT).show();
			} else {
				setWeatherInfo(weather);
			}

		} else {
			Toast.makeText(WeatherActivity.this, "Network Error",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Method that sets weather information in User Interface
	 * 
	 * @param weather
	 */
	public void setWeatherInfo(Weather weather) {
		String title = getResources().getString(R.string.weather_title)
				+ "<font color=#FFFF00>" + " " + ApplicationEx.myCity
				+ "</font>";
		actionBarSherlock.setTitle(Html.fromHtml(title));
		if (weather != null) {
			parentLayout.setAnimation(anim);
			weatherTextView.setText(weather.getMain());
			descTextView.setText(weather.getDescription());
			cTempTextView.setText(""
					+ Constants.convertKelvinToDegreeFahrenheit(weather
							.getCurrentTemp()) + "\u2109");
			minTempTextView.setText(""
					+ Constants.convertKelvinToDegreeFahrenheit(weather
							.getMinTemp()) + "\u2109");
			maxTempTextView.setText(""
					+ Constants.convertKelvinToDegreeFahrenheit(weather
							.getMaxTemp()) + "\u2109");
			humidityTextView.setText("" + weather.getHumidity());
			displayWeatherIcon(weather);
		} else {
			String actionBarTitle = getResources().getString(
					R.string.weather_title)
					+ "<font color=#EDC999>" + " " + "N/A" + "</font>";
			actionBarSherlock.setTitle(Html.fromHtml(actionBarTitle));
			weatherTextView.setText("N/A");
			descTextView.setText("N/A");
			cTempTextView.setText("N/A");
			minTempTextView.setText("N/A");
			maxTempTextView.setText("N/A");
			humidityTextView.setText("N/A");
		}
	}

	/**
	 * Display weather according to weather description & time
	 * 
	 * @param weather
	 */
	public void displayWeatherIcon(Weather weather) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		String str = sdf.format(new Date());
		Integer hours = Integer.parseInt(str);
		if ((hours <= 7 || hours > 18)
				&& weather.getMain().equalsIgnoreCase("Clear")) {
			weatherImageView.setImageResource(R.drawable.night_clear);
		} else if ((hours <= 7 || hours > 18)
				&& (weather.getMain().equalsIgnoreCase("Clouds")
						|| weather.getMain().equalsIgnoreCase("Haze") || weather
						.getMain().equalsIgnoreCase("Mist"))) {
			weatherImageView.setImageResource(R.drawable.night_cloudy);
		} else if ((hours <= 7 || hours > 18)
				&& weather.getMain().equalsIgnoreCase("Snow")) {
			weatherImageView.setImageResource(R.drawable.night_snowy);
		} else if ((hours <= 7 || hours > 18)
				&& weather.getMain().equalsIgnoreCase("Rain")) {
			weatherImageView.setImageResource(R.drawable.night_rainy);
		} else if ((hours > 7 || hours <= 18)
				&& weather.getMain().equalsIgnoreCase("Clear")) {
			weatherImageView.setImageResource(R.drawable.day_clear);
		} else if ((hours > 7 || hours <= 18)
				&& (weather.getMain().equalsIgnoreCase("Clouds")
						|| weather.getMain().equalsIgnoreCase("Haze") || weather
						.getMain().equalsIgnoreCase("Mist"))) {
			weatherImageView.setImageResource(R.drawable.day_cloudy);
		} else if ((hours > 7 || hours <= 19)
				&& weather.getMain().equalsIgnoreCase("Snow")) {
			weatherImageView.setImageResource(R.drawable.day_snowy);
		} else if ((hours > 7 || hours <= 19)
				&& weather.getMain().equalsIgnoreCase("Rain")) {
			weatherImageView.setImageResource(R.drawable.day_rainy);
		}

	}

	@Override
	public void onRetrieveWeatherFailed(int error, String message) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		Toast.makeText(WeatherActivity.this,
				Constants.HeraldDialogMessages.NETWORK_ERROR,
				Toast.LENGTH_SHORT).show();

	}

	/**
	 * Update Location with current location
	 * 
	 * @param location
	 */
	private void updateWithNewLocation(Location location) {
		if (pd != null && pd.isShowing())
			pd.cancel();
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			System.out.println("***lattitude***" + lat
					+ "**** & longitude ******" + lng);
			ApplicationEx.currentLocation.setLattitude(lat);
			ApplicationEx.currentLocation.setLongitude(lng);

			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());
			try {
				addresses = geocoder.getFromLocation(lat, lng, 1);
				myAddress = addresses.get(0).getAddressLine(0) + ", "
						+ addresses.get(0).getAddressLine(1);

				ApplicationEx.myCity = addresses.get(0).getLocality();

				/**
				 * Store the current city in shared preference
				 */
				SharedPreferences.Editor prefEditor = ApplicationEx.sharedPreference
						.edit();
				prefEditor.putString(getResources().getString(R.string.city),
						ApplicationEx.myCity).commit();
				getWeather();
			} catch (IOException e) {
				Toast.makeText(WeatherActivity.this,
						"Network Error. Please check the connection...",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

		} else {
			AlertDialog.Builder dialog = HeraldUtils.getDialogForStatus(
					WeatherActivity.this, "Please turn on the GPS",
					"Enable GPS");
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					});
			dialog.show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.animation.Animation.AnimationListener#onAnimationEnd(android
	 * .view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation animation) {
		parentLayout.setVisibility(View.VISIBLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.animation.Animation.AnimationListener#onAnimationRepeat(
	 * android.view.animation.Animation)
	 */
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.animation.Animation.AnimationListener#onAnimationStart(android
	 * .view.animation.Animation)
	 */
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}
