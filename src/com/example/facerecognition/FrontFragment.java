package com.example.facerecognition;

import java.text.DateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TextView;

public class FrontFragment extends Fragment{
	View rootview;
	Button by_face,by_pin;
	TextView date;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootview=inflater.inflate(R.layout.front, container, false);
		
		by_face=(Button)rootview.findViewById(R.id.button2);
		by_pin=(Button) rootview.findViewById(R.id.button1);
		date=(TextView) rootview.findViewById(R.id.textView1);
		date.setText( DateFormat.getDateInstance().format(new Date()));
		
		by_face.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(),FaceRecognition.class);
				startActivity(intent);
			}
		});
		
      by_pin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(),PIN_MODE.class);
				startActivity(intent);
			}
		});
		return rootview;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
}
