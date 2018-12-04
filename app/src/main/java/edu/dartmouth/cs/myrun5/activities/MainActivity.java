package edu.dartmouth.cs.myrun5.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import edu.dartmouth.cs.myrun5.Fragments.BoardFragment;
import edu.dartmouth.cs.myrun5.Fragments.HistoryFragment;
import edu.dartmouth.cs.myrun5.Fragments.StartFragment;
import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.adapters.NavViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private FirebaseUser user;
    public static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            userId = user.getUid();
        }


        //set up bottom navigation and add fragments
        BottomNavigationView navigation = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.viewpager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new StartFragment());
        fragments.add(new HistoryFragment());
        fragments.add(new BoardFragment());

        NavViewPagerAdapter viewPageAdapter = new NavViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewPageAdapter);


        //listener for bottom nav
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //start fragment
                    case R.id.navigation_start:
                        viewPager.setCurrentItem(0);
                        break;
                    //history fragment
                    case R.id.navigation_history:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_board:
                        viewPager.setCurrentItem(2);
                        break;
                }


                return true;
            }
        });
    }

//*************************************** Menu Items ********************************************//


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //go to settings
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            //go to edit profile
            case R.id.edit_profile:
                Intent editProfileIntent = new Intent(this, RegisterActivity.class);
                editProfileIntent.putExtra("startedFrom", "main");
                startActivity(editProfileIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
