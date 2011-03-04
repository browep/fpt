package com.github.browep.fpt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.github.browep.fpt.dao.DaoAwareActivity;

import java.util.Date;

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


    }

    public View.OnClickListener onClickListener = new View.OnClickListener(){
        public void onClick(View view) {
            Intent createWorkout2 = new Intent();
            Integer type = (Integer) view.getTag(R.id.associated_class);
            Workout workout = new Workout(type);
//            workout.setCreated(new Date());
//            workout.setModified(new Date());

            createWorkout2.putExtra(C.WORKOUT_OBJECT, workout);
            createWorkout2.setClass(self,CreateWorkout2.class);
            self.startActivity(createWorkout2);
        }
    };

}
