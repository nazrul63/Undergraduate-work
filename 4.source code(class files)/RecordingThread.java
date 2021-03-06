/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.n1363l.final_project_try_006;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class RecordingThread {
    private static final String LOG_TAG = RecordingThread.class.getSimpleName();
    private static final int SAMPLE_RATE = 44100;

    private Context context;
    double average = 0.0;
    short max_v = 0,max_exp;
    double x=0;
    double db,db_max,db_max_exp,present_db;
    int audiobuffer_lnt;
    public static double REFERENCE = 0.00002;
    static int i;
    ArrayList<Double>raw_audio = new ArrayList<>();
//    private MainActivity m_obj;
    private Accelerometer_v_for_testing_utilityC_modified aclrobj;
    private double highest_dB;
    private double dB_diff;

    public RecordingThread(AudioDataReceivedListener listener, Context context) {
        mListener = listener;
        this.context = context;
//        m_obj = new MainActivity();
        aclrobj = new Accelerometer_v_for_testing_utilityC_modified(context);
    }

    private boolean mShouldContinue;
    private AudioDataReceivedListener mListener;
    private Thread mThread;

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        mThread = null;
    }

    public void calculate_avg(short [] audioBuffer)
    {
        average = 0;
        max_exp = 0;
        audiobuffer_lnt = audioBuffer.length;
//        int i=0;
        for (short s : audioBuffer)
        {
            if(s>0)
            {
                average += Math.abs(s);
/*                if(s>max_exp)
                    max_exp = s;*/
            }
            else
            {
                audiobuffer_lnt--;
            }
//            Log.e("modified-portion: ",""+i+" : "+s);
//            i++;
        }
        x=0;
//        Log.e("in modified-portion","max_amplitude = "+max_exp);
        if(average != 0 && audiobuffer_lnt !=0)
        {
            x = average/audiobuffer_lnt;
            raw_audio.add(x);
/*            if(x>= 3277){
                aclrobj.audio_triggered = true;
//                aclrobj.to_check();
            }
            */
            if(x>0){
                double present_pressure = x/51805.5336; //the value 51805.5336 can be derived from asuming that x=32767=0.6325 Pa and x=1 = 0.00002 Pa (the reference value)

//        Log.d("in modified-portion", "x="+pressure +" Pa");
                present_db = (20 * Math.log10(present_pressure/REFERENCE));
//                String db_str = String.format(Locale.US,"%2f",present_db);
                aclrobj.string_dB = aclrobj.string_dB.concat("\n"+Double.toString(present_db));
                if(present_db>=60&&present_db>highest_dB){
                    highest_dB = present_db;
                    i=0;
                }

                if(i<=10){
                    dB_diff = highest_dB - present_db;
                    if(dB_diff>=30){
                        aclrobj.audio_triggered = true;
                        aclrobj.Triggering_dB.add(highest_dB);
                        aclrobj.triggering_dB_str = aclrobj.triggering_dB_str.concat(highest_dB+" \t "+present_db);
                        i= 11;
                        highest_dB=0;
                    }
                }
                else{
                    highest_dB = 0;
                }
                i++;
            }
        }

//        Log.e("in modified-portion","average = "+average+"\naudio_buffersize = "+audiobuffer_lnt+"\nadded : "+x);
    }

    private void record() {
        Log.v(LOG_TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        short[] audioBuffer = new short[bufferSize / 2];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        Log.v(LOG_TAG, "Start recording; buffersize: "+bufferSize+" \naudio buffer: "+audioBuffer);

        long shortsRead = 0;
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;

//            Log.e("asasas","numberOfShort : "+numberOfShort);

            calculate_avg(audioBuffer);
            // Notify waveform
            mListener.onAudioDataReceived(audioBuffer);
        }

        record.stop();

        record.release();

        Log.d("in modified-portion", "getNoiseLevel() ");
        db=0;
        db_max_exp = 0;
//        Log.e("in modified-portion","in calculating dB"+db);
        if (x==0){
            Log.e("in modified-portion","x is zero!!");
        }
        else
        {

            Log.e("in modified-portion","x is = "+x);

            // calculating the pascal pressure based on the idea that the max amplitude (between 0 and 32767) is
            // relative to the pressure
            int size = raw_audio.size();
            double sum = 0;
            int i;
            for(i=0;i<size;i++)
            {
                sum += raw_audio.get(i);
//            Log.e("sdsdsdsds"," added : "+raw_audio.get(i)+"    sum : "+sum);
            }

            double final_avg = sum/size;
            double max_v = Collections.max(raw_audio);
//        Log.e("sdsdsdsdkkk","raw_array_size : "+size+"\nsum : "+sum+"\nmax_v : "+max_v);

            double pressure = final_avg/51805.5336; //the value 51805.5336 can be derived from asuming that x=32767=0.6325 Pa and x=1 = 0.00002 Pa (the reference value)
            double pressure_max_exp = max_v/51805.5336;
//        Log.d("in modified-portion", "x="+pressure +" Pa");
//        Log.d("in modified-portion", "max_exp_pressure="+pressure_max_exp +" Pa");
            db = (20 * Math.log10(pressure/REFERENCE));
            db_max_exp = (20 * Math.log10(pressure_max_exp/REFERENCE));
            Log.e("in modified-portion","dB = "+db+"\nmax_exp_db = "+db_max_exp);

            raw_audio.clear();

        }

        Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }
}
