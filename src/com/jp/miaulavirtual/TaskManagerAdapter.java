/*
* Copyright (C) 2013 Juan Pons (see README for details)
* This file is part of miAulaVirtual.
*
* miAulaVirtual is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* miAulaVirtual is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with miAulaVirtual. If not, see <http://www.gnu.org/licenses/agpl.txt>.
*
*/

package com.jp.miaulavirtual;


import android.app.Activity;

import android.content.pm.ActivityInfo;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Creamos nuestro propio adaptador para la lista con la siguiente clase: AdaptadorDocs
 */

class TaskManagerAdapter extends BaseAdapter {
    
    Activity context;
    String[] names;
    Boolean status;
    int type;

    public TaskManagerAdapter(Activity context,  String[] names) {
    	 super();
         this.context = context;
         if(names!=null) {
	         this.names = new String[names.length];
	         System.arraycopy(names, 0, this.names, 0, names.length);
         }
         status = true;
    }
    
    public void setNames(String[] names) {
    	this.names = new String[names.length];
        System.arraycopy(names, 0, this.names, 0, names.length);
    }
    
    public void setNamesToNull() {
    	names = null;
    }
    
    public void setStatus(Boolean status, int type) {
    	this.status = status;
    	this.type = type;
    }
    
    public void setStatus(Boolean status) {
    	this.status = status;
    }
     
    public int getCount() {
    	if(names==null) return 1;
        return names.length;
    }
    
    public Object getItem(int position) {
        return position;
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    public void clearData() {
        // clear the data
        names = null;
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
    	if(status) {
	    	if(names!=null) {
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
		
			    // Image
			    image = (ImageView)item.findViewById(R.id.folderImage);
			    ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/icon_folder", null, null); // Folder ico
		    	image.setImageResource(ico);
		    	rgxTitle = names[position].toString();
		    	title.setText(rgxTitle.trim());
	        
	        /*
	         * Trick: When we clear the List (because user click any subject) we change the item count to 1 and we load the loading page (load_list) with restricted orientation.
	         */
	    	} else { 			
	    		setRestrictedOrientation();
	    
	    		LayoutInflater inflater = context.getLayoutInflater();
	    	    item = inflater.inflate(R.layout.load_list, null);
	    	    item.setMinimumHeight(35);
	    	}
    	} else {
    		setRestrictedOrientation();
    		
    		LayoutInflater inflater = context.getLayoutInflater();
    	    item = inflater.inflate(R.layout.reload_list, null);
    	    item.setMinimumHeight(35);
    	    
    	    TextView msg = (TextView) item.findViewById(R.id.reload);
    	    switch(type) {
    	    case 0:
    	    	msg.setText(context.getString(R.string.reload));
    	    	break;
    	    case 1:
    	    	msg.setText(context.getString(R.string.panel_error3));
    	    	break;
    	    case 2:
    	    	msg.setText(context.getString(R.string.no_tasks));
    	    	break;
    	    }
    	    Log.d("Names", "FUNCIONA2");
    	}
        return(item);
    }
}
