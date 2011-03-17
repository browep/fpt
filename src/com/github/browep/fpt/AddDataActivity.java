package com.github.browep.fpt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddDataActivity extends SubmittableActivity {

    private TextView mDateDisplay;
    private Button mPickDate;
    private Calendar mCalendar = Calendar.getInstance();

    static final int DATE_DIALOG_ID = 0;

    private Integer workoutType;
    private WorkoutDefinition definition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        setContentView(R.layout.submittable);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout wrapper = (LinearLayout) findViewById(R.id.submittable);

        Integer workoutDefinitionId = (Integer) getIntent().getExtras().get(C.WORKOUT_DEFINITION);
        definition = (WorkoutDefinition) dao.get(workoutDefinitionId);
        workoutType = (Integer) definition.get(C.WORKOUT_TYPE);

        inflater.inflate( C.WORKOUT_TYPE_TO_EDIT_LAYOUT.get(workoutType), wrapper, true );

        // update the name
        TextView nameDisplay = (TextView) findViewById(R.id.name_display);
        nameDisplay.setText((CharSequence) definition.get(C.WORKOUT_NAME));

        // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.date_display);
        mPickDate = (Button) findViewById(R.id.pick_date_button);

        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });



        // display the current date (this method is below)
        updateDisplay();
    }

    @Override
    public void onSubmit(View view) {
        // get all the data for the
        if (workoutType.equals(C.FOR_REPS_WORKOUT_TYPE)) {

            Workout workout = (Workout) dao.initialize(new Workout(C.FOR_REPS_WORKOUT_TYPE));

            // get the reps and date, create an entry
            String repsText = ((EditText) findViewById(R.id.rep_count)).getText().toString();
            if(StringUtils.isEmpty(repsText)){
                Util.longToastMessage(this,"You must enter something into the \"Reps\" box");
                return;
            }

            Integer reps = Integer.valueOf(repsText);
            workout.put(C.REPS, reps);
            workout.setCreated(mCalendar.getTime());

            workout.put(C.WORKOUT_DEFINITION_ID,definition.getId());

            String comment = ((EditText) findViewById(R.id.comment)).getText().toString();
            if(!StringUtils.isEmpty(comment))
                workout.put(C.COMMENT,comment);

            // create a new workout

            dao.save(workout);
            Util.longToastMessage(this, "Entry saved for \"" + definition.get(C.WORKOUT_NAME) + "\"");
            finish();
        }


    }

    // updates the date in the TextView
    private void updateDisplay() {
        mDateDisplay.setText( C.DISPLAY_FORMAT.format(mCalendar.getTime()));

    }

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {

                    mCalendar.set(Calendar.YEAR,year);
                    mCalendar.set(Calendar.MONTH,monthOfYear);
                    mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                    updateDisplay();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }
}
