package com.github.browep.fpt.dao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 2/27/11
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaoAwareActivity extends Activity{
    protected Dao dao;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dao = new Dao(getApplicationContext());
    }
}
