package com.ditg.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class BasicFunctions {
	
	public BasicFunctions(){
		
	}

	public JSONObject getAPLearningParameters(String filePath) throws JSONException {
		 JSONObject tempJSONObject = getClusterModel(filePath); 
		 return tempJSONObject; 
	}
	
	public void fileWrite(String debugOutFile, String outputString) {
		// TODO Auto-generated method stub
		try{
			File f;
			f = new File(debugOutFile);
			//writeConsole(debugOutFileTaos2 + " file is being written");
			FileOutputStream fos;
			// we are not appending... we are writing over... 
			fos = new FileOutputStream(f,false);
			fos.getChannel().lock();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
			out.write(outputString + "\n");
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
	}
	
	
	public void fileWriteAppend(String debugOutFile, String outputString) {
		// TODO Auto-generated method stub
		try{
			File f;
			f = new File(debugOutFile);
			//writeConsole(debugOutFileTaos2 + " file is being written");
			FileOutputStream fos;
			// we are appending... 
			fos = new FileOutputStream(f,true);
			fos.getChannel().lock();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
			out.write(outputString + "\n");
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
	}
	
    public JSONObject getClusterModel(String filePath) throws JSONException {
		JSONObject clusterModel = null;
		try {
			String str;
			File f = new File(filePath);
			if (!f.exists()) {
				writeConsole("no cluster model here : " + filePath);
			} 
			else {
				FileInputStream in = new FileInputStream(f);
				BufferedReader buf = new BufferedReader(new InputStreamReader(in));
				try {
				    final FileChannel c = in.getChannel();
				    final FileLock lock = c.lock(0L, Long.MAX_VALUE, true);
				    try {
				    	 String allFileInString = ""; 
						 while ((str = buf.readLine()) != null) {
							 allFileInString = allFileInString + str; 
						 }
						 clusterModel=new JSONObject(allFileInString);
				    } finally {
				        lock.release();
				    }
				} finally {
				    in.close();
				}
				
			}
		} catch (IOException e) {
			writeConsole("no file found for : " + filePath);
		}
		return clusterModel;
	}
    
    public String[] clearDotsFromBSSIDs(String[] bssids){
		try{
			for(int i = 0; i<bssids.length; i++){
				if( !(bssids[i].equalsIgnoreCase("null")) ){ 
					bssids[i] = bssids[i].replaceAll(":", ""); 
				}
			}
			return bssids; 
		}catch(Exception e){
			e.printStackTrace();
			return null; 
		}
		
	}
    
    
    public LinkedList<Number> getNumbersFromFile(String filePath) {
    	LinkedList<Number> numbers = new LinkedList<Number>();
		try {
			String str;
			File f = new File(filePath);
			if (!f.exists()) {
				writeConsole("no model here : " + filePath);
			} 
			else {
				FileInputStream in = new FileInputStream(f);
				BufferedReader buf = new BufferedReader(new InputStreamReader(in));
				try {
				    final FileChannel c = in.getChannel();
				    final FileLock lock = c.lock(0L, Long.MAX_VALUE, true);
				    try { 
						 while ((str = buf.readLine()) != null) {
							 numbers.add(  Integer.parseInt(str)   ); 
						 }
						 
				    } finally {
				        lock.release();
				    }
				} finally {
				    in.close();
				}
				
			}
		} catch (IOException e) {
			writeConsole("no file found for : " + filePath);
		}
		return numbers;
	}
    
    
    public LinkedList<Number> getNumbersFromFile(String filePath, int multiplicationFactor, int offset) {
    	LinkedList<Number> numbers = new LinkedList<Number>();
		try {
			String str;
			File f = new File(filePath);
			if (!f.exists()) {
				writeConsole("no model here : " + filePath);
			} 
			else {
				FileInputStream in = new FileInputStream(f);
				BufferedReader buf = new BufferedReader(new InputStreamReader(in));
				try {
				    final FileChannel c = in.getChannel();
				    final FileLock lock = c.lock(0L, Long.MAX_VALUE, true);
				    try { 
						 while ((str = buf.readLine()) != null) {
							 numbers.add(  (multiplicationFactor * Integer.parseInt(str)  + offset)   ); 
						 }
						 
				    } finally {
				        lock.release();
				    }
				} finally {
				    in.close();
				}
				
			}
		} catch (IOException e) {
			writeConsole("no file found for : " + filePath);
		}
		return numbers;
	}
    
    public void storeFile(String obj){
    	String root = Environment.getExternalStorageDirectory().toString();
    	File myDir = new File(root + "/QoExperience");    
    	boolean success = false;
    	if (!myDir.exists()) {
    	    success = myDir.mkdirs();
    	}	    	
    	
    	    String fname = "QoEResults.txt";
    	    //String string = "";
    	    File file = new File (myDir, fname);
    	    //if (file.exists ()) file.delete (); 
    	    try {
    	           FileOutputStream out = new FileOutputStream(file,true);
    	           out.write(obj.getBytes());
    	           out.flush();
    	           out.close();

    	    } catch (Exception e) {
    	           e.printStackTrace();
    	    }
    	}
    
    
    
    public void storeFileJson(JSONObject obj){
    	String root = Environment.getExternalStorageDirectory().toString();
    	File myDir = new File(root + "/QoExperience");    
    	boolean success = false;
    	if (!myDir.exists()) {
    	    success = myDir.mkdirs();
    	}	    	
    	String fname = "QoEResults.txt";
    	String string = "hello world cem this is  new line\n that you added :)!";
    	String jobs=obj.toString();
    	File file = new File (myDir, fname);
    	    //if (file.exists ()) file.delete (); 
    	    try {
    	           FileOutputStream out = new FileOutputStream(file,true);
    	           out.write(string.getBytes());
    	        //   out.write(jobs.getBytes());
    	           out.flush();
    	           out.close();

    	    } catch (Exception e) {
    	           e.printStackTrace();
    	    }
    	}
    	
    public void readFileFromSDCard() {

    	String root = Environment.getExternalStorageDirectory().toString();
    	File myDir = new File(root + "/QoExperience");  
    	String fname = "QoEResults.txt";
    	File file = new File (myDir, fname);
    	if (!file.exists()) {
    		throw new RuntimeException("File not found");
    	}
    	Log.e("Testing", "Starting to read");
    	BufferedReader reader = null;
    	try {
    		reader = new BufferedReader(new FileReader(file));
    		StringBuilder builder = new StringBuilder();
    		String line;
    		while ((line = reader.readLine()) != null) {
    			builder.append(line);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (reader != null) {
    			try {
    				reader.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    } 
    
    public void writeConsole(String msg){
    	System.out.println("DITG Messages" + ": " + msg); 
    }
    
    
    public void writeConsole2(String msg){
    	System.out.println("DITG Messages" + ": " + msg); 
    }
    
    
    
}
