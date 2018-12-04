package edu.dartmouth.cs.myrun5.model;



public class ExerciseEntryFirebase {
    private String id;
    private String activity;
    private String input;
    private String email;
    private String comment;
    private String date;
    private String time;
    private String duration;
    private String distance;
    private String calorie;
    private String heartbeat;

    private String climb = "0";
    private String avgSpeed = "0";
    private String speed = "0";
    private String latLngs= "";

    private boolean deleted = false;
    private int synced = 0;
    private int boarded = 0;

    public ExerciseEntryFirebase(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(String heartbeat) {
        this.heartbeat = heartbeat;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public String getClimb() {
        return climb;
    }

    public void setClimb(String climb) {
        this.climb = climb;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(String latLngs) {
        this.latLngs = latLngs;
    }

    public int getBoarded() {
        return boarded;
    }

    public void setBoarded(int boarded) {
        this.boarded = boarded;
    }
}
