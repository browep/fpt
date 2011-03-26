package com.github.browep.fpt;

import com.github.browep.fpt.dao.FptSqliteOpener;
import com.github.browep.fpt.util.FptTimeFormat;
import com.github.browep.fpt.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/3/11
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class C {


    public static final Integer MILLIS_IN_HOURS =  1000 * 60 *60;
    public static final Integer  MILLIS_IN_MINUTES =  1000 * 60;
    public static final Integer  MILLIS_IN_SECONDS =  1000;
    public static final String TIME = "time";
    public static final String DISTANCE = "distance";
    public static final String MAX_WEIGHT = "max_weight";
  public static final String X_VALUE_NAME = "x_value_name";

  private C(){} // should never be used, every

    public static final String WORKOUT_CLASS = "workout_class";
    public static final Integer FOR_REPS_WORKOUT_TYPE = 10;
    public static final Integer FOR_TIME_WORKOUT_TYPE = 11;
    public static final Integer FOR_DISTANCE_WORKOUT_TYPE = 12;
    public static final Integer FOR_MAX_WEIGHT_WORKOUT_TYPE = 13;

    public static final Integer WORKOUT_DEFINITION_TYPE = 5;

    public static final String WORKOUT_OBJECT = "workout_object";



    public static final Integer[] WORKOUT_TYPES = new Integer[]{FOR_REPS_WORKOUT_TYPE,FOR_TIME_WORKOUT_TYPE,FOR_DISTANCE_WORKOUT_TYPE,FOR_MAX_WEIGHT_WORKOUT_TYPE};
    public static final String WORKOUT_TYPE = "workout_type";
    public static final String WORKOUT_DEFINITION = "workout_definition";
    public static final String WORKOUT_DEFINITION_ID = "workout_definition_id";
    public static final String WORKOUT_NAME = "workout_name";
    public static final String FLURRY_ID = "PJ4JF52Q57JRP84IIGAY";
    public static final String REPS = "reps";
    static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("EEEEE, MMMMM dd, yyyy");
    public static final SimpleDateFormat GRAPH_DISPLAY_FORMAT = new SimpleDateFormat("MM/dd/yy");
    public static final Long MILLIS_IN_A_DAY =86400000L;
    public static final String COMMENT = "comment";

    static HashMap<Integer, Format> WORKOUT_TYPE_TO_X_FORMAT = new HashMap<Integer, Format>();
    static {
        WORKOUT_TYPE_TO_X_FORMAT.put(C.FOR_TIME_WORKOUT_TYPE,new FptTimeFormat());
    }







}
