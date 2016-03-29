package com.example.rahul.iotwificam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.os.Handler;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.os.Looper;
import android.os.Bundle;
import android.content.Intent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class MainActivity extends Activity  {

    public static String message = "Hi there, server thread can access data of UI Thread";
    private static Handler serverThreadHandler;
    private static  Handler uiThreadHandler;


    private Camera currentCamera;
    public static final String TAG="MYAPP";
    public static final String COM_TAG = "COM";

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


    private void setUpWifi(DialogInterface.OnClickListener alertListener){
        boolean wifiOn = Utilities.isWifiTurnedOn(this);
        if(!wifiOn){
            Log.d(MainActivity.TAG, "wIFI IS OFF");
            AlertDialog dialog =  Utilities.displayAlertDialog("Please turn on Wifi, and make sure both the client and server are connected to the same wifi network.",
                    this, "OK", alertListener, null, null);
            dialog.show();
            //       finish();
        }
    }

private void setUpCamera(int selectedCameraOption, DialogInterface.OnClickListener alertListener){
    currentCamera = getCamera(selectedCameraOption);
    if(currentCamera == null){
        AlertDialog dialog = Utilities.displayAlertDialog("Please close  apps that are using the Camera.",
                this, "OK", alertListener,
                null, null);
        dialog.show();
        releaseCamera();
        finish();
    }

}



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogInterface.OnClickListener alertListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(camServer!=null && camServer.isAlive()){
                    camServer.stop();
                }
                releaseCamera();
                MainActivity.this.finish();
            }
        };
        Intent launchedBy  = getIntent();
        int selectedCameraOption = launchedBy.getIntExtra(DetailsActivity.CameraChoiceKey, DetailsActivity.DEFAULT_CAMERA_CHOICE);
        Log.d(MainActivity.TAG, "Camera choice jey = " + selectedCameraOption);
        setUpCamera(selectedCameraOption, alertListener);
        setUpWifi(alertListener);

        FrameLayout previewLayout = (FrameLayout) this.findViewById(R.id.camera_preview);
        String ipAddress = Utilities.getIpAddress(this);
        int portNumber = 1069;
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


//    private void sendMessage(boolean fromServerToUI,String tag, String message){
//        Bundle bundle = new Bundle();
//        bundle.putString(tag, message);
//        Handler handler = fromServerToUI ? uiThreadHandler
//                : serverThreadHandler;
//        Message messageToSend = fromServerToUI ? uiThreadHandler.obtainMessage()
//                : serverThreadHandler.obtainMessage();
//        messageToSend.setData(bundle);
//        handler.sendMessage(messageToSend);
//    }


//    public void serverCode(int PORT_NUMBER){
//        try{
//
//
//       ServerSocket serverSocket = new ServerSocket(1069, 8080, InetAddress.getByName("0.0.0.0"));
//       //     SocketAddress serverAddress = new InetSocketAddress("10.16.4.42", PORT_NUMBER);
//            Log.d(MainActivity.TAG, "Setting up server");
//            sendMessage(true, COM_TAG, "Setting up the server");
////        handler.Display("Started the server");
////        Message message = uiThreadHandler.obtainMessage();
////            Bundle toSend = new Bundle();
////            toSend.putString("log", "You are a winner, from server thread!");
////            message.setData(toSend);
////            uiThreadHandler.sendMessage(message);
//            Log.d(MainActivity.TAG, "Starting the server");
//            Log.d(MainActivity.TAG, serverSocket.toString());
//         //   serverSocket.bind(serverAddress);
//        Socket clientSocket = serverSocket.accept();
//            Log.d(MainActivity.TAG, "I received something!");
//        sendMessage(true, COM_TAG, "Server received a connection");
//            sendData(clientSocket, "It worked!");
//    }catch(Exception ex){
//        Log.d(MainActivity.TAG, ex.toString());
//    }

//}

    @Override
    public void onBackPressed(){
        endServer();
        releaseCamera();
        finish();
    }

    private void endServer(){
        if(camServer!=null && camServer.isAlive())
            camServer.stop();
    }
}
