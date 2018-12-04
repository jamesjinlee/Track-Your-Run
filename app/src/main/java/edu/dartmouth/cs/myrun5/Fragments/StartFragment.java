package edu.dartmouth.cs.myrun5.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import edu.dartmouth.cs.myrun5.activities.EntryActivity;
import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.activities.MapsActivity;
import edu.dartmouth.cs.myrun5.activities.RegisterActivity;
import edu.dartmouth.cs.myrun5.activities.SettingsActivity;



public class StartFragment extends Fragment {

    public static final String INPUT_TYPE = "inputType";
    public static final String ACTIVITY_TYPE = "activityType";
    public static final int INPUT_MANUAL = 0;
    public static final int INPUT_GPS = 1;
    public static final int INPUT_AUTOMATIC = 2;
    public static final int ACTIVITY_RUNNING = 0;
    public static final int ACTIVITY_WALKING = 1;
    public static final int ACTIVITY_STANDING = 2;
    public static final int ACTIVITY_CYCLING = 3;
    public static final int ACTIVITY_HIKING = 4;
    public static final int ACTIVITY_DOWNHILL_SKIING = 5;
    public static final int ACTIVITY_CROSS_COUNTRY_SKIING = 6;
    public static final int ACTIVITY_SNOWBOARDING = 7;
    public static final int ACTIVITY_SKATING = 8;
    public static final int ACTIVITY_SWIMMING = 9;
    public static final int ACTIVITY_MOUNTAIN_BIKING= 10;
    public static final int ACTIVITY_WHEELCHAIR = 11;
    public static final int ACTIVITY_ELLIPTICAL = 12;
    public static final int ACTIVITY_OTHER = 13;
    public static final int ACTIVITY_IN_VEHICLE = 14;
    public static final int ACTIVITY_ON_FOOT= 15;
    public static final int ACTIVITY_STILL= 16;
    public static final int ACTIVITY_UNKNOWN= 17;



    String[] inputs = new String[] {"Manual", "GPS", "Automatic"};
    String[] activites = new String[] {"Running", "Walking", "Standing", "Cycling", "Hiking",
            "Downhill Skiing", "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other"};
    Spinner activitySpinner;
    Spinner inputSpinner;

    static final String TAG = "StartFragment";

    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.startfragment, container, false);

    }
    public void onViewCreated(View v, Bundle savedInstanceState) {
        ImageView mStart = v.findViewById(R.id.start_button);


        activitySpinner = v.findViewById(R.id.activity_spinner);
        inputSpinner = v.findViewById(R.id.input_spinner);

        ArrayAdapter<String> inputAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, inputs);
        inputSpinner.setAdapter(inputAdapter);

        AdapterView.OnItemSelectedListener inputListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,
                                       int position, long id) {
                if (position == 2) {
                    activitySpinner.setEnabled(false);
                } else {
                    activitySpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        };

        // Setting ItemClick Handler for Spinner Widget
        inputSpinner.setOnItemSelectedListener(inputListener);

        final ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, activites);
        activitySpinner.setAdapter(activityAdapter);


        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int input = inputSpinner.getSelectedItemPosition();
                int activity = activitySpinner.getSelectedItemPosition();
                if (input == INPUT_MANUAL) {

                    Intent intent = new Intent(getActivity(), EntryActivity.class);
                    intent.putExtra(ACTIVITY_TYPE, activity);
                    intent.putExtra(INPUT_TYPE, input);
                    intent.putExtra("startedFrom", "main");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra(ACTIVITY_TYPE, activity);
                    intent.putExtra(INPUT_TYPE, input);
                    intent.putExtra("startedFrom", "main");
                    startActivity(intent);
                }
            }
        });
    }


}
