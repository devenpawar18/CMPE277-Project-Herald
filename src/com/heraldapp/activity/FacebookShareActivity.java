package com.heraldapp.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.heraldapp.R;
import com.heraldapp.share.facebook.AsyncFacebookRunner;
import com.heraldapp.share.facebook.DialogError;
import com.heraldapp.share.facebook.Facebook;
import com.heraldapp.share.facebook.FacebookError;
import com.heraldapp.share.facebook.SessionEvents;
import com.heraldapp.share.facebook.SessionStore;
import com.heraldapp.share.facebook.AsyncFacebookRunner.RequestListener;
import com.heraldapp.share.facebook.Facebook.DialogListener;
import com.heraldapp.share.facebook.Facebook.FBResponseListener;
import com.heraldapp.share.facebook.SessionEvents.AuthListener;

public class FacebookShareActivity extends Activity implements
		FBResponseListener {

	private Facebook facebook = new Facebook();
	private AuthListener listener = new SampleAuthListener();
	private TextView feedField;

	/**
	 * Application ID provided by Facebook to uniquely identify your
	 * application.
	 */
	public final static String APP_ID = "225759547630794";

	/** Permissions for facebook operations. */
	public static final String[] PERMISSIONS = new String[] { "publish_stream",
			"read_stream" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_facebook);
		getActionBar().setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		getActionBar().setDisplayHomeAsUpEnabled(true);
		String facebookURL = getIntent().getExtras().getString("facebook_url");
		feedField = (TextView) findViewById(R.id.feed);
		feedField.setText(facebookURL);

		Button submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = feedField.getText().toString();
				status.replaceAll("&", "&amp;");
				setFacebookStatus(status);
			}
		});

		facebook = new Facebook();
		SessionStore.restore(facebook, FacebookShareActivity.this);
		if (facebook.isSessionValid()) {
			SessionStore.clear(FacebookShareActivity.this);
			new AsyncFacebookRunner(facebook).logout(
					FacebookShareActivity.this, new RequestListener() {
						public void onComplete(String response) {
							doLogout();
						}

						public void onFileNotFoundException(
								FileNotFoundException error) {
							doLogout();
						}

						public void onIOException(IOException error) {
							doLogout();
						}

						public void onMalformedURLException(
								MalformedURLException error) {
							doLogout();
						}

						public void onFacebookError(FacebookError error) {
							doLogout();
						}
					});
		} else {
			/** If session is not valid launch the login page. */
			SessionEvents.addAuthListener(listener);
			facebook.authorize(FacebookShareActivity.this, APP_ID, PERMISSIONS,
					new LoginDialogListener());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		SessionStore.restore(facebook, this);
	}

	/**
	 * Callback method on logout. Should be overriden.
	 */
	private void doLogout() {
		FacebookShareActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
			}
		});
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
	 * Wrapper method for updating status on facebook timeline if session is
	 * valid otherwise launches login page.
	 * 
	 * @param content
	 *            Status message to update on facebook
	 */
	private void setFacebookStatus(final String content) {
		final FacebookManager manager = FacebookManager.getInstance();
		if (facebook.isSessionValid()) {
			new Thread() {
				public void run() {
					manager.updateStatus(
							(FBResponseListener) FacebookShareActivity.this,
							content);
				};
			}.start();
		} else {
			SessionEvents.addAuthListener(listener);
			facebook.authorize(FacebookShareActivity.this, APP_ID, PERMISSIONS,
					new LoginDialogListener());
		}
	}

	/**
	 * Delegate Class for handling facebook login dialog
	 */
	private final class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			try {
				SessionEvents.onLoginSuccess();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}
	}

	/**
	 * Delegate Class for notifying the application when authorize events
	 * happen.
	 */
	public class SampleAuthListener implements AuthListener {
		public void onAuthSucceed() {
			SessionStore.save(facebook, FacebookShareActivity.this);
			SessionEvents.removeAuthListener(listener);
		}

		public void onAuthFail(String error) {

		}
	}

	/** Interface FBResponseListener implementation methods */

	@Override
	public void responseFailure(int requestCode) {
		Toast.makeText(FacebookShareActivity.this, "Failed to post...",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void responseSuccess(int requestCode, Object result) {
		Looper.prepare();
		Toast.makeText(FacebookShareActivity.this, "Posted Successfully...",
				Toast.LENGTH_SHORT).show();
	}

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

}