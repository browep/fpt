package com.github.browep.fpt.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.model.WorkoutDefinition;
import com.github.browep.fpt.ui.SubmittableActivity;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;

import java.util.List;
import java.util.Map;

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
      isEdit = true;
    } else
      throw new IllegalStateException("could not get a workout definition or workout definition id out of the bundle");

    setContentView(R.layout.submittable);
    LinearLayout wrapper = (LinearLayout) findViewById(R.id.submittable);
    LinearLayout workoutForm;
    Integer workoutType = (Integer) definition.get(C.WORKOUT_TYPE);
    Map model = getViewService().getModel(workoutType);

    Integer definitionFormId = model.get("define_data_template_id") != null ? (Integer) model.get("define_data_template_id") : R.layout.create_workout_default;
    workoutForm = (LinearLayout) View.inflate(this, definitionFormId, null);

    // set the helper text
    TextView nameHelperTextView = (TextView) workoutForm.findViewById(R.id.name_helper_text);
    if (nameHelperTextView != null) {
      nameHelperTextView.setText((String) model.get("name_label"));
    }

    // make enter in the name hide the keyboard
    workoutForm.findViewById(R.id.name_box).setOnKeyListener(hideKeyBoardListener);

    RadioGroup radioGroup = (RadioGroup) workoutForm.findViewById(R.id.label_radio);
    if(radioGroup != null && model.get("possible_labels")  != null){
      String label = (String) definition.get("label");
      // fill with all possible labels
      List<String> possibleLabels = (List<String>) model.get("possible_labels");
      boolean radioDefaulted = false;
      for(String possibleLabel : possibleLabels){
        RadioButton radioButton = new RadioButton(this);
        radioButton.setTextColor(Color.BLACK);
        radioButton.setText(possibleLabel);
        radioGroup.addView(radioButton);
        if(!StringUtils.isEmpty(label) && label.equals(possibleLabel)){
          radioButton.setChecked(true);
          radioDefaulted = true;
        }
        else if(!radioDefaulted)          {
          radioButton.setChecked(true);
          radioDefaulted = true;
        }
      }

    }


    if (isEdit) { // update the contents
      ((EditText) workoutForm.findViewById(R.id.name_box)).setText((CharSequence) definition.get(C.WORKOUT_NAME));

      updateSubmitButtonText("Update");


    }

    wrapper.addView(workoutForm);


  }

  @Override
  public void onSubmit(View view) {

    // grab the details from the form, put into definition, save
    EditText nameBox = (EditText) findViewById(R.id.name_box);

    if (!(definition.getId() > 0)) // we are creating a workout here, need to init it
      definition = (WorkoutDefinition) dao.initialize(definition);

    String name = nameBox.getText().toString();

    if (!StringUtils.isEmpty(name)) {
      definition.put(C.WORKOUT_NAME, name);
    } else {
      Util.longToastMessage(this, "You must input a name first");
      return;
    }

    // look for radio button for distance type
    RadioGroup radioGroup = (RadioGroup) findViewById(R.id.label_radio);
    if(radioGroup != null){
      String selectedText = null;
      int count = radioGroup.getChildCount();
      for (int i = 0; i < count; i++) {
        View o = radioGroup.getChildAt(i);
        if (o instanceof RadioButton) {
          RadioButton radioButton = (RadioButton) o;
          if (radioButton.isChecked()) {
            selectedText = radioButton.getText().toString();
          }
        }
      }

      definition.put("label",selectedText);
    }

    dao.save(definition);
    Util.longToastMessage(this, "'" + name + "' has been " + (isEdit ? "updated." : "created."));
    finish();
  }
}

