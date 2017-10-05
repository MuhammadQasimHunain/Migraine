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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cs on 5/10/2017.
 */

public class Page3 extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView lvsymptoms;
    private Button skip, next, add_sympts;

    private ArrayAdapter adapter;
    private ArrayList<String> selected_sympts;
    private ArrayList<String> patient_selected_sympts;

    private Integer FRAGMENT_CODE = 1111;
    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    DailyDiary dd;
    PersonalInformation pi;

    //DatabaseReference usersInfo;

    public Page3() {
        selected_sympts = new ArrayList<String>();
        patient_selected_sympts = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daily_diary_3, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");

        if(!selected_sympts.isEmpty()) {
            for (int i = 0; i < pi.getAssociatedSymptoms().size(); i++) {
                if (!selected_sympts.contains(pi.getAssociatedSymptoms().get(i)))
                    pi.getAssociatedSymptoms().remove(i);
            }
        }

        // Union
        Set<String> set = new HashSet<String>();
        set.addAll(selected_sympts);
        set.addAll(pi.getAssociatedSymptoms());
        selected_sympts = new ArrayList<String>(set);

        dd = (DailyDiary) this.getActivity().getIntent().getSerializableExtra("Daily_Diary");

        // Union
        set = new HashSet<String>();
        set.addAll(patient_selected_sympts);
        set.addAll(dd.getSymptoms());
        patient_selected_sympts = new ArrayList<>(set);

        //usersInfo = FirebaseDatabase.getInstance().getReference("UsersInfo");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        add_sympts = (Button) view.findViewById(R.id.bAddAssocSymptom);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        add_sympts.setOnClickListener(this);

        lvsymptoms = (ListView) view.findViewById(R.id.lvSymptoms);
        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, selected_sympts) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = selected_sympts.get(position);

                return (patient_selected_sympts.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
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
        lvsymptoms.setAdapter(adapter);
        lvsymptoms.setOnItemClickListener(this);

        return view;
    }

    public void onClick(View v) {
        Fragment fragment = null;

        int view_id = v.getId();
        switch (view_id) {
            case R.id.bSkip:
                fragment = new Page4();
                break;
            case R.id.bNext:
                dd.setSymptoms(patient_selected_sympts);
                pi.setAssociatedSymptoms(selected_sympts);
                this.getActivity().getIntent().putExtra("Daily_Diary", dd);
                this.getActivity().getIntent().putExtra("Personal_Information", pi);
                //usersInfo.child(this.getActivity().getIntent().getStringExtra("user_id")).child("associatedSymptoms").setValue(selected_sympts);
                fragment = new Page4();
                break;
            case R.id.bAddAssocSymptom:
                fragment = new AssociatedSymptoms();
                fragment.setTargetFragment(this, FRAGMENT_CODE);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("selected_symptoms", selected_sympts);
                bundle.putBoolean("symptoms_request", true);
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
        String item = lvsymptoms.getItemAtPosition(position).toString();
        if(patient_selected_sympts.contains(item)) {
            patient_selected_sympts.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            patient_selected_sympts.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FRAGMENT_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                ArrayList<String> list = data.getStringArrayListExtra("selected_symptoms");
                Collections.addAll(selected_sympts, list.toArray(new String[list.size()]));
            }
        }
    }*/
}