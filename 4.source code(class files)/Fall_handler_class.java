package com.example.n1363l.final_project_try_006;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.Locale;

/**
 * Created by N1363l on 12-10-17.
 */

public class Fall_handler_class {
    Context context;
    private static final String TAG  = "Fall_handler_class";

    /* for getting present gps value from sharedPrefs */
    SharedPreferences prefs_to_get_Location;
    public static final String LocationPrefs = "locationprefs";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";


    SharedPreferences prefs_for_db_value;
    public static final String dBPrefs = "dBPrefs";
    public static final String dB_v = "db";
    public static final String max_dB = "maxdb";


    SharedPreferences prefs_for_avg_db_value;
    public static final String Avg_dBPrefs = "avg_dBPrefs";
    public static final String avg_dB_v = "avg_db";

    public static final String locationPreference = "f_p_prefs";
    SharedPreferences Location_prefs;
    public static final String bd_r_lon = "bd_longitude";
    public static final String bd_r_lati = "bd_latitude";
    public static final String bth_r_lon = "bth_longitude";
    public static final String bth_r_lati = "bth_latitude";
    public static final String stair_r_lon = "stair_longitude";
    public static final String stair_r_lati = "stair_latitude";

    double pre_latitude,pre_longitude;
    double post_latitude,post_longitude;
    double diff_latitude=999,diff_longitude=999;
    double diff_bd_lati,diff_bd_longi;
    double diff_bth_lati,diff_bth_longi;
    double diff_str_lati,diff_str_longi;


//    private Accelerometer_v_for_testing_utilityC aclr_obj;
    private Obtaining_GPS_value_utilityC gps_obj;
//    private Sound_recorder recorder_obj;
    float db_v,db_max;
    double avg_db,during_fall_dB_v,diff_dB_v,diff_max_v;
    String falling_place;

    public Fall_handler_class(Context contxt)
    {
        this.context = contxt;
    }
    // to check the gps diff and audio value after 15 s
    Handler check_handler = new Handler();
    Runnable diff_check_run = new Runnable() {
        @Override
        public void run() {

            gps_obj.get_Location_modified();

            try {
                post_latitude = (double)prefs_to_get_Location.getFloat(latitude,0.0f);
                post_longitude = (double)prefs_to_get_Location.getFloat(longitude,0.0f);

/*                diff_latitude = Math.abs(post_latitude-pre_latitude);
                diff_longitude = Math.abs(post_longitude-pre_longitude);
                Log.v(TAG,"diff: "+diff_latitude+"\n"+diff_longitude);*/

                String post_latitude_s = String.format(Locale.US,"%.4f",post_latitude);
                String post_longitude_s = String.format(Locale.US,"%.4f",post_longitude);


                Location_prefs = context.getSharedPreferences(locationPreference, Context.MODE_PRIVATE);

                if(post_latitude_s.equals(Location_prefs.getString(bd_r_lati,null))&&post_longitude_s.equals(Location_prefs.getString(bd_r_lon,null)))
                    falling_place = "bedroom";
                else if (post_latitude_s.equals(Location_prefs.getString(bth_r_lati,null))&&post_longitude_s.equals(Location_prefs.getString(bth_r_lon,null)))
                    falling_place = "bathroom";
                else if (post_latitude_s.equals(Location_prefs.getString(stair_r_lati,null))&&post_longitude_s.equals(Location_prefs.getString(stair_r_lon,null)))
                    falling_place = "staircase";
                else
                    falling_place="outside of home";

            } catch (Exception e) {
                e.printStackTrace();
                /* gps is off so setting the diff = 0 */
            }

            to_call_modified();
        }
    };


/*    public void to_do(double pres_dB, double fall_db,double pres_max_db, double fall_max_db)
    {
        Log.v(TAG, "in to_do method");
        Toast.makeText(context,"in fall handler class ,checking conditions",Toast.LENGTH_LONG).show();

        aclr_obj = new Accelerometer_v_for_testing_utilityC(context);
        gps_obj = new Obtaining_GPS_value_utilityC(context);

        diff_dB_v = fall_db - pres_dB;
        diff_max_v = fall_max_db - pres_max_db;
        Log.e(TAG,"avg_dB :"+pres_dB+"  max_dB : "+pres_max_db+"\nfall_dB : "+fall_db+" fall_max_dB : "+fall_max_db);

        check_handler.postDelayed(diff_check_run,10000L);
    }*/

    public void to_do_modified()
    {
        Log.v(TAG, "in to_do method");
//        Toast.makeText(context,"in fall handler class ,checking conditions",Toast.LENGTH_LONG).show();

//        aclr_obj = new Accelerometer_v_for_testing_utilityC(context);
        gps_obj = new Obtaining_GPS_value_utilityC(context);

//        diff_dB_v = fall_db - pres_dB;
//        diff_max_v = fall_max_db - pres_max_db;
//        Log.e(TAG,"avg_dB :"+pres_dB+"  max_dB : "+pres_max_db+"\nfall_dB : "+fall_db+" fall_max_dB : "+fall_max_db);

        check_handler.postDelayed(diff_check_run,1000L);
    }

/*    public void to_call(){
        *//* jei condition e jak na keno listener gula close korbe *//*


        if(diff_dB_v>=1 || diff_max_v>=10){
            Toast.makeText(context,"condition fullfilled , alarm will start",Toast.LENGTH_LONG).show();
            Log.e(TAG,"found audio diff "+diff_dB_v);
            Intent intent = new Intent(context,Alarm_starter.class);
            intent.putExtra("fall_position",falling_place);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else{   *//*       don't sound the alarm
                  go to the monitoring phase *//*

            Toast.makeText(context,"sorry! no fall. dB diff : "+diff_dB_v,Toast.LENGTH_LONG).show();
            Log.e(TAG,"sorry! no fall. dB diff : "+diff_dB_v);
            Intent intent = new Intent(context,Comp_T_or_start_M.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }*/

    public void to_call_modified(){
        /* jei condition e jak na keno listener gula close korbe */

//
//        if(diff_dB_v>=1 || diff_max_v>=10){
//            Toast.makeText(context,"condition fullfilled , alarm will start",Toast.LENGTH_LONG).show();
//            Log.e(TAG,"found audio diff "+diff_dB_v);
            Intent intent = new Intent(context,Alarm_starter.class);
            intent.putExtra("fall_position",falling_place);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//        }
/*        else{   *//*       don't sound the alarm
                  go to the monitoring phase *//*

            Toast.makeText(context,"sorry! no fall. dB diff : "+diff_dB_v,Toast.LENGTH_LONG).show();
            Log.e(TAG,"sorry! no fall. dB diff : "+diff_dB_v);
            Intent intent = new Intent(context,Comp_T_or_start_M.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }*/
    }
}
