package com.example.rahul.iotwificam;

/**
 * Created by Rahul on 4/4/2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class RateAppDialog {

    private static String IS_DISABLED = "IS_DISABLED";
    private static String NAME = "RATEAPP";
    private static String LAUNCH_COUNT = "LAUNCH_COUNT";
    private static int LAUNCH_FOR_PROMPT =0;

     public static void show(final Context context){

         boolean showRateDialog = false;
         SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, 0);
         SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
         if(!sharedPreferences.getBoolean(IS_DISABLED, false)){

             int launchCount = sharedPreferences.getInt(LAUNCH_COUNT, 0) + 1;
            if(launchCount > LAUNCH_FOR_PROMPT){
                showRateDialog = true;
            }
             preferencesEditor.putInt(LAUNCH_COUNT, launchCount);
         }

         if(showRateDialog){

           DialogInterface.OnClickListener listener = new OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + MainActivity.APP_TITLE));
                   context.startActivity(intent);
                   dialog.dismiss();
               }
           };

             DialogInterface.OnClickListener notNow = new OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                 }
             };

            Dialog toShow =  Utilities.displayAlertDialog("Rate " + MainActivity.APP_TITLE, "Thanks for using my app!\nI'm happy"
             + " you've loved it so far.It would be awesome if you could rate my app at the store!",
                    (Activity)context,
                    "Sure!", listener, "Remind Me Later!", notNow,"No Thanks",notNow);
             toShow.show();
         }
     }

}
