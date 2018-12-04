package edu.dartmouth.cs.myrun5.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.dartmouth.cs.myrun5.R;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private  SharedPreferences sharedPref;
    // entry point of the Firebase Authentication
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();



        setContentView(R.layout.activity_profile);

        mEmailView = findViewById(R.id.email_input);
        mPasswordView = findViewById(R.id.password_input);
        Button mSignIn = findViewById(R.id.sign_in_button);
        Button mRegister = findViewById(R.id.register_button);

        //open shared preferences
        Context context = getBaseContext();
        sharedPref = context.getSharedPreferences("login_info", Context.MODE_PRIVATE);

        //sign in button
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get email from text box
                final String email = mEmailView.getText().toString();
                final String password = mPasswordView.getText().toString();

                //error exception for the text box requirements
                if(TextUtils.isEmpty(email)) {
                    mEmailView.setError(getString(R.string.field_required_error));
                }
                if(!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailView.setError(getString(R.string.invalid_email_error));
                }
                if(TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.field_required_error));
                }
                if(password.length() < 6 && password.length() >0 ) {
                    mPasswordView.setError(getString(R.string.password_length_error));
                }

                // Sign in
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //if login in authentification is successful
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "SIGN IN SUCCESS");
                                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    sharedPref.edit().putString("userEmail", email).commit();
                                    startActivity(intent);
                                //if login is unsuccessful
                                } else {
                                    Log.d(TAG, "SIGN IN FAIL");
                                    Toast.makeText(ProfileActivity.this, getString(R.string.auth_fail_message), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });

        //register button
        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
                intent.putExtra("startedFrom", "profile");
                startActivity(intent);


            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in automatically go to main activity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user){
        if (user!=null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }



}