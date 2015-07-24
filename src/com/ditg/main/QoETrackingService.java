package com.ditg.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import dai.cnm.conn.JavaToJavaClient;
import dai.cnm.json.JSONObject;
import dai.cnm.utils.CommonFunctions;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;

import android.os.Messenger;
import android.widget.LinearLayout;

public class QoETrackingService extends Activity {

	public BasicFunctions bFunctions = new BasicFunctions();
	public boolean activityContinue = true;
	public String debugFilesPath;
	public DITGTool ditgTool;

	// Target we publish for clients to send messages to IncomingHandler.
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	
	public void onCreate() {
		
		
		bFunctions.writeConsole2("On create method is called in QoETrackingService");

		File externalSdcard = new File(
				Environment.getExternalStorageDirectory() + "");
		if (externalSdcard.exists()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/.QoEExperiences");
			dir.mkdirs();
		}
		debugFilesPath = Environment.getExternalStorageDirectory()
				+ "/.QoEExperiences/";
		String selectedOption="ITGSend";
		initAll(selectedOption);
	}

	@Override
	public void onDestroy() {
		System.out.println("On destroy method is called in ClientTrackingService");
		activityContinue = false;
		ditgTool.cancel(true);
	}

	public String getLocalIpAddress() {
		WifiManager wifiManager;
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String first = "" + (ip & 0xff);
		String second = "" + (ip >> 8 & 0xff);
		String third = "" + (ip >> 16 & 0xff);
		String fourth = "" + (ip >> 24 & 0xff);
		String endIp = first + "." + second + "." + third + "." + fourth;
		return endIp;
	}

	public String getCurrentAPSSID() {
		WifiManager wifiManager;
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getSSID();
	}

	public String getCurrentAPBSSID() {
		WifiManager wifiManager;
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getBSSID();
	}

	public String getIperfServIP() {
		String[] ipAddress = getLocalIpAddress().split(".");
		String netAdr = "";
		for (int i = 0; i < ipAddress.length - 1; i++) {
			netAdr = netAdr + ipAddress[i] + ".";
		}
		netAdr = netAdr + "1";
		return netAdr;
	}

	public String getNetworkIP() {
		String[] ipAddress = getLocalIpAddress().split(".");
		String netAdr = "";
		for (int i = 0; i < ipAddress.length - 1; i++) {
			netAdr = netAdr + ipAddress[i] + ".";
		}
		netAdr = netAdr + "1";
		return netAdr;
	}

