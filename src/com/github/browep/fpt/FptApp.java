package com.github.browep.fpt;

import android.app.Application;
import com.github.browep.fpt.dao.Dao;
import com.github.browep.fpt.view.ModelService;
import com.github.browep.fpt.view.ViewService;

public class FptApp extends Application{
  private ModelService modelService;
  private Dao dao;

  @Override
  public void onCreate() {
    super.onCreate();
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
}
