package com.github.browep.fpt.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.browep.fpt.C;
import com.github.browep.fpt.model.FPTStorable;
import com.github.browep.fpt.util.Log;
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

  public FptPicture(File file) {
    super();
    put(C.FILE_NAME, file.getName());
    put("uploaded",false);
  }

  @Override
  public int getType() {
    return C.PICTURE_TYPE;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public List<String> getIndexBys() {
    List<String> indexes = new LinkedList<String>();
    indexes.add(toIndexPathFormat("uploaded",Boolean.toString((Boolean) get("uploaded"))));
    indexes.add(toIndexPathFormat("ispic", "true"));
    return indexes;
  }

}
