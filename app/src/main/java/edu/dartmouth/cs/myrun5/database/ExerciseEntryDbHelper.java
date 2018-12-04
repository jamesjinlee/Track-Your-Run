package edu.dartmouth.cs.myrun5.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ExerciseEntryDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_ENTRIES = "entries";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INPUT_TYPE = "input_type";
    public static final String COLUMN_ACTIVITY_TYPE = "activity_type";
    public static final String COLUMN_DATE_TIME = "date_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_HEART_RATE = "heart_rate";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_CLIMB = "climb";
    public static final String COLUMN_AVG_SPEED = "avg_speed";
    public static final String COLUMN_LATLNG ="lat_lng";
    public static final String COLUMN_SYNCED = "synced";
    public static final String COLUMN_BOARDED = "boarded";



    private static final String DATABASE_NAME = "Exercise_Entries";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ENTRIES + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY, "
            + COLUMN_INPUT_TYPE + " INTEGER NOT NULL, "
            + COLUMN_ACTIVITY_TYPE + " integer not null, "
            + COLUMN_DATE_TIME + " datetime not null, "
            + COLUMN_DURATION + " integer not null, "
            + COLUMN_DISTANCE + " float, "
            + COLUMN_CALORIES + " integer, "
            + COLUMN_HEART_RATE + " integer, "
            + COLUMN_COMMENT + " text,"
            + COLUMN_SPEED + " float,"
            + COLUMN_CLIMB + " float,"
            + COLUMN_AVG_SPEED + " float,"
            + COLUMN_LATLNG + " text,"
            + COLUMN_SYNCED + " int,"
            + COLUMN_BOARDED + " int)";

    public ExerciseEntryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ExerciseEntryDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }


}
