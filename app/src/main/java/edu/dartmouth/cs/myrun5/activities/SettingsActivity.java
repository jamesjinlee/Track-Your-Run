package edu.dartmouth.cs.myrun5.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import edu.dartmouth.cs.myrun5.Fragments.HistoryFragment;
import edu.dartmouth.cs.myrun5.Fragments.PrefsFragment;
import edu.dartmouth.cs.myrun5.R;


public class SettingsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get shared prefs and set it to kms
        // in adapter check to see if shared prefs = prefs manager
        // if it doesnt then unit change and set shared prefs to miles


        //start the preference fragment
        setContentView(R.layout.settings_activity);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //notify history fragment adapter of the unit preference
                HistoryFragment.adapter.notifyDataSetChanged();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
