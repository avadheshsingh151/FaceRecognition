package com.example.facerecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import android.R.string;
import android.app.Activity;
import android.app.AliasActivity;
import android.content.Context;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

public class FaceRecognition extends  Activity implements CvCameraViewListener2{
	
	private static final String    TAG                 = MainActivity.class.getName();
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    
    private Mat                    mRgba; 
    private Mat					   mGray;
 
    private File                   mCascadeFile, cascadeFileER;
    //This is the Classifier object where face detection classifier is loaded 
    
    private CascadeClassifier      faceDetector,eyeDetector;
    private static int camPreviewHeight,camPreviewWidth;
    private CameraBridgeViewBase   mOpenCvCameraView;
    
    private int learn_frames = 0;
    private Mat teplateR;
    private Mat teplateL;
    int method = 0;
    
    
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    
    double xCenter = -1;
    double yCenter = -1;
    
    TextView matValueView;
    static String matValue;
    Point center=new Point(0,0);
    
    private Mat mRgbaT;
    private Mat mGrayT;
    
    /**
     * This is the new method of loading OpenCV libraries
     * i.e. the Dynamic initialization.
     * This requires that the target mobile has OpenCV Manager installed, if absent
     * then user will be prompted to download it
     * 
     * This keeps the app size small, and provide only the libraries built for that 
     * architecture thus improving performance.
     * 
     * As I have told that the previous (Static initialization) didn't provide good performance
     * but this does.
     * 
     * Here u can check if the file is present instead of just copying it over every time.
     */
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");                    
                    try {
                        // load cascade classifier ".xml" file from application resources
                    	//This first copies all the bytes of the required file from the asset folder
                    	//to device default ext-sd and then create the required file.
                    	 // load cascade file from application resources
                        InputStream is = getResources().openRawResource(
                                R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir,
                                "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
     
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
     
                        // --------------------------------- load left eye
                        // classificator -----------------------------------
                        InputStream iser = getResources().openRawResource(
                                R.raw.haarcascade_lefteye_2splits);
                        File cascadeDirER = getDir("cascadeER",
                                Context.MODE_PRIVATE);
                        cascadeFileER = new File(cascadeDirER,
                                "haarcascade_eye_right.xml");
                        FileOutputStream oser = new FileOutputStream(cascadeFileER);
     
                        byte[] bufferER = new byte[4096];
                        int bytesReadER;
                        while ((bytesReadER = iser.read(bufferER)) != -1) {
                            oser.write(bufferER, 0, bytesReadER);
                        }
                        iser.close();
                        oser.close();
                        

                        
                        //Get path to the written file, ie. default ext-sd or sd
                        faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());                        
                        if (faceDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            faceDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());                                              

                        
                        eyeDetector = new CascadeClassifier(
                                cascadeFileER.getAbsolutePath());
                        if (eyeDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            eyeDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from "
                                    + cascadeFileER.getAbsolutePath());
                      
                        cascadeDir.delete();
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                               

                    //Enable the camera preview surface
                    //as Opencv library is loaded
                    
                    //Camera index = 1 for front camera
                    mOpenCvCameraView.setCameraIndex(1); 
                    //Camera fps meter to check performance
                    mOpenCvCameraView.enableFpsMeter();
                    mOpenCvCameraView.enableView();
                    
                    
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
         Log.i(TAG, "onCreate");
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_face_recognition);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		
		//mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        
      matValueView=(TextView) findViewById(R.id.textView1);
      
	   
     	
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.face_recognition, menu);
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

	//Pause the camera to save battery
		public void onPause()
	    {
	        super.onPause();
	        if (mOpenCvCameraView != null)
	            mOpenCvCameraView.disableView();
	    }

		
		//
	    @Override
	    public void onResume()
	    {
	        super.onResume();
	        //This is the command which actually calls the Opencv load method and then calls the 
	        //mLoaderCallback method defined above after variables declaration.
	        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
	 
	  
	    }

	    
	    
	    public void onDestroy() {
	        super.onDestroy();
	        mOpenCvCameraView.disableView();
	    }
	    
	    
	    
	    //Instantiate the Mat when camera is started
	    public void onCameraViewStarted(int width, int height) {        
	        mRgba = new Mat();
	        mGray = new Mat();
	        
	        //Get camera preview height, width for use in face detection
	        camPreviewHeight = height;
	        camPreviewWidth = width;
	    }

	    
	    
	    //Release the memory acquired by Mat    
	    public void onCameraViewStopped() {
	        mRgba.release();
	        mGray.release();
	    }

	    
	    /**
	     * This is the method where all the logic should be placed
	     * This is a callback method, the camera preview is first sent here, we need to process it 
	     * then send it to the camera preview surface after processing. 
	     * 
	     */
		@Override
		public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
			
			mRgba = inputFrame.rgba();    
			mGray = inputFrame.gray();
			
			
		/*	mGrayT=mGray.t();
			Core.flip(mGray.t(), mGrayT, -1);
			  Imgproc.resize(mGrayT, mGrayT, mGray.size());
			*/  
			
		      
			
			if (mAbsoluteFaceSize == 0) {
	            int height = mGray.rows();
	            if (Math.round(height * mRelativeFaceSize) > 0) {
	                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
	            }
	        }
	        MatOfRect faces = new MatOfRect();

	        //If the cascade classifier is not null
	        //then it tries to detect faces.
	        //Detecting face/object is faster in grayscale images, so we used mGray not mRGBA
	        if (faceDetector != null)
	            faceDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
	                     new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size(camPreviewHeight,camPreviewWidth));
	        
	       
	        //Loop over the array of rectangles of the detected face
	        //and draw a line around them
	        //tl-> top left 
	        //br-> bottom right
	        //Face was detected in mGray but the outline is drawn in mRGBA.
	        Rect[] facesArray = faces.toArray();
	        for (int i = 0; i < facesArray.length; i++)
	        {      Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
	        
	        
	        xCenter = (facesArray[i].x + facesArray[i].width + facesArray[i].x) / 2;
            yCenter = (facesArray[i].y + facesArray[i].y + facesArray[i].height) / 2;
           center = new Point(xCenter, yCenter);
           
            Core.circle(mRgba, center, 4, new Scalar(255, 0, 0, 255), 2);
            
            
            matValue="[" + center.x + "," + center.y + "]";
           
           
           
            
           
           
            Rect r = facesArray[i];
            // compute the eye area
            Rect eyearea = new Rect(r.x + r.width / 8,
                    (int) (r.y + (r.height / 4.5)), r.width - 2 * r.width / 8,
                    (int) (r.height / 3.0));
            // split it
            Rect eyearea_right = new Rect(r.x + r.width / 16,
                    (int) (r.y + (r.height / 4.5)),
                    (r.width - 2 * r.width / 16) / 2, (int) (r.height / 3.0));
            Rect eyearea_left = new Rect(r.x + r.width / 16
                    + (r.width - 2 * r.width / 16) / 2,
                    (int) (r.y + (r.height / 4.5)),
                    (r.width - 2 * r.width / 16) / 2, (int) (r.height / 3.0));
            // draw the area - mGray is working grayscale mat, if you want to
            // see area in rgb preview, change mGray to mRgba
            Core.rectangle(mRgba, eyearea_left.tl(), eyearea_left.br(),
                    new Scalar(255, 0, 0, 255), 2);
            Core.rectangle(mRgba, eyearea_right.tl(), eyearea_right.br(),
                    new Scalar(255, 0, 0, 255), 2);
            
            
            teplateR = get_template(eyeDetector, eyearea_right, 24);
            teplateL = get_template(eyeDetector, eyearea_left, 24);
            learn_frames++;
            
            
           
	        }
	      
	        mRgbaT = mRgba.t();
			  Core.flip(mRgba.t(), mRgbaT, -1);
			  Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
			  
			  Core.putText(mRgbaT, "[" + center.x + "," + center.y + "]",
	                    new Point(45,50),
	                    Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255,
	                            255)); 
			  runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
				}
			});
	        return mRgbaT;
	        
	        
	        //Return the processed frame to the camera surface preview.
	       // return mRgba;
	       
		}
		
		
		 private Mat get_template(CascadeClassifier clasificator, Rect area, int size) {
		        Mat template = new Mat();
		        Mat mROI = mGray.submat(area);
		        MatOfRect eyes = new MatOfRect();
		        Point iris = new Point();
		        Rect eye_template = new Rect();
		        clasificator.detectMultiScale(mROI, eyes, 1.15, 2,
		                Objdetect.CASCADE_FIND_BIGGEST_OBJECT
		                        | Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30),
		                new Size());
		 
		        Rect[] eyesArray = eyes.toArray();
		        for (int i = 0; i < eyesArray.length;) {
		            Rect e = eyesArray[i];
		            e.x = area.x + e.x;
		            e.y = area.y + e.y;
		            Rect eye_only_rectangle = new Rect((int) e.tl().x,
		                    (int) (e.tl().y + e.height * 0.4), (int) e.width,
		                    (int) (e.height * 0.6));
		            mROI = mGray.submat(eye_only_rectangle);
		            Mat vyrez = mRgba.submat(eye_only_rectangle);
		             
		             
		            Core.MinMaxLocResult mmG = Core.minMaxLoc(mROI);
		 
		            Core.circle(vyrez, mmG.minLoc, 2, new Scalar(255, 255, 255, 255), 2);
		            iris.x = mmG.minLoc.x + eye_only_rectangle.x;
		            iris.y = mmG.minLoc.y + eye_only_rectangle.y;
		            eye_template = new Rect((int) iris.x - size / 2, (int) iris.y
		                    - size / 2, size, size);
		            Core.rectangle(mRgba, eye_template.tl(), eye_template.br(),
		                    new Scalar(255, 0, 0, 255), 2);
		            template = (mGray.submat(eye_template)).clone();
		            return template;
		        }
		        return template;
		    }
		 
		 private void setMinFaceSize(float faceSize) {
		        mRelativeFaceSize = faceSize;
		        mAbsoluteFaceSize = 0;
		    }
		 
		
}

