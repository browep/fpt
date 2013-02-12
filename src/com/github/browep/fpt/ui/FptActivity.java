package com.github.browep.fpt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import com.github.browep.fpt.C;
import com.github.browep.fpt.FptApp;
import com.github.browep.fpt.R;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.PreferencesService;
import com.github.browep.fpt.util.Util;
import com.github.browep.nosql.Storable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/27/11
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FptActivity extends DaoAwareActivity{
  protected Activity self = this;

  protected void onLongCLickHandlerInner(final Integer definitionId, final String definitionName,final View toRemove) {

    AlertDialog.Builder builder = new AlertDialog.Builder(self);
    builder.setCancelable(true);
    builder.setTitle(definitionName);
    builder.setInverseBackgroundForced(true);
    builder.setMessage("What would you like to do with this workout definition?");

    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();

        Intent addDataActivity = new Intent();
        addDataActivity.setClass(self, WorkoutDefinitionForm.class);
        addDataActivity.putExtra(C.WORKOUT_DEFINITION_ID, definitionId);
        startActivityForResult(addDataActivity, R.id.workout_modified);
      }
    });

    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        deleteOnClick(definitionId,definitionName,toRemove);
        dialog.dismiss();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();

  }

  protected void deleteOnClick(final Integer definitionId,final String definitionName,final View toRemove){

    AlertDialog.Builder builder = new AlertDialog.Builder(self);
    builder.setCancelable(true);
    builder.setTitle("Confirm Delete");
    builder.setInverseBackgroundForced(true);
    builder.setMessage("Are you sure you want to delete \""+definitionName+"\"?  All data for it will be lost.  There is no undo!");

    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        deleteWorkout(definitionId);
        dialog.dismiss();
        if(toRemove != null)
          toRemove.setVisibility(View.GONE);
        Util.longToastMessage(self,"\""+definitionName + "\" has been deleted.");
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


  public void deleteWorkout(Integer workoutDefintionId){

    // delete all the workout entries

    Map where = new HashMap();
    where.put(C.WORKOUT_DEFINITION_ID, workoutDefintionId.toString());
    List<Storable> entries = getDao().where(where);
    for(Storable entry: entries)
        getDao().delete(entry.getId());

    // delete the actual workout definition
    getDao().delete(workoutDefintionId);
  }

    protected void tutorialDialog(String message, String title, final String dialogName, final Activity context) {

        final PreferencesService prefsService = FptApp.getInstance().getPreferencesService();

//    prefsService.setBooleanPreference(dialogName, false); // for debug purposes, remove for prod

        if (!prefsService.getBooleanPreference(dialogName)) {
            final AlertDialog dialog = new AlertDialog.Builder(context).create();
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    prefsService.setBooleanPreference(dialogName, true);
                }
            });
            dialog.show();
        }
    }

}
