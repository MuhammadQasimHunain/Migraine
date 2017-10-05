package com.mit.migraine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cs on 5/16/2017.
 */

public class ListOfMedications extends Fragment implements View.OnClickListener, TextWatcher {

    ListView lvmedications;
    private Button next, skip, add_med;
    private AutoCompleteTextView medName;

    ArrayList<String> quantities;

    private ArrayAdapter<String> adapter;

    private ArrayList<String> selected_medicines;
    private List<PatientMedicines> selected_meds;

    PersonalInformation pi;
    ArrayList<Medicine> medicines;

    private DatabaseReference usersInfo;

    public ListOfMedications() {
        selected_medicines = new ArrayList<String>();
        medicines = new ArrayList<Medicine>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_of_medications, container, false);

        pi = (PersonalInformation) this.getActivity().getIntent().getSerializableExtra("Personal_Information");

        selected_meds = pi.getListOfMedications();
        selected_medicines = new ArrayList<String>();
        for(int i = 0 ; i < selected_meds.size() ; i++)
            selected_medicines.add(selected_meds.get(i).getMed_name());

        medicines = this.getActivity().getIntent().getParcelableArrayListExtra("Medicines_List");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        skip = (Button) view.findViewById(R.id.bSkip);
        next = (Button) view.findViewById(R.id.bNext);
        add_med = (Button) view.findViewById(R.id.bAddMed);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        add_med.setOnClickListener(this);

        medName = (AutoCompleteTextView) view.findViewById(R.id.mactvMed);

        medName.addTextChangedListener(this);

