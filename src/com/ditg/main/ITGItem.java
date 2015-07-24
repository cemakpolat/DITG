package com.ditg.main;

import java.util.ArrayList;

public class ITGItem {
	public boolean enabled=false;
	public String param="";
	public String value=null;
	public String unit=null;
	public ArrayList<String> options=null;
	public String exp="";
	
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public ArrayList<String> getOptions() {
		return options;
	}
	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}

}
