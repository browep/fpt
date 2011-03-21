package com.github.browep.fpt;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.Storable;
import com.github.browep.fpt.util.FptDateFormat;

import java.text.Format;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/10/11
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewProgress extends DaoAwareActivity{
    Integer workoutDefinitionId;
    WorkoutDefinition definition;
    private XYPlot mySimpleXYPlot;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_progress);

        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        workoutDefinitionId = getIntent().getExtras().getInt(C.WORKOUT_DEFINITION_ID);
        definition = (WorkoutDefinition) dao.get(workoutDefinitionId);

        List<Number> yValues = new LinkedList<Number>();
        List<Number> xValues = new LinkedList<Number>();

        Map where = new HashMap();
        where.put(C.WORKOUT_DEFINITION_ID,workoutDefinitionId.toString());
        List<Storable> entries = dao.where(where);

        String xValuePropName = C.WORKOUT_TYPE_TO_X_PROP_NAME.get(definition.getType());
//        if(C.WORKOUT_TYPE_TO_X_FORMAT.get(definition.getType())!=null)
//            mySimpleXYPlot.setRangeValueFormat();

        for(Storable storable : entries){
            Workout workout = (Workout) storable;
            yValues.add((Number) workout.get(xValuePropName));
            xValues.add(workout.getCreated().getTime());
        }

        
        mySimpleXYPlot.setBackgroundColor(Color.TRANSPARENT);

        mySimpleXYPlot.setDrawBorderEnabled(false);

        XYSeries series1 = new SimpleXYSeries(
                xValues,
                yValues,
                "");
                                 // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.BLUE,                   // line color
                Color.rgb(0, 100, 0));

        mySimpleXYPlot.addSeries(series1, series1Format);
        mySimpleXYPlot.setTitle((String) definition.get(C.WORKOUT_NAME));

        mySimpleXYPlot.setDomainValueFormat( new FptDateFormat());

        mySimpleXYPlot.disableAllMarkup();


    }

}
