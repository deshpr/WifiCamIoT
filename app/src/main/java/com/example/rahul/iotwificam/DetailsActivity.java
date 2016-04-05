package com.example.rahul.iotwificam;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;

import android.os.Bundle;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Dialog;
import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.example.rahul.iotwificam.adapters.CameraSpinner;
import android.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;

/**
 * Created by Rahul on 3/6/2016.
 */

import android.content.IntentFilter;

public class DetailsActivity extends AppCompatActivity {

    public static String IPAddressKey = "IP";
    public static String PortNumberKey = "PORT";
    public static int DEFAULT_CAMERA_CHOICE = 0;
    public static String CameraChoiceKey = "CAMERACHOICE";// default is back.
    public  enum CameraChoices{

        FRONT("Front Camera"), BACK("Back Camera");
        private String value;
        private CameraChoices(String value){
            this.value = value;
        }
    }

    private BroadcastReceiver receiver = null;

    private Button  startServer;
    public Spinner cameraChoiceSpinner;
    public int  selectedCameraOption;

    private void setUpWifi(){
        boolean wifiOn = Utilities.isWifiTurnedOn(this);
        if(!wifiOn){
            Log.d(MainActivity.TAG, "wIFI IS OFF");
            AlertDialog dialog =  Utilities.displayAlertDialog(null, "Please turn on Wifi, and make sure both the client and server are connected to the same wifi network.",
                    this, "OK", null, null, null, null, null);
            dialog.show();
            //       finish();
        }
    }



    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void displayPortNumber(int portNumber){
        TextView portNumberView = (TextView)this.findViewById(R.id.serverPort);
        portNumberView.setText(String.valueOf(portNumber));
    }

//    private void displayServerDetails(String serverAddress, int portNumber){
////        TextView ipAddress = (TextView)this.findViewById(R.id.serverIp);
////        ipAddress.setText(serverAddress);
//        displayIpAddress(serverAddress, false);
//        displayPortNumber(portNumber);
//    }


    private void displayLinks(){
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.display_url, null);
        Dialog d = Utilities.createAlertDialog(v,this,"Got it!", null, null, null, null, null);
        d.show();
    }

    private int  previousColor;

    public void displayIpAddress(String serverAddress, boolean showError){

        TextView ipAddress = (TextView) findViewById(R.id.serverIp);
        Log.d(MainActivity.TAG, "Show Error = " + showError);
        if(showError){
            serverAddress = "WIFI OFF!";
                ipAddress.setTextColor(Color.argb(255,255,0,0));
        }
        else{
                ipAddress.setTextColor(previousColor);
        }
        ipAddress.setText(serverAddress);
    }



    public void registerBroadCastReceiver(){
        IntentFilter filter = new IntentFilter();
        Log.d(MainActivity.TAG, "Registering the receiver");
      filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        if(receiver==null){
            Log.d(MainActivity.TAG, "Registered the receiver");
            receiver = new DetectWifiStateChange(this);
        }
        this.registerReceiver(receiver, filter);
    }

    public  static class DetectWifiStateChange extends BroadcastReceiver {

        private DetailsActivity parentActivity;

        public DetectWifiStateChange(DetailsActivity parentActivity) {
            this.parentActivity = parentActivity;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String  ipAddress = Utilities.getIpAddress();
            parentActivity.displayIpAddress(ipAddress, !Utilities.isWifiTurnedOn(parentActivity));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_webpage:
                displayLinks();
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onStop(){
        super.onStop();
        Log.d(MainActivity.TAG, "Unregistered the receiver in stop");
        if(receiver!=null)
            try{
                unregisterReceiver(receiver);
            }
            catch(IllegalArgumentException ex){

            }
    }

    @Override
    public void onPause(){
        super.onPause();
      //  Log.d(MainActivity.TAG, "Unregistered the receiver in pause");
        if(receiver!=null){
            try{
                unregisterReceiver(receiver);
            }
            catch(IllegalArgumentException ex){

            }
        }
  }


    public Intent makeIntentWithData(String ipAddress, int portNumber){
        Intent serverIntent = new Intent(getBaseContext(), MainActivity.class);
        serverIntent.putExtra(CameraChoiceKey, selectedCameraOption);
        serverIntent.putExtra(IPAddressKey, ipAddress);
        serverIntent.putExtra(PortNumberKey, portNumber);
        return serverIntent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_details);
        previousColor = ((TextView)findViewById(R.id.serverIp)).getCurrentTextColor();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cameraChoiceSpinner = (Spinner)this.findViewById(R.id.camera_choice_list_view);
        cameraChoiceSpinner.setAdapter(new CameraSpinner(this, R.layout.layout_spinner,
                new String[]{
                        CameraChoices.BACK.value.toString(), CameraChoices.FRONT.value.toString()},
                new String[]{"B", "F"}, this.getLayoutInflater()));
        cameraChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCameraOption = position;
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final String ipAddress = Utilities.getIpAddress();

        displayIpAddress(ipAddress, !Utilities.isWifiTurnedOn(this));

        final int portNumber = 1069;

        displayPortNumber(portNumber);
     //   displayServerDetails(ipAddress, portNumber);
        registerBroadCastReceiver();
        startServer = (Button)this.findViewById(R.id.startStreamingBtn);
        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean wifiOn = Utilities.isWifiTurnedOn(DetailsActivity.this);
                if (!wifiOn) {
                    Log.d(MainActivity.TAG, "wIFI IS OFF");
                    AlertDialog dialog = Utilities.displayAlertDialog(null, "Please turn on Wifi, and make sure both the client and server are connected to the same wifi network.",
                            DetailsActivity.this, "OK", null, null, null, null, null);
                    dialog.show();
                } else {
                    Intent serverIntent = makeIntentWithData(ipAddress, portNumber);
                    startActivity(serverIntent);
                }
            }
        });
        RateAppDialog.show(this);
    }
}
