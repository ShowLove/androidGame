package com.mkyong.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkyong.android.R;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private final String[] mobileValues;

	public ImageAdapter(Context context, String[] mobileValues) {
		this.context = context;
		this.mobileValues = mobileValues;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);

			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.mobile, null);

			// set value into textview
			/*TextView textView = (TextView) gridView
					.findViewById(R.id.grid_item_label); 
			textView.setText(mobileValues[position]); */

			// set image based on selected text
			ImageView imageView = (ImageView) gridView
					.findViewById(R.id.grid_item_image);

			String mobile = mobileValues[position];
			
			if (mobile.equals("Ground")) {
				imageView.setImageResource(R.drawable.ground);
			} else if (mobile.equals("Treasure")) {
				imageView.setImageResource(R.drawable.treasure);
			} else if (mobile.equals("Hat")) {
				imageView.setImageResource(R.drawable.hat);
			}  else if (mobile.equals("Pickaxe")) {
				imageView.setImageResource(R.drawable.pickaxe);
			} else if (mobile.equals("Pit")) {
				imageView.setImageResource(R.drawable.pit);
			}else if (mobile.equals("Portal")) {
				imageView.setImageResource(R.drawable.portal);
			} else if (mobile.equals("Wheelbarrow")) {
				imageView.setImageResource(R.drawable.wheelbarrow);
			} else if (mobile.equals("Cave")) {
				imageView.setImageResource(R.drawable.cave);
			}
			
		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}


	@Override
	public int getCount() {
		return mobileValues.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
