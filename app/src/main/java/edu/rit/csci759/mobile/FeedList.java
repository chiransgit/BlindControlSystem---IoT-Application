package edu.rit.csci759.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FeedList extends Activity implements OnItemClickListener {

	//public static final String[] temp = new String[] { "85", "83", "85", "87" };
	public static ArrayList<String> temp = new ArrayList<String>();
	public static ArrayList<String> lightIntensity = new ArrayList<String>();
	
	//public static final String[] lightIntensity = new String[] {"0.83","0.29", "0.5","0.96" };

	public static final String[] blindState = { "half","open", "open", "close" };

	ListView listView;
	List<RowItem> rowItems;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedlist_listview);
		
		//temp = new ArrayList<String>();
		//lightIntensity = new ArrayList<String>();

		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < temp.size(); i++) {
			RowItem item = new RowItem(temp.get(i), lightIntensity.get(i));
			rowItems.add(item);
		}

		listView = (ListView) findViewById(R.id.list);
		CustomListViewAdapter adapter = new CustomListViewAdapter(this, R.layout.feedlist_item, rowItems);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Toast toast = Toast.makeText(getApplicationContext(), "Item "+ (position + 1) + ": " + rowItems.get(position), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
}