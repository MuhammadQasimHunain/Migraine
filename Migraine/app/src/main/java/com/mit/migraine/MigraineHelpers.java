package com.mit.migraine;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cs on 5/17/2017.
 */

public class MigraineHelpers extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView migraine_helpers;
    private Button skip, next, add_migraine_helpers;

    private List<String> helpers;

    private ArrayAdapter adapter;

    private ArrayList<String> selected_helps;

    private static final int TYPE_COUNT = 2;
    private static final int TYPE_ITEM_COLORED = 1;
    private static final int TYPE_ITEM_NORMAL = 0;

    PersonalInformation pi;
    //ArrayList<Medicine> medicines;

    public MigraineHelpers() {
        selected_helps = new ArrayList<String>();
        //medicines = new ArrayList<Medicine>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.migraine_helpers, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        add_migraine_helpers = (Button) view.findViewById(R.id.bAddMigraineHelper);

        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.getBoolean("helpers_request")) {
            selected_helps = bundle.getStringArrayList("selected_helpers");
            skip.setVisibility(View.GONE);
            next.setText("Done");
            //next.setGravity(Gravity.CENTER);
        } else {
            pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");
            Set<String> set = new HashSet<String>();
            set.addAll(selected_helps);
            set.addAll(pi.getHelpers());
            selected_helps = new ArrayList<String>(set);
            //medicines = this.getActivity().getIntent().getParcelableArrayListExtra("Medicines_List");
        }
        helpers = this.getActivity().getIntent().getStringArrayListExtra("Helpers");

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        add_migraine_helpers.setOnClickListener(this);

        migraine_helpers = (ListView) view.findViewById(R.id.lvMigraineHelpers);
        /*String[] helps = {"Sleep", "Yoga", "Exercise", "Medications"
                , "Hydration", "Glasses to Prevent Glare", "Caffeine"};

        helpers = new ArrayList<String>();
        Collections.addAll(helpers, helps);*/

        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, helpers) {
            @Override
            public int getViewTypeCount() {
                return TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position) {
                String item = helpers.get(position);

                return (selected_helps.contains(item)) ? TYPE_ITEM_COLORED : TYPE_ITEM_NORMAL;
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
        migraine_helpers.setAdapter(adapter);

        migraine_helpers.setOnItemClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //selected_helps.add(migraine_helpers.getItemAtPosition(position).toString());
        String item = migraine_helpers.getItemAtPosition(position).toString();
        if(selected_helps.contains(item)) {
            selected_helps.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        } else {
            selected_helps.add(item);
            view.setBackgroundResource(android.R.color.holo_blue_dark);
        }
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        if(v == add_migraine_helpers) {
            LayoutInflater li = LayoutInflater.from(this.getActivity());
            View promptsView = li.inflate(R.layout.prompt_migraine_helpers, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

            // set prompt_other_medical_conditions.xmlr_medical_conditions.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.etMigHelp);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // get user input and set it to result
                    // edit text
                    String newHelper = userInput.getText().toString();
                    if (!newHelper.equals("")) {
                        helpers.add(newHelper);
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
        /*} else if (v == next && next.getText().toString().equals("Next")) {
            pi.setHelpers(selected_helps);
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            fragment = new PromptSettings();*/
        } else if (v == next && next.getText().toString().equals("Done")) {
            /*Intent intent = new Intent();
            intent.putStringArrayListExtra("selected_helpers", selected_helps);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);*/
            getFragmentManager().popBackStack();
        } else if(v == next && next.getText().toString().equals("Next")) {
            pi.setHelpers(selected_helps);
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            //this.getActivity().getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new PromptSettings();
        } else if(v == skip) {
            FragmentManager fm = this.getFragmentManager();
            for (int i = 0 ; i < fm.getBackStackEntryCount() ; ++i) {
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
