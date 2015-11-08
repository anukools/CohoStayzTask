package com.tesk.cohostayz.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.task.cohostayz.R;
import com.task.cohostayz.SelectedProperty;
import com.task.cohostayz.R.id;
import com.task.cohostayz.R.layout;







import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeProperties extends BaseAdapter {

	// Declare Variables
	Context context;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	ImageLoader imageLoader;
	private DisplayImageOptions options;
	HashMap<String, String> resultp = new HashMap<String, String>();

	public HomeProperties(Context context, ArrayList<HashMap<String, String>> arraylist) {
		this.context = context;
		data = arraylist;
		imageLoader = ImageLoader.getInstance();	
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// Declare Variables
		TextView property_name;
		TextView cost;
		TextView proparea;
		ImageView flag;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.property_list, parent, false);
		// Get the position
		resultp = data.get(position);

		// Locate the TextViews in listview_item.xml
		property_name = (TextView) itemView.findViewById(R.id.property_name);
		cost = (TextView) itemView.findViewById(R.id.cost);
		proparea = (TextView) itemView.findViewById(R.id.loc_area);

		// Locate the ImageView in listview_item.xml
		flag = (ImageView) itemView.findViewById(R.id.home_overlay);

		// Capture position and set results to the TextViews
		property_name.setText(resultp.get("proptitle"));
		cost.setText("Rs. " + resultp.get("cost"));
	    proparea.setText(resultp.get("proparea"));
		// Capture position and set results to the ImageView
		// Passes flag images URL into ImageLoader.class
	    
	    options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.default_proprty)
		.showImageOnFail(R.drawable.default_proprty)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
	    
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		final ProgressBar spinner = (ProgressBar) itemView.findViewById(R.id.loading);
		imageLoader.displayImage(resultp.get("image"), flag, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				spinner.setVisibility(View.VISIBLE);
			}

			

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				spinner.setVisibility(View.GONE);
			}
		});

		
		
		// Capture ListView item click
		itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get the position
				resultp = data.get(position);
				Intent intent = new Intent(context, SelectedProperty.class);
				// Pass all data propid
				intent.putExtra("propid", resultp.get("propid"));
				// Pass all data proptitle
				intent.putExtra("proptitle", resultp.get("proptitle"));
				// Pass all data cost
				intent.putExtra("cost", resultp.get("cost"));
				// Pass all data proparea
				intent.putExtra("proparea", resultp.get("proparea"));
				// Pass all data image
				intent.putExtra("image", resultp.get("image"));
				// Start SingleItemView Class
				context.startActivity(intent);

			}
		});
		return itemView;
	}
}