package edu.dartmouth.cs.myrun5.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.dartmouth.cs.myrun5.model.ManualEntry;
import edu.dartmouth.cs.myrun5.R;


public class ManualEntryAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ManualEntry> manualEntries;
    public ManualEntryAdapter(Context context, ArrayList<ManualEntry> manualEntries){
        this.mContext = context;
        this.manualEntries = manualEntries;
    }

    @Override
    public int getCount() {
        return manualEntries.size();
    }

    @Override
    public ManualEntry getItem(int i) {
        return manualEntries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row;
        row = inflater.inflate(R.layout.list_item, null);
        TextView header = row.findViewById(R.id.text_header);
        TextView content = row.findViewById(R.id.text_content);
        header.setText(getItem(i).getHeader());
        content.setText(getItem(i).getContent());
        return row;

    }
}
