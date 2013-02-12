package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.model.ToTitleable;
import com.github.browep.fpt.util.Util;
import com.github.browep.fpt.view.ArrayAdapter;
import com.github.browep.nosql.Storable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.browep.fpt.C.*;

public class SeeProgressChooser extends FptActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    SeeProgressChooser self = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_workout);

        List<Storable> definitions = getDao().getByType(WORKOUT_DEFINITION_TYPE);
        // add see pictures button
        definitions.add(0, new SeePicturesRow());

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter(this, R.layout.simple_list_row, definitions));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        tutorialDialog(this.getString(R.string.view_progress_message),
                "Viewing Your Progress",
                VIEW_PROGRESS_DIALOG,
                SeeProgressChooser.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == R.id.workout_modified) {
            startActivity(new Intent(self, SeeProgressChooser.class));
            finish();
        }
    }

    @Override
    public String getPageName() {
        return "SeeProgressChooser";
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Storable storable = (Storable) view.getTag();
        if (storable instanceof SeePicturesRow) {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                Util.longToastMessage(self, SD_CARD_NOT_MOUNTED_MESSAGE);
            } else {
                Intent intent = new Intent();
                intent.setClass(self, SeePictures.class);
                startActivity(intent);
            }
        } else {
            final Integer id = storable.getId();
            // check to see if ther are actually any entries for this one, display message and finish if no
            Map where = new HashMap();
            where.put(WORKOUT_DEFINITION_ID, id.toString());
            List<Storable> entries = Util.sortByModified(getDao().where(where));
            if (entries.size() == 0) {
                Util.longToastMessage(self, "You don't have any entries for this workout yet.");
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
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Storable storable = (Storable) view.getTag();
        if (storable instanceof SeePicturesRow) {
            return false; // dont do anything for SeePictureRow
        }

        Integer definitionId = storable.getId();
        onLongCLickHandlerInner(definitionId, (String) storable.get(C.WORKOUT_NAME), view);
        return true;
    }

    public class SeePicturesRow extends Storable implements ToTitleable {

        public String toTitle() {
            return getString(R.string.see_your_pictures);
        }

        @Override public int getType() {
            return 0;
        }

        @Override public List<String> getIndexBys() {
            return null;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
        }
    }
}
