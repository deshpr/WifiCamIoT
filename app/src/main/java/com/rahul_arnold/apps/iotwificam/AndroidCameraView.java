package com.rahul_arnold.apps.iotwificam;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.ConnectException;

import android.os.Environment;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import java.net.URL;
import java.io.OutputStream;

import android.hardware.Camera;
import android.content.Context;
import android.util.*;
import android.hardware.Camera.PreviewCallback;
import android.graphics.Rect;
import  android.util.Base64;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.app.Activity;


/**
 * Created by Rahul on 3/4/2016.
 */
public class AndroidCameraView extends SurfaceView  implements SurfaceHolder.Callback {


    public SurfaceHolder myHolder;
    public Camera myCamera;

    public InetAddress  serverAddress;


    public static String  encodedImageToSend = "";
    public String serverAddres;
    public int portNumber;
    public Socket clientSocket = null;
    public static java.io.PrintWriter  out;

    private Activity launchingActivity;


    public AndroidCameraView(Context context, Camera  camera, Activity launchingActivity){

        super(context);
//
//        this.serverAddres =  serverAddress;
//        this.portNumber =  portNumber;
        this.myCamera = camera;
        this.launchingActivity = launchingActivity;
        Log.d(MainActivity.TAG, "the camera is = " + camera.toString());
        this.myHolder =getHolder();
        this.myHolder.addCallback(this);
        this.myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.launchingActivity = launchingActivity;

    }




    private HttpURLConnection setUpConnection(String   address) throws MalformedURLException, IOException{
        URL url = new URL(address);
        HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
        httpCon.setDoOutput(true);// use only for output.
        httpCon.setRequestMethod("POST");
        httpCon.setUseCaches(false);
        httpCon.setRequestProperty("Content-Type", "application/json");
        return httpCon;
    }
    private  void setDataToSend(HttpURLConnection httpCon, String dataToSend){
        //  httpCon.setRequestProperty("Content-Length", String.valueOf(dataToSend.length()));
        httpCon.setRequestProperty("Accept", "application/json");
    }

    private void sendData(HttpURLConnection httpCon, String dataToSend) throws IOException{
        httpCon.connect();
        OutputStream out =httpCon.getOutputStream();
        //    OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");

        // out.write(encodedData.getBytes());
        // out.write(dataToSend.toString());
        out.write(dataToSend.getBytes("UTF-8"));

//        osw.flush();
        //      osw.close();

    }

    private  void sendHttpPostRequest(String address, String jsonDataToSend){
        try{
            Log.d(MainActivity.TAG, "Started sending the post request");
            System.out.println("Start printing");
            HttpURLConnection   httpCon = setUpConnection(address);
            Log.d(MainActivity.TAG, "Finished setting up the connection");
            Log.d(MainActivity.TAG, httpCon.toString());
            setDataToSend(httpCon,  jsonDataToSend);
            sendData(httpCon, jsonDataToSend);
            Log.d(MainActivity.TAG, "Finished sending the data");
            System.out.println("get rsponse code");
            int code =httpCon.getResponseCode();
            System.out.println("Code = " + code);
            System.out.println("sent the data");
        }
        catch(MalformedURLException ex){

        }
        catch(ConnectException ex){

        }
        catch(IOException ex){

        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }
    }


