package edu.dartmouth.cs.myrun5.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class ActivityTypeService extends Service {
    private static final String TAG = "ActivityTypeService";
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "onStartCommand()");
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        Intent mIntentService = new Intent(this, ActivityTypeIntentService.class);

        mPendingIntent = PendingIntent.getService(this,
                1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesHandler();

        return START_STICKY;
    }
    // request updates and set up callbacks for success or failure
    public void requestActivityUpdatesHandler() {
        Log.d(TAG, "requestActivityUpdatesHandler()");
        if(mActivityRecognitionClient != null){
            Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                    2000,
                    mPendingIntent);

            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Successfully requested activity updates");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Requesting activity updates failed to start");
                }
            });
        }

    }

    // remove the activity requested updates from Google play.
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        // need to remove the request to Google play services. Brings down the connection.
        removeActivityUpdatesHandler();
    }

    // remove updates and set up callbacks for success or failure
    public void removeActivityUpdatesHandler() {
        if(mActivityRecognitionClient != null){
            Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                    mPendingIntent);
            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Removed activity updates successfully!");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to remove activity updates!");
                }
            });
        }
    }
}
