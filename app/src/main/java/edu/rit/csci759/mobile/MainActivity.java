package edu.rit.csci759.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends Activity {

	Button btlivefeed, btfeedlist, btruleslist, btaddrules;
	static int requestId;
	static String rules;
	static String livefeed;
	SeekBar sb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btlivefeed = (Button) findViewById(R.id.btlivefeed);
		btfeedlist = (Button) findViewById(R.id.btfeedlist);
		btruleslist = (Button) findViewById(R.id.btrulelist);
		btaddrules = (Button) findViewById(R.id.btaddrules);
		
		btlivefeed.setBackgroundResource(R.drawable.custom_livefeed);
		btfeedlist.setBackgroundResource(R.drawable.custom_feedlist);
		btruleslist.setBackgroundResource(R.drawable.custom_ruleslist);
		btaddrules.setBackgroundResource(R.drawable.custom_addrules);
	}

	public void clickLiveFeed(View v) {
		requestId = 0;
		new SendJSONRequest().execute();
	}

	public void clickFeedList(View v) {
		Intent i = new Intent(MainActivity.this, FeedList.class);
		startActivity(i);
	}

	public void clickRuleList(View v) {
		requestId = 1;
		new SendJSONRequest().execute();

	}

	public void clickAddRules(View v) {
		Intent i = new Intent(MainActivity.this, AddRules.class);
		startActivity(i);
	}

	class SendJSONRequest extends AsyncTask<Void, String, String> {
		String response_txt;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {
			String serverURL_text = "10.10.10.103:8080";
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
			// tv_response.setText(result);
			if (requestId == 1) {
				rules = result;
				if (rules != null) {
					Intent i = new Intent(MainActivity.this, RulesList.class);
					i.putExtra("RuleList", rules);
					startActivity(i);
				}
			}
			if (requestId == 0) {
				livefeed = result;
				if (livefeed != null) {
					Intent i = new Intent(MainActivity.this, LiveFeed.class);
					i.putExtra("LiveFeed", livefeed);
					startActivity(i);
				}
			}
		}

	}
}
