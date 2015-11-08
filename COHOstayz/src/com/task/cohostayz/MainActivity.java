package com.task.cohostayz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.task.cohostayz.SplashLogin.Sign_In_Request;
import com.tesk.cohostayz.adapters.HomeProperties;
import com.tesk.cohostayz.network.ApiConnector;
import com.tesk.cohostayz.network.ConnectionDetector;
import com.tesk.cohostayz.network.GlobalConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class MainActivity extends Activity {

	// Connection detector class
	protected ConnectionDetector cd;

	// flag for Internet connection status
	private Boolean isInternetPresent = false;

	// Declare Variables
	private JSONObject jsonobject;
	private JSONArray jsonarray;
	private ListView listview;
	private HomeProperties adapter;
	private ArrayList<HashMap<String, String>> arraylist;
	private Spinner select_location;
	private String location;
	private static ListView propertyListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initvariable();

		ArrayAdapter<CharSequence> adapterFeet = ArrayAdapter.createFromResource(this, R.array.locations,
				android.R.layout.simple_list_item_single_choice);
		adapterFeet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		select_location.setAdapter(adapterFeet);

		select_location.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				// TODO Auto-generated method stub
				location = select_location.getItemAtPosition(pos).toString().trim();
				check_Connection();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				// Log.d("SELECTED DROPDOWN 1",select_location.getSelectedItem().toString().trim());
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
			new GetAllProperties().execute(new ApiConnector());

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet
			showAlertDialog(MainActivity.this, "	Network Error", "You don't have internet connection.", false);
		}

	}

	private void initvariable() {
		propertyListView = (ListView) findViewById(R.id.homePostList);
		select_location = (Spinner) findViewById(R.id.select_your_location);
	}

	private class GetAllProperties extends AsyncTask<ApiConnector, Long, JSONObject> {

		private ProgressDialog pDialog;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(MainActivity.this);
			// pDialog.setTitle("  ");
			pDialog.setMessage("Getting your property....");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("deprecation")
		protected JSONObject doInBackground(ApiConnector... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("city", location));
			return params[0].GetAllShops(param, GlobalConstants.property_list_url);
		}

		@Override
		protected void onPostExecute(JSONObject jsonobj) {
			// Create an array
			arraylist = new ArrayList<HashMap<String, String>>();
			try {
				// Locate the array name in JSON
				jsonarray = jsonobj.getJSONArray("properties");

				for (int i = 0; i < jsonarray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					jsonobject = jsonarray.getJSONObject(i);
					// Retrive JSON Objects
					map.put("propid", jsonobject.getString("propid"));
					map.put("proptitle", jsonobject.getString("proptitle"));
					map.put("proparea", jsonobject.getString("proparea"));
					map.put("cost", jsonobject.getString("cost"));
					map.put("image", jsonobject.getString("image"));
					// Set the JSON Objects into the array
					arraylist.add(map);
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

			// Pass the results into ListViewAdapter.java
			adapter = new HomeProperties(MainActivity.this, arraylist);
			// Set the adapter to the ListView
			propertyListView.setAdapter(adapter);
			// Close the progressdialog
			pDialog.dismiss();

		}
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

		// Setting alert dialog icon
		// alertDialog.setIcon((status) ? R.drawable.connect :
		// R.drawable.block);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onBackPressed() {

		finish();

	}

}
