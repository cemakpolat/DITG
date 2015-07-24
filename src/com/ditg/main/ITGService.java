package com.ditg.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ditg.params.ITGOperations;
import com.wifitool.util.ShellInterface;

import dai.cnm.conn.JavaToJavaClient;
import dai.cnm.json.JSONArray;
import dai.cnm.json.JSONException;
import dai.cnm.json.JSONObject;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//TODO: http://stackoverflow.com/questions/20594936/communication-between-activity-and-service
public class ITGService extends Service 
{   
	
	static boolean itgRunning = true;
	static boolean itgStop = false;
	public static String output = "";
//	final String ITGSEND_PATH = "/data/data/com.ditg.main/ITGSend";
//	final String ITGRECV_PATH = "/data/data/com.ditg.main/ITGRecv";
//	public static String ITGSend = "ITGSend";
//	public static String ITGRecv = "ITGRecv";
//	final String STOP_ITGSERVICE="STOP";
//	public final String ITG="ITG MESSAGE ";
	
	ShellInterface shell=new ShellInterface();
	Timer   timer=null;
	Process process = null;
	boolean noError=false;
	
	public BasicFunctions bFunctions = new BasicFunctions();
	private ITGOperations itgOp = new ITGOperations();
	List<String> commands = new ArrayList<String>();
	
	/* For enabling the request of checking the wifi connection*/
	public static boolean serverDataTrasfer = false;
	public boolean wifiNetControl=true;
	public JavaToJavaClient mainClient;
	public int APSocketNumber = 13132;
	  // Use to send message to the Activity
    private Messenger outMessenger;	
	
    // Used to receive messages from the Activity
    final Messenger inMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler 
    {
        @Override
        public void handleMessage(Message msg) 
        {
            Bundle data = msg.getData();
            if(data.getBoolean(CommonDefinitions.STOP_ITGSERVICE, true)){
            	Log.v(CommonDefinitions.ITG,"itgRunning"+itgRunning);
            	stopITGRecv();
            	stopSelf();
            	itgRunning =false;
            	itgStop=true;
            	
            }else{
            	 Log.v(CommonDefinitions.ITG,"itgRunning:"+itgRunning);
            	itgRunning =true;
            }
        }
     }
    public ITGService () 
    {
          super();
    }

    NotificationManager mNotificationManager;
    Notification notification;
	private int NOTIFICATION =423434;

    @Override
    public IBinder onBind(Intent intent) 
    {
        Bundle extras = intent.getExtras();
        if (extras != null) 
        {
            outMessenger = (Messenger) extras.get("messenger");
            Log.v(CommonDefinitions.ITG,"onbind messager");
        }
        return inMessenger.getBinder();
    }

    @Override
    public void onCreate() 
    {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() 
    {
        super.onDestroy();
        mNotificationManager.cancel(NOTIFICATION);
        stopITGRecv();
       // Toast.makeText(this, "My Service Destroyed", Toast.LENGTH_SHORT).show();
    }
    public static String currentMode="";
    @Override
    public int onStartCommand(Intent intent, int flags, int startid) 
    {
    	itgStop=false;
    	itgRunning =false;
        super.onStart(intent, startid);
        String mode = intent.getStringExtra("ITGMode");
    	Log.v("ITG Mode",mode);
    	currentMode=mode;
        if(mode.equalsIgnoreCase("ITGRECV")){
        	runITGRecv();	
        	Log.v("ITG","ITGRECV is called");
        }else if(mode.equalsIgnoreCase("ITGSEND")){
        	runITGSend();
        	Log.v("ITG","ITGSEND is called");
        }
        publishNotification();
        return START_STICKY;
    }
    public void publishNotification(){
		Random generator = new Random();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(currentMode)
				.setContentText("D-ITG started")
				// .setProgress(100, 0, false)
				.setContentIntent(
						PendingIntent.getActivity(getApplicationContext(),
								generator.nextInt(), new Intent(
										getApplicationContext(),
										MainActivity.class),
								PendingIntent.FLAG_UPDATE_CURRENT));

		notification = mBuilder.build();
		startForeground( NOTIFICATION, notification );
		mNotificationManager.notify(NOTIFICATION, notification);
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
	}

	public void stopITGRecv() {
		if(process != null){
			Log.v("ITG","process is not null");
			process.destroy();
		}
		if (timer != null ) {
			Log.v("ITG","timer is not null");
			timer.cancel();
		}	
	}
  

   public void runITGRecv() {
	   itgRunning=true;
		timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				DITGRecv performBackgroundTask = new DITGRecv();
				performBackgroundTask.execute();
			}
		};

