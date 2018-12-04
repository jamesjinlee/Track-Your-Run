package edu.dartmouth.cs.myrun5.activities;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.dartmouth.cs.myrun5.Fragments.HistoryFragment;
import edu.dartmouth.cs.myrun5.Fragments.MyRunsDialogFragment;
import edu.dartmouth.cs.myrun5.Fragments.StartFragment;
import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.adapters.ManualEntryAdapter;
import edu.dartmouth.cs.myrun5.database.EntriesDataSource;
import edu.dartmouth.cs.myrun5.database.ExerciseEntryDbHelper;
import edu.dartmouth.cs.myrun5.model.ExerciseEntry;
import edu.dartmouth.cs.myrun5.model.ManualEntry;


public class EntryActivity extends AppCompatActivity {
    public static final String TAG = "Manual_Activity";

    // used to call the dialog for each item
    static final int LIST_DATE = 1;
    static final int LIST_TIME = 2;
    static final int LIST_DURATION = 3;
    static final int LIST_DISTANCE = 4;
    static final int LIST_CALORIE = 5;
    static final int LIST_HEARTBEAT = 6;
    static final int LIST_COMMENT = 7;

    static final double CONVERTER = 0.621;

    // set up the list view
    ArrayList<ManualEntry> manualEntries;
    ManualEntryAdapter manualEntryAdapter;
    private ListView mWorkoutList;

    // entry from database
    private long entryId;
    private int entryPosition;


    //content of each item in the list
    private int activityContent;
    private int inputContent;
    private String dateContent;
    private String timeContent;
    private String durationContent;
    private String distanceContent;
    private String calorieContent;
    private String heartbeatContent;
    private String commentContent;
    private Calendar mDateAndTime;

    //database
    public ExerciseEntryDbHelper databaseHelper;
    public ExerciseEntry entry;
    public EntriesDataSource datasource;

    private String calledFrom;          // where this activity is called from
    public String units;                // mile or kms

    private DatabaseReference ref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set units from settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        units = prefs.getString("Unit Preference","-1");
        if (units.equals("-1")){
            units = "kms";
        }

        //set up the database
        databaseHelper = new ExerciseEntryDbHelper(this);
        datasource = new EntriesDataSource(this);


        //if started from history fragment, set all data
        calledFrom = getIntent().getStringExtra("startedFrom");
        if (calledFrom.equals("history")) {
            entryPosition = getIntent().getIntExtra("index", 0);
            entryId = getIntent().getLongExtra("id", 0);
            int activity = getIntent().getIntExtra("activity", 0);
            int input = getIntent().getIntExtra("input", 0);
            long dateTime = getIntent().getLongExtra("dateTime", 0);
            int duration = getIntent().getIntExtra("duration", 0);
            double distance = getIntent().getDoubleExtra("distance", 0);
            int calorie = getIntent().getIntExtra("calorie", 0);
            int heartRate = getIntent().getIntExtra("heartRate", 0);
            String comment = getIntent().getStringExtra("comment");

            activityContent = activity;
            inputContent = input;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dateTime);
            dateContent = dateFormat.format(cal.getTime());
            timeContent = timeFormat.format(cal.getTime());



            durationContent = Integer.toString(duration);
            distanceContent = Double.toString(distance);
            calorieContent = Integer.toString(calorie);
            heartbeatContent = Integer.toString(heartRate);
            commentContent = comment;

