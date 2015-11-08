package com.task.cohostayz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tesk.cohostayz.adapters.ImageSlideAdapter;
import com.tesk.cohostayz.network.ApiConnector;
import com.tesk.cohostayz.network.GlobalConstants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SelectedProperty extends Activity {
	// Declare Variables
	private String propid;
	private String property_name;
	private String cost;
	private String proparea;
	private String flag;
	private String position;
	private String[] arraylist;
	// Declare Variables
	private JSONObject jsonobject;
	private JSONArray jsonarray;
	private ViewPager myPager;
	TextView propertyname;
	TextView cost_tv;
	TextView propertyArea;
	ImageView property_image;

	// public ImageLoader imageLoader = new ImageLoader(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from singleitemview.xml
		setContentView(R.layout.selected_property);

		// Get values of property from intent
		Intent i = getIntent();
		// Get the result of propod
		propid = i.getStringExtra("propid");
		property_name = i.getStringExtra("proptitle");
		// Get the result of cost
		cost = i.getStringExtra("cost");
		// Get the result of proparea
		proparea = i.getStringExtra("proparea");
		// Get the result of propertyimage
		flag = i.getStringExtra("image");

		// Initilaize all views
		initialize_views();

		// Set results to the TextViews
		propertyname.setText(property_name);
		cost_tv.setText("Rs. " + cost + " p.m. onwards");
		propertyArea.setText(proparea);

		// Capture position and set results to the ImageView
		// Passes flag images URL into ImageLoader.class
		// imageLoader.DisplayImage(flag, property_image);

		new GetSelectedProperties().execute(new ApiConnector());

	}

	public void initialize_views() {
		propertyname = (TextView) findViewById(R.id.property_name);
		cost_tv = (TextView) findViewById(R.id.cost);
		propertyArea = (TextView) findViewById(R.id.propertyArea);
		property_image = (ImageView) findViewById(R.id.property_image);
		myPager = (ViewPager) findViewById(R.id.pager);
	}

	private class GetSelectedProperties extends AsyncTask<ApiConnector, Long, JSONObject> {

		private ProgressDialog pDialog;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(SelectedProperty.this);
			// pDialog.setTitle("  ");
			pDialog.setMessage(" getting your place....");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("deprecation")
		protected JSONObject doInBackground(ApiConnector... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("propid", propid));
			return params[0].GetAllShops(param, GlobalConstants.property_detail_url);
		}

		@Override
		protected void onPostExecute(JSONObject jsonobj) {
			// Create an array

			try {
				// Locate the array name in JSON
				jsonarray = jsonobj.getJSONObject("details").getJSONArray("images");
				arraylist = new String[jsonarray.length()];
				for (int i = 0; i < jsonarray.length(); i++) {
					arraylist[i] = jsonarray.getString(i);

				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

			myPager.setAdapter(new ImageSlideAdapter(getApplicationContext(), arraylist));
			pDialog.dismiss();

		}
	}

}