package com.wifitool.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class FileIO {
	
	public String readFileFromSDCard(String fileName) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/DITG" );
		String fname = fileName;// "QoEResults.txt";
		File file = new File(myDir, fname);
		if (file.exists()) {
		//	throw new RuntimeException("File not found");
		this.writeINExternalFile(fileName+" mydir:"+root+ "");
		StringBuilder builder = null ;
		Log.e("Testing", "Starting to read");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			 builder = new StringBuilder();
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
		
		return builder.toString();
		}else{
			return "{}";
		}

			
	}

	public void storeFile(String obj, String fileName) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/Seccom");
		boolean success = false;
		if (!myDir.exists()) {
			success = myDir.mkdirs();
		}
		String fname = fileName;
		// String string = "";
		File file = new File(myDir, fname);
		// if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file, false);//FileOutputStream(file, true); for append
			out.write(obj.getBytes());
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void storeFile(String path, String fileName, String obj) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root+"/Seccom");
		boolean success = false;
		if (!myDir.exists()) {
			success = myDir.mkdirs();
		}

		String fname = fileName;
		// String string = "";
		File file = new File(myDir, fname);
		// if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file, true);
			out.write(obj.getBytes());
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fileWriteBasic(String file, String outputString) {
		try {
			File f;
			f = new File(file);
			// writeConsole(debugOutFileTaos2 + " file is being written");
			FileOutputStream fos;
			// we are not appending... we are writing over...
			fos = new FileOutputStream(f, false);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
			out.write(outputString + "\n");
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void fileWriteSynchro(String file, String outputString) {
		// TODO Auto-generated method stub
		try {
			File f;
			f = new File(file);
			// writeConsole(debugOutFileTaos2 + " file is being written");
			FileOutputStream fos;
			// we are not appending... we are writing over...
			fos = new FileOutputStream(f, false);
			fos.getChannel().lock();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
			out.write(outputString + "\n");
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void fileWriteAppendSynchro(String file, String outputString) {
		// TODO Auto-generated method stub
		try {
			File f;
			f = new File(file);
			// writeConsole(debugOutFileTaos2 + " file is being written");
			FileOutputStream fos;
			// we are appending...
			fos = new FileOutputStream(f, true);
			fos.getChannel().lock();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
			out.write(outputString + "\n");
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeTemporaryFile(String path, String fileName, String str) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + fileName);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// write the bytes in file
		if (file.exists()) {
			try {
				FileOutputStream fos;
				// we are appending...
				fos = new FileOutputStream(file, true);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
						fos));
				out.write(str + "\n");
				out.flush();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// deleting the file
		file.delete();
		System.out.println("file deleted");
	}
	//add here internal writing in android 
	public void writeINInternalFile(){
		
	}
	public void writeINExternalFile(String str){
		System.out.print(str);
	}
}
