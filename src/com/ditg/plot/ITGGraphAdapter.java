package com.ditg.plot;

import java.util.ArrayList;
import java.util.HashMap;

import com.ditg.main.R;
import com.ditg.main.ITGItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ITGGraphAdapter extends BaseAdapter {
    
    private Activity context;
    private ArrayList<ITGGraphItem> itgGraphlist;
    private static LayoutInflater inflater=null;
    
    /**
     * 
     * @param a
     * @param params
     */
    public ITGGraphAdapter(Activity a,  ArrayList<ITGGraphItem> params) { 
    	context = a;
        this.itgGraphlist=params;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /**
     * 
     */
    public int getCount() {
        return itgGraphlist.size();
    }

    /**
     * 
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * 
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * 
     */
	public View getView(int position, View convertView, ViewGroup parent) {
        final ITGGraphItem item=this.itgGraphlist.get(position);

		if (convertView == null) {
				convertView = inflater.inflate(R.layout.child_graph_item, null);
		}

		TextView param_key = (TextView) convertView.findViewById(R.id.param_key);
        EditText param_value = (EditText) convertView.findViewById(R.id.param_value);
        CheckBox param_checkbox = (CheckBox) convertView.findViewById(R.id.param_checkbox);
      
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
        param_value.setText(item.param);

		return convertView;
	}
}