package edu.dartmouth.cs.myrun5.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.dartmouth.cs.myrun5.activities.ProfileActivity;
import edu.dartmouth.cs.myrun5.R;


public class PrefsFragment extends PreferenceFragment {
    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.sign_out_key_btn)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d("PrefsFrag", "I am here!");
//                SharedPreferences prefs = getActivity().getSharedPreferences("logged", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putBoolean("loggedIn", false).apply();

                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);

                return false;
            }
        });

    }
}

