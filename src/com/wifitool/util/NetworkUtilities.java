package com.wifitool.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import android.util.Log;

public class NetworkUtilities {
	public String getInterface() {

		try {
			for (Enumeration<NetworkInterface> list = NetworkInterface
					.getNetworkInterfaces(); list.hasMoreElements();) {
				NetworkInterface i = list.nextElement();
				Log.e("network_interfaces",
						"display name " + i.getDisplayName());
				if (i.getName().equalsIgnoreCase("wlan0")
						|| i.getName().equalsIgnoreCase("eth1")
						|| i.getName().equalsIgnoreCase("en1")) {
					return i.getName();
				}

			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void getInterfaces(){
	
		try {
			for(Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces(); list.hasMoreElements();)
		    {
		            NetworkInterface i = list.nextElement();
		            Log.e("network_interfaces", "display name " + i.getDisplayName());
		 
		    }		
			} 
		catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public boolean validateIpAddress(String newIpAddress) {
		
		final Pattern IP_PATTERN = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])");
		if (IP_PATTERN.matcher(newIpAddress).matches() == false) {
			return false;
		}
		return true;
	}
	public String validateSSID(String newSSID) {
		String message = "";
		String validChars = "ABCDEFGHIJKLMONPQRSTUVWXYZ"
				+ "abcdefghijklmnopqrstuvwxyz" + "0123456789_.";
		for (int i = 0; i < newSSID.length(); i++) {
			if (!validChars.contains(newSSID.substring(i, i + 1))) {
				message = "invalid chars";
			}
		}
		if (newSSID.equals("")) {
			message = "ssid empty";
		}
		if (message.length() > 0)
			message += "ssid not saved";
		return message;
	}
}
