package com.github.browep.fpt;

import android.app.Application;
import com.github.browep.fpt.view.ModelService;
import com.github.browep.fpt.view.ViewService;

public class FptApp extends Application{
  private ModelService modelService;

  @Override
  public void onCreate() {
    super.onCreate();
    modelService = new ModelService(this);
    ViewService viewService = new ViewService(modelService);

  }

  public ModelService getModelService() {
    return modelService;
  }
}
