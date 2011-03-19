package com.github.browep.fpt;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/3/11
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkoutDefinitionForm extends SubmittableActivity {
    private WorkoutDefinition definition;

    private boolean isEdit = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        // check to see if this is create or edit

        Bundle extras = getIntent().getExtras();
        if (extras.get(C.WORKOUT_DEFINITION) != null) {
            definition = (WorkoutDefinition) extras.get(C.WORKOUT_DEFINITION);
        } else if (extras.get(C.WORKOUT_DEFINITION_ID) != null) {
            Integer workoutDefintionId = extras.getInt(C.WORKOUT_DEFINITION_ID);
            definition = (WorkoutDefinition) dao.get(workoutDefintionId);
            isEdit=true;
        }else
            throw new IllegalStateException("could not get a workout definition or workout definition id out of the bundle");

        setContentView(R.layout.submittable);
        LinearLayout wrapper = (LinearLayout) findViewById(R.id.submittable);
        LinearLayout workoutForm;
        Integer workoutType = (Integer) definition.get(C.WORKOUT_TYPE);

        if (C.WORKOUT_TYPE_TO_FORM_ID.get(workoutType) == null)
            throw new IllegalStateException("definition type of  " + workoutType + " could not be determined");

        workoutForm = (LinearLayout) View.inflate(this, C.WORKOUT_TYPE_TO_FORM_ID.get(workoutType), null);

        // set the helper text
        TextView nameHelperTextView = (TextView) workoutForm.findViewById(R.id.name_helper_text);
        if(nameHelperTextView != null){
            nameHelperTextView.setText(C.WORKOUT_TYPE_TO_NAME_LABEL.get(workoutType));
        }

        if(isEdit){ // update the contents
            ((EditText)workoutForm.findViewById(R.id.name_box)).setText((CharSequence) definition.get(C.WORKOUT_NAME));

            updateSubmitButtonText("Update");

        }

        wrapper.addView(workoutForm);



    }

    @Override
    public void onSubmit(View view) {

        // grab the details from the form, put into definition, save
        EditText nameBox = (EditText) findViewById(R.id.name_box);

        if(!(definition.getId() > 0)) // we are creating a workout here, need to init it
            definition = (WorkoutDefinition) dao.initialize(definition);

        String name = nameBox.getText().toString();

        if (!StringUtils.isEmpty(name)) {
            definition.put(C.WORKOUT_NAME, name);
            dao.save(definition);
            Util.longToastMessage(this,"'" +name + "' has been " + (isEdit ? "updated." : "created."));
            finish();
        }
        else{
            Util.longToastMessage(this,"You must input a name first");
        }
    }
}

