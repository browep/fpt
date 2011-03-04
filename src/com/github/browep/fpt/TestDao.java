package com.github.browep.fpt;

import android.os.Bundle;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.Storable;
import com.github.browep.fpt.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/2/11
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestDao extends DaoAwareActivity {
     @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        try {
//            ForRepsWorkout workout = (ForRepsWorkout) dao.initialize(ForRepsWorkout.class);
//            workout.put("status","complete");
//            dao.save(workout);
//            dao.dumpDbToLog();
//
//
//            workout = (ForRepsWorkout) dao.get(workout.getId());
//            workout.put("reps",10);
//            dao.save(workout);
//            dao.dumpDbToLog();
//
//            // find some by where
//            Map<String,String> where = new HashMap<String,String>();
//            where.put("status","complete");
//            List<Storable> storables = dao.where(where);
//            Log.i("found " + storables.size() + " storables");
//            for(Storable storable : storables){
//                Log.i(storable.toString());
//            }
//
//        } catch (InstantiationException e) {
//            Log.e("", e);
//
//        } catch (IllegalAccessException e) {
//            Log.e("", e);
//
//        } catch (IOException e) {
//            Log.e("", e);
//
//        } catch (ParseException e) {
//            Log.e("", e);
//        }


    }
}
