package edu.dartmouth.cs.myrun5.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.dartmouth.cs.myrun5.activities.MainActivity;
import edu.dartmouth.cs.myrun5.activities.MapsActivity;
import edu.dartmouth.cs.myrun5.database.AsyncEntriesLoader;
import edu.dartmouth.cs.myrun5.database.EntriesDataSource;
import edu.dartmouth.cs.myrun5.activities.EntryActivity;
import edu.dartmouth.cs.myrun5.database.ExerciseEntryDbHelper;
import edu.dartmouth.cs.myrun5.model.ExerciseEntry;
import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.adapters.HistoryFragmentAdapter;
import edu.dartmouth.cs.myrun5.model.ExerciseEntryFirebase;



public class HistoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<ExerciseEntry>>{
    public static final String TAG = "History_Fragment";

    public EntriesDataSource dataSource;
    public static HistoryFragmentAdapter adapter;
    public ListView entriesListView;

    public static List<ExerciseEntry> exerciseEntries = new ArrayList<>();
    private static final int ALL_ENTRIES_LOADER_ID = 1;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEntriesRef;


    //hashmap to store firebase entries
    LinkedHashMap<String,ExerciseEntryFirebase> firebaseHashMap=new LinkedHashMap<String,ExerciseEntryFirebase>();

