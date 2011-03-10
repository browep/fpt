package com.github.browep.fpt;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.browep.fpt.dao.Storable;
import com.github.browep.fpt.util.Log;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FPTStorable extends Storable {


    public FPTStorable(String data) throws IOException, JSONException {
        super(data);
    }

    public FPTStorable(){
        super();
    }


    @Override
    public List<String> getIndexBys() {
        List<String> indexes = new LinkedList<String>();
        Map<String, String> propsToIndex = new HashMap<String, String>();
        propsToIndex.put("status", "incomplete");

        for (Map.Entry<String, String> entry : propsToIndex.entrySet())
            indexes.add(toIndexPath(entry.getKey(), entry.getValue()));

        return indexes;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        try {

            parcel.writeInt(getType());
            parcel.writeLong(getCreated().getTime());
            parcel.writeLong(getModified().getTime());
            parcel.writeString(serialize());

        } catch (IOException e) {
            Log.e("", e);
        }
    }

    public static String toIndexPathFormat(String name, String value){
        return name + "_" + value;
    }


}
