package com.jp.miaulavirtual;

import java.io.File;

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

class ListManagerAdapter extends BaseAdapter {
    
	private Boolean isHomePath;
    private Activity context;
    private File[] data; // 0 = names 1 = urls 2 = types

    public ListManagerAdapter(Activity context,  File[] data, Boolean isHome) {
    	 super();
    	 isHomePath = isHome;
         this.context = context;
         if(!isHome) {
	         this.data = new File[data.length+1];
	         System.arraycopy(data, 0, this.data, 1, data.length);
         } else {
	         this.data = new File[data.length];
	         System.arraycopy(data, 0, this.data, 0, data.length); 
         }
    }
    
    public void setData(File[] data) {
        if(!isHomePath) {
        	 this.data = null;
	         this.data = new File[data.length+1];
	         System.arraycopy(data, 0, this.data, 1, data.length);
        } else {
	         this.data = data;
        }
    }
    
    public void setIsHomePath(Boolean isHomePath) {
    	this.isHomePath = isHomePath;
    }
    
    public void clear() {
    	this.data = null;
    }
    public int getCount() {
    	if(data==null) return 1;
        return data.length;
    }
    
    public Object getItem(int position) {
        return position;
    }
    
    public long getItemId(int position) {
        return position;
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
	    LayoutInflater inflater = context.getLayoutInflater();
	    	
	    item = inflater.inflate(R.layout.list_docs, null);
	    item.setMinimumHeight(65);  
	    item.setPadding(14, 0, 6, 0);
	        if(data!=null) {
	        TextView title;
	        ImageView image;
	        int ico;
	        // Title
		    title = (TextView)item.findViewById(R.id.list_title);
		    String rgxTitle;
		    // Image
		    image = (ImageView)item.findViewById(R.id.folderImage);
		    
		    if(isHomePath) {
		    	rgxTitle = data[position].getName();
		    	title.setText(rgxTitle);
		    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/icon_folder", null, null); // Back ico
		    	image.setImageResource(ico);
		    } else {
			    if(position==0){
			    	title.setText("Atrás");
			    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_back", null, null); // Back ico
			    	image.setImageResource(ico);
			    } else {
				    // Get extension file
			        String file_s = data[position].toString();
			        String extension = "";
			
					int i = file_s.lastIndexOf('.');
					int p = Math.max(file_s.lastIndexOf('/'), file_s.lastIndexOf('\\'));
			
					if (i > p) {
					    extension = file_s.substring(i+1);
					}
					extension = extension.toLowerCase();
				    Log.d("extension", extension);
				    
				    rgxTitle = data[position].getName().replaceFirst("[.][^.]+$", "");
			    	title.setText(rgxTitle);
			    	ico = context.getResources().getIdentifier("com.jp.miaulavirtual:drawable/"+setIco(extension), null, null); // Back ico
			    	image.setImageResource(ico);
			    }
		    }   
    	}
		return(item);
    }
	
	public String setIco(String ext) {
	    if(ext.equalsIgnoreCase("pdf")) return "ic_pdf";
	    if(ext.equalsIgnoreCase("xls")) return "ic_excel";
	    if(ext.equalsIgnoreCase("ppt")) return "ic_ppt";
	    if(ext.equalsIgnoreCase("doc")) return "ic_word";
	    if(ext.equalsIgnoreCase("docx")) return "ic_word";
	    return "ic_def";
	}
}
