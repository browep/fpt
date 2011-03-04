package com.github.browep.fpt;

import android.os.Bundle;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/3/11
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateWorkout2 extends DaoAwareActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        Workout workout = (Workout) getIntent().getExtras().get(C.WORKOUT_OBJECT);
        Log.i("in CreateWorkout2, got this: " + workout.toString());
    }
}
