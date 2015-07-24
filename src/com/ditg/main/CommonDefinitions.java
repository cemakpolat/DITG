package com.ditg.main;

import android.content.Context;


public class CommonDefinitions {
	
	public final static String STOP_ITGSERVICE="STOP";
	public  final static String ITG_MESSAGE= "com.ditg.main.MESSAGE";
	public  static String ITG_SEND="ITGSend";
	public  static String ITG_RECV="ITGRecv";
	public  static String ITG_LOG="ITGLog";
	public  static String ITG_MANAGER="ITGManager";
	public  static String ITG_DEC="ITGDec";
	
	
	public  static String ITGSEND_PATH = "/data/data/com.ditg.main/ITGSend";
	public  static String ITGRECV_PATH = "/data/data/com.ditg.main/ITGRecv";
	public  static String ITGDEC_PATH  = "/data/data/com.ditg.main/ITGDec";
//	public  static String ITGSEND_PATH = "/data/local/ITGSend";
//	public  static String ITGRECV_PATH = "/data/local/ITGRecv";
//	public  static String ITGDEC_PATH  = "/data/local/ITGDec";
	public static String ITGSend = "ITGSend";
	public static String ITGRecv = "ITGRecv";
	public final static String ITG="ITG MESSAGE";
	public static String PACKAGE_NAME="/data/data/com.ditg.main/";
	public CommonDefinitions(){
		
	}
	public String getPackageName(Context ct){
		String packageName= ct.getPackageName();
		PACKAGE_NAME ="/data/data/"+packageName;
		return PACKAGE_NAME;
	}
	public String getPackageName(){
		return PACKAGE_NAME;
	}
	public static String getModulePath(String module){
		return PACKAGE_NAME+module;
	}
}
