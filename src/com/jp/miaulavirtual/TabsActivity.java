package com.jp.miaulavirtual;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

@SuppressWarnings("deprecation")
public class TabsActivity extends TabActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        Log.d("Lifecycle3", "In the onCreate()");
 
        TabHost tabHost = getTabHost();
 
        // Tab for Docs
        TabSpec docsspec = tabHost.newTabSpec("Documentos");
        // setting Title and Icon for the Tab
        docsspec.setIndicator("Documentos", getResources().getDrawable(R.drawable.tab_indicator_ab_actionbar));
        
        Intent intent = getIntent();
        Intent myintent = new Intent(intent);
        myintent.setClass(this, DisplayMessageActivity.class);
        docsspec.setContent(myintent);
 
        // Tab for Manager
        TabSpec managerspec = tabHost.newTabSpec("Gestor");
        managerspec.setIndicator("Gestor", getResources().getDrawable(R.drawable.tab_indicator_ab_actionbar));
        Intent songsIntent = new Intent(this, FileManager.class);
        managerspec.setContent(songsIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(docsspec); // Adding docs tab
        tabHost.addTab(managerspec); // Adding manager tab
        
        TabWidget tw = getTabWidget();

        for (int i = 0; i < tw.getChildCount(); i++) {
                    View v = tw.getChildAt(i);
                    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_indicator_ab_actionbar));
        }
        
    }
    
    public void onStart()
    {
        super.onStart();
        Log.d("Lifecycle3", "In the onStart()");
        
    }
    
    public void onRestart()
    {
        super.onRestart();
        Log.d("Lifecycle3", "In the onRestart()");

    }
    
    public void onResume()
    {
        super.onResume();
        Log.d("Lifecycle3", "In the onResume()");

    }
    
    public void onPause()
    {
        super.onPause();
        Log.d("Lifecycle3", "In the onPause()");
    }
    
    public void onStop()
    {
        super.onStop();
        Log.d("Lifecycle3", "In the onStop()");
    }
    
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("Lifecycle3", "In the onDestroy()");

    }

}