package dai.cnm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CommonFunctions {

	public static void randomBackOfftime(int requestedTimeInterval){
		 Random randomNumberGenerator = new Random();
	     int randomInstance = randomNumberGenerator.nextInt(requestedTimeInterval);
	     randomInstance++;
	     try {
	         Thread.sleep(randomInstance * 1000);
	     } catch (InterruptedException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	     }
	}
	
	public static void randomBackOfftime(int startInterval, int finishInterval){
		 Random randomNumberGenerator = new Random();
	     int randomInstance = randomNumberGenerator.nextInt(  (finishInterval - startInterval)  );
	     randomInstance = startInterval + randomInstance; 
	     try {
	         Thread.sleep(randomInstance * 1000);
	     } catch (InterruptedException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	     }
	}
	
	public boolean isConnectedToWifi(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		writeConsole("Checking connectivity: "+ mWifi.isConnected());
		return mWifi.isConnected();
	}
	public boolean isITGNodeReachable(String ip,int port){
		boolean exists = false;
		try {
		    SocketAddress sockaddr = new InetSocketAddress(ip, port);
		    // Create an unbound socket
		    Socket sock = new Socket();
		    // This method will block no more than timeoutMs.
		    // If the timeout occurs, SocketTimeoutException is thrown.
		    int timeoutMs = 2000;   // 2 seconds
		    sock.connect(sockaddr, timeoutMs);
		    exists = true;
		}catch(Exception e){
		}
		
		return exists;
	}
	 
	public static void randomBackOfftime(){
		 Random randomNumberGenerator = new Random();
	     int randomInstance = randomNumberGenerator.nextInt(10);
	     randomInstance++;
	     try {
	         Thread.sleep(randomInstance * 10000);
	     } catch (InterruptedException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	     }
	}
	public static void writeConsole(String line){
		System.out.println(line);
	}
	public static void writeConsole(String className,String line){
		System.out.println(className+": "+line);
	}

public class Ping {

    private static final String TAG = "Network.java";   

    public  String pingError = "";

    /**
     * Ping a host and return an int value of 0 or 1 or 2 0=success, 1=fail, 2=error
     * 
     * Does not work in Android emulator and also delay by '1' second if host not pingable
     * In the Android emulator only ping to 127.0.0.1 works
     * 
     * @param String host in dotted IP address format
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public  int pingHost(String host) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();     
        int exit = proc.exitValue();
        return exit;
    }

    public  String ping(String host) throws IOException, InterruptedException {
        StringBuffer echo = new StringBuffer();
        Runtime runtime = Runtime.getRuntime();
        Log.v(TAG, "About to ping using runtime.exec");
        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();
        int exit = proc.exitValue();
        if (exit == 0) {
            InputStreamReader reader = new InputStreamReader(proc.getInputStream());
            BufferedReader buffer = new BufferedReader(reader);
            String line = "";
            while ((line = buffer.readLine()) != null) {
                echo.append(line + "\n");
            }           
            return getPingStats(echo.toString());   
        } else if (exit == 1) {
            pingError = "failed, exit = 1";
            return null;            
        } else {
            pingError = "error, exit = 2";
            return null;    
        }       
    }

    /**
     * getPingStats interprets the text result of a Linux ping command
     * 
     * Set pingError on error and return null
     * 
     * http://en.wikipedia.org/wiki/Ping
     * 
     * PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.
     * 64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.251 ms
     * 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=0.294 ms
     * 64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=0.295 ms
     * 64 bytes from 127.0.0.1: icmp_seq=4 ttl=64 time=0.300 ms
     *
     * --- 127.0.0.1 ping statistics ---
     * 4 packets transmitted, 4 received, 0% packet loss, time 0ms
     * rtt min/avg/max/mdev = 0.251/0.285/0.300/0.019 ms
     * 
     * PING 192.168.0.2 (192.168.0.2) 56(84) bytes of data.
     * 
     * --- 192.168.0.2 ping statistics ---
     * 1 packets transmitted, 0 received, 100% packet loss, time 0ms
     *
     * # ping 321321.
     * ping: unknown host 321321.
     * 
     * 1. Check if output contains 0% packet loss : Branch to success -> Get stats
     * 2. Check if output contains 100% packet loss : Branch to fail -> No stats
     * 3. Check if output contains 25% packet loss : Branch to partial success -> Get stats
     * 4. Check if output contains "unknown host"
     * 
     * @param s
     */
    public  String getPingStats(String s) {
        if (s.contains("0% packet loss")) {
            int start = s.indexOf("/mdev = ");
            int end = s.indexOf(" ms\n", start);
            s = s.substring(start + 8, end);            
            String stats[] = s.split("/");
            return stats[2];
        } else if (s.contains("100% packet loss")) {
            pingError = "100% packet loss";
            return null;            
        } else if (s.contains("% packet loss")) {
            pingError = "partial packet loss";
            return null;
        } else if (s.contains("unknown host")) {
            pingError = "unknown host";
            return null;
        } else {
            pingError = "unknown error in getPingStats";
            return null;
        }       
    }
}
}
