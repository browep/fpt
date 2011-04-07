package com.github.browep.fpt.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.github.browep.fpt.C;
import com.github.browep.fpt.Workout;
import com.github.browep.fpt.WorkoutDefinition;
import com.github.browep.fpt.util.Log;
import com.sun.corba.se.spi.orb.StringPair;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 2/27/11
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Dao {
  private static final String INSTANCES_TABLE_NAME = "instances";
  private static final String INDEXES_TABLE_NAME = "indexes";
  private SQLiteDatabase db = null;
  private static Map<Integer, Class> CLASS_TO_TYPE = new HashMap<Integer, Class>();
  private static final SimpleDateFormat SQL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  Context self;

  static {
    CLASS_TO_TYPE.put(C.FOR_DISTANCE_WORKOUT_TYPE, Workout.class);
    CLASS_TO_TYPE.put(C.FOR_TIME_WORKOUT_TYPE, Workout.class);
    CLASS_TO_TYPE.put(C.FOR_MAX_WEIGHT_WORKOUT_TYPE, Workout.class);
    CLASS_TO_TYPE.put(C.FOR_REPS_WORKOUT_TYPE, Workout.class);
    CLASS_TO_TYPE.put(C.WORKOUT_DEFINITION_TYPE, WorkoutDefinition.class);
  }

  public Dao(Context context) {
//        FptSqliteOpener fptSqliteOpener = new FptSqliteOpener(context);
//        db = fptSqliteOpener.getWritableDatabase();
    self = context;
  }


  public void save(Storable storable) {
    // if there is an id, do an update
    db = getOrOpen();
    try {


      ContentValues values = new ContentValues();

      values.put("created", SQL_FORMAT.format(storable.getCreated()));
      values.put("modified", SQL_FORMAT.format(storable.getModified()));
      values.put("data", storable.serialize());

      db.update(INSTANCES_TABLE_NAME, values, "ROWID = ?", new String[]{String.valueOf(storable.getId())});

      List<String> indexPaths = storable.getIndexBys();

      // remove all indexes
      db.delete(INDEXES_TABLE_NAME, "instance_id = ? ", new String[]{String.valueOf(storable.getId())});

      // regen them
      for (String indexPath : indexPaths) {
        values = new ContentValues();
        values.put("instance_id", String.valueOf(storable.getId()));
        values.put("path", indexPath);
        db.insert(INDEXES_TABLE_NAME, null, values);
      }
    } catch (IOException e) {
      Log.e("Error trying to save" + storable.toString(), e);
    } finally {
      db.close();
    }

  }

  private int getLastInsertedRowId() {

    try {
      db = getOrOpen();

      Cursor cursor = db.query(INSTANCES_TABLE_NAME, new String[]{"last_insert_rowid()"}, null, null, null, null, null);
      cursor.moveToFirst();
      return cursor.getInt(0);
    } finally {
      db.close();
    }

  }

  public Storable initialize(Class clazz) throws IllegalAccessException, InstantiationException {
    Storable storable = (Storable) clazz.newInstance();
    return initialize(storable);

  }


  public Storable initialize(Storable storable) {


    try {
      db = getOrOpen();

// create an entry in the db
      ContentValues values = new ContentValues();
      values.put("modified", SQL_FORMAT.format(storable.getModified()));
      values.put("created", SQL_FORMAT.format(storable.getCreated()));
      values.put("type", storable.getType());

      try {
        values.put("data", storable.serialize());
      } catch (IOException e) {
        Log.e("", e);
      }
      db.insert(INSTANCES_TABLE_NAME, null, values);

      storable.setId(getLastInsertedRowId());
    } finally {
      db.close();
    }

    return storable;
  }

  public SQLiteDatabase getOrOpen() {
    return db != null && db.isOpen() ? db : (new FptSqliteOpener(self)).getWritableDatabase();
  }


  public void dumpDbToLog() {

    Log.i("dumping to log");
    try {
      db = getOrOpen();

      Cursor cursor = db.query(INSTANCES_TABLE_NAME, new String[]{"ROWID", "type", "created", "modified", "data"}, null, null, null, null, null);
      cursor.move(1);
      while (!cursor.isAfterLast()) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSTANCE: ").append(cursor.getInt(0)).append(",").append(cursor.getString(1)).append(",").append(cursor.getString(2)).append(",").append(cursor.getString(3)).append(",").append(cursor.getString(4));
        Log.i(sb.toString());
        cursor.move(1);
      }

      cursor = db.query(INDEXES_TABLE_NAME, new String[]{"ROWID", "instance_id", "path"}, null, null, null, null, null);
      cursor.move(1);
      while (!cursor.isAfterLast()) {
        StringBuilder sb = new StringBuilder();
        sb.append("INDEX   : ").append(cursor.getInt(0)).append(",").append(cursor.getInt(1)).append(",").append(cursor.getString(2));
        Log.i(sb.toString());
        cursor.move(1);
      }
    } finally {
      db.close();

    }

  }


  public Storable get(int id) {
    Storable storable = null;
    try {
      db = getOrOpen();
      Cursor cursor = db.query(INSTANCES_TABLE_NAME, new String[]{"ROWID", "type", "created", "modified", "data"}, "ROWID = ?", new String[]{String.valueOf(id)}, null, null, null);
      cursor.moveToFirst();
      if (cursor.isAfterLast()) {
        throw new NotFoundInDb();
      }
      try {
        storable = inflate(cursor);
      } catch (IOException e) {
        Log.e(cursor.getString(3), e);

      }
    } catch (Exception e) {
      Log.e("problem retrieving " + id, e);
    } finally {
      db.close();

    }
    return storable;

  }

  private Storable inflate(Cursor cursor) throws InstantiationException, IllegalAccessException, ParseException, IOException {
    Storable storable;
    int type = cursor.getInt(1);
    Class clazz = CLASS_TO_TYPE.get(type);
    storable = (Storable) clazz.newInstance();
    storable.setId(cursor.getInt(0));
    storable.setCreated(SQL_FORMAT.parse(cursor.getString(2)));
    storable.setModified(SQL_FORMAT.parse(cursor.getString(3)));
    storable.setData(cursor.getString(4));
    return storable;
  }


  public List<Storable> where(Map<String, String> wheres) {

    try {
      db = getOrOpen();

      List<Integer> found = null;

      for (Map.Entry<String, String> where : wheres.entrySet()) {
        String combined = where.getKey() + "_" + where.getValue();
        Log.i("where: " + combined);
        Cursor cursor = db.query(INDEXES_TABLE_NAME, new String[]{"instance_id"}, "path = ?", new String[]{combined}, null, null, null);
        List<Integer> found_this_where = new LinkedList<Integer>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
          found_this_where.add(cursor.getInt(0));
          cursor.move(1);
        }

        if (found_this_where.isEmpty()) // didnt find anything just return nothing
          return new LinkedList<Storable>();
        else if (found == null) {
          found = found_this_where;
        } else {
          found.retainAll(found_this_where);
          // if we retained none and this is empty, return as well never match anything
          if (found.isEmpty())
            return new LinkedList<Storable>();
        }
      }
      if (found == null || found.isEmpty())
        return new LinkedList<Storable>();
      else {
        // we found some that match resolve all
        return resolveIds(found);
      }
    } finally {
      db.close();
    }
  }

  private List<Storable> resolveIds(List<Integer> found) {
    List<Storable> storables = new LinkedList<Storable>();
    for (Integer id : found) {
      try {
        storables.add(get(id));
      } catch (Exception e) {
        Log.e("Error retrieving id=" + id, e);
      }

    }
    return storables;
  }

  public List<Storable> getByType(int type) {
    return getByType(type, 100);
  }

  public List<Storable> getByType(int type, int limit) {
    List<Storable> storables = new LinkedList<Storable>();
    try {
      db = getOrOpen();
      Cursor cursor = db.query(INSTANCES_TABLE_NAME, new String[]{"ROWID", "type", "created", "modified", "data"}, "type = ?", new String[]{String.valueOf(type)}, null, null, null, String.valueOf(limit));
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        try {
          storables.add(inflate(cursor));
        } catch (Exception e) {
          Log.e("issue inflating " + cursor, e);
        }
        cursor.move(1);
      }
    } finally {
      db.close();
    }
    return storables;
  }


  public void delete(int rowId){
     try {
      db = getOrOpen();
      // delete from instances.
      db.delete(INSTANCES_TABLE_NAME, "ROWID =?", new String[]{String.valueOf(rowId)} );
      // delete all indexes
       db.delete(INDEXES_TABLE_NAME, "instance_id = ? ", new String[]{String.valueOf(rowId)});

    } finally {
      db.close();
    }
  }


}
