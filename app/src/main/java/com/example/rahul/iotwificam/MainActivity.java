package com.example.rahul.iotwificam;

import android.app.AlertDialog;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;
import android.content.Intent;
import java.io.IOException;


public class MainActivity extends Activity  {

    private Camera currentCamera;
    public static final String TAG="MYAPP";
    public static String APP_TITLE = "IoTWifiCam";

    public static Camera getCamera(int selectedCameraOption){
        Camera camera = null;
        // open the camera.
        try{
            camera = Camera.open(selectedCameraOption);
            Log.d(TAG, "Created the camera  :) ");
        }catch(Exception ex){
            Log.d(TAG, "There was an exception when opening the camera");
        }
      return camera;
    }

    private AndroidCameraView myCameraPreview;
    private CamServer camServer;

    private void releaseCamera(){
        currentCamera.stopPreview();
        currentCamera.setPreviewCallback(null);
        currentCamera.release();
        currentCamera = null;
    }

    private void setUpCamera(int selectedCameraOption){
        currentCamera = getCamera(selectedCameraOption);
        if(currentCamera == null){
            AlertDialog dialog = Utilities.displayAlertDialog(null, "Please close  apps that are using the Camera.",
                this, "OK", null,
                null, null, null, null);
            dialog.show();
            releaseCamera();
            finish();
    }

}

    private void stopAndReleaseCamera(){
        if(camServer!=null && camServer.isAlive()){
            camServer.stop();
        }
        releaseCamera();
        MainActivity.this.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent launchedBy  = getIntent();
        int selectedCameraOption = launchedBy.getIntExtra(DetailsActivity.CameraChoiceKey, DetailsActivity.DEFAULT_CAMERA_CHOICE);
        Log.d(MainActivity.TAG, "Camera choice jey = " + selectedCameraOption);
        setUpCamera(selectedCameraOption);
        final int portNumber = launchedBy.getIntExtra(DetailsActivity.PortNumberKey, 1069);
        final String ipAddress = launchedBy.getStringExtra(DetailsActivity.IPAddressKey);
        FrameLayout previewLayout = (FrameLayout) this.findViewById(R.id.camera_preview);

        myCameraPreview = new AndroidCameraView(this, currentCamera, this);

        previewLayout.addView(myCameraPreview);
          try{
              camServer = new CamServer(ipAddress, portNumber);
              camServer.start();
          }
          catch(IOException ex){
              Log.d(MainActivity.TAG, "Could not start the server");
          }
    }

    @Override
    public void onBackPressed(){
        endServer();
        stopAndReleaseCamera();
        finish();
    }

    private void endServer(){
        if(camServer!=null && camServer.isAlive())
            camServer.stop();
    }
}
