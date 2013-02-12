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
 
        // Tab for Photos
        TabSpec photospec = tabHost.newTabSpec("Photos");
        // setting Title and Icon for the Tab
        photospec.setIndicator("Documentos", getResources().getDrawable(R.drawable.tab_indicator_ab_actionbar));
        
        Intent intent = getIntent();
        Intent myintent = new Intent(intent);
        myintent.setClass(this, DisplayMessageActivity.class);
        Log.d("Actividad", "Creando DisplayMessage");
        photospec.setContent(myintent);
 
        // Tab for Songs
        TabSpec songspec = tabHost.newTabSpec("Songs");
        songspec.setIndicator("Gestor", getResources().getDrawable(R.drawable.tab_indicator_ab_actionbar));
        Intent songsIntent = new Intent(this, FileManager.class);
        songspec.setContent(songsIntent);
 
        // Tab for Videos
        TabSpec videospec = tabHost.newTabSpec("Videos");
        videospec.setIndicator("Tareas", getResources().getDrawable(R.drawable.tab_indicator_ab_actionbar));
        Intent videosIntent = new Intent(this, FileManager.class);
        videospec.setContent(videosIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(photospec); // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab
        tabHost.addTab(videospec); // Adding videos tab
        
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