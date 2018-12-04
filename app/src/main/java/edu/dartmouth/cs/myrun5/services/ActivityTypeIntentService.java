package edu.dartmouth.cs.myrun5.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityTypeIntentService extends IntentService {
    protected static final String TAG = "ActivityIntentService";

    public ActivityTypeIntentService() {
        super(ActivityTypeService.class.getSimpleName());
        // Log.d(TAG,TAG + "DetectedActivityIntentService()");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // Log.d(TAG,TAG + "onCreate()");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent()");
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);


        List<DetectedActivity> detectedActivities = result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
            Log.d(TAG, activity.toString());
            //Log.d(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
            if (activity.getConfidence() >70){
                Intent activityIntent = new Intent("activityDetection");
                activityIntent.putExtra("activityType", activity.getType());
                activityIntent.putExtra("confidence", activity.getConfidence());
                LocalBroadcastManager.getInstance(this).sendBroadcast(activityIntent);
            }

        }
    }

}

