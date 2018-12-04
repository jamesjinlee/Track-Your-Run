package edu.dartmouth.cs.myrun5.Fragments;

import android.app.VoiceInteractor;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.activities.EntryActivity;
import edu.dartmouth.cs.myrun5.activities.MainActivity;
import edu.dartmouth.cs.myrun5.adapters.BoardFragmentAdapter;
import edu.dartmouth.cs.myrun5.adapters.HistoryFragmentAdapter;
import edu.dartmouth.cs.myrun5.database.AsyncEntriesLoader;
import edu.dartmouth.cs.myrun5.database.EntriesDataSource;
import edu.dartmouth.cs.myrun5.model.ExerciseEntry;
import edu.dartmouth.cs.myrun5.model.ExerciseEntryBoard;


public class BoardFragment extends ListFragment{
    private static final String TAG = "BoardFragment";
    private String URL = "http://129.170.212.93:5000";
    private boolean isAnon;
    private RequestQueue queue;
    public String userEmail;
    public ArrayList<ExerciseEntryBoard> boardEntries;
    public EntriesDataSource dataSource;
    public  BoardFragmentAdapter boardAdapter;

    public ListView boardListView;



    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //initialize datasource
        dataSource = new EntriesDataSource(this.getActivity());

        //get user email
        SharedPreferences prefs = this.getActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        userEmail = prefs.getString("userEmail", "none");

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_board, container, false);


    }
    public void onViewCreated(View v, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        SharedPreferences anon = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //retrieve anonymous preference
        isAnon = anon.getBoolean("Privacy Settings", false);
        queue = Volley.newRequestQueue(getContext());

        //initialize boardEntires array list
        boardEntries = new ArrayList<>();

        //set adapter to list view
        boardAdapter = new BoardFragmentAdapter(getActivity(), boardEntries);
        boardListView = v.findViewById(R.id.list_board_entries);
        boardListView.setAdapter(boardAdapter);

        //helper method to retrieve entries from the server
        getFromBoard();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sync, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                // push entries from local database to social server
                syncToBoard();
                Toast.makeText(getActivity(), "SYNCED",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }

        return false;
    }
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

    }

    public void syncToBoard(){
        Log.d(TAG, "SYNC TO BOARD");

        //retrieve all entries from database
        ArrayList<ExerciseEntry> entries = dataSource.fetchEntries();

        for (ExerciseEntry entry: entries){
            //for each entry, if not already boarded, add to server
            if (entry.getBoarded() == 0) {
                Log.d(TAG, "ENTRY TO ADD "+ entry);
                JSONObject jsonObject = new JSONObject();
                try {
                    //if anonymous set to true
                    if (isAnon) {
                        jsonObject.put("email", MainActivity.userId);
                    } else {

                        jsonObject.put("email", userEmail);
                    }
                    //put info into the json object
                    jsonObject.put("activity_type", EntryActivity.activityName(entry.getActivityType()));

                    //convert calendar to string
                    Long milliseconds = entry.getDateTime().getTimeInMillis();
                    SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(milliseconds);
                    String date = calendarFormat.format(cal.getTime());

                    jsonObject.put("activity_date", date);
                    jsonObject.put("input_type", EntryActivity.inputName(entry.getInputType()));
                    jsonObject.put("duration", Integer.toString(entry.getDuration()));
                    jsonObject.put("distance", Double.toString(entry.getDistance()));

                    //set entry as boarded and update entry
                    entry.setBoarded(1);
                    dataSource.updateEntry(entry);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, URL + "/upload_exercise", jsonObject, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (!response.has("result") || !response.getString("result").equalsIgnoreCase("success")) {
                                        // Server operation is successful.
                                        Log.d(TAG, "SUCCESS");

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                if (error.getMessage() != null)
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });


                queue.add(jsonObjectRequest);
            }

        }
        //refresh board
        getFromBoard();
        boardAdapter.notifyDataSetChanged();

    }

    //helper method to retreive entries from social server
    public void getFromBoard(){
        Log.d(TAG, "GET FROM BOARD");

        //reset array list
        boardEntries.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, URL+"/get_exercises", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse the JSON array and each JSON objects inside it
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject entry = response.getJSONObject(i);
                                ExerciseEntryBoard boardEntry = new ExerciseEntryBoard();
                                String activity = entry.getString("activity_type");
                                String date = entry.getString("activity_date");
                                String input = entry.getString("input_type");
                                String duration = entry.getString("duration");
                                String distance = entry.getString("distance");
                                String email = entry.getString("email");
                                boardEntry.setActivity(activity);
                                boardEntry.setDate(date);
                                boardEntry.setInput(input);
                                boardEntry.setDuration(duration);
                                boardEntry.setDistance(distance);
                                boardEntry.setEmail(email);

                                boardEntries.add(boardEntry);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        boardAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        if(error.getMessage() != null)
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

                    queue.add(jsonArrayRequest);
    }
}
