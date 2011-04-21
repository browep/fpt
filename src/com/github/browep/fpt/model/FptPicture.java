package com.github.browep.fpt.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.browep.fpt.C;
import com.github.browep.fpt.model.FPTStorable;
import com.github.browep.fpt.util.Log;
import com.github.browep.nosql.Dao;
import org.json.JSONException;

import java.io.File;
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
public class FptPicture extends FPTStorable {
  public FptPicture(String data) throws IOException, JSONException {
    super(data);
  }

  public FptPicture() {
    super();
  }

  public static FptPicture fptPictureFromFile(File file,Dao dao) {
    FptPicture fptPicture = null;
    try {
      fptPicture = (FptPicture) dao.initialize(FptPicture.class);
    } catch (IllegalAccessException e) {
      Log.e("", e);
    } catch (InstantiationException e) {
      Log.e("", e);
    }
    fptPicture.put(C.FILE_NAME, file.getName());
    fptPicture.put(C.UPLOADED, false);
    dao.save(fptPicture);
    return fptPicture;
  }

  @Override
  public int getType() {
    return C.PICTURE_TYPE;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public List<String> getIndexBys() {
    List<String> indexes = new LinkedList<String>();
    indexes.add(toIndexPathFormat(C.UPLOADED,Boolean.toString((Boolean) get(C.UPLOADED))));
    indexes.add(toIndexPathFormat("ispic", "true"));
    indexes.add(toIndexPathFormat(C.FILE_NAME, (String) get(C.FILE_NAME)));
    return indexes;
  }

}
