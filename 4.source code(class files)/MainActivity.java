package com.example.n1363l.final_project_try_006;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main_class";
    public static final String MyPREFERENCES = "f_p_prefs";
    SharedPreferences sharedprfs;
    public static final String phone1 = "phonekey1";

    String ph01,get_ph01;
    EditText num01;
    Button enter_button,btn_start_t_u,btn_start_m_u,btn_start_t;

    private RadioGroup first_group;
    private RadioGroup second_group;
    private RadioGroup third_group;
    private RadioButton minutes_r_btn, seconds_r_btn;
    private RadioButton shirt_r_btn,pant_r_btn,training_btn,non_training_btn;
    private TextView txt_ph_pos,txt_time_format;
    private Button btn_action_send;
    private EditText edt_txt_number;
    private LinearLayout ll;

    int ph_position_trn;

    boolean inshirt_pocket, inpant_pocket;
    boolean num_in_sec = false,num_in_min = false;
    boolean in_training = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num01 = (EditText)findViewById(R.id.editText_num_01);

        sharedprfs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE); /* for storing the phone numbers */

//        first_group = (RadioGroup) findViewById(R.id.radio_grp_th_option);
//        btn_action_send = (Button)findViewById(R.id.button_start);
        btn_start_t_u = (Button)findViewById(R.id.button_start_training_user);
        btn_start_m_u = (Button)findViewById(R.id.button_start_monitoring_user);

        ll = (LinearLayout)findViewById(R.id.layout_hidden);

        try{
            get_ph01 = sharedprfs.getString(phone1,null);
            if(!TextUtils.isEmpty(get_ph01)){
                num01.setText(get_ph01);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        btn_start_m_u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ph01= num01.getText().toString();
                if(!ph01.equals("")&&ph01.length()==11) {
                    SharedPreferences.Editor editor = sharedprfs.edit();
                    editor.putString(phone1, ph01);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(),Comp_T_or_start_M.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btn_start_t_u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.user_choice_training);

                second_group = (RadioGroup) findViewById(R.id.radio_grp_phone_pos);
                third_group = (RadioGroup)findViewById(R.id.radio_grp_time_selection);

//        training_btn = (RadioButton)findViewById(R.id.r_button_training);
//        non_training_btn = (RadioButton)findViewById(R.id.r_button_no_training);
                shirt_r_btn = (RadioButton)findViewById(R.id.r_btn_shirt_pocket);
                pant_r_btn = (RadioButton)findViewById(R.id.r_btn_pant_pocket);
                seconds_r_btn = (RadioButton) findViewById(R.id.seconds_radio_button);
                minutes_r_btn = (RadioButton) findViewById(R.id.minutes_radio_button);

                txt_ph_pos = (TextView)findViewById(R.id.textView_trainig_phone_pos);
                txt_time_format = (TextView)findViewById(R.id.textView_training_time);

                edt_txt_number = (EditText) findViewById(R.id.input_interval_time);
                btn_start_t = (Button)findViewById(R.id.button_start_training);

                second_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        switch (checkedId){
                            case R.id.r_btn_shirt_pocket:
                                inshirt_pocket = true;
                                ph_position_trn = 1;
                                Log.e(TAG,"in shirt's pocket");
                                txt_time_format.setVisibility(View.VISIBLE);
                                third_group.setVisibility(View.VISIBLE);
                                break;
                            case R.id.r_btn_pant_pocket:
                                inpant_pocket = false;
                                ph_position_trn=2;
                                Log.e(TAG,"in pant's pocket");
                                txt_time_format.setVisibility(View.VISIBLE);
                                third_group.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });

                third_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        switch (checkedId){
                            case R.id.seconds_radio_button:
                                num_in_sec = true;
                                Log.e(TAG,"number format in sec");
                                edt_txt_number.setVisibility(View.VISIBLE);
                                break;
                            case R.id.minutes_radio_button:
                                num_in_min = true;
                                Log.e(TAG,"number format in sec");
                                edt_txt_number.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });

                btn_start_t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String getInterval = edt_txt_number.getText().toString().trim();//get interval from edittext
                        int interval_inInt = 0;
                        try {
                            interval_inInt = getTimeInterval(getInterval);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //check interval should not be empty and 0
                        if (!getInterval.equals("") && !getInterval.equals("0") && !(interval_inInt<60)){
                            Log.e(TAG,"in button clicked method- training");

                            Intent intent = new Intent(getApplicationContext(),Acclerometer_v_for_training.class);
                            intent.putExtra("ph_position",ph_position_trn);
                            intent.putExtra("interval",interval_inInt);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });


/*        first_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.r_button_training:
                        in_training = true;
                        txt_ph_pos.setVisibility(View.VISIBLE);
                        second_group.setVisibility(View.VISIBLE);
                        Log.e(TAG,"in training");
                        break;
                    case R.id.r_button_no_training:
                        in_training = false;
                        txt_ph_pos.setVisibility(View.INVISIBLE);
                        second_group.setVisibility(View.INVISIBLE);
                        txt_time_format.setVisibility(View.INVISIBLE);
                        third_group.setVisibility(View.INVISIBLE);
                        edt_txt_number.setVisibility(View.INVISIBLE);
                        Log.e(TAG,"not in training");
                        break;
                }
            }
        });*/



/*        btn_action_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ph01= num01.getText().toString();
                if(!ph01.equals("")&&ph01.length()==11){
                    SharedPreferences.Editor editor = sharedprfs.edit();
                    editor.putString(phone1,ph01);
                    editor.commit();

                    if(in_training){
                        String getInterval = edt_txt_number.getText().toString().trim();//get interval from edittext
                        int interval_inInt = getTimeInterval(getInterval);
                        //check interval should not be empty and 0
                        if (!getInterval.equals("") && !getInterval.equals("0") && !(interval_inInt<60)){
                            Log.e(TAG,"in button clicked method- training");

                            Intent intent = new Intent(getApplicationContext(),Acclerometer_v_for_training.class);
                            intent.putExtra("ph_position",ph_position_trn);
                            intent.putExtra("interval",interval_inInt);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{
                        Log.e(TAG,"btn clicked, not in training- will go for sensor listening");
                        Intent intent = new Intent(getApplicationContext(),Comp_T_or_start_M.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });*/
    }

    //get time interval to trigger alarm manager
    private int getTimeInterval(String getInterval) {
        int interval = Integer.parseInt(getInterval);//convert string interval into integer
        //Return interval on basis of radio button selection
        if (seconds_r_btn.isChecked())
            return interval;
        if (minutes_r_btn.isChecked())
            return interval * 60;//convert minute into seconds
        return 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
