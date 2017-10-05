package com.mit.migraine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class OtherMedicalConditions extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView lvotherMedConds;
    Button skip, next, add_otherMed;

    ArrayAdapter adapter;

    List<String> conditions;
    private ArrayList<String> selected_conds;

    PersonalInformation pi;
    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    //ArrayList<Medicine> medicines;

    public OtherMedicalConditions() {
        selected_conds = new ArrayList<String>();
        //medicines = new ArrayList<Medicine>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.other_medical_conditions, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");
        Set<String> set = new HashSet<String>();
        set.addAll(selected_conds);
        set.addAll(pi.getOtherMedicalConditions());
        selected_conds = new ArrayList<String>(set);

        conditions = this.getActivity().getIntent().getStringArrayListExtra("MedicalConditions");
        //medicines = this.getActivity().getIntent().getParcelableArrayListExtra("Medicines_List");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        add_otherMed = (Button) view.findViewById(R.id.bAddOtherMedCond);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        add_otherMed.setOnClickListener(this);

        lvotherMedConds = (ListView) view.findViewById(R.id.lvMedConds);

        /*String[] conds = {"High Blood Pressure", "Diabetes", "Heart Attack/Coronary Artery Disease", "Cancer"
                , "Stroke", "Irritable Bowel Syndrome", "Thyroid Problem", "Benign Prostatic Hypertrophy", "Eating Disorders"
                , "Polycystic Ovarian Disease", "Obesity", "HIV", "Depression", "Anxiety", "Schizophrenia/Bipolar Disorder"
                , "Attention Deficit Hyperactivity Disorder", "Attention Deficit Disorder", "Panic Disorder", "Food Allergies"};

        conditions = new ArrayList<String>();
        Collections.addAll(conditions, conds);*/

        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, conditions) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = conditions.get(position);

                return (selected_conds.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
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
        lvotherMedConds.setAdapter(adapter);

        lvotherMedConds.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = lvotherMedConds.getItemAtPosition(position).toString();
        if(selected_conds.contains(item)) {
            selected_conds.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            selected_conds.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if(v == add_otherMed) {
            LayoutInflater li = LayoutInflater.from(this.getActivity());
            View promptsView = li.inflate(R.layout.prompt_other_medical_conditions, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

            // set prompt_other_medical_conditionsother_medical_conditions.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.etMedCond);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                // get user input and set it to result
                // edit text
                String newCond = userInput.getText().toString();
                if(!newCond.equals("")) {
                    conditions.add(userInput.getText().toString());
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
        } else if(v == next) {
            pi.setOtherMedicalConditions(selected_conds);
            //this.getActivity().getIntent().putExtra("Personal_Information", pi);
            //this.getActivity().getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new ListOfMedications();
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
