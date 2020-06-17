package com.example.n1363l.final_project_try_006;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by N1363l on 16-10-17.
 */

public class Acclerometer_v_for_training extends Activity implements SensorEventListener{

    Context context;
    private static final String TAG  = "aclr_training";
    private SensorManager new_s_manager;
    private Sensor newAccelerometer;
    PowerManager new_P_manager;
    PowerManager.WakeLock new_wl;


    /* for storing position threshold value in sharedPrefs */
    SharedPreferences prefs_to_pos_threshold;
    public static final String Position_prefs = "SP_POSITION";

    public static final String x_sh = "x1";
    public static final String x_pn = "x2";
    public static final String y_sh = "y1";
    public static final String y_pn = "y2";
    public static final String z_sh = "z1";
    public static final String z_pn = "z2";


    int ph_position=999;
    int training_time,i=0;

    float []X = new float[10000]; // to save the accelerometer x's value
    float []Y = new float[10000];
    float []Z = new float[10000];
    static float x=0.0f,y=0.0f,z=0.0f;
    double training_avg;

    ArrayList<Float> A_X = new ArrayList<>();
    ArrayList<Float> A_Y = new ArrayList<>();
    ArrayList<Float> A_Z = new ArrayList<>();
    ArrayList<Float> A_t = new ArrayList<>();
    ArrayList<Float> A_t_avg = new ArrayList<>();
    ArrayList<Float> A_t_dup = new ArrayList<>();
    ArrayList<Float>x_diff = new ArrayList<>();
    ArrayList<Float>y_diff = new ArrayList<>();
    ArrayList<Float>z_diff = new ArrayList<>();

    String pos;
    float max_x,max_y,max_z,min_x,min_y,min_z,a_t,avg_A_t;
    TextView txt_show_status,txt_show_remainig_time;
    private Button btn_m,btn_t,btn;
    private LinearLayout ll1;
    //Pending intent instance
    private PendingIntent pendingIntent;
    //Alarm Request Code
    private static final int ALARM_REQUEST_CODE = 133;



  /*  public Acclerometer_v_for_training (Context contxt)
    {
        this.context = contxt;
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_training);

        btn_m = (Button)findViewById(R.id.button_go_monitoring);
        btn_t = (Button)findViewById(R.id.button_go_training);

        ll1 = (LinearLayout)findViewById(R.id.linearLayout_01);
        txt_show_status = (TextView)findViewById(R.id.textView_show_training_status);
        txt_show_remainig_time = (TextView)findViewById(R.id.textView_show_training_time_rmn);

        // for working even in locked srceen
        if(ll1.getVisibility()==View.VISIBLE)
            ll1.setVisibility(View.INVISIBLE);

        new_P_manager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        new_wl = new_P_manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"my lock");
        new_wl.acquire();

        new_s_manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        newAccelerometer = new_s_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

/* from below was the start_sensor_listening method */

        Intent intent = getIntent();
        ph_position = intent.getIntExtra("ph_position",0);
        training_time = intent.getIntExtra("interval",0);
        Log.e(TAG,""+ph_position+" ; "+training_time);

        if(ph_position ==1)
            pos = "Shirt's pocket";
        else if(ph_position == 2)
            pos = "Pant's pocket";


        txt_show_status.setText("System will go under Training for "+training_time+"s\n \n");
        txt_show_remainig_time.setText("Put your phone in your "+pos+" within 30s");

        Log.v(TAG, "in Accelerometer training class for training "+training_time+"s");
        new_s_manager.registerListener(this,newAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        handler_4_stp_trn.postDelayed(run_trn_stopper,training_time*1000);

        CountDownTimer c_timer_srt_trn = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txt_show_status.setText("System will go under Training for "+training_time+"s\n \n");
                txt_show_remainig_time.setText("Put your phone in your "+pos+" within "+millisUntilFinished/1000+"s"+"\n\nAn alarm will be ringing to indicate the complition of training");
            }

