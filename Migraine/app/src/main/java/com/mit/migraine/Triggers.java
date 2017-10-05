package com.mit.migraine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

/**
 * Created by cs on 5/11/2017.
 */

public class Triggers extends Fragment implements ExpandableListView.OnGroupClickListener
, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnChildClickListener, View.OnClickListener {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    Button skip, next, add_trig;
    List<String> other;
    ArrayList<String> selected_triggers;

    PersonalInformation pi;
    //ArrayList<Medicine> medicines;

    public Triggers() {
        selected_triggers = new ArrayList<String>();
        //medicines = new ArrayList<Medicine>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_triggers, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        add_trig = (Button) view.findViewById(R.id.bAddTrigger);

        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.getBoolean("triggers_request")) {
            selected_triggers = bundle.getStringArrayList("selected_triggers");
            skip.setVisibility(View.GONE);
            next.setText("Done");
            //next.setGravity(Gravity.CENTER);
        } else {
            pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");
            Set<String> set = new HashSet<String>();
            set.addAll(selected_triggers);
            set.addAll(pi.getTriggers());
            selected_triggers = new ArrayList<String>(set);
            //medicines = this.getActivity().getIntent().getParcelableArrayListExtra("Medicines_List");
        }
        listDataChild = (HashMap<String, List<String>>) this.getActivity().getIntent().getSerializableExtra("Triggers");
        listDataHeader = this.getActivity().getIntent().getStringArrayListExtra("Triggers_Header");

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        add_trig.setOnClickListener(this);

        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);

        // preparing list data
        // prepareListData();

        listAdapter = new ExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild, selected_triggers);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(this);

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(this);

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(this);

        // Listview on child click listener
        expListView.setOnChildClickListener(this);

        return view;
    }

    /*
     * Preparing the list data
     */
    /*private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Everyday Stressors");
        listDataHeader.add("Foods");
        listDataHeader.add("Hormonal");
        listDataHeader.add("Sensory Overload");
        listDataHeader.add("Weather");
        listDataHeader.add("Pollution");
        listDataHeader.add("Recreational Substances");
        listDataHeader.add("Other");

        // Adding child data
        List<String> everyday_stessors = new ArrayList<String>();
        everyday_stessors.add("Emotional Stress");
        everyday_stessors.add("Hunger");
        everyday_stessors.add("Dehydration");
        everyday_stessors.add("Gaps in Between Meals");
        everyday_stessors.add("Sexual Activity");
        everyday_stessors.add("Infections");
        everyday_stessors.add("Too Much Sleep");
        everyday_stessors.add("Lack of Sleep");
        everyday_stessors.add("Tiring Activity");
        everyday_stessors.add("Exercise");

        List<String> foods = new ArrayList<String>();
        foods.add("TMSG");
        foods.add("Onions");
        foods.add("Citrus/Bananas");
        foods.add("Cheese");
        foods.add("Chocolate");
        foods.add("Nitrites");
        foods.add("Processed Foods");
        foods.add("Gluten");
        foods.add("Tyramine");
        foods.add("Dyes in Food");
        foods.add("Artificial Sweetners");
        foods.add("Aspartame");
        foods.add("Saccharin");
        foods.add("Sucralose - Chlorinated Sucrose");

        List<String> hormonal = new ArrayList<String>();
        hormonal.add("Menstruation");
        hormonal.add("Birth Control Pill");

        List<String> sensory_overload = new ArrayList<String>();
        sensory_overload.add("Light");
        sensory_overload.add("Noise");
        sensory_overload.add("Motion");
        sensory_overload.add("Perfumes");

        List<String> weather = new ArrayList<String>();
        weather.add("High Barometric Pressure");
        weather.add("High Humidity");
        weather.add("High Temperature");
        weather.add("Wind");
        weather.add("Change in Temperature");
        weather.add("Cold Temperature");
        weather.add("Lightning");
        weather.add("Drop in Barometric Pressure");
        weather.add("Flying");

        List<String> pollution = new ArrayList<String>();
        pollution.add("Smoke");

        List<String> recreational_substances = new ArrayList<String>();
        recreational_substances.add("Cigarette Smoking");
        recreational_substances.add("Alcohol");
        recreational_substances.add("Cocaine");
        recreational_substances.add("Marijuana");
        recreational_substances.add("Heroin");
        recreational_substances.add("Ritalin");
        recreational_substances.add("Amphetamine");

        other = new ArrayList<String>();
        pollution.add("Headache Medication (Medication Rebound)");

        listDataChild.put(listDataHeader.get(0), everyday_stessors); // Header, Child data
        listDataChild.put(listDataHeader.get(1), foods);
        listDataChild.put(listDataHeader.get(2), hormonal);
        listDataChild.put(listDataHeader.get(3), sensory_overload);
        listDataChild.put(listDataHeader.get(4), weather);
        listDataChild.put(listDataHeader.get(5), pollution);
        listDataChild.put(listDataHeader.get(6), recreational_substances);
        listDataChild.put(listDataHeader.get(7), other);
    }*/

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        // Toast.makeText(getApplicationContext(),
        // "Group Clicked " + listDataHeader.get(groupPosition),
        // Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        // Toast.makeText(this.getActivity().getApplicationContext(), listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        // Toast.makeText(this.getActivity().getApplicationContext(), listDataHeader.get(groupPosition) + " Collapsed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        // TODO Auto-generated method stub
        /*Toast.makeText(this.getActivity().getApplicationContext(), listDataHeader.get(groupPosition)
                        + " : "
                        + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();*/
        String item = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).toString();
        if(selected_triggers.contains(item)) {
            selected_triggers.remove(item);
            v.setBackgroundResource(android.R.color.transparent);
        } else {
            selected_triggers.add(item);
            v.setBackgroundResource(android.R.color.holo_blue_dark);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        if(v == add_trig) {
            LayoutInflater li = LayoutInflater.from(this.getActivity());
            View promptsView = li.inflate(R.layout.prompt_add_triggers, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

            // set prompt_other_medical_conditions.xmlr_medical_conditions.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.etTrigger);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // get user input and set it to result
                    // edit text
                    String newTrigger = userInput.getText().toString();
                    if (!newTrigger.equals("")) {
                        other.add(newTrigger);
                        listAdapter.notifyDataSetChanged();
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
        } else if (v == next && next.getText().toString().equals("Next")) {
            pi.setTriggers(selected_triggers);
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            //this.getActivity().getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new MigraineHelpers();
            Bundle bundle = new Bundle();
            bundle.putBoolean("helpers_request", false);
            fragment.setArguments(bundle);
        } else if (v == next && next.getText().toString().equals("Done")) {
            /*Intent intent = new Intent();
            intent.putStringArrayListExtra("selected_helpers", selected_helps);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);*/
            getFragmentManager().popBackStack();
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