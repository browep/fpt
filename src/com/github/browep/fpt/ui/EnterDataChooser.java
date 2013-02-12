package com.github.browep.fpt.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.github.browep.fpt.C;
import com.github.browep.fpt.FptApp;
import com.github.browep.fpt.R;
import com.github.browep.fpt.dao.PreferencesService;
import com.github.browep.fpt.model.WorkoutDefinition;
import com.github.browep.fpt.util.Util;
import com.github.browep.fpt.view.ArrayAdapter;
import com.github.browep.nosql.Storable;

import java.util.ArrayList;
import java.util.List;

import static com.github.browep.fpt.SortOrder.ALPHABETICALLY;
import static com.github.browep.fpt.SortOrder.MODIFIED;

public class EnterDataChooser extends FptActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private PreferencesService prefsService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        // get the list of workouts.  Create a button for each, inflate into scroll view
        prefsService = FptApp.getInstance().getPreferencesService();

        setContentView(R.layout.select_workout);

        fillData();
    }

    private void fillData() {
        List<Storable> byType = getDao().getByType(C.WORKOUT_DEFINITION_TYPE);
        List<WorkoutDefinition> definitions = new ArrayList<WorkoutDefinition>();
        for(Storable storable : byType){
            definitions.add((WorkoutDefinition) storable);
        }

        // if there are no workout definitions, show message and close
        if (definitions.size() == 0) {
            Util.longToastMessage(this, "You must define a workout first.  Click \"Define a New Workout\"");
            finish();
        }

        if ( prefsService.getIntPreference(C.ENTER_DATA_SORT_ORDER) == ALPHABETICALLY.ordinal()){
            Util.sortAlphabetically(definitions);
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

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(prefsService.getIntPreference(C.ENTER_DATA_SORT_ORDER) == ALPHABETICALLY.ordinal()){
            menu.add(0, MODIFIED.ordinal(),0,R.string.sort_modified);
        } else {
            menu.add(0, ALPHABETICALLY.ordinal(),0,R.string.sort_alphabetically);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        prefsService.setIntPreference(C.ENTER_DATA_SORT_ORDER, item.getItemId());
        if(Build.VERSION.SDK_INT >= 11)
            invalidateOptionsMenu();
        fillData();
        return true;
    }

}
