package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.model.WorkoutDefinition;
import com.github.browep.nosql.Storable;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;

import java.text.Format;
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
public class EditData extends FptActivity {

  EditData self = this;
  private WorkoutDefinition definition;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.edit_data);

    Integer defintionId = getIntent().getExtras().getInt(C.WORKOUT_DEFINITION_ID);

    definition = (WorkoutDefinition) getDao().get(defintionId);

    Map model = getViewService().getModel((Integer) definition.get(C.WORKOUT_TYPE));

    // set title
    ((TextView) findViewById(R.id.edit_data_title)).setText((CharSequence) definition.get(C.WORKOUT_NAME));
    String xValueName = (String) model.get(C.X_VALUE_NAME);

    String label = (String) definition.get("label");
    if (StringUtils.isEmpty(label))
      label = (String) getViewService().getPropertyDefinition((Integer) definition.get(C.WORKOUT_TYPE)).values().iterator().next().get("enter-text");

    ((TextView) findViewById(R.id.edit_data_subtitle)).setText("(" + label + ")");

    Map where = new HashMap();
    where.put(C.WORKOUT_DEFINITION_ID, defintionId.toString());
    List<Storable> entries = Util.sortByModified(getDao().where(where));

    LinearLayout wrapper = (LinearLayout) findViewById(R.id.edit_data_list);

    Button editDataButton;

    Format formatter = C.WORKOUT_TYPE_TO_X_FORMAT.get(definition.get(C.WORKOUT_TYPE));

    int i = 0;
    for (Storable entry : entries) {

      // inflate the edit_data_entry layout
//      LinearLayout inflatedLayout = (LinearLayout) ((LinearLayout) inflater.inflate(R.layout.edit_data_entry, wrapper, true)).getChildAt(i);
      LinearLayout inflatedLayout = (LinearLayout) View.inflate(this, R.layout.edit_data_entry, null);

      // setup edit button
      editDataButton = (Button) inflatedLayout.findViewById(R.id.edit_data_button);
      editDataButton.setOnClickListener(editDataOnClickListener);
      editDataButton.setTag(R.id.workout_entry_id, entry.getId());

      // setup delete button
      View deleteButton = inflatedLayout.findViewById(R.id.delete_data_button);
      deleteButton.setTag(R.id.workout_entry_id, entry.getId());
      deleteButton.setTag(R.id.view_parent, inflatedLayout);
      deleteButton.setOnClickListener(deleteDataOnClickListener);

      Object entryValue = entry.get(xValueName);
      if (formatter != null) {
        entryValue = formatter.format(entryValue);
      }
      ((TextView) inflatedLayout.findViewById(R.id.edit_text)).setText(C.GRAPH_DISPLAY_FORMAT.format(entry.getCreated()) + ": " + entryValue);

      wrapper.addView(inflatedLayout);

      // if there are comments then add a textview below it
      if (!StringUtils.isEmpty((String) entry.get("comment"))) {
        LinearLayout commentLayout = (LinearLayout) View.inflate(this, R.layout.edit_data_comment, null);
        TextView commentView = (TextView) commentLayout.findViewById(R.id.comment);
        commentView.setText((CharSequence) entry.get("comment"));
        wrapper.addView(commentLayout);
        deleteButton.setTag(R.id.view_parent_2, commentView);
        i++;
      }

      i++;
    }


    getFptApplication().getTracker().trackEvent(
           "Workout",  // Category
           "Table Viewed",  // Action
           (String)definition.get(C.WORKOUT_NAME), // Label
           0 );

  }


  private View.OnClickListener editDataOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {
      Intent intent = new Intent(self,EnterData.class);
      intent.putExtra(C.WORKOUT_ENTRY_ID, (Integer) view.getTag(R.id.workout_entry_id));
      intent.putExtra(C.WORKOUT_DEFINITION, definition.getId());
      self.startActivityForResult(intent, R.id.data_entered);

    }
  };


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // data has been entered, close this activity, open the same window
    Intent intent = new Intent(self,EditData.class);
    intent.putExtra(C.WORKOUT_DEFINITION_ID,definition.getId());
    startActivity(intent);
    finish();
  }

  private View.OnClickListener deleteDataOnClickListener = new View.OnClickListener() {
    public void onClick(final View view) {

      AlertDialog.Builder builder = new AlertDialog.Builder(self);
      builder.setCancelable(true);
      builder.setTitle("Confirm Delete");
      builder.setInverseBackgroundForced(true);
      builder.setMessage("Are you sure you want to delete this workout entry?");

      builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          getDao().delete((Integer) view.getTag(R.id.workout_entry_id));
          View parentView = (View) view.getTag(R.id.view_parent);
          parentView.setVisibility(View.GONE);
          View commentView = (View) view.getTag(R.id.view_parent_2);
          if(commentView != null)
            commentView.setVisibility(View.GONE);
        }
      });

      builder.setNegativeButton("Don't Delete", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();

    }
  };



  @Override
  public String getPageName() {
    return "EditData";
  }

}
