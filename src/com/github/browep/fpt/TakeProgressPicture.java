package com.github.browep.fpt;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/12/11
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TakeProgressPicture extends DaoAwareActivity{
    private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 123;
    String fileName = "current.jpg";
    Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//define the file-name to save photo taken by Camera activity
//create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //use imageUri here to access the image
                File imageFile = convertImageUriToFile(imageUri,this);
                Log.i("image file: " +  imageFile.toString());

                // put them into the extra directory
                File externalStorageBase = Environment.getExternalStorageDirectory();

                File fptExternalStorageDir = new File(externalStorageBase + "/com.github.browep.fpt");
                if(!fptExternalStorageDir.exists()){
                    // create the directory
                    boolean dirCreateSuccess = fptExternalStorageDir.mkdir();
                    if(!dirCreateSuccess)
                        throw new RuntimeException(fptExternalStorageDir.getAbsolutePath() + "  was not created");
                }

                // copy file contents to app dir
                String fileName = ((Long)(new Date()).getTime()).toString() + ".jpg";
                File writeToFile = new File(fptExternalStorageDir + "/" + fileName);
                Util.copyfile(imageFile.getAbsolutePath(),writeToFile.getAbsolutePath());

                Log.i("copied to " + writeToFile.getAbsolutePath());
                Util.longToastMessage(this,"Picture saved successfully");

                finish();

            } else if (resultCode == RESULT_CANCELED) {
                Util.longToastMessage(this,"Picture was not taken");
            } else {
                Util.longToastMessage(this, "Picture was not taken");
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
}
