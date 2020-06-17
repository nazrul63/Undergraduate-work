package com.example.n1363l.final_project_try_006;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by N1363l on 13-07-17.
 */

public class Accelerometer_v_for_testing_utilityC_modified implements SensorEventListener {

    private Context context;
    private static final String TAG  = "aclr_utility_c_mod_cls";
     SensorManager new_s_manager;
     Sensor newAccelerometer;
     Sensor newMagnetometer;
    PowerManager new_P_manager;
    PowerManager.WakeLock new_wl;
    static boolean accelerometer_on = false;
    boolean gravity_on  = true;


    /* for storing position threshold value in sharedPrefs */
    SharedPreferences prefs_to_pos_threshold;
    public static final String Position_prefs = "SP_POSITION";

    public static final String x_sh = "x1";
    public static final String x_pn = "x2";
    public static final String y_sh = "y1";
    public static final String y_pn = "y2";
    public static final String z_sh = "z1";
    public static final String z_pn = "z2";

    /* for getting the stored avg dB of sound  */

    SharedPreferences prefs_for_avg_db_value;
    public static final String Avg_dBPrefs = "avg_dBPrefs";
    public static final String avg_dB_v = "avg_db";
    static double pre_latitude,pre_longitude;

    static float x=0.0f,y=0.0f,z=0.0f;
    static float azimut=0.0f,pitch=0.0f,roll=0.0f;

    float []X = new float[10000]; // to save the accelerometer x's value
    float []Y = new float[10000];
    float []Z = new float[10000];

    ArrayList<Float> A_X = new ArrayList<>();
    ArrayList<Float> A_Y = new ArrayList<>();
    ArrayList<Float> A_Z = new ArrayList<>();
    ArrayList<Float> A_t = new ArrayList<>();
    ArrayList<Float> A_t_dup = new ArrayList<>();
    ArrayList<Float>Triggering_A_t = new ArrayList<>();
    ArrayList<Double>Triggering_dB = new ArrayList<>();


    int i=0,j=0;
    float [] linear_acceleration={0.0f, 0.0f, 0.0f};
    float [] gravity={0.0f, 0.0f, 0.0f};
    float max_x,max_y,max_z,min_x,min_y,min_z,a_t,avg_A_t;
    float max_A_v,min_A_v;


    int phone_position =999;    // default / garbage phone position value

    float avg_avg=0;
    boolean done = true;
//    float dif;
    float A_v;
    ArrayList<Float>A_v_array = new ArrayList<>();

    float first_threshold  = 5.5f; /*  less than 1g */
    float first_threshold_limit = 1f;
    float second_threshold = (2f*9.8f);   /*  greater or equal to 2g */
    float third_threshold = 0.2f;

    boolean first_condition= false;
    boolean second_condition = false;
    boolean third_condition = false;

    int size_diff,t,f,c;
    int position_threshold =15;
    float x_th_s,y_th_s,z_th_s;
    float x_th_p,y_th_p,z_th_p;


    ArrayList<Float>A_t_diff_check = new ArrayList<>();

    float c_diff;
    static float previous_value;
    boolean save_value =true;
    boolean in_dif_check ;
    boolean interrupted_f = false;
    static boolean aclr_triggered;
    static boolean orientation_triggered;
    static boolean audio_triggered;
    static double Triggering_audio;

    long start_time,elapsed_time;
    long start_time_A_v ;
    long entry_time;
    Queue<Float> A_v_queue = new LinkedList<>();

    boolean in_filling;
    boolean first_time;
    float first_value;
    Queue<Float> A_v_diff_queue = new LinkedList<>();

    double present_avg_dB,during_fall_dB;
    double present_max_dB,during_fall_max_dB;

    private AudioDataReceivedListener ad_rcv_listener;
    RecordingThread rcd_thread;

    private Fall_handler_class fall_obj;
    private Obtaining_GPS_value_utilityC gps_obj;



    private static final String FILE_NAME = "project_file_at_v.csv";
    private static final String FILE_NAME2 = "project_file_dB_v.csv";
    private static final String FILE_NAME3 = "project_file_max_x.csv";
    private static final String FILE_NAME4 = "project_file_max_y.csv";
    private static final String FILE_NAME5 = "project_file_max_z.csv";
    private static final String FILE_NAME6 = "project_file_min_x.csv";
    private static final String FILE_NAME7 = "project_file_min_y.csv";
    private static final String FILE_NAME8 = "project_file_min_z.csv";
    private static final String FILE_NAME9 = "project_file_max_at.csv";
    private static final String FILE_NAME10 = "project_file_min_at.csv";
    private static final String FILE_NAME11 = "triggering_record.csv";




