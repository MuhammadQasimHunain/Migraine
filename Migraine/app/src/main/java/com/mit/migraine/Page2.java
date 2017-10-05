package com.mit.migraine;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by cs on 5/9/2017.
 */

public class Page2 extends Fragment implements View.OnClickListener {

    private SeekbarWithIntervals seekbarWithIntervals = null;
    private Spinner s1, s2;

    private Button skip, next;

    private TimePickerDialog TimeEntry;

    List<String> list1;
    List<String> list2;

    ArrayAdapter<String> dataAdapter1;
    ArrayAdapter<String> dataAdapter2;

    DailyDiary dd;
    //PersonalInformation pi;

    public Page2() {
        dd = new DailyDiary();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_diary_2, container, false);

        //pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");

        /*Bundle bundle = this.getArguments();
        if(bundle != null && bundle.getBoolean("quickly_record_migraine")) {
            skip.setVisibility(View.GONE);
            next.setText("Done");
        }*/

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        dd = (DailyDiary) this.getActivity().getIntent().getSerializableExtra("Daily_Diary");

        List<String> seekbarIntervals = getIntervals();
        seekbarWithIntervals = (SeekbarWithIntervals) view.findViewById(R.id.seekbarWithIntervals);
        seekbarWithIntervals.setIntervals(seekbarIntervals);
        seekbarWithIntervals.setProgress(dd.getSeverity());

        list1 = new ArrayList<String>(Arrays.asList("15 mins ago", "30 mins ago", "1 hrs ago", "other time"));
        list2 = new ArrayList<String>(Arrays.asList("15 mins ago", "30 mins ago", "1 hrs ago", "other time"));

        if(!dd.getMigraine_start().equals("") && !dd.getMigraine_start().equals("other time"))
            list1.set(3, dd.getMigraine_start());
        if(!dd.getMigraine_end().equals("") && !dd.getMigraine_end().equals("other time"))
            list2.set(3, dd.getMigraine_end());

        s1 = (Spinner) view.findViewById(R.id.spinner1);
        s2 = (Spinner) view.findViewById(R.id.spinner2);

        dataAdapter1 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(dataAdapter1);

        SpinnerInteractionListener listener1 = new SpinnerInteractionListener();
        s1.setOnTouchListener(listener1);
        s1.setOnItemSelectedListener(listener1);

        dataAdapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(dataAdapter2);

        SpinnerInteractionListener listener2 = new SpinnerInteractionListener();
        s2.setOnTouchListener(listener2);
        s2.setOnItemSelectedListener(listener2);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        return view;
    }

    public void onClick(View v)
    {
        Fragment fragment = null;

        int view_id = v.getId();
        if(v == skip) {
            //this.getActivity().getIntent().putExtra("Personal_Information", pi);
            fragment = new Page3();
        } else if (v == next && next.getText().toString().equals("Next")) {
            dd.setMigraine_start(s1.getSelectedItem().toString());
            dd.setMigraine_end(s2.getSelectedItem().toString());
            dd.setSeverity(seekbarWithIntervals.getProgress());

            this.getActivity().getIntent().putExtra("Daily_Diary", dd);
            fragment = new Page3();
        }

        /*else if (v == next && next.getText().toString().equals("Done")) {
            this.getFragmentManager().popBackStack();
        }*/

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private List<String> getIntervals() {
        return new ArrayList<String>() {{
            add("0");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
            add("8");
            add("9");
        }};
    }


    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;
        private AdapterView<?> parent;

        private void setDateTimeField() {

            Calendar newCalendar = Calendar.getInstance();
            int hour = newCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = newCalendar.get(Calendar.MINUTE);
            TimeEntry = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    //time1.setText(hourOfDay + ":" + minute);
                    String AM_PM  = "";
                    if(hourOfDay < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                    }
                    if(parent == s1) {
                        list1.set(3, hourOfDay + ":" + minute+ " " + AM_PM);
                        dataAdapter1.notifyDataSetChanged();
                    } else if (parent == s2) {
                        list2.set(3, hourOfDay + ":" + minute+ " " + AM_PM);
                        dataAdapter2.notifyDataSetChanged();
                    }
                }

            }, hour, minute, false);
            TimeEntry.setTitle("Select Time");
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userSelect) {
                // Your selection handling code here
                userSelect = false;
                String option = parent.getSelectedItem().toString();
                if(!option.equals("15 mins ago") && !option.equals("30 mins ago") && !option.equals("1 hrs ago")) {
                    this.parent = parent;
                    setDateTimeField();
                    TimeEntry.show();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }
}
