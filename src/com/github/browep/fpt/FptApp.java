package com.github.browep.fpt;

import android.app.Application;
import com.github.browep.fpt.dao.PreferencesService;
import com.github.browep.fpt.model.FptPicture;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;
import com.github.browep.nosql.Dao;
import com.github.browep.fpt.view.ModelService;
import com.github.browep.fpt.view.ViewService;
import com.github.browep.nosql.NoSqlSqliteOpener;
import com.github.browep.nosql.Storable;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FptApp extends Application{
  private ModelService modelService;
  private Dao dao;
  GoogleAnalyticsTracker tracker;
  private PreferencesService preferencesService;
  private static FptApp singleton;

  @Override
  public void onCreate() {
    super.onCreate();
    this.tracker = GoogleAnalyticsTracker.getInstance();
    this.tracker.start(C.GOOGLE_ANALYTICS_ID, 300, this);

    modelService = new ModelService(this);
    ViewService viewService = new ViewService(modelService);
    dao = new Dao(getApplicationContext());
    preferencesService = new PreferencesService(getApplicationContext());
    singleton = this;

  }

  public ModelService getModelService() {
    return modelService;
  }

  public int getIdResource(String name){
    return getResources().getIdentifier(name,"id",getPackageName());
  }

  public Dao getDao() {
    return dao;
  }

  public GoogleAnalyticsTracker getTracker() {
    return tracker;
  }

  public int uploadAnyNonUploadedPics(){
    List<FptPicture> fptPictures = Util.getAllNotYetUploadedPics(getDao());
    if(fptPictures != null && !fptPictures.isEmpty()){
      FptPicture[] picturesArr = new FptPicture[fptPictures.size()];
      for(int i=0;i<fptPictures.size();i++){
        picturesArr[i] = fptPictures.get(i);
      }
      new UploadImageTask().execute(new UploadImageTask.UploadImageTaskPackage(this,picturesArr));
    }
    return  fptPictures.size();
  }

  public List<FptPicture> makeFptPicturesOutOfExistingFiles(){
    List<FptPicture> createdFptPictures = new LinkedList<FptPicture>();
    // get all the thumbs in the dir
    File pictureDirectory = new File(Util.getThumbsDirectory());
    if(!pictureDirectory.exists())
      pictureDirectory.mkdirs();
    File[] pictures = pictureDirectory.listFiles();
    for(File pictureFile : pictures){
      // see if there is an entry for this file
      Map where = new HashMap();
      where.put(C.FILE_NAME,pictureFile.getName());
      List<Storable> fptPictures = getDao().where(where);
      if(fptPictures.isEmpty()){
        // there were no entries for that file, create one
        createdFptPictures.add(FptPicture.fptPictureFromFile(pictureFile, getDao()));
      }
    }
    return createdFptPictures;
  }


  public boolean sendReport(String email){

    // get the model as an input stream
    InputStream inputStream =
        getApplicationContext().getResources().openRawResource(R.raw.model);

    File dbFile = getDatabasePath(NoSqlSqliteOpener.getDbName());
    if(dbFile.exists()){
      try {

        HttpClient client = new DefaultHttpClient();
        StringBuilder urlBuilder = new StringBuilder("http://").append(C.UPLOAD_HOSTNAME);
        urlBuilder.append("/reports/upload?email=").append(URLEncoder.encode(email));
        urlBuilder.append("&modelJson=").append(URLEncoder.encode(Util.slurp(inputStream)));

        String postURL = urlBuilder.toString();

        Log.i("posting to: " + postURL);
        HttpPost post = new HttpPost(postURL);

        // add db file
        FileBody bin = new FileBody(dbFile);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("dbFile", bin);
        post.setEntity(reqEntity);


        HttpResponse response = client.execute(post);
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
          String responseString = EntityUtils.toString(resEntity).trim();
          Log.i("RESPONSE:" + responseString);

        }
      } catch (Exception e) {
        Log.e("error uploading " + dbFile, e);
      }


    }else{
      Log.e("could not find " + dbFile.getAbsolutePath() + " on file system");
    }

    return true;


  }

  public PreferencesService getPreferencesService() {
    return preferencesService;
  }

  public static FptApp getInstance() {
    return singleton;
  }
}
