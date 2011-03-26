package com.github.browep.fpt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.Storable;
import com.github.browep.fpt.util.FptDateFormat;
import com.github.browep.fpt.util.StringUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/10/11
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewProgress extends DaoAwareActivity {
  public static final String EDIT_SEE_COMMENTS_TEXT = "Edit / See Comments";
  Integer workoutDefinitionId;
  WorkoutDefinition definition;
  private XYPlot xyPlot;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_progress);

    xyPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

    workoutDefinitionId = getIntent().getExtras().getInt(C.WORKOUT_DEFINITION_ID);
    definition = (WorkoutDefinition) dao.get(workoutDefinitionId);

    List<Number> yValues = new LinkedList<Number>();
    List<Number> xValues = new LinkedList<Number>();

    Map model = getViewService().getModel((Integer) definition.get(C.WORKOUT_TYPE));

    Map where = new HashMap();
    where.put(C.WORKOUT_DEFINITION_ID, workoutDefinitionId.toString());
    List<Storable> entries = dao.where(where);

    String xValuePropName = (String) model.get("x_value_name");

    for (Storable storable : entries) {
      Workout workout = (Workout) storable;
      yValues.add((Number) workout.get(xValuePropName));
      xValues.add(workout.getCreated().getTime());
    }

    xyPlot.setBackgroundColor(Color.TRANSPARENT);

    xyPlot.setDrawBorderEnabled(false);

    XYSeries series1 = new SimpleXYSeries(
        xValues,
        yValues,
        "");
    // Create a formatter to use for drawing a series using LineAndPointRenderer:
    LineAndPointFormatter series1Format = new LineAndPointFormatter(
        Color.BLUE,                   // line color
        Color.rgb(0, 100, 0));

    xyPlot.getLegendWidget().setHeight(0);
    xyPlot.getLegendWidget().setWidth(0);
    xyPlot.setDomainLabel("");

    xyPlot.addSeries(series1, series1Format);
    xyPlot.setTitle((String) definition.get(C.WORKOUT_NAME));

    xyPlot.setDomainValueFormat(new FptDateFormat());

    if(C.WORKOUT_TYPE_TO_X_FORMAT.containsKey(model.get("id"))){
      xyPlot.setRangeValueFormat(C.WORKOUT_TYPE_TO_X_FORMAT.get(model.get("id")));
    }
    if(model.containsKey("y_plot_width")){
      xyPlot.getGraphWidget().setRangeLabelWidth(((Integer)model.get("y_plot_width")).floatValue());
    }
    if(model.containsKey("y_label")) {
      String yLabel = (String) model.get("y_label");
      if(!StringUtils.isEmpty((String) definition.get("label")))
        yLabel += " (" + definition.get("label") + ")";
      xyPlot.setRangeLabel(yLabel);
    }

    xyPlot.getRangeLabelWidget().pack();

    xyPlot.disableAllMarkup();


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem item = menu.add(EDIT_SEE_COMMENTS_TEXT);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // open up the edit screen
    Intent intent = new Intent(this, EditData.class);
    intent.putExtra(C.WORKOUT_DEFINITION_ID, definition.getId());
    startActivity(intent);
    return true;
  }
}
