package com.github.browep.fpt.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.C;
import com.github.browep.fpt.UploadImageTask;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;
import com.github.browep.fpt.model.FptPicture;

import java.io.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/12/11
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TakeProgressPicture extends FptActivity {
  private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 123;
  String fileName = "current.jpg";
  Uri imageUri;
  Handler mHandler;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    mHandler = new Handler();

    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(this)));
    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
            "Saving Picture...", true);

        //use imageUri here to access the image


        new Thread() {
          @Override
          public void run() {
            try {
              final File tempFile = getTempFile(self);

              Bitmap captureBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(tempFile));
              File outFile = File.createTempFile("bitmap", "tmp");
              FileOutputStream out = new FileOutputStream(outFile);
              captureBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);

              Bitmap thumbBitmap = Util.decodeFile(outFile);

              String fileName = ((Long) (new Date()).getTime()).toString() + ".jpg";

              String thumbsDirectory = Util.getThumbsDirectory();
              Util.createIfNotPresent(thumbsDirectory);
              File thumbFile = new File(thumbsDirectory + "/" + fileName);

              try {
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(thumbFile));
              } catch (FileNotFoundException e) {
                Log.e("trying to save thumb", e);
              }

              // try to upload
              final FptPicture fptPicture = FptPicture.fptPictureFromFile(thumbFile, getDao());
              getDao().save(fptPicture);

              getFptApplication().getTracker().trackEvent(
                  "Picture",  // Category
                  "Taken",  // Action
                  null, // Label
                  0);
              FlurryAgent.onEvent("PICTURE_TAKEN");

              mHandler.post(new Runnable() {
                public void run() {
                  new UploadImageTask().execute(new UploadImageTask.UploadImageTaskPackage(getFptApplication(), new FptPicture[]{fptPicture}));
                  dialog.dismiss();
                  Util.longToastMessage(self, "Picture saved successfully");
                  finish();
                }
              });
            } catch (IOException e) {
              Log.e("error getting image", e);
              throw new RuntimeException(e);
            }

          }
        }.start();

      } else {
        Util.longToastMessage(this, "Picture was not taken");
        finish();
      }
    }
  }

  public static File convertImageUriToFile(Uri imageUri, Activity activity) {
    Cursor cursor = null;
    try {
      String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
      cursor = activity.managedQuery(imageUri,
          proj, // Which columns to return
          null,       // WHERE clause; which rows to return (all rows)
          null,       // WHERE clause selection arguments (none)
          null); // Order-by clause (ascending by name)
      int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
      if (cursor.moveToFirst()) {
        String orientation = cursor.getString(orientation_ColumnIndex);
        return new File(cursor.getString(file_ColumnIndex));
      }
      return null;
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }


private File getTempFile(Context context){

  final File path = new File( Environment.getExternalStorageDirectory(), context.getPackageName() );
  if(!path.exists()){
    path.mkdir();
  }
  return new File(path, "image.tmp");
}

  @Override
  public String getPageName() {
    return "TakeProgressPicture";
  }
}
