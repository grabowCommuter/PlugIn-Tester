##How to create a PlugIn for ACom?


To create a PlugIn, you have to perform three steps:

 **1. Develop the PlugIn:** You have to define the behavior of the PlugIn, that means define which script should be executed, which url
    should be loaded, etc. Our PlugIn-Developer-App will help you. 
    
 **2. Test the PlugIn:** Describe the behavior the the PlugIn in a JSON-Object and test the JSON-Object in our PlugIn-Tester.
    
 **3. Publish the PlugIn** (if you want to) on github.

---------

###Part 2: Test the PlugIn

With [Part 1: Develop the PlugIn](https://github.com/grabowCommuter/PlugIn-Developer) you have obtained a set of arguments, with which our methods are invoked.

Now, you have to define a JSON-Object which contains these arguments. You can find a template (`PlugIn_Template.json`) of all possible arguments in the `assets`-folder in this repository. Just take this template and modify it. 

We will specify the arguments in the following section:

#### JSON-Format of the PlugIn

The PlugIn is a JSON-Object consisting of following key-value-pairs:

{
**`"moduleId"` : `" … fullname of github repo …"`**,	// Mandatory: This field is required for publishing later on. Each PlugIn has a unique moduleId, which is the name of the github-repository, it is available at. 

**`"title" : "Template"`**, // Mandatory: Each PlugIn has a title. This title is show on the navigation drawer in the ACom app.

**`"subtitle" : "Template for PlugIns"`**,  // Mandatory: Each PlugIn has a subtitle. This subtitle is shown on the navigation drawer in the ACom app. If your PlugIn calls an external website, you should put the URL here.

**`"backButton" : false`**,  // Optional (default: false): BackButton will be used for navigation inside PlugIn (true) or for finishing the PlugIn (false)

**`"enableGPS" : false`**,  // Optional (default: false): Enable location feature for the PlugIn (true) or block location (false)

**`"zoomControl" : false`**, // Optional (default: false): Show the zoomContrl widgets (true) or hide the widgets (false)

**`"screenLockRot" : false`**, // Optional (default: false): Lock the screen orientation as it is (true) or allow change of screen orientation (false)

**`"reload" : false`**,  // Optional (default: false): Enable clean-reload-feature (true) or disable clean-and-reload (false). If true, clean&reload will delete all cookies and local storage and the PlugIn will be reloaded. 

**`"shouldOverrideUrlLoading1" : null`**,  // Optional; default: null: Specify the url or prefix of the url which is handled by your PlugIn. (If you want every webpage to be handled by your PlugIn define „http“ as prefix).

**`"shouldOverrideUrlLoading2" : null`**, // Optional; default: null: Specify an alternative url or alternative  prefix of the url which is handled by your PlugIn. (Often, an alternative to „http“ is „https“).

**`"loadUrl" : null`**, // Optional, default: null: Specify an Url, which is called by your PlugIn (e.g. http://www.google.com/maps/`). If set to null, loadUrl() is not called. More info at Android documentation.

    
**`"loadDataWithBaseUrl1" : null`**, // Optional, default null: Instead of loadUrl(), you can use loadDataWithBaseUrl() for loading a script (see the Android Documentation for more information about loadDataWithBaseUrl() ). If set to null, no base url is specified.

**`"loadDataWithBaseUrl2" : null`**, // Optional, default null: Specify an Script, which is executed. If set to null, loadDataWitnBaseUrl() is not executed.

**`"loadDataWithBaseUrl3" : null`**, // Optional, default „text/html“: Specify the mime-type

**`"loadDataWithBaseUrl4" : "utf-8"`**,  // Optional, default „utf-8“: Specify the encoding.
 
**`"loadDataWithBaseUrl5" : null`**, // Optional, default null: The Url of a webpage to use as the history entry.
					
    
**`"onPageFinishedLoadUrl1" : null`**, // Optional, default null:  If you want to execute a script after the page has been loaded, you can specify the script here. It will be executed in the onPageFinished()-method. The parameters are the same as by loadDataWithBaseUrl(). If set to null, no base url is specified.

**`"onPageFinishedLoadUrl2" : null`**, // / Optional, default null: Specify a script, which is executed. If set to null, loadDataWitnBaseUrl() is not executed.

**`"onPageFinishedLoadUrl3" : null`**, // Optional, default „text/html“: Specify the mime-type

**`"onPageFinishedLoadUrl4" : „utf-8“`**, // Optional, default „utf-8“: Specify the encoding. 

**`"onPageFinishedLoadUrl5" : null`**, // Optional, default null: The Url of a webpage to use as the history entry.
					
**`"browserLaunchLink" : null`** // Optional, default null: Specify the website, which should be loaded with the options menu „launch in browser“. If set to null, this options menu item is not shown.

}

----
An example of a simple JSON-Object loading google maps satellite view:

    {
        "moduleId" : " ... github repro ...",
        "title" : "Google Satellite",
        "subtitle" : "Google Maps Satellite View",
        
        "backButton" : false,
        "enableGPS" : false,
        "zoomControl" : false,
        "screenLockRot" : false,
        
        "shouldOverrideUrlLoading1" : "http",
        "shouldOverrideUrlLoading2" : "https",
        
        "loadUrl" : "https://www.google.de/maps/@#lat#,#lng#,#zoom#z/data=!3m1!1e3?hl=en",
        				
        "browserLaunchLink" : "http://maps.google.com"
    }

-----

For testing your JSON-Object, we provide a PlugIn-Tester app. Download or clone the app from this repro, save your JSON-Object in the assets-folder, adjust the filename in the loadWebView()-method and run the app. The PlugIn will be started. View in LogCat (Tag `„HAG“`) for Debugging-Information. Look in LogCat also for the `„chromium“`-Tag, if errors occur.

-----
#### Important hints:

 - If you copy and paste a html-text or javascript-text and insert it in the JSON-file (e.g. for loadDataWithBaseUrl2 value), **replace all " (quotation marks) by '  (inverted comma) first**. Otherwise, the JSON-syntax will be violated.
 - If you just want to execute javascript with loadUrl, place the „javascript:“ Tag at the beginning (see more in the Android documentation).
 - Set shouldOverrideUrlLoading1 carefully. (Often to „http“ - otherwise the browser will be launched.
 - jQuery mobile requires a base Url (loadDataWithBaseUrl2) unequal to `null` and  '`about:blank`‘. This is for history reasons of jQuery mobile.

----
If the PlugIn behaves as you want, you are done and can proceed with [Part 3: Publish your PlugIn](https://github.com/grabowCommuter/PlugIn-Publishing).


-----
> Written with [StackEdit](https://stackedit.io/).