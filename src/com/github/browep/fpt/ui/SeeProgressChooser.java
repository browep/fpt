package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.github.browep.fpt.R;
import com.github.browep.fpt.util.Util;
import com.github.browep.fpt.view.ArrayAdapter;
import com.github.browep.nosql.Storable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.browep.fpt.C.*;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/10/11
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeeProgressChooser extends FptActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
  SeeProgressChooser self = this;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.select_workout);

    // add see pictures button
//    Button selectButton = (Button) ((LinearLayout) inflater.inflate(R.layout.select_workout_button, wrapper, true)).getChildAt(0);
//    selectButton.setOnClickListener(seePicturesOnClickListener);
//    selectButton.setText("See Your Pictures");

      List<Storable> definitions = getDao().getByType(WORKOUT_DEFINITION_TYPE);

      ListView listView = (ListView) findViewById(R.id.listview);
      listView.setAdapter(new ArrayAdapter(this, R.layout.simple_list_row, definitions));
      listView.setOnItemClickListener(this);
      listView.setOnItemLongClickListener(this);

      tutorialDialog(this.getString(R.string.view_progress_message),
        "Viewing Your Progress",
        VIEW_PROGRESS_DIALOG,
        SeeProgressChooser.this);
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
      where.put(WORKOUT_DEFINITION_ID, id.toString());
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
          intent.putExtra(WORKOUT_DEFINITION_ID, id);
          intent.setClass(self, ViewProgress.class);
          startActivity(intent);
        }
      });

      builder.setNegativeButton("Table", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          Intent intent = new Intent(self, EditData.class);
          intent.putExtra(WORKOUT_DEFINITION_ID, id);
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
      // check to see if the SD-card is mounted
      String state = Environment.getExternalStorageState();
      if (!Environment.MEDIA_MOUNTED.equals(state)) {
        Util.longToastMessage(self, SD_CARD_NOT_MOUNTED_MESSAGE);
      } else {
        Intent intent = new Intent();
        intent.setClass(self, SeePictures.class);
        startActivity(intent);
      }

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

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }
}
