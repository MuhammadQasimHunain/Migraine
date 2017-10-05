package com.mit.migraine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cs on 5/17/2017.
 */

public class HeadacheLocations extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView lvheadacheLoc;
    private Button next, skip;

    String[] locations = {"Frontal", "Temporal", "Occipital", "Behind the Eyes", "Base of Skull and Neck"};

    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    ArrayList<String> selected_locations;

    PersonalInformation pi;

    public HeadacheLocations() {
        selected_locations = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.headache_locations, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");

        // Union
        Set<String> set = new HashSet<String>();
        set.addAll(selected_locations);
        set.addAll(pi.getHeadacheLocations());
        selected_locations = new ArrayList<String>(set);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        lvheadacheLoc = (ListView) view.findViewById(R.id.lvHeadacheLoc);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, locations) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = locations[position];

                return (selected_locations.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
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
        lvheadacheLoc.setAdapter(adapter);

        lvheadacheLoc.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;

        if(v == next) {
            pi.setHeadacheLocations(selected_locations);
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            fragment = new Triggers();
        } else if(v == skip) {
            FragmentManager fm = this.getFragmentManager();
            for(int i = 0 ; i < fm.getBackStackEntryCount() ; ++i) {
                fm.popBackStack();
            }
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
        String item = lvheadacheLoc.getItemAtPosition(position).toString();
        if(selected_locations.contains(item)) {
            selected_locations.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            selected_locations.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
    }
}