    public long entryId;



    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //get instance of firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEntriesRef = mFirebaseDatabase.getReference("user_"+ MainActivity.userId);
        mEntriesRef.child("exercise_entries").addChildEventListener(new EntriesChildEventListener());

    }
    //child event listener to handle firebase events
    class EntriesChildEventListener implements ChildEventListener{

        @Override
        //when child is added in firebase
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "ON CHILD ADDED");
            //get entry added and put into hashmap
            ExerciseEntryFirebase entry = dataSnapshot.getValue(ExerciseEntryFirebase.class);
            firebaseHashMap.put(entry.getId(), entry);

            //convert firebase entry to exercise entry and add to database if not in database
            ExerciseEntry entry2 = firebaseToObject(entry);
            ArrayList<ExerciseEntry> entryArray = dataSource.fetchEntries();
            boolean inDatabase = false;
            for (ExerciseEntry entries: entryArray){
                if (entries.getId()==entry2.getId()){
                    inDatabase = true;
                }
            }
            if(!inDatabase){
                Log.d(TAG, "INSERTING TO DATABASE");
                dataSource.insertEntry(entry2);
            }
            updateList();

        }

        @Override
        //when child is updated, change in database
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "ON CHILD CHANGED");
            ExerciseEntryFirebase entry = dataSnapshot.getValue(ExerciseEntryFirebase.class);

            ExerciseEntry entry2 = firebaseToObject(entry);
            dataSource.updateEntry(entry2);

            firebaseHashMap.put(entry.getId(), entry);
            Log.d(TAG, "firebaseEntries change " + firebaseHashMap);
            updateList();


        }

        @Override
        //when child is removed in firebase
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "ON CHILD REMOVED");
            ExerciseEntryFirebase entry = dataSnapshot.getValue(ExerciseEntryFirebase.class);
            entryId = Long.parseLong(entry.getId());
            for (ExerciseEntry entries: exerciseEntries){
                if (entries.getId().equals(entryId)){
                    entries.setDeleted(true);
                }
            }


            firebaseHashMap.remove(entry.getId());
            Log.d(TAG, "firebaseEntries remove " + firebaseHashMap);


        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    //on click listener for each list item
    @Override
    public void onListItemClick(ListView listview, View view, int position, long id){
        super.onListItemClick(listview, view, position, id);


        //get position of entry
        ExerciseEntry entry = exerciseEntries.get(position);

        int input = entry.getInputType();

        if (input == StartFragment.INPUT_MANUAL) {
            //get all data from entry
            Long entryId = entry.getId();
            int activity = entry.getActivityType();
            Long dateTime = entry.getDateTime().getTimeInMillis();
            int duration = entry.getDuration();
            double distance = entry.getDistance();
            int calorie = entry.getCalorie();
            int heartRate = entry.getHeartRate();
            String comment = entry.getComment();

            //call intent to Entry Activity with all data
            Intent intent = new Intent(this.getActivity(), EntryActivity.class);
            intent.putExtra("index", position);
            intent.putExtra("id", entryId);
            intent.putExtra("activity", activity);
            intent.putExtra("input", input);
            intent.putExtra("dateTime", dateTime);
            intent.putExtra("duration", duration);
            intent.putExtra("distance", distance);
            intent.putExtra("calorie", calorie);
            intent.putExtra("heartRate", heartRate);
            intent.putExtra("comment", comment);
            intent.putExtra("startedFrom", "history");
            startActivity(intent);
        } else {
            Long entryId = entry.getId();
            int activity = entry.getActivityType();
            Long dateTime = entry.getDateTime().getTimeInMillis();
            int duration = entry.getDuration();
            double distance = entry.getDistance();
            int calorie = entry.getCalorie();
            double climb = entry.getClimb();
            double avgSpeed = entry.getAvgSpeed();
            double speed = entry.getSpeed();
            String locations = entry.getLatLng();


            //call intent to Entry Activity with all data
            Intent intent = new Intent(this.getActivity(), MapsActivity.class);
            intent.putExtra("index", position);
            intent.putExtra("id", entryId);
            intent.putExtra("activity", activity);
            intent.putExtra("input", input);
            intent.putExtra("dateTime", dateTime);
            intent.putExtra("duration", duration);
            intent.putExtra("distance", distance);
            intent.putExtra("calorie", calorie);
            intent.putExtra("startedFrom", "history");
            intent.putExtra("climb", climb);
            intent.putExtra("avg_speed", avgSpeed);
            intent.putExtra("speed", speed);
            intent.putExtra("locations", locations);
            startActivity(intent);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        //initialize adapter
        adapter = new HistoryFragmentAdapter(getActivity(), exerciseEntries);
        //initialize datasource
        dataSource = new EntriesDataSource(this.getActivity());
        entriesListView = view.findViewById(R.id.list_entries);
        entriesListView.setAdapter(adapter);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate layout
        View view =inflater.inflate(R.layout.historyfragment, container, false);
        return view;

    }
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        getLoaderManager().initLoader(ALL_ENTRIES_LOADER_ID, null, this).forceLoad();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

    }

    public Loader<List<ExerciseEntry>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        if (id == ALL_ENTRIES_LOADER_ID) {
            return new AsyncEntriesLoader(this.getActivity());
        }
        return null;
    }

    public void onLoadFinished(Loader<List<ExerciseEntry>> loader, List<ExerciseEntry> data){
        Log.d(TAG, "onLoadFinished");
        if (loader.getId() == ALL_ENTRIES_LOADER_ID){
            if (data.size() >= 0) {
                //clear all data entries from the exerciseEntries array and add all data
                exerciseEntries.clear();
                exerciseEntries.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ExerciseEntry>> loader){
        Log.d(TAG, "onLoaderReset");
        if (loader.getId() == ALL_ENTRIES_LOADER_ID){
            exerciseEntries.clear();
            adapter.notifyDataSetChanged();
        }
    }
    //helper method to save entries to firebase
    public void saveToFirebase(){

        //for all entries in the exerciseEntries arrayList
        for (ExerciseEntry entry: exerciseEntries){
            //if entry is not already synced
            if (entry.getSynced() == 0){
                final ExerciseEntry exerciseEntry = entry;
                //set entry synced
                entry.setSynced(1);
                dataSource.updateEntry(entry);

                //convert entry to string format to push to firebase
                ExerciseEntryFirebase firebaseEntry = exerciseEntryToString(entry);
                mFirebaseDatabase.getReference().child("user_" + MainActivity.userId)
                        .child("exercise_entries").child(firebaseEntry.getId()).setValue(firebaseEntry)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "SAVED");
                                    updateList();


                                } else {
                                    //set not synced if failed
                                    exerciseEntry.setSynced(0);
                                    dataSource.updateEntry(exerciseEntry);
                                    if(task.getException() != null)
                                        Log.w(TAG, task.getException().getMessage());
                                }
                            }
                        });
            }
            //delete entries that have been marked to be deleted
            if (entry.isDeleted()){
                Log.d(TAG, "DELETE");
                dataSource.removeEntry(entry.getId());

            }
        }
        updateList();


    }
    //helper method to convert exerciseEntry to firebase entry string format
    public static ExerciseEntryFirebase exerciseEntryToString(ExerciseEntry entry){
        String entryId = entry.getId().toString();
        String activity = EntryActivity.activityName(entry.getActivityType());
        String dateTime = Long.toString(entry.getDateTime().getTimeInMillis());
        String duration = Integer.toString(entry.getDuration());
        String distance = Double.toString(entry.getDistance());
        String calorie = Integer.toString(entry.getCalorie());
        String heartRate = Integer.toString(entry.getHeartRate());
        String comment = entry.getComment();
        String input = EntryActivity.inputName(entry.getInputType());
        String climb = Double.toString(entry.getClimb());
        String avgSpeed = Double.toString(entry.getAvgSpeed());
        String speed = Double.toString(entry.getSpeed());



        ExerciseEntryFirebase firebaseEntry = new ExerciseEntryFirebase();
        firebaseEntry.setId(entryId);
        firebaseEntry.setActivity(activity);
        firebaseEntry.setDate(dateTime);
        firebaseEntry.setDuration(duration);
        firebaseEntry.setDistance(distance);
        firebaseEntry.setCalorie(calorie);
        firebaseEntry.setHeartbeat(heartRate);
        firebaseEntry.setComment(comment);
        firebaseEntry.setInput(input);
        firebaseEntry.setSynced(entry.getSynced());
        firebaseEntry.setClimb(climb);
        firebaseEntry.setAvgSpeed(avgSpeed);
        firebaseEntry.setSpeed(speed);
        firebaseEntry.setLatLngs(entry.getLatLng());

        return firebaseEntry;
    }
    //helper method to convert firebase entry to exercise entry
    public ExerciseEntry firebaseToObject(ExerciseEntryFirebase entry){
        Long entryId = Long.parseLong(entry.getId());
        int activity = EntryActivity.activityInt(entry.getActivity());
        Long dateTime = Long.parseLong(entry.getDate());
        int duration = Integer.parseInt(entry.getDuration());
        double distance = Double.parseDouble(entry.getDistance());
        int calorie = Integer.parseInt(entry.getCalorie());
        int heartrate = Integer.parseInt(entry.getHeartbeat());
        String comment = entry.getComment();
        int input = EntryActivity.inputInt(entry.getInput());
        double climb = Double.parseDouble(entry.getClimb());
        double avgSpeed = Double.parseDouble(entry.getAvgSpeed());
        double speed = Double.parseDouble(entry.getSpeed());


        ExerciseEntry exerciseEntry = new ExerciseEntry();
        exerciseEntry.setId(entryId);
        exerciseEntry.setActivityType(activity);
        exerciseEntry.setDateTime(dateTime);
        exerciseEntry.setDuration(duration);
        exerciseEntry.setDistance(distance);
        exerciseEntry.setCalorie(calorie);
        exerciseEntry.setHeartRate(heartrate);
        exerciseEntry.setComment(comment);
        exerciseEntry.setInputType(input);
        exerciseEntry.setSynced(entry.getSynced());
        exerciseEntry.setClimb(climb);
        exerciseEntry.setAvgSpeed(avgSpeed);
        exerciseEntry.setSpeed(speed);
        exerciseEntry.setLatLng(entry.getLatLngs());
        return exerciseEntry;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sync, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                // synce to firebase
                Toast.makeText(getActivity(), "SYNCED",
                        Toast.LENGTH_SHORT).show();
                saveToFirebase();

                return true;
            default:
                break;
        }

        return false;
    }
    //helper method to update the exercise Entries list
    public void updateList(){
        Log.d(TAG, "updateList");
        exerciseEntries.clear();
        for (Map.Entry<String, ExerciseEntryFirebase> entry : firebaseHashMap.entrySet()) {
            ExerciseEntryFirebase entryFirebase = entry.getValue();
            exerciseEntries.add(firebaseToObject(entryFirebase));
        }
        adapter.notifyDataSetChanged();
    }



}