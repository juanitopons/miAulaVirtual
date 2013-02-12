package com.jp.miaulavirtual;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class FileManager extends Activity {
	public String mys;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_manager);
		
		Log.d("Lifecycle4", "In the onCreate()");
		
		Bundle bund = getIntent().getExtras();
		if(bund == null) {
			Log.d("Texto1", "Hola1");
		} else {
			Log.d("Texto1", "uacamole");
		}
	}

	public Object onRetainNonConfigurationInstance() {
		
		mys = "Muahahaha";
		return(true);
	}
	
    public void onStart()
    {
        super.onStart();
        Log.d("Lifecycle4", "In the onStart()");
        
    }
    
    public void onRestart()
    {
        super.onRestart();
        Log.d("Lifecycle4", "In the onRestart()");

    }
    
    public void onResume()
    {
        super.onResume();
        Log.d("Lifecycle4", "In the onResume()");

    }
    
    public void onPause()
    {
        super.onPause();
        Log.d("Lifecycle4", "In the onPause()");
    }
    
    public void onStop()
    {
        super.onStop();
        Log.d("Lifecycle4", "In the onStop()");
    }
    
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("Lifecycle4", "In the onDestroy()");

    }

}
