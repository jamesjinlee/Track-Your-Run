package edu.dartmouth.cs.myrun5.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

public class NavViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = NavViewPagerAdapter.class.getSimpleName();;
    private ArrayList<Fragment> fragments;

    private static final int START = 0;
    private static final int HISTORY = 1;
    private static final int BOARD = 2;
    private static final String TAB_START = "START";
    private static final String TAB_HISTORY = "HISTORY";
    private static final String TAB_BOARD = "BOARD";

    public NavViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }
    public Fragment getItem(int pos){
        Log.d(TAG, "getItem " + "position" + pos);
        return fragments.get(pos);
    }
    // Return the number of views available
    public int getCount(){
        Log.d(TAG, "getCount " + "size " + fragments.size());
        return fragments.size();
    }


    public CharSequence getPageTitle(int position) {
        Log.d(TAG, "getPageTitle " + "position " + position);
        switch (position) {
            case START:
                return TAB_START;
            case HISTORY:
                return TAB_HISTORY;
            case BOARD:
                return TAB_BOARD;
            default:
                break;
        }
        return null;
    }
}
