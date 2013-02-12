package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.github.browep.fpt.R;
import com.github.browep.fpt.model.WorkoutDefinition;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;
import com.github.browep.nosql.Storable;

import java.text.Format;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.browep.fpt.C.*;

public class EditData extends FptActivity {

    EditData self = this;
    private WorkoutDefinition definition;
    private String xValueName;
    private String label;
    private Format formatter;
    private ListView listView;
    private List<Storable> entries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_data);

        Integer defintionId = getIntent().getExtras().getInt(WORKOUT_DEFINITION_ID);

        definition = (WorkoutDefinition) getDao().get(defintionId);

        Map model = getViewService().getModel((Integer) definition.get(WORKOUT_TYPE));

        // set title
        ((TextView) findViewById(R.id.edit_data_title)).setText((CharSequence) definition.get(WORKOUT_NAME));
        xValueName = (String) model.get(X_VALUE_NAME);

        label = (String) definition.get("label");
        if (StringUtils.isEmpty(label))
            label = (String) getViewService().getPropertyDefinition((Integer) definition.get(WORKOUT_TYPE)).values().iterator().next().get("enter-text");

        ((TextView) findViewById(R.id.edit_data_subtitle)).setText("(" + label + ")");

        Map where = new HashMap();
        where.put(WORKOUT_DEFINITION_ID, defintionId.toString());
        entries = Util.sortByModified(getDao().where(where));

        formatter = WORKOUT_TYPE_TO_X_FORMAT.get(definition.get(WORKOUT_TYPE));

        // setup list adapater
        listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(new EditDataAdapter(this, R.layout.edit_data_entry, entries));
        listView.setOnItemLongClickListener(itemLongClickListener);


        getFptApplication().getTracker().trackEvent(
                "Workout",  // Category
                "Table Viewed",  // Action
                (String) definition.get(WORKOUT_NAME), // Label
                0);

        tutorialDialog(this.getString(R.string.edit_data_message), "View or Edit Data", EDIT_DATA_DIALOG, EditData.this);

    }

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> adapterView, final View view, final int i, long l) {
            AlertDialog alertDialog = new AlertDialog.Builder(EditData.this)
                    .setTitle("What would you like to do?")
                    .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(self, EnterData.class);
                                    intent.putExtra(WORKOUT_ENTRY_ID, (Integer) view.getTag(R.id.workout_entry_id));
                                    intent.putExtra(WORKOUT_DEFINITION, definition.getId());
                                    self.startActivityForResult(intent, R.id.data_entered);
                                    break;
                                case 1:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(self);
                                    builder.setCancelable(true);
                                    builder.setTitle("Confirm Delete");
                                    builder.setInverseBackgroundForced(true);
                                    builder.setMessage("Are you sure you want to delete this workout entry?");

                                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // remove from db
                                            getDao().delete((Integer) view.getTag(R.id.workout_entry_id));
                                            // remove from list, and invalidate views
                                            entries.remove(i);
                                            listView.invalidateViews();
                                        }
                                    });

                                    builder.setNegativeButton("Don't Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    break;
                            }

                        }
                    })
                    .create();
            alertDialog.show();
            return true;
        }
    };


    private View.OnClickListener editDataOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(self, EnterData.class);
            intent.putExtra(WORKOUT_ENTRY_ID, (Integer) view.getTag(R.id.workout_entry_id));
            intent.putExtra(WORKOUT_DEFINITION, definition.getId());
            self.startActivityForResult(intent, R.id.data_entered);

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // data has been entered, close this activity, open the same window
        Intent intent = new Intent(self, EditData.class);
        intent.putExtra(WORKOUT_DEFINITION_ID, definition.getId());
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
                    if (commentView != null)
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

    private class EditDataAdapter extends ArrayAdapter<Storable> {
        private List<Storable> items;

        public EditDataAdapter(Context context, int textViewResourceId, List<Storable> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public EditDataAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public int getCount() {
            return items.size();
        }

        public Storable getItem(int i) {
            return items.get(i);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Storable storable = items.get(position);
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.edit_data_entry, null);
                v.setTag(R.id.workout_entry_id, storable.getId());

            }
            if (storable != null) {
                TextView textView = (TextView) v.findViewById(R.id.content);
                Object entryValue = storable.get(xValueName);
                if (formatter != null) {
                    entryValue = formatter.format(entryValue);
                }
                textView.setText(GRAPH_DISPLAY_FORMAT.format(storable.getCreated()) + ": " + entryValue);
                TextView commentTextView = (TextView) v.findViewById(R.id.comment);
                if (storable.get("comment") != null)
                    commentTextView.setText((CharSequence) storable.get("comment"));
                else
                    commentTextView.setVisibility(View.GONE);

            }
            return v;
        }


    }

}
