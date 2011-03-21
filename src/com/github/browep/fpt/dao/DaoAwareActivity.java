package com.github.browep.fpt.dao;

import android.os.Bundle;
import android.view.Window;
import com.github.browep.fpt.FptApp;
import com.github.browep.fpt.view.ModelService;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 2/27/11
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaoAwareActivity extends AnalyzableActivity {
  protected Dao dao;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    dao = new Dao(getApplicationContext());
  }

  public ModelService getViewService(){
    return ((FptApp)getApplication()).getModelService();
  }


}
