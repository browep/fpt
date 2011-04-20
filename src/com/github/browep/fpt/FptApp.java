package com.github.browep.fpt;

import android.app.Application;
import com.github.browep.fpt.dao.Dao;
import com.github.browep.fpt.view.ModelService;
import com.github.browep.fpt.view.ViewService;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class FptApp extends Application{
  private ModelService modelService;
  private Dao dao;
  GoogleAnalyticsTracker tracker;

  @Override
  public void onCreate() {
    super.onCreate();
    this.tracker = GoogleAnalyticsTracker.getInstance();
    this.tracker.start(C.GOOGLE_ANALYTICS_ID, 300, this);

    modelService = new ModelService(this);
    ViewService viewService = new ViewService(modelService);
    dao = new Dao(getApplicationContext());

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
}
