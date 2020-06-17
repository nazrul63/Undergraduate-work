package com.example.n1363l.final_project_try_006;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by N1363l on 24-05-17.
 */

public class Send_SMS extends Activity {

    MainActivity mact = new MainActivity();
    Obtaining_GPS_value_utilityC gps ;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    String message = "Your buddy is not well.He just fell in:";
    String ph01,ph02,ph03;
    Boolean check=false,sendmsg=true;


    SharedPreferences prefs_to_get;
    public static final String MyPREFERENCES = "f_p_prefs"; //  name of phone number shared preference
    String phone1_num,phone2_num,phone3_num;    // to store the obtained phone numbers from shared prefs


    /* for getting present gps value from sharedPrefs */
    SharedPreferences prefs_to_get_Location;
    public static final String LocationPrefs = "locationprefs";

    public static final String latitude = "latitude";
    public static final String longitude = "longitude";

    float latitude_v,longitude_v;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.demo_layout);

        Toast.makeText(getApplicationContext(),"in Sms class", Toast.LENGTH_SHORT).show();
        gps = new Obtaining_GPS_value_utilityC(getApplicationContext());

        try {
            gps.get_Location();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefs_to_get_Location = getSharedPreferences(LocationPrefs,0);  // 0 means private

        latitude_v = prefs_to_get_Location.getFloat(latitude,0.0f);
        longitude_v = prefs_to_get_Location.getFloat(longitude,0.0f);

        String lat_v = Float.toString(latitude_v);
        String long_v = Float.toString(longitude_v);


        try {
//            message =  "your buddy is not well.\n he just fell \n his location is : ";
            if(gps.s != null)
                message = message.concat(gps.s);
            else if(lat_v!= null && long_v!= null)
                message = message.concat("\nlatitude: "+lat_v+" \nlongitude: "+long_v+" \n ");
            Log.e("sms class",message);
        } catch (Exception e) {
            e.printStackTrace();
        }


        prefs_to_get = getSharedPreferences(MyPREFERENCES,0);   // sharedpreference for getting phone numbers

        phone1_num = prefs_to_get.getString(mact.phone1,null);
/*        phone2_num = prefs_to_get.getString(mact.phone2,null);
        phone3_num = prefs_to_get.getString(mact.phone3,null);*/


        if(checkPermission(Manifest.permission.SEND_SMS)) {
            check=true;
        }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        if(!TextUtils.isEmpty(phone1_num)){
            if((checkPermission(Manifest.permission.SEND_SMS))&& (sendmsg==true)){

                try {
//                    SmsManager smsmanager = SmsManager.getDefault();
//                    smsmanager.sendTextMessage(phone1_num,null,message,null,null);
                    sendSMS(phone1_num,message);
                    Log.e("SMS_class","able to send sms");
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            Log.e("SMS_class","permission to send sms is denied");
        }
    }

    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);
        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getApplicationContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String>parts = sms.divideMessage(message);
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        sms.sendMultipartTextMessage(phoneNumber,null,parts,sendList,deliverList);
    }

    private boolean checkPermission(String permission){
        int checkPermission = ContextCompat.checkSelfPermission(this,permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions, @NonNull int [] grantResults){
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                   check = true;
                }
                return;
            }
        }
    }
}
