package com.github.browep.fpt;

import android.os.Parcel;
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
public class Workout extends FPTStorable {

    public Workout(){
        super();
    }

    public Workout(int type){
        super();
        this.type = type;
    }

    public Workout(int type, String data) throws IOException, JSONException {
        super(data);
        this.type=type;
    }

    private int type;

    @Override
    public int getType() {
        return type;
    }

    @Override
    public List<String> getIndexBys() {
        List<String> indexes = super.getIndexBys();    //To change body of overridden methods use File | Settings | File Templates.
        if(get(C.WORKOUT_DEFINITION_ID) != null)
            indexes.add(toIndexPathFormat(C.WORKOUT_DEFINITION_ID, get(C.WORKOUT_DEFINITION_ID).toString()));
        return indexes;
    }


    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
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
