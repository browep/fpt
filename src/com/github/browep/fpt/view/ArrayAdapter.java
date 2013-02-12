package com.github.browep.fpt.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.browep.fpt.R;
import com.github.browep.fpt.model.ToTitleable;

import java.util.List;

public class ArrayAdapter extends android.widget.ArrayAdapter<ToTitleable> {
    private static String TAG = ArrayAdapter.class.getCanonicalName();
    private int layoutResourceId;
    private List objects;

    public ArrayAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);

        layoutResourceId = textViewResourceId;
        this.objects = objects;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        ToTitleable item = getItem(position);
        convertView.setTag(item);
        ((TextView) convertView.findViewById(R.id.text)).setText(item.toTitle());

        return convertView;
    }
}
