package com.ditg.plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.ditg.main.R;
import com.ditg.params.ITGOperations;


public class Plot extends Activity{

	
	public ITGOperations itgOp=new ITGOperations();
	public ArrayList<ArrayList<ITGGraphItem>> itgGraphListNew= new ArrayList< ArrayList<ITGGraphItem>>();

	private  List<String> itgObjectNames=null;
	private List<String> plotList=new ArrayList<String>();
	private List<Double> plotListDouble=new ArrayList<Double>();
	public static boolean reloadGraph=true;
	public static int graphUpdateTime=6000;
	public Spinner spinner;
	public double maxValueForRange=0;
	public double minValueForRange=0;
	
	public static String packetLoss="Packet Dropped";
	public static String selectedOption="";
	
	
	private static final int HISTORY_SIZE = 30;
	
	private XYPlot aprHistoryPlot = null;
	
	private SimpleXYSeries azimuthHistorySeries = null;
	private SimpleXYSeries pitchHistorySeries = null;
	private SimpleXYSeries rollHistorySeries = null;
	
	
	{
		azimuthHistorySeries = new SimpleXYSeries("Azimuth");
		pitchHistorySeries = new SimpleXYSeries("Pitch");
		rollHistorySeries = new SimpleXYSeries("Roll");
	}
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.plot_main);



	        // setup the APR History plot:
	        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);
	        aprHistoryPlot.setRangeBoundaries(0, 1, BoundaryMode.FIXED);//The shown graph and the last point that graph can achieve
	        aprHistoryPlot.setDomainBoundaries(0, 30, BoundaryMode.FIXED);
	        aprHistoryPlot.addSeries(azimuthHistorySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK,   Color.rgb(150, 190, 150)));
	       
	        

	        
	        //aprHistoryPlot.setDomainStepValue(10);
	        aprHistoryPlot.setRangeStepValue(10); // creates 20 fields on the y axis
	        //aprHistoryPlot.setTicksPerRangeLabel(5);//each range consists of 5 separate field on the y axis
	       // aprHistoryPlot.setDomainStep(XYStepMode.SUBDIVIDE, 10);

	        aprHistoryPlot.setDomainLabel("Sample Index");
	        aprHistoryPlot.getDomainLabelWidget().pack();
	        aprHistoryPlot.setRangeLabel("Angle (Degs)");
	        aprHistoryPlot.getRangeLabelWidget().pack();
	        aprHistoryPlot.disableAllMarkup();
	      
	        
	 		itgObjectNames=itgOp.getITGFlowList(); // get object names
	 		if(itgObjectNames!=null){
		 		this.filterITGList();
		 		loadSpinner();// load here the spinner
		 		
		        getDataSource();
		        
		        Thread th=new Thread(new Runnable(){
					@Override
					public void run() {
						while(reloadGraph){
							getDataSource();
							//send here to Handler message	
							Message msg = new Message();
							msg.obj = "call";
							Plot.this.displayMessageHandler.sendMessage(msg);
							try {
								Thread.sleep(graphUpdateTime);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
		        	
		        });
		        th.start();
	 		}else{
	 			Toast.makeText(this, "Any data is found for plotting!", Toast.LENGTH_LONG).show();
	 		}
	        
	 }
	 
	 public void updateGraph(double max, double min, int totalElement){
		    aprHistoryPlot.setRangeBoundaries(0, max, BoundaryMode.FIXED);//The shown graph and the last point that graph can achieve
	        aprHistoryPlot.setDomainBoundaries(0, totalElement, BoundaryMode.FIXED);
	 }
	 public boolean isEmptyList(){
		 int listSize=0;
		 listSize=this.plotListDouble.size();
		 if(listSize>0 ){
			 return false;
		 }else{
			 return true;
		 }
	 }
	 public double findMax(){
		 if(!isEmptyList()){
			 int maxIndex = this.plotListDouble.indexOf(Collections.max(plotListDouble));
			 return this.plotListDouble.get(maxIndex);	 
		 }
		 return 0;
	 }
	 public double findMin(){
		 if(!isEmptyList()){
			 int minIndex = this.plotListDouble.indexOf(Collections.min(plotListDouble));
			 return this.plotListDouble.get(minIndex);	 
		 }
		 return 0;
	 }
	 @Override
		public void onDestroy() {
			super.onDestroy();
			reloadGraph=false;
		}
	 
	 public void loadSpinner() {
		spinner = (Spinner) findViewById(R.id.graph_spinner);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, this.itgObjectNames);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	selectedOption=spinner.getItemAtPosition(position).toString();
		    	//send here to Handler message	
				Message msg = new Message();
				msg.obj = "call";
				Plot.this.displayMessageHandler.sendMessage(msg);
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
	  }
	
		Handler displayMessageHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.obj != null) {
					//SetupSettings.this.displayToastMessage((String) msg.obj);
					getDataSource();
					rePlot();
				}
				super.handleMessage(msg);
			}
		};
			 
	 	public void rePlot(){
	        // get rid the oldest sample in history:
//	        if (rollHistory.size() > HISTORY_SIZE) {
//	            //rollHistory.removeFirst();
//	            //pitchHistory.removeFirst();
//	            azimuthHistory.removeFirst();
//	        }
//	        // add the latest history sample:
//	        azimuthHistory.addLast(plotListDouble);
//	        pitchHistory.addLast(getRandomValue());
//	        rollHistory.addLast(getRandomValue());
//	 		
//	        // update the plot with the updated history Lists:
//	       pitchHistorySeries.setModel(pitchHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
//	        rollHistorySeries.setModel(rollHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	 		
	 		azimuthHistorySeries.setModel(this.plotListDouble, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	        aprHistoryPlot.redraw();	 		// redraw the Plots:
	 	}
	 	

	 	public void getDataSource(){
	 		
	 		itgGraphListNew.clear();
	 		this.plotListDouble.clear();
	 		itgOp.getITGResultValues();// getFlow Values
	 		itgGraphListNew=itgOp.itgListNew;
	 		
	 		
	 		for(int i=0;i<itgGraphListNew.size();i++){
	 			ArrayList<ITGGraphItem> list=itgGraphListNew.get(i);
	 			for(int j=0;j<list.size();j++){
		 			ITGGraphItem item=list.get(j);
		 			if(item.param.equalsIgnoreCase(selectedOption) && !item.param.equalsIgnoreCase(packetLoss)){
		 				plotListDouble.add(Double.parseDouble(item.getValue()));
		 			}else if(item.param.equalsIgnoreCase(packetLoss)){
		 				String[] params=item.getValue().split("\\(");
		 				plotListDouble.add(Double.parseDouble(params[0])/100);
		 			}
		 		}
	 		}
	 		if(plotListDouble.size()>0){
	 			updateGraph(findMax(),findMin(),itgGraphListNew.size());	
	 		}else{
	 			System.out.println("List Size is 0");
	 		}
	 		
	 	}
	 	/**
	 	 * {"Flow Result":[{"Flow Number":"1","FromIP":"192.168.178.38","FromPort":"32814","ToIP":"192.168.178.22",
	 	 * "ToPort":"9524","Total Time":"     1.034363","Total Packet":"           98 ","Minimum Delay":"     0.000153",
	 	 * "Maximum Delay":"     0.043824","Average Delay":"     0.006320","Average Jitter":"     0.004141",
	 	 * "DelayStd Deviation":"     0.005324","BytesRX":"        89425","Average Bitrate":"   691.633401",
	 	 * "Average Packet Rate":"    94.744302","Packet Dropped": "            3 (2.97 %)",
	 	 * "Average loss-burst Size":"     1.000000"}],"Total Result":{"Flow Number":"            1","Total Time":"     1.034363",
	 	 * "Total Packet":"           98","Minimum Delay":"     0.000153","Maximum Delay":"     0.043824",
	 	 * "Average Delay":"     0.006320","Average Jitter":"     0.004141","DelayStd Deviation":"     0.005324",
	 	 * "BytesRX":"        89425","Average Bitrate":"   691.633401","Average Packet Rate":"    94.744302",
	 	 * "Packet Dropped":"            3 (2.97 %)","Average Loss-Burst Size":"     1.000000","Error lines": "            0"}}
	 	 */
	 	public void filterITGList(){
	 		for(int index=0;index<itgObjectNames.size();index++){
	 			if(isInNonGraphParameter(itgObjectNames.get(index))){
	 				itgObjectNames.remove(index);
	 			}
	 		}
	 	}
	 	String[] nonGraphParameters={"Flow Number","FromIP","FromPort","ToIP","ToPort","Total Time"};
	 	public boolean isInNonGraphParameter(String item){
	 		for(int i=0;i<nonGraphParameters.length;i++){
	 			if(item.equalsIgnoreCase(this.nonGraphParameters[i])){
	 				return true;
	 			}
	 		}
	 		return false;
	 	}
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plot_activity_main, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.param_list:
	    		startITGParam();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void startITGParam(){
		Intent intent = new Intent(this, ITGGraphParamActivity.class);
	    startActivity(intent);
	} 
	
	 
}

