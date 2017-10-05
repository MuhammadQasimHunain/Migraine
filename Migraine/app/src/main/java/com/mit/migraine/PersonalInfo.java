package com.mit.migraine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by cs on 5/12/2017.
 */

public class PersonalInfo extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText age, methOfBirthControl, LMP;
    private EditText next_period;

    private DatePickerDialog LMPDatePickerDialog;
    private DatePickerDialog NextPeriodDatePickerDialog;

    private Button skip, next;

    private RadioGroup gender;
    private RadioButton radioButton1;
    private RadioButton radioButton2;

    private SimpleDateFormat dateFormatter;

    private PersonalInformation pi;
    //ArrayList<Medicine> medicines;

    public PersonalInfo() {
        //medicines = new ArrayList<Medicine>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_info, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");
        //medicines = this.getActivity().getIntent().getParcelableArrayListExtra("Medicines_List");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        age = (EditText) view.findViewById(R.id.etAge);
        methOfBirthControl = (EditText) view.findViewById(R.id.etMethBirthCont);
        LMP = (EditText) view.findViewById(R.id.etLMP);
        next_period = (EditText) view.findViewById(R.id.etNextPeriod);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        gender = (RadioGroup) view.findViewById(R.id.rgGender);
        radioButton1 = (RadioButton) gender.findViewById(R.id.rbMale);
        radioButton2 = (RadioButton) gender.findViewById(R.id.rbFemale);

        gender.setOnCheckedChangeListener(this);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        setDateTimeField();

        age.setText(pi.getAge());
        LMP.setText(pi.getDateOfLMP());
        next_period.setText(pi.getAnticipatedDataOfNextPeriod());
        if (pi.getGender().equals("Male")) {
            radioButton1.setChecked(true);
            radioButton2.setChecked(false);
            methOfBirthControl.setVisibility(View.GONE);
            LMP.setVisibility(View.GONE);
            next_period.setVisibility(View.GONE);
        } else {
            radioButton2.setChecked(true);
            radioButton1.setChecked(false);
            methOfBirthControl.setVisibility(View.VISIBLE);
            LMP.setVisibility(View.VISIBLE);
            next_period.setVisibility(View.VISIBLE);
        }

        return view;
    }


    private void setDateTimeField() {
        LMP.setOnClickListener(this);
        next_period.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        LMPDatePickerDialog = new DatePickerDialog(this.getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                LMP.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        LMPDatePickerDialog.setTitle("Date of LMP");

        NextPeriodDatePickerDialog = new DatePickerDialog(this.getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                next_period.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        NextPeriodDatePickerDialog.setTitle("Anticipated date of next period");
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        if(view == LMP) {
            LMPDatePickerDialog.show();
        } else if(view == next_period) {
            NextPeriodDatePickerDialog.show();
        } else if(view == skip) {
            FragmentManager fm = this.getFragmentManager();
            for(int i = 0 ; i < fm.getBackStackEntryCount() ; ++i) {
                fm.popBackStack();
            }
        } else if(view == next) {
            String ag = age.getText().toString();
            if(!ag.equals(""))
                pi.setAge(ag);
            //pi.setMethodsOfBirthControl(methOfBirthControl.getText().toString());
            if (pi.getGender().equals(""))
                pi.setGender(radioButton1.getText().toString());
            pi.setDateOfLMP(LMP.getText().toString());
            pi.setAnticipatedDataOfNextPeriod(next_period.getText().toString());
            //this.getActivity().getIntent().putExtra("Personal_Information", pi);
            //this.getActivity().getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new OtherMedicalConditions();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        // This will get the radiobutton that has changed in its check state
        RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
        // This puts the value (true/false) into the variable
        boolean isChecked = checkedRadioButton.isChecked();
        // If the radiobutton that has changed in check state is now checked...
        if (isChecked)
        {
            if(checkedRadioButton.getText().toString().equals("Female")) {
                pi.setGender("Female");
                methOfBirthControl.setVisibility(View.VISIBLE);
                LMP.setVisibility(View.VISIBLE);
                next_period.setVisibility(View.VISIBLE);
            } else if (checkedRadioButton.getText().toString().equals("Male")) {
                pi.setGender("Male");
                methOfBirthControl.setVisibility(View.GONE);
                LMP.setVisibility(View.GONE);
                next_period.setVisibility(View.GONE);
            }
        }
    }
}
