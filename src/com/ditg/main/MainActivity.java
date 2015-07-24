package com.ditg.main;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ditg.main.R;
import com.ditg.params.ITGOperations;
import com.ditg.params.ITGRecvParams;
import com.ditg.params.ITGSendParams;
import com.ditg.plot.Plot;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnNavigationListener {
    
	
	private Button btnStart,btnStop,parameters;
	public Spinner spinner;
	public static String selectedOption="ITGRecv";
	public EditText editTextIP;
	public static String serverIPAddress="",output="";
	public static TextView textView_output;
	public LinearLayout cpLayout;
	Messenger messenger = null;
	static ScrollView scroller;
	SharedPreferences.Editor editor;
	public static boolean serviceBound=false;
	
	public BasicFunctions bFunctions = new BasicFunctions();
	private ITGOperations itgOp = new ITGOperations();
	
	
	private static Handler handler = new Handler() {
		public void handleMessage(Message message) {
//			Log.v("MESSAGE", "handleMessage");
			Bundle data = message.getData();
			String msg = data.getString("message");
			if (textView_output != null) {
				textView_output.append("\n " + msg);
				scroller.post(new Runnable() {
					public void run() {
						scroller.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
			}
		}
	};

	private ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			Log.v("MESSAGE", "ServiceConnection");
			messenger = new Messenger(binder);

		}
		public void onServiceDisconnected(ComponentName className) {
			Log.v("MESSAGE", "ServicedisConnection");
			messenger = null;
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bindService();
	}

	public void bindService(){
		if(!serviceBound){
			Intent intent = null;
			intent = new Intent(this, ITGService.class);
			textView_output = (TextView) findViewById(R.id.txt_console_outputs);
			Messenger messenger = new Messenger(handler);
			intent.putExtra("messenger", messenger);
			serviceBound=bindService(intent, conn, Context.BIND_AUTO_CREATE);
		}
	}
	
	public void unBindService(){
		Log.v("UNBIND","unbined");
		if(serviceBound){
			unbindService(conn);
			serviceBound=false;
		}	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinner = (Spinner)findViewById(R.id.spinner1);
		spinner.setSelection(getIndex(spinner,getRunningITGModule())); // set previously saved ITG mode into the spinner
		cpLayout=(LinearLayout)findViewById(R.id.controlPanel);
		editor = getPreferences(MODE_PRIVATE).edit();
		scroller=(ScrollView)findViewById(R.id.scrollView1);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	
		    	selectedOption=spinner.getItemAtPosition(position).toString();
		    	if(selectedOption.equalsIgnoreCase(CommonDefinitions.ITG_SEND)){
		    		cpLayout.setBackgroundColor(Color.CYAN);
		    	}else if(selectedOption.equalsIgnoreCase(CommonDefinitions.ITG_RECV)){
		    		cpLayout.setBackgroundColor(Color.GREEN);
		    	}
		    	/*
		    	else if(selectedOption.equalsIgnoreCase(ITG_MANAGER)){
		    		cpLayout.setBackgroundColor(Color.MAGENTA);
		    	}
		    	else if(selectedOption.equalsIgnoreCase(ITG_LOG)){
		    		cpLayout.setBackgroundColor(Color.RED);
		    	}else{
		    		cpLayout.setBackgroundColor(Color.CYAN);
		    	}*/
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		textView_output=(TextView)findViewById(R.id.txt_console_outputs);
		textView_output.setText("");
		
 		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 // timer task: http://android-er.blogspot.de/2013/12/example-of-using-timer-and-timertask-on.html
	            	 startService(selectedOption);
	            	 //btnStart.setVisibility(View.INVISIBLE);
	            	 
	             }
	         });
		
		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 stopService(selectedOption);
	            	// btnStop.setVisibility(View.INVISIBLE);
	             }
	         });
		
		parameters = (Button) findViewById(R.id.btnParameters);
		parameters.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (selectedOption.equalsIgnoreCase(CommonDefinitions.ITG_SEND)) {
					startITGSendParamAct();
				} else if (selectedOption.equalsIgnoreCase(CommonDefinitions.ITG_RECV)) {
					startITGRecvParamAct();
				} 
				/*else if (selectedOption.equalsIgnoreCase(ITG_MANAGER)) {
					startITGManagerParamAct();
				} else if (selectedOption.equalsIgnoreCase(ITG_DEC)) {
					startITGDevParamAct();
				} else if (selectedOption.equalsIgnoreCase(ITG_LOG)) {
					startITGLogParamAct();
				}*/

			}
		});

		//showActionBar();
	}
	@Override
	protected void onPause() {
		super.onPause();
		unBindService();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	public void startService(String moduleName){
		Log.v("service",serviceBound+" module ->"+moduleName);
		cleanPanel();
		loadRelatedITGBinary(itgOp.ITG_DEC);
		loadRelatedITGBinary(moduleName);
		bindService();
		if(serviceBound){
			Intent intent=new Intent(this,ITGService.class);
			setITGModuleInRunning(moduleName);
			intent.putExtra("ITGMode", moduleName);
			startService(intent);
		}
	}

	public void stopService(String moduleName){
		Log.v("service",serviceBound+" module ->"+moduleName);
		if(serviceBound){
			setITGModuleInStop(moduleName);
			stopService(new Intent(this, ITGService.class));
			Message msg = Message.obtain();
			try {
				Bundle bundle = new Bundle();
				bundle.putBoolean(CommonDefinitions.STOP_ITGSERVICE, true);
				msg.setData(bundle);
				messenger.send(msg);
				unBindService();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	/////// SPINNER 
	private int getIndex(Spinner spinner, String myString)
	 {
	  int index = 0;
	  for (int i=0;i<spinner.getCount();i++){
	   if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
	    index = i;
	    i=spinner.getCount();//will stop the loop, kind of break, by making condition false
	   }
	  }
	  return index;
	 } 
		
	////// SAVE MODULES NAMES INTO THE SHARED PREF 
	public void setITGModuleInRunning(String moduleName){
		Log.v("ITG",moduleName);
		editor.putBoolean(moduleName,true).apply();
	}
	public void setITGModuleInStop(String moduleName){	
		editor.putBoolean(moduleName,false).apply();
	}
	public String getRunningITGModule(){
		SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
		boolean itgRecv=prefs.getBoolean(CommonDefinitions.ITG_RECV, false);
		boolean itgSend=prefs.getBoolean(CommonDefinitions.ITG_SEND, false);

		String result="";
		if(itgRecv){
			result= CommonDefinitions.ITG_RECV;
		}else if(itgSend){
			result= CommonDefinitions.ITG_SEND;
		}
		Log.d(CommonDefinitions.ITG,"default spinner option ->"+result);
		return result;
	}


	//////// LOAD BINARIES FOR ITGRECV and ITGSEND
	public void loadRelatedITGBinary(String modulName) {
		InputStream in;
		//String compiledName="";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		    // Run the file which was created using APP_PLATFORM := android-16
			//modulName=modulName+"16";
		} else {
		    // Run the file which was created using APP_PLATFORM := android-9
			//modulName=modulName+"9";
		}
		try {
			in = getResources().getAssets().open(modulName);
		} catch (IOException e2) {
			bFunctions.writeConsole2("Problem occured for DITG at assets");
			e2.printStackTrace();
			return;
		}
		try {
			OutputStream out = new FileOutputStream(CommonDefinitions.getModulePath(modulName), false);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			Process processChmod = Runtime
					.getRuntime()
					.exec("/system/bin/chmod 755 "+CommonDefinitions.getModulePath(modulName));
			processChmod.waitFor();
			new FileInputStream(CommonDefinitions.getModulePath(modulName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}

	/////// ACTIONBAR
	
	/**
	 * Action Bar comprising ITGRecv and ITGSend lists. Action Bar is disabled due to its usefulness, nevertheless 
	 * in case of need this function can be used.
	 */
	public void showActionBar(){
		final ActionBar actionBar = getActionBar();
		
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ArrayList<String> itemList = new ArrayList<String>();
		itemList.add("ITGSend");
		itemList.add("ITGRecv");
		ArrayAdapter<String> aAdpt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemList);
		actionBar.setListNavigationCallbacks(aAdpt, this);		
	}
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	///////// MENU
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		   if(selectedOption.equalsIgnoreCase(CommonDefinitions.ITG_SEND)){
			   switch (item.getItemId()) {
					case R.id.menu_plot:
						startPlotActivity();
						return true;
					case R.id.menu_showServerConf:
						showServerConf();
						return true;
				}
			}
		   switch (item.getItemId()) {
			case R.id.menu_sysInfo:
				startSystemInfoActivity();
				return true;
		}
//		   Toast.makeText(getApplicationContext(), "This functionality isn't enabled for ITGRecv!",  Toast.LENGTH_SHORT);
			return super.onOptionsItemSelected(item);
	}
	private void startSystemInfoActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SystemInfo.class);
		String message = "SystemInfo";
		intent.putExtra(CommonDefinitions.ITG, message);
		startActivity(intent);
	}
	
	private void startPlotActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Plot.class);
		String message = "Plot";
		intent.putExtra(CommonDefinitions.ITG, message);
		startActivity(intent);
	}
	


	///// IP PATTERN CHECK
	private static final String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static boolean validate(final String ip) {

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

//	public void showServerConfPage(){
//		Intent intent = new Intent(this, ServerConfPage.class);
//		String message = "Server Configurations";
//		intent.putExtra(CommonDefinitions.ITG, message);
//		startActivity(intent);
//	}
	//// ITGSEND Dialog and save the entered configuration parameters
	public void showServerConf() {
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText serverIP = (EditText) promptsView	
				.findViewById(R.id.edt_serverIP);
		
		final EditText serverPort = (EditText) promptsView
				.findViewById(R.id.edt_serverPort);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						saveServerConfigurations(serverIP.getText().toString(),
								serverPort.getText().toString());
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	public void saveServerConfigurations(String ip, String port) {
		if (validate(ip)) {
			itgOp.serverIPAddress = ip;
			itgOp.serverPortAddress = port;
		} else {
			Toast.makeText(getApplicationContext(),
					"The format of IP Address isn't appropriate!",
					Toast.LENGTH_LONG);
		}

	}
	/////////// START PARAMETERS ACTIVITIES
	public void startITGSendParamAct(){
		Intent intent = new Intent(this, ITGSendParams.class);
		String message = "ITGSend";
		intent.putExtra(CommonDefinitions.ITG_MESSAGE, message);
	    startActivity(intent);
	}
	
	public void startITGRecvParamAct(){
		Intent intent = new Intent(this, ITGRecvParams.class);
		String message = "ITGRecv";
		intent.putExtra(CommonDefinitions.ITG_MESSAGE, message);
	    startActivity(intent);
	}
	
	public void startITGManagerParamAct(){
		
	}
	
	public void startITGDevParamAct(){
		
	}
	
	public void startITGLogParamAct(){
		
	}
	//// UTILS
	public void cleanPanel(){
		textView_output.setText("");
	}

	@SuppressLint("ShowToast")
	public void popupMessage(String message){
		Toast.makeText(getApplicationContext(), message,  Toast.LENGTH_SHORT);
	}

	    
}
