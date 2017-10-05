package com.mit.migraine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DrawerLocker, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView tvTodaysDiary, blogOut, bchangePassword, quicklyRecordMigraine;

    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private FirebaseAuth auth;
    private FirebaseUser user;
    //DatabaseReference databaseMedicine;
    DatabaseReference usersInfo;
    DatabaseReference meds;

    DatabaseReference assocSympts;
    DatabaseReference medCond;
    DatabaseReference helpers;
    DatabaseReference triggers;

    ProgressDialog progressDialog;

    PersonalInformation pi;
    ArrayList<Medicine> medicines;

    public MainActivity() {
        pi = new PersonalInformation();
        medicines = new ArrayList<Medicine>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        if(user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            progressDialog = ProgressDialog.show(this, "", "Loading...", true);
            this.getIntent().putExtra("user_id", user.getUid());
            loadData();

            tvTodaysDiary = (TextView) findViewById(R.id.tvTodaysDiary);
            tvTodaysDiary.setOnClickListener(this);

            blogOut = (TextView) findViewById(R.id.blogOut);
            blogOut.setOnClickListener(this);

            bchangePassword = (TextView) findViewById(R.id.bchangePassword);
            bchangePassword.setOnClickListener(this);

            quicklyRecordMigraine = (TextView) findViewById(R.id.tvQuickRecordMigraine);
            quicklyRecordMigraine.setOnClickListener(this);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        setDrawerEnabled(true);
                    }
                }
            });

            /*sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            int i = sharedpreferences.getInt("numberoflaunches", 1);

            if (i < 2){
                alarmMethod();
                i++;
                editor.putInt("numberoflaunches", i);
                editor.commit();
            }*/
        }
    }

    private void loadData() {
        usersInfo = FirebaseDatabase.getInstance().getReference("UsersInfo");
        usersInfo.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pi = dataSnapshot.getValue(PersonalInformation.class);
                if(pi == null) {
                    pi = new PersonalInformation();
                    pi.setTimeOfPrompt1("10:00 AM");
                    pi.setTimeOfPrompt2("19:00 PM");
                    new MyAlarmManager().alarmMethod(MainActivity.this.getApplicationContext(), "10:00 AM", "19:00 PM");
                }
                getIntent().putExtra("Personal_Information", pi);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        meds = FirebaseDatabase.getInstance().getReference("medicines");
        meds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //medicines = (ArrayList<Medicine>) dataSnapshot.getValue();

                for (DataSnapshot medicineSnapshot : dataSnapshot.getChildren()) {
                    medicines.add(medicineSnapshot.getValue(Medicine.class));
                }
                getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
                progressDialog.dismiss();

                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                if (sharedpreferences.getBoolean("New", true)) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new PersonalInfo());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("New", false);
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        assocSympts = FirebaseDatabase.getInstance().getReference("Symptoms");
        assocSympts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getIntent().putStringArrayListExtra("Symptoms", (ArrayList<String>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        medCond = FirebaseDatabase.getInstance().getReference("MedicalConditions");
        medCond.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getIntent().putStringArrayListExtra("MedicalConditions", (ArrayList<String>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        helpers = FirebaseDatabase.getInstance().getReference("Helpers");
        helpers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getIntent().putStringArrayListExtra("Helpers", (ArrayList<String>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        triggers = FirebaseDatabase.getInstance().getReference("Triggers");
        triggers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> triggers_header = new ArrayList<String>(Arrays.asList("Everyday Stressors", "Foods", "Hormonal", "Sensory Overload", "Weather", "Pollution",
                        "Recreational Substances", "Other"));
                HashMap<String, List<String>> triggers = new HashMap<String, List<String>>();
                for(int i = 0 ; i < triggers_header.size() ; i++)
                    triggers.put(triggers_header.get(i), (ArrayList<String>) dataSnapshot.child(triggers_header.get(i)).getValue());
                getIntent().putExtra("Triggers", (Serializable) triggers);
                getIntent().putStringArrayListExtra("Triggers_Header", (ArrayList<String>) triggers_header);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.homeScreen) {
            // Handle the HomeFragment action
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0 ; i < fm.getBackStackEntryCount() ; i++) {
                fm.popBackStack();
            }
        } else if (id == R.id.diaryDiary) {
            //this.getIntent().putExtra("Personal_Information", pi);
            fragment = new Page1();
        } else if (id == R.id.personalInfo) {
            //this.getIntent().putExtra("Personal_Information", pi);
            //this.getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new PersonalInfo();
        } else if (id == R.id.otherMedicalConditions) {
            fragment = new OtherMedicalConditions();
        } else if (id == R.id.listOfMedications) {
            //this.getIntent().putParcelableArrayListExtra("Medicines_List", medicines);
            fragment = new ListOfMedications();
        } else if (id == R.id.associatedSymptoms) {
            fragment = new AssociatedSymptoms();
        } else if (id == R.id.triggers) {
            fragment = new Triggers();
        } else if (id == R.id.migraineHelpers) {
            fragment = new MigraineHelpers();
        } else if (id == R.id.promptSettings) {
            fragment = new PromptSettings();
        } else if (id == R.id.missingMigraineEndTime) {

        } else if(id == R.id.logOut) {
            signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.tvTodaysDiary:
                getIntent().putExtra("Personal_Information", pi);
                fragment = new Page1();
            break;
            case R.id.blogOut:
                signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            /*case R.id.tvQuickRecordMigraine:
                fragment = new Page2();
                Bundle bundle = new Bundle();
                bundle.putBoolean("quickly_record_migraine", true);
                fragment.setArguments(bundle);
                break;*/
            case R.id.bchangePassword:
                progressDialog = ProgressDialog.show(this, "", "Updating...", true);
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.prompt_change_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                // set prompt_other_medical_conditions.xmlr_medical_conditions.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.etSelectPassword);

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        String newPassword = userInput.getText().toString();
                        if (newPassword.trim().length() < 6) {
                            Toast.makeText(MainActivity.this, "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            user.updatePassword(newPassword.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        signOut();
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        progressDialog.dismiss();
                        dialog.cancel();
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                break;
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }
}