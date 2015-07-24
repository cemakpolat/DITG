package com.wifitool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;


import dai.cnm.json.JSONArray;
import dai.cnm.json.JSONException;
import dai.cnm.json.JSONObject;

import android.os.Environment;

public class JsonIO {
	
	//adjust the function as a global file storing function
	/**
	 * 
	 * @param obj
	 */
	public String folderName = "SecCom";

	public File getRootPathFileDir(){
		String root = Environment.getExternalStorageDirectory().toString();
    	File myDir = new File(root + "/"+folderName);    
    	boolean success = false;
    	if (!myDir.exists()) {
    	    success = myDir.mkdirs();
    	}	
    	return myDir;
	}

	public void storeFileJson(String obj,String filename) {

		String fname = filename;
	//	String string = "hello world cem this is  new line\n that you added :)!";
		String jobs = obj;
		File file = new File(getRootPathFileDir(), fname);
		try {
			FileOutputStream out = new FileOutputStream(file, false);//new FileOutputStream(file, true); append
			out.write(jobs.getBytes());
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public JSONObject getJSONFromFile(String fileName) throws JSONException {
		JSONObject model = null;
		try {
			String str;
			File f = new File(getRootPathFileDir(), fileName + ".txt");
			if (!f.exists()) {
				System.out.println("File doesn't exist");
			} else {
				FileInputStream in = null;
				BufferedReader buf = null;
				try {
					in = new FileInputStream(f);
					buf = new BufferedReader(new InputStreamReader(in));
					while ((str = buf.readLine()) != null) {
						model = new JSONObject(str);
					}
				} finally {
					in.close();
				}
			}
		} catch (IOException e) {

		}
		return model;
	}
	
	public JSONArray getJSONsFromFile(String fileName)  {
		JSONArray list = null;
		try {
			String str;
			File f = new File(getRootPathFileDir(),fileName + ".txt");
			if (!f.exists()) {
				System.out.println("File doesn't exist");
			} else {
				FileInputStream in =null;
				BufferedReader buf = null;
				try {
				 in = new FileInputStream(f);
				 buf = new BufferedReader(new InputStreamReader(in));
						while ((str = buf.readLine()) != null) {
							JSONObject	model = new JSONObject(str);
							list.put(model);
						}
					}   catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				finally {
					in.close();
				}
			}
		} catch (IOException e) {
			
		}
		return list;
	}
	 
//		private void firstUserSessionsDeployement(JSONObject userModel) throws JSONException{
//			if(userModel!=null){
//			
//			if(userModel.getString("userId").toString().equalsIgnoreCase("")){
//				int size=24*60/this.timeInterval;
//
//				for(int i=0;i<size;i++){
//					JSONObject jsonobject = new JSONObject();
//					jsonobject.put("visitingFrequency",0);
//					jsonobject.put("bandwidth",0);
//					userModel.getJSONArray("sessionsPerMin").put(jsonobject);
//				}
//			}	}
//			else{
//				BlackBoard.writeConsole("user model returns null");
//			}
//		}
	
	
//	this.visitingFrequency = userModel.getJSONArray("sessionsPerMin").getJSONObject(j).getDouble("visitingFrequency");

//	userModel.getJSONArray("sessionsPerMin").getJSONObject(place).put("visitingFrequency",this.visitingFrequency);
	
	//synchronized reading
	public JSONObject getUserModelFromFile(String fileName) throws JSONException {
		JSONObject model = null;
		try {
			String str;
			File f = new File(getRootPathFileDir(),fileName + ".txt");
			if (!f.exists()) {
				System.out.println("File doesn't exist");
			} else {

				FileInputStream in = new FileInputStream(f);
				BufferedReader buf = new BufferedReader(new InputStreamReader(
						in));

				try {
					FileChannel channel = new RandomAccessFile(f, "rw").getChannel();
					final FileLock lock = channel.lock();

					try {
						int i = 0;
						while ((str = buf.readLine()) != null) {
							if (i == 0) {
								model = new JSONObject(str);
							}
							i++;
						}
					} catch (OverlappingFileLockException e) {
						e.printStackTrace();
						
					}

					finally {
						lock.release();
						channel.close();
					}
				} finally {
					in.close();
				}
			}
		} catch (IOException e) {
		}
	
		return model;
	}
	
	//synchronized storing
	public void storeJSONToFile(dai.cnm.json.JSONObject obj, String fileName){

		FileWriter fw = null;
		try {

			File file = new File(getRootPathFileDir(),fileName + ".txt");
			fw = new FileWriter(file, true);
			fw.write(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Close the file
			
		}
	}
	public void storeJSONToFile(JSONArray list, String fileName)throws JSONException {

		FileWriter fw = null;
		try {

			File file = new File(getRootPathFileDir(),fileName + ".json");
			fw = new FileWriter(file, true);
			fw.write(list.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Close the file
			
		}
	}
	@SuppressWarnings("resource")
	public void storeJSONFile(JSONObject model,String fileName)throws JSONException {
		FileChannel channel = null;
		FileLock lock = null ;
		FileWriter fw = null;
		try {

			File file = new File(getRootPathFileDir(),fileName + ".json");
			fw = new FileWriter(file, true);
			channel = new RandomAccessFile(file, "rw").getChannel();
			lock = channel.lock();
			fw.write(model.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				lock.release();
				channel.close();
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Close the file
			
		}
	}
}
