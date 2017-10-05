package com.mit.migraine;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by cs on 5/18/2017.
 */

public class PromptSettings extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Switch no_of_prompts;
    private EditText time1, time2;

    private Button skip, next;

    private TimePickerDialog TimeEntry1;
    private TimePickerDialog TimeEntry2;

    private DatabaseReference usersInfo;

    private PersonalInformation pi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prompt_settings, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");

        time1 = (EditText) view.findViewById(R.id.etTime1);
        time2 = (EditText) view.findViewById(R.id.etTime2);

        time1.setText(pi.getTimeOfPrompt1());

        no_of_prompts = (Switch) view.findViewById(R.id.sPrompts);
        no_of_prompts.setText(pi.getNoOfPrompts());
        if(pi.getNoOfPrompts().equals("1")) {
            no_of_prompts.setChecked(false);
            time2.setVisibility(View.GONE);
        }
        else {
            no_of_prompts.setChecked(true);
            time2.setVisibility(View.VISIBLE);
            time2.setText(pi.getTimeOfPrompt2());
        }
        no_of_prompts.setOnCheckedChangeListener(this);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        setDateTimeField();

        return view;
    }

    private void setDateTimeField() {
        time1.setOnClickListener(this);
        time2.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        int hour = newCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = newCalendar.get(Calendar.MINUTE);
        TimeEntry1 = new TimePickerDialog(this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String AM_PM  = "";
                if(hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                time1.setText(hourOfDay + ":" + minute + " " + AM_PM);
            }

        }, hour, minute, false);
        TimeEntry1.setTitle("Select Time");

        TimeEntry2 = new TimePickerDialog(this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String AM_PM  = "";
                if(hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                time2.setText(hourOfDay + ":" + minute + " " + AM_PM);
            }

        }, hour, minute, false);
        TimeEntry2.setTitle("Select Time");
    }

    @Override
    public void onClick(View v) {

        if(v == time1) {
            TimeEntry1.show();
        } else if(v == time2) {
            TimeEntry2.show();
        } else if(v == next) {
            pi.setNoOfPrompts(no_of_prompts.getText().toString());
            pi.setTimeOfPrompt1(time1.getText().toString());
            if(pi.getNoOfPrompts().equals("2"))
                pi.setTimeOfPrompt2(time2.getText().toString());
            else
                pi.setTimeOfPrompt2("");
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            usersInfo = FirebaseDatabase.getInstance().getReference("UsersInfo");
            usersInfo.child(this.getActivity().getIntent().getStringExtra("user_id")).setValue(pi);

            // Set Alarm
            new MyAlarmManager().alarmMethod(this.getActivity().getApplicationContext(), pi.getTimeOfPrompt1(), pi.getTimeOfPrompt2());
            // alarmMethod();

            FragmentManager fm = this.getFragmentManager();
            for(int i = 0 ; i < fm.getBackStackEntryCount() ; i++) {
                fm.popBackStack();
            }
            Toast.makeText(this.getActivity(), "You have successfully filled up your personal information.", Toast.LENGTH_LONG).show();
        } else if(v == skip) {
            FragmentManager fm = this.getFragmentManager();
            for(int i = 0 ; i < fm.getBackStackEntryCount() ; i++) {
                fm.popBackStack();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // do something when check is selected
            no_of_prompts.setText("2");
            time2.setVisibility(View.VISIBLE);
            time2.setText("10:00 PM");
        } else {
            //do something when unchecked
            no_of_prompts.setText("1");
            time2.setVisibility(View.GONE);
        }
    }

    /*public void alarmMethod(){
        Intent intent = new Intent(this.getActivity().getApplicationContext(), Notifi.class);
        AlarmManager alarmManager = (AlarmManager) this.getActivity().getApplicationContext().getSystemService(this.getActivity().getApplicationContext().ALARM_SERVICE);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getActivity().getApplicationContext(), 0, intent, 0);
        alarmManager.cancel(pendingIntent1);
        pendingIntent1.cancel();

        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pi.getTimeOfPrompt1().split(":")[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(pi.getTimeOfPrompt1().split(":")[1].split(" ")[0]));
        cal1.set(Calendar.SECOND, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent1);

        if(!pi.getTimeOfPrompt2().equals("")) {
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this.getActivity().getApplicationContext(), 1, intent, 0);
            alarmManager.cancel(pendingIntent2);
            pendingIntent2.cancel();

            Calendar cal2 = Calendar.getInstance();
            cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pi.getTimeOfPrompt2().split(":")[0]));
            cal2.set(Calendar.MINUTE, Integer.parseInt(pi.getTimeOfPrompt2().split(":")[1].split(" ")[0]));
            cal2.set(Calendar.SECOND, 0);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);
        }
        //1000 * 60 * 60 * 24
        //Toast.makeText(MainActivity.this, "Start Alarm", Toast.LENGTH_LONG).show();
    }*/
}
