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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class DisplayMessageActivity extends Activity {

    // flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
	
	//Variables POST
    private String url;
	public final static String RESPONSE = "com.jp.miaulavirtual.RESPONSE";
	
	private String url_back;
	private Boolean task_status = true;
	
	//Respuesta
	private Response res;
    
	private int a = 0; //Control n� de tareas 1 = GET || POST 2 = GET cookie vencida, POST, GET.
	private String scookie;
	private Map<String, String> cookies; //Obtener la cookie del cache
	
	// Activity
	private Context mycontext;
	
	private String tag = "Lifecycle2";
	
	// User data
	private String user;
	private String pass;
	
	// Valores del HOME de documentos
	private Document doc;
	private Elements elements;
	private String fServ; //Respuesta primera (home)
	private ArrayList<Object[]> first = new ArrayList<Object[]>(); // Primera respuesta (home)
	private ArrayList<Object[]> first2 = new ArrayList<Object[]>();
	
	//Nombres URL's y Types por las que se pasa y Boolean necesarios
	private ArrayList<String> onUrl = new ArrayList<String>();
	private ArrayList<String> onName = new ArrayList<String>();
	private Boolean isTheHome;
	private Boolean comunidades = false; // URL principal documentos y comunidades = true -> Carpeta Comunidades url /clubs/; Sino carpeta Principal, url-> /classes/ y otras
	private int docPosition = 0; // Necesitamos saber la posición del archivo clickeado para poder mandar la URL desde fuera del Listener
	private ProgressDialog dialog;
	private TextView headerTitle;
	
	// Interface
	private ListView lstDocs;
	private AdaptadorDocs lstAdapter;
	
	private DataUpdateReceiver dataUpdateReceiver;
	
	//BroadcastReceiver, recibe variables de nuestro servicio posteriormente ejecutado CurlService. Lo utilizamos para poder enviar mensaje de excepciones o de procesos acabados.
	private class DataUpdateReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(DisplayMessageActivity.RESPONSE)) {
	        	fServ = intent.getStringExtra("response");
        		if(fServ.equals("cont")) {
        			Toast.makeText(getBaseContext(),getString(R.string.toast_1), Toast.LENGTH_SHORT).show();
        		} else if(fServ.equals("oops")) {
        			int id =  intent.getIntExtra("id", 5);
        			String msg = null;
        			Log.d("Broadcaster", String.valueOf(id));
        		    switch(id) {
        		    case 0: // cancelled
        		    	dialog.dismiss();
        		    	msg = getString(R.string.toast_0);
        		    	break;
        		    case 2: // not enough free storage space
        		    	dialog.dismiss();
        		    	msg = getString(R.string.toast_2);
        		    	break;
        		    case 3: // invalid URL
        		    	dialog.dismiss();
        		    	msg = getString(R.string.toast_3);
        		    	break;
        		    case 4: // file not found
        		    	dialog.dismiss();
        		    	msg = getString(R.string.toast_4);
        		    	break;
        		    case 5: // general error
        		    	msg = getString(R.string.toast_5);
        		    	break;
        		    case 6: // conection problems
        		    	msg = getString(R.string.toast_6);
        		    	afterBroadcaster2();
        		    	break;
        		    case 7: // conection problems 2
        		    	dialog.dismiss();
        		    	msg = getString(R.string.toast_6);
        		    	afterBroadcaster2();
        		    	break;
        		    case 8: // general error 2
        		    	dialog.dismiss();
        		    	msg = getString(R.string.toast_5);
        		    	afterBroadcaster2();
        		    	break;
        		    } 
        			Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        		} else {
        			if(isTheHome) {
	            		if(docPosition == 0) comunidades = true; //Click Comunidades y otros
	            		onUrl.add(first.get(1)[docPosition].toString());
	            		onName.add(first.get(0)[docPosition].toString());
	            	} else { 
	            		if(docPosition==0){ //Click atrás
	            			onUrl.remove(onUrl.size() - 1); //Quitamos la URL actual
	            			onName.remove(onName.size() - 1); //Quitamos el Nombre actual
	            			comunidades = false;
	            			if(onUrl.size()>=2 && (onUrl.get(0).toString().equals(onUrl.get(1).toString()))) comunidades = true;
	            		} else {
	            			onUrl.add(first.get(1)[docPosition].toString());
	                    	onName.add(first.get(0)[docPosition].toString());
	            		}
	            	}
	        		afterBroadcaster(fServ);
        		}
	        }
	    }
	}
	
	/**
	 * We need to save the Response of the connection for recreate it when activity change orientation
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		//---save whatever you want here; it takes in an Object type---
		
		// we pass the Arraylist to String[] so we can put it, after, inside de 'first' and then can pass the ALL the data needed with one Object
		String [] urls = new String[onUrl.size()-1];
		String [] names = new String[onName.size()-1];
		urls = onUrl.toArray(urls);
		names = onName.toArray(names);
		first2.add(urls);
		first2.add(names);
		
		// Save the cookie
		String[] cookie_values = cookies.values().toArray(new String[0]);
		String[] cookie_keys = cookies.keySet().toArray(new String[0]);
		first2.add(cookie_keys);
		first2.add(cookie_values);
		
		return(first2);
	}

	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        Log.d(tag, "In the onCreate() event");
        
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        
        // general context
        mycontext = this;
        
        // Get the onRetainNonConfigurationInstance()
        final ArrayList<Object[]> first3 = (ArrayList<Object[]>) getLastNonConfigurationInstance();
        
        if(first3 != null) { /* if exists it's because there was a orientation change. We need to reformat as we need the data passed */
            // onUrl and onName to ArrayList
            onUrl = new ArrayList<String>(Arrays.asList((String[]) first3.get(3))); // hierarchical urls
            onName = new ArrayList<String>(Arrays.asList((String[]) first3.get(4))); // hierarchical names
            first3.remove(3);
            first3.remove(3);
            
            // cookies to Map
            cookies = new HashMap<String, String>();
            for (int i = 0; i < first3.get(3).length; i++) {
		          cookies.put(first3.get(3)[i].toString(), first3.get(4)[i].toString());
		    }
            first3.remove(3);
            first3.remove(3);
            
        	// first 3 = first 1
            for (Object[] objects: first3) first.add((Object[])objects.clone());
            // first 3 = first 2
            for (Object[] objects: first3) first2.add((Object[])objects.clone());
            first3.clear();
            
            // Update de subtitle
            headerTitle = (TextView) findViewById(R.id.LblSubTitulo); // Título Header
            headerTitle.setTextColor(getResources().getColor(R.color.list_title));
            headerTitle.setTypeface(null, 1);
            headerTitle.setText(onName.get(onName.size() - 1));
            
            // retrieve the same View before change orientation
            lstDocs = (ListView)findViewById(R.id.LstDocs); // Declaramos la lista
            lstAdapter = new AdaptadorDocs(this, first2);
            lstDocs.setAdapter(lstAdapter); // Declaramos nuestra propia clase adaptador como adaptador

        } else {
        	
            // cookies
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
            scookie = prefs.getString("cookies", ""); //�Existe cookie?
    	    if(scookie!="") {
    	    	cookies = toMap(scookie); //Si existe cookie la pasamos a MAP
    	    }
        
	        onUrl.add("/dotlrn/?page_num=2");
	        onName.add("Documentos");
	        
	        // Actualizamos nombre de LblSubTitulo
	        headerTitle = (TextView) findViewById(R.id.LblSubTitulo); // Título Header
	        headerTitle.setTextColor(getResources().getColor(R.color.list_title));
	        headerTitle.setTypeface(null, 1);
	        headerTitle.setText(onName.get(onName.size() - 1));
	        
	        //Recibimos primera llamada al crear la Actividad
	        Intent intent = getIntent();
	        
		    // Datos de usuario
		    user = intent.getStringExtra("user");
		    pass = intent.getStringExtra("pass");
		    
		    // Recibimos datos HOME
	        fServ = intent.getStringExtra("out");
			doc = Jsoup.parse(fServ);
			
			
	        try {
	        	elements = scrap(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        first.add(asigsToArray(elements, true, comunidades)); // Añadimos el Array con los  nombres de Carpetas, Asignaturas y Archivos al ArrayList - [0]
	        String s[] = urlsToArray(elements, true, comunidades);
	        first.add(s); // Añadimos el Array con las  URLS al ArrayList - [1]
	        int mysize = s.length;
	        first.add(typeToArray(elements, true, comunidades, mysize)); // Añadimos el Array con los TYPES al ArrayList - [2]
	        
	        //Copia de FIRST que puede ser borrada
	        for (Object[] objects: first) {
	        first2.add((Object[])objects.clone());
	        }
	
	        lstDocs = (ListView)findViewById(R.id.LstDocs); // Declaramos la lista
	        lstAdapter = new AdaptadorDocs(this, first2);
	        lstDocs.setAdapter(lstAdapter); // Declaramos nuestra propia clase adaptador como adaptador
	    }
	        
        lstDocs.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) { //Al clicar X item de la lista
            	isInternetPresent = cd.isConnectingToInternet();
            	if(isInternetPresent) {
	            	if(!(first.get(2)[position].toString().equals("0")) && !(first.get(2)[position].toString().equals("1")) && !(first.get(2)[position].toString().equals("6"))) {
	            		docPosition = position;
	            		Log.d("TIPO", "DOCUMENTO");
	            		// ProgressDialog (salta para mostrar el proceso del archivo descargándose)
	            		dialog = new ProgressDialog(mycontext);
	                    
	            		// Servicio para la descarga del archivo
	            		url = first.get(1)[docPosition].toString();
	            		url_back = onUrl.get(onUrl.size() - 2);
	            		new docDownload().execute();

	            	} else {
		            	if(onUrl.get(onUrl.size() - 1) == "/dotlrn/?page_num=2" && !comunidades){
		                	isTheHome = true;
		                } else {
		                	isTheHome = false;
		                }
		            	//Cuando se hace click en una opción de la lista, queremos borrar todo mientras carga, incluído el título header
		            	headerTitle.setText(null);
		            	lstAdapter.clearData();
		            	// Refrescamos View
		            	lstAdapter.notifyDataSetChanged();
		            	lstDocs.setDividerHeight(0);
		            	docPosition = position;
		            	Log.d("URL", onUrl.get(onUrl.size() - 1));
		            	
		            	url = first.get(1)[docPosition].toString();
		            	new urlConnect().execute();
	            	}
	            } else {
	            	Toast.makeText(getBaseContext(),getString(R.string.no_internet), Toast.LENGTH_LONG).show();
	            }
            }
        });
	    
	    if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(DisplayMessageActivity.RESPONSE);
        registerReceiver(dataUpdateReceiver, intentFilter);
        
	}
	
	/**
	 * Creamos nuestro propio adaptador para la lista con la siguiente clase: AdaptadorDocs
	 */
	
	class AdaptadorDocs extends BaseAdapter {
        
	    Activity context;
	    ArrayList<Object[]> data; // 0 = nombres 1 = urls 2 = type

	    AdaptadorDocs(Activity context,  ArrayList<Object[]> data) {
	    	 super();
	         this.context = context;
	         this.data = data;
	    }
	     
	    public int getCount() {
	    	if(data.isEmpty()) return 1;
            return data.get(0).length;
        }
	    
	    public Object getItem(int position) {
            return position;
        }
	    
	    public long getItemId(int position) {
            return position;
        }
	    
	    public void clearData() {
	        // clear the data
	        data.clear();
	    }
	    
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View item;
	    	if(!data.isEmpty()) {
	    	LayoutInflater inflater = context.getLayoutInflater();
	    	
	        item = inflater.inflate(R.layout.list_docs, null);
	        item.setMinimumHeight(65);  
	        item.setPadding(14, 0, 6, 0);
	        TextView title;
	        ImageView image;
	        int ico;
	        // T�tulo
		    title = (TextView)item.findViewById(R.id.list_title);
		    String rgxTitle;
		    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); // Obtenemos las preferencias
		    
		    // Imagen
		    image = (ImageView)item.findViewById(R.id.folderImage);
		    Log.d("SWITCH", "Posición " +position+ " Valor: " +data.get(2)[position].toString()+ " Tama�o total: " +String.valueOf(data.get(2).length-1));
		    switch(Integer.parseInt(data.get(2)[position].toString())) {
		    case 0: // Atr�s
		    	rgxTitle = data.get(0)[position].toString().replaceAll( "\\d{4}-\\d{4}\\s|\\d{4}-\\d{2}\\s|Documentos\\sde\\s?|Gr\\..+?\\s|\\(.+?\\)|Sgr\\..+?\\s", "" );
		    	title.setText(rgxTitle.trim());
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_back", null, null); // Back ico
		    	image.setImageResource(ico);
		    	break;
		    case 1: // Carpeta
		    	rgxTitle = data.get(0)[position].toString().replaceAll( "\\d{4}-\\d{4}\\s|\\d{4}-\\d{2}\\s|Documentos\\sde\\s?|Gr\\..+?\\s|\\(.+?\\)|Sgr\\..+?\\s", "" );
		    	title.setText(rgxTitle.trim());
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/icon_folder", null, null); // Folder ico
		    	image.setImageResource(ico);
		    	break;
		    case 2: // PDF
		    	rgxTitle = data.get(0)[position].toString();
		    	title.setText(rgxTitle.trim());
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_pdf", null, null); // PDF ico
		    	image.setImageResource(ico);
		    	break;	    	
		    case 3: // Excel
		    	rgxTitle = data.get(0)[position].toString();
		    	title.setText(rgxTitle.trim());
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_excel", null, null); // Excel ico
		    	image.setImageResource(ico);
		    	break;
		    case 4: // Power Point
		    	rgxTitle = data.get(0)[position].toString();
		    	title.setText(rgxTitle.trim());
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_ppt", null, null); // PPT ico
		    	image.setImageResource(ico);
		    	break;
		    case 5: // Word
		    	rgxTitle = data.get(0)[position].toString();
		    	title.setText(rgxTitle.trim());
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_word", null, null); // Word ico
		    	image.setImageResource(ico);
		    	break;
		    case 6: // Comunidades y Otros
		    	rgxTitle = data.get(0)[position].toString().replaceAll( "\\d{4}-\\d{4}\\s|\\d{4}-\\d{2}\\s|Documentos\\sde\\s?|Gr\\..+?\\s|\\(.+?\\)|Sgr\\..+?\\s", "" );
		    	title.setText(rgxTitle.trim());
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_comu", null, null); // Word ico
		    	image.setImageResource(ico);
		    	break;
		    default: // Otros
		    	rgxTitle = data.get(0)[position].toString();
		    	title.setText(rgxTitle);
		    	ico = getResources().getIdentifier("com.jp.miaulavirtual:drawable/ic_def", null, null); // Other ico
		    	image.setImageResource(ico);
		    	break;
		    }
		    if(!prefs.getBoolean("pattern", true)) title.setText(data.get(0)[position].toString());
	        
	        // Imagen
	         // 1
	        //int pdf = getResources().getIdentifier("com.jp.miaulavirtual:drawable/icon_pdf", null, null); // 2
	        //int excel = getResources().getIdentifier("com.jp.miaulavirtual:drawable/icon_excel", null, null); // 3
	        
 
	        
	        //switch(Integer.parseInt(data.get(2)[position].toString())) {
	        
	        //}
	 
	        //botoncito desplegable pendiente AQUI
	    	} else {
	    			LayoutInflater inflater = context.getLayoutInflater();
	    	        item = inflater.inflate(R.layout.load_list, null);
	    	        item.setMinimumHeight(35);
	    	}
	        return(item);
	    }
	}
	
	public void afterBroadcaster(String mydoc) { //Método proceso GET
		Boolean isHome;
		
		// Actualizamos nombre de LblSubTitulo;
		TextView headerTitle = (TextView) findViewById(R.id.LblSubTitulo); // Título Header
		headerTitle.setTextColor(getResources().getColor(R.color.list_title));
        headerTitle.setTypeface(null, 1);
        headerTitle.setText(onName.get(onName.size() - 1));
		
		doc = Jsoup.parse(mydoc);
        try {
			elements = scrap(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(onUrl.get(onUrl.size() - 1) == "/dotlrn/?page_num=2" && !comunidades){
        	isHome = true;
        } else {
        	isHome = false;

        }
        first.remove(0);
        first.remove(0);
        first.remove(0);
		first.add(asigsToArray(elements, isHome, comunidades)); //Array con los  nombres de Carpetas, Asignaturas y Archivos
		String s[] = urlsToArray(elements, isHome, comunidades);
		first.add(s);
		int mysize =  s.length;
		first.add(typeToArray(elements, isHome, comunidades, mysize));
		
		//Copia de FIRST que puede ser borrada
        for (Object[] objects: first) {
        first2.add((Object[])objects.clone());
        }
        
        // Re-iniciamos adaptador de lista
        lstAdapter = new AdaptadorDocs(this, first2);
        lstDocs.setDividerHeight(1);
        lstDocs.setAdapter(lstAdapter);	
	}
	
	public void afterBroadcaster2() {
		//Copia de FIRST que puede ser borrada
        for (Object[] objects: first) {
        first2.add((Object[])objects.clone());
        }
		lstDocs = (ListView)findViewById(R.id.LstDocs); // Declaramos la lista
        lstAdapter = new AdaptadorDocs(this, first2);
        lstDocs.setAdapter(lstAdapter); // Declaramos nuestra propia clase adaptador como adaptador
	}
	
    public void onStart()
    {
        super.onStart();
        Log.d(tag, "In the onStart() event");
        
    }
    
    public void onRestart()
    {
        super.onRestart();
        isInternetPresent = cd.isConnectingToInternet();
        Log.d(tag, "In the onRestart() event");
        
        /**
         * Comprobar si tiene conexi�n a Internet.
         * Tiene conexi�n: Actualizar
         * No tiene conexi�n: Mensaje "No existe conexi�n a internet"
         */
    }
    
    public void onResume()
    {
        super.onResume();
        Log.d(tag, "In the onResume() event");
    }
    
    public void onPause()
    {
        super.onPause();
        Log.d(tag, "In the onPause() event");
    }
    
    public void onStop()
    {
        super.onStop();
        Log.d(tag, "In the onStop() event");
    }
    
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(tag, "In the onDestroy() event");
        if(dataUpdateReceiver!=null) unregisterReceiver(dataUpdateReceiver);
        
        //ELIMINAR COOKIE DEL CACHE para que siempre que se inicie la aplicacion haga POST
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = prefs.edit(); //eliminamos la cookie
    	editor.remove("cookies"); //ELIMINAMOS LA COOKIE DE LA PASADA VEZ
    	editor.commit();
    }

	
    /**
     * Acción para cada Item del Menu
     */
    public boolean onOptionsItemSelected(MenuItem item){
        /*El switch se encargar� de gestionar cada elemento del men� dependiendo de su id,
        por eso dijimos antes que ning�n id de los elementos del men� podia ser igual.
        */
        switch(item.getItemId()){
        case R.id.preferencias: //Nombre del id del men�, para combrobar que se ha pulsado
        	Log.d(tag, "Preferencias");
        	startActivity(new Intent(this, SettingsActivity.class));;
        	break;
        case R.id.mysesion:
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Editor editor = prefs.edit();
            editor.remove("myuser");
            editor.remove("mypass");
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            break;
        case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
        }
        return true;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_display_message, menu);
        return true;
	}

    public Elements scrap(Document mdoc) throws IOException {
		Elements elem = mdoc.select("tbody tr[class=odd], tbody tr[class=even], tbody tr[class=even last], tbody tr[class=odd last]"); //Filas de Documentos
	    Log.d("Service", "Passing elements variable");
		return elem;
	}
	
	public String [] asigsToArray(Elements melem, Boolean isHome, Boolean comun) {
		int i = 1;
		String s[];
		Elements elem;
		if(isHome) {
			elem = melem.select("td[headers=contents_name] a, td[headers=folders_name] a").not("[href*=/clubs/]"); //Nombre Asignaturas String !"Comunuidades"
			s = new String[(elem.size())+1]; //todo-comunidades + 1(carpeta comunidades)
			s[0] = "Comunidades y otros";
			for(Element el : elem){
			    s[i] = el.text();
			    i++;
			}
		} else if(comun) {
			elem = melem.select("td[headers=contents_name] a[href*=/clubs/], td[headers=folders_name] a[href*=/clubs/]"); //Nombre Asignaturas String "Comunuidades"
			s = new String[elem.size()+1]; //comunidades + 1(atr�s)
			s[0] = "Atrás " + onName.get(onName.size() - 2);
			for(Element el : elem){
			    s[i] = el.text();
			    i++;
			}
		} else {
			elem = melem.select("td[headers=contents_name] a[href], td[headers=folders_name] a[href]"); //Nombre Asignaturas String 
			s = new String[elem.size()+1]; //todo + 1 (atr�s)
			s[0] = "Atrás " + onName.get(onName.size() - 2);
			for(Element el : elem){
			    s[i] = el.text();
			    i++;
			}
		}
		Log.d("asigseToArray", String.valueOf(elem.size()));
		return s;
	}
	
	public String [] urlsToArray(Elements melem, Boolean isHome, Boolean comun) {
		Elements elem;
		int i = 1;
		String s[];
		if(isHome) {
			elem = melem.select("td[headers=contents_name] a, td[headers=folders_name] a").not("[href*=/clubs/]"); //Nombre Asignaturas String !"Comunuidades"
			s = new String[(elem.size())+1];
			s[0] = "/dotlrn/?page_num=2";
			for(Element el : elem){
			    s[i] = el.select("a").attr("href");
			    i++;
			}
		} else if(comun) {
			elem = melem.select("td[headers=contents_name] a[href*=/clubs/], td[headers=folders_name] a[href*=/clubs/]"); //Nombre Asignaturas String "Comunuidades"
			s = new String[elem.size()+1];
			s[0] = onUrl.get(onUrl.size() - 2);
			for(Element el : elem){
			    s[i] = el.select("a").attr("href");
			    i++;
			}
		} else {
			elem = melem.select("td[headers=contents_name] a[href], td[headers=folders_name] a[href]"); //Nombre Asignaturas String 
			s = new String[elem.size()+1];
			s[0] = onUrl.get(onUrl.size() - 2);
			for(Element el : elem){
			    s[i] = el.select("a").attr("href");
			    i++;
			}
		}
		Log.d("urlsToArray", String.valueOf(s.length));
		return s;
	}
	
	public String [] typeToArray(Elements melem, Boolean isHome, Boolean comun, int size) {
		Elements elem = melem.select("td[headers=folders_type], td[headers=contents_type]"); //Nombre Asignaturas String
		String[] mtypes = {"carpeta", "Carpeta", "PDF", "Microsoft Excel", "Microsoft PowerPoint", "Microsoft Word"};
		String s[];
		int i = 1;
		if(isHome) {
			s = new String[size];
			s[0] = "6";	
			while(i<size) {
				Log.d("get(type)", i+"  Tama�o: " +size);
				s[i] = "1";
				i++;
			}
		} else if(comun) {
			s = new String[size];
			s[0] = "0";
			while(i<size) {
				s[i] = "6";
				i++;
			}
		} else {
			s = new String[elem.size()+1];
			s[0] = "0";
			for(Element el : elem){
				String types = el.text().trim();
				s[i] = "7"; // Defecto al menos que...:
				if(mtypes[0].equals(types.toString()) || mtypes[1].equals(types.toString())) s[i] = "1"; // 1 = Carpeta
				if(mtypes[2].equals(types.toString())) s[i] = "2"; // 2 = PDF 
				if(mtypes[3].equals(types.toString())) s[i] = "3"; // 3 = Excel
				if(mtypes[4].equals(types.toString())) s[i] = "4"; // 4 = Power Point
				if(mtypes[5].equals(types.toString())) s[i] = "5"; // 5 = Word
				Log.d("Types", s[i]);
				i++;
			}
		}
		Log.d("typeToArray", String.valueOf(size));
		return s;
	}
	
	/* TASKS OR SERVICES */
	
	private class docDownload extends AsyncTask<Void, Integer, Void> {
		protected docDownload() {}
		
    	protected Void doInBackground(Void... params) {
        	if(cookies != null) {
        		Log.d("Document", "getDoc AHORA");
        		try {
        			getDoc(mycontext);
            	}catch(SocketTimeoutException e)
            	{
            		startOk3(mycontext, 6, false);
            	}catch(IOException e)
            	{
            		startOk3(mycontext, 5, false);
            	}
        		Log.d("Cookie", "HAY COOKIE!");
        	} else {
        		try {
        			setData();
            	}catch(SocketTimeoutException e)
            	{
            		startOk3(mycontext, 6, false);
            	}catch(IOException e)
            	{
            		startOk3(mycontext, 5, false);
            	}
        	}
            return null;
        }
    	
        protected void onProgressUpdate(Integer... progress) {
            /** Log.d("Scrapping",
                    String.valueOf(progress[0]) + "% scrapped");
            Toast.makeText(getBaseContext(),
                String.valueOf(progress[0]) + "% scrapped",
                Toast.LENGTH_LONG).show(); **/
        }
        protected void onPreExecute() {
        	// ProgressDialog (salta para mostrar el proceso del archivo descarg�ndose)
    		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		dialog.setMessage(getString(R.string.process));
            dialog.setCancelable(true);
            dialog.setMax(100);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel_button),
            		new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog,
            		int whichButton)
            		{
            		task_status = false;
            		}
            		});
	        dialog.show();
        }
    }
    
    private class urlConnect extends AsyncTask<Void, Integer, Response> {
    	protected urlConnect() {}
    	
    	protected Response doInBackground(Void... params) {
        	//Mirar si hay datos en cache, si los hay, cogerlos y hacer el get()
        	if(cookies != null) { //si hay cookie, hacemos el GET
        		Log.d("Cookie", "HAY COOKIE!");
        		try {
            		connectGet();
            	}catch(SocketTimeoutException e)
            	{
            		res = null;
            	}catch(IOException e)
            	{
            		res = null;
            	}
        	} else { //Si no hay cookie, hacemos el POST
        		Log.d("Cookie", "NO HAY COOKIE!");
            	try {
            		setData();
            	}catch(SocketTimeoutException e)
            	{
            		res = null;
            	}catch(IOException e)
            	{
            		res = null;
            	}
        	}
            return res;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Response response) {
            if(res != null) {
	        	if(res.hasCookie("ad_user_login")) { // El usuario y la contrase�a son correctas
	            	Log.d("Cookie", String.valueOf(a));
	            	if(a==2) {
	            		a = 1;
	            		new urlConnect().execute(); //REejecutamos la tarea (GET)
	            	}
	            } else if(res.hasCookie("fs_block_id")) { // No tiene "ad_user_login" pero si "fs_block_id" --> Cookie NO vencida
	            	startOk2(response, mycontext);
	            } else if(res.hasCookie("ad_session_id")) { // Usuario y contrase�a incorrectos. No tiene ni "ad_user_login" ni "fs_block_id"
	            	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
	            	Editor editor = prefs.edit();
		            editor.remove("cookies");
	                editor.commit();
	                cookies = null; //eliminamos la cookie
	                a = a+1; // Aumentamos el contador
	                Log.d("Cookie", "COOKIE VENCIDA");
	                new urlConnect().execute(); //REejecutamos la tarea (POST)

	            } else if(res.hasCookie("tupi_style") || res.hasCookie("zen_style")) { // Cookie correcta, sesi�n POST ya habilitada. GET correcto. Procede.
	            	startOk2(response, mycontext);
	            }
	        } else {
	        	Log.d("Exception", "Timeout2");
	        	startOk3(mycontext, 6, false);
	        }
        }
    }
    
    private void startOk2(Response response, Context context) {
    	String out = "";  
    	Intent bcIntent = new Intent();
        bcIntent.setAction(RESPONSE);
        try {
			out = response.parse().body().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        bcIntent.putExtra("response", out);
        sendBroadcast(bcIntent);
    	}
    
    private void startOk3(Context context, int id, Boolean status) {
    	Intent bcIntent = new Intent();
        bcIntent.setAction(RESPONSE);
        if(status) bcIntent.putExtra("response", "cont");
        if(!status) bcIntent.putExtra("response", "oops");
        bcIntent.putExtra("id", id);
        Log.d("StartOk3", String.valueOf(id));
        sendBroadcast(bcIntent);
    	}
    
    public void setData() throws IOException, SocketTimeoutException {
    	
    	Document av = Jsoup.connect("https://aulavirtual.uv.es/dotlrn/index").timeout(10*1000).get();
	    	
		Elements inputs = av.select("input[name=__confirmed_p], input[name=__refreshing_p], input[name=form:id], input[name=form:mode], input[name=formbutton:ok], input[name=hash], input[name=time], input[name=token_id]");
			
		Response resp = Jsoup.connect("https://aulavirtual.uv.es/register/")
			    .data("__confirmed_p", inputs.get(2).attr("value"), "__refreshing_p", inputs.get(3).attr("value"), "form:id", inputs.get(1).attr("value"), "form:mode", inputs.get(0).attr("value"), "formbutton:ok", inputs.get(7).attr("value"), "hash", inputs.get(6).attr("value"), "time", inputs.get(4).attr("value"), "return_url", url, "token_id", inputs.get(5).attr("value"), "username", user, "password", pass)
			    .method(Method.POST)
			    .timeout(10*1000)
			    .execute();
		res = resp; //IMPORTANTE verificará si el usuario ha logueado o por el contrario ha dado una contraseña o usuario incorrecto(s)
		cookies = resp.cookies();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
        Editor editor = prefs.edit();
        editor.putString("cookies", cookies.toString());
        editor.commit();
        Log.d("Connect", "Conectando sin cookies");

    }
	
	public void connectGet() throws IOException, SocketTimeoutException {
		Response resp = Jsoup.connect("https://aulavirtual.uv.es"+ url).cookies(cookies).method(Method.GET).timeout(10*1000).execute();
		res = resp;
		Log.d("Connect", "Conectando con cookies");
		Log.d("URL", "https://aulavirtual.uv.es"+url);
		Log.d("COOKIE GET", resp.cookies().toString());
	}
	
	/**
	 * Codifica la URL del archivo, lo descarga (si no existe) y lo guarda en Almacenamiento externo (SDCARD)//Android/data/com.jp.miaulavirtual/files
	 * @return String - Tama�o del archivo en formato del SI
	 * @throws IOException
	 */
    public void getDoc(Context mycontext) throws IOException, SocketTimeoutException {
    	URI uri;
    	Log.d("Document", url);
    	String request = null;
		// Codificamos la URL del archivo
    	try {
			uri = new URI(
				    "https", 
				    "aulavirtual.uv.es", 
				    url,
				    null);
			request = uri.toASCIIString();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.d("Document", request);
    	Log.d("Document", url_back);
    	
    	// Recheck cookie isn't expire
    	Response resp = Jsoup.connect("https://aulavirtual.uv.es"+ url_back).cookies(cookies).method(Method.GET).timeout(10*1000).execute();
    	res = resp;
    	Log.d("Document", "Respuesta2");
    	
    	// Action in response of cookie checking
    	if(res.hasCookie("ad_user_login")) { // El usuario y la contraseña son correctas al renovar la COOKIE (NO PUEDEN SER INCORRECTOS, YA ESTABA LOGUEADO)
        	Log.d("Cookie", String.valueOf(a));
        	if(a==2) {
        		a = 1;
        		new docDownload().execute(); //REejecutamos la tarea docDownload
        	}
    	} else if(res.hasCookie("fs_block_id")) {
    		downloadFile(request);  
        } else if(res.hasCookie("ad_session_id")) { // Cookie Vencida
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
        	Editor editor = prefs.edit();
        	// Cookie Vencida
            editor.remove("cookies");
            editor.commit();
            cookies = null; // eliminamos la cookie
            a = a+1; // Aumentamos el contador
            Log.d("Cookie", "COOKIE VENCIDA");
            new docDownload().execute(); //REejecutamos la tarea (POST)
        } else if(res.hasCookie("tupi_style") || res.hasCookie("zen_style")) { // Cookie correcta, sesi�n POST ya habilitada. GET correcto. Procede.
        	downloadFile(request);
        }
    }
    
    public void downloadFile(String request) {
    	URL url2;
	    URLConnection conn;
	    int lastSlash;
	    Long fileSize = null;
	    BufferedInputStream inStream;
	    BufferedOutputStream outStream;
	    FileOutputStream fileStream;
	    String cookies = cookieFormat(scookie); // format cookie for URL setRequestProperty
	    final int BUFFER_SIZE = 23 * 1024;
	    int id = 1;
	    
	    File file = null;
		
		Log.d("Document", "2ª respueesta");
	    try
	    {
    		// Just resources
    		lastSlash = url.toString().lastIndexOf('/');
    		
    		// Directory creation
    		String root = Environment.getExternalStorageDirectory().toString();
    	    File myDir = new File(root + "/Android/data/com.jp.miaulavirtual/files");    
    	    myDir.mkdirs();
    	    
    	    // Document creation
    	    String name = url.toString().substring(lastSlash + 1);
    	    file = new File (myDir, name);
    	    fileSize = (long) file.length();
    	    
    	    // Check if we have already downloaded the whole file
    	    if (file.exists ()) {
    	    	dialog.setProgress(100); // full progress if file already donwloaded
    	    } else {
		        // Start the connection with COOKIES (we already verified that the cookies aren't expired and we can use them)
	    	    url2 = new URL(request);
		        conn = url2.openConnection();
		        conn.setUseCaches(false);
		        conn.setRequestProperty("Cookie", cookies);
		        conn.setConnectTimeout(10*1000);
		        conn.setReadTimeout(20*1000);
		        fileSize = (long) conn.getContentLength();
		        
		        // Check if we have necesary space
		        if(fileSize >= myDir.getUsableSpace()) { task_status = false; id = 2;
		        } else {
		        	
			        // Start downloading
			        inStream = new BufferedInputStream(conn.getInputStream());
			        fileStream = new FileOutputStream(file);
			        outStream = new BufferedOutputStream(fileStream, BUFFER_SIZE);
			        byte[] data = new byte[BUFFER_SIZE];
			        int bytesRead = 0;
			        int setMax = (conn.getContentLength()/1024);
			        dialog.setMax(setMax);
		
			        while(task_status && (bytesRead = inStream.read(data, 0, data.length)) >= 0)
			        {
			            outStream.write(data, 0, bytesRead);
			            // update progress bar
			            dialog.incrementProgressBy((int)(bytesRead/1024));
			        }
			        
			        // Close stream
			        outStream.close();
			        fileStream.close();
			        inStream.close();
			        
			        // Delete file if Cancel button
			        if(!task_status) file.delete(); id=0;
			        
			    }
		    }
    	    // Open file
	        if(task_status) {
	        	Log.d("Type", "Hola2");
	        	dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
              
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String ext=file.getName().substring(file.getName().indexOf(".")+1);
                String type = mime.getMimeTypeFromExtension(ext);
                Log.d("Type", type);
             
                intent.setDataAndType(Uri.fromFile(file),type);
                startActivity(intent);
	        }
	    }
	    catch(MalformedURLException e) // Invalid URL
	    {
	    	task_status = false;
	    	if(file.exists ()) { file.delete(); }
	    	id = 3;
	    }
	    catch(FileNotFoundException e) // FIle not found
	    {
	    	task_status = false;
	    	if(file.exists ()) { file.delete(); }
	    	id = 4;
	    }
	    catch(SocketTimeoutException e) // time out
	    {
	    	task_status = false;
	    	if(file.exists ()) { file.delete(); }
	    	id = 7;
	    }
	    catch(Exception e) // General error
	    {
	    	task_status = false;
	    	if(file.exists ()) { file.delete(); }
	    	id = 8;
	    }
	    Log.d("StartOk3", "¿Como he llegado hasta aquí?");
	    // notify completion
	    startOk3(mycontext, id, task_status);

    }	
	
    /**
     * Pasa cookies en formato String {cookies} a Map que es el formato utilizado poara insertar cookies por JSoup
     * @param scookie
     * @return Map<String, String>
     */
	public Map<String, String> toMap(String scookie) {
		Map<String, String> cookies = new LinkedHashMap<String, String>();
		String[] split = scookie.split(" *[=,^{}$] *");
		int i = 1;
		while(i<split.length-1) {
			//System.out.println(i);
			cookies.put(split[i], split[i+1]);
			//i++;
			i=i+2;
		}
		return cookies;
	}
	
	public String cookieFormat(String scookie) {
		String [] arrayc = scookie.split(" *[=,^{}$] *");
		String cookies ="";
		for(int a = 1; a<(int)arrayc.length; a++) {
			cookies += arrayc[a]+"="+arrayc[a+1]+"; ";
			a = a+1;
		}
		cookies = cookies.trim();
		return cookies;
	}
	
	/**
	 * Pasa Bytes a formate legible del SI (si = true) o del Sistema Binario
	 * @param bytes
	 * @param si
	 * @return String
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
