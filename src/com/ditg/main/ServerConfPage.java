package com.ditg.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class ServerConfPage extends Activity {
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.itgrecv_parameters);

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
   
	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(CommonDefinitions.ITG);
	    getActionBar().setTitle(message);
	    getActionBar().setLogo(R.drawable.ic_launcher); // return here the appropriate user photo
	    
	    // Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(40);
	    textView.setText(message);

	    // Set the text view as the activity layout
	    setContentView(textView);
	    
	    
	    // add here the configuration list 
	    // load here the configuration list
	    
	}
}
