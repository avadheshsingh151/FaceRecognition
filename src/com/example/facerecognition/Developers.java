package com.example.facerecognition;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Developers extends Fragment {
	View rootview;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 rootview = inflater.inflate(R.layout.developer, container, false);
		
  return rootview;
	}
}
