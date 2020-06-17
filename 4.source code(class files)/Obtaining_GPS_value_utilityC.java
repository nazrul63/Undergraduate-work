package com.example.n1363l.final_project_try_006;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by N1363l on 25-09-17.
 */

public class Obtaining_GPS_value_utilityC {


    private static final String TAG  = "GPS_utility_class";
    SharedPreferences prefs_for_Location;
    public static final String LocationPrefs = "locationprefs";

    public static final String latitude = "latitude";
    public static final String longitude = "longitude";


    public static final String locationPreference = "f_p_prefs";
    SharedPreferences Location_prefs;
    public static final String bd_r_lon = "bd_longitude";
    public static final String bd_r_lati = "bd_latitude";
    public static final String bth_r_lon = "bth_longitude";
    public static final String bth_r_lati = "bth_latitude";
    public static final String stair_r_lon = "stair_longitude";
    public static final String stair_r_lati = "stair_latitude";

    private Context context;
    private LocationManager locationMangaer ;
    private LocationListener locationListener ;

    private static boolean flag ;

    double present_latitude ;
    double present_longitude ;

    static ArrayList<Double> latitude_array = new ArrayList<>();    // to store the latitude
    static ArrayList<Double> longitude_array = new ArrayList<>();   // to store the longitude

    String Address ;
    String Address1;
    String state ;
    String cityname ;
    String country ;
    static String s ;

    long start;
    long end;

    private MainActivity m_obj;

    public Boolean getFlag() {
        return flag;
    }
    public static void setFlag(boolean flag) {
        Obtaining_GPS_value_utilityC.flag = flag;
    }

    public Obtaining_GPS_value_utilityC()
    {

        prefs_for_Location = context.getSharedPreferences(LocationPrefs,0);  // 0 means private
        locationMangaer = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Obtaining_GPS_value_utilityC(Context context)
    {
        this.context = context;

        prefs_for_Location = context.getSharedPreferences(LocationPrefs,0);  // 0 means private
        locationMangaer = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        m_obj = new MainActivity();
    }



    public void get_Location() {
        Toast.makeText(context,"in GPS class", Toast.LENGTH_SHORT).show();

        flag = displayGpsStatus();
        if (flag) {

            Log.v(TAG, "getting gps ... wait");
            locationListener = new MyLocationListener();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                return;
            }
            locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);
            hnd_remove_gps_update.postDelayed(run_removegps,90000);

        } else {
            Log.v(TAG,"gps off -- show alert");
            alertbox("Gps Status!!", "Your GPS is: OFF");

            CountDownTimer ctimer = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    flag = displayGpsStatus();
                    if(flag)
                        get_Location();
                    else
                        Log.e(TAG,"couldn't have the permission");

                }
            };
            ctimer.start();
        }
    }

    Handler hnd_remove_gps_update = new Handler();
    Runnable run_removegps = new Runnable() {
        @Override
        public void run() {
            locationMangaer.removeUpdates(locationListener);
        }
    };

    public void stop_gps(){
        if(locationListener!= null)
            locationMangaer.removeUpdates(locationListener);
    }

    /* modified get_location method while fall*/
    public void get_Location_modified()
    {
        flag = displayGpsStatus();
        if(flag)
        {
            locationListener = new MyLocationListener();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                return;
            }
            locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locationListener);
            hnd_remove_gps_update.postDelayed(run_removegps,60000);

        }
        else
        {

        }

    }


    /*----Method to Check GPS is enable or disable ----- */
    public Boolean displayGpsStatus() {
        boolean gpsStatus = locationMangaer.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            Log.e(TAG,"gps is on");
            return true;

        } else {
            Log.e(TAG,"gps is off");
            return false;
        }
    }


    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(myIntent);
                                flag =true;
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

//            Log.e(TAG,"Location changed : Lat: " +loc.getLatitude()+ " Lng: " + loc.getLongitude());

            try {
                present_latitude = loc.getLatitude();
                present_longitude = loc.getLongitude();

                SharedPreferences.Editor editor = prefs_for_Location.edit();
                editor.putFloat(latitude,(float) present_latitude);
                editor.putFloat(longitude,(float)present_longitude);
                editor.commit();
                Log.e(TAG,"Pre_lt: "+present_latitude+" pre_long: "+present_longitude);

            } catch (Exception e) {
                e.printStackTrace();
            }

            String longitude = "Longitude: "+loc.getLongitude();
            String latitude = "Latitude: "+loc.getLatitude();

            try {
                latitude_array.add(loc.getLatitude());
                longitude_array.add(loc.getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
            }

   /*----------to get City-Name from coordinates ------------- */


            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<android.location.Address> address_list;
            try{
                address_list = gcd.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);

                Address = address_list.get(0).getAddressLine(0);
                Address1= address_list.get(0).getAddressLine(1);
                state = address_list.get(0).getAdminArea();
                cityname = address_list.get(0).getLocality();
                country = address_list.get(0).getCountryName();

            }catch (IOException e){
//                e.printStackTrace();
            }
//            s = longitude+"\n"+latitude+"\n\ncurrent address is: "+Address+Address1+"\ncity: "+cityname+"\nState : "+state+"\ncountry: "+country;
//            Log.e("gps_class",longitude+"\n"+latitude+"\n\ncurrent address is: "+Address+Address1+"\ncity: "+cityname+"\nState : "+state+"\ncountry: "+country);
            try {
                if(latitude!= null && longitude!= null)
                    s = s.concat("\n"+latitude+"\n"+longitude);
                if(Address!= null)
                    s = s.concat("\ncurrent address is: "+Address);
                if(Address1!= null)
                    s = s.concat(Address1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
