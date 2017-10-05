package com.mit.migraine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cs on 5/16/2017.
 */

public class HeadacheInfo extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    ListView lvheadacheLocation;
    EditText headacheDuration;
    Button next, skip, done;

    ArrayList<String> selected_headachetypes;

    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    PersonalInformation pi;

    String[] locations = {"One side of the head", "Both sides of the head", "Pulsating/Throbbing", "Stabbing", "Band like Tension"};

    ArrayList<Medicine> medicines;

    public HeadacheInfo() {
        selected_headachetypes = new ArrayList<String>();
        medicines = new ArrayList<Medicine>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.headache_info, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");
        Set<String> set = new HashSet<String>();
        set.addAll(selected_headachetypes);
        set.addAll(pi.getHeadacheTypes());
        selected_headachetypes = new ArrayList<String>(set);

        medicines = this.getActivity().getIntent().getParcelableArrayListExtra("Medicines_List");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        headacheDuration = (EditText) view.findViewById(R.id.etHours);

        headacheDuration.setText(pi.getHeadacheLostFor());

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        done = (Button) view.findViewById(R.id.bDone);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        done.setOnClickListener(this);

        lvheadacheLocation = (ListView) view.findViewById(R.id.lvMeds);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, locations) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = locations[position];

                return (selected_headachetypes.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
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
        lvheadacheLocation.setAdapter(adapter);

        lvheadacheLocation.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if(v == next) {
            String dur = headacheDuration.getText().toString();
            if(!dur.equals(""))
                pi.setHeadacheLostFor(dur);
            pi.setHeadacheTypes(selected_headachetypes);
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            //this.getActivity().getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new AssociatedSymptoms();
        } else if(v == skip) {
            FragmentManager fm = this.getFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        } else if (v == done) {
            InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(headacheDuration.getWindowToken(), 0);
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
        String item = lvheadacheLocation.getItemAtPosition(position).toString();
        if(selected_headachetypes.contains(item)) {
            selected_headachetypes.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            selected_headachetypes.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
    }
}
