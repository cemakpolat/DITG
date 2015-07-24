package com.ditg.params;

import java.util.ArrayList;
import java.util.HashMap;

import com.ditg.main.ITGItem;
import com.ditg.main.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LazyAdapter extends BaseAdapter {
    
	
    private Activity context;
    //private ArrayList<String> params;
    private ArrayList<ITGItem> itglist;
    private static LayoutInflater inflater=null;
    
   
//    public LazyAdapter(Activity a,  ArrayList<String> params) { 
//        activity = a;
//        this.params=params;
//        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
    public LazyAdapter(Activity a,  ArrayList<ITGItem> params) { 
    	context = a;
        this.itglist=params;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount() {
        return itglist.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    /**
     * Return different type of view
     */
    @Override
    public int getItemViewType (int position){
    	if(itglist.get(position).options!=null){
    		return 1;
    	}else{
    		return 0;
    	}
    }
    /**
     * There are two different view
     */
    @Override
    public int getViewTypeCount(){
    	return 2;
    }
    

	public View getView(int position, View convertView, ViewGroup parent) {
        final ITGItem item=this.itglist.get(position);

//		if (convertView == null)
//			vi = inflater.inflate(R.layout.child_item, null);
		if (convertView == null) {
			if (getItemViewType(position) == 1)
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
            	// set title
                builder.setTitle("Parameter Info");
                builder.setMessage(item.exp);
                builder.setCancelable(false);

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
}