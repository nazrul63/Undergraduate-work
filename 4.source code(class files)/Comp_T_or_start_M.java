package com.example.n1363l.final_project_try_006;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by N1363l on 17-10-17.
 */

public class Comp_T_or_start_M extends Activity {

    private static final String TAG = "Comp_T_or_start_M_class";
    private Button c_app,minimize_app;
    TextView txt_greetings;

    private Accelerometer_v_for_testing_utilityC_modified aclr_obj;
    private AudioDataReceivedListener ad_received_listener;

    private RecordingThread mRecordingThread;
    private static final int REQUEST_RECORD_AUDIO = 13;

    private Obtaining_GPS_value_utilityC gpsobj;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_complete_or_start_monitoring);

        c_app = (Button)findViewById(R.id.button_close_app);
        minimize_app = (Button)findViewById(R.id.button_minimize_app);
        txt_greetings = (TextView)findViewById(R.id.textView_greetings);


        aclr_obj = new Accelerometer_v_for_testing_utilityC_modified(getApplicationContext());
        try{
            if(aclr_obj.rcd_thread.recording()){
                aclr_obj.stop_microphone();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        gpsobj = new Obtaining_GPS_value_utilityC(getApplicationContext());
        gpsobj.get_Location();

        aclr_obj.start_sensor_listening();



        ad_received_listener = new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
            }
        };
        mRecordingThread = new RecordingThread(ad_received_listener,getApplicationContext());

        if (!mRecordingThread.recording()) {
            try {
                if(startAudioRecordingSafe())
                    mRecordingThread.stopRecording();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mRecordingThread.stopRecording();
        }

        c_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* will close the application */
                stop_sensors();
            }
        });

        minimize_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
    }

    public void stop_sensors(){
        if(mRecordingThread.recording()){
            try{
                mRecordingThread.stopRecording();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        aclr_obj.stop_acclr_class_activity();
//        aclr_obj.exit_app();
        finish();
//        exit_app();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mRecordingThread!= null)
        {
            mRecordingThread.stopRecording();
        }
    }

    private boolean startAudioRecordingSafe() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
//            mRecordingThread.startRecording();
            return true;
        } else {
            requestMicrophonePermission();
            return false;
        }
    }

    private void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Comp_T_or_start_M.this, android.Manifest.permission.RECORD_AUDIO)) {
            // Show dialog explaining why we need record audio
            ActivityCompat.requestPermissions(Comp_T_or_start_M.this, new String[]{
                    android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);

        } else {
            ActivityCompat.requestPermissions(Comp_T_or_start_M.this, new String[]{
                    android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mRecordingThread.stopRecording();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void exit_app(){
        try {
            this.finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