    public void surfaceCreated(SurfaceHolder holder){
        if(myCamera == null){
            Log.d(MainActivity.TAG, "You passed a null camera");
            return;
        }
        try{
            myCamera.setPreviewDisplay(holder);
            Log.d(MainActivity.TAG, "Starting the preview");
            myCamera.startPreview();
            Log.d(MainActivity.TAG, "Preview has started");
        }catch(java.io.IOException ex){
            Log.d(MainActivity.TAG, ex.toString());
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder){
        if(myCamera!=null){
            this.myCamera.stopPreview();

        }
    }


    private String makeJsonToSend(String messageToSend){
        String dataToSend = "{\"id\":\"" + messageToSend + "\"}";
        return dataToSend;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        if(this.myCamera == null)
            return;
        if(this.myHolder.getSurface() == null){
            return;
        }
        try{
            this.myCamera.stopPreview();


        }catch(Exception ex){

        }
        try{

            this.myCamera.setPreviewDisplay(this.myHolder);
            this.myCamera.startPreview();

        }catch(Exception ex){
            Log.d(MainActivity.TAG, "Exception in AndroidView before start preview");
            Log.d(MainActivity.TAG, ex.toString());
        }



        this.myCamera.setPreviewCallback(new PreviewCallback(){
            @Override
            public void onPreviewFrame(byte[] data, Camera camera){
                Camera.Parameters  parameters =  myCamera.getParameters();
//                int  previewHeight = 480;
//                int previewWidth = 640;
//               parameters.setPreviewSize(previewWidth, previewHeight);
//                myCamera.setParameters(parameters);
                int height = parameters.getPreviewSize().height;
                int width = parameters.getPreviewSize().width;
                //  Log.d(MainActivity.TAG, "Width = " + width);
                //  Log.d(MainActivity.TAG, "Height = " + height);
                YuvImage  yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);

                Rect rectangle = new Rect(0,0,width, height);

//                Bitmap image = BitmapFactory.decodeByteArray(data,0, data.length);


                File mediaFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if(mediaFile == null){
                    Log.d(MainActivity.TAG, "Could not make the media file, see storage");
                }
                try{

                    FileOutputStream  fos = new FileOutputStream(mediaFile, false);// overwrite the file.
                    //         boolean written = image.compress(Bitmap.CompressFormat.JPEG,20, fos);
                    boolean writeData = yuvImage.compressToJpeg(rectangle,80, fos);
                    fos.close();
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(mediaFile));
                    byte[] bytes  = new byte[(int)mediaFile.length()];
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                    String result= Base64.encodeToString(bytes, Base64.NO_WRAP);
                    encodedImageToSend =  result;
                    Log.d("ANOTHERAPP", encodedImageToSend);
             //       new Thread(new ClientThread()).start();
                }catch(Exception ex){
                    Log.d(MainActivity.TAG, "Could not save the file");
                    Log.d(MainActivity.TAG, ex.getMessage());
                }
            }
        });
    }


    public static int MEDIA_TYPE_IMAGE = 1;

    public File getOutputMediaFile(int fileType){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "WifiCam");
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.d(MainActivity.TAG, "Could not make the directory");
                return null;
            }
        }
        File mediaFile;
        if(fileType == MEDIA_TYPE_IMAGE){
            String path = mediaStorageDir.getPath() + File.separator + "IMGrocksFinal.jpg";
            mediaFile = new File(path);
            // Log.d(MainActivity.TAG, "File path = " +  path);
            if(mediaFile.exists()){
                //         Log.d(MainActivity.TAG, "The file exists");
                mediaFile.delete();
                //       Log.d(MainActivity.TAG, "The file is deleted");
                mediaFile = new File(path);
            }

        }
        else{
            return null;
        }
        return mediaFile;
    }



    public String makeStringToSend(int sizeLength, String stringToSend){
        //  first 16 bytes(characters) represent the size of the image to  send.
        StringBuffer buffer = new StringBuffer();
        int sizeStringLength = String.valueOf(sizeLength).length();

        for(int i= 0; i < (16-sizeStringLength); i++ ){
            buffer.append(" ");
        }
        buffer.append(String.valueOf(sizeLength));
        buffer.append(".");
        buffer.append(stringToSend);
        return buffer.toString();
    }

    boolean setConnection = false;
    int count = 0;
    class ClientThread  implements Runnable {
        @Override
        public void run(){
            try{
                Log.d(MainActivity.TAG, "Running the main thread");
                String message = "Hi there  from thread " + count;
                String dataToSend = "{\"id\":\"" + message + "\"}";
                ++count;
                if(count%2 == 0){
                    String address = "http://10.16.12.66:1069/";
                    String  jsonDataToSend = makeJsonToSend(encodedImageToSend);
                    sendHttpPostRequest(address, jsonDataToSend);

                    //          Log.d(MainActivity.TAG, "Sending the string now...");
//                    String toSend =   encodedImageToSend;
                    //                  int length = toSend.length();
                    //                 String result = makeStringToSend(length, toSend);
                    //                out.println(result);
                }
            }
//     catch(java.net.ConnectException ex){
//         Log.d(MainActivity.TAG, "Failed to connect to server");
//      //   displayAlertDialog("Cannot connect to the server");
//         launchingActivity.finish();    // finish thw activity
//        }
            catch(Exception ex){
                Log.d(MainActivity.TAG, "Exception inside the thread");
                StringWriter writer = new StringWriter();
                PrintWriter pw = new PrintWriter(writer);
                ex.printStackTrace(pw);
                Log.d(MainActivity.TAG, writer.toString());
                Log.d(MainActivity.TAG,  ex.toString());
            }
        }
    }
}


