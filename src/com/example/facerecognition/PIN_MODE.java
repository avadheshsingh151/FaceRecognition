package com.example.facerecognition;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PIN_MODE extends Activity {
Button ok;
EditText password;
SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin__mode);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        preferences=getSharedPreferences("log",0);
        ok=(Button) findViewById(R.id.button1);
        password=(EditText) findViewById(R.id.editText1);
        ok.setOnClickListener(new OnClickListener() {
       
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String s=(preferences.getString("passkey", "helloavi")).toString();
				String pass=password.getText().toString();
				if(pass.equals(s))
				{
					Toast.makeText(getApplicationContext(),"Great..!! your Welcome.", Toast.LENGTH_SHORT).show();
					
				}
				else
				{
					Toast.makeText(getApplicationContext(),"oops..!!Password is Incorrect.", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pin__mode, menu);
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
