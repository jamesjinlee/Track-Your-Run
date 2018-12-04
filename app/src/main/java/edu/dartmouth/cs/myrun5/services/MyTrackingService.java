package edu.dartmouth.cs.myrun5.services;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import edu.dartmouth.cs.myrun5.activities.MapsActivity;
import edu.dartmouth.cs.myrun5.model.ExerciseEntry;
import edu.dartmouth.cs.myrun5.R;

public class MyTrackingService extends Service {
    private static final String TAG = "MyTrackingService";
    private final IBinder mBinder = new TrackingBinder();
    public static ExerciseEntry exerciseEntry;
    private Location prevLocation;

    public final static String STOP_SERVICE_BROADCAST_KEY = "StopServiceBroadcastKey";
    public final static int RQS_STOP_SERVICE = 1;
    private NotifyServiceReceiver notifyServiceReceiver;
    private double startingTime;
    StringBuilder locationsString;



    public class TrackingBinder extends Binder {
        MyTrackingService getService() {
            return MyTrackingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        notifyServiceReceiver = new NotifyServiceReceiver();
        notification();
        locationsString = new StringBuilder();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        //initialize exerciseEntry
        exerciseEntry = new ExerciseEntry();
        exerciseEntry.setDistance(0);
        exerciseEntry.setClimb(0);
        exerciseEntry.setCalorie(0);
        exerciseEntry.setAvgSpeed(0);
        exerciseEntry.setSpeed(0);
        exerciseEntry.setTimeElapsed(0);
        String locations = (String) intent.getExtras().get("locations");

        //retrieve values for when screen rotated
        if (!locations.equals("none")){
            String[] latlngs = locations.split(";");
            ArrayList<LatLng> LatLngs = new ArrayList<LatLng>();
            for (String l: latlngs){
                String[] latlngs2= l.split(",");
                double latitude = Double.parseDouble(latlngs2[0]);
                double longitude = Double.parseDouble(latlngs2[1]);
                LatLng finLatLong = new LatLng(latitude, longitude);
                LatLngs.add(finLatLong);
            }
            exerciseEntry.setLocationList(LatLngs);
            exerciseEntry.setClimb((Double) intent.getExtras().get("climb"));
            exerciseEntry.setSpeed((Double) intent.getExtras().get("speed"));
            exerciseEntry.setDistance((Double) intent.getExtras().get("distance"));
            exerciseEntry.setCalorie((Integer) intent.getExtras().get("calorie"));
            exerciseEntry.setTimeElapsed((Double) intent.getExtras().get("time_elapsed"));
        }
        //get location
        showMapLocation();

        return START_STICKY;

    }
  // distance x .06

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        this.unregisterReceiver(notifyServiceReceiver);

    }


    public void showMapLocation(){
        Log.d(TAG, "showMapLocation");
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        Location l = locationManager.getLastKnownLocation(provider);
        //get starting time of first location to get time elapsed
        startingTime = System.currentTimeMillis();

        //add to locationsString to store in database
        String latAndLong = Double.toString(l.getLatitude()) + "," + Double.toString(l.getLongitude()) + ";";
        locationsString.append(latAndLong);

        //update entry
        updateEntry(l);

        LatLng latlng = LocationToLatLng(l);
        exerciseEntry.addLocationList(latlng);

        //send broadcast of first locatation
        Intent i = new Intent("locationUpdates");
        i.putExtra("locationPlace", 0);
        sendBroadcast(i);
        locationManager.requestLocationUpdates(provider, 3000, 0, locationListener);

    }
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            updateWithNewLocation(location);


        }

        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }

        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }
    };
    //helper function to convert location to latlng
    private LatLng LocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    //updating map
    private void updateWithNewLocation(Location location) {
        Log.d(TAG, "updateWithNewLocation");


        if (location != null) {

            String latAndLong = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()) + ";";
            locationsString.append(latAndLong);

            // Update the map location.
            updateEntry(location);

            LatLng latlng = LocationToLatLng(location);
            exerciseEntry.addLocationList(latlng);
            Log.d(TAG, "LOCATION LIST" + exerciseEntry.getLocationList().toString());
            Intent i = new Intent("locationUpdates");
            i.putExtra("locationPlace", 1);

            sendBroadcast(i);

        }
        prevLocation = location;
    }
    public static ExerciseEntry getEntry(){
        return exerciseEntry;
    }

    //helper functino to set up notification
    public void notification(){
        Log.d(TAG, "notifcationSetUp");
        IntentFilter intentFilter = new IntentFilter("stopNotification");
        registerReceiver(notifyServiceReceiver, intentFilter);

        //Send Notification
        Context context = getApplicationContext();
        String notificationTitle = "MyRuns";
        String notificationText = "Background service is tracking your exercise";
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "notification channel")
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setSmallIcon(R.drawable.circle)
                        .setContentIntent(pendingIntent); // note the pending intent to launch browser

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;



        if (notificationManager != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("notification channel", "Tracking Service", importance);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notification);

        }
    }

    //broadcast receiver for notifications
    public class NotifyServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "NotifyService:onReceive(): stop service " + intent);
            NotificationManager notificationManager;
            int rqs = intent.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0);
            if (rqs == RQS_STOP_SERVICE) {

                notificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
                if (notificationManager != null)
                    notificationManager.cancelAll();
            }

            stopSelf();
        }
    }

    public void updateEntry(Location location){
        Log.d(TAG, "UpdateEntry()");
        double distance = 0;
        double climb = 0;
        if (prevLocation!=null){
            distance = prevLocation.distanceTo(location)/1000;

            exerciseEntry.setDistance(exerciseEntry.getDistance() + distance);
            climb = location.getAltitude() - prevLocation.getAltitude();
            exerciseEntry.setClimb(exerciseEntry.getClimb() + climb);
        }
        exerciseEntry.setSpeed(location.getSpeed());
        exerciseEntry.setCalorie((int) (exerciseEntry.getDistance() * 0.06));

        //avg speed
        double timeElapsed = (System.currentTimeMillis() - startingTime)/1000 + 1;
        Log.d(TAG, "timeElapse: " + timeElapsed);
        exerciseEntry.setDuration((int) (timeElapsed/60));
        exerciseEntry.setTimeElapsed(timeElapsed);
        double avgSpeed = exerciseEntry.getDistance()/timeElapsed;
        exerciseEntry.setAvgSpeed(avgSpeed);
        exerciseEntry.setLatLng(locationsString.toString());

    }



}
