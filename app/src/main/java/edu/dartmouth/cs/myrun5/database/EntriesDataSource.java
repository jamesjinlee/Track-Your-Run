package edu.dartmouth.cs.myrun5.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import edu.dartmouth.cs.myrun5.model.ExerciseEntry;


public class EntriesDataSource {
    public static final String TAG = "EntriesDataSource";
    private SQLiteDatabase database;
    private ExerciseEntryDbHelper dbHelper;
    private String[] allColumns = { ExerciseEntryDbHelper.COLUMN_ID,
            ExerciseEntryDbHelper.COLUMN_INPUT_TYPE,
            ExerciseEntryDbHelper.COLUMN_ACTIVITY_TYPE,
            ExerciseEntryDbHelper.COLUMN_DATE_TIME,
            ExerciseEntryDbHelper.COLUMN_DURATION,
            ExerciseEntryDbHelper.COLUMN_DISTANCE,
            ExerciseEntryDbHelper.COLUMN_CALORIES,
            ExerciseEntryDbHelper.COLUMN_HEART_RATE,
            ExerciseEntryDbHelper.COLUMN_COMMENT,
            ExerciseEntryDbHelper.COLUMN_SPEED,
            ExerciseEntryDbHelper.COLUMN_CLIMB,
            ExerciseEntryDbHelper.COLUMN_AVG_SPEED,
            ExerciseEntryDbHelper.COLUMN_LATLNG,
            ExerciseEntryDbHelper.COLUMN_SYNCED,
            ExerciseEntryDbHelper.COLUMN_BOARDED};



    public EntriesDataSource(Context context) {
        dbHelper = new ExerciseEntryDbHelper(context);
    }

    public void open() throws SQLException {
        Log.d(TAG, "DataSource open");
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        Log.d(TAG, "DataSource close");
        dbHelper.close();
    }
    // Insert a item given each column value
    public void insertEntry(ExerciseEntry entry) {
        Log.d(TAG, "DataSource insertEntry");
        ContentValues values = new ContentValues();

        values.put(ExerciseEntryDbHelper.COLUMN_ID, entry.getId());
        values.put(ExerciseEntryDbHelper.COLUMN_INPUT_TYPE, entry.getInputType());
        values.put(ExerciseEntryDbHelper.COLUMN_ACTIVITY_TYPE, entry.getActivityType());
        values.put(ExerciseEntryDbHelper.COLUMN_DATE_TIME, entry.getDateTime().getTimeInMillis());
        values.put(ExerciseEntryDbHelper.COLUMN_DURATION, entry.getDuration());
        values.put(ExerciseEntryDbHelper.COLUMN_DISTANCE, entry.getDistance());
        values.put(ExerciseEntryDbHelper.COLUMN_CALORIES, entry.getCalorie());
        values.put(ExerciseEntryDbHelper.COLUMN_HEART_RATE, entry.getHeartRate());
        values.put(ExerciseEntryDbHelper.COLUMN_COMMENT, entry.getComment());
        values.put(ExerciseEntryDbHelper.COLUMN_SPEED, entry.getSpeed());
        values.put(ExerciseEntryDbHelper.COLUMN_CLIMB, entry.getClimb());
        values.put(ExerciseEntryDbHelper.COLUMN_AVG_SPEED, entry.getAvgSpeed());
        values.put(ExerciseEntryDbHelper.COLUMN_LATLNG, entry.getLatLng());
        values.put(ExerciseEntryDbHelper.COLUMN_SYNCED, entry.getSynced());
        values.put(ExerciseEntryDbHelper.COLUMN_BOARDED, entry.getBoarded());

        open();
        database.insert(ExerciseEntryDbHelper.TABLE_ENTRIES, null, values);
        close();
    }


