package edu.dartmouth.cs.myrun5.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.model.ExerciseEntryBoard;


public class BoardFragmentAdapter extends BaseAdapter {
    public static final String TAG = "BOARD_ADAPTER";
    public static double CONVERTER = 0.621;
    private Context mContext;
    private List<ExerciseEntryBoard> mExerciseEntries;

    public BoardFragmentAdapter(Context context, List<ExerciseEntryBoard> exerciseEntries){
        this.mContext = context;
        this.mExerciseEntries = exerciseEntries;
    }

    @Override
    public int getCount() {
        return mExerciseEntries.size();
    }

    @Override
    public Object getItem(int i) {
        return mExerciseEntries.get(i);
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d(TAG, "GET VIEW");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row;
        row = inflater.inflate(R.layout.board_list_item, null);

        //get unit preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String units = prefs.getString("Unit Preference","-1");
        if (units.equals("-1")){
            units = "kms";
        }

        TextView header = row.findViewById(R.id.text_activity);
        TextView content = row.findViewById(R.id.text_input);
        TextView dateTime = row.findViewById(R.id.text_dateTime);
        TextView email = row.findViewById(R.id.userEmail_input);

        //set content for each list item
        ExerciseEntryBoard entry = mExerciseEntries.get(i);
        String headerText = entry.getInput() + ": " + entry.getActivity();
        header.setText(headerText);
        dateTime.setText(entry.getDate());

        String distance = entry.getDistance();
        if (units.equals("miles")){
            distance = Double.toString(Double.parseDouble(entry.getDistance())* CONVERTER);
        }
        String listContent= distance + " " + units + " " + entry.getDuration() + " mins";
        content.setText(listContent);
        email.setText(entry.getEmail());

        return row;

    }
}
