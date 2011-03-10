package com.github.browep.fpt;

import android.os.Parcel;
import com.github.browep.fpt.util.Log;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkoutDefinition extends FPTStorable {
    public static final Creator<WorkoutDefinition> CREATOR = new Creator<WorkoutDefinition>() {
        public WorkoutDefinition createFromParcel(Parcel source) {
            try {
                int type = source.readInt();
                long longCreated = source.readLong();
                long longModified = source.readLong();
                String data = source.readString();
                WorkoutDefinition definition = new WorkoutDefinition(data);
                definition.setCreated(new Date(longCreated));
                definition.setModified(new Date(longModified));
                return definition;
            } catch (IOException e) {
                Log.e("", e);
            } catch (JSONException e) {
                Log.e("", e);
            }
            return null;
        }
        public WorkoutDefinition[] newArray(int size) {
            return new WorkoutDefinition[size];
        }
    };

    public WorkoutDefinition(String data) throws IOException, JSONException {
        super(data);
    }

    public WorkoutDefinition(){
        super();
    }

    @Override
    public int getType() {
        return C.WORKOUT_DEFINITION_TYPE;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getIndexBys() {
        List<String> indexes = new LinkedList<String>();
        indexes.add(toIndexPathFormat("isdef","true"));
        return indexes;
    }

}
