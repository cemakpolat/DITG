package com.ditg.plot;

import java.util.ArrayList;

import com.ditg.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ITGGraphParamActivity extends Activity{

	ArrayList<String> childList;
	ArrayList<ITGGraphItem> adapterList;
	ListView list;
	ITGGraphAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.itgraph_parameters);
	    
		list= (ListView) findViewById(R.id.itgGrapParam_list);
		adapter=new ITGGraphAdapter(this, adapterList);        
	    list.setAdapter(adapter);
	    
	    // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				//showToast();
			}
		});	

	}
}
