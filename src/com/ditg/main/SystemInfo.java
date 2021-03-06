package com.ditg.main;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

public class SystemInfo extends Activity {
	
	public static TextView ipAddress;
	public static TextView macAddress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.system_info);

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
   
	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(CommonDefinitions.ITG);
	    getActionBar().setTitle(message);
	    //getActionBar().setLogo(R.drawable.ic_launcher); // return here the appropriate user photo
	    
	    // Create the text view
//	    TextView textView = new TextView(this);
//	    textView.setTextSize(20);
//	    setContentView(textView);
	    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
	    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
	    String mac =wm.getConnectionInfo().getMacAddress(); 
	    
	    
	    
	    ipAddress=(TextView)findViewById(R.id.sys_ip);
	    ipAddress.setText(ip);
	    macAddress=(TextView)findViewById(R.id.sys_mac);
	    macAddress.setText(mac);
	    // Set the text view as the activity layout
	    
	}
	// GET IP ADDRESS 
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    String ip = Formatter.formatIpAddress(inetAddress.hashCode());
	                    Log.i("Sys Info", "***** IP="+ ip);
	                    return ip;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("Sys Info", ex.toString());
	    }
	    return null;
	}


	public class Utils {
		
		// AndroidManifest.xml permissions
//		<uses-permission android:name="android.permission.INTERNET" />
//		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	//
//		// test functions
//		Utils.getMACAddress("wlan0");
//		Utils.getMACAddress("eth0");
//		Utils.getIPAddress(true); // IPv4
//		Utils.getIPAddress(false); // IPv6 
		// UTILS FOR GETTING IP AND MAC ADDRESS
	
	    /**
	     * Convert byte array to hex string
	     * @param bytes
	     * @return
	     */
	    public String bytesToHex(byte[] bytes) {
	        StringBuilder sbuf = new StringBuilder();
	        for(int idx=0; idx < bytes.length; idx++) {
	            int intVal = bytes[idx] & 0xff;
	            if (intVal < 0x10) sbuf.append("0");
	            sbuf.append(Integer.toHexString(intVal).toUpperCase());
	        }
	        return sbuf.toString();
	    }
	
	    /**
	     * Get utf8 byte array.
	     * @param str
	     * @return  array of NULL if error was found
	     */
	    public byte[] getUTF8Bytes(String str) {
	        try { return str.getBytes("UTF-8"); } catch (Exception ex) { return null; }
	    }
	
	    /**
	     * Load UTF8withBOM or any ansi text file.
	     * @param filename
	     * @return  
	     * @throws java.io.IOException
	     */
	    public String loadFileAsString(String filename) throws java.io.IOException {
	        final int BUFLEN=1024;
	        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
	        try {
	            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
	            byte[] bytes = new byte[BUFLEN];
	            boolean isUTF8=false;
	            int read,count=0;           
	            while((read=is.read(bytes)) != -1) {
	                if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
	                    isUTF8=true;
	                    baos.write(bytes, 3, read-3); // drop UTF8 bom marker
	                } else {
	                    baos.write(bytes, 0, read);
	                }
	                count+=read;
	            }
	            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
	        } finally {
	            try{ is.close(); } catch(Exception ex){} 
	        }
	    }
	
	    /**
	     * Returns MAC address of the given interface name.
	     * @param interfaceName eth0, wlan0 or NULL=use first interface 
	     * @return  mac address or empty string
	     */
	    public String getMACAddress(String interfaceName) {
	        try {
	            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
	            for (NetworkInterface intf : interfaces) {
	                if (interfaceName != null) {
	                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
	                }
	                byte[] mac = intf.getHardwareAddress();
	                if (mac==null) return "";
	                StringBuilder buf = new StringBuilder();
	                for (int idx=0; idx<mac.length; idx++)
	                    buf.append(String.format("%02X:", mac[idx]));       
	                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
	                return buf.toString();
	            }
	        } catch (Exception ex) { } // for now eat exceptions
	        return "";
	        /*try {
	            // this is so Linux hack
	            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
	        } catch (IOException ex) {
	            return null;
	        }*/
	    }
	
	    /**
	     * Get IP address from first non-localhost interface
	     * @param ipv4  true=return ipv4, false=return ipv6
	     * @return  address or empty string
	     */
	    public String getIPAddress(boolean useIPv4) {
	        try {
	            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
	            for (NetworkInterface intf : interfaces) {
	                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
	                for (InetAddress addr : addrs) {
	                    if (!addr.isLoopbackAddress()) {
	                        String sAddr = addr.getHostAddress().toUpperCase();
	                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
	                        if (useIPv4) {
	                            if (isIPv4) 
	                                return sAddr;
	                        } else {
	                            if (!isIPv4) {
	                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
	                                return delim<0 ? sAddr : sAddr.substring(0, delim);
	                            }
	                        }
	                    }
	                }
	            }
	        } catch (Exception ex) { } // for now eat exceptions
	        return "";
	    }
	
	}
}
