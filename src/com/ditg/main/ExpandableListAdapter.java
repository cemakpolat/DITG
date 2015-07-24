package com.ditg.main;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Activity context;
	//private Map<String, List<String>> laptopCollections;
	private Map<String, ArrayList<ITGItem>> laptopCollections;

	private List<String> laptops;

//    public ExpandableListAdapter(Activity context, List<String> laptops,
//            Map<String, List<String>> laptopCollections) {
//        this.context = context;
//        this.laptopCollections = laptopCollections;
//        this.laptops = laptops;
//    }
    public ExpandableListAdapter(Activity context, List<String> laptops,
            Map<String, ArrayList<ITGItem>> laptopCollections) {
        this.context = context;
        this.laptopCollections= laptopCollections;
        this.laptops = laptops;
    }
    public Object getChild(int groupPosition, int childPosition) {
        return laptopCollections.get(laptops.get(groupPosition)).get(childPosition);
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    /**
     * Return different type of view
     */
    @Override
    public int getChildType (int groupPosition, int childPosition){
    	if(laptopCollections.get(laptops.get(groupPosition)).get(childPosition).options!=null){
    		return 1;
    	}else{
    		return 0;
    	}
    }
    /**
     * There are two different view
     */
    @Override
    public int getChildTypeCount (){
    	return 2;
    }
    
//    @Override
//    public int getGroupType (int groupPosition){
//    	return 0;    	
//    }
//    @Override
//    public int getGroupTypeCount (){
//    	
//    	return 2;
//    }
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        //final String laptop = (String) getChild(groupPosition, childPosition);
        final ITGItem item=(ITGItem)getChild(groupPosition, childPosition);
        
        LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			if (getChildType(groupPosition, childPosition) == 1)
				convertView = inflater.inflate(R.layout.child_item_2, null);
			else
				convertView = inflater.inflate(R.layout.child_item, null);
		}

        TextView param_key = (TextView) convertView.findViewById(R.id.param_key);
        EditText param_value = (EditText) convertView.findViewById(R.id.param_value);
        CheckBox param_checkbox = (CheckBox) convertView.findViewById(R.id.param_checkbox);
        if(item.options!=null){
        	Spinner param_spinner = (Spinner) convertView.findViewById(R.id.param_spinners);	
            ArrayList<String>  li=new ArrayList<String>();

			if (item.options.size() > 0) {
				for (int i = 0; i < item.options.size(); i++) {
					li.add(item.options.get(i));
				}
				ArrayAdapter<String> adp = new ArrayAdapter<String>(context,
						android.R.layout.simple_spinner_item, li);
				adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
				param_spinner.setAdapter(adp);
			}
        }
        
        TextView param_unit = (TextView) convertView.findViewById(R.id.param_unit);
        ImageView param_exp = (ImageView) convertView.findViewById(R.id.param_exp);


        param_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					Toast.makeText(context,"Bro, try Android :)", Toast.LENGTH_LONG).show();
				}

			}
		});
        
      
        
        param_checkbox.setChecked(item.enabled); 
        param_key.setText(item.param);
        param_value.setText(item.value);
        param_unit.setText(item.unit);

        param_exp.setOnClickListener(new OnClickListener() {
     	   public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Parameter Info");
                builder.setMessage(item.exp);
                builder.setCancelable(false);
//                builder.setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                List<ITGItem> child = 
//                                    laptopCollections.get(laptops.get(groupPosition));
//                                child.remove(childPosition);
//                                notifyDataSetChanged();
//                            }
//                        });
                builder.setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
     });
        return convertView;
    }
	public void writeOut(String str){
		System.out.println(str);
	}
    public int getChildrenCount(int groupPosition) {
    	//System.out.println("Count:"+laptopCollections.get(laptops.get(groupPosition)).size());
        return laptopCollections.get(laptops.get(groupPosition)).size();
    }
 
    public Object getGroup(int groupPosition) {
        return laptops.get(groupPosition);
    }
 
    public int getGroupCount() {
        return laptops.size();
    }
 
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.laptop);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName);
        return convertView;
    }
 
    public boolean hasStableIds() {
        return true;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}