package com.github.browep.fpt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.Storable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnterDataChooser extends DaoAwareActivity {
    EnterDataChooser self = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        // get the list of workouts.  Create a button for each, inflate into scroll view
        setContentView(R.layout.select_workout);
        LinearLayout wrapper = (LinearLayout) findViewById(R.id.workout_list);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        List<Storable> definitions = dao.getByType(C.WORKOUT_DEFINITION_TYPE);
        int i=0;
        for(Storable definition : definitions){
            Button selectButton = (Button) ((LinearLayout)inflater.inflate(R.layout.select_workout_button, wrapper,true)).getChildAt(i);
            selectButton.setText((CharSequence) definition.get(C.WORKOUT_NAME));
            selectButton.setTag(R.id.workout_definition_id, definition.getId());
            selectButton.setOnClickListener(selectOnClickListener);
            selectButton.setOnLongClickListener(selectLongClickOnClickListener);
            i++;
        }
    }

    public View.OnClickListener selectOnClickListener = new View.OnClickListener(){
        public void onClick(View view) {
            Intent addDataActivity = new Intent();
            addDataActivity.setClass(self,AddDataActivity.class);
            addDataActivity.putExtra(C.WORKOUT_DEFINITION, (Integer) view.getTag(R.id.workout_definition_id));
            startActivityForResult(addDataActivity,R.id.workout_data_added);
        }
    };

    public View.OnLongClickListener selectLongClickOnClickListener = new View.OnLongClickListener(){

        public boolean onLongClick(View view) {
            Intent addDataActivity = new Intent();
            addDataActivity.setClass(self,WorkoutDefinitionForm.class);
            addDataActivity.putExtra(C.WORKOUT_DEFINITION_ID, (Integer) view.getTag(R.id.workout_definition_id));
            startActivityForResult(addDataActivity,R.id.workout_data_added);
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
