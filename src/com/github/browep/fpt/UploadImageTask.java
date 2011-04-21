package com.github.browep.fpt;

import android.os.AsyncTask;
import com.github.browep.fpt.model.FptPicture;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;


/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 4/11/11
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class UploadImageTask extends AsyncTask<UploadImageTask.UploadImageTaskPackage, Void, String> {
  protected String doInBackground(UploadImageTaskPackage... uploadImageTaskPackages) {

    UploadImageTaskPackage uploadImageTaskPackage = uploadImageTaskPackages[0];

    FptApp fptApp = uploadImageTaskPackage.getFptApp();

    for (FptPicture fptPicture : uploadImageTaskPackage.getFptPictures()) {

      File imageFile = new File(Util.getThumbsDirectory() + "/" + fptPicture.get(C.FILE_NAME));
      Log.i("starting upload for " + imageFile);

      try {

        HttpClient client = new DefaultHttpClient();
        String postURL = "http://" + C.UPLOAD_HOSTNAME + "/reports/upload?editor_id=enRlK&key=OK&filename=" + imageFile.getName();
        Log.i("posting to: " + postURL);
        HttpPost post = new HttpPost(postURL);
        FileBody bin = new FileBody(imageFile);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("myFile", bin);
        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
          String responseString = EntityUtils.toString(resEntity).trim();
          Log.i("RESPONSE:" + responseString);
          fptPicture.put(C.URL,responseString);
          fptPicture.put(C.UPLOADED,true);
          fptApp.getDao().save(fptPicture);
        }
      } catch (Exception e) {
        Log.e("error uploading " + imageFile, e);
      }

    }
    return null;

  }

  protected void onPostExecute(File result) {
  }

  public static class UploadImageTaskPackage {

    FptApp fptApp;
    FptPicture[] files;
    public UploadImageTaskPackage(FptApp fptApp, FptPicture[] files) {
      this.fptApp = fptApp;
      this.files = files;
    }

    public FptApp getFptApp() {
      return fptApp;
    }

    public FptPicture[] getFptPictures() {
      return files;
    }
  }
}
