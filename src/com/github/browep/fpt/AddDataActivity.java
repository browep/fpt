package com.github.browep.fpt;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddDataActivity extends SubmittableActivity {

    private static Map<Integer,Integer> WORKOUT_TYPE_TO_EDIT_LAYOUT = new HashMap<Integer,Integer>();

    static
    {
        WORKOUT_TYPE_TO_EDIT_LAYOUT.put(C.FOR_DISTANCE_WORKOUT_TYPE,R.layout.add_data_distance);
        WORKOUT_TYPE_TO_EDIT_LAYOUT.put(C.FOR_REPS_WORKOUT_TYPE,R.layout.add_data_reps);
        WORKOUT_TYPE_TO_EDIT_LAYOUT.put(C.FOR_MAX_WEIGHT_WORKOUT_TYPE,R.layout.add_data_max_weight);
        WORKOUT_TYPE_TO_EDIT_LAYOUT.put(C.FOR_TIME_WORKOUT_TYPE,R.layout.add_data_time);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.submittable);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout wrapper = (LinearLayout) findViewById(R.id.submittable);

        Integer workoutDefinitionId = (Integer) getIntent().getExtras().get(C.WORKOUT_DEFINITION);
        WorkoutDefinition definition = (WorkoutDefinition) dao.get(workoutDefinitionId);
        Integer workoutType = (Integer) definition.get(C.WORKOUT_TYPE);

        inflater.inflate( WORKOUT_TYPE_TO_EDIT_LAYOUT.get(workoutType), wrapper, true );
    }

    @Override
    public void onSubmit(View view) {
        // get all the data for the


    }
}
