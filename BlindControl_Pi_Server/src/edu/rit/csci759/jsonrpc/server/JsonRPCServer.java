package edu.rit.csci759.jsonrpc.server;

//The JSON-RPC 2.0 Base classes that define the 
//JSON-RPC 2.0 protocol messages
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.net.*;
import java.lang.Math;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
//The JSON-RPC 2.0 server framework package
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import edu.rit.csci759.fuzzylogic.MyTipperClass;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import edu.rit.csci759.rspi.utils.MCP3008ADCReader;
import edu.rit.csci759.rspi.RpiBlinkLED;

public class JsonRPCServer {
	/**
	 * The port that the server listens on.
	 */
	private static final int PORT = 8080;
	private static int reqCount=0;

	private final static boolean DEBUG = false;
	private static boolean keepRunning = true;
	public static GpioController gpio = GpioFactory.getInstance();
	static int flag = 0;
	static int ambient = 0;
	static boolean isSending = false;
	public static RpiBlinkLED blinkLED = new RpiBlinkLED();	
	
       public static float getTemperature()
	{

		if(flag==0)
		{		
			MCP3008ADCReader.initSPI(gpio);
			flag++;
		}

		float temp_F = 0;

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				System.out.println("Shutting down.");
				keepRunning = false;
			}
		});
		

	
		/*
		 * Reading ambient light from the photocell sensor using the MCP3008 ADC 
		 */
		int adc_ambient = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		ambient = (int)(adc_ambient / 10.24); 
			
		if (DEBUG){
			System.out.println("readAdc:" + Integer.toString(adc_ambient) + 
					" (0x" + MCP3008ADCReader.lpad(Integer.toString(adc_ambient, 16).toUpperCase(), "0", 2) + 
					", 0&" + MCP3008ADCReader.lpad(Integer.toString(adc_ambient, 2), "0", 8) + ")");        
			System.out.println("Ambient:" + ambient + "/100 (" + adc_ambient + "/1024)");
		}
			
			
		/*
		 * Reading temperature from the TMP36 sensor using the MCP3008 ADC 
		 */
		int adc_temperature = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0.ch());

		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		int temperature = (int)(adc_temperature / 10.24); 
			
		if (DEBUG){
			System.out.println("readAdc:" + Integer.toString(adc_temperature) + 
					" (0x" + MCP3008ADCReader.lpad(Integer.toString(adc_temperature, 16).toUpperCase(), "0", 2) + 
					", 0&" + MCP3008ADCReader.lpad(Integer.toString(adc_temperature, 2), "0", 8) + ")");        
			System.out.println("Temperature:" + temperature + "/100 (" + adc_temperature + "/1024)");
		}
		
		float tmp36_mVolts =(float) (adc_temperature * (3300.0/1024.0));
		// 10 mv per degree
	        float temp_C = (float) (((tmp36_mVolts - 100.0) / 10.0) - 40.0);
	        // convert celsius to fahrenheit
	        temp_F = (float) ((temp_C * 9.0 / 5.0) + 32);
	        
	        System.out.println("Ambient:" + ambient + "/100; Temperature:"+temperature+"/100 => "+String.valueOf(temp_C)+"C => "+String.valueOf(temp_F)+"F");
			

		try { Thread.sleep(500L); } catch (InterruptedException ie) { ie.printStackTrace(); }
	
		return temp_F;
	}

	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for a dealing with a single client
	 * and broadcasting its messages.
	 */
	private static class Handler extends Thread {
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private Dispatcher dispatcher;
		private int local_count;
		public int threadID;
		private InetAddress clientAddress;

		/**
		 * Constructs a handler thread, squirreling away the socket.
		 * All the interesting work is done in the run method.
		 */
		public Handler(int threadNo) {
			this.threadID = threadNo;
		}
		
		public Handler(Socket socket, int threadNo) {
			this.threadID = threadNo;
			this.socket = socket;

			// Create a new JSON-RPC 2.0 request dispatcher
			this.dispatcher =  new Dispatcher();

			// Register the "echo", "getDate" and "getTime" handlers with it
			dispatcher.register(new JsonHandler.EchoHandler());
			dispatcher.register(new JsonHandler.DateTimeHandler());
			this.clientAddress = socket.getInetAddress();


		}

		/**
		 * Services this thread's client by repeatedly requesting a
		 * screen name until a unique one has been submitted, then
		 * acknowledges the name and registers the output stream for
		 * the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() {
			if(threadID == 0) {
				System.out.println("listening for stuff");
				try {
					while(true) {
						float currentTemp = getTemperature();
						MyTipperClass blindState = new MyTipperClass();
						String blind = blindState.getBlindState(currentTemp,ambient);
				               
                                		if(blind.equals("half")){
                                        		blinkLED.lightLED(1);

                                		}	
                                		else if(blind.equals("open")) {
                                        		blinkLED.lightLED(0);
                                		}
                                		else if(blind.equals("close")) {
                                        		blinkLED.lightLED(2);
                                		}		
					//	blinkLED.shutdown();
						sleep(1000);
					}
				}catch(Exception e) {
				}
			}
			else {
			try {
				
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// read request
				String line;
				line = in.readLine();
				//System.out.println(line);
				StringBuilder raw = new StringBuilder();
				raw.append("" + line);
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
				float temp = getTemperature();
				MyTipperClass blindObj = new MyTipperClass();
				String blind = blindObj.getBlindState(temp, ambient);
		
				ArrayList<String> ruleList = blindObj.getRules();
				System.out.println("THE BLIND STATE: " + blind);			

				System.out.println("Temperature is "+temp);
				if (isPost) {
					int c = 0;
					for (int i = 0; i < contentLength; i++) {
						c = in.read();
						body.append((char) c);
					}
				}
		
				System.out.println(body.toString());
				JSONRPC2Request request = JSONRPC2Request.parse(body.toString());
				
				local_count = reqCount;
				String responseString = "{\"jsonrpc\":\"2.0\",\"id\":\"0\",\"result\":\"";
				
				
				int requestID =Integer.parseInt(request.getID().toString());
				if(requestID == 0) {
					responseString += temp +";"+ambient+"\"}"; 
				}
				else if(requestID == 1) {				
					Iterator it = ruleList.iterator();
					String rules="";
					while(it.hasNext())
						rules += it.next();

					responseString += rules + "\"}";
				}
				else if(requestID == 2) {
					List<Object> getRules = request.getPositionalParams();
					MyTipperClass insertRules = new MyTipperClass();
					insertRules.setRules(getRules.get(0).toString(), "0");
					responseString += "\"}";
				}
				else if(requestID == 3) {System.out.println("This is called---");
					List<Object> getRules = request.getPositionalParams();
					MyTipperClass insertRules = new MyTipperClass();
					insertRules.updateRule(getRules.get(1).toString(),getRules.get(0).toString());
					responseString += "\"}";		

				}
				else {
					responseString += blind + "\"}";
				}
	
				JSONRPC2Response resp = JSONRPC2Response.parse(responseString);
				//JSONRPC2Response resp =null;
				//resp.setResult(ruleList);
				//resp.setError(null); 
				//JSONRPC2Response resp = dispatcher.process(request, null);
				

				resp.setID(requestID);

				out.write("HTTP/1.1 200 OK\r\n");
				out.write("Content-Type: application/json\r\n");
				out.write("\r\n");
				out.write(resp.toJSONString());
				// do not in.close();
				out.flush();
				out.close();
				socket.close();
				
				System.out.println(clientAddress.toString());
				
				if(isSending==false) {
					isSending = true;
					JSONRPC2Session mySession=null;
					boolean done=false;
					float oldTemp = getTemperature();
					while(true) {System.out.println("Sending");
						float newTemp = getTemperature();
						if(Math.abs(newTemp-oldTemp) > 2) {
						oldTemp = newTemp;
						URL clientURL = new URL("http:/" + clientAddress +":49153");
						

						if(done==false) {
							mySession = new JSONRPC2Session(clientURL);
							done=true;
						}
						String method = "getTime";
						int requestIDs = 0;

						String tempAmbientString = newTemp +";"+ ambient;
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("temp",tempAmbientString);

						JSONRPC2Request requests = new JSONRPC2Request(method,params, requestIDs);
						//requests.setMethod("sendTemp");	 
						// Send request
						JSONRPC2Response response = null;

						try {
							 response = mySession.send(requests);
							 sleep(10000);
						} catch (Exception e) {

							System.err.println(e.getMessage());
							// handle exception...
						}
					}
					 
					}
				}
			} catch ( Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		}
	}




	public static void main(String[] args) throws Exception {
		
		System.out.println("The server is running.");
		System.out.println("-------------------------");
		new Handler(0).start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				blinkLED.shutdown();
			}
		});


		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Handler(listener.accept(),1).start();
			}
		} finally {
			listener.close();
		}
		

	}
	
	
}