		timer.schedule(doAsynchronousTask, 0, 100000); // execute in every 50000
														// ms
		
	}
	public void runITGSend(){
		itgRunning=true;
		DITGSend performBackgroundTask = new DITGSend();
		performBackgroundTask.execute();
		
	}
	
	class DITGRecv extends AsyncTask<Void, String, String> {
		final static String finalized="finalized";
		protected void onPreExecute (){
	        Log.d("PreExceute","On pre Exceute......");
	    }

		@Override
        protected String doInBackground(Void... params) {
			try {
				itgOp=new ITGOperations();
				itgOp.collectAllParameters(CommonDefinitions.ITG_RECV);
				itgOp.saveITGRecvChanges();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			commands=itgOp.getParamList();
    		try{
    			// TODO: Parameters should be added here 
    			//String processPath=CommonDefinitions.ITGRECV_PATH;
				while (itgRunning) {
					
					ProcessBuilder pb = null;
					pb = new ProcessBuilder(commands);
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
								//appendTestResultsToExperienceFile("Error ITG");
								break;
							}
						}
						if (noError == true) {
							// There is a problem with
							bFunctions.writeConsole("Waiting for the exit code of ITG ");

							int exitValue = process.waitFor();
							System.out.println("ITGSend Exited with code "+ exitValue);

							if (exitValue == 0) {
								bFunctions.writeConsole("****** Successfull test on Network *****");

							} else if (exitValue == 1) {
								bFunctions.writeConsole("****** Successfull test on Network *****");
							} else {
								bFunctions.writeConsole("EXIT VALUE of ITGSend "+ exitValue);
								 break;
							}
						}
						publishProgress("Process is being destroyed");
						process.destroy();
					}	
				}
    		}catch(Exception e){
    			e.printStackTrace(); 
    		}
    		return finalized; 
    	}
		
		@Override
		protected void onProgressUpdate(String... values) {
			Log.d("PROGRESS",values[0].toString());
			sendMessage(values[0].toString());
		}
		 @Override
	    protected void onPostExecute(String result) {
		      Log.d("Post execute",result);		      
	          if(result.equalsIgnoreCase(finalized)){
	        	  itgRunning=false;
	          }  
	          sendMessage("ITG Process is finalized");
		 }
	}
	
	/**
	 * 
	 * @author cemakpolat
	 */
	class DITGSend extends AsyncTask<Void, String, String> {
		final static String finalized="finalized";
		
		protected void onPreExecute (){
	        Log.d("PreExceute","On pre Exceute......");
	    }

		@Override
        protected String doInBackground(Void... params) {
			itgOp=new ITGOperations();
			itgOp.collectAllParameters(CommonDefinitions.ITGSend);
			try {
				itgOp.saveITGSendChanges();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commands=itgOp.getParamList();
			
	    	try{
				bFunctions.writeConsole2("Experience Tracker started successfully ");
				Log.v("STARTED","message");
				if(!isConnectedToWifi() && wifiNetControl){
					publishProgress("Wifi Connection doesn't exist!");
					itgRunning=false;
				}
				while (itgRunning) {
					Log.v(CommonDefinitions.ITG,"ITGSend Called");
					ProcessBuilder pb = null;
					String command="";

					commands=itgOp.getDummyParamList();// TODO: ITG Send uses dummy list 

					pb = new ProcessBuilder(commands);
					pb.redirectErrorStream(true);
					if (pb != null) {

						Process process = pb.start();
						BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

						String line = "";
						boolean noError = true;
		
						while (((line = br.readLine()) != null) && noError) {
							if (line.contains("Error")|| line.contains("error")|| line.contains("ERROR")) {
								bFunctions.writeConsole(line);
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
								publishProgress(line);
								break;
							}else if(line.contains("|")){
								Pattern pattern = Pattern.compile("|"); 
								Matcher matcher = pattern.matcher(line);
								int count = 0;
								while (matcher.find()) count++;
								if(!(count>2)){
									sendMessage(line);
								}else{
									bFunctions.writeConsole(line);
								}
							}else{
								bFunctions.writeConsole(line);
								publishProgress(line);
							}
							
						}
						publishProgress(line);
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
								JSONObject obj = itgOp.readTestResults();
								if(obj!=null){
									publishProgress("*****Flow Result****");
									publishProgress(obj.toString());
									publishProgress("********************");
									if(serverDataTrasfer){
										sendTestResultsToAP(obj);
									}
									appendTestResultsToExperienceFile(obj.toString());
								}
							} else if (exitValue == 1) {// temporary
								// code
								JSONObject obj = itgOp.readTestResults();
								if(obj!=null){
									publishProgress("*****Flow Result****");
									publishProgress(obj.toString());
									publishProgress("********************");
									if(serverDataTrasfer){
										sendTestResultsToAP(obj);
									}
									appendTestResultsToExperienceFile(obj.toString());
								}
								bFunctions.writeConsole("****** Successfull test on Network *****");
							} else {
								bFunctions.writeConsole("EXIT VALUE of ITGSend "+ exitValue);
								// break;
							}
						}
						bFunctions.writeConsole("ITGRUNNING:"+itgRunning+" ITGSTOP:"+itgStop);
						process.destroy();
					
					}
					bFunctions.writeConsole("ITGRUNNING:"+itgRunning+" ITGSTOP:"+itgStop);

					if(itgStop){
						itgRunning=false;
					}else{
						randomBackOfftime();	
					}
				}
				
				if(!isConnectedToWifi() && wifiNetControl){
					bFunctions.writeConsole("Please check your IP Address");
					itgRunning=false;
				}
				else{
					bFunctions.writeConsole("The process is completed");
				}

			}catch(Exception e){
				e.printStackTrace(); 
			}
    		return finalized; 
    	}
		
		@Override
		protected void onProgressUpdate(String... values) {

			if(values!=null || values.length>1){
				try{
					Log.d("PROGRESS",values[0].toString());
					sendMessage(values[0].toString());	
				}catch(Exception e){
					System.out.println(e);
				}
			}else{
				Log.d("PROGRESS",values.toString());
			}
			
		}
		 @Override
	    protected void onPostExecute(String result) {
		      Log.d("Post execute",result);		      
	          sendMessage("ITG Process is finalized");
		 }
	}
	

	public void sendMessage(String message){
		bFunctions.writeConsole("The process is completed "+ message);
		Message backMsg = Message.obtain();
	    Bundle bundle = new Bundle();
	    bundle.putString("message", message);
	    backMsg.setData(bundle);
	    try {
			outMessenger.send(backMsg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void sendTestResultsToAP(JSONObject obj) {
		String ourResponse = obj.toString();
		try {
			mainClient = new JavaToJavaClient(APSocketNumber);
			bFunctions.writeConsole2("IPAddress of Server "
					+ itgOp.serverIPAddress);
			long QoSvalue = mainClient.send(ourResponse,itgOp.serverIPAddress);
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
	public JSONArray collectedMeasurements=null; 
	public static int ditgResultLimit=30;
	public void saveMeasurements(){
		try {
			if(!itgRunning){
				JSONArray	objArray = itgOp.getJSONArrayFromFile(itgOp.ditgResults);
				this.writeOut(objArray.toString());
				if(objArray.length()>ditgResultLimit){
					objArray.remove(0);
				}
				objArray.put(collectedMeasurements);
				itgOp.fileWriteBasic(objArray.toString());
				collectedMeasurements=null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeOut(String str){
		System.out.println(str);
	}
	public void randomBackOfftime(){
		//Log.v("RADNDOM BACKOFF"," start");
		 Random randomNumberGenerator = new Random();
	     int randomInstance = randomNumberGenerator.nextInt(10);
	     randomInstance++;
	     try {
	         Thread.sleep(randomInstance * 1000);
	     } catch (InterruptedException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	     }
	     //Log.v("RADNDOM BACKOFF"," end");
	}
	public boolean isConnectedToWifi() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		bFunctions.writeConsole2("Checking connectivity: "+ mWifi.isConnected());
		return mWifi.isConnected();
	}
	 
	public void appendTestResultsToExperienceFile(String str) {		
		itgOp.fileAppendBasic(str);
	}
	public void sendNotification(String message){
		   mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder =
	         new NotificationCompat.Builder(this)
	         .setSmallIcon(R.drawable.ic_launcher)
	         .setContentTitle("D-ITGSend")
	         .setContentText(message)
	         .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_ONE_SHOT));

	     notification = mBuilder.build();
	   //  startForeground( NOTIFICATION, notification );
	     mNotificationManager.notify(NOTIFICATION, notification);
	}

}
