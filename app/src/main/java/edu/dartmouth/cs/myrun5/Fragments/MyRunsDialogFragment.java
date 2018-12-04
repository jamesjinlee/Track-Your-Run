package edu.dartmouth.cs.myrun5.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import edu.dartmouth.cs.myrun5.activities.EntryActivity;
import edu.dartmouth.cs.myrun5.R;
import edu.dartmouth.cs.myrun5.activities.RegisterActivity;

/**
 * Created by jameslee on 4/9/18.
 */

public class MyRunsDialogFragment extends DialogFragment {
    public static final int DIALOG_ID_PHOTO_PICKER = 1;
    private static final String DIALOG_ID_KEY = "dialog_id";


    public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
    public static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;

    public static final int DIALOG_ENTRY_DATE = 2;
    public static final int DIALOG_ENTRY_TIME = 3;
    public static final int DIALOG_ENTRY_DURATION = 4;
    public static final int DIALOG_ENTRY_DISTANCE = 5;
    public static final int DIALOG_ENTRY_CALORIE = 6;
    public static final int DIALOG_ENTRY_HEARTBEAT = 7;
    public static final int DIALOG_ENTRY_COMMENT = 8;

    Calendar mDateAndTime = Calendar.getInstance();



    public static MyRunsDialogFragment newInstance(int dialog_id) {
        MyRunsDialogFragment fragment = new MyRunsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        // Setup dialog appearance and onClick Listeners
        switch (dialog_id) {
            case DIALOG_ID_PHOTO_PICKER:
                AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.profile_picture_picker);
                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        ((RegisterActivity) parent)
                                .onPhotoPicker(item);
                    }
                };
                builder.setItems(R.array.photo_picker_items, dlistener);
                return builder.create();

            case DIALOG_ENTRY_DATE:
                DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mDateAndTime.set(Calendar.YEAR, year);
                        mDateAndTime.set(Calendar.MONTH, month);
                        mDateAndTime.set(Calendar.DAY_OF_MONTH, day);
                        ((EntryActivity)parent).setDate(mDateAndTime, DIALOG_ENTRY_DATE-1);

                    }
                };

                AlertDialog datePickerDialog = new DatePickerDialog(getActivity(), mDateListener,mDateAndTime.get(Calendar.YEAR),
                        mDateAndTime.get(Calendar.MONTH),
                        mDateAndTime.get(Calendar.DAY_OF_MONTH));

                return datePickerDialog;

            case DIALOG_ENTRY_TIME:
                TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mDateAndTime.set(Calendar.HOUR_OF_DAY, i);
                        mDateAndTime.set(Calendar.MINUTE, i1);
                        ((EntryActivity)parent).setTime(mDateAndTime, DIALOG_ENTRY_TIME-1);
                    }
                };
                AlertDialog timePickerDialog = new TimePickerDialog(getActivity(), mTimeListener,
                        mDateAndTime.get(Calendar.HOUR_OF_DAY),
                        mDateAndTime.get(Calendar.MINUTE), true);
                return timePickerDialog;

            case DIALOG_ENTRY_DURATION:
                AlertDialog.Builder durationBuilder = new AlertDialog.Builder(parent);
                durationBuilder.setTitle(R.string.duration);
                final EditText editText = new EditText(durationBuilder.getContext());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                durationBuilder.setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String duration = editText.getText().toString();
                                if (!TextUtils.isEmpty(duration)) {
                                    ((EntryActivity) parent).setDuration(editText.getText().toString(), DIALOG_ENTRY_DURATION - 1);
                                }
                            }
                        });
                return durationBuilder.create();


            case DIALOG_ENTRY_DISTANCE:
                AlertDialog.Builder distanceBuilder = new AlertDialog.Builder(parent);
                distanceBuilder.setTitle(R.string.distance);
                final EditText distanceEditText = new EditText(distanceBuilder.getContext());
                distanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                distanceBuilder.setView(distanceEditText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String distance = distanceEditText.getText().toString();
                                if (!TextUtils.isEmpty(distance)) {
                                    ((EntryActivity)parent).setDistance(distanceEditText.getText().toString(), DIALOG_ENTRY_DISTANCE-1);
                                }
                            }
                        });
                return distanceBuilder.create();

            case DIALOG_ENTRY_CALORIE:
                AlertDialog.Builder calorieBuilder = new AlertDialog.Builder(parent);
                calorieBuilder.setTitle(R.string.calorie);
                final EditText calorieEditText = new EditText(calorieBuilder.getContext());
                calorieEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                calorieBuilder.setView(calorieEditText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String calorie = calorieEditText.getText().toString();
                                if (!TextUtils.isEmpty(calorie)) {
                                    ((EntryActivity) parent).setCalorie(calorie, DIALOG_ENTRY_CALORIE - 1);
                                }
                            }
                        });
                return calorieBuilder.create();
            case DIALOG_ENTRY_HEARTBEAT:
                AlertDialog.Builder heartbeatBuilder = new AlertDialog.Builder(parent);
                heartbeatBuilder.setTitle(R.string.heartbeat);
                final EditText heartbeatEditText = new EditText(heartbeatBuilder.getContext());
                heartbeatEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                heartbeatBuilder.setView(heartbeatEditText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String heartbeat = heartbeatEditText.getText().toString();
                                if (!TextUtils.isEmpty(heartbeat)) {
                                    ((EntryActivity) parent).setHeartbeat(heartbeat, DIALOG_ENTRY_HEARTBEAT - 1);
                                }
                            }
                        });
                return heartbeatBuilder.create();
            case DIALOG_ENTRY_COMMENT:
                AlertDialog.Builder commentBuilder = new AlertDialog.Builder(parent);
                commentBuilder.setTitle(R.string.comment);
                final EditText commentEditText = new EditText(commentBuilder.getContext());
                commentEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                commentBuilder.setView(commentEditText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String comment = commentEditText.getText().toString();
                                if (!TextUtils.isEmpty(comment)) {
                                    ((EntryActivity) parent).setComment(comment, DIALOG_ENTRY_COMMENT - 1);
                                }
                            }
                        });
                return commentBuilder.create();

            default:
                return null;
        }

    }



}
