package com.rahul_arnold.apps.iotwificam;



/**
 * Created by Rahul on 3/28/2016.
 */
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;



public class CamServer extends NanoHTTPD {

    public CamServer(String ipAddress, int portNumber) throws IOException {
        super(ipAddress, portNumber);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.d(MainActivity.TAG, "Started the server!");
    }

    @Override
    public Response serve(IHTTPSession session){
        Map<String, String> headers = session.getHeaders();
        for(String header : headers.keySet()){
            Log.d(MainActivity.TAG, header + ": "  + headers.get(header));
        }
        Log.d(MainActivity.TAG, "Message is = " + AndroidCameraView.encodedImageToSend);
        Response response =  newFixedLengthResponse(AndroidCameraView.encodedImageToSend);
        response.addHeader("Access-Control-Allow-Methods", "DELETE, GET, POST, PUT");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Accept");
        return response;
    }
}
