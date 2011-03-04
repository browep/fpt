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
 * Date: 3/3/11
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Workout extends Storable {

    private int type;

    public Workout(int type){
        this.type = type;
    }

    public Workout(int type, String data) throws IOException, JSONException {
        super(data);
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
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
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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

    public static final Parcelable.Creator<Workout> CREATOR = new Parcelable.Creator<Workout>() {
        public Workout createFromParcel(Parcel source) {
            try {
                int type = source.readInt();
                long longCreated = source.readLong();
                long longModified = source.readLong();
                String data = source.readString();
                Workout workout = new Workout(type, data);
                workout.setCreated(new Date(longCreated));
                workout.setModified(new Date(longModified));
                return workout;
            } catch (IOException e) {
                Log.e("", e);
            } catch (JSONException e) {
                Log.e("", e);
            }
            return null;
        }
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };
}
