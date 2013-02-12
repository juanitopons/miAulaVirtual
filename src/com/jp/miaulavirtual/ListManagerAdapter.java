package com.jp.miaulavirtual;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class ListManagerAdapter extends BaseAdapter {
    
    Activity context;
    File[] data; // 0 = names 1 = urls 2 = types

    public ListManagerAdapter(Activity context,  File[] data) {
    	 super();
         this.context = context;
         this.data = data;
    }
     
    public int getCount() {
    	if(data.length==0) return 1;
        return data.length;
    }
    
    public Object getItem(int position) {
        return position;
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    public void clearData() {
        // clear the data
        data = null;
    }
    
	private void setRestrictedOrientation() {
		/* We don't want change screen orientation */
	    //---get the current display info---
	    WindowManager wm = context.getWindowManager();
	    Display d = wm.getDefaultDisplay();
	    if (d.getWidth() > d.getHeight()) {
	    	//---change to landscape mode---
	    	context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	    } else {
	    	//---change to portrait mode---
	    	context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
	    }
	}
    
	public View getView(int position, View convertView, ViewGroup parent) {
    	View item;
    	if(data.length>0) {
    	LayoutInflater inflater = context.getLayoutInflater();
    	
        item = inflater.inflate(R.layout.list_docs, null);
        item.setMinimumHeight(65);  
        item.setPadding(14, 0, 6, 0);
        TextView title;
        ImageView image;
        int ico;
        // Title
	    title = (TextView)item.findViewById(R.id.list_title);
	    String rgxTitle;
	    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); // Obtenemos las preferencias
	    
	    rgxTitle = data[position].getName();
    	title.setText(rgxTitle);
    	
    	// Image
    	image = (ImageView)item.findViewById(R.id.folderImage);
    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/icon_folder", null, null); // Folder ico
    	image.setImageResource(ico);
    	
	    // Image
	    /** image = (ImageView)item.findViewById(R.id.folderImage);
	    switch(Integer.parseInt(data.get(2)[position].toString())) {
	    case 0: // Back button
	    	rgxTitle = data.get(0)[position].toString().replaceAll( "\\d{4}-\\d{4}\\s|\\d{4}-\\d{2}\\s|Documentos\\sde\\s?|Gr\\..+?\\s|\\(.+?\\)", "" );
	    	title.setText(rgxTitle.trim());
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_back", null, null); // Back ico
	    	image.setImageResource(ico);
	    	break;
	    case 1: // Folder
	    	rgxTitle = data.get(0)[position].toString().replaceAll( "\\d{4}-\\d{4}\\s|\\d{4}-\\d{2}\\s|Documentos\\sde\\s?|Gr\\..+?\\s|\\(.+?\\)", "" );
	    	title.setText(rgxTitle.trim());
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/icon_folder", null, null); // Folder ico
	    	image.setImageResource(ico);
	    	break;
	    case 2: // PDF
	    	rgxTitle = data.get(0)[position].toString();
	    	title.setText(rgxTitle.trim());
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_pdf", null, null); // PDF ico
	    	image.setImageResource(ico);
	    	break;	    	
	    case 3: // Excel
	    	rgxTitle = data.get(0)[position].toString();
	    	title.setText(rgxTitle.trim());
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_excel", null, null); // Excel ico
	    	image.setImageResource(ico);
	    	break;
	    case 4: // Power Point
	    	rgxTitle = data.get(0)[position].toString();
	    	title.setText(rgxTitle.trim());
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_ppt", null, null); // PPT ico
	    	image.setImageResource(ico);
	    	break;
	    case 5: // Word
	    	rgxTitle = data.get(0)[position].toString();
	    	title.setText(rgxTitle.trim());
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_word", null, null); // Word ico
	    	image.setImageResource(ico);
	    	break;
	    case 6: // Communities and others
	    	rgxTitle = data.get(0)[position].toString().replaceAll( "\\d{4}-\\d{4}\\s|\\d{4}-\\d{2}\\s|Documentos\\sde\\s?|Gr\\..+?\\s|\\(.+?\\)", "" );
	    	title.setText(rgxTitle.trim());
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_comu", null, null); // Word ico
	    	image.setImageResource(ico);
	    	break;
	    default: // Default
	    	rgxTitle = data.get(0)[position].toString();
	    	title.setText(rgxTitle);
	    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_def", null, null); // Other ico
	    	image.setImageResource(ico);
	    	break;
	    }
	    if(!prefs.getBoolean("pattern", true)) title.setText(data.get(0)[position].toString()); **/
        
        /*
         * Trick: When we clear the List (because user click any subject) we change the item count to 1 and we load the loading page (load_list) with restricted orientation.
         */
    	} else { 			
    		setRestrictedOrientation();
    
    		LayoutInflater inflater = context.getLayoutInflater();
    	    item = inflater.inflate(R.layout.load_list, null);
    	    item.setMinimumHeight(35);
    	}
        return(item);
    }
}
