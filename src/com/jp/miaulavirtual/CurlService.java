package com.jp.miaulavirtual;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Connection.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;


import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class CurlService extends Service {
	//Variable a través del ServiceConnection de la Activity
	
	//Variables POST
	String user, pass;
	String confirm;
	String refresh;
	String fm_id;
	String fm_mode;
	String fm_button;
	String hash;
	String time;
	String token_id;
	String url;
	public final static String LOGGED = "com.jp.miaulavirtual.LOGGED";
	public final static String RESPONSE = "com.jp.miaulavirtual.RESPONSE";
	
	Boolean isDocument; // Queremos saber si la URL es un Documento (tarea docDownload) o no (tarea urlConnect)
	
	//Respuesta
	Response res;
	Context mycontext;
    
	int i = 0; //Control nº de tareas por servicio 1 = GET || POST 2 = GET cookie vencida, POST, GET.
	String scookie;
	Map<String, String> cookies; //Obtener la cookie del cache
	
	
	public final static String EXTRA_MESSAGE = "com.jp.miaulavirtual.MESSAGE";

    // Binder given to clients
    private final IBinder mBinder = new CurlBinder();
    
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class CurlBinder extends Binder {
    	CurlService getService() {
            // Return this instance of CurlService so clients can call public methods
            return CurlService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    /** method for clients */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //Cuando el Servicio se inicia
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    Toast.makeText(this, "Cargando", Toast.LENGTH_SHORT).show();
	    i = i+1;
	    if(isDocument) {
	    	new docDownload(this).execute();
	    } else {
	    	new urlConnect(this).execute();
	    }
	    return START_STICKY;
    }
    
    private class docDownload extends AsyncTask<Void, Integer, String> {
    	docDownload(Context context) {
    		mycontext = context;
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
    	    scookie = prefs.getString("cookies", ""); //¿Existe cookie?
    	    if(scookie!="") {
    	    	Log.d("Cookie", "Cookie string:" + scookie);
    	    	cookies = toMap(scookie); //Si existe cookie la pasamos a MAP para luego procesar el GET con la cookie
    	    }
    		// ProgressDialog (salta para mostrar el proceso del archivo descargándose)
    	}
    	protected String doInBackground(Void... params) {
    		String fileSize = null;
    		try {
            	if(cookies != null) {
            		Log.d("Document", "getDoc AHORA");
            		fileSize = getDoc();
            		Log.d("Cookie", "HAY COOKIE!");
            	} else {
            		setData();
            		connect();
            	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            publishProgress(100);
            return fileSize;
        }
    	
        protected void onProgressUpdate(Integer... progress) {
            /** Log.d("Scrapping",
                    String.valueOf(progress[0]) + "% scrapped");
            Toast.makeText(getBaseContext(),
                String.valueOf(progress[0]) + "% scrapped",
                Toast.LENGTH_LONG).show(); **/
        }
        protected void onPostExecute(String size) {
        	if(res.cookies().toString().equals("{}")) {
        		startOk3(mycontext, size);
        	} else if(res.hasCookie("ad_user_login")) { // El usuario y la contraseña son correctas al renovar la COOKIE (NO PUEDEN SER INCORRECTOS, YA ESTABA LOGUEADO)
            	Log.d("Cookie", String.valueOf(i));
            	if(i==2) new docDownload(mycontext).execute(); //REejecutamos la tarea docDownload
            } else if(res.hasCookie("ad_session_id")) { // Cookie Vencida
            	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
            	Editor editor = prefs.edit();
            	// Cookie Vencida
                editor.remove("cookies");
                editor.commit();
                cookies = null; // eliminamos la cookie
                i = i+1; // Aumentamos el contador
                Log.d("Cookie", "COOKIE VENCIDA");
                new docDownload(mycontext).execute(); //REejecutamos la tarea (POST)
            }  
        }
    }

    
    private class urlConnect extends AsyncTask<Void, Integer, Response> {
    	Boolean isInside;
    	urlConnect(Context context) {
    		mycontext = context;
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
    	    scookie = prefs.getString("cookies", ""); //¿Existe cookie?
    	    if(scookie!="") {
    	    	Log.d("Cookie", "Cookie string:" + scookie);
    	    	cookies = toMap(scookie); //Si existe cookie la pasamos a MAP para luego procesar el GET con la cookie
    	    }
    	}
    	
    	protected Response doInBackground(Void... params) {
            try {
				
            	//Mirar si hay datos en cache, si los hay, cogerlos y hacer el get()
            	if(cookies != null) { //si hay cookie, hacemos el GET
            		isInside = true;
            		Log.d("Cookie", "HAY COOKIE!");
            		connectGet();
            	} else { //Si no hay cookie, hacemos el POST
            		isInside = false;
            		Log.d("Cookie", "NO HAY COOKIE!");
	            	setData();
					connect();
            	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            publishProgress(100);
            return res;
        }

        protected void onProgressUpdate(Integer... progress) {
            /** Log.d("Scrapping",
                    String.valueOf(progress[0]) + "% scrapped");
            Toast.makeText(getBaseContext(),
                String.valueOf(progress[0]) + "% scrapped",
                Toast.LENGTH_LONG).show(); **/
        }

        protected void onPostExecute(Response response) {
            if(res.hasCookie("ad_user_login")) { // El usuario y la contraseña son correctas
            	Log.d("Cookie", String.valueOf(i));
            	if(i==2) {
            		new urlConnect(mycontext).execute(); //REejecutamos la tarea (GET)
            	} else {
	            	Intent bcIntent = new Intent();
	                bcIntent.setAction(LOGGED);
	                bcIntent.putExtra("logged", true);
	                sendBroadcast(bcIntent);
	            	startOk(response, mycontext);
            	}
            } else if(res.hasCookie("fs_block_id")) { // No tiene "ad_user_login" pero si "fs_block_id" --> Cookie NO vencida
            	startOk2(response, mycontext);
            } else if(res.hasCookie("ad_session_id")) { // Usuario y contraseña incorrectos. No tiene ni "ad_user_login" ni "fs_block_id"
            	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
            	Editor editor = prefs.edit();
            	if(!isInside) {	// Usuario y contraseña incorrectos en POST
	            		editor.remove("cookies");
	                	editor.commit();
	            		Intent bcIntent = new Intent();
		                bcIntent.setAction(LOGGED);
		                bcIntent.putExtra("logged", false);
		                sendBroadcast(bcIntent);
            	} else { // Cookie Vencida
	                	editor.remove("cookies");
	                	editor.commit();
	                	cookies = null; //eliminamos la cookie
	                	i = i+1; // Aumentamos el contador
	                	Log.d("Cookie", "COOKIE VENCIDA");
	                	new urlConnect(mycontext).execute(); //REejecutamos la tarea (POST)
            	}
            } else if(res.hasCookie("tupi_style")) { // Cookie correcta, sesión POST ya habilitada. GET correcto. Procede.
            	startOk2(response, mycontext);
            }
            
        }
    }
    private void startOk(Response response, Context context) {
    	String out = "";  
    	Intent i = new Intent(this, DisplayMessageActivity.class);
    	  try {
			out = response.parse().body().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	  i.putExtra("out", out);
    	  i.putExtra("user", user);
    	  i.putExtra("pass", pass);
    	  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	  startActivity(i);
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
    
    private void startOk3(Context context, String size) {
    	Intent bcIntent = new Intent();
        bcIntent.setAction(RESPONSE);
        bcIntent.putExtra("size", size);
        bcIntent.putExtra("response", "continue");
        sendBroadcast(bcIntent);
    	}
    
    public void setData() throws IOException {
    	Document av = Jsoup.connect("https://aulavirtual.uv.es/dotlrn/index").timeout(0).get();
    	
		Elements inputs = av.select("input[name=__confirmed_p], input[name=__refreshing_p], input[name=form:id], input[name=form:mode], input[name=formbutton:ok], input[name=hash], input[name=time], input[name=token_id]");
		
		confirm = inputs.get(2).attr("value");
		refresh = inputs.get(3).attr("value");
		fm_id = inputs.get(1).attr("value");
		fm_mode = inputs.get(0).attr("value");
		fm_button = inputs.get(7).attr("value");
		hash = inputs.get(6).attr("value");
		time = inputs.get(4).attr("value");
		token_id = inputs.get(5).attr("value");
		//Fin Variables POST
		Log.d("Service", hash);
    }
    
	public void connect() throws IOException {
		/**
		 * Chequear si se está logueado (verificar sesión)
		 * Si NO se esta setData();
		 * Si se esta...continuar
		 */
		Response resp = Jsoup.connect("https://aulavirtual.uv.es/register/")
			    .data("__confirmed_p", confirm, "__refreshing_p", refresh, "form:id", fm_id, "form:mode", fm_mode, "formbutton:ok", fm_button, "hash", hash, "time", time, "return_url", url, "token_id", token_id, "username", user, "password", pass)
			    .method(Method.POST)
			    .execute();
		res = resp; //IMPORTANTE verificará si el usuario ha logueado o por el contrario ha dado una contraseña o usuario incorrecto(s)
		cookies = resp.cookies();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mycontext);
        Editor editor = prefs.edit();
        editor.putString("cookies", cookies.toString());
        editor.commit();
        Log.d("Connect", "Conectando sin cookies");
	}
	
	public void connectGet() throws IOException {
		Response resp = Jsoup.connect("https://aulavirtual.uv.es"+ url).cookies(cookies).method(Method.GET).execute();
		res = resp; //Nombre Asignaturas String
		Log.d("Connect", "Conectando con cookies");
		Log.d("URL", "https://aulavirtual.uv.es"+url);
		Log.d("COOKIE GET", resp.cookies().toString());
	}
	
	/**
	 * Codifica la URL del archivo, lo descarga (si no existe) y lo guarda en Almacenamiento externo (SDCARD)//Android/data/com.jp.miaulavirtual/files
	 * @return String - Tamaño del archivo en formato del SI
	 * @throws IOException
	 */
    public String getDoc() throws IOException {
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
    	Response resp = Jsoup.connect(request).cookies(cookies).method(Method.GET).ignoreContentType(true).execute();
    	Log.d("Document", resp.statusMessage());
    	res = resp;
		
		// Guardamos nuestro documento
		int lastSlash = url.toString().lastIndexOf('/');
		
		// Se crea el directorio
		String root = Environment.getExternalStorageDirectory().toString();
	    File myDir = new File(root + "/Android/data/com.jp.miaulavirtual/files");    
	    myDir.mkdirs();
	    
	    // Se crea el Documento
	    String name = url.toString().substring(lastSlash + 1);
	    File file = new File (myDir, name);
		Long fileSize = Long.parseLong(resp.headers().get("Content-Length")); // tamaño del archivo
		
		// Descargamos el archivo si no existe
		if (!file.exists ()) { 
			try {
				Log.d("Document", "descargando...");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(resp.bodyAsBytes());
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return humanReadableByteCount(fileSize, true);
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
