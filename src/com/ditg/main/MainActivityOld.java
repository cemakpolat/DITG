package com.ditg.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ditg.params.ITGOperations;
import com.ditg.params.ITGRecvParams;
import com.ditg.params.ITGSendParams;
import com.wifitool.util.ShellInterface;

import dai.cnm.conn.JavaToJavaClient;
import dai.cnm.json.JSONObject;
import dai.cnm.utils.CommonFunctions;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityOld extends Activity {
    
	
	private Button btnStart,btnStop;
	public DITGTool ditgTool;
	public BasicFunctions bFunctions = new BasicFunctions();
	public boolean activityContinue = true;
	public Spinner spinner;
	public static String selectedOption="";
	public EditText editTextIP;
	public static String serverIPAddress="",output="";
	public TextView textView_output;
	public LinearLayout cpLayout;
	public Button parameters;
	
	List<String> groupList;
//	Map<String, ArrayList<ITGItem>> itgSendCollection;
//	Map<String, ArrayList<ITGItem>> itgDefaultCollection;
//	
	String itgSendPath = "/data/data/com.example.ditg/ITGSend";
	String itgRecvPath = "/data/data/com.example.ditg/ITGRecv";
	final String ITG_SEND="ITGSend";
	final String ITG_RECV="ITGRecv";
	
	// Future Work
	final String ITG_LOG="ITGLog";
	final String ITG_MANAGER="ITGManager";
	final String ITG_DEC="ITGDec";
	
	// PID=ps -ef | grep myProcess | grep -v grep | awk '{printf "%i",$2}'
	List<String> commands = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinner = (Spinner)findViewById(R.id.spinner1);
		selectedOption = spinner.getSelectedItem().toString();
		cpLayout=(LinearLayout)findViewById(R.id.controlPanel);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	selectedOption=spinner.getItemAtPosition(position).toString();
		    	//Log.v("SELECTED: ",selectedOption+ spinner.getSelectedItem().toString() );
		    	if(selectedOption.equalsIgnoreCase(ITG_SEND)){
		    		cpLayout.setBackgroundColor(Color.CYAN);
		    	}else if(selectedOption.equalsIgnoreCase(ITG_RECV)){
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
	         		
	            	 while(!isITGActive()){
	        			itgOp=new ITGOperations();
	        			itgOp.createConfigurationFile(selectedOption);
	        			commands=itgOp.getParamList();
	        			popupMessage("Previous process is still active");
	        			// wait here a bit and retry.
	         		}
	            	 activityContinue=true;
	            	 if (selectedOption.equalsIgnoreCase(ITG_SEND)) {
	 		    		initAll(ITG_SEND);
	            	 } else if (selectedOption.equalsIgnoreCase(ITG_RECV)) {
	 		    		initAll(ITG_RECV);
	 		    	}
	             }
	         });
		
		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 activityContinue=false;
	            	 if(timer!=null && process!=null){
	            		 timer.cancel();
	            		 process.destroy();
	            	 	 killITGServer();
	            	 }
	             }
	         });
	
		
		
		
		parameters = (Button) findViewById(R.id.btnParameters);
		parameters.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (selectedOption.equalsIgnoreCase(ITG_SEND)) {
					startITGSendParamAct();
				} else if (selectedOption.equalsIgnoreCase(ITG_RECV)) {
					startITGRecvParamAct();
				} else if (selectedOption.equalsIgnoreCase(ITG_MANAGER)) {
					startITGManagerParamAct();
				} else if (selectedOption.equalsIgnoreCase(ITG_DEC)) {
					startITGDevParamAct();
				} else if (selectedOption.equalsIgnoreCase(ITG_LOG)) {
					startITGLogParamAct();
				}

			}
		});
		
	}
	
	public void killITGServer(){
		if(pid!=""){
			Log.v("ITGRecv","Killed processed ID:"+pid);
			Runnable runnable=new Runnable() {
 	                public void run() {       
 	                    try {	   
 	                    	shell.runAsRootWithOutput("kill "+pid);
	                    } catch (Exception e) {
	                        // TODO Auto-generated catch block
	                    }
 	                }};
 	                Thread thread=new Thread(runnable);
 	                thread.start();
 	                thread.interrupt(); // this is required, because read method is blocking while reading from console. 
		}else{
			popupMessage("Process of ITGServer is not valid");
		}
	}
	public void popupMessage(String message){
		Toast.makeText(getApplicationContext(), message,  Toast.LENGTH_LONG);
	}
	public boolean isITGActive(){
		return itgStopped;
	}
	public void setITGRunning(){
		itgStopped=true;
	}
	public void setRunningITGModule(String moduleName){
		// runningModule=moduleName;
	}
	
	public String getRunningITGModule(){
		return "" ;
	}


	ShellInterface shell=new ShellInterface();
	public final static String MESSAGE_MESSAGE= "com.example.ditg.MESSAGE";
	public void startITGSendParamAct(){
		Intent intent = new Intent(this, ITGSendParams.class);
		String message = "ITGSend";
		intent.putExtra(MESSAGE_MESSAGE, message);
	    startActivity(intent);
	}
	
	public void startITGRecvParamAct(){
		Intent intent = new Intent(this, ITGRecvParams.class);
		String message = "ITGRecv";
		intent.putExtra(MESSAGE_MESSAGE, message);
	    startActivity(intent);
	}
	public void saveResultDialog(){
		
	}
	public void startITGManagerParamAct(){
	
	}
	
	public void startITGDevParamAct(){
		
	}
	
	public void startITGLogParamAct(){
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("On destroy method is called in DITG Tool");
		activityContinue = false;
		if(ditgTool!=null){
			ditgTool.cancel(true);
		}
	}

	private ITGOperations itgOp;
	public void initAll(String modul) {
		InputStream in;
		try {
			in = getResources().getAssets().open(modul);
		} catch (IOException e2) {
			bFunctions.writeConsole2("Problem occured for DITG at assets");
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
					.exec("/system/bin/chmod 744 /data/data/com.example.ditg/"+modul);
			processChmod.waitFor();
			new FileInputStream("/data/data/com.example.ditg/"+modul);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//TODO: get list from ITG Operations
		if(this.selectedOption.equalsIgnoreCase(this.ITG_RECV)){
			callAsynchronousTaskForITGRecv();
		}else{
			ditgTool = new DITGTool();
			ditgTool.execute();	
		}
		
		
		return;
	}

	public static boolean selected = true;
	static boolean itgStopped=true;
	public String command = "ps | grep ITGRecv | grep -v grep | awk '{printf \"%i\",$2}'";
	public String pid="";
	Timer   timer=null;
	public void callAsynchronousTaskForITGRecv() {
	    final Handler handler = new Handler();
	    timer = new Timer();
	    TimerTask doAsynchronousTask = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() {       
	                    try {	   
	                    	  Runnable runnable=new Runnable() {
	          	                public void run() {       
	          	                    try {	   
	          	                    	pid=shell.runAsRootWithOutput(command);
	          	                    	Log.v("ITGRecv","Running ITGRecv processed ID:"+pid);
	        	                    } catch (Exception e) {
	        	                        // TODO Auto-generated catch block
	        	                    }
	          	                }};
	          	                Thread thread=new Thread(runnable);
	          	                thread.start();
	          	                thread.interrupt();   
//	          	                int p=0;
//	          	                try{
//	          	                	p=Integer.parseInt(pid);
//	          	                }catch(NumberFormatException e){
//	          	                	p=0;
//	          	                	System.out.println("Exception :"+p);
//	          	                }
//	            			
//	                    	if(p>1){
//	                    		   runnable=new Runnable() {
//	  	          	                public void run() {       
//	  	          	                    try {	   
//	  	          	                    	shell.runAsRootWithOutput("kill "+pid);
//	  	        	                    } catch (Exception e) {
//	  	        	                        // TODO Auto-generated catch block
//	  	        	                    }
//	  	          	                }};
//	  	          	                thread=new Thread(runnable);
//	  	          	                thread.start();
////	  	          	                Thread.sleep(1000);
//	  	          	                thread.interrupt(); // this is required, because read method is blocking while reading from console. 
//	                    		
//	                    	}
//	                    	pid="";
	                    	DITGTool performBackgroundTask = new DITGTool();
	                         //PerformBackgroundTask this class is the class that extends AsynchTask 
	                        performBackgroundTask.execute();
	                       // Thread.sleep(10000);
	                    	
	                    } catch (Exception e) {
	                        // TODO Auto-generated catch block
	                    }
	                }

	            });
	        }
	    };
	    timer.schedule(doAsynchronousTask, 0, 100000); //execute in every 50000 ms
	    
	    
	}
	Process process = null;
	boolean noError=false;
	class DITGTool extends AsyncTask<Void, String, String> {
		
		
		protected void onPreExecute (){
	        Log.d("PreExceute","On pre Exceute......");
	    }

		@Override
        protected String doInBackground(Void... params) {
    		try{
    			String processPath=itgRecvPath;
    			bFunctions.writeConsole2("Experience Tracker started successfully "); 
				while (activityContinue) {
					bFunctions.writeConsole2("Experience Tracker started successfully 1");
					if (selectedOption.equalsIgnoreCase("ITGSend")) {
						if (!isConnectedToWifi()) {
							activityContinue=false;
							break;
						}
						processPath=itgSendPath;
					}
					ProcessBuilder pb = null;
					publishProgress(selectedOption+" is started");
					
					pb = new ProcessBuilder(processPath);
					pb.redirectErrorStream(true);
					if (pb != null) {

						process = pb.start();
						BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

						String line = "";
						noError = true;
						while (((line = br.readLine()) != null) && noError) {
							bFunctions.writeConsole(line);
				            publishProgress(line);
							if (line.contains("Error")|| line.contains("error")|| line.contains("ERROR")) {
								if ((line.contains("Busy port"))) {
									bFunctions.writeConsole("Port is busy now on ITGRecv side");
								} else if ((line.contains("Connection refused"))) {
									bFunctions.writeConsole("Connection refused from ITGRecv side");

								} else if ((line.contains("No route"))) {
									bFunctions.writeConsole("No Route to host");
									// Connectivity.connectToRandomAP();
								} else {
									bFunctions.writeConsole("Unknown Error occured");
									bFunctions.writeConsole(line);

								}
								noError = false;
								appendTestResultsToExperienceFile("Error ITG");
								break;
							}
						}
						if (noError == true) {
							// There is a problem with
							bFunctions.writeConsole("Waiting for the exit code of ITG ");
							/*
							 * There is a problem with the ITGSend.
							 * Sometimes it does not send an exit value.
							 * Finding a timeout mechanism for waiting might
							 * solve the problem.
							 */
							int exitValue = process.waitFor();
							System.out.println("ITGSend Exited with code "+ exitValue);

							if (exitValue == 0) {
								bFunctions.writeConsole("****** Successfull test on Network *****");

							} else if (exitValue == 1) {// temporary
								// code

								bFunctions.writeConsole("****** Successfull test on Network *****");
							} else {
								bFunctions.writeConsole("EXIT VALUE of ITGSend "+ exitValue);
								 break;
							}
						}
						publishProgress("Process is being destroyed");
						process.destroy();
						if(selectedOption.equalsIgnoreCase("ITGSend")){
							CommonFunctions.randomBackOfftime();
						}
					}
					
				}
				
    		}catch(Exception e){
    			e.printStackTrace(); 
    		}
    		return finalized; 
    	}
		final static String finalized="finalized";
		@Override
		protected void onProgressUpdate(String... values) {
			Log.d("PROGRESS",values[0].toString());
			textView_output.append("\n"+values[0].toString());
		}
		 @Override
	     protected void onPostExecute(String result) {
		      Log.d("Post execute",result);
	          //TextView txt = (TextView) findViewById(R.id.txt_console_outputs);
	          //txt.setText("Executed"); 
		      popupMessage("Test Finalized");
	          if(result.equalsIgnoreCase(finalized)){
	        	  itgStopped=true;
	          }  
	        
	        }
		public JavaToJavaClient mainClient;
		public int APSocketNumber = 13132;

		protected void sendTestResultsToAP(JSONObject obj) {
//			String ourResponse = obj.toString();
//			try {
//				mainClient = new JavaToJavaClient(APSocketNumber);
//				bFunctions.writeConsole2("IPAddress of Server "
//						+ getNetworkIP());
//				long QoSvalue = mainClient.send(ourResponse, getNetworkIP());
//				if (QoSvalue < 0) {
//					bFunctions.writeConsole2("No connection for QoE values ");
//				} else if (QoSvalue == 1) {
//					bFunctions
//							.writeConsole2("Socket connection timeout, continuing ");
//				} else if (QoSvalue == 2) {
//					bFunctions
//							.writeConsole2("Socket connection error, continuing ");
//				} else {
//					bFunctions.writeConsole2("QoE successfully sended");
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				bFunctions
//						.writeConsole2("\n  Problem occurred by sending test results");
//			}
		}
	}

