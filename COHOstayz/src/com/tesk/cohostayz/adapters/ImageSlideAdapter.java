package com.tesk.cohostayz.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.task.cohostayz.R;
import com.task.cohostayz.R.drawable;
import com.task.cohostayz.R.id;
import com.task.cohostayz.R.layout;

public class ImageSlideAdapter extends PagerAdapter {

	private static  String[] IMAGE_URLS;

	private LayoutInflater inflater;
	private DisplayImageOptions options;
	ImageLoader loader;
	Context baseContext;

	public ImageSlideAdapter(Context context,String[] url_array) {
		baseContext =context;
		inflater = LayoutInflater.from(context);
		IMAGE_URLS = url_array;
		loader = ImageLoader.getInstance();			
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_proprty)
				.showImageOnFail(R.drawable.default_proprty)
				.resetViewBeforeLoading(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.build();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return IMAGE_URLS.length;
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		View imageLayout = inflater.inflate(R.layout.selected_slider_images, view, false);
		assert imageLayout != null;
		ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
		final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

		
		loader.init(ImageLoaderConfiguration.createDefault(baseContext));
		loader.displayImage(IMAGE_URLS[position], imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				spinner.setVisibility(View.VISIBLE);
			}

			

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				spinner.setVisibility(View.GONE);
			}
		});

		view.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}
}

