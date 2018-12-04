package edu.dartmouth.cs.myrun5.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.soundcloud.android.crop.Crop;

import edu.dartmouth.cs.myrun5.Fragments.MyRunsDialogFragment;
import edu.dartmouth.cs.myrun5.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //inputs
    TextInputEditText etName;
    TextInputEditText etEmail;
    TextInputEditText etPassword;
    TextInputEditText etPhone;
    TextInputEditText etMajor;
    TextInputEditText etDartClass;
    RadioGroup rgGender;
    RadioButton rbMale;
    RadioButton rbFemale;

    public static final int REQUEST_CODE_CAMERA = 0;
    public static final int REQUEST_CODE_GALLERY = 1;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    private Uri mImageCaptureUri;
    private Uri mImageCroppedUri;
    private ImageView mImageView;
    private boolean isTakenFromCamera;
    private String prevPassword;
    private boolean passChanged;
    private Boolean error = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.name_input);
        etEmail = findViewById(R.id.email_input2);
        etPassword = findViewById(R.id.password_input2);
        etPhone = findViewById(R.id.phone_input);
        etMajor = findViewById(R.id.major_input);
        etDartClass = findViewById(R.id.class_input);
        mImageView = findViewById(R.id.image);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rgGender = findViewById(R.id.rgGender);

        mAuth = FirebaseAuth.getInstance();


        //check from what page its coming from
        String calledFrom = getIntent().getStringExtra("startedFrom");
        //if you are editing profile
        if (calledFrom.equals("main")){
            setTitle("Profile");
            loadProfile();
            loadSnap();
            etEmail.setFocusable(false);
            etEmail.setFocusableInTouchMode(false);
            etEmail.setClickable(false);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // save and set image when rotated
        if (savedInstanceState != null) {
            mImageCroppedUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
            mImageView.setImageURI(mImageCroppedUri);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCroppedUri);
    }


    // Handle data after activity returns.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {

            //from galary
            case REQUEST_CODE_GALLERY:
                mImageCaptureUri = data.getData();
                Uri destination2 = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(mImageCaptureUri, destination2).asSquare().start(this);
                break;

            //from the camera
            case REQUEST_CODE_CAMERA:
                Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(mImageCaptureUri, destination).asSquare().start(this);
                break;

            //from cropped
            case Crop.REQUEST_CROP:
                mImageCroppedUri = Crop.getOutput(data);
                mImageView.setImageBitmap(null);

                // Delete temporary image taken by camera after crop.
                if (isTakenFromCamera) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                }

                mImageView.setImageURI(mImageCroppedUri);


                break;
        }
    }


    //Button Click Back: Change Photo
    public void onChangeClicked(View v) {
        displayDialog(MyRunsDialogFragment.DIALOG_ID_PHOTO_PICKER);

    }
    //helper function: used to call fragment
    public void displayDialog(int id){
        DialogFragment fragment = MyRunsDialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog));
    }

    //from dialog to camera/gallery
    public void onPhotoPicker(int item){
        switch (item){
            case MyRunsDialogFragment.ID_PHOTO_PICKER_FROM_CAMERA:
                if(Build.VERSION.SDK_INT < 23) {
                    return;
                }
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "photo button");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    ContentValues values = new ContentValues(1);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    intent.putExtra("return-data", true);
                    try {
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    isTakenFromCamera = true;
                    break;
                }
                else {
                    checkPermissions();
                }
            case MyRunsDialogFragment.ID_PHOTO_PICKER_FROM_GALLERY:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);


        }
    }
    //helper function: save image to internal storage
    private void saveSnap() {
        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.saved_image), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //helper function: load image in UI from internal storage
    private void loadSnap() {
        try {
            FileInputStream fis = openFileInput(getString(R.string.saved_image));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            mImageView.setImageResource(R.drawable.image);
        }
    }

    //helper function: load profile
    private void loadProfile(){
        SharedPreferences prefs = getSharedPreferences("login_info", MODE_PRIVATE);
        String mName = prefs.getString("name", "");
        String mEmail = prefs.getString("email", "");
        String mPassword =prefs.getString("password", "");
        String mPhone=prefs.getString("phone", "");
        String mMajor=prefs.getString("major", "");
        String mDartClass=prefs.getString("dartClass", "");
        Boolean maleBtn=prefs.getBoolean("male", false);
        Boolean femaleBtn=prefs.getBoolean("female", false);

        prevPassword = mPassword;

        etName.setText(mName);
        etEmail.setText(mEmail);
        etPassword.setText(mPassword);
        etPhone.setText(mPhone);
        etMajor.setText(mMajor);
        etDartClass.setText(mDartClass);
        if (maleBtn) {
            rbMale.setChecked(true);
        }
        if (femaleBtn) {
            rbFemale.setChecked(true);
        }





    }
    //helper function: save the profile
    public void saveProfile() {
        //get shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);

        //get all the inputs as strings
        String newName = etName.getText().toString();
        String newEmail = etEmail.getText().toString();
        String newPassword = etPassword.getText().toString();
        String newPhone = etPhone.getText().toString();
        String newMajor = etMajor.getText().toString();
        String newDartClass = etDartClass.getText().toString();

        //checking field requirements
        if (!etPassword.getText().toString().equals(prevPassword)){
            passChanged = true;
        }

        if(TextUtils.isEmpty(newName)) {
            etName.setError(getString(R.string.field_required_error));
            error = true;
        }
        if(TextUtils.isEmpty(newEmail)) {
            etEmail.setError(getString(R.string.field_required_error));
            error = true;
        }
        if(!TextUtils.isEmpty(newEmail) && !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            etEmail.setError(getString(R.string.invalid_email_error));
            error = true;
        }
        if(TextUtils.isEmpty(newPassword)) {
            etPassword.setError(getString(R.string.field_required_error));
            error = true;
        } if (newPassword.length() >0 && newPassword.length() < 6) {
            etPassword.setError(getString(R.string.password_length_error));
            error = true;
        }
        if (rgGender.getCheckedRadioButtonId()==-1){
            Toast.makeText(this, R.string.gender_needed, Toast.LENGTH_SHORT).show();
            error = true;
        }


        //if there are no errors, put all data into shared preferences/firebase
        if (!error){
            //put email and password into firebase
            mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),
                    etPassword.getText().toString())
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(RegisterActivity.this, "Authentication worked.",
                                        Toast.LENGTH_LONG).show();
//


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed",
                                        Toast.LENGTH_LONG).show();
//
                            }
                        }
                    });
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", newName);
            editor.putString("email", newEmail);
            editor.putString("password", newPassword);
            editor.putString("phone", newPhone);
            editor.putString("major", newMajor);
            editor.putString("dartClass", newDartClass);
            editor.putBoolean("male", rbMale.isChecked());
            editor.putBoolean("female", rbFemale.isChecked());
            editor.commit();
            finish();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        String calledFrom = getIntent().getStringExtra("startedFrom");
        if (calledFrom.equals("main")) {
            inflater.inflate(R.menu.save, menu);

        }else {
            inflater.inflate(R.menu.register, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.registerOption:
                saveProfile();
                saveSnap();
                //reset errors
                error= false;
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                saveProfile();
                saveSnap();
                if (passChanged && !error){
                    SharedPreferences prefs = getSharedPreferences("logged", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("loggedIn", false).apply();
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
                error= false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }



}