	public void initAll(String modul) {
		InputStream in;
		try {
			in = getResources().getAssets().open(modul);
		} catch (IOException e2) {
			bFunctions.writeConsole2("Problem occured for ditg at assets");
			e2.printStackTrace();
			return;
		}
		try {
			OutputStream out = new FileOutputStream("/data/data/com.example.ditg/"+modul, false);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			Process processChmod = Runtime
					.getRuntime()
					.exec("/system/bin/chmod 755 /data/data/com.example.ditg/"+modul);
			processChmod.waitFor();
			new FileInputStream("/data/data/com.example.ditg/"+modul);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ditgTool = new DITGTool();
		ditgTool.execute();
		return;
	}

	String serverIP = "192.168.178";
	public static boolean selected = true;

	class DITGTool extends AsyncTask<Void, String, Void> {
		String iperfOutput = "";
		String itgSendPath = "/data/data/com.example.ditg/ITGSend";
		String itgRecvPath = "/data/data/com.example.ditg/ITGRecv";

		@Override
        protected Void doInBackground(Void... params) {
    		try{
    			bFunctions.writeConsole2("Experience Tracker started successfully "); 
				while (activityContinue) {
					if (isConnectedToWifi()) {

						ProcessBuilder pb = null;
						if (selected) {
							pb = new ProcessBuilder(itgSendPath, "-a",
									serverIP, "-rp", "9524", "-E", "100", "-e",
									"750", "-T", "TCP", "-t", "1000", "-m",
									"rttm", "-l", "sendLogFile");
							pb.redirectErrorStream(true);
						} else {
							pb = new ProcessBuilder(itgRecvPath);
							pb.redirectErrorStream(true);
						}
						if (pb != null) {

							Process process = pb.start();
							BufferedReader br = new BufferedReader(
									new InputStreamReader(
											process.getInputStream()));

							String line = "";
							boolean noError = true;
							while (((line = br.readLine()) != null) && noError) {
								bFunctions.writeConsole(line);
								if (line.contains("Error")
										|| line.contains("error")
										|| line.contains("ERROR")) {
									if ((line.contains("Busy port"))) {
										bFunctions
												.writeConsole("Port is busy now on ITGRecv side");
									} else if ((line
											.contains("Connection refused"))) {
										bFunctions
												.writeConsole("Connection refused from ITGRecv side");

									} else if ((line.contains("No route"))) {
										bFunctions
												.writeConsole("No Route to host");
										// Connectivity.connectToRandomAP();
									} else {
										bFunctions
												.writeConsole("Unknown Error occured");
										bFunctions.writeConsole(line);

									}
									noError = false;
									CommonFunctions.randomBackOfftime();
									appendTestResultsToExperienceFile("Error ITG");
									break;
								}
							}
							if (noError == true) {
								// There is a problem with
								bFunctions
										.writeConsole("Waiting for the exit code of ITG ");
								/*
								 * There is a problem with the ITGSend.
								 * Sometimes it does not send an exit value.
								 * Finding a timeout mechanism for waiting might
								 * solve the problem.
								 */
								/*
								 * Thread.sleep(2000); if ( process.exitValue()
								 * != 0) { process.destroy(); }
								 */int exitValue = process.waitFor();
								System.out.println("ITGSend Exited with code "
										+ exitValue);
								// Thread.sleep(2);
								if (exitValue == 0) {

									bFunctions
											.writeConsole("****** Successfull test on Network *****");

								} else if (exitValue == 1) {// temporary
									// code

									bFunctions
											.writeConsole("****** Successfull test on Network *****");
								} else {
									bFunctions
											.writeConsole("EXIT VALUE of ITGSend "
													+ exitValue);
									// break;
								}
							}
							process.destroy();
							CommonFunctions.randomBackOfftime();
							bFunctions.writeConsole2("EP Provider finished");
						}
					}
				}
    		}catch(Exception e){
    			e.printStackTrace(); 
    		}
    		return null; 
    	}

		@Override
		protected void onProgressUpdate(String... values) {
			Intent broadcastIntent = new Intent();
			broadcastIntent.putExtra("VAL", "DITG: " + values[0].toString());
			broadcastIntent.setAction("MESSAGE");
			getBaseContext().sendBroadcast(broadcastIntent);
		}

		public JavaToJavaClient mainClient;
		public int APSocketNumber = 13132;

		protected void sendTestResultsToAP(JSONObject obj) {
			String ourResponse = obj.toString();
			try {
				mainClient = new JavaToJavaClient(APSocketNumber);
				bFunctions.writeConsole2("IPAddress of Server "
						+ getNetworkIP());
				long QoSvalue = mainClient.send(ourResponse, getNetworkIP());
				if (QoSvalue < 0) {
					bFunctions.writeConsole2("No connection for QoE values ");
				} else if (QoSvalue == 1) {
					bFunctions
							.writeConsole2("Socket connection timeout, continuing ");
				} else if (QoSvalue == 2) {
					bFunctions
							.writeConsole2("Socket connection error, continuing ");
				} else {
					bFunctions.writeConsole2("QoE successfully sended");
				}

			} catch (Exception e) {
				e.printStackTrace();
				bFunctions
						.writeConsole2("\n  Problem occurred by sending test results");
			}
		}
	}

	
	public boolean isConnectedToWifi() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		bFunctions.writeConsole2("Checking connectivity: "
				+ mWifi.isConnected());
		return mWifi.isConnected();
	}

	public void appendTestResultsToExperienceFile(String str) {

	}

	public void appendTestResultsToExperienceFile(JSONObject obj) {

	}

}
