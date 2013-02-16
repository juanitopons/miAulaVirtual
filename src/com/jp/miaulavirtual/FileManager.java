package com.jp.miaulavirtual;

import java.io.File;
import java.io.FileFilter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FileManager extends Activity {
	private ListView lstDocs2;
	private ListManagerAdapter lstAdapter2;
	private String homePath;
	private Boolean isHomePath;
	private File[] sdData;
	private Activity mycontext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_manager);
		
		mycontext = this;
		Log.d("Lifecycle4", "In the onCreate()");

		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		//you may also want to add (...|| Environment.MEDIA_MOUNTED_READ_ONLY)
		//if you are only interested in reading the filesystem
		if(!isSDPresent) {
		    //handle error here
		}
		else {
			String root = Environment.getExternalStorageDirectory().toString();
			homePath = root + "/Android/data/com.jp.miaulavirtual/files";
			File myDir = new File(homePath);
			sdData = listFiles(myDir, true);
			
			if(sdData.length>0) {
				// our listView
				isHomePath = isHomePath(sdData[0].getParent());
		        lstDocs2 = (ListView)findViewById(R.id.LstDocs2); // Declaramos la lista
		        lstAdapter2 = new ListManagerAdapter(mycontext, sdData, isHomePath);
		        lstDocs2.setAdapter(lstAdapter2); // Declaramos nuestra propia clase adaptador como adaptador
			} else {
				//No hay archivos
			}
		}
		
		lstDocs2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) { //Al clicar X item de la lista
	            if(position == 0 && !isHomePath(sdData[position].getParent())) { // back button
	            	Log.d("Tipo", "Atrás");
	            	lstAdapter2.clearData();
            		lstAdapter2.notifyDataSetChanged();
	            	lstDocs2.setDividerHeight(0);
	            	sdData = listFiles(sdData[0].getParentFile().getParentFile(), false);
	            	isHomePath = isHomePath(sdData[0].getParent());
	            	
	            	// re-construct listView
			        lstAdapter2 = new ListManagerAdapter(mycontext, sdData, isHomePath);
			        lstDocs2.setDividerHeight(1);
			        lstDocs2.setAdapter(lstAdapter2); // Declaramos nuestra propia clase adaptador como adaptador	
	            } else {
	            	if(isHomePath(sdData[0].getParent())) {
	            		Log.d("Tipo", "Directorio");
	            		lstAdapter2.clearData();
	            		lstAdapter2.notifyDataSetChanged();
		            	lstDocs2.setDividerHeight(0);
		            	isHomePath = isHomePath(sdData[position].getAbsolutePath());
		            	sdData = listFiles(sdData[position].getAbsoluteFile(), false);
		            	
		            	// re-construct listView
				        lstAdapter2 = new ListManagerAdapter(mycontext, sdData, isHomePath);
				        lstDocs2.setDividerHeight(1);
				        lstDocs2.setAdapter(lstAdapter2); // Declaramos nuestra propia clase adaptador como adaptador
	            	} else {
		            	if(sdData[position-1].isDirectory()){
		            		Log.d("Tipo", "Directorio2");
		            		lstAdapter2.clearData();
		            		lstAdapter2.notifyDataSetChanged();
			            	lstDocs2.setDividerHeight(0);
			            	isHomePath = isHomePath(sdData[position].getAbsolutePath());
			            	sdData = listFiles(sdData[position].getAbsoluteFile(), false);
			            	
			            	// re-construct listView
					        lstAdapter2 = new ListManagerAdapter(mycontext, sdData, isHomePath);
					        lstDocs2.setDividerHeight(1);
					        lstDocs2.setAdapter(lstAdapter2); // Declaramos nuestra propia clase adaptador como adaptador
		            	} else {
		            		Log.d("Tipo", "Archivo");
			                Intent intent = new Intent();
			                intent.setAction(android.content.Intent.ACTION_VIEW);
			              
			                MimeTypeMap mime = MimeTypeMap.getSingleton();
			                
			                // Get extension file
			                String file_s = sdData[position-1].toString();
			                String extension = "";
			
			        		int i = file_s.lastIndexOf('.');
			        		int p = Math.max(file_s.lastIndexOf('/'), file_s.lastIndexOf('\\'));
			
			        		if (i > p) {
			        		    extension = file_s.substring(i+1);
			        		}
			        		
			        		// Get extension reference
			                String doc_type = mime.getMimeTypeFromExtension(extension);
			                
			                Log.d("Type", extension);
			             
			                intent.setDataAndType(Uri.fromFile(sdData[position-1]),doc_type);
			                try {
			                	startActivity(intent);
			            	} catch(ActivityNotFoundException e) {
								Toast.makeText(mycontext, "Lo siento, ha ocurrido algún error abriendo el archivo.", Toast.LENGTH_SHORT).show();
							}
		            	}
	            	}
	            }
            }
        }); 
	}
	
	public File[] listFiles(File path, Boolean filter) {
		if(filter) {
			FileFilter filterDirectoriesOnly = new FileFilter() {
			    public boolean accept(File file) {
			        return file.isDirectory();
			    }
			};
			return path.listFiles(filterDirectoriesOnly);
		}
		return path.listFiles();
	}
	
	public Boolean isHomePath(String path) {
		Log.d("Path", path);
		Log.d("Path", homePath);
		Log.d("Path", String.valueOf(path.equalsIgnoreCase(homePath)));
		return path.equalsIgnoreCase(homePath);
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
