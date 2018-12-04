package edu.dartmouth.cs.myrun5.activities;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import edu.dartmouth.cs.myrun5.Fragments.HistoryFragment;
import edu.dartmouth.cs.myrun5.Fragments.StartFragment;
import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.database.EntriesDataSource;
import edu.dartmouth.cs.myrun5.database.ExerciseEntryDbHelper;
import edu.dartmouth.cs.myrun5.model.ExerciseEntry;
import edu.dartmouth.cs.myrun5.services.ActivityTypeService;
import edu.dartmouth.cs.myrun5.services.MyTrackingService;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    public Marker endLocation;

    boolean mIsBound;
    MyTrackingService mTrackingService;
    Application mAppContext = null;
    ExerciseEntry entry;
    TextView mActivity;
    TextView mSpeed;
    TextView mDistance;
    TextView mAvgSpeed;
    TextView mClimb;
    TextView mCalorie;

    public int input;
    public ExerciseEntryDbHelper databaseHelper;
    public EntriesDataSource datasource;
    //history adapter
    private long entryId;
    private int entryPosition;

    private String calledFrom;  //where intent was called from

    //save values for screen rotation
    public double climbSaved;
    public double avgSpeedSaved;
    public double speedSaved;
    public int calorieSaved;
    public int activitySaved;
    public double distanceSaved;
    public String locationsSaved;
    public double timeElapsedSaved;
    public float zoomLevel;
    private DatabaseReference ref;




    HashMap<String, Integer> activitiesMap = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new ExerciseEntryDbHelper(this);
        datasource = new EntriesDataSource(this);

        mActivity = findViewById(R.id.map_activity_type);
        mSpeed = findViewById(R.id.map_speed);
        mDistance = findViewById(R.id.map_distance);
        mAvgSpeed = findViewById(R.id.map_avg_speed);
        mClimb = findViewById(R.id.map_climbed);
        mCalorie = findViewById(R.id.map_calorie);

        //if called from history page, load the page and info from the database
        calledFrom = getIntent().getStringExtra("startedFrom");
        if (calledFrom.equals("history")) {
            entryPosition = getIntent().getIntExtra("index", 0);
            entryId = getIntent().getLongExtra("id", 0);

            //set up map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            //get entry info from the history fragment
            double climb = getIntent().getDoubleExtra("climb", 0);
            double avgSpeed = getIntent().getDoubleExtra("avg_speed", 0);
            double speed = getIntent().getDoubleExtra("speed", 0);
            int calorie = getIntent().getIntExtra("calorie", 0);
            int activity = getIntent().getIntExtra("activity", 0);
            double distance = getIntent().getDoubleExtra("distance", 0);

            String activityText = "Activity: " + EntryActivity.activityName(activity);
            String speedText = "Speed: " + Double.toString(twoDecimal(speed));
            String avgSpeedText = "Avg Speed: " + Double.toString(twoDecimal(avgSpeed));
            String climbText = "Clim b: " + Double.toString(twoDecimal(climb));
            String calorieText = "Calorie: " + Integer.toString(calorie);
            String distanceText = "Distance: " + Double.toString(twoDecimal(distance));

            //set deck
            mActivity.setText(activityText);
            mSpeed.setText(speedText);
            mAvgSpeed.setText(avgSpeedText);
            mClimb.setText(climbText);
            mCalorie.setText(calorieText);
            mDistance.setText(distanceText);

        } else {

            //new intent to start service
            Intent i;
            i = new Intent(this, MyTrackingService.class);
            zoomLevel = 17;
            //for screen rotation, pass values to tracking service
            if (savedInstanceState!=null) {
                i.putExtra("locations", savedInstanceState.getString("locations"));
                i.putExtra("climb", savedInstanceState.getDouble("climb"));
                i.putExtra("speed", savedInstanceState.getDouble("speed"));
                i.putExtra("distance", savedInstanceState.getDouble("distance"));
                i.putExtra("calorie", savedInstanceState.getInt("calorie"));
                i.putExtra("time_elapsed", savedInstanceState.getDouble("time_elapsed"));
                zoomLevel = savedInstanceState.getFloat("zoom");


            } else {
                i.putExtra("locations", "none");
            }
            startService(i);

            //for the broadcast receiver for locations
            IntentFilter filter=new IntentFilter();
            filter.addAction("locationUpdates");
            registerReceiver(locationReceiver,filter);


            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            input = getIntent().getIntExtra(StartFragment.INPUT_TYPE, 0);

            //initialize activitiesMap hashmap
            activitiesMap.put("inVehicle", 0);
            activitiesMap.put("onFoot", 0);
            activitiesMap.put("walking", 0);
            activitiesMap.put("still", 0);
            activitiesMap.put("unknown", 0);
            activitiesMap.put("onBicycle", 0);
            activitiesMap.put("running", 0);

        }


    }

    public void putLocationMarkers(){
        String locations = getIntent().getStringExtra("locations");
        String[] latLngs = locations.split(";");

        //mark first location
        String[] firstLatLng = latLngs[0].split(",");
        double firstLatitude = Double.parseDouble(firstLatLng[0]);
        double firstLongitude = Double.parseDouble(firstLatLng[1]);
        LatLng firstLatlng = new LatLng(firstLatitude, firstLongitude);
        Log.d(TAG, "first" + firstLatLng[0]);
        Log.d(TAG, "first" + firstLatLng[1]);
        mMap.addMarker(new MarkerOptions().position(firstLatlng).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE)));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLatlng, 17));

        LatLng prevLatLng;
        prevLatLng= firstLatlng;

        //draw the polyline between each location
        for (String string: Arrays.copyOfRange(latLngs, 1, latLngs.length)){
            String[] eachLatLng = string.split(",");
            Log.d(TAG, eachLatLng[0]);
            Log.d(TAG, eachLatLng[1]);
            double latitude = Double.parseDouble(eachLatLng[0]);
            double longitude = Double.parseDouble(eachLatLng[1]);
            LatLng nextLatLng = new LatLng(latitude, longitude);

            mMap.addPolyline(new PolylineOptions()
                    .add(prevLatLng, nextLatLng)
                    .width(8)
                    .color(Color.BLACK));

            prevLatLng = nextLatLng;
        }

        //mark last location
        String[] lastLatLng = latLngs[latLngs.length -1].split(",");
        double lastLatitude = Double.parseDouble(lastLatLng[0]);
        double lastLongitude = Double.parseDouble(lastLatLng[1]);
        LatLng lastlatlng = new LatLng(lastLatitude,lastLongitude);
        mMap.addMarker(new MarkerOptions().position(lastlatlng).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)));

    }
    // save for when rotated
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putString("locations", locationsSaved);
        outstate.putDouble("climb", climbSaved);
        outstate.putDouble("speed", speedSaved);
        outstate.putInt("calorie", calorieSaved);
        outstate.putDouble("distance", distanceSaved);
        outstate.putFloat("zoom", mMap.getCameraPosition().zoom);
        outstate.putDouble("time_elapsed", timeElapsedSaved);
        Log.d(TAG, "SAVEINSTANCE" + mMap.getCameraPosition().zoom);

    }

    // register the RX and start up the ActivityDetectionService service
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart():start ActivityDetectionService");
        //if input is automatic, start activity recognition
        if (input == StartFragment.INPUT_AUTOMATIC) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mActivityBroadcastReceiver,
                    new IntentFilter("activityDetection"));

            startService(new Intent(this, ActivityTypeService.class));

        }
    }

