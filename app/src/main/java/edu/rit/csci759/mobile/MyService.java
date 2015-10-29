package edu.rit.csci759.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import edu.rit.csci759.mobile.FeedList;

public class MyService extends Service{
	
	public NotificationManager notificationManager;
	private static final String TAG = "MyService";
	FeedList fl = new FeedList();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG)
				.show();
		Log.d(TAG, "onCreate");
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		Thread t = new Thread(null, mtask, "MyService");
		t.start();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
	}

	
	private Runnable mtask = new Runnable() {
		public void run() {
			try {
			ServerSocket ss;
			ss = new ServerSocket(49153);
			Socket ss1;
			double cmp = 0.0;

			while(true){

					StringBuilder raw = new StringBuilder();
					ss1 = ss.accept();
					PrintWriter out = new PrintWriter(ss1.getOutputStream(), true);

					BufferedReader in = new BufferedReader(new InputStreamReader(ss1.getInputStream()));
					String line = in.readLine();
					
					JSONRPC2Response resp = new JSONRPC2Response(0);
					
					// send response
					out.write("HTTP/1.1 200 OK\r\n");
					out.write("Content-Type: application/json\r\n");
					out.write("\r\n");
					
					out.write(resp.toJSONString());
					// do not in.close();
					out.flush();
					out.close();
					//ss1.close();
					
					boolean isPost = line.startsWith("POST");
					int contentLength = 0;
					while (!(line = in.readLine()).equals("")) {
						//System.out.println(line);
						raw.append('\n' + line);
						if (isPost) {
							final String contentHeader = "Content-Length: ";
							if (line.startsWith(contentHeader)) {
								contentLength = Integer.parseInt(line.substring(contentHeader.length()));
							}
						}
					}
					StringBuilder body = new StringBuilder();
					if (isPost) {
						int c = 0;
						for (int i = 0; i < contentLength; i++) {
							c = in.read();
							body.append((char) c);
						}
					}
					
					JSONRPC2Request request = JSONRPC2Request.parse(body.toString());
					HashMap<String, Object> ex =  (HashMap<String, Object>) request.getNamedParams();
					String  temp =  ex.get("temp").toString();
					String values[] = temp.split(";");
					double temperature = Double.parseDouble(values[0]);
					Log.d("Temp", temp);
					if(cmp == 0){
						cmp = temperature;
						sendNotification(temp);
					}else{
						if (Math.abs(temperature-cmp) > 2){
							cmp = temperature;
							sendNotification(temp);
						}
					}
					
				} 
				
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
		}
	};

	public void sendNotification(String feedList) {
		// TODO Auto-generated method stub
		// intent triggered, you can add other intent for other actions
        Intent intent = new Intent(MyService.this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);

        
        
        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)
        
            .setContentTitle("Temperature: ")
            .setContentText(feedList)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntent)
            .build();
        
       
        String feed[] = feedList.split(";");
        
        fl.temp.add(feed[0]);
        fl.lightIntensity.add(feed[1]);
        
        notificationManager.notify(0, mNotification); 
        
        
	}



}