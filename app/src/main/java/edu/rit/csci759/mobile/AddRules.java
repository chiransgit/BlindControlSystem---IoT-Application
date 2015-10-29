package edu.rit.csci759.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class AddRules extends Activity {

	Spinner spTemp, spAmbient, spBlindState, spLogicalOperation;
	Button btAdd;
	static int requestId;
	static List<Object> sendRuleObject;
	static int flag = 0;
	static String ruleNumber = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrules);

		String value = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			value = extras.getString("UpdateRule");
		}

		String values[] = value.split(" ");

		addItemsOnSpTemp();
		addItemsOnSpAmbient();
		addItemsOnSpBlindState();
		addItemsOnSpLogicalOperation();
		addListenerOnButton();

		if (value != "") {
			flag = 1;
			requestId = 3;
			setDefaultValues(values);
		}else{
			requestId = 2;
			flag = 0;

		}
		

		Log.d("Rules", value);

	}

	private void setDefaultValues(String[] values) {
		// TODO Auto-generated method stub
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("temperature")) {
				String temp = values[i + 1];
				if (temp.equals("freezing")) {
					spTemp.setSelection(0);
				}
				if (temp.equals("cold")) {
					spTemp.setSelection(1);
				}
				if (temp.equals("comfort")) {
					spTemp.setSelection(2);
				}
				if (temp.equals("warm")) {
					spTemp.setSelection(3);
				}
				if (temp.equals("hot")) {
					spTemp.setSelection(4);
				}
			}
			
			if (values[i].equals("AND") || values[i].equals("OR")) {
				String temp = values[i];
				if (temp.equals("AND")) {
					spAmbient.setSelection(0);
				}
				if (temp.equals("OR")) {
					spAmbient.setSelection(1);
				}
			}

			
			if (values[i].equals("ambient")) {
				String temp = values[i + 1];
				if (temp.equals("dark")) {
					spAmbient.setSelection(0);
				}
				if (temp.equals("dim")) {
					spAmbient.setSelection(1);
				}
				if (temp.equals("bright")) {
					spAmbient.setSelection(2);
				}

			}
			if (values[i].equals("blind")) {
				String temp = values[i + 1];
				if (temp.equals("open")) {
					spBlindState.setSelection(0);
				}
				if (temp.equals("half")) {
					spBlindState.setSelection(1);
				}
				if (temp.equals("close")) {
					spBlindState.setSelection(2);
				}

			}
			
		}
		
		ruleNumber = values[values.length - 1];

	}

	private void addListenerOnButton() {
		// TODO Auto-generated method stub
		spTemp = (Spinner) findViewById(R.id.spTemp);
		spAmbient = (Spinner) findViewById(R.id.spAmbient);
		spBlindState = (Spinner) findViewById(R.id.spBlindState);

		btAdd = (Button) findViewById(R.id.btadd);

		btAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(
						AddRules.this,
						"OnClickListener : "
								+ "\nTemp : "
								+ String.valueOf(spTemp.getSelectedItem())
								+ "\nAmbient : "
								+ String.valueOf(spAmbient.getSelectedItem())
								+ "\nBlind State : "
								+ String.valueOf(spBlindState.getSelectedItem()),
						Toast.LENGTH_SHORT).show();

				int operation;

				if (spLogicalOperation.getSelectedItem().toString()
						.equals("AND")) {
					operation = 1;
				} else if (spLogicalOperation.getSelectedItem().toString()
						.equals("OR")) {
					operation = 0;
				} else {
					operation = 2;
				}

				String sendRule = "temperature" + " "
						+ String.valueOf(spTemp.getSelectedItem()) + " "
						+ "ambient" + " "
						+ String.valueOf(spAmbient.getSelectedItem()) + " "
						+ "blind" + " "
						+ String.valueOf(spBlindState.getSelectedItem()) + " "
						+ operation;

				sendRuleObject = new ArrayList<Object>();
				
				if(flag == 0){
					sendRuleObject.add(sendRule);
				}
				else{
					Log.d("Rule Number", ruleNumber);
					sendRuleObject.add(sendRule);
					sendRuleObject.add(ruleNumber);
				}

				new SendJSONRequest().execute();
			}
		});

	}

	private void addItemsOnSpTemp() {
		spTemp = (Spinner) findViewById(R.id.spTemp);
		List<String> list = new ArrayList<String>();
		list.add("freezing");
		list.add("cold");
		list.add("comfort");
		list.add("warm");
		list.add("hot");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTemp.setAdapter(dataAdapter);
	}

	private void addItemsOnSpLogicalOperation() {
		spLogicalOperation = (Spinner) findViewById(R.id.spLogicalOperation);
		List<String> list = new ArrayList<String>();
		list.add("AND");
		list.add("OR");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spLogicalOperation.setAdapter(dataAdapter);

	}

	private void addItemsOnSpAmbient() {
		spAmbient = (Spinner) findViewById(R.id.spAmbient);
		List<String> list = new ArrayList<String>();
		list.add("dark");
		list.add("dim");
		list.add("bright");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spAmbient.setAdapter(dataAdapter);
	}

	private void addItemsOnSpBlindState() {
		spBlindState = (Spinner) findViewById(R.id.spBlindState);
		List<String> list = new ArrayList<String>();
		list.add("open");
		list.add("half");
		list.add("close");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spBlindState.setAdapter(dataAdapter);
	}

	class SendJSONRequest extends AsyncTask<Void, String, String> {
		String response_txt;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {
			String serverURL_text = "10.10.10.113:8080";
			// String request_method = et_requst_method.getText().toString();

			response_txt = JSONHandler.testJSONRequest(serverURL_text,
					requestId);
			return response_txt;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(String result) {
			Log.d("debug", result);
			Log.d("debug", response_txt);
			Toast.makeText(AddRules.this, "Rules Added Successfully! : ",
					Toast.LENGTH_SHORT).show();
		}
	}
}
