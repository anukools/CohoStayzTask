package com.task.cohostayz;

/**
 * Created by Anukool Srivastav  - 05/11/2015
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.tesk.cohostayz.adapters.DatabaseHandler;
import com.tesk.cohostayz.network.ConnectionDetector;
import com.tesk.cohostayz.network.GlobalConstants;
import com.tesk.cohostayz.network.JSONParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SplashLogin extends Activity {

	// Connection detector class
	protected ConnectionDetector cd;

	// flag for Internet connection status
	private Boolean isInternetPresent = false;

	private Button fb_login;

	// Your Facebook APP ID
	private static String APP_ID = "1051725148205615";
	// Instance of facebook Class
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;

	// USERS Table Columns names
	private static String KEY_UID = "user_id";
	private static String KEY_FIRST_NAME = "first_name";
	private static String KEY_LAST_NAME = "last_name";
	private static String KEY_EMAIL = "email";
	private static String KEY_GENDER = "gender";
	private static String FB_ID = "facebookid";

	protected JSONParser jsonParser = new JSONParser();
	private DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		/* GET USER LOGIN INFO FROM DATABASE */
		db = new DatabaseHandler(SplashLogin.this);
		HashMap<String, String> getUser = db.GET_USER_INFO();
		String userId = getUser.get("userid");
		System.out.println("USER DETAIL :" + userId);

		if (userId != null) {
			System.out.println("Already Logged In");
			Intent homedashboard = new Intent(SplashLogin.this, MainActivity.class);
			startActivity(homedashboard);
		}

		/* FB LOGIN BUTTON */
		fb_login = (Button) findViewById(R.id.fblogin);
		fb_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) { // TODO Auto-generated method

				check_Connection();
			}
		});

	}

	private void check_Connection() {
		// TODO Auto-generated method stub

		// creating connection detector class instance
		cd = new ConnectionDetector(getApplicationContext());
		// get Internet status

		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {
			// Internet Connection is Present
			// CALL TO THE SERVER DATABASE
			loginToFacebook();
		} else {
			// Internet connection is not present
			// Ask user to connect to Internet
			showAlertDialog(SplashLogin.this, "	Network Error", "You don't have internet connection.", false);
		}

	}

	/**
	 * Background Async Task to Submit login info
	 * */
	public class Sign_In_Request extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;
		JSONObject json = null;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
		}

		/**
		 * Requesting Server
		 * */

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("first_name", KEY_FIRST_NAME));
			params.add(new BasicNameValuePair("last_name", KEY_LAST_NAME));
			params.add(new BasicNameValuePair("gender",  KEY_GENDER));
			params.add(new BasicNameValuePair("email", KEY_EMAIL));
			params.add(new BasicNameValuePair("facebook_id", FB_ID));

			// Note that create product url accepts POST method
			json = jsonParser.makeLoginRequest(GlobalConstants.user_registration_url, "POST", params);

			System.out.println("Sussfully logged JSON  :" + json);
			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String url) {
			// dismiss the dialog once done
			System.out.println("ON POST EXECUTE");
			try {
				String status = json.getString("status");
				if (status.equalsIgnoreCase("success")) {

					String userid = json.getString("user_id");

					db.addUser(userid);

					/**
					 * If JSON array details are stored in SQlite it launches
					 * the User Panel.
					 **/
					System.out.println("Successfully logged IN");
					Intent dashboard = new Intent(SplashLogin.this, MainActivity.class);
					startActivity(dashboard);
					/**
					 * Close Login Screen
					 **/
					finish();
				} else {
					System.out.println("Failed logged IN");

					// pDialog.dismiss();
					Toast.makeText(SplashLogin.this, "Facebook login failed.", Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Function to REGISTER with FACEBOOK
	 * */
	@SuppressWarnings("deprecation")
	public void loginToFacebook() {
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
			System.out.print("TOKEN DETAILS : " + access_token);

			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "email", "public_profile" }, new DialogListener() {

				@Override
				public void onCancel() {
					// Function to handle cancel event
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token

					// System.out.print("\n BUNDLE DETAILS : " + values);
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires", facebook.getAccessExpires());
					editor.commit();

					System.out.print("\n TOKEN DETAILS : " + facebook.getAccessToken());

					System.out.print("\n BUNDLE DETAILS : " + values);
					getProfileInformation();
				}

				@Override
				public void onError(DialogError error) {
					// Function to handle error

				}

				@Override
				public void onFacebookError(FacebookError fberror) {
					// Function to handle Facebook errors

				}

			});
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("\n ACTIVITY RESULT");
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	/**
	 * Get Profile information by making request to Facebook Graph API
	 * */
	@SuppressWarnings("deprecation")
	public void getProfileInformation() {

		Bundle bundle = new Bundle();
		bundle.putString("fields", "email,name,first_name,last_name,gender");
		mAsyncRunner.request("me", bundle, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);

				try {
					// Facebook Profile JSON data
					JSONObject profile = new JSONObject(response);

					// getting details of the user
					FB_ID = profile.getString("id");
					KEY_FIRST_NAME = profile.getString("first_name");
					KEY_LAST_NAME = profile.getString("last_name");
					KEY_GENDER = profile.getString("gender");
					// getting email of the user
					KEY_EMAIL = profile.getString("email");
					// CALL TO LOGIN
					new Sign_In_Request().execute();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	/**
	 * Function to display simple Alert Dialog
	 * 
	 * @param context
	 *            - application context
	 * @param title
	 *            - alert dialog title
	 * @param message
	 *            - alert message
	 * @param status
	 *            - success/failure (used to set icon)
	 * */
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);		

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

}
