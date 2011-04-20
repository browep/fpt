package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.dao.Storable;
import com.github.browep.fpt.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/10/11
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeeProgressChooser extends FptActivity {
  SeeProgressChooser self = this;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.select_workout);
    LinearLayout wrapper = (LinearLayout) findViewById(R.id.workout_list);

    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

    // add see pictures button
    Button selectButton = (Button) ((LinearLayout) inflater.inflate(R.layout.select_workout_button, wrapper, true)).getChildAt(0);
    selectButton.setOnClickListener(seePicturesOnClickListener);
    selectButton.setText("See Your Pictures");

    List<Storable> definitions = getDao().getByType(C.WORKOUT_DEFINITION_TYPE);
    int i = 1;
    for (Storable definition : definitions) {
      selectButton = (Button) ((LinearLayout) inflater.inflate(R.layout.select_workout_button, wrapper, true)).getChildAt(i);
      String workoutName = (String) definition.get(C.WORKOUT_NAME);
      selectButton.setText(workoutName);
      selectButton.setTag(R.id.workout_definition_id, definition.getId());
      selectButton.setTag(R.id.workout_definition_name, workoutName);
      selectButton.setOnClickListener(selectOnClickListener);
      selectButton.setOnLongClickListener(selectLongClickOnClickListener);
      i++;
    }

    // get all the definitions, inflate view_specific_progress
  }

  public View.OnLongClickListener selectLongClickOnClickListener = new View.OnLongClickListener() {

    public boolean onLongClick(View view) {
      Integer definitionId = (Integer) view.getTag(R.id.workout_definition_id);
      onLongCLickHandlerInner(definitionId, (String) view.getTag(R.id.workout_definition_name), view);
      return true;
    }
  };


  private View.OnClickListener selectOnClickListener = new View.OnClickListener() {
    public void onClick(final View view) {
      final Integer id = (Integer) view.getTag(R.id.workout_definition_id);

      // check to see if ther are actually any entries for this one, display message and finish if no
      Map where = new HashMap();
      where.put(C.WORKOUT_DEFINITION_ID, id.toString());
      List<Storable> entries = Util.sortByModified(getDao().where(where));
      if(entries.size() == 0){
        Util.longToastMessage(self,"You dont't have any entries for this workout yet.");
        return;
      }

      AlertDialog.Builder builder = new AlertDialog.Builder(self);
      builder.setCancelable(true);
      builder.setTitle((CharSequence) view.getTag(R.id.workout_definition_name));
      builder.setInverseBackgroundForced(true);
      builder.setMessage("How would you like to see your workout data?");

      builder.setPositiveButton("Graph", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          Intent intent = new Intent();
          intent.putExtra(C.WORKOUT_DEFINITION_ID, id);
          intent.setClass(self, ViewProgress.class);
          startActivity(intent);
        }
      });

      builder.setNegativeButton("Table", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          Intent intent = new Intent(self, EditData.class);
          intent.putExtra(C.WORKOUT_DEFINITION_ID, id);
          startActivity(intent);
          dialog.dismiss();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();

    }
  };

  private View.OnClickListener seePicturesOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {
      Intent intent = new Intent();
      intent.setClass(self, SeePictures.class);
      startActivity(intent);

    }
  };

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == R.id.workout_modified){
      startActivity(new Intent(self,SeeProgressChooser.class));
      finish();
    }
  }


  @Override
  public String getPageName() {
    return "SeeProgressChooser";
  }
}
