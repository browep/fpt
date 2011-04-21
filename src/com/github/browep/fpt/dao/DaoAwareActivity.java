package com.github.browep.fpt.dao;

import android.os.Bundle;
import android.view.Window;
import com.github.browep.fpt.FptApp;
import com.github.browep.fpt.view.ModelService;
import com.github.browep.nosql.Dao;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 2/27/11
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DaoAwareActivity extends AnalyzableActivity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  public ModelService getViewService(){
    return ((FptApp)getApplication()).getModelService();
  }

  public Integer getIdResource(String name){
    return ((FptApp)getApplication()).getIdResource(name );
  }

  public Dao getDao() {
    return ((FptApp)getApplication()).getDao();
  }


}
