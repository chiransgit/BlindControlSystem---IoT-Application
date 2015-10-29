package edu.rit.csci759.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LiveFeed extends Activity {
	
	TextView tvTemp, tvAmbient;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livefeed);
        
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvAmbient = (TextView) findViewById(R.id.tvAmbient);
        
        String value = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("LiveFeed");
        }
        
        String values[] = value.split(";");
        
        tvTemp.setText(values[0]);
        tvAmbient.setText(values[1]);

        
	}

}
