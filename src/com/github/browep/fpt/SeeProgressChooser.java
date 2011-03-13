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
 * Date: 3/10/11
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeeProgressChooser extends DaoAwareActivity {
    SeeProgressChooser self = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_workout);
        LinearLayout wrapper = (LinearLayout) findViewById(R.id.workout_list);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // add see pictures button
        Button selectButton = (Button) ((LinearLayout)inflater.inflate(R.layout.select_workout_button, wrapper,true)).getChildAt(0);
        selectButton.setOnClickListener(seePicturesOnClickListener);
        selectButton.setText("See Your Pictures");

        List<Storable> definitions = dao.getByType(C.WORKOUT_DEFINITION_TYPE);
        int i=1;
        for(Storable definition : definitions){
            selectButton = (Button) ((LinearLayout)inflater.inflate(R.layout.select_workout_button, wrapper,true)).getChildAt(i);
            selectButton.setText((CharSequence) definition.get(C.WORKOUT_NAME));
            selectButton.setTag(R.id.workout_definition_id,definition.getId());
            selectButton.setOnClickListener(selectOnClickListener);
            i++;
        }

        // get all the definitions, inflate view_specific_progress
    }

    private View.OnClickListener selectOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent();
            Integer id = (Integer) view.getTag(R.id.workout_definition_id);
            intent.putExtra(C.WORKOUT_DEFINITION_ID,id);
            intent.setClass(self,ViewProgress.class);
            startActivity(intent);

        }
    };

    private View.OnClickListener seePicturesOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(self,SeePictures.class);
            startActivity(intent);

        }
    };

}
