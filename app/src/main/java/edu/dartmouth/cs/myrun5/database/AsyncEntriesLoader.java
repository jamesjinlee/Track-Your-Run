package edu.dartmouth.cs.myrun5.database;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import edu.dartmouth.cs.myrun5.model.ExerciseEntry;


public class AsyncEntriesLoader extends AsyncTaskLoader<List<ExerciseEntry>> {
    private EntriesDataSource dataSource;

    public AsyncEntriesLoader(Context context) {
        super(context);
        //create database
        this.dataSource = new EntriesDataSource(context);
    }

    @Override
    public List<ExerciseEntry> loadInBackground() {
        return dataSource.fetchEntries();
    }

}