    public void updateEntry(ExerciseEntry entry){
        ContentValues values = new ContentValues();

        values.put(ExerciseEntryDbHelper.COLUMN_ID, entry.getId());
        values.put(ExerciseEntryDbHelper.COLUMN_INPUT_TYPE, entry.getInputType());
        values.put(ExerciseEntryDbHelper.COLUMN_ACTIVITY_TYPE, entry.getActivityType());
        values.put(ExerciseEntryDbHelper.COLUMN_DATE_TIME, entry.getDateTime().getTimeInMillis());
        values.put(ExerciseEntryDbHelper.COLUMN_DURATION, entry.getDuration());
        values.put(ExerciseEntryDbHelper.COLUMN_DISTANCE, entry.getDistance());
        values.put(ExerciseEntryDbHelper.COLUMN_CALORIES, entry.getCalorie());
        values.put(ExerciseEntryDbHelper.COLUMN_HEART_RATE, entry.getHeartRate());
        values.put(ExerciseEntryDbHelper.COLUMN_COMMENT, entry.getComment());
        values.put(ExerciseEntryDbHelper.COLUMN_SPEED, entry.getSpeed());
        values.put(ExerciseEntryDbHelper.COLUMN_CLIMB, entry.getClimb());
        values.put(ExerciseEntryDbHelper.COLUMN_AVG_SPEED, entry.getAvgSpeed());
        values.put(ExerciseEntryDbHelper.COLUMN_LATLNG, entry.getLatLng());
        values.put(ExerciseEntryDbHelper.COLUMN_SYNCED, entry.getSynced());
        values.put(ExerciseEntryDbHelper.COLUMN_BOARDED, entry.getBoarded());

        open();
        database.update(ExerciseEntryDbHelper.TABLE_ENTRIES, values, "id=?", new String[]{entry.getId()+""});
        close();
    }

    // Remove an entry by giving its index
    public void removeEntry(long rowIndex) {
        open();
        long id = rowIndex;
        database.delete(ExerciseEntryDbHelper.TABLE_ENTRIES, ExerciseEntryDbHelper.COLUMN_ID
                + " = " + id, null);
        close();
    }

    // Query a specific entry by its index.
    public ExerciseEntry fetchEntryByIndex(long rowId) {
        database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(ExerciseEntryDbHelper.TABLE_ENTRIES, allColumns,
                ExerciseEntryDbHelper.COLUMN_ID + " = " + rowId,
                null, null, null, null);

        ExerciseEntry newEntry = cursorToEntry(cursor);
        cursor.close();
        database.close();
        return newEntry;

    }


    // Query the entire table, return all rows
    public ArrayList<ExerciseEntry> fetchEntries() {
        database = dbHelper.getReadableDatabase();
        ArrayList<ExerciseEntry> entries = new ArrayList<>();
        Cursor cursor = database.query(ExerciseEntryDbHelper.TABLE_ENTRIES, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            ExerciseEntry entry = cursorToEntry(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return entries;
    }

//    public void updateSynced(int a){
//        String strFilter = "id=" + 13;
//        ContentValues args = new ContentValues();
//        args.put("synced", a);  // use 1 since there's no boolean only integer in sqlite
//        int rowsReturned = database.update(ExerciseEntryDbHelper.TABLE_ENTRIES, args, strFilter, null);
//    }




//    public void update(int a){
//        String strFilter = "id=" + 13;
//        ContentValues args = new ContentValues();
//        args.put("synced", a);  // use 1 since there's no boolean only integer in sqlite
//        int rowsReturned = database.update(ExerciseEntryDbHelper.TABLE_ENTRIES, args, strFilter, null);
//    }

    private ExerciseEntry cursorToEntry(Cursor cursor){
        ExerciseEntry entry = new ExerciseEntry();

        entry.setId(cursor.getLong(0));
        entry.setInputType(cursor.getInt(1));
        entry.setActivityType(cursor.getInt(2));
        entry.setDateTime(cursor.getLong(3));
        entry.setDuration(cursor.getInt(4));
        entry.setDistance(cursor.getDouble(5));
        entry.setCalorie(cursor.getInt(6));
        entry.setHeartRate(cursor.getInt(7));
        entry.setComment(cursor.getString(8));
        entry.setSpeed(cursor.getDouble(9));
        entry.setClimb(cursor.getDouble(10));
        entry.setAvgSpeed(cursor.getDouble(11));
        entry.setLatLng(cursor.getString(12));
        entry.setSynced(cursor.getInt(13));
        entry.setBoarded(cursor.getInt(14));

        return entry;

    }

}