    FileOutputStream fileoutputstrm1 = null;
    FileOutputStream fileoutputstrm2 = null;
    FileOutputStream fileoutputstrm3 = null;
    FileOutputStream fileoutputstrm4 = null;
    FileOutputStream fileoutputstrm5 = null;
    FileOutputStream fileoutputstrm6 = null;
    FileOutputStream fileoutputstrm7 = null;
    FileOutputStream fileoutputstrm8 = null;
    FileOutputStream fileoutputstrm9 = null;
    FileOutputStream fileoutputstrm10 = null;
    FileOutputStream fileoutputstrm11 = null;
    File file ,file2,file3,file4,file5,file6,file7,file8,file9,file10,file11;

    String string_at  = "\n";
    static String string_dB = "\n";
    String string_max_x  = "\n";
    String string_max_y = "\n";
    String string_max_z = "\n";
    String string_min_x  = "\n";
    String string_min_y = "\n";
    String string_min_z = "\n";
    String string_max_at = "\n";
    String string_min_at  = "\n";
    String string_triggering_rcd = "\n";
    static String triggering_at_str;
    static String triggering_dB_str = "";
    private double present_dB_from_recorder;
    private String show_A_t;
    private boolean free_fall;

    public Accelerometer_v_for_testing_utilityC_modified(Context contxt){
        this.context = contxt;
        // for working even in locked srceen
        new_P_manager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        new_wl = new_P_manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"my lock");
        new_wl.acquire();

