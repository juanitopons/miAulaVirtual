package com.jp.miaulavirtual;

import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class MainActivity extends Activity {
	CurlService cURL;
	Intent i;
	
	String user;
	String pass;
	String url;
	
	String tag = "Lifecycle";
	private DataUpdateReceiver dataUpdateReceiver;
	
	//BroadcastReceiver, recibe variables de nuestro servicio posteriormente ejecutado CurlService
	private class DataUpdateReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(CurlService.LOGGED)) {
	        	boolean logged = intent.getBooleanExtra("logged", true);
	        	if(!logged) { 
	        		setContentView(R.layout.activity_main); 
	        		Toast.makeText(getBaseContext(),"Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
	        		stopService(i);
	        		unbindService(CurlConnection);
	        	} else {
	        		stopService(i);
	        		unbindService(CurlConnection);
	        		finish();
	        	}
	        }
	    }
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the user interface layout for this Activity
        // The layout file is defined in the project res/layout/main_activity.xml file
        PreferenceManager.setDefaultValues(this, R.xml.apppreferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        Editor editor = prefs.edit(); //eliminamos la cookie
    	editor.remove("cookies"); //ELIMINAMOS LA COOKIE DE LA PASADA VEZ
    	editor.commit();
        
        user = prefs.getString("myuser", "Ninguno");
        pass = prefs.getString("mypass", "Ninguno");
        url = "/dotlrn/?page_num=2";
       
        if(user!="Ninguno" && user!=""){ //Si el usuario entra a la aplicación y ya está logueado
        	setContentView(R.layout.activity_main2); //Content si está logueado - Loader mientras espera el scrap
        	
        	Log.d("DATOS", user);
            Log.d("DATOS", pass);
        	i = new Intent(this, CurlService.class);
            bindService(i, CurlConnection, Context.BIND_AUTO_CREATE); //conectamos el servicio

        	/** String [] user_data;
        	user_data = new String[2];
        	user_data[0] = user;
        	user_data[1] = pass;
        	
        	intent.putExtra(USER_DATA, user_data); //message = qué mostrar en la nueva actividad al pulsar el boton de "Login" **/
        	
        } else {
        setContentView(R.layout.activity_main); //Content si no está logueado
        }
        Log.d(tag, "In the onCreate() event");
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(CurlService.LOGGED);
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /**
     * Acción para cada Item del Menu
     */
    public boolean onOptionsItemSelected(MenuItem item){
        /*El switch se encargará de gestionar cada elemento del menú dependiendo de su id,
        por eso dijimos antes que ningún id de los elementos del menú podia ser igual.
        */
        switch(item.getItemId()){
        case R.id.preferencias: //Nombre del id del menú, para combrobar que se ha pulsado
        	Log.d(tag, "Preferencias");
        	startActivity(new Intent(this, SettingsActivity.class));;
        }
        return true;
    }
    
    
    public void onStart()
    {
        super.onStart();
        Log.d(tag, "In the onStart() event");
    }
    
    public void onRestart()
    {
        super.onRestart();
        Log.d(tag, "In the onRestart() event"); 
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
    }
    
    /** Llamada cuando el usuario hace clic en 'Enviar' */
    public void sendMessage(View view) {
    	/**
         * Habrá que guardar el Usuario y Contraseña si es la primera vez que loguea (es la primera vez que loguea porque se envia el SEND.
         * 
         */
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = prefs.edit();
        EditText tuser = (EditText) findViewById(R.id.user);
    	EditText tpass = (EditText) findViewById(R.id.pass);
    	//Incertamos el usuario y la contraseña en las preferencias
        editor.putString("myuser", tuser.getText().toString());
        editor.putString("mypass", tpass.getText().toString());
        editor.commit();
    	
        //El usuario ha logueado, ponemos la pantalla de carga
        setContentView(R.layout.activity_main2);
        
        //Actualizamos el usuario y contraseña
        user = tuser.getText().toString();
        pass = tpass.getText().toString();
        url = "/dotlrn/?page_num=2";
        
        Log.d("DATOS2", user);
        Log.d("DATOS2", pass);
        
        //Iniciamos el servicio de Scrap
        i = new Intent(this, CurlService.class);
        bindService(i, CurlConnection, Context.BIND_AUTO_CREATE); //conectamos el servicio
        
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(CurlService.LOGGED);
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    ServiceConnection CurlConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	cURL = ((CurlService.CurlBinder)service).getService();
        	// Pasamos variables al Servicio
        	cURL.user = user;
        	cURL.pass = pass;
        	cURL.url = url;
        	cURL.isDocument = false;
            startService(i);
            Log.d("Service", "Servicio Conectado");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	cURL = null;
        	Log.d("Service", "Servicio Desconectado");
        }
    };
    
}
