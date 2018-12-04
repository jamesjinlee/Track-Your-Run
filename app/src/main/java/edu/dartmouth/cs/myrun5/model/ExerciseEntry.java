package edu.dartmouth.cs.myrun5.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class ExerciseEntry {
    private Long id;               //entry id

    private int mInputType;        // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private Calendar mDateTime;    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private double mDistance;      // Distance traveled. Either in meters or feet.
    private int mCalorie;          // Calories burnt
    private int mHeartRate;        // Heart rate
    private String mComment;       // Comments
    private ArrayList<LatLng> mLocationList; // Location list
    private double mSpeed;
    private double mClimb;
    private double mAvgSpeed;
    private String mLatLng;
    private double timeElapsed;

    private int synced = 0;
    private boolean deleted = false;
    private int boarded = 0;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }



    public boolean isSavedKms() {
        return savedKms;
    }

    public void setSavedKms(boolean savedKms) {
        this.savedKms = savedKms;
    }

    private boolean savedKms;

    public ExerciseEntry() {
           mLocationList=new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getInputType() {
        return mInputType;
    }

    public void setInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public int getActivityType() {
        return mActivityType;
    }

    public void setActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }

    public Calendar getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Calendar mDateTime) {
        this.mDateTime = mDateTime;
    }

    public void setDateTime(long mDateTime) {
        Calendar cal  = Calendar.getInstance();
        cal.setTimeInMillis(mDateTime);
        this.mDateTime = cal;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public int getCalorie() {
        return mCalorie;
    }

    public void setCalorie(int mCalorie) {
        this.mCalorie = mCalorie;
    }

    public int getHeartRate() {
        return mHeartRate;
    }

    public void setHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public void setLocationList(ArrayList<LatLng> mLocationList) { this.mLocationList = mLocationList; }

    public void addLocationList(LatLng mLatLng) { mLocationList.add(mLatLng); }

    public ArrayList<LatLng> getLocationList() { return mLocationList; }

    public double getSpeed() {
        return mSpeed;
    }

    public void setSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getClimb() {
        return mClimb;
    }

    public void setClimb(double mClimb) {
        this.mClimb = mClimb;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(double mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public String getLatLng() {
        return mLatLng;
    }

    public void setLatLng(String mLatLng) {
        this.mLatLng = mLatLng;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public int getBoarded() {
        return boarded;
    }

    public void setBoarded(int boarded) {
        this.boarded = boarded;
    }
}