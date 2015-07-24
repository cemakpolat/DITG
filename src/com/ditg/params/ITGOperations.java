package com.ditg.params;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ditg.main.BasicFunctions;
import com.ditg.main.CommonDefinitions;
import com.ditg.main.DBInterface;
import com.ditg.main.ITGItem;
import com.ditg.plot.ITGGraphItem;

import dai.cnm.json.JSONArray;
import dai.cnm.json.JSONException;
import dai.cnm.json.JSONObject;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;




public class ITGOperations {
	String itgSendPath = "/data/data/com.ditg.main/ITGSend";
	String itgRecvPath = "/data/data/com.ditg.main/ITGRecv";
	String itgDecPath = "/data/data/com.ditg.main/ITGDec";
	
	
//	String itgSendPath = "/data/local/ITGSend";
//	String itgRecvPath = "/data/local/ITGRecv";
//	String itgDecPath = "/data/local/ITGDec";
	
	public final String ITG_SEND="ITGSend";
	public final String ITG_DEC="ITGDec";
	
	List<String> itgGroupList;
	Map<String, ArrayList<ITGItem>> itgSendCollection;
	 ArrayList<ITGItem>itgRecvCollection;
	
	DBInterface db=new DBInterface();
	JSONObject itgObj=null;
	
	// Data Transfer to the specified Server
	public String serverIPAddress="192.168.43.1";
	public String serverPortAddress="13122";

	// Files and folder names
	public String ditgFolderName = "DITG";
	public static String ditgResults="ditgResults.txt";
	public static String ditgReport="ditgReport.txt";
	public String itgSendConfFile="itgSendConfiguration.txt";
	public String itgRecvConfFile="itgRecvConfiguration.txt";
	
	public ITGOperations(){}	
	
	public ArrayList<String> getITGObject(String toolName) throws JSONException{
		ArrayList<String> list=new ArrayList<String>();
		if(toolName.equalsIgnoreCase(CommonDefinitions.ITG_RECV)){
			itgObj=db.getITGObject("ITGRecv");
			if(itgObj!=null){
				JSONArray array=itgObj.names();
				for(int i=0;i<array.length();i++){
					if(!array.getString(i).equalsIgnoreCase("exp")){
						list.add(array.getString(i));
					}
				}
			}
		}else if(toolName.equalsIgnoreCase(CommonDefinitions.ITG_SEND)){
			itgObj=db.getITGObject(ITG_SEND);
			if(itgObj!=null){
				JSONArray array=itgObj.names();
				for(int i=0;i<array.length();i++){
					if(!array.getString(i).equalsIgnoreCase("exp")){
						list.add(array.getString(i));
					}
				}
			}
		}
		//System.out.println(" ITG GROUPLIST:"+);
    	return list;
	}
	
