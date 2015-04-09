package com.grabow.commuter.plugin_tester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewFragment extends Fragment {
	
	// The method loadWebView() in WebViewFragment.java should be considered first.
	// Here, you specify which script in the asset folder will be loaded
	
	public WebViewFragment() {
	}

	WebView wv;
	String browserLaunchLink;
	boolean reload = true;
	String lat;
	String lng;
	String zoom;
	String zoomD;
	
	
	final static boolean DEBUG = true;
	final static String CURRENT_VERSION = "2.8";
	final static String TAG = "HAG";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (DEBUG) Log.i(TAG, "onCreateView");

		if (container == null) {
			return null;
		}


		if (wv != null) {
			wv.destroy();
		}

		wv = new WebView(getActivity());
					 
		// Must be - security is alwasy an issue		
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setDomStorageEnabled(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setMixedContent(wv);
		}
		wv.requestFocusFromTouch();
		
		wv.addJavascriptInterface(new WebAppInterface(getActivity()), "grabowCommuter");
		
		wv.setWebChromeClient(new WebChromeClient() {

			// To enable geloaction
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}

			// To enable alerts from javascript
			@Override
			public boolean onJsAlert(WebView view, String url,
					String message, final android.webkit.JsResult result) {
				new AlertDialog.Builder(getActivity())
						.setTitle("Alert")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			}
			
			// To enable confirms from javascript
			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final android.webkit.JsResult result) {
				new AlertDialog.Builder(getActivity())
						.setTitle("Dialog")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								}).create().show();
				return true;
			}
			
			// To enable promts from javascript
			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final android.webkit.JsPromptResult result) {
				final LayoutInflater factory = LayoutInflater.from(getActivity());
				final View v = factory.inflate(
						R.layout.javascript_p_dialog, null);

				((TextView) v.findViewById(R.id.prompt_message_text))
						.setText(message);
				((EditText) v.findViewById(R.id.prompt_input_field))
						.setText(defaultValue);

				new AlertDialog.Builder(getActivity())
						.setTitle("Dialog")
						.setView(v)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										String value = ((EditText) v
												.findViewById(R.id.prompt_input_field))
												.getText().toString();
										result.confirm(value);
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										result.cancel();
									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									public void onCancel(DialogInterface dialog) {
										result.cancel();
									}
								}).show();

				return true;
			};
			
		});

		return wv;
	}
	
	public class WebAppInterface {
	    Context mContext;
	    
	    /** Instantiate the interface and set the context */
	    WebAppInterface(Context c) {
	        mContext = c;
	    }

	    /** Show a toast from the web page */
	    @JavascriptInterface
	    public void showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	    }
	    
	    @JavascriptInterface
	    public String getVersion() {
	        return CURRENT_VERSION;
	    }		    
	}
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (DEBUG) Log.i(TAG, "onCreate");			
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onResume() {
		if (DEBUG) Log.i(TAG, "onResume");
		super.onResume();
		wv.onResume();
		loadWebView();
	}
	
	@Override
	public void onPause() {
		if (DEBUG) Log.i(TAG, "onPause");			
		wv.onPause();		
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		if (DEBUG) Log.i(TAG, "onDestroy()");
		if (wv != null) {
			wv.destroy();
			wv = null;
		}
		super.onDestroy();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (DEBUG) Log.i(TAG, "Fragment:onCreateOptions");
		inflater.inflate(R.menu.webfrag_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (DEBUG) Log.i(TAG, "Fragemnt:opPrepareOptions");

		if ((browserLaunchLink == null) || browserLaunchLink.isEmpty()) {
			menu.findItem(R.id.launch).setVisible(false);
		} else {
			menu.findItem(R.id.launch).setVisible(true);
		}
		
		if (!reload) {
			menu.findItem(R.id.refresh).setVisible(false);
		} else {
			menu.findItem(R.id.refresh).setVisible(true);
		}
	}
	
	@TargetApi(21)
	private void setMixedContent(WebView wv) {
		wv.getSettings().setMixedContentMode(
				WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			doRefresh(); 
			break;
		case R.id.launch:
			launchBrowser(browserLaunchLink);
			break;
		default:
			break; 
		}
		return false;
	}
	
	public void doRefresh() {
		wv.clearCache(true);

		String clearScript = "localStorage.clear();"
				+ "sessionStorage.clear();"
				+ "var cookies = document.cookie.split(';');"
				
				+ " for (var i = 0; i < cookies.length; i++) {"
				+ "    var cookie = cookies[i];"
				+ "    var eqPos = cookie.indexOf('=');"
				+ "    var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;"
				+ "    document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:00 GMT';"
				+ "}";					
		wv.loadUrl("javascript: " + clearScript);			
		wv.loadUrl("file:///android_asset/infAppPause.html");			 
		loadWebView();
		
	}
	      
	public void setOptions(WebView wv, boolean zoomCtrl, boolean geoLocEnable, boolean backButton, boolean lockScreenRot, boolean reload) {
		
		if (zoomCtrl)
			wv.getSettings().setBuiltInZoomControls(true);
		if (backButton)
			setBackButton(wv);
		if (!geoLocEnable) {
			wv.getSettings().setGeolocationEnabled(false);
		}
		if (lockScreenRot) {
			int currentOrientation = getResources().getConfiguration().orientation;
			if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}
			else {
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			}
		}
		this.reload = reload;
		
	}
	
	public void setBackButton(WebView wv) {
		wv.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					WebView webView = (WebView) v;

					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						if (webView.canGoBack()) {
							webView.goBack();
							return true;
						}
						break;
					}
				}
				return false;
			}
		});
	}
	
	
	public void launchBrowser(String link) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(link));
		startActivity(intent);
	}
			
	private String replace(String script) {
		if (script != null) {
			String out = script.replace("#lat#", lat);
			out = out.replace("#lng#", lng);
			out = out.replace("#zoom#", zoom);
			out = out.replace("#zoomD#", zoomD);
			return out;
		} else {
			return null;
		}
	}
	
	private String readFileFromAsset(String filename) {
		// Return: String or null
		InputStream fIn = null;

		// Read the file from the assets folder
		try {
			fIn = getActivity().getResources().getAssets().open(filename);
			if (DEBUG)
				Log.i(TAG, "Reading assets... ");
		} catch (IOException e1) {
			if (DEBUG)
				Log.e(TAG, "Can't open assets: " + Log.getStackTraceString(e1));
			return null;
		}

		// Convert file to string using String Builder
		String result;
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(fIn, "utf-8"), 8);
			StringBuilder sBuilder = new StringBuilder();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				sBuilder.append(line + "\n");
			}

			fIn.close();
			result = sBuilder.toString();

		} catch (Exception e) {
			if (DEBUG)
				Log.e(TAG,
						"Error readInString: \n"
								+ Log.getStackTraceString(e));
			result = null;
		}
		return result;
	}
			
	// -------------------------------------------------------------------------------------
	// This method is useful for testing your module
	// -------------------------------------------------------------------------------------
	public void loadWebView() { 
		 
		// Load your Script (e.g. plugin.js) form the assets-folder of this project
		// Example 1: 
		String myJsonStr = readFileFromAsset("alert_confirm_promt.json");   
		
		// Example 2: 
		// String myJsonStr = readFileFromAsset("speedometer_digital.json");

		// Example 3: 
		// String myJsonStr = readFileFromAsset("google_satellite.json");
		  
		// Default
		// String myJsonStr = readFileFromAsset("plugin.js");
		
		// For testing just set some values, e.g. Berlin
		// In values represent the center of the Master Map 
		lat = "52.519432961166046";
		lng = "13.402783870697021";
		zoom = "12"; 
		zoomD = "12.567";		
		    
		// Take the settings from JSON
		// --------------------------------------------------------------------------------
		try {
			JSONObject myObj = new JSONObject(myJsonStr);

			final boolean backButton = myObj.optBoolean("backButton");
			final boolean enableGPS = myObj.optBoolean("enableGPS");
			final boolean zoomControl = myObj.optBoolean("zoomControl");
			final boolean screenLockRot = myObj.optBoolean("screenLockRot");
			final boolean reload = myObj.optBoolean("reload");
			final String shouldOverrideUrlLoading1 = replace(myObj.optString(
					"shouldOverrideUrlLoading1", "#X#"));
			final String shouldOverrideUrlLoading2 = replace(myObj.optString(
					"shouldOverrideUrlLoading2", "#X#"));
			String loadUrl = replace(myObj.optString("loadUrl", null));
			
			String loadDataWithBaseUrl1 = replace(myObj.optString(
					"loadDataWithBaseUrl1", null));
			String loadDataWithBaseUrl2 = replace(myObj.optString(
					"loadDataWithBaseUrl2", null));
			String loadDataWithBaseUrl3 = replace(myObj.optString(
					"loadDataWithBaseUrl3", "text/html"));
			String loadDataWithBaseUrl4 = replace(myObj.optString(
					"loadDataWithBaseUrl4", "utf-8"));
			String loadDataWithBaseUrl5 = replace(myObj.optString(
					"loadDataWithBaseUrl5", null));

			final String onPageFinishedLoadUrl = replace(myObj.optString(
					"onPageFinishedLoadUrl", null));
			
			String browserLaunchLink = replace(myObj.optString(
					"browserLaunchLink", null));

			if (DEBUG) {
				Log.i("HAG", "backButton: " + backButton);
				Log.i("HAG", "enableGPS: " + enableGPS);
				Log.i("HAG", "zoomControl: " + zoomControl);
				Log.i("HAG", "screenLockRot: " + screenLockRot);
				Log.i("HAG", "reload: " + reload);
				Log.i("HAG", "shouldOverrideUrlLoading1: "
						+ shouldOverrideUrlLoading1);
				Log.i("HAG", "shouldOverrideUrlLoading2: "
						+ shouldOverrideUrlLoading2);
				Log.i("HAG", "loadUrl: " + loadUrl);

				Log.i("HAG", "loadDataWithBaseUrl1: " + loadDataWithBaseUrl1);
				Log.i("HAG", "loadDataWithBaseUrl2: " + loadDataWithBaseUrl2);
				Log.i("HAG", "loadDataWithBaseUrl3: " + loadDataWithBaseUrl3);
				Log.i("HAG", "loadDataWithBaseUrl4: " + loadDataWithBaseUrl4);
				Log.i("HAG", "loadDataWithBaseUrl5: " + loadDataWithBaseUrl5);
				
				Log.i("HAG", "onPageFinishedLoadUrl: " + onPageFinishedLoadUrl);
				
				Log.i("HAG", "browserLaunchLink: " + browserLaunchLink);
			}

			// Execute the scripts
			// -----------------------------------------------------------------------------------------
			if (DEBUG) Log.i("HAG", "Execute webview! ");

			setOptions(wv, zoomControl, enableGPS, backButton, screenLockRot, reload);
			wv.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (DEBUG)
						Log.i("HAG", "URL:" + url);
					if ((url.startsWith(shouldOverrideUrlLoading1))
							|| (url.startsWith(shouldOverrideUrlLoading2))) {
						return false;
					} else {
						launchBrowser(url);
						return true;
					}
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					if (!((onPageFinishedLoadUrl == null) || onPageFinishedLoadUrl
							.isEmpty())) {
						super.onPageFinished(wv, url);
						wv.loadUrl(onPageFinishedLoadUrl);
					} else {
						return;
					}
				}
				
			});

			if (!((loadUrl == null) || loadUrl.isEmpty())) {
				wv.loadUrl(loadUrl);
			}

			if (!((loadDataWithBaseUrl2 == null) || loadDataWithBaseUrl2
					.isEmpty())) {
				
				wv.loadDataWithBaseURL(loadDataWithBaseUrl1,
						loadDataWithBaseUrl2, loadDataWithBaseUrl3,
						loadDataWithBaseUrl4, loadDataWithBaseUrl5);
			}

			this.browserLaunchLink = browserLaunchLink;


		} catch (JSONException e) {
			if (DEBUG) Log.e(TAG, "JSON-Error: " + Log.getStackTraceString(e));
			Toast.makeText(getActivity(), "Plug-In error: \n" + e, Toast.LENGTH_LONG).show();
		}
	}
}