        lvmedications = (ListView) view.findViewById(R.id.lvMeds);

        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, selected_medicines);

        // Assign adapter to ListView
        lvmedications.setAdapter(adapter);
        registerForContextMenu(lvmedications);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        if(v == add_med) {
            String text = medName.getText().toString();

            quantities = new ArrayList<String>();
            quantities.add("Select Med. Strength...");
            String[] sts = new String[0];
            for (int i = 0 ; i < medicines.size() ; i++) {
                if (medicines.get(i).getMed_name().contains(text)) {
                    sts = medicines.get(i).getMed_strength().split(",");
                    break;
                }
            }

            Collections.addAll(quantities, sts);
            if(!medName.getText().toString().equals("")) {
                LayoutInflater li = LayoutInflater.from(this.getActivity());
                View promptsView = li.inflate(R.layout.prompt_list_of_medications, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

                alertDialogBuilder.setView(promptsView);

                final Spinner quantity = (Spinner) promptsView.findViewById(R.id.etQuantity);

                ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, quantities);
                quantity.setAdapter(sadapter);

                final EditText frequency = (EditText) promptsView.findViewById(R.id.etFrequency);

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        String name = medName.getText().toString();
                        String strength = quantity.getSelectedItem().toString();
                        String quant = frequency.getText().toString();
                        if (!strength.equals("Select Med. Strength") && !quant.equals("")) {
                            PatientMedicines pm = new PatientMedicines();
                            pm.setMed_name(name);
                            pm.setMed_strength(strength);
                            pm.setMed_quantitily(quant);
                            selected_meds.add(pm);
                            selected_medicines.add(name);
                            adapter.notifyDataSetChanged();
                            medName.setText("");
                        } else
                            Toast.makeText(getActivity(), "Please select strength and frequency.", Toast.LENGTH_SHORT).show();
                        //medName.setText("");
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        } else if(v == next) {
            pi.setListOfMedications(selected_meds);
            this.getActivity().getIntent().putExtra("Personal_Information", pi);
            //this.getActivity().getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new HeadacheInfo();
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // new SearchMedicines(this.getActivity(), medName).execute(s.toString());

        ArrayList<String> m = new ArrayList<String>();
        for (int i = 0 ; i < medicines.size() ; i++) {
            if (medicines.get(i).getMed_name().toLowerCase().contains(s.toString().toLowerCase())) {
                m.add(medicines.get(i).getMed_name());
            }
        }
        ArrayList<String> meds = new ArrayList<String>();
        ArrayAdapter<String> mAdapter;
        if(m.isEmpty()){
            meds.add("No Medicine Found!");
            mAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, meds);
            medName.setAdapter(mAdapter);
        } else {
            for(int i = 0 ; i < m.size() ; i++){
                meds.add(m.get(i));
            }
            mAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, meds);
            medName.setAdapter(mAdapter);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "View Details");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getTitle()=="View Details") {
            LayoutInflater li = LayoutInflater.from(this.getActivity());
            View promptsView = li.inflate(R.layout.prompt_view_medicine_details, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
            alertDialogBuilder.setView(promptsView);

            final TextView med_name = (TextView) promptsView.findViewById(R.id.tvName);
            final TextView med_strength = (TextView) promptsView.findViewById(R.id.tvStrength);
            final TextView med_frequency = (TextView) promptsView.findViewById(R.id.tvFrequency);

            med_name.setText(selected_meds.get(info.position).getMed_name());
            med_strength.setText(selected_meds.get(info.position).getMed_strength());
            med_frequency.setText(selected_meds.get(info.position).getMed_quantity());

            alertDialogBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // get user input and set it to result
                    // edit text
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setTitle("Details");
            alertDialog.show();
        }
        else if(item.getTitle()=="Delete") {
            selected_meds.remove(info.position);
            pi.setListOfMedications(selected_meds);
            selected_medicines.remove(info.position);
            adapter.notifyDataSetChanged();

            usersInfo = FirebaseDatabase.getInstance().getReference("UsersInfo");
            usersInfo.child(this.getActivity().getIntent().getStringExtra("user_id")).child("listOfMedications").setValue(selected_meds);
        }
        return true;
    }

    /*String item = lvmedications.getItemAtPosition(position).toString();

    PatientMedicines pm = new PatientMedicines();
    selected_meds = pi.getListOfMedications();
        for(int i = 0 ; i < selected_meds.size() ; i++) {
        if(selected_meds.get(i).getMed_name().equals(item)) {
            pm = selected_meds.get(i);
            break;
        }
    }*/

    /*public class SearchMedicines extends AsyncTask<String, Integer, String> implements AdapterView.OnItemClickListener {

        ArrayList<Medicine> m;
        Database db;
        Activity mContex;
        AutoCompleteTextView mAutoCompleteTextView;

        public SearchMedicines(Activity context, AutoCompleteTextView textView) {
            mContex = context;
            mAutoCompleteTextView = textView;
            m = new ArrayList<Medicine>();
        }

        @Override
        protected String doInBackground(String... params) {
            db = new Database(mContex);
            db.Open();
            m = db.getMedicines(params[0]);
            db.Close();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            ArrayList<String> meds = new ArrayList<String>();
            ArrayAdapter<String> mAdapter;
            if(m.isEmpty()){
                meds.add("No Medicine Found!");
                mAdapter = new ArrayAdapter<String>(mContex, android.R.layout.simple_list_item_1, android.R.id.text1, meds);
                mAutoCompleteTextView.setAdapter(mAdapter);
            } else {
                for(int i = 0 ; i < m.size() ; i++){
                    meds.add(m.get(i).getMed_name());
                }
                mAdapter = new ArrayAdapter<String>(mContex, android.R.layout.simple_list_item_1, android.R.id.text1, meds);
                mAutoCompleteTextView.setAdapter(mAdapter);
                mAutoCompleteTextView.setOnItemClickListener(this);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = mAutoCompleteTextView.getText().toString();

            db = new Database(mContex);
            db.Open();
            Medicine med = db.getMedicine(text);
            db.Close();

            quantities = new ArrayList<String>();
            quantities.add("Select Med. Strength...");
            String[] sts = med.getMed_strength().split(",");

            Collections.addAll(quantities, sts);
        }
    }*/
}
