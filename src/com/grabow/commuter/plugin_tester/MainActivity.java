package com.grabow.commuter.plugin_tester;

import android.app.Activity;
import android.os.Bundle;

import com.grabow.commuter.plugin_tester.R;

public class MainActivity extends Activity {

	// Put your plugin in the assets-folder under 'somename.json' and start this app for testing.
	// Adjust the loadWebView() method in the WebViewFragment-class to load your script.
	// Do look at the logcat with the Tag 'HAG'.
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new WebViewFragment()).commit();
		}
	}	
}

