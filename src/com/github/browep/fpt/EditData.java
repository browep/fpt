package com.github.browep.fpt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.Storable;
import com.github.browep.fpt.util.Util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/24/11
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditData extends DaoAwareActivity {

  EditData self = this;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.edit_data);

    Integer defintionId = getIntent().getExtras().getInt(C.WORKOUT_DEFINITION_ID);

    WorkoutDefinition definition = (WorkoutDefinition) dao.get(defintionId);

    // set title
    ((TextView)findViewById(R.id.edit_data_title)).setText((CharSequence) definition.get(C.WORKOUT_NAME) );
    ((TextView)findViewById(R.id.edit_data_subtitle)).setText("("+ definition.get(C.X_VALUE_NAME) +")");

    Map where = new HashMap();
    where.put(C.WORKOUT_DEFINITION_ID, defintionId.toString());
    List<Storable> entries = dao.where(where);

    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout wrapper = (LinearLayout) findViewById(R.id.edit_data_list);

    Button editDataButton;

    Format formatter = C.WORKOUT_TYPE_TO_X_FORMAT.get(definition.getType());

    for (Storable entry : entries) {
      LinearLayout inflatedLayout = (LinearLayout) inflater.inflate(R.layout.edit_data_entry, wrapper, true);
      editDataButton = (Button) inflatedLayout.findViewById(R.id.edit_data_button);
      editDataButton.setOnClickListener(editDataOnClickListener);
      editDataButton.setTag(R.id.workout_entry_id,entry.getId());

      View deleteButton = inflatedLayout.findViewById(R.id.delete_data_button);
      deleteButton.setTag(R.id.workout_entry_id,entry.getId());
      deleteButton.setOnClickListener(deleteDataOnClickListener);

      Object entryValue = entry.get(C.X_VALUE_NAME);
      if (formatter != null) {
        entryValue = formatter.format(entryValue);
      }
      ((TextView)inflatedLayout.findViewById(R.id.edit_text)).setText(C.GRAPH_DISPLAY_FORMAT.format(entry.getCreated()) +": " + entryValue);
    }

  }


  private View.OnClickListener editDataOnClickListener = new View.OnClickListener() {
      public void onClick(View view) {
        Util.longToastMessage(self,"Edit Clicked");


      }
  };
  private View.OnClickListener deleteDataOnClickListener = new View.OnClickListener() {
      public void onClick(View view) {
        Util.longToastMessage(self,"Delete Clicked");


      }
  };



}
