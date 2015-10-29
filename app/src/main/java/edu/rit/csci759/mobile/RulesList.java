package edu.rit.csci759.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RulesList extends Activity {
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ruleslist);

		String value = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			value = extras.getString("RuleList");
		}

		Log.d("Rules", value);

		String values[] = value.split(";");

		// String[] values = new String[] { "Android List View",
		// "Adapter implementation",
		// "Simple List View In Android",
		// "Create List View Android",
		// "Android Example",
		// "List View Source Code",
		// "List View Array Adapter",
		// "Android Example List View"
		// };

		Log.d("FEEDLIST", "NullPointer");
		// Get ListView object from xml
		listView = (ListView) findViewById(R.id.list);

		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item index
				int itemPosition = position;

				// ListView Clicked item value
				String itemValue = (String) listView
						.getItemAtPosition(position);

				// Show Alert
				Toast.makeText(
						getApplicationContext(),
						"Position :" + itemPosition + "  ListItem : "
								+ itemValue, Toast.LENGTH_LONG).show();

				
				//parsing the selected rules
				String breakItemValue[] = itemValue.split(" ");
				String currentRuleComponents[] = null;
				//String currentRuleComponents2[] = new String[4];
				
				if (breakItemValue.length == 12 && breakItemValue[1].equals("(temperature")) {
					currentRuleComponents = new String[7];
					currentRuleComponents[0] = "temperature";
					currentRuleComponents[1] = breakItemValue[3];
					currentRuleComponents[2] = breakItemValue[4];
					currentRuleComponents[3] = breakItemValue[5];
					currentRuleComponents[4] = breakItemValue[7];
					currentRuleComponents[5] = breakItemValue[9];
					currentRuleComponents[6] = breakItemValue[11];
				}else if(breakItemValue.length == 8 && breakItemValue[1].equals("temperature")){
					currentRuleComponents = new String[4];
					currentRuleComponents[0] = "temperature";
					currentRuleComponents[1] = breakItemValue[3];
					currentRuleComponents[2] = breakItemValue[5];
					currentRuleComponents[3] = breakItemValue[7];
				}else if(breakItemValue.length == 8 && breakItemValue[1].equals("ambient")){
					currentRuleComponents = new String[4];
					currentRuleComponents[0] = "ambient";
					currentRuleComponents[1] = breakItemValue[3];
					currentRuleComponents[2] = breakItemValue[5];
					currentRuleComponents[3] = breakItemValue[7];
				}
				
				for(int i = 0; i < currentRuleComponents.length; i++){
						currentRuleComponents[i] = currentRuleComponents[i].replace("(", "");
						currentRuleComponents[i] = currentRuleComponents[i].replace(")", "");
				}
				
				
				String sendRuleComponents = "";
				
				for(int i = 0; i < currentRuleComponents.length; i++){
					sendRuleComponents += currentRuleComponents[i] + " ";
				}

				sendRuleComponents += " " + (itemPosition+1);

				Intent i = new Intent(RulesList.this, AddRules.class);
				i.putExtra("UpdateRule", sendRuleComponents);
				startActivity(i);

			}

		});
	}

}