        new_s_manager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
        newAccelerometer = new_s_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        newMagnetometer = new_s_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    public void start_sensor_listening(){
        Log.v(TAG, "in sensor_listening method");
        fall_obj = new Fall_handler_class(context);
        gps_obj = new Obtaining_GPS_value_utilityC(context);

        prefs_to_pos_threshold = context.getSharedPreferences(Position_prefs,0);
        prefs_for_avg_db_value = context.getSharedPreferences(Avg_dBPrefs,0);

        if(!TextUtils.isEmpty(prefs_to_pos_threshold.getString(x_sh,null))&&!TextUtils.isEmpty(prefs_to_pos_threshold.getString(y_sh,null))&&!TextUtils.isEmpty(prefs_to_pos_threshold.getString(z_sh,null)))
        {
            x_th_s = Float.parseFloat(prefs_to_pos_threshold.getString(x_sh,null));
            y_th_s = Float.parseFloat(prefs_to_pos_threshold.getString(y_sh,null));
            z_th_s = Float.parseFloat(prefs_to_pos_threshold.getString(z_sh,null));
        }
        else
        {
            x_th_s = 10f;
            y_th_s = 10f;
            z_th_s = 10f;
        }
        if(!TextUtils.isEmpty(prefs_to_pos_threshold.getString(x_pn,null))&&!TextUtils.isEmpty(prefs_to_pos_threshold.getString(y_pn,null))&&!TextUtils.isEmpty(prefs_to_pos_threshold.getString(z_pn,null)))
        {
            x_th_p = Float.parseFloat(prefs_to_pos_threshold.getString(x_pn,null));
            y_th_p = Float.parseFloat(prefs_to_pos_threshold.getString(y_pn,null));
            z_th_p = Float.parseFloat(prefs_to_pos_threshold.getString(z_pn,null));
        }
        else
        {
            x_th_p = 15f;
            y_th_p = 10f;
            z_th_p = 15f;
        }

        /* for recording  */
        if (ad_rcv_listener == null){
            ad_rcv_listener = new AudioDataReceivedListener() {
                @Override
                public void onAudioDataReceived(short[] data) {
                }
            };
        }

        if(rcd_thread == null){
            rcd_thread = new RecordingThread(ad_rcv_listener,context);
            Log.e(TAG,"in the recorder initializer");
        }
        else{
            Log.e(TAG,"didn't go to in the recorder initializer");
        }

        new_s_manager.registerListener(this,newAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        new_s_manager.registerListener(this,newMagnetometer,SensorManager.SENSOR_DELAY_NORMAL);


        hnd_avg_A_t_calculator.postDelayed(run_avg_At_t,60000L);
       /* if recorder is not on start recorder and collect the result after 10 seconds */
        if (!rcd_thread.recording()) {
            rcd_thread.startRecording();
            hnd_stop_recording.postDelayed(run_stop_rcd,10000L);
        } else {
            rcd_thread.stopRecording();
            rcd_thread.startRecording();
            hnd_stop_recording.postDelayed(run_stop_rcd,10000L);
        }
        entry_time = System.nanoTime();
        first_time = true;
    }

    private File getFile(){
        return new File(Environment.getExternalStorageDirectory(),FILE_NAME);

    }
    private File getFile2(){
        return new  File(Environment.getExternalStorageDirectory(),FILE_NAME2);
    }
    private File getFile3(){
        return new File(Environment.getExternalStorageDirectory(),FILE_NAME3);

    }
    private File getFile4() {
        return new File(Environment.getExternalStorageDirectory(), FILE_NAME4);
    }
    private File getFile5(){
        return new File(Environment.getExternalStorageDirectory(),FILE_NAME5);

    }
    private File getFile6(){
        return new  File(Environment.getExternalStorageDirectory(),FILE_NAME6);
    }
    private File getFile7(){
        return new File(Environment.getExternalStorageDirectory(),FILE_NAME7);

    }
    private File getFile8(){
        return new  File(Environment.getExternalStorageDirectory(),FILE_NAME8);
    }
    private File getFile9(){
        return new File(Environment.getExternalStorageDirectory(),FILE_NAME9);

    }
    private File getFile10(){
        return new  File(Environment.getExternalStorageDirectory(),FILE_NAME10);
    }
    private File getFile11(){
        return new  File(Environment.getExternalStorageDirectory(),FILE_NAME11);
    }


    Handler hnd_aclr_bool = new Handler();
    Runnable run_aclr_bool = new Runnable() {
        @Override
        public void run() {
            aclr_triggered = false;
        }
    };

    Handler hnd_audio_bool = new Handler();
    Runnable run_audio_bool = new Runnable() {
        @Override
        public void run() {
            audio_triggered = false;
        }
    };

    Handler hnd_orien_bool = new Handler();
    Runnable run_orien_bool = new Runnable() {
        @Override
        public void run() {
            orientation_triggered = false;
        }
    };


    public void to_check() {
        if(aclr_triggered == true) {
            if(orientation_triggered == true){
                if(audio_triggered == true){
                    if(in_dif_check == false){
                        Log.e(TAG,"all three sensors are triggered !!!");
//                        Toast.makeText(context,"all three sensors are triggered !!!",Toast.LENGTH_SHORT).show();
                        fall_ck_handler.postDelayed(fall_ck_run, 30000L);    /* to check phone is in stationary condition? */
                        in_dif_check = true;
                        previous_value = a_t;
                        string_triggering_rcd = string_triggering_rcd.concat("\n");
                        string_triggering_rcd = string_triggering_rcd.concat(triggering_at_str);
                        string_triggering_rcd = string_triggering_rcd.concat("\t");
                        string_triggering_rcd = string_triggering_rcd.concat(triggering_dB_str);

                        gps_obj.get_Location();

                        hnd_audio_bool.postDelayed(run_audio_bool,5000L);
                        hnd_orien_bool.postDelayed(run_orien_bool,5000L);
                        hnd_aclr_bool.postDelayed(run_aclr_bool,5000L);
                    }
                    else {
                        Log.e(TAG,"all three sensors are triggered but fall is alreeady triggered ");
                        string_triggering_rcd = string_triggering_rcd.concat("\n");
                        string_triggering_rcd = string_triggering_rcd.concat(triggering_at_str);
                        string_triggering_rcd = string_triggering_rcd.concat("\t");
                        string_triggering_rcd = string_triggering_rcd.concat(triggering_dB_str);
//                        Toast.makeText(context,"all three sensors are triggered \nbut fall is alreeady triggered",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if(in_dif_check == false){
                        Log.e(TAG,"accelerometer & orientation sensors are triggered !!");
//                        Toast.makeText(context,"accelerometer & orientation sensors are triggered !!",Toast.LENGTH_SHORT).show();
                        fall_ck_handler.postDelayed(fall_ck_run, 30000L);     /*to check phone is in stationary condition? */
                        in_dif_check = true;
                        previous_value = a_t;
                        gps_obj.get_Location();

                        hnd_orien_bool.postDelayed(run_orien_bool,5000L);
                        hnd_aclr_bool.postDelayed(run_aclr_bool,5000L);
                    }
                    else
                        Log.e(TAG,"accelerometer & orientation sensors are triggered but fall is alreeady triggered ");
//                    Toast.makeText(context,"accelerometer & orientation sensors are triggered \nbut fall is alreeady triggered",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                if(audio_triggered == true){
                    if(in_dif_check == false){
                        Log.e(TAG,"accelerometer & audio sensors are triggered !!");
//                        Toast.makeText(context,"accelerometer & audio sensors are triggered !!",Toast.LENGTH_SHORT).show();
                        fall_ck_handler.postDelayed(fall_ck_run, 30000L);    /* to check phone is in stationary condition? */
                        in_dif_check = true;
                        previous_value = a_t;

                        string_triggering_rcd = string_triggering_rcd.concat("\n");
                        string_triggering_rcd = string_triggering_rcd.concat(show_A_t);
                        string_triggering_rcd = string_triggering_rcd.concat("\t B ");
                        string_triggering_rcd = string_triggering_rcd.concat(triggering_dB_str);
                        Log.e(TAG,string_triggering_rcd);
                        triggering_dB_str = "";
//                        hnd_start_gps.postDelayed(run_gp)
                        gps_obj.get_Location();

//                        hnd_audio_bool.postDelayed(run_audio_bool,1000L);
//                        hnd_aclr_bool.postDelayed(run_aclr_bool,1000L);
                        audio_triggered=false;
                        aclr_triggered = false;
                    }
                    else
                        Log.e(TAG,"accelerometer & audio sensors are triggered but fall is alreeady triggered");
                    string_triggering_rcd = string_triggering_rcd.concat("\n");
                    string_triggering_rcd = string_triggering_rcd.concat(triggering_at_str);
                    string_triggering_rcd = string_triggering_rcd.concat("\t D ");
                    string_triggering_rcd = string_triggering_rcd.concat(triggering_dB_str);
                    triggering_dB_str = "";
//                    Toast.makeText(context,"accelerometer & audio sensors are triggered but fall is alreeady triggered",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e(TAG,"only accelerometer sensor is triggered !");
//                    Toast.makeText(context,"only accelerometer sensor is triggered !",Toast.LENGTH_SHORT).show();
                    string_triggering_rcd = string_triggering_rcd.concat("\n");
                    string_triggering_rcd = string_triggering_rcd.concat(triggering_at_str);
                    string_triggering_rcd = string_triggering_rcd.concat("\t A ");
                    string_triggering_rcd = string_triggering_rcd.concat("->  "+rcd_thread.present_db);
                    hnd_aclr_bool.postDelayed(run_aclr_bool,2000L);
//                    aclr_triggered = false;
                }

            }
        }
        else{
            if(orientation_triggered == true){
                if(audio_triggered == true){
                    if(in_dif_check == false){
                        Log.e(TAG,"orientation & audio sensos are triggered !!");
//                        Toast.makeText(context,"orientation & audio sensos are triggered !!",Toast.LENGTH_SHORT).show();
                        fall_ck_handler.postDelayed(fall_ck_run, 30000L);     /*to check phone is in stationary condition? */
                        in_dif_check = true;
                        previous_value = a_t;
                        gps_obj.get_Location();

                        hnd_audio_bool.postDelayed(run_audio_bool,5000L);
                        hnd_orien_bool.postDelayed(run_orien_bool,5000L);
                    }
                    else
                        Log.e(TAG,"orientation & audio sensos are triggered but fall is alreeady triggered");
//                    Toast.makeText(context,"orientation & audio sensos are triggered but fall is alreeady triggered",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e(TAG,"only orientation sensor is triggered !");
//                    Toast.makeText(context,"only orientation sensor is triggered !",Toast.LENGTH_SHORT).show();
                    hnd_orien_bool.postDelayed(run_orien_bool,1000L);
                }
            }
            else{
                if(audio_triggered == true){
                    Log.e(TAG,"only audio sensor is triggered !");
//                    Toast.makeText(context,"only audio sensor is triggered !",Toast.LENGTH_SHORT).show();
//                    hnd_audio_bool.postDelayed(run_audio_bool,1000L);
                    audio_triggered = false;
                    string_triggering_rcd = string_triggering_rcd.concat("\n");
                    string_triggering_rcd = string_triggering_rcd.concat(show_A_t);
                    string_triggering_rcd = string_triggering_rcd.concat("\t R ");
                    string_triggering_rcd = string_triggering_rcd.concat(triggering_dB_str);
                    triggering_dB_str = "";
                }
                else{
                    Log.e(TAG,"should not come to this point- no sensor is triggered !!!!");
//                    Toast.makeText(context,"should not come to this point- no sensor is triggered !!!!",Toast.LENGTH_SHORT).show();
                }

            }

        }

    }



    Handler hnd_start_recording = new Handler();
    Runnable run_start_rcd = new Runnable() {
        @Override
        public void run() {
            /* start the recording */
            if (!rcd_thread.recording()) {
                rcd_thread.startRecording();
                hnd_stop_recording.postDelayed(run_stop_rcd,10000L);
            }
        }
    };


    Handler hnd_stop_recording = new Handler();
    Runnable run_stop_rcd = new Runnable() {
        @Override
        public void run() {
            if(interrupted_f == true ){
                rcd_thread.stopRecording();
                during_fall_dB = rcd_thread.db;
                during_fall_max_dB = rcd_thread.db_max_exp;
//                Toast.makeText(context,"falling_dB = "+during_fall_dB+"\nfalling_max_exp_db = "+during_fall_max_dB,Toast.LENGTH_LONG).show();
                hnd_start_recording.removeCallbacks(run_start_rcd);
            }
            else{
                rcd_thread.stopRecording();
                present_avg_dB = rcd_thread.db;
                present_max_dB = rcd_thread.db_max_exp;
//                Toast.makeText(context,"pres_dB = "+present_avg_dB+"\npres_max_exp_db = "+present_max_dB,Toast.LENGTH_LONG).show();
                hnd_start_recording.postDelayed(run_start_rcd,1000L);
            }

        }
    };

    /* code to check fall following on first thereshold */
    Handler fall_ck_handler = new Handler();
    Runnable fall_ck_run = new Runnable() {
        @Override
        public void run() {


            size_diff = A_t_diff_check.size();
            t=0 ;
            f=0;
            for(c=0;c<size_diff;c++){
                if(A_t_diff_check.get(c)<=third_threshold){
                   // fall_condition = true;
                    t++;
                }
                else{
                    //fall_condition = false;
                    f++;
                }
            }
            Log.e(TAG,"size: "+size_diff+" t: "+t+" f: "+f);
            second_condition = false;
            in_dif_check= false;

            A_t_diff_check.clear();

            if(t>f){
                Log.e(TAG,"going to the fall_handler class");
                Toast.makeText(context,"condition fullfilled , going to fall handler class",Toast.LENGTH_LONG).show();

                stop_acclr_class_activity();
                interrupted_f = true;
                fall_obj.to_do_modified();
            }
            else{
                Log.e(TAG,"false triggering !! ");
                Toast.makeText(context,"false triggering",Toast.LENGTH_LONG).show();
                if(!rcd_thread.recording()){
                    rcd_thread.startRecording();
                    interrupted_f = false;
                    hnd_stop_recording.postDelayed(run_stop_rcd,10000L);
                }
            }
        }
    };

    public void stop_microphone(){
        if(rcd_thread.recording()){
            rcd_thread.stopRecording();
            hnd_start_recording.removeCallbacks(run_start_rcd);
            hnd_stop_recording.removeCallbacks(run_stop_rcd);
            Log.e(TAG,"removed recorder");
        }
    }

    public void stop_handlers(){
        hnd_avg_A_t_calculator.removeCallbacks(run_avg_At_t);
        a_t_v_save_hand.removeCallbacks(at_v_save_run);
        new_handler.removeCallbacks(new_run);
        fall_ck_handler.removeCallbacks(fall_ck_run);
    }

    public  void stop_accelerometer(){
        if(new_s_manager!=null)
            new_s_manager.unregisterListener(this);
    }


    public void stop_acclr_class_activity(){
        stop_accelerometer();
        stop_microphone();
        stop_handlers();

        try{
                file = getFile();
                file2 = getFile2();
                file3 = getFile3();
                file4 = getFile4();
                file5 = getFile5();
                file6 = getFile6();
                file7 = getFile7();
                file8 = getFile8();
                file9 = getFile9();
                file10 = getFile10();
                file11 = getFile11();

            fileoutputstrm1 = new FileOutputStream(file,true);
            fileoutputstrm2 = new FileOutputStream(file2,true);
            fileoutputstrm3 = new FileOutputStream(file3,true);
            fileoutputstrm4 = new FileOutputStream(file4,true);
            fileoutputstrm5 = new FileOutputStream(file5,true);
            fileoutputstrm6 = new FileOutputStream(file6,true);
            fileoutputstrm7 = new FileOutputStream(file7,true);
            fileoutputstrm8 = new FileOutputStream(file8,true);
            fileoutputstrm9 = new FileOutputStream(file9,true);
            fileoutputstrm10 = new FileOutputStream(file10,true);
            fileoutputstrm11 = new FileOutputStream(file11,true);

            fileoutputstrm1.write(string_at.getBytes());
            fileoutputstrm2.write(string_dB.getBytes());
            fileoutputstrm3.write(string_max_x.getBytes());
            fileoutputstrm4.write(string_max_y.getBytes());
            fileoutputstrm5.write(string_max_z.getBytes());
            fileoutputstrm6.write(string_min_x.getBytes());
            fileoutputstrm7.write(string_min_y.getBytes());
            fileoutputstrm8.write(string_min_z.getBytes());
            fileoutputstrm9.write(string_max_at.getBytes());
            fileoutputstrm10.write(string_min_at.getBytes());
            fileoutputstrm11.write(string_triggering_rcd.getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                fileoutputstrm1.close();
                fileoutputstrm2.close();
                fileoutputstrm3.close();
                fileoutputstrm4.close();
                fileoutputstrm5.close();
                fileoutputstrm6.close();
                fileoutputstrm7.close();
                fileoutputstrm8.close();
                fileoutputstrm9.close();
                fileoutputstrm10.close();
                fileoutputstrm11.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    Handler a_t_v_save_hand = new Handler();
    Runnable at_v_save_run = new Runnable() {
        @Override
        public void run() {
           save_value = true;   /* for triggering to save value of A_t for detecting phone position */
        }
    };

    Handler hnd_avg_A_t_calculator = new Handler();
    Runnable run_avg_At_t = new Runnable() {
        @Override
        public void run() {
            avg_avg = (float) calculateAverage(A_t_dup);
            Log.v(TAG, "avg_avg= "+avg_avg);
            Toast.makeText(context,"avg_avg= "+avg_avg,Toast.LENGTH_LONG).show();

            done = true;
            save_value = false;
            A_t_dup.clear();

            a_t_v_save_hand.postDelayed(at_v_save_run,240000L);
            hnd_avg_A_t_calculator.postDelayed(run_avg_At_t,300000L);
        }
    };

    private double calculateAverage(List<Float> marks) {
        Float sum = 0f;
        if(!marks.isEmpty()) {
            for (Float mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private double calculate_sum(Queue<Float> marks){
        Float sum = 0f;
        if(!marks.isEmpty()) {
            for (Float mark : marks) {
                sum += mark;
            }
            return sum.doubleValue();
        }
        return sum.doubleValue();
    }



    // code to set the timer to check the average after 30000L i.e. 30 seconds but it is called in onSensorChanged method
    Handler new_handler = new Handler();
    Runnable new_run = new Runnable() {
        @Override
        public void run() {
            j=i;

            max_x = Collections.max(A_X);
            max_y = Collections.max(A_Y);
            max_z = Collections.max(A_Z);
            min_x = Collections.min(A_X);
            min_y = Collections.min(A_Y);
            min_z = Collections.min(A_Z);
            String max_x_s = String.format(Locale.US,"%.2f",max_x);
            String max_y_s = String.format(Locale.US,"%.2f",max_y);
            String max_z_s = String.format(Locale.US,"%.2f",max_z);
            String min_x_s = String.format(Locale.US,"%.2f",min_x);
            String min_y_s = String.format(Locale.US,"%.2f",min_y);
            String min_z_s = String.format(Locale.US,"%.2f",min_z);

            string_max_x = string_max_x.concat("\n");
            string_max_x = string_max_x.concat(max_x_s);
            string_max_y = string_max_y.concat("\n");
            string_max_y = string_max_y.concat(max_y_s);
            string_max_z = string_max_z.concat("\n");
            string_max_z = string_max_z.concat(max_z_s);
            string_min_x = string_min_x.concat("\n");
            string_min_x = string_min_x.concat(min_x_s);
            string_min_y = string_min_y.concat("\n");
            string_min_y = string_min_y.concat(min_y_s);
            string_min_z = string_min_z.concat("\n");
            string_min_z = string_min_z.concat(min_z_s);

            i=0;

            if((Math.abs(max_x-min_x)<=x_th_s)&&(Math.abs(max_y-min_y)<=y_th_s)&&(Math.abs(max_z-min_z)<=z_th_s))
            {
                /* phone is in shirt's pocket */
                int previous_pos = phone_position;
                phone_position = 1;
                Log.e(TAG,"phone is in shirt's pocket");
                Toast.makeText(context,"the minimum value of A_t : "+Collections.min(A_t),Toast.LENGTH_LONG).show();
                if(phone_position!=previous_pos)
                    second_threshold = 2f*9.8f;

            }
            else if((Math.abs(max_x-min_x)<=x_th_p)&&(Math.abs(max_y-min_y)<=y_th_p)&&(Math.abs(max_z-min_z)<=z_th_p))
            {
                /* phone is in pant's pocket */
                int previous_pos = phone_position;
                phone_position = 2;
                Log.e(TAG,"phone is in pant's pocket");
                Toast.makeText(context,"the minimum value of A_t : "+Collections.min(A_t),Toast.LENGTH_LONG).show();
                if(phone_position!=previous_pos)
                    second_threshold = 15f; /* have to change at last */
            }
            else{
                phone_position = 2;
                Log.e(TAG,"not within the phone position range");
                Toast.makeText(context,"the minimum value of A_t : "+Collections.min(A_t),Toast.LENGTH_LONG).show();
                second_threshold = 15f; /* have to change at last */

            }


            float max_at = Collections.max(A_t);
            float min_at = Collections.min(A_t);
            String max_a_t_str = String.format(Locale.US,"%.2f",max_at);
            String min_a_t_str = String.format(Locale.US,"%.2f",min_at);

            string_max_at = string_max_at.concat("\n");
            string_max_at = string_max_at.concat(max_a_t_str);
            string_min_at = string_min_at.concat("\n");
            string_min_at = string_min_at.concat(min_a_t_str);

            A_X.clear();
            A_Y.clear();
            A_Z.clear();
            A_t.clear();
        }
    };

    Handler hnd_A_v = new Handler();
    Runnable run_A_v = new Runnable() {
        @Override
        public void run() {
            if(Math.abs(Collections.max(A_v_queue)-Collections.min(A_v_queue))<2){
                orientation_triggered = true;
                to_check();
            }
        }
    };

    Handler hnd_free_fall = new Handler();
    Runnable run_free_fall = new Runnable() {
        @Override
        public void run() {
            free_fall = false;
        }
    };



    @Override
    public void onSensorChanged(SensorEvent event) {
//        float[] mGravity = null;
//        float[] mGeomagnetic = null;

        if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){

            accelerometer_on=true;
//            mGravity = event.values;

            if(gravity_on)
            {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                X[i]= x;
                Y[i]= y;
                Z[i]= z;
                A_X.add(x);
                A_Y.add(y);
                A_Z.add(z);

             /*   if(a_t_indicator==0)
                    a_t_handler.postDelayed(a_t_run,15000L);
                a_t_indicator++;*/

                if(i==0)
                    new_handler.postDelayed(new_run,30000L);   // sending to calculate the average of the accelerometer's value
                i++;

                a_t  = (float) Math.sqrt(Math.pow(x,2)+ Math.pow(y,2)+ Math.pow(z,2));
                A_t.add(a_t);

                show_A_t = String.format(Locale.US,"%.2f",a_t);
                string_at = string_at.concat("\n");
                string_at = string_at.concat(show_A_t);

                if(save_value)
                    A_t_dup.add(a_t);

                if(audio_triggered)
                    to_check();

                if(a_t<first_threshold_limit){
                    double seconds = (double) (System.nanoTime() - start_time) / 1000000000.0;
                    free_fall = true;
                    if (seconds <= 3 ){
                        gps_obj.stop_gps();
                        fall_ck_handler.removeCallbacks(fall_ck_run);
                        Log.e(TAG,"stopped the handler***********************");
                    }
                    else
                        Log.e(TAG,"couldn't stop the handler ***************** more time elapsed");

                    hnd_free_fall.postDelayed(run_free_fall,2000);
                }
                if(a_t<=first_threshold && a_t>=first_threshold_limit ){
                    if(free_fall == false){
                        aclr_triggered = true;
                        Triggering_A_t.add(a_t);
                        triggering_at_str = String.format(Locale.US,"%.2f",a_t);
                        start_time = System.nanoTime();
//                      first_condition = true;
                        Log.e(TAG,"accelerometer triggered fall for value : "+a_t);
                        to_check();
                    }
                    else
                        Log.e(TAG,"just went to almost zero acceleration************");

                }

                if((a_t>=second_threshold)) {
                    double seconds = (double) (System.nanoTime() - start_time) / 1000000000.0;
                    if (seconds <= 1 )
                        second_condition = true;

                }

                if(audio_triggered)
                    to_check();

                if(a_t<first_threshold_limit){
                    double seconds = (double) (System.nanoTime() - start_time) / 1000000000.0;
                    free_fall = true;
                    if (seconds <= 3 )
                    {
                        gps_obj.stop_gps();
                        fall_ck_handler.removeCallbacks(fall_ck_run);
                        Log.e(TAG,"stopped the handler***********************");
                    }
                    else
                        Log.e(TAG,"couldn't stop the handler ***************** more time elapsed");

                    hnd_free_fall.postDelayed(run_free_fall,2000);
                }

/*
                present_dB_from_recorder = rcd_thread.present_db;
                String show_dB = String.format(Locale.US,"%2f",present_dB_from_recorder);
                string_dB = string_dB.concat("\n");
                string_dB = string_dB.concat(show_dB);*/


                if(in_dif_check == true)
                {
                    A_t_diff_check.add(Math.abs(previous_value-a_t));
                    previous_value = a_t;
                }

            }
            //else if(new_check_B_choice.isChecked()==false)
            else
            {
                //this is to have the accelerometers value without the gravities value i.e this will show only device's acceleration
                final float alpha = 0.8f;

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                x = linear_acceleration[0];
                y = linear_acceleration[1];
                z = linear_acceleration[2];

            }
        }
        if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD)
        {
//            mGeomagnetic = event.values;
            Log.e(TAG,"++++++++++++++++++++++++++++++++++++++++++===have magnetometer+++++++++++++++++++++++++++++++++");
        }

/*        if(mGravity!= null && mGeomagnetic!=null)
        {
            float R_data[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R_data, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R_data, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                pitch = orientation[1];
                roll = orientation[2];
                Log.e(TAG,"azimuth : "+azimut+"\npitch : "+pitch+"\nroll : "+roll);

                A_v= (Math.abs(x*azimut+y*pitch+z*roll));
//                A_v_array.add(A_v);


                *//* filling the queue *//*
                if(((double) (System.nanoTime() - start_time) / 1000000000.0)<=4){
                    A_v_queue.add(A_v);
                    in_filling = true;
                    if(first_time == true){
                        first_time = false;
                        first_value = A_v;
                    }
                    else{
                        try{
                            A_v_diff_queue.add(Math.abs(A_v - first_value));
                            first_value = A_v;
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    in_filling = false;
                    A_v_queue.remove();
                    A_v_queue.add(A_v);
                    A_v_diff_queue.remove();
                    A_v_diff_queue.add(Math.abs(A_v - first_value));
                    first_value = A_v;
                }

                if(in_filling == false){
                    if(calculate_sum(A_v_diff_queue)>50){
                        hnd_A_v.postDelayed(run_A_v,4000L);
                    }

                }
                *//*
                if(A_v>6)
                {
                    orientation_triggered = true;
                    to_check();
                }*//*
            }
        }*/
/*        else if(mGeomagnetic == null && mGravity == null)
            Log.e(TAG,"both are null = aclr+ magetometer");
        else if(mGravity== null)
            Log.e(TAG," mgravity is null - acclr");

        else if(mGeomagnetic == null)
            Log.e(TAG,"mGeometric is null -  magentometer");*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
