package com.lalitp.sampleapp.Utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.lalitp.sampleapp.R;


/**
 * Created by atulsia on 5/7/16.
 */
public class PermissionDialogView {

    public static int MEDIA_PERMISSION = 0;
    public static int LOCATION_PERMISSION = 1;
    public static int SMS_PERMISSION = 2;
    public static int PHONE_PERMISSION = 3;

    private static void goToSettings(Activity activity) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(myAppSettings, 168);
    }

    public static void gotoSettingsDialog(final Activity activity, final int type) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
        // set title
        String appName = activity.getResources().getString(R.string.app_name);
        alertDialogBuilder.setTitle(appName);

        String strMSG = getPermissionMessage(type);

        // set dialog message
        alertDialogBuilder
                .setMessage(strMSG)
                .setCancelable(false)
                .setPositiveButton("go to settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        if (Build.VERSION.SDK_INT > 22) {
                            goToSettings(activity);
                        }
                    }
                })
                .setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        if (type == 3) {
                            activity.finish();
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    private static String getPermissionMessage(int type) {
        switch (type) {
            case 0:
                return "Few permission required for media access.";
            case 1:
                return "Few permission required for location access.";
            case 2:
                return "Few permission required for sms access.";
            case 3:
                return "Few permission required for start the app.";
            default:
                return "Few permission required for start the app.";
        }

    }

}