//	DBInterface db=new DBInterface();
//	JSONObject itgObj=null;
//	private ArrayList<String> getITGObject() throws JSONException{
//		ArrayList<String> list=new ArrayList<String>();
//		itgObj=db.getITGObject(selectedOption);
//
////		itgObj=db.getITGObject("ITGSend");
//		if(itgObj!=null){
//			JSONArray array=itgObj.names();
//			for(int i=0;i<array.length();i++){
//				if(!array.getString(i).equalsIgnoreCase("exp")){
//					list.add(array.getString(i));
//				}
//			}
//		}
//    	return list;
//	}

//	private void createGroupList() {
//		try {
//			groupList = getITGObject();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//	
//	private void collectAllParameters(){
//		for(int i=0;i<itgSendCollection.size();i++){
//			ArrayList<ITGItem> list=itgSendCollection.get(this.groupList.get(i));
//			for(int j=0;j<list.size();j++){
//				commands.add(list.get(j).param);
//				if(!list.get(j).value.equalsIgnoreCase("none")){
//					commands.add(list.get(j).value);	
//				}
//			}
//		}
//	}
//	private void createCollection() throws JSONException {
//		itgSendCollection = new LinkedHashMap<String, ArrayList<ITGItem>>();
//		
//		ArrayList<ITGItem> list=null;
//		for (String optionName : groupList) {
//			JSONObject obj=itgObj.getJSONObject(optionName);
//			JSONArray array=obj.names();
//			list=new ArrayList<ITGItem>();
//			boolean paramEnabled=false;
//			for(int i=0;i<array.length();i++){
//				
//				ITGItem item=new ITGItem();
//				if(!array.getString(i).equalsIgnoreCase("exp")){
//					JSONObject obj1=obj.getJSONObject(array.getString(i));
//					
//					item.enabled=obj1.getBoolean("enable");
//					if(item.enabled){
//						paramEnabled=true;
//						item.param=obj1.getString("param");
//						if(obj1.getString("value").equalsIgnoreCase("none")){
//							item.value=null;
//						}else{
//							item.value=obj1.getString("value");
//						}
//						if(obj1.getString("unit").equalsIgnoreCase("none")){
//							item.unit=null;
//						}else{
//							item.unit=obj1.getString("unit");
//						}
//						
//						if(obj1.getJSONArray("options").length()>0){
//							JSONArray jsonarray=obj1.getJSONArray("options");
//							item.options=new ArrayList<String>();
//							for(int m=0;m<jsonarray.length();m++){
//								item.options.add(jsonarray.get(m).toString());	
//							}
//						}else{
//							item.options=null;
//						}
//						item.exp=obj1.getString("exp");
//					}
//					
//				}
//				this.writeOut(item.param);
//				if(paramEnabled){
//					list.add(item);	
//				}
//			}
//			itgSendCollection.put(optionName, list);
//		}
//	}
	public void writeOut(String str){
		System.out.println(str);
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
	 
	public class SleepInterrupt extends Object implements Runnable {
	  public void run() {
	    try {
	      System.out.println("in run() - sleep for 20 seconds");
	      Thread.sleep(10000);
	      System.out.println("in run() - woke up");
	    } catch (InterruptedException x) {
	      System.out.println("in run() - interrupted while sleeping");
	      return;
	    }
	    System.out.println("in run() - leaving normally");
	  }

//	  public static void main(String[] args) {
//	    SleepInterrupt si = new SleepInterrupt();
//	    Thread t = new Thread(si);
//	    t.start();
//
//	    // Be sure that the new thread gets a chance to
//	    // run for a while.
//	    try {
//	      Thread.sleep(2000);
//	    } catch (InterruptedException x) {
//	    }
//
//	    System.out.println("in main() - interrupting other thread");
//	    t.interrupt();
//	    System.out.println("in main() - leaving");
//	  }
	}

	    
}
