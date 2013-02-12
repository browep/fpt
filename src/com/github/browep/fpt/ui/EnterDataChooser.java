package com.github.browep.fpt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.model.WorkoutDefinition;
import com.github.browep.fpt.util.Util;
import com.github.browep.fpt.view.ArrayAdapter;
import com.github.browep.nosql.Storable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnterDataChooser extends FptActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        // get the list of workouts.  Create a button for each, inflate into scroll view
        setContentView(R.layout.select_workout);

        List<Storable> definitions = getDao().getByType(C.WORKOUT_DEFINITION_TYPE);

        // if there are no workout definitions, show message and close
        if (definitions.size() == 0) {
            Util.longToastMessage(this, "You must define a workout first.  Click \"Define a New Workout\"");
            finish();
        }

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter(this,R.layout.simple_list_row,definitions));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == R.id.workout_modified) // if one of the workouts was modified then we need to refresh the data
            startActivity(new Intent(self, EnterDataChooser.class));
        finish();
    }


    @Override
    public String getPageName() {
        return "EnterDataChooser";
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent addDataActivity = new Intent();
        addDataActivity.setClass(self, EnterData.class);
        addDataActivity.putExtra(C.WORKOUT_DEFINITION, ((WorkoutDefinition) view.getTag()).getId());
        startActivityForResult(addDataActivity, R.id.workout_data_added);
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Integer definitionId = ((WorkoutDefinition) view.getTag()).getId();
        onLongCLickHandlerInner(definitionId, (String) view.getTag(R.id.workout_definition_name), view);
        return true;
    }
}
