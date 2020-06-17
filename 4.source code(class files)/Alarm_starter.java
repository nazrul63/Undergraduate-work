package com.example.n1363l.final_project_try_006;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Alarm_starter extends AppCompatActivity {

    //Pending intent instance
    private PendingIntent pendingIntent;
    static boolean cancel_button_clicked= false;
    TextView txt;

    //Alarm Request Code
    private static final int ALARM_REQUEST_CODE = 133;
//    private Accelerometer_v_for_testing_utilityC aclr_obj;

    Handler handler_4_actv_close = new Handler();
    Runnable run_actv_close = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);
        txt = (TextView)findViewById(R.id.textView_showtext);

//        aclr_obj = new Accelerometer_v_for_testing_utilityC(getApplicationContext());

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(Alarm_starter.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Alarm_starter.this, ALARM_REQUEST_CODE, alarmIntent, 0);

        triggerAlarmManager(10);

        //set on click over stop alarm button
        findViewById(R.id.stop_alarm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stop alarm manager
                cancel_button_clicked = true;
                stopAlarmManager();
            }
        });

    }

    //Trigger alarm manager with entered time interval
    public void triggerAlarmManager(int alarmTriggerTime) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
        manager.set(AlarmManager.RTC_WAKEUP, (alarmTriggerTime * 1000), pendingIntent);//sst alarm manager with entered timer by converting into milliseconds

//        Toast.makeText(this, "Alarm Set for " + alarmTriggerTime + " seconds.", Toast.LENGTH_SHORT).show();

        final CountDownTimer count_alrm_time = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(cancel_button_clicked == true)
                    cancel();
            }

            @Override
            public void onFinish() {
                if(cancel_button_clicked == false)
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
        stopService(new Intent(Alarm_starter.this, AlarmSoundService.class));

        //remove the notification from notification tray
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmNotificationService.NOTIFICATION_ID);

        if(cancel_button_clicked)
        {
            Toast.makeText(this, "Alarm Canceled/Stop by User.", Toast.LENGTH_SHORT).show();
            txt.setText("sorry !!\n false alarm ");
            Log.e("alarm_class","now will go back to the accelerometer class");
//            aclr_obj.start_sensor_listening();
            Intent intent  = new Intent(getApplicationContext(),Comp_T_or_start_M.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
/*            if(cancel_button_clicked)
                findViewById(R.id.stop_alarm_button).setEnabled(false);
            handler_4_actv_close.postDelayed(run_actv_close,4000L);*/
        }

        else
        {
            Toast.makeText(this, "Alarm Canceled/Stop automaticly.", Toast.LENGTH_SHORT).show();
            // send messge to the relatives
            Intent intent = new Intent(getApplicationContext(),Send_SMS.class);
            startActivity(intent);
            finish();
        }


    }
}
