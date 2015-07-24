package com.wifitool.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;



public class ShellInterface {

	/**
	 * Receive Command parameter and execute in shell with root permission
	 * @param command
	 */
	public static void runAsRoot(String command){
		   try {
		        Process process = Runtime.getRuntime().exec("su -c sh");
		        //process = Runtime.getRuntime().exec("sh");
		        OutputStream os = process.getOutputStream();
		       // Log.d(LOG_TAG, "runSuCommand() cmd=" + sucommand );
		        writeLine( os, command );
		        os.flush();
		        }
		      catch ( IOException e ) {
		        e.printStackTrace();
		      }
	}
	/**
	 * 
	 * @param os
	 * @param value
	 * @throws IOException
	 */
	private static void writeLine(OutputStream os, String value) throws IOException
	{
		String line = value + "\n";
		os.write( line.getBytes() );
	}
	/**
	 * 
	 * @param cmds
	 */
	 public  void RunAsRoot(String[] cmds){
         Process p;
		try {
			p = Runtime.getRuntime().exec("su");
		
         DataOutputStream os = new DataOutputStream(p.getOutputStream());            
         for (String tmpCmd : cmds) {
                 try {
					os.writeBytes(tmpCmd+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
         }           
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
	 }
	/**
	 * Receive command parameter and execute in shell with root permission
	 * returns the output of the executed command
	 * @param command
	 * @return the output of the executed command
	 */
	public  String runAsRootWithOutput(String command){
		try{
			Log.v("IPT", command);
			Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "system/bin/sh"});
			 DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
			 //from here all commands are executed with su permissions
			 stdin.writeBytes(command +" \n"); // \n executes the command
			 InputStream stdout = p.getInputStream();
			 byte[] buffer = new byte[4096];
			 int read;
			 String out = new String();
			 //read method will wait forever if there is nothing in the stream
			 //so we need to read it in another way than while((read=stdout.read(buffer))>0)
//				Log.v("IPT", "1");
			 while(true){
			     read = stdout.read(buffer);
			     out += new String(buffer, 0, read);//FIXME: There is an issue related to StringindexOutOfBound
			     if(read<4096){
			         //we have read everything
			         break;
			     }
			 }
//				Log.v("IPT", "2");
				stdin.writeBytes("exit\n");
				stdin.flush();
				return out;
			 }catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return ""; 
	}
	/**
	 * 
	 * @param command
	 * @return
	 */
	public static ArrayList<String> runAsRootWithOutputList(String command){
		//Log.v("IWList", command);
		ArrayList<String> shellOutput=new ArrayList<String>();
		try{
			Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "system/bin/sh"});
			 DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
			 //from here all commands are executed with su permissions
			 stdin.writeBytes(command +" \n"); // \n executes the command
			 InputStream stdout = p.getInputStream();
			 byte[] buffer = new byte[4096];
			 int read;
			 String out = new String();
			 //read method will wait forever if there is nothing in the stream
			 //so we need to read it in another way than while((read=stdout.read(buffer))>0)
			 
			 while(true){
			     read = stdout.read(buffer);
			     out += new String(buffer, 0, read);//FIXME: There is an issue related to StringindexOutOfBound
			     if(read<4096){
			         //we have read everything
			         break;
			     }
			 }
			 	shellOutput.add(out);
				return shellOutput;
			 }catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return shellOutput; 
	}
		/**
		 * 
		 * @param command
		 * @return
		 */
	public static  ArrayList<String> runCommandWithOutput(String command){
		Process process = null;
		ArrayList<String> shellOutput=new ArrayList<String>();
		try {
			
			process = new ProcessBuilder().command(command).redirectErrorStream(true).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				System.out.println("OUT:"+read);
				output.append(buffer, 0, read);
				shellOutput.add(output.toString());
				output.delete(0, output.length());
			}
			reader.close();
			process.destroy();
			return shellOutput;
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		shellOutput.add("");
		return shellOutput;
	}
	/**
	 * 
	 * @param commands
	 * @return
	 */
	
	public  ArrayList<String> runCommandStrs(String[] commands){
		Process process = null;
		ArrayList<String> shellOutput=new ArrayList<String>();
		try {
			
			process = new ProcessBuilder().command(commands).redirectErrorStream(true).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
				shellOutput.add(output.toString());
				output.delete(0, output.length());
			}
			reader.close();
			process.destroy();
			return shellOutput;
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		shellOutput.add("");
		return shellOutput;
	}
	public void runAsRoot(String[] cmds) throws Exception {
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        InputStream is = p.getInputStream();
        for (String tmpCmd : cmds) {
            os.writeBytes(tmpCmd+"\n");
            int readed = 0;
            byte[] buff = new byte[4096];
            Log.v("IPT", "1");
            // if cmd requires an output
            // due to the blocking behaviour of read(...)
            boolean cmdRequiresAnOutput = true;
            if (cmdRequiresAnOutput) {
                while( is.available() <= 0) {
                    try { Thread.sleep(200); } catch(Exception ex) {}
                }

                while( is.available() > 0) {
                    readed = is.read(buff);
                    if ( readed <= 0 ) break;
                    String seg = new String(buff,0,readed);
                    Log.i("#>", seg);
                }
            }
            Log.v("IPT", "2");
        }   
        Log.v("IPT", "3");
        os.writeBytes("exit\n");
        os.flush();
    }
/**
 * 
 * @param con
 * @param where
 * @param binaryName
 */
	public void installBinary(Context con,String where,String binaryName){
		InputStream in;
		String runPath=where+"/"+binaryName;
		//runPath="/data/data/com.example.uloop/iwconfig";
		try {
			in = con.getResources().getAssets().open(binaryName);
			
		} catch (IOException e2) {
			Log.v("SHELL","\nError occurred while accessing system resources, please reboot and try again.");
			return;			
		}
		try {
			//Checks if the file already exists, if not copies it.			
			new FileInputStream(runPath);
		} catch (FileNotFoundException e1) {
			try {
				//The file named "iperf" is created in a system designated folder for this application.
				OutputStream out = new FileOutputStream(runPath, false); 
				// Transfer bytes from "in" to "out"
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				//After the copy operation is finished, we give execute permissions to the "iperf" executable using shell commands.
				Process processChmod = Runtime.getRuntime().exec("/system/bin/chmod 744 "+runPath); 
				// Executes the command and waits until it finishes.
				processChmod.waitFor();
			} catch (IOException e) {
				Log.v("SHELL","\nError occurred while accessing system resources, please reboot and try again.");
				return;
			} catch (InterruptedException e) {
				Log.v("SHELL","\nError occurred while accessing system resources, please reboot and try again.");
				return;
			}		
			return;					
		} 
		return;
	}
	
	public String executeCommand(String command) {
		 
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
 
                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
 
	}
	/**
	 * 
	 * @param args
	 * @return
	 */
	 public static boolean execAndWait(String[] args) {
	        try {
	            Process p = Runtime.getRuntime().exec(args);
	            while (true) try {
	                int code = p.waitFor();
	                if (code != 0) { Log.d("FlashRec", String.format("%s exited with code %d", args[0], code)); return false; }
	                return true;
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	                continue;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	 /**
	  * 
	  * @param path
	  */
	public  void installBusyboxUnderXBin(String path){
		runAsRoot("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system");
		runAsRoot(path+"/busybox cp "+ path+"/busybox /system/xbin ");
		runAsRoot("cd /system/xbin; ");
		runAsRoot("busybox --install .");
		runAsRoot("mount -o ro,remount -t yaffs2 /dev/block/mtdblock3 /system ");
		runAsRoot("sync");
	}
}
