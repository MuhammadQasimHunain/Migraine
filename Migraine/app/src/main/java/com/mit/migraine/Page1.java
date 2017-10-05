package com.mit.migraine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cs on 5/9/2017.
 */

public class Page1 extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private SeekbarWithIntervals seekbarWithIntervals1 = null;
    private SeekbarWithIntervals seekbarWithIntervals2 = null;

    private Button inc1, inc2, dec1, dec2;
    private TextView incdec1, incdec2;
    private Button skip, next;

    private Switch pain;

    DailyDiary dd;

    //PersonalInformation pi;

    public Page1() {
        dd = new DailyDiary();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_diary_1, container, false);

        //pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");
        dd = (DailyDiary) this.getActivity().getIntent().getSerializableExtra("Daily_Diary");
        if(dd == null)
            dd = new DailyDiary();

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        List<String> seekbarIntervals = getIntervals();
        seekbarWithIntervals1 = (SeekbarWithIntervals) view.findViewById(R.id.seekbarWithIntervals1);
        seekbarWithIntervals1.setIntervals(seekbarIntervals);
        seekbarWithIntervals1.setProgress(dd.getSleep_quality());

        seekbarWithIntervals2 = (SeekbarWithIntervals) view.findViewById(R.id.seekbarWithIntervals2);
        seekbarWithIntervals2.setIntervals(seekbarIntervals);
        seekbarWithIntervals2.setProgress(dd.getStress_level());

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        inc1 = (Button) view.findViewById(R.id.bInc1);
        inc2 = (Button) view.findViewById(R.id.bInc2);
        dec1 = (Button) view.findViewById(R.id.bDec1);
        dec2 = (Button) view.findViewById(R.id.bDec2);

        inc1.setOnClickListener(this);
        inc2.setOnClickListener(this);
        dec1.setOnClickListener(this);
        dec2.setOnClickListener(this);

        incdec1 = (TextView) view.findViewById(R.id.tvIncDec1);
        incdec1.setText(Integer.toString(dd.getHours()));
        incdec2 = (TextView) view.findViewById(R.id.tvIncDec2);
        incdec2.setText(Integer.toString(dd.getMinutes()));

        pain = (Switch) view.findViewById(R.id.sPain);
        if(dd.getHeadache().equals("No"))
            pain.setChecked(false);
        else
            pain.setChecked(true);
        pain.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // do something when check is selected
            pain.setText("Yes");
        } else {
            //do something when unchecked
            pain.setText("No");
        }
    }

    public void onClick(View v)
    {
        Fragment fragment = null;
        int view_id = v.getId();
        switch (view_id) {
            case R.id.bInc1:
                incdec1.setText(Integer.toString(Integer.parseInt(incdec1.getText().toString()) + 1));
                break;
            case R.id.bDec1:
                incdec1.setText(Integer.toString(Integer.parseInt(incdec1.getText().toString()) - 1));
                break;
            case R.id.bInc2:
                incdec2.setText(Integer.toString(Integer.parseInt(incdec2.getText().toString()) + 1));
                break;
            case R.id.bDec2:
                incdec2.setText(Integer.toString(Integer.parseInt(incdec2.getText().toString()) - 1));
                break;
            case R.id.bSkip:
                this.getActivity().getIntent().putExtra("Daily_Diary", dd);
                //this.getActivity().getIntent().putExtra("Personal_Information", pi);
                fragment = new Page2();
                break;
            case R.id.bNext:
                dd.setHours(Integer.parseInt(incdec1.getText().toString()));
                dd.setMinutes(Integer.parseInt(incdec2.getText().toString()));
                dd.setSleep_quality(seekbarWithIntervals1.getProgress());
                dd.setStress_level(seekbarWithIntervals2.getProgress());
                dd.setHeadache(pain.getText().toString());

                this.getActivity().getIntent().putExtra("Daily_Diary", dd);
                if(dd.getHeadache().equals("No"))
                    fragment = new OptionalPage2();
                else {
                    dd.setLurking("");
                    fragment = new Page2();
                }
                break;
            default:
                break;
        }

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
}