        // if started from the main activity start fragment
        } else {

            //retrieve activity from the spinner of the previous page
            activityContent = getIntent().getExtras().getInt(StartFragment.ACTIVITY_TYPE);
            inputContent = getIntent().getExtras().getInt(StartFragment.INPUT_TYPE);
            //set text for the content of items in list
            mDateAndTime = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dateContent = df.format(mDateAndTime.getTime());
            SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
            timeContent = tf.format(mDateAndTime.getTime());

            //initialize all to 0/""
            durationContent = "0";
            distanceContent = "0";
            calorieContent = "0";
            heartbeatContent = "0";
            commentContent = "";

            entry = new ExerciseEntry();
            entry.setActivityType(activityContent);
            entry.setInputType(inputContent);

        }
        //load content for when rotated
        if (savedInstanceState != null) {
            dateContent = savedInstanceState.getString("date");
            timeContent = savedInstanceState.getString("time");
            durationContent = savedInstanceState.getString("duration");
            distanceContent = savedInstanceState.getString("distance");
            calorieContent = savedInstanceState.getString("calorie");
            heartbeatContent = savedInstanceState.getString("heartbeat");
            commentContent = savedInstanceState.getString("comment");
        }

        mWorkoutList = findViewById(R.id.list_workout);


        //create list of entries
        manualEntries = new ArrayList<ManualEntry>();
        manualEntries.add(new ManualEntry("Activity", activityName(activityContent)));
        manualEntries.add(new ManualEntry("Date", dateContent));
        manualEntries.add(new ManualEntry("Time", timeContent));
        manualEntries.add(new ManualEntry("Duration", durationContent + " mins"));
        manualEntries.add(new ManualEntry("Distance", distanceContent + " " + units));
        manualEntries.add(new ManualEntry("Calorie", calorieContent + " cal"));
        manualEntries.add(new ManualEntry("Heartbeat", heartbeatContent + " bpm"));
        manualEntries.add(new ManualEntry("Comment", commentContent));

        manualEntryAdapter = new ManualEntryAdapter(this, manualEntries);
        mWorkoutList.setAdapter(manualEntryAdapter);

        //lister for each item in list if came from main activity
        if (calledFrom.equals("main")) {
            mWorkoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });
            AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //call dialog when clicked
                    switch (position) {
                        case LIST_DATE:
                            DialogFragment dateFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.DIALOG_ENTRY_DATE);
                            dateFragment.show(getFragmentManager(), getString(R.string.dialog));
                            break;
                        case LIST_TIME:
                            DialogFragment timeFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.DIALOG_ENTRY_TIME);
                            timeFragment.show(getFragmentManager(), getString(R.string.dialog));
                            break;
                        case LIST_DURATION:
                            DialogFragment durationFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.DIALOG_ENTRY_DURATION);
                            durationFragment.show(getFragmentManager(), getString(R.string.dialog));
                            break;
                        case LIST_DISTANCE:
                            DialogFragment distanceFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.DIALOG_ENTRY_DISTANCE);
                            distanceFragment.show(getFragmentManager(), getString(R.string.dialog));
                            break;
                        case LIST_CALORIE:
                            DialogFragment calorieFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.DIALOG_ENTRY_CALORIE);
                            calorieFragment.show(getFragmentManager(), getString(R.string.dialog));
                            break;
                        case LIST_HEARTBEAT:
                            DialogFragment heartbeatFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.DIALOG_ENTRY_HEARTBEAT);
                            heartbeatFragment.show(getFragmentManager(), getString(R.string.dialog));
                            break;
                        case LIST_COMMENT:
                            DialogFragment commentFragment = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.DIALOG_ENTRY_COMMENT);
                            commentFragment.show(getFragmentManager(), getString(R.string.dialog));
                            break;
                    }
                }
            };
            mWorkoutList.setOnItemClickListener(mListener);
        }
    }

    // return name of input given input int
    public static String inputName(int a){
        String input;
        switch(a){
            case StartFragment.INPUT_MANUAL:
                input = "Manual";
                break;
            case StartFragment.INPUT_GPS:
                input = "GPS";
                break;
            case StartFragment.INPUT_AUTOMATIC:
                input = "Automatic";
                break;
            default:
                input = "none";
                break;
        }
        return input;
    }
    // return input int given name
    public static int inputInt(String input){
        int inputInt;
        switch(input){
            case "Manual":
                inputInt = 0;
                break;
            case "GPS":
                inputInt = 1;
                break;
            case "Automatic":
                inputInt = 2;
                break;
            default:
                inputInt = 0;
                break;
        }
        return inputInt;
    }

    // return name of activity given activity int
    public static String activityName(int a){
        String activityName;
        switch(a){
            case StartFragment.ACTIVITY_RUNNING:
                activityName =  "Running";
                break;
            case StartFragment.ACTIVITY_WALKING:
                activityName = "Walking";
                break;
            case StartFragment.ACTIVITY_STANDING:
                activityName = "Standing";
                break;
            case StartFragment.ACTIVITY_CYCLING:
                activityName = "Cycling";
                break;
            case StartFragment.ACTIVITY_HIKING:
                activityName = "Hiking";
                break;
            case StartFragment.ACTIVITY_DOWNHILL_SKIING:
                activityName = "Downhill Skiing";
                break;
            case StartFragment.ACTIVITY_CROSS_COUNTRY_SKIING:
                activityName = "Cross Country Skiing";
                break;
            case StartFragment.ACTIVITY_SNOWBOARDING:
                activityName = "Snowboarding";
                break;
            case StartFragment.ACTIVITY_SKATING:
                activityName = "Skating";
                break;
            case StartFragment.ACTIVITY_SWIMMING:
                activityName = "Swimming";
                break;
            case StartFragment.ACTIVITY_MOUNTAIN_BIKING:
                activityName = "Mountain Biking";
                break;
            case StartFragment.ACTIVITY_WHEELCHAIR:
                activityName = "Wheelchair";
                break;
            case StartFragment.ACTIVITY_ELLIPTICAL:
                activityName = "Elliptical";
                break;
            case StartFragment.ACTIVITY_OTHER:
                activityName = "Other";
                break;
            case StartFragment.ACTIVITY_IN_VEHICLE:
                activityName = "In Vehicle";
                break;
            case StartFragment.ACTIVITY_STILL:
                activityName = "Still";
                break;
            case StartFragment.ACTIVITY_ON_FOOT:
                activityName = "On Foot";
                break;
            case StartFragment.ACTIVITY_UNKNOWN:
                activityName = "Unknown";
                break;

            default:
                activityName = "none";
                break;
        }
        return activityName;
    }
    // return activity int given activity string
    public static int activityInt(String a){
        int activityName;
        switch(a){
            case "Running":
                activityName =  0;
                break;
            case "Walking":
                activityName = 1;
                break;
            case "Standing":
                activityName = 2;
                break;
            case "Cycling":
                activityName = 3;
                break;
            case "Hiking":
                activityName = 4;
                break;
            case "Downhill Skiing":
                activityName = 5;
                break;
            case "Cross Country Skiing":
                activityName = 6;
                break;
            case "Snowboarding":
                activityName = 7;
                break;
            case "Skating":
                activityName = 8;
                break;
            case "Swimming":
                activityName = 9;
                break;
            case "Mountain Biking":
                activityName = 10;
                break;
            case "Wheelchair":
                activityName = 11;
                break;
            case "Elliptical":
                activityName = 12;
                break;
            case "Other":
                activityName = 13;
                break;
            case "In Vehicle":
                activityName = 14;
                break;
            case "Still":
                activityName = 15;
                break;
            case "On Foot":
                activityName = 16;
                break;
            case "Unknown":
                activityName = 17;
                break;

            default:
                activityName = 18;
                break;
        }
        return activityName;
    }


