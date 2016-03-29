package com.example.rahul.iotwificam;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.ViewGroup.LayoutParams;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;

import com.example.rahul.iotwificam.adapters.CameraSpinner;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


/**
 * Created by Rahul on 3/6/2016.
 */
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

    private EditText serverIpAddressEditText;
    private EditText serverPortNumberEditText;
    private Button  startServer;
    public Spinner cameraChoiceSpinner;
    public int  selectedCameraOption;

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setBackgroundUsingResource(View view, int resourceId){
        view.setBackgroundResource(resourceId);
    }





    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_details);
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
                Toast.makeText(getApplicationContext(), "You selected the option = " + selectedCameraOption,
                        Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        startServer = (Button)this.findViewById(R.id.startStreamingBtn);
        startServer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(getBaseContext(), MainActivity.class);
                serverIntent.putExtra(CameraChoiceKey, selectedCameraOption);

                startActivity(serverIntent);
                //     finish();   // finish this activity.
            }
        });

    }


}
