package com.heraldapp.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.heraldapp.share.facebook.Facebook;
import com.heraldapp.share.facebook.SessionStore;
import com.heraldapp.share.facebook.Facebook.FBResponseListener;

/**
 * Manages the Facebook update and post comments
 *
 */
public class FacebookManager  {
	
	public static final int PERMISSIONREQUESTCODE = 1;
	public static final int MESSAGEPUBLISHED = 2;
	
	/**
	 * Facebook request constants
	 */
	public static final int REQUEST_LOGIN = 0;
	public static final int REQUEST_LOGOUT = 1;
	public static final int REQUEST_STATUS_UPDATE = 2;
	public static final int REQUEST_GET_TIMELINE = 3;
	public static final int REQUEST_USER_REQUEST = 4;
	public static final int REQUEST_USER_LIKE = 5;
	public static final int REQUEST_USER_COMMENT = 6;
	
    private static FacebookManager instance;
	  
    private Facebook facebook;
    private Context context;
    private int requestcode;

    private FacebookManager()
    {
        instance = this;
    }
    
    /**
     * Creates (if necessary) and returns the static manager object
     * @return static manager object
     */
    public static FacebookManager getInstance() {
    	if(instance == null)
    		instance = new FacebookManager();
    	return instance;
    }
    
    /**
     * Updates the facebook status
     * @param listener Callback for status update response
     * @param message Message to post
     */
    public void updateStatus(final FBResponseListener listener, final String message)
    {
    	context = (Activity)listener;
    	
    	Thread timeLineThread = new Thread(new Runnable(){

			@Override
			public void run() {
		    	Bundle params = new Bundle();
		    	params.putString("message", message);
		    	try {
		    		requestcode = FacebookManager.REQUEST_STATUS_UPDATE;
					getFacebook().request("me/feed", params, "POST");
					listener.responseSuccess(requestcode, null);
				} catch (FileNotFoundException e) {
					listener.responseFailure(requestcode);
					
				} catch (MalformedURLException e) {
					listener.responseFailure(requestcode);
					
				} catch (IOException e) {
					listener.responseFailure(requestcode);
				}
			}
    	});
		timeLineThread.start();
    }
   
    /**
     * Post comment on facebook timeline
     * @param listener Callback for post comment response
     * @param postId Path to resource in the Facebook graph
     * @param message Comment to post
     */
    public void postComment(final FBResponseListener listener, final String postId, final String message)
    {
    	requestcode = REQUEST_USER_COMMENT;
    	Thread commentThread = new Thread(new Runnable(){

			@Override
			public void run() {
				Bundle params = new Bundle();
		        params.putString("message", message);
		        
		        try
		        {
			        getFacebook().request(postId + "/comments", params, "POST");
			        listener.responseSuccess(requestcode, null);
		        }catch (FileNotFoundException e) {
					listener.responseFailure(requestcode);
					
				} catch (MalformedURLException e) {
					listener.responseFailure(requestcode);
					
				} catch (IOException e) {
					listener.responseFailure(requestcode);
				}				
			}
    		
    	});
    	commentThread.start();
    }
    
    /**
     * Create and return a new Facebook object and restore the session 
     * @return Newly created Facebook object
     */
    private Facebook getFacebook()
    {
    	facebook = new Facebook();
    	SessionStore.restore(facebook, context);
    	return facebook;
    }
  
}

