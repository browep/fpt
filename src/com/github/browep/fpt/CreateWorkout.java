package com.github.browep.fpt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.github.browep.fpt.dao.DaoAwareActivity;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/3/11
 * Time: 12:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateWorkout extends DaoAwareActivity {
    CreateWorkout self = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_workout);

        View forDistanceButton = findViewById(R.id.for_distance_button);
        forDistanceButton.setTag(R.id.associated_class, C.FOR_DISTANCE_WORKOUT_TYPE);
        forDistanceButton.setOnClickListener(onClickListener);

        View forRepsButton = findViewById(R.id.for_reps_button);
        forRepsButton.setTag(R.id.associated_class, C.FOR_REPS_WORKOUT_TYPE);
        forRepsButton.setOnClickListener(onClickListener);


        View forTimeButton = findViewById(R.id.for_time_button);
        forTimeButton.setTag(R.id.associated_class, C.FOR_TIME_WORKOUT_TYPE);
        forTimeButton.setOnClickListener(onClickListener);

        View forMaxWeightButton = findViewById(R.id.for_max_weight_button);
        forMaxWeightButton.setTag(R.id.associated_class, C.FOR_MAX_WEIGHT_WORKOUT_TYPE);
        forMaxWeightButton.setOnClickListener(onClickListener);


    }

    public View.OnClickListener onClickListener = new View.OnClickListener(){
        public void onClick(View view) {
            Intent createWorkout2 = new Intent();
            Integer type = (Integer) view.getTag(R.id.associated_class);
            WorkoutDefinition definition = new WorkoutDefinition();
            definition.put(C.WORKOUT_TYPE,type);

            createWorkout2.putExtra(C.WORKOUT_DEFINITION, definition);
            createWorkout2.setClass(self,WorkoutDefinitionForm.class);
            self.startActivityForResult(createWorkout2,R.id.workout_saved);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.
        finish();
    }
}
