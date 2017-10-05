package com.mit.migraine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cs on 5/16/2017.
 */

public class AssociatedSymptoms extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView lvassocSymptoms;
    private Button next, add_assoc_symp, skip;

    private ArrayAdapter<String> adapter;

    private ArrayList<String> symptoms;
    private ArrayList<String> selected_sympts;

    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    PersonalInformation pi;
    //ArrayList<Medicine> medicines;

    public AssociatedSymptoms() {
        selected_sympts = new ArrayList<String>();
        //medicines = new ArrayList<Medicine>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.associated_symptoms, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        add_assoc_symp = (Button) view.findViewById(R.id.bAddAssocSymptom);

        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.getBoolean("symptoms_request")) {
            selected_sympts = bundle.getStringArrayList("selected_symptoms");
            skip.setVisibility(View.GONE);
            next.setText("Done");
            //next.setGravity(Gravity.CENTER);
        } else {
            pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");
            // Union
            Set<String> set = new HashSet<String>();
            set.addAll(selected_sympts);
            set.addAll(pi.getAssociatedSymptoms());
            selected_sympts = new ArrayList<String>(set);
            //medicines = this.getActivity().getIntent().getParcelableArrayListExtra("Medicines_List");
        }
        symptoms = this.getActivity().getIntent().getStringArrayListExtra("Symptoms");

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        add_assoc_symp.setOnClickListener(this);

        lvassocSymptoms = (ListView) view.findViewById(R.id.lvAssocSymptoms);

        /*String[] syms = {"Sensitivity to Light", "Sensitivity to Sound", "Sensitivity to Smells", "Dizziness"
                , "Moodiness/Irritability", "Fatigue", "Cravings", "Tinnitus", "Fever", "Decreased Appetite", "Nausea/Vomiting"
                , "Pallor", "Hot/Cold Sensation", "Body Pain", "Seizures", "Abdominal Pain"};

        symptoms = new ArrayList<String>();
        Collections.addAll(symptoms, syms);*/

        // adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, symptoms);
        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, symptoms) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = symptoms.get(position);

                return (selected_sympts.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
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
        lvassocSymptoms.setAdapter(adapter);
        lvassocSymptoms.setOnItemClickListener(this);

        return view;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //selected_sympts.add(AssocSymptoms.getItemAtPosition(position).toString());
        //AssocSymptoms.setItemChecked(position, true);
        //AssocSymptoms.setSelector(android.R.color.holo_blue_dark);

        String item = lvassocSymptoms.getItemAtPosition(position).toString();
        if(selected_sympts.contains(item)) {
            selected_sympts.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            selected_sympts.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
        //yourarraylist.add((String) getListAdapter().getItem(position);)
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        if(v == add_assoc_symp) {
            LayoutInflater li = LayoutInflater.from(this.getActivity());
            View promptsView = li.inflate(R.layout.prompt_associated_symptoms, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

            // set prompt_other_medical_conditions.xmlr_medical_conditions.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.etAssocSympt);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                // get user input and set it to result
                // edit text
                String newSympt = userInput.getText().toString();
                if (!newSympt.equals("")) {
                    symptoms.add(newSympt);
                    adapter.notifyDataSetChanged();
                }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                }
            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else if(v == next && next.getText().toString().equals("Done")) {
            /*Intent intent = new Intent();
            intent.putStringArrayListExtra("selected_symptoms", selected_sympts);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);*/
            getFragmentManager().popBackStack();
        } else if(v == next && next.getText().toString().equals("Next")) {
            pi.setAssociatedSymptoms(selected_sympts);
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            //this.getActivity().getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new HeadacheLocations();
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
}
