package com.jp.miaulavirtual;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {
	private static final String[] EMAIL = {"juanpons1459@gmail.com"};
	private static final String WEB = "http://es.linkedin.com/in/juanitopons/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		String version = null;
		PackageManager pm = getPackageManager();
		try {
			//---get the package info---
			PackageInfo pi = pm.getPackageInfo("com.jp.miaulavirtual", 0);
			//---display the versioncode---
			version = Integer.toString(pi.versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String text = "miAulaVirtual v."+version
				+"\nDesarrollador: Juan Pons - @juanitopons\n\n"
				+"Si tienes algún problema o sugerencia, por favor"
				+" envía un email al desarrollador o contacta a través "
				+"de su página de Linkedin.\n\nGracias";

				TextView label = (TextView)findViewById(R.id.acercade);
				label.setText(text);
	}
	
	public void action(View view) {
	int id = view.getId();
	Intent i = new Intent();
	
		switch(id) {
			case R.id.help_email_bt:
				i.setAction(android.content.Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, EMAIL);
				try {
					startActivity(Intent.createChooser(i, "Email using..."));
				
				} catch(ActivityNotFoundException e) {
					Toast.makeText(this, "Lo siento, no es posible iniciar la aplicación de email.", Toast.LENGTH_SHORT).show();
				}
			break;
			
			case R.id.help_website_bt:
				i.setAction(android.content.Intent.ACTION_VIEW);
				i.setData(Uri.parse(WEB));
				try {
					startActivity(i);
				
				} catch(ActivityNotFoundException e) {
					Toast.makeText(this, "Lo siento, no es posible abrir la web solicitada.", Toast.LENGTH_SHORT).show();
				}
			break;
		}
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
        	startActivity(new Intent(this, SettingsActivity.class));;
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
        }
        return true;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_about, menu);
        return true;
	}

}