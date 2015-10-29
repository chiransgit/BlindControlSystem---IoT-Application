package edu.rit.csci759.mobile;

import java.net.MalformedURLException;
import java.net.URL;

import android.location.Address;
import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class JSONHandler {

	public static String testJSONRequest(String server_URL_text, int requestId){
		// Creating a new session to a JSON-RPC 2.0 web service at a specified URL

		Log.d("Debug serverURL", server_URL_text);
		
		// The JSON-RPC 2.0 server URL
		URL serverURL = null;

		try {
			serverURL = new URL("http://"+server_URL_text);
			Log.d("ServerURL", serverURL.toString());

		} catch (MalformedURLException e) {
		// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


		// Once the client session object is created, you can use to send a series
		// of JSON-RPC 2.0 requests and notifications to it.

		// Sending an example "getTime" request:
		// Construct new request
		
		JSONRPC2Response response = null;
		if (requestId == 2 || requestId == 3){
			JSONRPC2Request addRuleRequest = new JSONRPC2Request("addRule", AddRules.sendRuleObject, requestId);
			// Send request
			try {
				response = mySession.send(addRuleRequest);

			} catch (JSONRPC2SessionException e) {

			Log.e("error", e.getMessage().toString());
			// handle exception...
			}
		}else{
			JSONRPC2Request request = new JSONRPC2Request("getTime", requestId);
			// Send request
			try {
				response = mySession.send(request);

			} catch (JSONRPC2SessionException e) {

			Log.e("error", e.getMessage().toString());
			// handle exception...
			}
		}

		// Print response result / error
		if (response.indicatesSuccess())
			Log.d("debug", response.getResult().toString());
		else
			Log.e("error", response.getError().getMessage().toString());
		
	
		return response.getResult().toString();
	}
	
}