//***************************************** Menu Items *******************************************//
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        if (calledFrom.equals("history")){
            inflater.inflate(R.menu.delete, menu);
        } else {
            inflater.inflate(R.menu.save, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            // save entry
            case R.id.save:
                Log.d(TAG, "save");
                entry.setDateTime(mDateAndTime);
                entry.setDuration(Integer.parseInt(durationContent));
                //store distance as kilometers
                if (units.equals("kms")) {
                    entry.setDistance(Double.parseDouble(distanceContent));
                } else {
                    DecimalFormat decFormat = new DecimalFormat("#.#");
                    double distanceKms = Double.parseDouble(decFormat.format(Double.parseDouble(distanceContent)/CONVERTER));
                    entry.setDistance(distanceKms);
                }

                entry.setCalorie(Integer.parseInt(calorieContent));
                entry.setHeartRate(Integer.parseInt(heartbeatContent));
                entry.setComment(commentContent);
                AsyncWriter asyncWriter = new AsyncWriter();
                asyncWriter.execute();

                break;
            // delete entry
            case R.id.delete:
                AsyncEntriesDeleter asyncDeleter = new AsyncEntriesDeleter();
                asyncDeleter.execute();

                //delete item in firebase
                ref = FirebaseDatabase.getInstance().getReference("user_"+ MainActivity.userId).child("exercise_entries");
                ref.child(Long.toString(entryId)).removeValue();
                Log.d(TAG, "FIREBASE DELETE " + Long.toString(entryId));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


//*********************************** OnSaveInstanceState ****************************************//
    // save for when rotated
    public void onSaveInstanceState(Bundle outstate){
        super.onSaveInstanceState(outstate);
        outstate.putString("date", dateContent);
        outstate.putString("time", timeContent);
        outstate.putString("duration", durationContent);
        outstate.putString("distance", distanceContent);
        outstate.putString("calorie", calorieContent);
        outstate.putString("heartbeat", heartbeatContent);
        outstate.putString("comment", commentContent);
    }

//*************************** Helper functions to update list view data **************************//

    //helper function: to update the adapter
    public void updateAdapter(int position, ManualEntry manualEntry){
        manualEntries.remove(position);
        manualEntries.add(position, manualEntry);
        manualEntryAdapter = new ManualEntryAdapter(this, manualEntries);
        mWorkoutList.setAdapter(manualEntryAdapter);
    }

    //helper functions to set content
    public void setDate(Calendar c, int position){
        mDateAndTime = c;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(c.getTime());
        ManualEntry manualEntry = manualEntryAdapter.getItem(position);
        manualEntry.setContent(date);
        updateAdapter(position, manualEntry);
        dateContent = date;

    }
    public void setTime(Calendar c, int position){
        mDateAndTime = c;
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        String time = tf.format(c.getTime());
        ManualEntry manualEntry = manualEntryAdapter.getItem(position);
        manualEntry.setContent(time);
        updateAdapter(position, manualEntry);
        timeContent = time;
    }
    public void setDuration(String duration, int position){
        ManualEntry manualEntry = manualEntryAdapter.getItem(position);
        manualEntry.setContent(duration + " mins");
        updateAdapter(position, manualEntry);
        durationContent = duration;

    }
    public void setDistance(String distance, int position){
        ManualEntry manualEntry = manualEntryAdapter.getItem(position);
        manualEntry.setContent(distance + " " + units);
        updateAdapter(position, manualEntry);
        distanceContent = distance;
    }
    public void setCalorie(String calorie, int position){
        ManualEntry manualEntry = manualEntryAdapter.getItem(position);
        manualEntry.setContent(calorie + " cals");
        updateAdapter(position, manualEntry);
        calorieContent = calorie;
    }
    public void setHeartbeat(String heartbeat, int position){
        ManualEntry manualEntry = manualEntryAdapter.getItem(position);
        manualEntry.setContent(heartbeat + " bpm");
        updateAdapter(position, manualEntry);
        heartbeatContent = heartbeat;
    }
    public void setComment(String comment, int position){
        ManualEntry manualEntry = manualEntryAdapter.getItem(position);
        manualEntry.setContent(comment);
        updateAdapter(position, manualEntry);
        commentContent = comment;
    }

//********************************** Async Task Functions ***************************************//

    class AsyncWriter extends AsyncTask<ExerciseEntry, Void, Void> {
        @Override
        protected Void doInBackground(ExerciseEntry... params){
            Log.d(TAG, "AsyncWriter adding entry");
            entry.setId(System.currentTimeMillis());
            datasource.insertEntry(entry);
            HistoryFragment.exerciseEntries.add(entry);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            HistoryFragment.adapter.notifyDataSetChanged();
            finish();
        }
    }
    public class AsyncEntriesDeleter extends AsyncTask<ExerciseEntry, Void, Void> {
        @Override
        protected Void doInBackground(ExerciseEntry... params) {
            EntriesDataSource dataSource = new EntriesDataSource(EntryActivity.this);
            dataSource.removeEntry(entryId);
            HistoryFragment.exerciseEntries.remove(entryPosition);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            HistoryFragment.adapter.notifyDataSetChanged();
            finish();
        }
    }

}