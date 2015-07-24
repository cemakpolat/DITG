package com.ditg.params;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.ditg.main.CommonDefinitions;
import com.ditg.main.DBInterface;
import com.ditg.main.ITGItem;
import com.ditg.main.MainActivity;
import com.ditg.main.R;
import com.ditg.main.R.drawable;
import com.ditg.main.R.layout;

import dai.cnm.json.JSONArray;
import dai.cnm.json.JSONException;
import dai.cnm.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ITGRecvParams extends Activity{
	ArrayList<String> childList;
	ArrayList<ITGItem> adapterList;
	ListView list;
	LazyAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.itgrecv_parameters);

		// Get the message from the intent
		Intent intent = getIntent();
		String message = intent.getStringExtra(CommonDefinitions.ITG_MESSAGE);
	

	    getActionBar().setTitle(message);
		try {
			getITGObject();
			createList();
			createCollection();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		list= (ListView) findViewById(R.id.itgrecv_list);
		adapter=new LazyAdapter(this, adapterList);        
	    list.setAdapter(adapter);
	    
	    // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				//showToast();
			}
		});	

	}
	DBInterface db=new DBInterface();
	JSONObject itgObj=null;
	private ArrayList<String> getITGObject() throws JSONException{
		ArrayList<String> list=new ArrayList<String>();
		itgObj=db.getITGObject("ITGRecv");
		if(itgObj!=null){
			JSONArray array=itgObj.names();
			for(int i=0;i<array.length();i++){
					list.add(array.getString(i));
				
			}
		}
    	return list;
	}

	private void createList() {
		try {
			childList = getITGObject();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void createCollection() throws JSONException {
		adapterList=new ArrayList<ITGItem>();
			for(int i=0;i<childList.size();i++){
				
				ITGItem item=new ITGItem();
				if(!childList.get(i).equalsIgnoreCase("exp")){
					JSONObject obj1=itgObj.getJSONObject(childList.get(i));
					
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
				adapterList.add(item);
			}
		

	}
	public void writeOut(String str){
		System.out.println(str);
	}

}
