package com.example.facerecognition;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Pin_Config extends Activity {
EditText currentp,newp,confirmp;
String current,nw,confirm;
Button change;
SharedPreferences preferences;
Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin__config);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        change=(Button) findViewById(R.id.button1);
        currentp=(EditText) findViewById(R.id.editText1);
        newp=(EditText) findViewById(R.id.editText2);
        confirmp=(EditText) findViewById(R.id.editText3);
        preferences =getSharedPreferences("log", 0);
		editor=preferences.edit();
        
        change.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				current=currentp.getText().toString();
				nw=newp.getText().toString();
				confirm=confirmp.getText().toString();
				String s=(preferences.getString("passkey", "helloavi")).toString();
				
				if(current.equals(s))
				{
					if(nw.equals(confirm))
					{
						editor.putString("passkey",confirmp.getText().toString());
						editor.commit();
						Toast.makeText(getApplicationContext(),"Great..!!Password Changed", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(Pin_Config.this,PIN_MODE.class);
						startActivity(intent);
					}
					else
					{
						Toast.makeText(getApplicationContext(),"oops..!!Password is not matched.", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(),"oops..!!Current Password is Incorrect.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pin__config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		onBackPressed();
		return true;
	}
}
