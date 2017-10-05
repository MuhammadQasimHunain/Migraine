package com.mit.migraine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by cs on 9/22/2017.
 */

public class OptionalPage2 extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button skip, next;

    private Switch lurking;

    DailyDiary dd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.optional_daily_diary_2, container, false);

        dd = (DailyDiary) this.getActivity().getIntent().getSerializableExtra("Daily_Diary");

        lurking = (Switch) view.findViewById(R.id.sLurking);
        if(dd.getLurking().equals("No"))
            lurking.setChecked(false);
        else
            lurking.setChecked(true);
        lurking.setOnCheckedChangeListener(this);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // do something when check is selected
            lurking.setText("Yes");
        } else {
            //do something when unchecked
            lurking.setText("No");
        }
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        int view_id = v.getId();
        switch (view_id) {
            case R.id.bSkip:
                //this.getActivity().getIntent().putExtra("Personal_Information", pi);
                fragment = new Page2();
                break;
            case R.id.bNext:
                dd.setLurking(lurking.getText().toString());
                this.getActivity().getIntent().putExtra("Daily_Diary", dd);

                fragment = new Page3();
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
}
