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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cs on 5/11/2017.
 */

public class Page4 extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button skip, next, add_triggers;

    private ListView lvtriggers;
    private ArrayAdapter adapter;

    private Integer FRAGMENT_CODE = 2222;
    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    private ArrayList<String> selected_triggers;
    private ArrayList<String> patient_selected_triggers;

    DailyDiary dd;
    PersonalInformation pi;

    //DatabaseReference usersInfo;

    public Page4() {
        selected_triggers = new ArrayList<String>();
        patient_selected_triggers = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_diary_4, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");

        if(!selected_triggers.isEmpty()) {
            for (int i = 0; i < pi.getTriggers().size(); i++) {
                if (!selected_triggers.contains(pi.getTriggers().get(i)))
                    pi.getTriggers().remove(i);
            }
        }

        // Union
        Set<String> set = new HashSet<String>();
        set.addAll(selected_triggers);
        set.addAll(pi.getTriggers());
        selected_triggers = new ArrayList<String>(set);

        dd = (DailyDiary) this.getActivity().getIntent().getSerializableExtra("Daily_Diary");

        // Union
        set = new HashSet<String>();
        set.addAll(patient_selected_triggers);
        set.addAll(dd.getTriggers());
        patient_selected_triggers = new ArrayList<>(set);

        //usersInfo = FirebaseDatabase.getInstance().getReference("UsersInfo");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        add_triggers = (Button) view.findViewById(R.id.bAddMigraineTriggers);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        add_triggers.setOnClickListener(this);

        lvtriggers = (ListView) view.findViewById(R.id.lvTriggers);
        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, selected_triggers) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = selected_triggers.get(position);

                return (patient_selected_triggers.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
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

        lvtriggers.setAdapter(adapter);
        lvtriggers.setOnItemClickListener(this);

        return view;
    }

    public void onClick(View v) {
        Fragment fragment = null;

        int view_id = v.getId();
        switch (view_id) {
            case R.id.bSkip:
                fragment = new Page5();
                break;
            case R.id.bNext:
                dd.setTriggers(patient_selected_triggers);
                pi.setTriggers(selected_triggers);
                this.getActivity().getIntent().putExtra("Daily_Diary", dd);
                this.getActivity().getIntent().putExtra("Personal_Information", pi);
                //usersInfo.child(this.getActivity().getIntent().getStringExtra("user_id")).child("triggers").setValue(selected_triggers);
                fragment = new Page5();
                break;
            case R.id.bAddMigraineTriggers:
                fragment = new Triggers();
                fragment.setTargetFragment(this, FRAGMENT_CODE);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("selected_triggers", selected_triggers);
                bundle.putBoolean("triggers_request", true);
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
        String item = lvtriggers.getItemAtPosition(position).toString();
        if(patient_selected_triggers.contains(item)) {
            patient_selected_triggers.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            patient_selected_triggers.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
    }
}
