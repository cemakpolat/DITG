package com.ditg.params;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ditg.main.CommonDefinitions;
import com.ditg.main.MainActivity;
import com.ditg.main.DBInterface;
import com.ditg.main.ExpandableListAdapter;
import com.ditg.main.ITGItem;
import com.ditg.main.R;
import com.ditg.main.R.id;
import com.ditg.main.R.layout;
import com.ditg.main.R.menu;

import dai.cnm.json.JSONArray;
import dai.cnm.json.JSONException;
import dai.cnm.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ITGSendParams extends Activity {
	List<String> groupList;
	List<String> childList;
	Map<String, List<String>> laptopCollection;
	Map<String, ArrayList<ITGItem>> laptopCollection2;
	ExpandableListView expListView;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itgsend_parameters);

		// Get the message from the intent
		Intent intent = getIntent();
		String message = intent.getStringExtra(CommonDefinitions.ITG_MESSAGE);

	    getActionBar().setTitle(message);
	    
		try {
			getITGObject();
		    createGroupList();
			createCollection();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		expListView = (ExpandableListView) findViewById(R.id.laptop_list);
//		final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
//				this, groupList, laptopCollection);
		final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
				this, groupList, laptopCollection2);
		expListView.setAdapter(expListAdapter);

		setGroupIndicatorToRight();// Changing the group arrow indicator 

		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				final String selected = (String) expListAdapter.getChild(
						groupPosition, childPosition);
				Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
						.show();

				return true;
			}

		});
	}
	
	DBInterface db=new DBInterface();
	JSONObject itgObj=null;
	private ArrayList<String> getITGObject() throws JSONException{
		ArrayList<String> list=new ArrayList<String>();
		itgObj=db.getITGObject("ITGSend");
		if(itgObj!=null){
			JSONArray array=itgObj.names();
			for(int i=0;i<array.length();i++){
				if(!array.getString(i).equalsIgnoreCase("exp")){
					list.add(array.getString(i));
				}
			}
		}
    	return list;
	}

	private void createGroupList() {
		try {
			groupList = getITGObject();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void createCollection() throws JSONException {
		laptopCollection2 = new LinkedHashMap<String, ArrayList<ITGItem>>();
		
		ArrayList<ITGItem> list=null;
		for (String optionName : groupList) {
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
					item.exp=obj1.getString("exp");
				}
				this.writeOut(item.param);
				list.add(item);
			}
		//	if(list!=null)
				laptopCollection2.put(optionName, list);
		}
	}
	public void writeOut(String str){
		System.out.println(str);
	}
	//save 
	private void saveITGSendChanges() throws JSONException{

		for(int i=0;i<laptopCollection2.size();i++){
			ArrayList<ITGItem> list=(ArrayList<ITGItem>) this.laptopCollection2.get(this.groupList.get(i));
			JSONObject obj=itgObj.getJSONObject(this.groupList.get(i));
			for(int j=0;j<list.size();j++){
				ITGItem item=list.get(j);
				JSONObject jsonItem=new JSONObject();
				jsonItem.put("enable", item.enabled);
				jsonItem.put("param", item.param);
				if(item.unit!=null){
					jsonItem.put("unit", item.unit);	
				}
				if(item.value!=null){
					jsonItem.put("value", item.value);
				}
				JSONArray option=null;
				if(item.options!=null){
					 option=new JSONArray();
					 for(int k=0;k<item.options.size();k++){
						 option.put(item.options.get(k));
					 }
				}
				jsonItem.put("exp", item.exp);
				itgObj.put(this.groupList.get(i), jsonItem);
			}
		}
	}

	private void loadChild(String[] laptopModels) {
		childList = new ArrayList<String>();
		for (String model : laptopModels)
			childList.add(model);
	}

	private void setGroupIndicatorToRight() {
		/* Get the screen width */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;

		expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
				- getDipsFromPixel(5));
	}

	// Convert pixel to dip
	public int getDipsFromPixel(float pixels) {
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_itgsend, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.menu_save:
        	try {
				saveITGSendChanges();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
}
