package com.github.browep.fpt;

import android.content.Context;
import com.github.browep.fpt.dao.Dao;
import com.github.browep.fpt.dao.Storable;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/3/11
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class FptDao extends Dao {
     public FptDao(Context context){
        super(context);
    }

    public Storable initialize(int type){
        Workout workout = new Workout(type);
        return initialize(workout);
    }
}