//     unregister the RX and stop up the ActivityDetectionService service
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause():stop ActivityDetectionService");
        if(mActivityBroadcastReceiver != null){
            stopService(new Intent(this, ActivityTypeService.class));
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityBroadcastReceiver);
        }
    }

    BroadcastReceiver mActivityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "onReceive()");
            if (intent.getAction().equals("activityDetection")) {
                int type = intent.getIntExtra("activityType", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                handleUserActivity(type, confidence);
            }
        }
    };

    private void handleUserActivity(int type, int confidence) {
        String label = "Unknown";
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                entry.setActivityType(StartFragment.ACTIVITY_IN_VEHICLE);
                label = "In_Vehicle";
                activitiesMap.put("inVehicle", activitiesMap.get("inVehicle") + 1);
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                entry.setActivityType(StartFragment.ACTIVITY_CYCLING);
                label = "On_Bicycle";
                activitiesMap.put("onBicycle", activitiesMap.get("onBicycle") + 1);
                break;
            }
            case DetectedActivity.ON_FOOT: {
                entry.setActivityType(StartFragment.ACTIVITY_ON_FOOT);
                label = "On_Foot";
                activitiesMap.put("onFoot", activitiesMap.get("onFoot") + 1);
                break;
            }
            case DetectedActivity.RUNNING: {
                entry.setActivityType(StartFragment.ACTIVITY_RUNNING);
                label = "Running";
                activitiesMap.put("running", activitiesMap.get("running") + 1);
                break;
            }
            case DetectedActivity.STILL: {
                entry.setActivityType(StartFragment.ACTIVITY_STILL);
                label = "Still";
                activitiesMap.put("still", activitiesMap.get("still") + 1);
                break;
            }
            case DetectedActivity.WALKING: {
                entry.setActivityType(StartFragment.ACTIVITY_WALKING);
                label = "Walking";
                activitiesMap.put("walking", activitiesMap.get("walking") + 1);
                break;
            }
            case DetectedActivity.UNKNOWN: {
                entry.setActivityType(StartFragment.ACTIVITY_UNKNOWN);
                label="Unknown";
                activitiesMap.put("unknown", activitiesMap.get("unknown") + 1);
                break;
            }
        }

        Log.d(TAG, "broadcast:onReceive(): Activity is " + label
                + " and confidence level is: " + confidence);

        String maxEntry= "none";
        int maxCount = -1;
        //get max value from activies hashmap, to see most common activity
        for (String entry : activitiesMap.keySet()) {
            if (activitiesMap.get(entry) > maxCount){
                maxEntry = entry;
                maxCount = activitiesMap.get(entry);
            }
        }
        String automaticActivityString = "Activity: " + maxEntry;
        mActivity.setText(automaticActivityString);
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        entry = MyTrackingService.getEntry();

        int a = intent.getIntExtra("locationPlace", 0);
        StringBuilder locations = new StringBuilder();
        for (LatLng latlng : entry.getLocationList()) {
            Double lat = latlng.latitude;
            Double lng = latlng.longitude;
            String latString = lat.toString();
            String lngString = lng.toString();
            locations.append(latString + "," + lngString + ";");
        }
        locationsSaved = locations.toString();

        //retrieve data from entry
        climbSaved = entry.getClimb();
        avgSpeedSaved = entry.getAvgSpeed();
        speedSaved = entry.getSpeed();
        distanceSaved = entry.getDistance();
        timeElapsedSaved = entry.getTimeElapsed();


        //set marker for first point and zoom
        if (a == 0) {
            LatLng latlng = entry.getLocationList().get(0);
            mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_BLUE)));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
        } else {
            for (int i = 1; i < entry.getLocationList().size(); i++) {
                LatLng latlng2 = entry.getLocationList().get(i);
                mMap.addPolyline(new PolylineOptions()
                        .add(entry.getLocationList().get(i - 1), latlng2)
                        .width(8)
                        .color(Color.BLACK));

            }

            if (endLocation != null) {
                endLocation.remove();
            }
            //mark the last point
            LatLng latlngEnd = entry.getLocationList().get(entry.getLocationList().size() - 1);
            endLocation = mMap.addMarker(new MarkerOptions().position(latlngEnd).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN)));
        }
        entry.setInputType(input);

        if (input == StartFragment.INPUT_GPS) {
            int activity = getIntent().getIntExtra(StartFragment.ACTIVITY_TYPE, 0);
            entry.setActivityType(activity);
            String activityName = EntryActivity.activityName(activity);
            String activityString = "Activity: " + activityName;
            mActivity.setText(activityString);
        }

        String speedString = "Speed: " + twoDecimal(entry.getSpeed()) + " m/s";
        String distanceString = "Distance: " + twoDecimal(entry.getDistance()) + " m";
        String calorieString = "Calorie: " + entry.getCalorie();
        String avgSpeed = "Avg Speed: " + twoDecimal(entry.getAvgSpeed()) + " m/s";
        String climbSpeed = "Climbed: " + twoDecimal(entry.getClimb()) + " m";

        // set the textviews on the map
        mSpeed.setText(speedString);
        mDistance.setText(distanceString);
        mCalorie.setText(calorieString);
        mAvgSpeed.setText(avgSpeed);
        mClimb.setText(climbSpeed);

        }
    };
    //helper function to round to two decimal places
    public double twoDecimal(double number){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return Double.parseDouble(df.format(number));
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:unbindService()");
        if (calledFrom.equals("main")) {
            //stop the notification
            Intent intent = new Intent();
            intent.setAction("stopNotification");
            intent.putExtra(MyTrackingService.STOP_SERVICE_BROADCAST_KEY, MyTrackingService.RQS_STOP_SERVICE);
            sendBroadcast(intent);

            //stop the service
            unregisterReceiver(locationReceiver);
            Intent i = new Intent(this, MyTrackingService.class);
            stopService(i);
            mIsBound = false;

        }
        super.onDestroy();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        if (calledFrom.equals("history")){
            putLocationMarkers();
        }
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission granted. Let's show the map");

        } else {
            Log.d(TAG, "permission denied! I am going to close the app");
            finish();
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            return true;}
        else {
            return false;
        }
    }
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
            case R.id.save:
                onSaveMap();
                finish();
                return true;
            case R.id.delete:
                AsyncEntriesDeleter asyncDeleter = new AsyncEntriesDeleter();
                asyncDeleter.execute();

                ref = FirebaseDatabase.getInstance().getReference("user_"+ MainActivity.userId).child("exercise_entries");
                ref.child(Long.toString(entryId)).removeValue();
                Log.d(TAG, "FIREBASE DELETE MAP " + Long.toString(entryId));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    //helper function to save map using AsyncWriter
    public void onSaveMap(){
        entry.setDateTime(Calendar.getInstance());
        AsyncWriter asyncWriter = new AsyncWriter();
        asyncWriter.execute();
    }

    //async task functions
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
            Log.d(TAG, "ASYNC DELETER");
            EntriesDataSource dataSource = new EntriesDataSource(MapsActivity.this);
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