            @Override
            public void onFinish() {
                start_training(training_time);
            }
        };
        c_timer_srt_trn.start();
    }

    public void start_training(final int training_time){
    final CountDownTimer c_timer = new CountDownTimer(training_time*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            txt_show_status.setText("Under Training\n\n");
            txt_show_remainig_time.setText("Remaining time : "+(millisUntilFinished/1000)+" s");
        }

        @Override
        public void onFinish() {
            float x_max_range= Collections.max(x_diff);
            float y_max_range= Collections.max(y_diff);
            float z_max_range= Collections.max(z_diff);

            String x_max_str = Float.toString(x_max_range);
            String y_max_str = Float.toString(y_max_range);
            String z_max_str = Float.toString(z_max_range);


            prefs_to_pos_threshold = getSharedPreferences(Position_prefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs_to_pos_threshold.edit();
            if(ph_position==1)
            {
                Log.e(TAG,"in shirt pocket");
                editor.putString(x_sh,x_max_str);
                editor.putString(y_sh,y_max_str);
                editor.putString(z_sh,z_max_str);
            }
            else if(ph_position==2)
            {
                Log.e(TAG,"in pant pocket");
                editor.putString(x_pn,x_max_str);
                editor.putString(y_pn,y_max_str);
                editor.putString(z_pn,z_max_str);
            }
            else
                Log.e(TAG,"couldn't go to any logic");
            editor.commit();

            ll1.setVisibility(View.VISIBLE);
            String trainig_avg_str = String.format(Locale.US,"%.2f",training_avg);

            if(ph_position == 1){
                txt_show_status.setText("***   Congratulations   ***\n ");
                txt_show_remainig_time.setText("Successfully completed the training\n\nPhone position : Shirt's pocket\nAcceleration Average : "+trainig_avg_str+"\nMax value in X axis : "+x_max_str+"\nMax value in Y axis : "+y_max_str+"\nMax value in Z axis : "+z_max_str);
            }
            else{
                txt_show_status.setText("***   Congratulations   ***\n");
                txt_show_remainig_time.setText("Successfully completed the training\n\nPhone position : Pant's pocket\nAcceleration Average : "+trainig_avg_str+"\nMax value in X axis : "+x_max_str+"\nMax value in Y axis : "+y_max_str+"\nMax value in Z axis : "+z_max_str);
            }

             /* Retrieve a PendingIntent that will perform a broadcast */
            Intent alarmIntent = new Intent(Acclerometer_v_for_training.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(Acclerometer_v_for_training.this, ALARM_REQUEST_CODE, alarmIntent, 0);

            triggerAlarmManager(5);


            btn_t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            btn_m.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(getApplicationContext(),Comp_T_or_start_M.class);
                    startActivity(intent1);
                    finish();
                }
            });



           /* Intent intent = new Intent(context,Comp_T_or_start_M.class);
            intent.putExtra("phone_pos",ph_position);
            intent.putExtra("average",training_avg);
            intent.putExtra("indicator",1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*/


        }
    };
    c_timer.start();
    }
    /*
    Handler handler_4_stp_trn = new Handler();
    Runnable run_trn_stopper = new Runnable() {
        @Override
        public void run() {
            float x_max_range= Collections.max(x_diff);
            float y_max_range= Collections.max(y_diff);
            float z_max_range= Collections.max(z_diff);

            String x_max_str = Float.toString(x_max_range);
            String y_max_str = Float.toString(y_max_range);
            String z_max_str = Float.toString(z_max_range);


            prefs_to_pos_threshold = getSharedPreferences(Position_prefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs_to_pos_threshold.edit();
            if(ph_position==1)
            {
                Log.e(TAG,"in shirt pocket");
                editor.putString(x_sh,x_max_str);
                editor.putString(y_sh,y_max_str);
                editor.putString(z_sh,z_max_str);
            }
            else if(ph_position==2)
            {
                Log.e(TAG,"in pant pocket");
                editor.putString(x_pn,x_max_str);
                editor.putString(y_pn,y_max_str);
                editor.putString(z_pn,z_max_str);
            }
            else
                Log.e(TAG,"couldn't go to any logic");
            editor.commit();

            ll1.setVisibility(View.VISIBLE);

            if(ph_position == 1)
                txt_show_status.setText("***   Congratulations   ***\n successfully completed the training" +
                        "\n\nPhone position : Shirt's pocket\nAcceleration Avg: "+training_avg);
            else
                txt_show_status.setText("***   Congratulations   ***\n successfully completed the training" +
                        "\n\nPhone position : Pant's pocket\nAcceleration Avg   : "+training_avg);

             *//* Retrieve a PendingIntent that will perform a broadcast *//*
            Intent alarmIntent = new Intent(Acclerometer_v_for_training.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(Acclerometer_v_for_training.this, ALARM_REQUEST_CODE, alarmIntent, 0);

            triggerAlarmManager(5);


            btn_t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            btn_m.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    *//*Intent intent1 = new Intent(getApplicationContext(),Comp_T_or_start_M.class);
                    startActivity(intent1);
                    finish();*//*
                }
            });



           *//* Intent intent = new Intent(context,Comp_T_or_start_M.class);
            intent.putExtra("phone_pos",ph_position);
            intent.putExtra("average",training_avg);
            intent.putExtra("indicator",1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*//*
        }
    };*/

    //Trigger alarm manager with entered time interval
    public void triggerAlarmManager(int alarmTriggerTime) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
        manager.set(AlarmManager.RTC_WAKEUP, (alarmTriggerTime * 1000), pendingIntent);//sst alarm manager with entered timer by converting into milliseconds

//        Toast.makeText(this, "Alarm Set for " + alarmTriggerTime + " seconds.", Toast.LENGTH_SHORT).show();

        final CountDownTimer count_alrm_time = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                    stopAlarmManager();
                //Toast.makeText(getApplication(),"stopped by countdown \n button clicked: "+cancel_button_clicked,Toast.LENGTH_SHORT).show();
            }
        };
        count_alrm_time.start();
    }

    //Stop/Cancel alarm manager
    public void stopAlarmManager() {

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent


        //Stop the Media Player Service to stop sound
        stopService(new Intent(Acclerometer_v_for_training.this, AlarmSoundService.class));

        //remove the notification from notification tray
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmNotificationService.NOTIFICATION_ID);
    }

    // code to set the timer to check the average after 30000L i.e. 30 seconds but it is called in onSensorChanged method
    Handler new_handler = new Handler();
    Runnable new_run = new Runnable() {
        @Override
        public void run() {

            max_x = Collections.max(A_X);
            max_y = Collections.max(A_Y);
            max_z = Collections.max(A_Z);
            min_x = Collections.min(A_X);
            min_y = Collections.min(A_Y);
            min_z = Collections.min(A_Z);

            x_diff.add(Math.abs(max_x-min_x));
            y_diff.add(Math.abs(max_y-min_y));
            z_diff.add(Math.abs(max_z-min_z));

            i=0;

            training_avg = calculateAverage(A_t);
            Log.e(TAG,"avg: "+training_avg);

            A_X.clear();
            A_Y.clear();
            A_Z.clear();
            A_t.clear();
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


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER)
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

            if(i==0)
                new_handler.postDelayed(new_run,30000L);   // sending to calculate the average of the accelerometer's value
            i++;

            a_t  = (float) Math.sqrt(Math.pow(x,2)+ Math.pow(y,2)+ Math.pow(z,2));
            A_t.add(a_t);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
