package com.github.browep.fpt;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/3/11
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateWorkout2 extends SubmittableActivity {
    static Map<Integer,Integer> WORKOUT_TYPE_TO_FORM_ID = new HashMap<Integer,Integer>();
    private WorkoutDefinition definition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        WORKOUT_TYPE_TO_FORM_ID.put(C.FOR_REPS_WORKOUT_TYPE,R.layout.create_workout_reps);
        WORKOUT_TYPE_TO_FORM_ID.put(C.FOR_TIME_WORKOUT_TYPE,R.layout.create_workout_reps);
        WORKOUT_TYPE_TO_FORM_ID.put(C.FOR_DISTANCE_WORKOUT_TYPE,R.layout.create_workout_reps);
        WORKOUT_TYPE_TO_FORM_ID.put(C.FOR_MAX_WEIGHT_WORKOUT_TYPE,R.layout.create_workout_reps);

        definition = (WorkoutDefinition) getIntent().getExtras().get(C.WORKOUT_DEFINITION);
        Log.i("in CreateWorkout2, got this: " + definition.toString());
        setContentView(R.layout.submittable);
        LinearLayout wrapper = (LinearLayout) findViewById(R.id.submittable);
        LinearLayout workoutForm;
        Integer workoutType = (Integer) definition.get(C.WORKOUT_TYPE);

        if (WORKOUT_TYPE_TO_FORM_ID.get(workoutType) == null)
            throw new IllegalStateException("definition type of  " + workoutType + " could not be determined");

        workoutForm = (LinearLayout) View.inflate(this, WORKOUT_TYPE_TO_FORM_ID.get(workoutType), null);

        wrapper.addView(workoutForm);



    }

    @Override
    public void onSubmit(View view) {

        // grab the details from the form, put into definition, save
        EditText nameBox = (EditText) findViewById(R.id.name_box);
        definition = (WorkoutDefinition) dao.initialize(definition);
        String name = nameBox.getText().toString();
        if (!StringUtils.isEmpty(name)) {
            definition.put(C.WORKOUT_NAME, name);
            dao.save(definition);
            Util.longToastMessage(this,"'" +name + "' has been created. ");
            finish();
        }
        else{
            Util.longToastMessage(this,"You must input a name first");
        }
    }
}

