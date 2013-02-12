package com.jp.miaulavirtual;

import java.io.File;
import java.io.FileFilter;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

public class FileManager extends Activity {
	private ListView lstDocs;
	private ListManagerAdapter lstAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_manager);
		
		Log.d("Lifecycle4", "In the onCreate()");

		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		//you may also want to add (...|| Environment.MEDIA_MOUNTED_READ_ONLY)
		//if you are only interested in reading the filesystem
		if(!isSDPresent) {
		    //handle error here
		}
		else {
		    //do your file work here
		}
		
		FileFilter filterDirectoriesOnly = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		};
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/Android/data/com.jp.miaulavirtual/files/");
		File[] sdDirectories = myDir.listFiles(filterDirectoriesOnly);
		
		// our listView
        lstDocs = (ListView)findViewById(R.id.LstDocs2); // Declaramos la lista
        lstAdapter = new ListManagerAdapter(this, sdDirectories);
        lstDocs.setAdapter(lstAdapter); // Declaramos nuestra propia clase adaptador como adaptador
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
