package edu.dartmouth.cs.myrun5.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.myrun5.activities.EntryActivity;
import edu.dartmouth.cs.myrun5.model.ExerciseEntry;
import edu.dartmouth.cs.myrun5.R;


public class HistoryFragmentAdapter extends BaseAdapter {
    public static final String TAG = "HISTORY_ADAPTER";
    public static double CONVERTER = 0.621;
    private Context mContext;
    private List<ExerciseEntry> mExerciseEntries;

    public HistoryFragmentAdapter(Context context, List<ExerciseEntry> exerciseEntries){
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
        return mExerciseEntries.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //get the unit preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String units = prefs.getString("Unit Preference","-1");
        if (units.equals("-1")){
            units = "kms";
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row;
        row = inflater.inflate(R.layout.entries_list_item, null);


        TextView header = row.findViewById(R.id.text_activity);
        TextView content = row.findViewById(R.id.text_input);
        TextView dateTime = row.findViewById(R.id.text_dateTime);

        //get the activity and input string
        String activity = EntryActivity.activityName(mExerciseEntries.get(i).getActivityType());
        String input = EntryActivity.inputName(mExerciseEntries.get(i).getInputType());

        //List header is the input and activity
        String listHeader = input + ": " + activity;
        header.setText(listHeader);

        ExerciseEntry entry = mExerciseEntries.get(i);

        if (units.equals("miles")){
            entry.setDistance(entry.getDistance() * CONVERTER);
        }

        String distance = Double.toString(twoDecimal(entry.getDistance()));

        if(distance.contains(".0")){
            distance = distance.substring(0, distance.indexOf('.'));
        }

        int duration = entry.getDuration();


        //set list content with duration and distance
        String listContent = distance + " " + units+ ", " + duration + " mins";
        content.setText(listContent);


        //get the calender in milliseconds, and convert it to date format
        Long milliseconds = entry.getDateTime().getTimeInMillis();
        SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);

        //set dateAndTime to the calendar format
        String dateAndTime = calendarFormat.format(cal.getTime());
        dateTime.setText(dateAndTime);

        return row;
    }
    private double twoDecimal(double number){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return Double.parseDouble(df.format(number));
    }


}