	private void createGroupList(String toolName) {
		try {
			itgGroupList = getITGObject(toolName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createCollection(String module) throws JSONException {
		if(module.equalsIgnoreCase(CommonDefinitions.ITG_RECV)){
			itgRecvCollection=new ArrayList<ITGItem>();
			for(int i=0;i<itgGroupList.size();i++){
				
				ITGItem item=new ITGItem();
				if(!itgGroupList.get(i).equalsIgnoreCase("exp")){
					JSONObject obj1=itgObj.getJSONObject(itgGroupList.get(i));
					
					item.enabled=obj1.getBoolean("enable");
					item.param=obj1.getString("param");
					if(obj1.getString("value").equalsIgnoreCase("none")){
						item.value=null;
					}else{
						item.value=obj1.getString("value");
					}
					if(obj1.getString("unit").equalsIgnoreCase("none")){
						item.unit=null;
					}else{
						item.unit=obj1.getString("unit");
					}
					
					if(obj1.getJSONArray("options").length()>0){
						JSONArray jsonarray=obj1.getJSONArray("options");
						item.options=new ArrayList<String>();
						for(int m=0;m<jsonarray.length();m++){
							item.options.add(jsonarray.get(m).toString());	
						}
					}else{
						item.options=null;
					}
					item.exp=obj1.getString("exp");
				}
//				this.writeOut(item.param);
				itgRecvCollection.add(item);
			}
		}else if(module.equalsIgnoreCase(CommonDefinitions.ITG_SEND)){
			itgSendCollection = new LinkedHashMap<String, ArrayList<ITGItem>>();
			ArrayList<ITGItem> list=null;
			for (String optionName : itgGroupList) {
				JSONObject obj=itgObj.getJSONObject(optionName);
				JSONArray array=obj.names();
				list=new ArrayList<ITGItem>();
				for(int i=0;i<array.length();i++){
					
					ITGItem item=new ITGItem();
					if(!array.getString(i).equalsIgnoreCase("exp")){
						JSONObject obj1=obj.getJSONObject(array.getString(i));
						
						item.enabled=obj1.getBoolean("enable");
						item.param=obj1.getString("param");
						if(obj1.getString("value").equalsIgnoreCase("none")){
							item.value=null;
						}else{
							item.value=obj1.getString("value");
						}
						if(obj1.getString("unit").equalsIgnoreCase("none")){
							item.unit=null;
						}else{
							item.unit=obj1.getString("unit");
						}
						if(obj1.getJSONArray("options").length()>0){
							JSONArray jsonarray=obj1.getJSONArray("options");
							item.options=new ArrayList<String>();
							for(int m=0;m<jsonarray.length();m++){
								item.options.add(jsonarray.get(m).toString());	
							}
						}else{
							item.options=null;
						}
						//writeOut("RESULT:"+item.param+""+item.value);
						item.exp=obj1.getString("exp");
					}
					list.add(item);
				}
				itgSendCollection.put(optionName, list);
		}
		}
	}
	public void writeOut(String str){
		System.out.println(str);
	}

	//save 
	//TODO: Save ITGRecv Changes
	public void saveITGRecvChanges() throws JSONException{
		
		for(int i=0;i<itgRecvCollection.size();i++){
			ITGItem item=(ITGItem) this.itgRecvCollection.get(i);			
			JSONObject jsonItem=new JSONObject();
			jsonItem.put("enable", item.enabled);
			jsonItem.put("param", item.param);
			if(item.unit!=null){
				jsonItem.put("unit", item.unit);	
			}else{
				jsonItem.put("unit", "none");
			}
			if(item.value!=null){
				if(item.enabled){
					if(item.value.equalsIgnoreCase("")){
						//Toast.makeText(context," Please, provide the required parameter value that you enabled",Toast.LENGTH_LONG).show();
						jsonItem.put("enable", false);
					}
				}
				jsonItem.put("value", item.value);
			}else{
				jsonItem.put("value", "none");
			}
			JSONArray option=new JSONArray();
			if(item.options!=null){
				 option=new JSONArray();
				 for(int k=0;k<item.options.size();k++){
					 option.put(item.options.get(k));
				 }
			}
			jsonItem.put("options",option );	
			jsonItem.put("exp", item.exp);
			this.writeOut(this.itgGroupList.get(i)+"-> "+jsonItem.toString());
			this.itgObj.put(this.itgGroupList.get(i), jsonItem);
		}
		//this.writeOut(itgObj.toString());
		this.fileWriteBasic(itgObj.toString(),CommonDefinitions.ITG_RECV);
	}
	public void saveITGSendChanges() throws JSONException{
		
		for(int i=0;i<itgSendCollection.size();i++){
			ArrayList<ITGItem> list=(ArrayList<ITGItem>) this.itgSendCollection.get(this.itgGroupList.get(i));
			JSONObject obj=itgObj.getJSONObject(this.itgGroupList.get(i));
			JSONArray array=obj.names();
			for(int j=0;j<list.size();j++){
				ITGItem item=list.get(j);
				
				JSONObject jsonItem=new JSONObject();
				jsonItem.put("enable", item.enabled);
				jsonItem.put("param", item.param);
				if(item.unit!=null){
					jsonItem.put("unit", item.unit);	
				}else{
					jsonItem.put("unit", "none");
				}
				if(item.value!=null){
					if(item.enabled){
						if(item.value.equalsIgnoreCase("")){
							//Toast.makeText(context," Please, provide the required parameter value that you enabled",Toast.LENGTH_LONG).show();
							jsonItem.put("enable", false);
						}
					}
					jsonItem.put("value", item.value);
				}else{
					jsonItem.put("value", "none");
				}
				JSONArray option=new JSONArray();
				if(item.options!=null){
					 option=new JSONArray();
					 for(int k=0;k<item.options.size();k++){
						 option.put(item.options.get(k));
					 }
				}
					//JSONArray op=obj.getJSONObject(array.getString(j)).getJSONArray("options");
				jsonItem.put("options",option );	
				jsonItem.put("exp", item.exp);
				this.writeOut(jsonItem.toString());
				obj.put(array.getString(j),jsonItem);
			//	this.writeOut(array.getString(j)+"-> "+obj.toString());
			}
			this.itgObj.put(this.itgGroupList.get(i), obj);

		}
		this.fileWriteBasic(itgObj.toString(),CommonDefinitions.ITG_SEND);
		//this.writeOut("-> "+itgObj.toString());
		//this.writeOut("********");
	}
	
	List<String> commands = new ArrayList<String>();
	public void collectAllParameters(String moduleName){
		 
		 try{
			 createGroupList(moduleName);
			 createCollection(moduleName);
		 }catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(moduleName.equalsIgnoreCase(CommonDefinitions.ITG_RECV)){
			 commands.add(itgRecvPath);
			 for(int i=0;i<itgRecvCollection.size();i++){
				 if(itgRecvCollection.get(i).enabled){
						commands.add(itgRecvCollection.get(i).param);
						if(!itgRecvCollection.get(i).value.equalsIgnoreCase("none")){
							commands.add(itgRecvCollection.get(i).value);	
						}
				 }
			 }
		 }else if(moduleName.equalsIgnoreCase(CommonDefinitions.ITG_SEND)){
				
				commands.add(itgSendPath);
				for(int i=0;i<itgSendCollection.size();i++){
					ArrayList<ITGItem> list=itgSendCollection.get(this.itgGroupList.get(i));
					for(int j=0;j<list.size();j++){
						if(list.get(j).enabled){
							commands.add(list.get(j).param);
							if(!list.get(j).value.equalsIgnoreCase("none")){
								commands.add(list.get(j).value);	
							}
						}
					}
				} 
		 }
	
	}
	public List<String>  getParamList(){
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/"+ditgFolderName);
		// file path change for ITGSend
		String command="";
		
		for(int i=0;i<commands.size();i++){
			if(commands.get(i).equalsIgnoreCase("-l")){
				String str=myDir+"/"+commands.get(i+1);
				commands.set(i+1,str);
			}
			command=command+commands.get(i)+" ";
		}
		
		this.writeOut("COMMANDs:"+command);
		return commands;
	}
	public List<String>  getDummyParamList(){
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/"+ditgFolderName);
		List<String> temp = new ArrayList<String>();
		temp.add(itgSendPath);
		temp.add("-a");
		temp.add(serverIPAddress);
		temp.add("-rp");
		temp.add("9524");
		temp.add("-E");
		temp.add("100");
		temp.add("-e");
		temp.add("750");
		temp.add("-T");
		temp.add("TCP");
		temp.add("-t");
		temp.add("1000");
		temp.add("-m");
		temp.add("rttm");
		temp.add("-l");
		temp.add(myDir+"/ITGSendLog");
		return temp;
	}
	//ITG Configuration file in JSON Format
	public void createConfigurationFile(String toolName){
		try {
		    createGroupList(toolName);
		    createCollection(toolName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public  JSONObject readTestResults() throws JSONException {
		JSONObject jobj = readTestResultsFromFile();
		 //System.out.println("JSON Result:\n"+jobj.toString());
		
		return jobj;
	}
	// read file
		public JSONObject readTestResultsFromFile() throws JSONException {
			// readFile
			JSONObject testResults = null;

			try {
				String str;
				String root = Environment.getExternalStorageDirectory().toString();

				File f = new File( root+"/"+"DITG"+"/"+ ditgReport);

				FileInputStream in = new FileInputStream(f);
				BufferedReader buf = new BufferedReader(new InputStreamReader(in));

				try {
					final FileChannel c = in.getChannel();
					final FileLock lock = c.lock(0L, Long.MAX_VALUE, true);
					try {
						while ((str = buf.readLine()) != null) {
							testResults = new JSONObject(str);
						}
					} finally {
						lock.release();
					}
				} finally {
					in.close();
				}

			} catch (IOException e) {
			}
			return testResults;
		}
		public String readFileFromSDCard(String fileName) {
			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File( root+"/"+ditgFolderName);
			//String fname = fileName;// "QoEResults.txt";
			File file = new File(myDir, ditgResults);
			if (file.exists()) {
			//	throw new RuntimeException("File not found");
			
			String str="";
			StringBuilder builder = null ;
			Log.e("Testing", "Starting to read");
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				 builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					//builder.append(line);
					str=str+line+"\n";
			
					this.writeOut(line);
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
			
			return str;
			}else{
				return "";
			}

				
		}
		public void fileAppendBasic(String outputString) {
			String root = Environment.getExternalStorageDirectory().toString();
			
			try {
				File f;
				String path=root+"/"+ditgFolderName+"/"+ ditgResults;
				f = new File( root+"/"+ditgFolderName+"/"+ ditgResults);
				(new BasicFunctions()).writeConsole(outputString + " file is being written");
				(new BasicFunctions()).writeConsole(path + " file is being written");
				FileOutputStream fos;
				// we are not appending... we are writing over...
				fos = new FileOutputStream(f, true);

				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
				out.write(outputString + "\n");
				out.flush();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		public void fileWriteBasic(String outputString) {
			String root = Environment.getExternalStorageDirectory().toString();
			String ditgResultsFile = itgSendConfFile; 
			
			try {
				File f;
				f = new File( root+"/"+ditgFolderName+"/"+ ditgResultsFile);
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
		public void fileWriteBasic(String outputString,String module) {
			String root = Environment.getExternalStorageDirectory().toString();
			String ditgResultsFile = "default.txt"; 
			if(module.equalsIgnoreCase(CommonDefinitions.ITG_RECV)){
				 ditgResultsFile = itgRecvConfFile; 	
			}else if(module.equalsIgnoreCase(CommonDefinitions.ITG_SEND)){
				 ditgResultsFile = itgSendConfFile;
			}
			try {
				File f;
				f = new File( root+"/"+ditgFolderName+"/"+ ditgResultsFile);
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
				File f = new File(getRootPathFileDir(), fileName );
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
		public JSONArray getJSONArrayFromFile(String fileName) throws JSONException {
			JSONArray model = new JSONArray();
			try {
				String str;
				File f = new File(getRootPathFileDir(), fileName );
				if (!f.exists()) {
					System.out.println("File doesn't exist");

				} else {
					FileInputStream in = null;
					BufferedReader buf = null;
					try {
						in = new FileInputStream(f);
						buf = new BufferedReader(new InputStreamReader(in));
						while ((str = buf.readLine()) != null) {
							model = new JSONArray(str);
						}
					} finally {
						in.close();
					}
				}
			} catch (IOException e) {

			}
			return model;
		}
		public File getRootPathFileDir(){
			String root = Environment.getExternalStorageDirectory().toString();
	    	File myDir = new File(root + "/"+ditgFolderName);    
	    	boolean success = false;
	    	if (!myDir.exists()) {
	    	    success = myDir.mkdirs();
	    	}	
	    	return myDir;
		}
		
		public boolean fileExist(String file){
			boolean result=false;			
			File myFile = new File(getRootPathFileDir(),file);
			if(myFile.exists()){
				result=true;
				this.writeOut("ITGSend Configuration File exist");
			}else{
				this.writeOut("ITGSend Configuration File doesn't exist!");
			}
			
			return result;
		}
		
		
		public JSONObject getFlowData() throws JSONException{
			JSONObject itg=null;
        	if(this.fileExist(this.ditgReport)){
        		itg=this.getJSONFromFile(ditgReport);
        		
        		JSONArray obj=itg.getJSONArray("Flow Result");
        		//this.writeOut(itg.toString());
        		//this.writeOut("\n\n"+obj.toString());
        		return obj.getJSONObject(0);
        	}
        	return null;
		}
//		 
//		public JSONArray getAllFlowData() throws JSONException{
//			JSONArray itg=null;
//        	if(this.fileExist(ditgResult)){
//        		itg=this.getJSONArrayFromFile(ditgResult);
//        		return itg;
//        	}
//        	return null;
//		}
//		
		public JSONObject getTotalResulData() throws JSONException{
			JSONObject itg=null;
        	if(this.fileExist(this.itgSendConfFile)){
        		itg=this.getJSONFromFile(this.itgSendConfFile);
        			JSONObject obj=itg.getJSONObject("Total Result");
        			return obj;
        		}
        	return null;
		}
	
	public Map<Integer, ArrayList<ITGGraphItem>> itgList= new LinkedHashMap<Integer, ArrayList<ITGGraphItem>>();
	public ArrayList<ArrayList<ITGGraphItem>> itgListNew= new ArrayList< ArrayList<ITGGraphItem>>();
	
	public JSONArray getAllFlowData() throws JSONException{
		JSONArray itg=new JSONArray();
    	if(this.fileExist(ditgResults)){
    		String out= this.readFileFromSDCard(ditgResults);
    		String lines[] = out.split("\\n");
    		for(int i=0;i<lines.length;i++){
    			if(!lines[i].contains("Error ITG")){
    				JSONObject obj=new JSONObject(lines[i]);
    				itg.put(obj);
//    				System.out.println(" CEM=======");	
//    				System.out.println("  \n\n"+lines[i].toString());
//    				System.out.println(" CEM =======");
    			}else{
//    				System.out.println(" ERROR=======");
//    				System.out.println("  \n\n"+lines[i].toString());
//    				System.out.println(" ERROR =======");	
    			}
    			
    		}
    	}
    	return itg;
	}
	
	
	public void getITGResultValues() {
		JSONArray array;
		try {
			
			
			array = this.getAllFlowData();
			this.writeOut(array.length()+"");
			for(int k=0;k<array.length();k++){
				
				JSONObject obj=array.getJSONObject(k);
				JSONArray flowArray=obj.getJSONArray("Flow Result");
				JSONObject flowObj=flowArray.getJSONObject(0);
				JSONArray objNames = flowObj.names();
				ArrayList<ITGGraphItem> list = new ArrayList<ITGGraphItem>();
				
				for (int i = 0; i < objNames.length(); i++) {
					ITGGraphItem itgItem = new ITGGraphItem();
					itgItem.setParam(objNames.getString(i));
					itgItem.setValue(flowObj.getString(objNames.getString(i)));
					list.add(itgItem);
				}
				itgListNew.add(list);
				//itgList.put(itgList.size() + 1, list);	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getITGFlowList() {
		JSONObject obj;
		try {
			obj = this.getFlowData();
			if(obj!=null){
				JSONArray objNames = obj.names();
				ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < objNames.length(); i++) {
					
					list.add(objNames.getString(i));
				}
			return list;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
