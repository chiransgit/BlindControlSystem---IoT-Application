package edu.rit.csci759.mobile;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {

	Context context;

	public CustomListViewAdapter(Context context, int resourceId, List<RowItem> items) {
		super(context, resourceId, items);
		this.context = context;
	}
	
	/*private view holder class*/
	private class ViewHolder {
		TextView tvTemp;
		TextView tvLightIntensity;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RowItem rowItem = getItem(position);
		
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feedlist_item, null);
			holder = new ViewHolder();
			holder.tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
			holder.tvLightIntensity = (TextView) convertView.findViewById(R.id.tvLight);
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder) convertView.getTag();
				
		holder.tvTemp.setText(rowItem.getTemp());
		holder.tvLightIntensity.setText(rowItem.getLightIntensity());
		
		return convertView;
	}
}
