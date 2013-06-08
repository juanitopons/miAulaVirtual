package com.jp.miaulavirtual;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskManager extends Activity {
	// flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    
	//Respuesta
	private String url;
	private Response res;
    
	private int a = 0; //Control nº de tareas 1 = GET || POST 2 = GET cookie vencida, POST, GET.
	private String scookie;
	private Map<String, String> cookies; //Obtener la cookie del cache
	
	// Activity
	private Activity mycontext;
	
	// User data
	private String user;
	private String pass;
	private String panel; // control de panel number of Documents section
	private String period;
	
	// Valores del HOME de documentos
	String [][] names;
	String [] names2;
	
	// Interface
	private ListView lstDocs3;
	private TaskManagerAdapter lstAdapter3;
	private TextView headerTitle3;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager); //Content si está logueado - Loader mientras espera el scrap
		
		// creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        
        // general context
        mycontext = this;
        
        // Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
        // panel
    	panel = prefs.getString("task_panel", "1");
    	period = prefs.getString("task_period", "25");
    	
    	// cookies
        scookie = prefs.getString("cookies", ""); //�Existe cookie?
	    if(scookie!="") {
	    	cookies = toMap(scookie); //Si existe cookie la pasamos a MAP
	    }
	    
	    // Actualizamos nombre de LblSubTitulo
        headerTitle3 = (TextView) findViewById(R.id.LblSubTitulo3); // Título Header
        headerTitle3.setTextColor(getResources().getColor(R.color.list_title));
        headerTitle3.setTypeface(null, 1);
        headerTitle3.setText("Tareas");
        
        // Getting user data
        user = prefs.getString("myuser", "Ninguno");
        pass = prefs.getString("mypass", "Ninguno");
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
        url = "/dotlrn/?date="+dateFormat.format(date)+"&view=list&page_num="+panel+"&period_days="+period+"#calendar";
        
        // Re-iniciamos adaptador de lista
        lstDocs3 = (ListView)findViewById(R.id.LstDocs3); // Declaramos la lista
        lstAdapter3 = new TaskManagerAdapter(mycontext, names2);
        lstDocs3.setDividerHeight(1);
        lstDocs3.setAdapter(lstAdapter3);
        
        isInternetPresent = cd.isConnectingToInternet();
        try {
	        if(isInternetPresent) {
	        	new urlConnect().execute();
	        } else {
	        	process();
	        }
        } catch(ArrayIndexOutOfBoundsException e) {
	        TextView tv = (TextView) findViewById(R.id.panel_error);
	        tv.setVisibility(0);
	        setRestrictedOrientation();
	        Toast.makeText(getBaseContext(),getString(R.string.panel_error2), Toast.LENGTH_LONG).show();
        } catch(IndexOutOfBoundsException e) {
	        TextView tv = (TextView) findViewById(R.id.panel_error);
	        tv.setVisibility(0);
	        setRestrictedOrientation();
	        Toast.makeText(getBaseContext(),getString(R.string.panel_error2), Toast.LENGTH_LONG).show();
		} catch(IllegalArgumentException e) {
	        TextView tv = (TextView) findViewById(R.id.panel_error);
	        tv.setVisibility(0);
	        setRestrictedOrientation();
	        Toast.makeText(getBaseContext(),getString(R.string.panel_error2), Toast.LENGTH_LONG).show();
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
	            		Log.d("Cookie", "Recargada Cookie, hacemos peticion GET");
	            		new urlConnect().execute(); //REejecutamos la tarea (GET)
	            	}
	            } else if(res.hasCookie("fs_block_id")) { // No tiene "ad_user_login" pero si "fs_block_id" --> Cookie NO vencida
	            	Log.d("Cookie", "No vencida: GET hecho");
	            	String out = "";  
	                try {
	        			out = response.parse().body().toString();
	        		} catch (IOException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	                afterBroadcaster(out);
	            } else if(res.hasCookie("ad_session_id")) { // Usuario y contrase�a incorrectos. No tiene ni "ad_user_login" ni "fs_block_id"
	            	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
	            	Editor editor = prefs.edit();
		            editor.remove("cookies");
	                editor.commit();
	                cookies = null; //eliminamos la cookie
	                a = 2; // Aumentamos el contador
	                Log.d("Cookie", "COOKIE VENCIDA");
	                new urlConnect().execute(); //REejecutamos la tarea (POST)

	            } else if(res.hasCookie("tupi_style") || res.hasCookie("zen_style")) { // Cookie correcta, sesi�n POST ya habilitada. GET correcto. Procede.
	            	String out = "";  
	                try {
	        			out = response.parse().body().toString();
	        		} catch (IOException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	                afterBroadcaster(out);
	            }
	        } else {
	        	Log.d("Exception", "Timeout2");
	        	process();
	        }
        }
    }
   
   	public void process() {
   		Toast.makeText(getBaseContext(), getString(R.string.toast_6), Toast.LENGTH_SHORT).show();
   		lstAdapter3.setStatus(false, 0);
   		lstAdapter3.notifyDataSetChanged();
   		lstDocs3.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) { //Al clicar X item de la lista
            	reload();
            }
   		});
   	}
   	
   	public void reload() {
   		isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
        	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    		Date date = new Date();
            url = "/dotlrn/?date="+dateFormat.format(date)+"&view=list&page_num="+panel+"&period_days="+period+"#calendar";
        	lstAdapter3.setStatus(true);
        	lstAdapter3.setNamesToNull();
       		lstAdapter3.notifyDataSetChanged();
        	new urlConnect().execute();
        } else {
        	process();
        }
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
	
    public Elements scrap(Document mdoc) throws IOException {
		Elements elem = mdoc.select("td table[class=cal-table-display]"); //Filas de Documentos
		return elem;
	}
    
    public void namesToArray(Elements melem) throws IndexOutOfBoundsException {
    	Elements elem1, elem2;
    	int i = 0;
    	elem1 = melem.select("div[class=list-entry-item calendar-ItemListContainer] table[class=cal-table-list] tbody tr td[class=calendar-ItemListName]").not("strong"); //Nombre Asignaturas String !"Comunuidades"
    	elem2 = melem.select("div[class=list-entry-item calendar-ItemListContainer] thead tr td").not("strong");
    	
    	String[] days = new String[elem2.size()];
    	for(Element el : elem2){
		    days[i] = el.text();
		    i++;
		}
    	
    	
    	Set<String> mySet = new TreeSet<String>(Arrays.asList(days));
    	names = new String[days.length+1][5]; //todo-comunidades + 1(carpeta comunidades)
		
		int a = 0;
		int b;
		for(String day : mySet){
			names[a][0] = day;
			Log.d("DIAS", names[a][0]);
			b = 0;
			i = 0;
			for(Element el : elem1){
				if(elem2.get(i).text().equalsIgnoreCase(day)) {
					b = b+1;
					names[a][b] = el.text();
					Log.d("NOMBRES", names[a][b]);
				}
			    i++;
			}
			a++;
		}
		Log.d("Diaaaaaaa", names[0][0]);
    }
	
	public void afterBroadcaster(String mydoc) { //Método proceso GET
		Document doc = Jsoup.parse(mydoc);
		Elements elements = null;
		try {
        	elements = scrap(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		namesToArray(elements);
        
        // Re-iniciamos adaptador de lista
    	Log.d("SIZE", String.valueOf(elements.size()));
		if(names.length > 0) {

	        lstAdapter3.notifyDataSetChanged();
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		} else {
			if(elements.size()>0) {
				lstAdapter3.setStatus(false, 2);
		        lstAdapter3.notifyDataSetChanged();
			} else {
				lstAdapter3.setStatus(false, 1);
		        lstAdapter3.notifyDataSetChanged();
			}
		}
	}

    public void onRestart()
    {
        super.onRestart();

        String p2 = panel;
        String period2 = period;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
        // panel; We reload the app if panel change
    	period = prefs.getString("task_period", "25");
        panel = prefs.getString("task_panel", "1");
    	if(!p2.equalsIgnoreCase(panel) || !period2.equalsIgnoreCase(period)) {
    		reload();
    	}
    	Log.d("Lifecycle8", "In onRestart()");
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
        	startActivity(new Intent(this, SettingsActivity.class));
        	break;
        case R.id.acercade: //Nombre del id del men�, para combrobar que se ha pulsado
        	startActivity(new Intent(this, AboutActivity.class));
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
	
    /**
     * Pasa cookies en formato String {cookies} a Map que es el formato utilizado poara insertar cookies por JSoup
     * @param scookie la cookie que queremos pasar a Map
     * @return Map<String, String> cookie
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
	
	@SuppressWarnings("deprecation")
	public void setRestrictedOrientation() {
		/* We don't want change screen orientation */
	    //---get the current display info---
	    WindowManager wm = getWindowManager();
	    Display d = wm.getDefaultDisplay();
	    if (d.getWidth() > d.getHeight()) {
	    	//---change to landscape mode---
	        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	    } else {
	    	//---change to portrait mode---
	        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
	    }
	}

}
