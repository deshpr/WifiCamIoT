package com.example.rahul.iotwificam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Rahul on 3/29/2016.
 */
public class Utilities {

    public static boolean isWifiTurnedOn(Activity callingActivity){
        WifiManager  wifiManager = (WifiManager)callingActivity.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static String  getIpAddress(){
        try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface inf = en.nextElement();
                if(inf.getName().contains("wlan")){
                    for(Enumeration<InetAddress> enumIpAddr = inf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if(!inetAddress.isLoopbackAddress() && (inetAddress.getAddress().length == 4)){
                            Log.d(MainActivity.TAG, inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        }catch(SocketException ex){
            Log.d(MainActivity.TAG, ex.toString());
        }
        Log.d(MainActivity.TAG, "no address found");
        return null;
    }


    public static android.app.AlertDialog createAlertDialog(View dialogView, Activity parentActivity, String positiveText,
                                                             DialogInterface.OnClickListener positiveButtonDelegate,
                                                             String negativeButtonText, DialogInterface.OnClickListener negativeButtonDelegate,
                                                            String neutralButtonText, DialogInterface.OnClickListener neutralButtonDelegate
                                                            ){
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);

        builder.setView(dialogView);
        builder.setPositiveButton(positiveText, positiveButtonDelegate);
        if(negativeButtonText!=null && negativeButtonText.trim().length()!=0){
            builder.setNegativeButton(negativeButtonText, negativeButtonDelegate);
        }
        if(neutralButtonText!=null && neutralButtonText.trim().length()!=0){
            builder.setNeutralButton(neutralButtonText, neutralButtonDelegate);
        }

        return builder.create();
    }

    public  static android.app.AlertDialog displayAlertDialog(String  title, String message, Activity parentActivity, String positiveText, DialogInterface.OnClickListener positiveListener,
                                                              String negativeText, DialogInterface.OnClickListener  negativeListener,
                                                              String neutralButtonText, DialogInterface.OnClickListener neutralButtonDelegate){
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        android.view.LayoutInflater inflater = parentActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        TextView  titleView = (TextView)dialogView.findViewById(R.id.alert_title);
        if(title!=null && title.trim().length()!=0){
            titleView.setText(String.valueOf(title));
        }
        TextView displayMessage = (TextView)dialogView.findViewById(R.id.alert_message);
        displayMessage.setText(message);
        builder.setView(dialogView);
        if(negativeText!=null && negativeText.trim().length()!=0){
            builder.setNegativeButton(negativeText, negativeListener);
        }
        if(neutralButtonText!=null && neutralButtonText.trim().length()!=0){
            builder.setNeutralButton(neutralButtonText, neutralButtonDelegate);
        }
        if(positiveText!=null && positiveText.trim().length()!=0){
            builder.setPositiveButton(positiveText, positiveListener);
        }
        return builder.create();
    }
}
