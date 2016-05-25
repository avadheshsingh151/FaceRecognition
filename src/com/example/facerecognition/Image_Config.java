package com.example.facerecognition;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment.SavedState;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Image_Config extends Activity {
	 private static final int TAKE_PICTURE_CODE = 100;
	 private static Bitmap cameraBitmap = null;
	 Button takepicture,save,detect;
	 SharedPreferences sharedPreferences;
	 Editor  editor;
	 boolean status;
	   ImageView imageView;
	   
	   
	   private static final int MAX_FACES = 5;
	    int [] fpx = null;
		 int [] fpy = null;
		 
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image__config);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        takepicture=(Button) findViewById(R.id.button1);
        save=(Button) findViewById(R.id.button2);
        detect=(Button) findViewById(R.id.button3);
        imageView = (ImageView)findViewById(R.id.imageView1);
        
        save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveImage(cameraBitmap);
			}
		});
        
        sharedPreferences =getSharedPreferences("log", 0);
		editor=sharedPreferences.edit();
		status =sharedPreferences.getBoolean("status",false);
		if(status)
		{
			Toast.makeText(getApplicationContext(), "Image Loaded", Toast.LENGTH_SHORT).show();
			cameraBitmap=loadImage();
			if(cameraBitmap==null){
				cameraBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
			            R.drawable.avad);}
		  imageView.setImageBitmap(cameraBitmap);
		  
		}
		
		
        takepicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openCamera();
			}
		});
        
        
        detect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				detectFaces();
			}
		});
	}
	
	
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
    
            if(TAKE_PICTURE_CODE == requestCode){
                    processCameraImage(data);
            }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image__config, menu);
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
	private void openCamera(){
	    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    
	    startActivityForResult(intent, TAKE_PICTURE_CODE);
	}
	private void processCameraImage(Intent intent){
	   
	   
	    cameraBitmap = (Bitmap)intent.getExtras().get("data");
	    imageView.setImageBitmap(cameraBitmap);
	    editor.putBoolean("status", true);
	    editor.commit();
	   
	}
	
	public boolean saveImage(Bitmap image) {

		if(image==null){
			image = BitmapFactory.decodeResource(getApplicationContext().getResources(),
		            R.drawable.avad);}
		
		try {
		FileOutputStream fos = getApplicationContext().openFileOutput("Face.png", getApplicationContext().MODE_PRIVATE);
		image.compress(Bitmap.CompressFormat.PNG, 100, fos);
		
		fos.close();

		return true;
		} catch (Exception e) {
		Log.e("saveToInternalStorage()", e.getMessage());
		return false;
		}
		}
	
	public Bitmap loadImage() {
		Bitmap thumbnail = null;
		try {
			File filePath = getApplicationContext().getFileStreamPath("Face.png");
			FileInputStream fi = new FileInputStream(filePath);
			cameraBitmap = BitmapFactory.decodeStream(fi);
			} catch (Exception ex) {
			Log.e("getThumbnail() on internal storage", ex.getMessage());
			}
			
			return cameraBitmap;
	}
	
	private void detectFaces(){
		if(cameraBitmap==null){
		cameraBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
	            R.drawable.avad);}
	    if(null != cameraBitmap){
	            int width = cameraBitmap.getWidth();
	            int height = cameraBitmap.getHeight();
	            
	            FaceDetector detector = new FaceDetector(width, height,Image_Config.MAX_FACES);
	            Face[] faces = new Face[Image_Config.MAX_FACES];
	            
	            Bitmap bitmap565 = Bitmap.createBitmap(width, height, Config.RGB_565);
	            Paint ditherPaint = new Paint();
	            Paint drawPaint = new Paint();
	            
	            ditherPaint.setDither(true);
	            drawPaint.setStyle(Paint.Style.STROKE);
	            drawPaint.setStrokeWidth(2);
	            
	            Canvas canvas = new Canvas();
	            canvas.setBitmap(bitmap565);
	            canvas.drawBitmap(cameraBitmap, 0, 0, ditherPaint);
	            
	            int facesFound = detector.findFaces(bitmap565, faces);
	            PointF midPoint = new PointF();
	            float eyeDistance = 0.0f;
	            float confidence = 0.0f;
	            
	            fpx=new int[facesFound*2];
	            fpy=new int[facesFound*2];
	            Log.i("FaceDetector", "Number of faces found: " + facesFound);
	            
	            if(facesFound > 0)
	            {
	                    for(int index=0; index<facesFound; ++index){
	                            faces[index].getMidPoint(midPoint);
	                            eyeDistance = faces[index].eyesDistance();
	                            confidence = faces[index].confidence();
	                            
	                                           
	                         // set up left eye location
	                       	 fpx[2 * index] = (int)(midPoint.x - eyeDistance / 2);
	                       	 fpy[2 * index] = (int)midPoint.y;
	                       	 
	                       	 // set up right eye location
	                       	 fpx[2 * index + 1] = (int)(midPoint.x + eyeDistance / 2);
	                       	 fpy[2 * index + 1] = (int)midPoint.y;
	                       	 
	                       	 drawPaint.setColor(Color.RED);  	 
	                                      	 
	                  canvas.drawRect( fpx[2 * index]-1 , 
	                        		 fpy[2 * index]-1, 
	                        		 fpx[2 * index]+1, 
	                                 fpy[2 * index]+1, drawPaint);
	                         
	                         
	                         canvas.drawRect( fpx[2 * index+1]-1, 
	                        		 fpy[2 * index+1]-1 , 
	                        		 fpx[2 * index+1]+1, 
	                        		 fpy[2 * index+1]+1, drawPaint);
	                                  
	                            
	                            Log.i("FaceDetector", 
	                                            "Confidence: " + confidence + 
	                                            ", Eye distance: " + eyeDistance + 
	                                            ", Mid Point: (" + midPoint.x + ", " + midPoint.y + ")");
	                            drawPaint.setColor(Color.GREEN);
	                            canvas.drawRect((int)midPoint.x - eyeDistance , 
	                                                            (int)midPoint.y - eyeDistance , 
	                                                            (int)midPoint.x + eyeDistance, 
	                                                            (int)midPoint.y + eyeDistance, drawPaint);
	                            
	                           
	                    }
	            }
	            
	         
	          
	      /*
	            String filepath = Environment.getExternalStorageDirectory() + "/facedetect" + System.currentTimeMillis() + ".jpg";
	            
	                    try {
	                            FileOutputStream fos = new FileOutputStream(filepath);
	                            
	                            bitmap565.compress(CompressFormat.JPEG, 100, fos);
	                            
	                            
	                            fos.flush();
	                            fos.close();
	                    } catch (FileNotFoundException e) {
	                            e.printStackTrace();
	                    } catch (IOException e) {
	                            e.printStackTrace();
	                    }
	                    
	                    */
	                    
	               ImageView     imageView = (ImageView)findViewById(R.id.imageView1);
	                    
	                    imageView.setImageBitmap(bitmap565);
	    }
	}
}
