package com.heraldapp.activity;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.heraldapp.ApplicationEx;
import com.heraldapp.R;
import com.heraldapp.entity.News;
import com.heraldapp.utils.Constants;

/**
 * Reprents the news in detail. (Integrated with View Pager)
 * 
 * @author DEVEN
 * 
 */
public class NewsDetailActivity extends Activity implements OnGestureListener,
		AnimationListener {

	private MyFragmentAdapter myFragmentAdapter;
	private ViewPager mPager;
	private TextView pageIndicator;
	private int currentPage = 0;
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector detector = new GestureDetector(this);

	private Button mTwitter;
	private Button mLinkedIn;
	private Button mMail;
	private Button mFaceBook;
	private Dialog dialog;
	private final int SHAKE_DIALOG = 1;
	private News news;

	private RelativeLayout mainLayout;
	private Animation anim;
	private Animation fade_anim;
	private Button webSiteButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ApplicationEx.isFirstLoad = false;
		dialog = new Dialog(NewsDetailActivity.this);

		getActionBar().setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		getActionBar().setDisplayHomeAsUpEnabled(true);

		pageIndicator = (TextView) findViewById(R.id.text);
		mPager = (ViewPager) findViewById(R.id.pager);
		myFragmentAdapter = new MyFragmentAdapter(getApplicationContext());
		mPager.setAdapter(myFragmentAdapter);

		/**
		 * sets the current page at the position which you want.
		 */
		mPager.setCurrentItem(ApplicationEx.selectedPosition);

		/**
		 * Sets the page indicator
		 */
		pageIndicator.setText("" + (ApplicationEx.selectedPosition + 1) + " /"
				+ ApplicationEx.newsList.size());

		/**
		 * Set the touch event listener
		 */
		mPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentPage = position;
				currentPage = currentPage % ApplicationEx.newsList.size();
				pageIndicator.setText((mPager.getCurrentItem() + 1) + " /"
						+ ApplicationEx.newsList.size());

			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});

	}

	/**
	 * Method to share news via email
	 */
	private void sendEmail() {
		String[] TO = { "" };
		String[] CC = { "" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, news.getTitle());
		emailIntent.putExtra(Intent.EXTRA_TEXT, news.getUrl());

		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(NewsDetailActivity.this,
					"There is no email client installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		/**
		 * Start Google Analytics Tracking
		 */
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		/**
		 * Stop Google Analytics Tracking
		 */
		EasyTracker.getInstance(this).activityStop(this);
	}

	/**
	 * crates a dialog with news sharing options
	 */
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHAKE_DIALOG:
			dialog.setTitle("Share Via:");
			dialog.setContentView(R.layout.share);

			/**
			 * Share News via Twitter
			 */
			mTwitter = (Button) dialog.findViewById(R.id.twitter);
			mTwitter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent tweetIntent = new Intent(Intent.ACTION_SEND);
					tweetIntent.putExtra(Intent.EXTRA_TEXT, news.getUrl());
					tweetIntent.setType("text/plain");

					PackageManager packManager = getPackageManager();
					List<ResolveInfo> resolvedInfoList = packManager
							.queryIntentActivities(tweetIntent,
									PackageManager.MATCH_DEFAULT_ONLY);

					boolean resolved = false;
					for (ResolveInfo resolveInfo : resolvedInfoList) {
						if (resolveInfo.activityInfo.packageName
								.startsWith("com.twitter.android")) {
							tweetIntent.setClassName(
									resolveInfo.activityInfo.packageName,
									resolveInfo.activityInfo.name);
							resolved = true;
							break;
						}
					}
					if (resolved) {
						startActivity(tweetIntent);
					} else {
						Toast.makeText(NewsDetailActivity.this,
								"Twitter app isn't found", Toast.LENGTH_LONG)
								.show();
					}
				}
			});

			/**
			 * Share News via LinkedIn
			 */
			mLinkedIn = (Button) dialog.findViewById(R.id.linked_in);
			mLinkedIn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent tweetIntent = new Intent(Intent.ACTION_SEND);
					tweetIntent.putExtra(Intent.EXTRA_TEXT, news.getUrl());
					tweetIntent.setType("text/plain");

					PackageManager packManager = getPackageManager();
					List<ResolveInfo> resolvedInfoList = packManager
							.queryIntentActivities(tweetIntent,
									PackageManager.MATCH_DEFAULT_ONLY);

					boolean resolved = false;
					for (ResolveInfo resolveInfo : resolvedInfoList) {
						if (resolveInfo.activityInfo.packageName
								.startsWith("com.linkedin.android")) {
							tweetIntent.setClassName(
									resolveInfo.activityInfo.packageName,
									resolveInfo.activityInfo.name);
							resolved = true;
							break;
						}
					}
					if (resolved) {
						startActivity(tweetIntent);
					} else {
						Toast.makeText(NewsDetailActivity.this,
								"LinkedIn app isn't found", Toast.LENGTH_LONG)
								.show();
					}
				}
			});

			/**
			 * Share News via email
			 */
			mMail = (Button) dialog.findViewById(R.id.mail);
			mMail.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					sendEmail();
					dialog.dismiss();
				}
			});
			/**
			 * Share News via Facebook
			 */
			mFaceBook = (Button) dialog.findViewById(R.id.faceBook);
			mFaceBook.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent settingIntent = new Intent(NewsDetailActivity.this,
							FacebookShareActivity.class);
					settingIntent.putExtra("facebook_url", news.getUrl());
					startActivity(settingIntent);
					dialog.dismiss();
				}
			});
			return dialog;
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * Handles back press
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
	 * View Pager Adapter that allows user to see other news details by swipping
	 * 
	 * @author DEVEN
	 * 
	 */
	public class MyFragmentAdapter extends PagerAdapter {
		private Context context;
		private View layout = null;
		private TextView newsDate;
		private TextView newsAbstract;
		protected static final float SHAKE_THRESHOLD = 1000;
		private SensorManager sensorManager;

		public MyFragmentAdapter(Context context) {
			this.context = context;
			/**
			 * load the animation
			 */
			anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.bounce);
			fade_anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.fade_in);
			fade_anim.setDuration(1000);

			/**
			 * set animation listener
			 */
			anim.setAnimationListener(NewsDetailActivity.this);
			fade_anim.setAnimationListener(NewsDetailActivity.this);

		}

		@Override
		public int getCount() {
			return ApplicationEx.newsList.size() * 2;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewGroup) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.news_detail, null);
			mainLayout = (RelativeLayout) layout.findViewById(R.id.main_layout);
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			/**
			 * Registers the accelerometer sensor
			 */
			boolean isSensorAvailable = sensorManager.registerListener(
					sensorListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);

			if (!isSensorAvailable) {
				sensorManager.unregisterListener(sensorListener, sensorManager
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
			}

			newsDate = (TextView) layout.findViewById(R.id.title);
			newsAbstract = (TextView) layout.findViewById(R.id.news_abstract);
			webSiteButton = (Button) layout.findViewById(R.id.web_site);
			webSiteButton.setOnClickListener(onClickListener);

			if (position == ApplicationEx.newsList.size())
				news = ApplicationEx.newsList
						.get(ApplicationEx.newsList.size() - 1);
			else
				news = ApplicationEx.newsList.get(position);

			if (news != null) {
				newsDate.setText(news.getTitle());
				newsAbstract.setText(news.getNewsAbstract());
				getActionBar().setTitle("News Details");
			}

			webSiteButton.setVisibility(View.VISIBLE);
			if (!ApplicationEx.isFirstLoad) {
				mainLayout.setAnimation(fade_anim);
				webSiteButton.setAnimation(anim);
				ApplicationEx.isFirstLoad = true;
			} else {
				mainLayout.setVisibility(View.VISIBLE);
				webSiteButton.setVisibility(View.VISIBLE);
			}

			container.addView(layout);
			return layout;
		}

		/**
		 * Onclick listener
		 */
		private OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = null;
				int id = view.getId();
				switch (id) {
				case R.id.web_site:
					intent = new Intent(NewsDetailActivity.this,
							WebViewActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("url", news.getUrl());
					startActivity(intent);
					break;
				default:
					break;
				}

			}
		};

		/**
		 * Display the news sharing dialog on device shake with the help of
		 * accelerometer sensor
		 */
		private final SensorEventListener sensorListener = new SensorEventListener() {
			private float last_x;
			private float last_y;
			private float last_z;
			private long lastUpdate;

			public void onSensorChanged(SensorEvent se) {
				if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					long curTime = System.currentTimeMillis();
					/**
					 * only allow one update every 100ms.
					 */
					if ((curTime - lastUpdate) > 100) {
						long diffTime = (curTime - lastUpdate);
						lastUpdate = curTime;

						float x = se.values[SensorManager.DATA_X];
						float y = se.values[SensorManager.DATA_Y];
						float z = se.values[SensorManager.DATA_Z];

						float speed = Math.abs(x + y + z - last_x - last_y
								- last_z)
								/ diffTime * 10000;

						if (speed > SHAKE_THRESHOLD) {
							if (!dialog.isShowing()
									&& !NewsDetailActivity.this.isFinishing()) {
								showDialog(SHAKE_DIALOG);
							}
						}
						last_x = x;
						last_y = y;
						last_z = z;
					}
				}

			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd
	 * (android .view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation animation) {
		if (mainLayout.getVisibility() == View.INVISIBLE) {
			mainLayout.setVisibility(View.VISIBLE);

		}
		if (webSiteButton.getVisibility() == View.INVISIBLE)
			webSiteButton.setVisibility(View.VISIBLE);
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.animation.Animation.AnimationListener#onAnimationStart
	 * (android .view.animation.Animation)
	 */
	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * method to identify gesture (left or right swipe)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {

				return false;
			}

			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
					&& currentPage == (ApplicationEx.newsList.size() - 1)) {

				mPager.setCurrentItem(0, false);
				return true;
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
					&& currentPage == 0) {
				mPager.setCurrentItem(ApplicationEx.newsList.size() - 1, false);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}