package com.mit.migraine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.text.DateFormat.getDateTimeInstance;

/**
 * Created by cs on 5/11/2017.
 */

public class Page5 extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button done, select_helps;

    private ListView lvhelpers;
    private ArrayAdapter adapter;
    private ArrayList<String> selected_helpers;
    private ArrayList<String> patient_selected_helpers;

    private Integer FRAGMENT_CODE = 3333;
    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    DailyDiary dd;
    PersonalInformation pi;

    DatabaseReference dailyDiary;
    DatabaseReference usersInfo;

    public Page5() {
        selected_helpers = new ArrayList<String>();
        patient_selected_helpers = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_diary_5, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");

        if(!selected_helpers.isEmpty()) {
            for (int i = 0; i < pi.getHelpers().size(); i++) {
                if (!selected_helpers.contains(pi.getHelpers().get(i)))
                    pi.getHelpers().remove(i);
            }
        }

        // Union
        Set<String> set = new HashSet<String>();
        set.addAll(selected_helpers);
        set.addAll(pi.getHelpers());
        selected_helpers = new ArrayList<String>(set);

        dd = (DailyDiary) this.getActivity().getIntent().getSerializableExtra("Daily_Diary");
        patient_selected_helpers.addAll(dd.getHelpers());

        usersInfo = FirebaseDatabase.getInstance().getReference("UsersInfo");
        dailyDiary = FirebaseDatabase.getInstance().getReference("DailyDiary");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        done = (Button) view.findViewById(R.id.bDone);
        select_helps = (Button) view.findViewById(R.id.bAddMigraineHelper);

        done.setOnClickListener(this);
        select_helps.setOnClickListener(this);

        lvhelpers = (ListView) view.findViewById(R.id.lvHelpers);
        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, selected_helpers) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = selected_helpers.get(position);

                return (patient_selected_helpers.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                switch (getItemViewType(position)) {
                    case TYPE_ITEM_COLORED:
                        v.setBackgroundResource(android.R.color.holo_blue_dark);
                        break;
                    case TYPE_ITEM_NORMAL:
                        break;
                }
                return v;
            }
        };

        // Assign adapter to ListView
        lvhelpers.setAdapter(adapter);
        lvhelpers.setOnItemClickListener(this);

        return view;
    }

    public void onClick(View v) {
        Fragment fragment = null;
        int view_id = v.getId();
        switch (view_id) {
            case R.id.bDone:
                dd.setHelpers(patient_selected_helpers);
                pi.setHelpers(selected_helpers);
                this.getActivity().getIntent().putExtra("Daily_Diary", dd);
                this.getActivity().getIntent().putExtra("Personal_Information", pi);
                dailyDiary.child(this.getActivity().getIntent().getStringExtra("user_id")).child(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a z").format(new Date())).setValue(dd);
                //usersInfo.child(this.getActivity().getIntent().getStringExtra("user_id")).child("helpers").setValue(selected_helpers);
                usersInfo.child(this.getActivity().getIntent().getStringExtra("user_id")).setValue(pi);
                FragmentManager fm = this.getFragmentManager();
                for(int i = 0 ; i < fm.getBackStackEntryCount() ; i++) {
                    fm.popBackStack();
                }
                Toast.makeText(this.getActivity(), "You have successfully completed your Diary.", Toast.LENGTH_LONG).show();
                break;
            case R.id.bAddMigraineHelper:
                fragment = new MigraineHelpers();
                fragment.setTargetFragment(this, FRAGMENT_CODE);
                Bundle bundle = new Bundle();
                bundle.putBoolean("helpers_request", true);
                bundle.putStringArrayList("selected_helpers", selected_helpers);
                fragment.setArguments(bundle);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = lvhelpers.getItemAtPosition(position).toString();
        if(patient_selected_helpers.contains(item)) {
            patient_selected_helpers.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            patient_selected_helpers.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FRAGMENT_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                ArrayList<String> list = data.getStringArrayListExtra("selected_helpers");
                Collections.addAll(selected_helpers, list.toArray(new String[list.size()]));
            }
        }
    }*/
}
